package com.wallring;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;
import es.dmoral.toasty.Toasty;

import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.FirebaseApp;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.*;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wallring.Common.Common;
import com.wallring.Model.User;


public class SignIn extends AppCompatActivity {
    EditText edtPhone, edtPassword;
    Button btnSignIn;
    TextView txtForgotPwd;

    FirebaseDatabase database;
    DatabaseReference table_user;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        FirebaseApp.initializeApp(this);

        if(getIntent().getStringExtra("exist")!=null) {
            if (getIntent().getStringExtra("exist").equalsIgnoreCase("true")) {
                Toasty.warning(SignIn.this, "ID number already registered!", Toast.LENGTH_LONG).show();
            } else {
                Toasty.success(SignIn.this, "Sign up successfully!", Toast.LENGTH_LONG).show();
            }
        }


        edtPassword = (MaterialEditText)findViewById(R.id.edtPasswordI);
        edtPhone = (MaterialEditText)findViewById(R.id.edtPhoneI);
        txtForgotPwd = (TextView) findViewById(R.id.txtForgotPwd);

        btnSignIn = (Button)findViewById(R.id.btnSignIn);


        // Init Firebase
        database = FirebaseDatabase.getInstance();
        table_user = database.getReference("User");

        txtForgotPwd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showForgotPwdDialog();
                
            }
        });

        btnSignIn.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(SignIn.this);
                mDialog.setMessage("Please waiting ...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {

                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        // Check if user not exist in databases
                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            // Get user information
                            mDialog.dismiss();
                            User user = dataSnapshot.child(edtPhone.getText().toString()).getValue(User.class);
                            if (user.getPassword().equals(edtPassword.getText().toString())) {
                                Toasty.success(SignIn.this, "Sign in succesfully !", Toast.LENGTH_LONG).show();
                                Common.currentUser = user;
                                Intent homeIntent = new Intent(SignIn.this, Home.class);
                                homeIntent.putExtra("user", edtPhone.getText().toString());
                                homeIntent.putExtra("name", user.getName());
                                startActivity(homeIntent);
                                finish();
                            } else {
                                Toasty.error(SignIn.this, "Password is not correct!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            mDialog.dismiss();
                            Toasty.warning(SignIn.this, "User doesn't exist!", Toast.LENGTH_LONG).show();

                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });
            }
        });

    }

    private void showForgotPwdDialog() {
        Toasty.info(this, "Please enter your security number to be able to reveal your password", Toast.LENGTH_LONG).show();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Forgot Password");
        builder.setMessage("Enter your secure code");

        LayoutInflater inflater = this.getLayoutInflater();
        View forgot_view = inflater.inflate(R.layout.forgot_password_layout, null);

        builder.setView(forgot_view);
        builder.setIcon(R.drawable.ic_security_black_24dp);

        final MaterialEditText edtPhone = (MaterialEditText) forgot_view.findViewById(R.id.edtPhone);
        final MaterialEditText edtSecureCode = (MaterialEditText) forgot_view.findViewById(R.id.edtSecureCode);

        builder.setPositiveButton("YES", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int i) {
                // Check if user available
                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {
                        if(dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                            User user = dataSnapshot.child(edtPhone.getText().toString())
                                    .getValue(User.class);
                            if (user.getSecureCode().equals(edtSecureCode.getText().toString())) {
                                Toasty.normal(SignIn.this, "Your password: " + user.getPassword(), Toast.LENGTH_LONG, ContextCompat.getDrawable(
                                        getBaseContext(), R.drawable.ic_security_black_24dp)).show();
                            } else {
                                Toasty.error(SignIn.this, "Wrong security code!", Toast.LENGTH_LONG).show();
                            }
                        }else{
                            Toasty.error(SignIn.this, "User doesn't exist!", Toast.LENGTH_LONG).show();
                        }

                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });


            }
        });
        builder.setNegativeButton("NO", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });

        builder.show();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(SignIn.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
    }
}
