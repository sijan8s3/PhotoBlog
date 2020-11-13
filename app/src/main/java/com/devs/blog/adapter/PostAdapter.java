package com.devs.blog.adapter;

import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devs.blog.CommentActivity;
import com.devs.blog.R;
import com.devs.blog.model.Post;
import com.devs.blog.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.hendraanggrian.appcompat.widget.SocialTextView;

import java.util.List;

public class PostAdapter extends RecyclerView.Adapter<PostAdapter.ViewHolder>{
    private final Context context;
    private List<Post> posts;
    final FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();


    public PostAdapter(Context context, List<Post> posts) {
        this.context = context;
        this.posts = posts;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.post_item, parent, false);


        return new PostAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        final Post post= posts.get(position);
        Glide.with(context).load(post.getImageUrl()).into(holder.postImg);
        holder.description.setText(post.getDescription());

        FirebaseDatabase.getInstance().getReference().child("Users").child(post.getCreator()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);

                //if user have unique image url -> show the image, else -> show the launcher
                if (user.getImageurl().equals("default")){
                    holder.profileImg.setImageResource(R.drawable.logo);
                }else {
                    Glide.with(context).load(user.getImageurl()).placeholder(R.mipmap.ic_launcher).into(holder.profileImg);
                }
                Glide.with(context).load(user.getImageurl()).into(holder.profileImg);
                holder.username.setText(user.getUsername());
                holder.creator.setText(user.getName());
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.commentsImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, CommentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("creatorID", post.getCreator());
                context.startActivity(intent);
            }
        });
        holder.commentsNumber.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, CommentActivity.class);
                intent.putExtra("postID", post.getPostID());
                intent.putExtra("creatorID", post.getCreator());
                context.startActivity(intent);
            }
        });

        isLiked(post.getPostID(), holder.likesImg);
        likesCount(post.getPostID(),holder.likesNumber);
        cmntCount(post.getPostID(), holder.commentsNumber);

        //on clicking like image icon
        holder.likesImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (holder.likesImg.getTag().equals("like")){
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostID()).child(user.getUid()).setValue(true);
                }else {
                    FirebaseDatabase.getInstance().getReference().child("Likes").child(post.getPostID()).child(user.getUid()).removeValue();
                }
            }
        });

    }

    @Override
    public int getItemCount() {
        return posts.size() ;
    }

    public class ViewHolder extends RecyclerView.ViewHolder{

        public ImageView profileImg, postImg, likesImg, commentsImg, saveImg, moreImg;
        public TextView username, likesNumber, commentsNumber, creator;
        SocialTextView description;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);

            profileImg= itemView.findViewById(R.id.profile_image);
            postImg= itemView.findViewById(R.id.post_image);
            username= itemView.findViewById(R.id.username);
            likesImg= itemView.findViewById(R.id.like);
            commentsImg= itemView.findViewById(R.id.comment);
            saveImg= itemView.findViewById(R.id.save);
            likesNumber= itemView.findViewById(R.id.likes_number);
            commentsNumber= itemView.findViewById(R.id.comments_number);
            creator= itemView.findViewById(R.id.author);
            description= itemView.findViewById(R.id.description);
            moreImg= itemView.findViewById(R.id.more);


        }
    }

    //checks if the post is already liked or not
    private void isLiked(String postID, final ImageView likeIcon){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //if Uid exists inside Likes -> PostID
                if (snapshot.child(user.getUid()).exists()){
                    likeIcon.setImageResource(R.drawable.ic_liked);
                    likeIcon.setTag("liked");
                } else {
                    likeIcon.setImageResource(R.drawable.ic_like);
                    likeIcon.setTag("like");

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void likesCount(String postID, final TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Likes").child(postID).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //count the number of ids inside likes->postID
                textView.setText(snapshot.getChildrenCount()+" likes");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
    private void cmntCount(String postID, final TextView textView){
        FirebaseDatabase.getInstance().getReference().child("Comments").child(postID).addValueEventListener(new ValueEventListener() {
            @RequiresApi(api = Build.VERSION_CODES.N)
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                //count the number of ids inside Comments->postID
                Integer cmnt= Math.toIntExact(snapshot.getChildrenCount());
                if (cmnt.equals(0)){
                    textView.setVisibility(View.GONE);
                }else {
                    textView.setText("View all " + snapshot.getChildrenCount() + " comments");
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
