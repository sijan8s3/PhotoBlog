package com.devs.blog.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devs.blog.R;
import com.devs.blog.fragments.PostDetailFragment;
import com.devs.blog.fragments.ProfileFragment;
import com.devs.blog.model.Notification;
import com.devs.blog.model.Post;
import com.devs.blog.model.User;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class NotificationAdapter extends RecyclerView.Adapter<NotificationAdapter.ViewHolder>{

    private Context context;
    private List<Notification> notificationList;

    public NotificationAdapter(Context context, List<Notification> notificationList) {
        this.context = context;
        this.notificationList = notificationList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.notification_item, parent, false);
        return new NotificationAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {
        
        final Notification notification= notificationList.get(position);
        
        getUser(holder.profileImage, holder.username, notification.getUserID());
        holder.comment.setText(notification.getText());

        if (notification.isPost()){
            holder.postImage.setVisibility(View.VISIBLE);
            getPostImage(holder.postImage, notification.getPostID());
        }else {
            holder.postImage.setVisibility(View.GONE);
        }

        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (notification.isPost()){
                    context.getSharedPreferences("POSTPREF",Context.MODE_PRIVATE).edit()
                            .putString("postID", notification.getPostID()).apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new PostDetailFragment()).commit();
                }else {
                    context.getSharedPreferences("PROFILE", Context.MODE_PRIVATE).edit()
                            .putString("profileID", notification.getUserID()).apply();

                    ((FragmentActivity)context).getSupportFragmentManager().beginTransaction()
                            .replace(R.id.fragment_container, new ProfileFragment()).commit();
                }
            }
        });

    }




    @Override
    public int getItemCount() {
        return notificationList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImage, postImage;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImage= itemView.findViewById(R.id.profile_image);
            postImage= itemView.findViewById(R.id.post_image);
            username= itemView.findViewById(R.id.username);
            comment= itemView.findViewById(R.id.comment);
        }
    }


    private void getUser(final ImageView profileImage, final TextView username, final String userID) {
        FirebaseDatabase.getInstance().getReference().child("Users").child(userID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                if (user.getImageurl().equals("default")){
                    profileImage.setImageResource(R.mipmap.ic_launcher);
                }else {
                    Glide.with(context).load(user.getImageurl()).into(profileImage);
                }
                username.setText(user.getUsername());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getPostImage(final ImageView postImage, String postID) {
        FirebaseDatabase.getInstance().getReference().child("Posts").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                Post post= snapshot.getValue(Post.class);
                Glide.with(context).load(post.getImageUrl()).placeholder(R.mipmap.ic_launcher).into(postImage);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
