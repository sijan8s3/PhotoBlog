package com.devs.blog.adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.devs.blog.BrowseActivity;
import com.devs.blog.MainActivity;
import com.devs.blog.R;
import com.devs.blog.model.Comment;
import com.devs.blog.model.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

public class CommentAdapter extends RecyclerView.Adapter<CommentAdapter.ViewHolder>{


    private Context context;
    private List<Comment> comments;
    String postID;
    FirebaseUser firebaseUser;

    public CommentAdapter(Context context, List<Comment> comments, String postID) {
        this.context = context;
        this.comments = comments;
        this.postID= postID;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(context).inflate(R.layout.comment_item, parent, false);

        return new CommentAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull final ViewHolder holder, int position) {
        firebaseUser= FirebaseAuth.getInstance().getCurrentUser();


        final Comment comment= comments.get(position);
        holder.comment.setText(comment.getComment());

        FirebaseDatabase.getInstance().getReference().child("Users").child(comment.getAuthorID()).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                User user= snapshot.getValue(User.class);
                holder.username.setText(user.getUsername());
                if (user.getImageurl().equals("default")){
                    holder.profileImage.setImageResource(R.drawable.logo);
                }else {
                    Glide.with(context).load(user.getImageurl()).into(holder.profileImage);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        holder.profileImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, BrowseActivity.class);
                intent.putExtra("authorID", comment.getAuthorID());
                context.startActivity(intent);
            }
        });
        holder.username.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent= new Intent(context, BrowseActivity.class);
                intent.putExtra("authorID", comment.getAuthorID());
                context.startActivity(intent);
            }
        });
        holder.itemView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View view) {
                if (comment.getAuthorID().equals(firebaseUser.getUid())){
                    AlertDialog alertDialog= new AlertDialog.Builder(context).create();
                    alertDialog.setTitle("Delete your comment?");
                    alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            dialogInterface.dismiss();
                        }
                    });
                    alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "YES", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(final DialogInterface dialogInterface, int i) {
                            FirebaseDatabase.getInstance().getReference().child("Comments").
                                    child(postID).child(comment.getCommentID()).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()){
                                        Toast.makeText(context, "Comment Deleted!", Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }else {
                                        Toast.makeText(context, task.getException().getMessage(), Toast.LENGTH_SHORT).show();
                                        dialogInterface.dismiss();
                                    }
                                }
                            });
                        }
                    });
                    alertDialog.show();
                }
                return true;
            }
        });

    }

    @Override
    public int getItemCount() {
        return comments.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        public ImageView profileImage;
        public TextView username, comment;

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            profileImage= itemView.findViewById(R.id.profile_image);
            username= itemView.findViewById(R.id.username);
            comment= itemView.findViewById(R.id.comment);

        }
    }
}
