package com.devs.blog.fragments;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devs.blog.R;
import com.devs.blog.adapter.PostAdapter;
import com.devs.blog.model.Post;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class HomeFragment extends Fragment{
    RecyclerView recyclerViewPosts;
    private PostAdapter postAdapter;
    private List<Post> postList;  //list of posts
    private List<String> followingList;   //to show only followed user's posts


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {
        View view= inflater.inflate(R.layout.fragment_home, container, false);

        recyclerViewPosts= view.findViewById(R.id.recycler_post);
        recyclerViewPosts.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager= new LinearLayoutManager(getContext());
        linearLayoutManager.setStackFromEnd(true); //to make latest posts available on the top
        linearLayoutManager.setReverseLayout(true);
        
        recyclerViewPosts.setLayoutManager(linearLayoutManager);
        postList= new ArrayList<>();
        postAdapter= new PostAdapter(getContext(),postList);
        recyclerViewPosts.setAdapter(postAdapter);
        
        followingList= new ArrayList<>();
        //checkFollowingUsers();
        readPosts();
        
        return view;
    }

    //add value to the following list for all the users who are followed by current users
    private void checkFollowingUsers() {
        FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(FirebaseAuth.getInstance().getCurrentUser().
                        getUid()).child("Following").addValueEventListener(new ValueEventListener() {

                            //add followers to the following list
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followingList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    followingList.add(dataSnapshot.getKey());
                }

                //reads the whole posts filters the following list's user's posts only
                readPosts();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeFragment.this.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void readPosts() {
        FirebaseDatabase.getInstance().getReference().child("Posts").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                postList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    //get the whole posts to post
                    Post post= dataSnapshot.getValue(Post.class);
                    postList.add(post);

                    //add following list to id string
//                    for (String id : followingList){
//                        //if post's creator is equals to the id from following list, add the post to post list
//                        if (post.getCreator().equals(id)) {
//                            postList.add(post);
//                        }
//                    }
                }
                postAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
                Toast.makeText(HomeFragment.this.getContext(), error.getMessage(), Toast.LENGTH_SHORT).show();

            }
        });
    }
}

