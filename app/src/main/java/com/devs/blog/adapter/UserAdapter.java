package com.devs.blog.adapter;

import android.content.Context;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devs.blog.R;
import com.devs.blog.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;


public class UserAdapter extends RecyclerView.Adapter<UserAdapter.ViewHolder>{
    private Context context;
    private List<User> users;
    private boolean isFragment;
    private FirebaseUser firebaseUser;

    public UserAdapter(Context context, List<User> users, boolean isFragment) {
        this.context = context;
        this.users = users;
        this.isFragment = isFragment;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.user_item, parent, false);
        return new UserAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {

        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();
        final User user= users.get(position);
        holder.follow_button.setVisibility(View.VISIBLE);
        holder.fullName.setText(user.getName());
        holder.username.setText(user.getUsername());

        Glide.with(context).load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.profileImage);

        isFollowed(user.getUid(), holder.follow_button);

        if (user.getUid().equals(firebaseUser.getUid())){
            holder.follow_button.setVisibility(View.GONE);
        }

        holder.follow_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.follow_button.getText().toString().equals("Follow")){
                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(firebaseUser.getUid()).child("Following").child(user.getUid()).setValue(true);

                    FirebaseDatabase.getInstance().getReference().child("Follow")
                            .child(user.getUid()).child("Followers").child(firebaseUser.getUid()).setValue(true);
                }else {
                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(firebaseUser.getUid()).child("Following").child(user.getUid()).removeValue();

                        FirebaseDatabase.getInstance().getReference().child("Follow")
                                .child(user.getUid()).child("Followers").child(firebaseUser.getUid()).removeValue();
                }
            }
        });

    }

    private void isFollowed(final String uid, final Button follow_button) {
        DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("Follow")
                .child(firebaseUser.getUid()).child("Following");
        reference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.child(uid).exists()){
                    follow_button.setText("Following");
                }else {
                    follow_button.setText("Follow");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return users.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profileImage;
        TextView username, fullName;
        Button follow_button;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage = itemView.findViewById(R.id.profile_image);
            username = itemView.findViewById(R.id.username);
            fullName = itemView.findViewById(R.id.fullName);
            follow_button = itemView.findViewById(R.id.follow_button);
        }
    }
}
