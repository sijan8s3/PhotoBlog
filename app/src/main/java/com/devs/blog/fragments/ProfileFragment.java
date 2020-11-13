package com.devs.blog.fragments;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devs.blog.R;
import com.devs.blog.adapter.PhotoAdapter;
import com.devs.blog.model.Post;
import com.devs.blog.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ProfileFragment extends Fragment{

    private RecyclerView recyclerPhoto;
    private List<Post> photoList;
    private PhotoAdapter photoAdapter;

    private ImageView profileImage, options;
    private TextView posts, followers, followings, fullName, bio, username;
    private ImageButton myPicture, saved;
    FirebaseUser firebaseUser;
    String profileID;
    private Button edit_profile;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view= inflater.inflate(R.layout.fragment_profile, container,false);

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        myPicture= view.findViewById(R.id.my_picture);
        saved= view.findViewById(R.id.saved);
        profileImage= view.findViewById(R.id.profile_image);
        options= view.findViewById(R.id.options);
        posts= view.findViewById(R.id.no_of_posts);
        followers= view.findViewById(R.id.no_of_followers);
        followings= view.findViewById(R.id.no_of_followings);
        fullName= view.findViewById(R.id.fullName);
        bio= view.findViewById(R.id.bio);
        username= view.findViewById(R.id.username);
        edit_profile= view.findViewById(R.id.edit_profile);

        recyclerPhoto= view.findViewById(R.id.recycler_pictures);
        recyclerPhoto.setHasFixedSize(true);
        recyclerPhoto.setLayoutManager(new GridLayoutManager(getContext(), 3));
        photoList= new ArrayList<>();
        photoAdapter= new PhotoAdapter(getContext(), photoList);
        recyclerPhoto.setAdapter(photoAdapter);

        String data= getContext().getSharedPreferences("PROFILE", Context.MODE_PRIVATE).getString("profileID", "none");
        if (data.equals("none")){
            profileID= firebaseUser.getUid();
        }else {
            profileID= data;
        }


        userInfo();
        getFollowingsFollowersCount();
        getPostCount();
        getPhotos();

        if (profileID.equals(firebaseUser.getUid())){
            edit_profile.setText("Edit Profile");
        }else {
            checkFollowingStatus();
        }

        edit_profile.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String btnText= edit_profile.getText().toString();

                if (btnText.equals("Edit Profile")){
                    //GOTO EDIT Activity
                } else {
                    if (btnText.equals("Follow")){
                        //follow the user
                        //add followed user's UID to current user's following list
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("Following").child(profileID).setValue(true);

                        //add current user's UID to followed user's followers list
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileID).child("Followers").child(firebaseUser.getUid()).setValue(true);

                    }else {
                        //unfollow the user
                        //remove followed user's UID from current user's following list
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("Following").child(profileID).removeValue();

                        //remove current user's UID from followed user's followers list
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(profileID).child("Followers").child(firebaseUser.getUid()).removeValue();

                    }

                }
            }
        });



        return view;
    }

    private void getPhotos() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                photoList.clear();
                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post= dataSnapshot.getValue(Post.class);

                    if (post.getCreator().equals(profileID)){
                        photoList.add(post);
                    }
                }
                Collections.reverse(photoList);
                photoAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void checkFollowingStatus() {
        FirebaseDatabase.getInstance().getReference().child("Follow").child(firebaseUser.getUid()).child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(profileID).exists()){
                    edit_profile.setText("Following");
                }else{
                    edit_profile.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostCount() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Posts");

        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int counter = 0;

                for (DataSnapshot dataSnapshot: snapshot.getChildren()){
                    Post post= dataSnapshot.getValue(Post.class);
                    if (post.getCreator().equals(profileID))
                        counter++;

                }
                posts.setText(String.valueOf(counter));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getFollowingsFollowersCount() {
        DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("Follow").child(profileID);

        reference.child("Followers").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(" "+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
        reference.child("Following").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(" "+ snapshot.getChildrenCount());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void userInfo() {
        FirebaseDatabase.getInstance().getReference().child("Users").child(profileID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                Glide.with(getContext()).load(user.getImageurl()).into(profileImage);
                username.setText(user.getUsername());
                fullName.setText(user.getName());
                bio.setText(user.getBio());

            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}

