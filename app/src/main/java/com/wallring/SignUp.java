package com.wallring;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import es.dmoral.toasty.Toasty;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rengwuxian.materialedittext.MaterialEditText;
import com.wallring.Model.User;

public class SignUp extends AppCompatActivity {

    MaterialEditText edtPhone, edtName, edtPassword, edtSecureCode;
    Button btnSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        edtPhone = (MaterialEditText)findViewById(R.id.edtPhone);
        edtName = (MaterialEditText)findViewById(R.id.edtName);
        edtPassword = (MaterialEditText)findViewById(R.id.edtPassword);
        edtSecureCode = (MaterialEditText)findViewById(R.id.edtSecureCode);



        btnSignUp = (Button)findViewById(R.id.btnSignUp);


        // Init Firebase
        final FirebaseDatabase database = FirebaseDatabase.getInstance();
        final DatabaseReference table_user = database.getReference("User");


        btnSignUp.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                final ProgressDialog mDialog = new ProgressDialog(SignUp.this);
                mDialog.setMessage("Please waiting ...");
                mDialog.show();

                table_user.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot dataSnapshot) {

                        if(edtPhone.getText().toString().length() < 5){
                            mDialog.dismiss();
                            Toasty.warning(SignUp.this, "User ID should have 5 numbers", Toast.LENGTH_LONG).show();
                        }else {
                            // Check if already phone
                            if (dataSnapshot.child(edtPhone.getText().toString()).exists()) {
                                mDialog.dismiss();
                                Intent signIn = new Intent(SignUp.this, SignIn.class);
                                signIn.putExtra("exist", "true");
                                startActivity(signIn);
                            } else {
                                mDialog.dismiss();
                                User user = new User(edtName.getText().toString(),
                                        edtPassword.getText().toString(),
                                        edtSecureCode.getText().toString()
                                );
                                table_user.child(edtPhone.getText().toString()).setValue(user);
                                 Intent signIn = new Intent(SignUp.this, SignIn.class);
                                 signIn.putExtra("exist", "false");
                                 startActivity(signIn);
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError databaseError) {

                    }
                });

            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        Intent main = new Intent(SignUp.this, MainActivity.class);
        main.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(main);
    }
}
