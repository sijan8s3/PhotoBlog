package com.devs.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.animation.ObjectAnimator;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.devs.blog.adapter.CommentAdapter;
import com.devs.blog.model.Comment;
import com.devs.blog.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CommentActivity extends AppCompatActivity {
    private Toolbar toolbar;
    private ImageView profileImage;
    private EditText comment;
    private TextView postCmnt;

    private String postID, creatorID;
    FirebaseUser firebaseUser;

    private RecyclerView commentRecycler;
    private CommentAdapter commentAdapter;
    private List<Comment> commentList;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comment);

        toolbar= findViewById(R.id.toolbar);
        profileImage= findViewById(R.id.profile_image);
        comment= findViewById(R.id.comment);
        postCmnt= findViewById(R.id.post_cmnt);

        Intent intent= getIntent();
        postID= intent.getStringExtra("postID");
        creatorID= intent.getStringExtra("creatorID");


        setSupportActionBar(toolbar);
        getSupportActionBar().setTitle("Comments");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();

            }
        });

        commentRecycler= findViewById(R.id.recycler_cmnt);
        commentRecycler.setHasFixedSize(true);
        commentRecycler.setLayoutManager(new LinearLayoutManager(this));

        commentList= new ArrayList<>();
        commentAdapter= new CommentAdapter(this,commentList, postID);
        commentRecycler.setAdapter(commentAdapter);


        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        getUserImage();
        
        postCmnt.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (TextUtils.isEmpty(postCmnt.toString())){
                    Toast.makeText(CommentActivity.this, "No comment added", Toast.LENGTH_SHORT).show();
                }else {
                    insertComment();
                    //  TODO addNotification(firebaseUser,postID);
                }
            }
        });

        getComment();
    }

//    private void addNotification(String uid, String postID) {
//        HashMap<String, Object> hashMap = new HashMap<>();
//
//        hashMap.put("userID", uid);
//        hashMap.put("text", "started following you.");
//        hashMap.put("postID", postID);
//        hashMap.put("isPost", false);
//
//        FirebaseDatabase.getInstance().getReference().child("Notification").child(firebaseUser.getUid()).push().setValue(hashMap);
//    }

    private void getComment() {
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                commentList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Comment comment= dataSnapshot.getValue(Comment.class);
                    commentList.add(comment);
                }
                commentAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void insertComment() {
        HashMap<String, Object> map= new HashMap<>();

        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Comments").child(postID);
        String commentID= reference.push().getKey();

        map.put("comment", comment.getText().toString());
        map.put("authorID", firebaseUser.getUid());
        map.put("commentID", commentID);

        reference.child(commentID).setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if (task.isSuccessful()){
                    Toast.makeText(CommentActivity.this, "Comment Added", Toast.LENGTH_SHORT).show();
                    comment.setText("");
                }else {
                    Toast.makeText(CommentActivity.this, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    private void getUserImage() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(firebaseUser.getUid()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                if (user.getImageurl().toLowerCase().equals("default")){
                    profileImage.setImageResource(R.drawable.logo);
                }else {
                    Glide.with(getApplicationContext()).load(user.getImageurl()).into(profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}