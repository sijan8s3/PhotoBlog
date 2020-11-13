package com.devs.blog;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.hendraanggrian.appcompat.socialview.Hashtag;
import com.hendraanggrian.appcompat.widget.HashtagArrayAdapter;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;
import java.util.List;

public class PostActivity extends AppCompatActivity {
    private Uri imageUri;
    private String imageString;
    private ImageView close, image_added;
    private TextView post;
    SocialAutoCompleteTextView description;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_post);

        close= findViewById(R.id.close);
        image_added= findViewById(R.id.image_added);
        post= findViewById(R.id.post);
        description= findViewById(R.id.description);

        close.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(PostActivity.this, BrowseActivity.class));
                finish();
            }
        });

        post.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                uploadImage();
            }
        });

        CropImage.activity().start(PostActivity.this);
        //opens the explorer and allow user to select image, => onActivityResult

    }

    //function to upload image on clicking post
    private void uploadImage() {
        final ProgressDialog progressDialog= new ProgressDialog(this);
        progressDialog.setMessage("Uploading...");
        progressDialog.show();

        if (imageUri != null){
            //add image to firebase storage
            final StorageReference reference= FirebaseStorage.getInstance().getReference("Posts").child(System.currentTimeMillis()+"."+getFileExtension(imageUri));
            StorageTask uploadTask = reference.putFile(imageUri);
            uploadTask.continueWithTask(new Continuation() {
                @Override
                public Object then(@NonNull Task task) throws Exception {
                    if (!task.isSuccessful()){
                        throw task.getException();
                    }
                    return reference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@NonNull Task<Uri> task) {
                    Uri downloadUri= task.getResult();
                    imageString= downloadUri.toString();

                    DatabaseReference ref = FirebaseDatabase.getInstance().getReference("Posts");
                    //creating post id and getting post description
                    String postID = ref.push().getKey();
                    String postDesc = description.getText().toString();

                    HashMap<String, Object> hashMap = new HashMap<>();
                    hashMap.put("postID", postID);
                    hashMap.put("description", postDesc);
                    hashMap.put("imageUrl", imageString);
                    hashMap.put("creator", FirebaseAuth.getInstance().getCurrentUser().getUid());

                    //setting hashMap value to firebase storage
                    ref.child(postID).setValue(hashMap);

                    //for tags
                    DatabaseReference hashTagRef = FirebaseDatabase.getInstance().getReference().child("HashTags");
                    List<String> hashTags = description.getHashtags();
                    if (!hashTags.isEmpty()){
                        for (String tag : hashTags){
                            hashMap.clear();
                            hashMap.put("tag", tag.toLowerCase());
                            hashMap.put("postID", postID);
                            hashTagRef.child(tag.toLowerCase()).child(postID).setValue(hashMap);
                        }
                    }
                    progressDialog.dismiss();
                    Toast.makeText(PostActivity.this, "Successfully uploaded!", Toast.LENGTH_SHORT).show();
                    startActivity(new Intent(PostActivity.this, BrowseActivity.class));
                    finish();
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(PostActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
    }

    private String getFileExtension(Uri uri) {
        return MimeTypeMap.getSingleton().getExtensionFromMimeType(this.getApplicationContext().getContentResolver().getType(uri));
    }

    //when user select an image || to fetch the image to postActivity
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode== CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode== RESULT_OK){
            CropImage.ActivityResult result= CropImage.getActivityResult(data);
            imageUri= result.getUri();

            image_added.setImageURI(imageUri);
        }else {
            Toast.makeText(this, "Unable to fetch image.", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(PostActivity.this, BrowseActivity.class));

        }
    }

    //if written something with hash '#' SocialAutoComplete will get the available options from firebase HashTags in populated view
    @Override
    protected void onStart() {
        super.onStart();

        final ArrayAdapter<Hashtag> hashTagAdapter= new HashtagArrayAdapter<>(getApplicationContext());
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    hashTagAdapter.add(new Hashtag(dataSnapshot.getKey(), (int) dataSnapshot.getChildrenCount()));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        description.setHashtagAdapter(hashTagAdapter);
    }
}