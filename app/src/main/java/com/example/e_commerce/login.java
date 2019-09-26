package com.example.e_commerce;

import android.app.ProgressDialog;
import android.content.Intent;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.e_commerce.Model.Users;
import com.example.e_commerce.Prevalent.Prevalent;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.rey.material.widget.CheckBox;

import io.paperdb.Paper;

public class login extends AppCompatActivity
{
    private EditText inputNumber,inputPassword;
    private Button btn_login;
    private ProgressDialog loadingBar;
    //private TextView adminLink,notAdminLink;

    private String parentDBName="Users";
    private CheckBox checkRememberMe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        btn_login=(Button)findViewById(R.id.btn_login);
        inputNumber=(EditText)findViewById(R.id.input_login_phoneNumber);
        inputPassword=(EditText)findViewById(R.id.input_login_password);
        loadingBar=new ProgressDialog(this);
        //adminLink=(TextView)findViewById(R.id.link_adminPanel);
        //notAdminLink=(TextView)findViewById(R.id.link_not_adminPanel);

        checkRememberMe=(CheckBox)findViewById(R.id.remember_me_checkbox);
        Paper.init(this);

        btn_login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                loginUser();

            }
        });

        /*adminLink.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View view)
            {
                btn_login.setText("Login Admin");
                adminLink.setVisibility(View.INVISIBLE);
                notAdminLink.setVisibility(View.VISIBLE);

                parentDBName="Admins";
            }
        });

        notAdminLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
             btn_login.setText("Login");
             adminLink.setVisibility(View.VISIBLE);
             notAdminLink.setVisibility(View.INVISIBLE);


             parentDBName="Users";
            }
        });*/
    }

    private void loginUser()
    {
        String phone=inputNumber.getText().toString();
        String password=inputPassword.getText().toString();

        if(TextUtils.isEmpty(phone))
        {
            Toast.makeText(this, "Please enter your phone number...", Toast.LENGTH_SHORT).show();
        }
        else if(TextUtils.isEmpty(password))
        {
            Toast.makeText(this, "Please input a password", Toast.LENGTH_SHORT).show();
        }
        else
        {
            loadingBar.setTitle("Login Account");
            loadingBar.setMessage("Please wait,while we check your credentials");
            loadingBar.setCanceledOnTouchOutside(false);
            loadingBar.show();

            AllowAccessAccount(phone,password);
        }
    }

    private void AllowAccessAccount(final String phone, final String password)
    {
        if(checkRememberMe.isChecked())
        {
            Paper.book().write(Prevalent.userPhonekey,phone);
            Paper.book().write(Prevalent.userPasswordkey,password);
        }
        final DatabaseReference rootRef;
        rootRef= FirebaseDatabase.getInstance().getReference();

        rootRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.child(parentDBName).child(phone).exists())
                {
                    Users usersData=dataSnapshot.child(parentDBName).child(phone).getValue(Users.class);

                    if(usersData.getPhone().equals(phone))
                    {
                        if(usersData.getPassword().equals(password))
                        {
                            if(parentDBName.equals("Admins"))
                            {
                                Toast.makeText(login.this, "Welcome Admin, you Logged in successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                startActivity(new Intent(login.this,adminAddProduct.class));
                            }
                            else if(parentDBName.equals("Users"))
                            {
                                Toast.makeText(login.this, "Logged in successfully...", Toast.LENGTH_SHORT).show();
                                loadingBar.dismiss();

                                startActivity(new Intent(login.this,Home.class));
                                Prevalent.onlineUsers=usersData;
                            }

                        }
                        else
                        {
                            loadingBar.dismiss();
                            Toast.makeText(login.this, "Incorrect password", Toast.LENGTH_SHORT).show();
                        }
                    }
                }
                else
                {
                    Toast.makeText(login.this, "Account with this " + phone + "don't exists", Toast.LENGTH_SHORT).show();
                    loadingBar.dismiss();
                    //Toast.makeText(login.this, "Create another account", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });
    }
}
