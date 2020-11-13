package com.devs.blog;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    private EditText username, name, email, password;
    private Button register;
    private TextView loginUser;
    private DatabaseReference rootRef;
    private FirebaseAuth firebaseAuth;
    
    ProgressDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = findViewById(R.id.username);
        name = findViewById(R.id.fname);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        loginUser= findViewById(R.id.loginUser);
        register= findViewById(R.id.register);

        rootRef = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        dialog = new ProgressDialog(this);

        loginUser.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivity(new Intent(RegisterActivity.this, LoginActivity.class));
            }
        });
        register.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String txtUsername = username.getText().toString().trim();
                String txtName = name.getText().toString().trim();
                String txtEmail = email.getText().toString().trim();
                String txtPassword = password.getText().toString().trim();

                if (TextUtils.isEmpty(txtUsername) ||
                        TextUtils.isEmpty(txtName) ||
                        TextUtils.isEmpty(txtEmail) ||
                        TextUtils.isEmpty(txtPassword)){
                    Toast.makeText(RegisterActivity.this, "Empty Credentials", Toast.LENGTH_SHORT).show();
                }else if (txtPassword.length()<8){
                    Toast.makeText(RegisterActivity.this, "Password must be at least 8 characters!", Toast.LENGTH_SHORT).show();
                }
                registerUser(txtUsername, txtName, txtEmail, txtPassword);
            }
        });


    }

    private void registerUser(final String username, final String name, final String email, String password) {
        dialog.setMessage("registering... please wait");
        dialog.show();
        firebaseAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                HashMap<String, Object> hashMap= new HashMap<>();
                hashMap.put("username", username);
                hashMap.put("name", name);
                hashMap.put("email", email);
                hashMap.put("Uid", firebaseAuth.getCurrentUser().getUid());
                hashMap.put("bio", "");
                hashMap.put("imageurl", "default");

                rootRef.child("Users").child(firebaseAuth.getCurrentUser().getUid()).setValue(hashMap).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()){
                            dialog.dismiss();
                            Toast.makeText(RegisterActivity.this, "Added to firebase", Toast.LENGTH_SHORT).show();
                            startActivity(new Intent(RegisterActivity.this, BrowseActivity.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}