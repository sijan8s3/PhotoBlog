package com.devs.blog.fragments;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.devs.blog.R;
import com.devs.blog.adapter.TagAdapter;
import com.devs.blog.adapter.UserAdapter;
import com.devs.blog.model.User;
import com.google.android.gms.dynamic.IFragmentWrapper;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialAutoCompleteTextView;

import java.util.ArrayList;
import java.util.List;

public class SearchFragment extends Fragment{
    private RecyclerView recyclerView;
    private SocialAutoCompleteTextView searchBar;
    private List<User> mUsers;
    private UserAdapter userAdapter;

    private RecyclerView recyclerViewTags;
    private List<String> mHashTags;
    private List<String> mHashTagCount;
    private TagAdapter tagAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle saveInstanceState) {

        View view = inflater.inflate(R.layout.fragment_search, container, false);

        recyclerView= view.findViewById(R.id.recycler_users);
        recyclerView.setHasFixedSize(true);
        recyclerView.setLayoutManager(new LinearLayoutManager(getContext()));

        mUsers= new ArrayList<>();
        userAdapter= new UserAdapter(getContext(), mUsers, true);
        recyclerView.setAdapter(userAdapter);

        recyclerViewTags= view.findViewById(R.id.recycler_tags);
        recyclerViewTags.setHasFixedSize(true);
        recyclerViewTags.setLayoutManager(new LinearLayoutManager(getContext()));

        mHashTags= new ArrayList<>();
        mHashTagCount= new ArrayList<>();
        tagAdapter= new TagAdapter(getContext(),mHashTags, mHashTagCount);
        recyclerViewTags.setAdapter(tagAdapter);



        searchBar= view.findViewById(R.id.search_bar);

        //read all available users
        readUsers();
        //read all available tags
        readTags();

        //
        searchBar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                searchUser(charSequence.toString());
            }

            @Override
            public void afterTextChanged(Editable editable) {
                searchTags(editable.toString());
            }
        });




        return view;
    }

    private void readTags() {
        FirebaseDatabase.getInstance().getReference().child("HashTags").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                mHashTags.clear();
                mHashTagCount.clear();

                for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                    mHashTags.add(dataSnapshot.getKey());
                    mHashTagCount.add(dataSnapshot.getChildrenCount() + "");
                    }
                    tagAdapter.notifyDataSetChanged();
                }


            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void readUsers() {
        FirebaseDatabase.getInstance().getReference().child("Users").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (TextUtils.isEmpty(searchBar.getText())){
                    mUsers.clear();
                    for (DataSnapshot dataSnapshot : snapshot.getChildren()){
                        User user= dataSnapshot.getValue(User.class);
                        mUsers.add(user);
                    }
                    userAdapter.notifyDataSetChanged();
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

    }
    //search users
    private void searchUser(final String s){
        Query query= FirebaseDatabase.getInstance().getReference().child("Users").orderByChild("username").startAt(s).endAt(s+"\uf8ff");

        query.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                mUsers.clear();
                for (DataSnapshot snapshot: dataSnapshot.getChildren()){
                    User user= snapshot.getValue(User.class);
                    mUsers.add(user);
                }
                userAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    //search tags
    private void searchTags(final String tag){
        List<String> mSearchTags = new ArrayList<>();
        List<String> mSearchTagsCount = new ArrayList<>();

        for (String s : mHashTags){
            if (s.toLowerCase().contains(tag.toLowerCase())){
                mSearchTags.add(s);
                mSearchTagsCount.add(mHashTagCount.get(mHashTags.indexOf(s)));
            }
        }

        tagAdapter.filter(mSearchTags, mSearchTagsCount);
    }
}

