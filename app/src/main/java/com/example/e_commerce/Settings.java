package com.example.e_commerce;

import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.e_commerce.Prevalent.Prevalent;
import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.squareup.picasso.Picasso;
import com.theartofdev.edmodo.cropper.CropImage;

import java.util.HashMap;

import de.hdodenhof.circleimageview.CircleImageView;

public class Settings extends AppCompatActivity {

    private CircleImageView profileImageView;
    private EditText fullnames,addressChange,phoneNumber;
    private TextView profileChangebtn,closeChangebtn,updateBtn;

    private Uri imageUri;
    private String myUrl="";
    private StorageTask uploadTask;
    private StorageReference storageProfilePicture;
    private String checker="";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        storageProfilePicture= FirebaseStorage.getInstance().getReference().child("Profile Pictures");

        profileImageView=(CircleImageView)findViewById(R.id.profile_image_setting);
        fullnames=(EditText)findViewById(R.id.settings_fullnames);
        addressChange=(EditText)findViewById(R.id.settings_address);
        phoneNumber=(EditText)findViewById(R.id.settings_profile_number);
        profileChangebtn=(TextView)findViewById(R.id.profile_image_settings_btn);
        closeChangebtn=(TextView)findViewById(R.id.close_setting);
        updateBtn=(TextView)findViewById(R.id.update_settings);

        
        userInfoDisplay(profileImageView,fullnames,addressChange,phoneNumber,profileChangebtn,closeChangebtn,updateBtn);

        closeChangebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                finish();
            }
        });
        updateBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                if(checker.equals("clicked"))
                {
                    userInfoSaved();

                }else
                {
                    updateUserInfo();
                }
            }
        });

        profileChangebtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view)
            {
                checker="clicked";
                CropImage.activity(imageUri)
                        .setAspectRatio(1,1)
                        .start(Settings.this);
            }
        });
    }

    private void updateUserInfo()
    {
        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

        HashMap<String,Object> userMap=new HashMap<>();
        userMap.put("name",fullnames.getText().toString());
        userMap.put("address",addressChange.getText().toString());
        userMap.put("phoneOrder",phoneNumber.getText().toString());
        ref.child(Prevalent.onlineUsers.getPhone()).updateChildren(userMap);


        startActivity(new Intent(Settings.this,MainActivity.class));
        Toast.makeText(Settings.this, "Farmer info updated successfully", Toast.LENGTH_SHORT).show();
        finish();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data)
    {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode==CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE && resultCode==RESULT_OK && data!=null)
        {
            CropImage.ActivityResult result=CropImage.getActivityResult(data);
            imageUri=result.getUri();

            profileImageView.setImageURI(imageUri);

        }else
        {
            Toast.makeText(this, "Error! Try Again farmer", Toast.LENGTH_SHORT).show();
            startActivity(new Intent(Settings.this,Settings.class));
            finish();
        }
    }

    private void userInfoSaved()
    {
            if(TextUtils.isEmpty(fullnames.getText().toString())){

                Toast.makeText(this, "Name is mandatory", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(addressChange.getText().toString()))
            {
                Toast.makeText(this, "Add an address", Toast.LENGTH_SHORT).show();
            }
            else if (TextUtils.isEmpty(phoneNumber.getText().toString()))
            {
                Toast.makeText(this, "input phone number", Toast.LENGTH_SHORT).show();
            }
            else if (checker.equals("clicked"))
            {
                uploadImage();
            }


    }

    private void uploadImage()
    {
        final ProgressDialog progressDialog=new ProgressDialog(this);
        progressDialog.setTitle("Update Profile");
        progressDialog.setMessage("Please wait as we update your information farmer");
        progressDialog.setCanceledOnTouchOutside(false);
        progressDialog.show();

        if(imageUri!=null)
        {
            final StorageReference fileReference=storageProfilePicture
                    .child(Prevalent.onlineUsers.getPhone() + ".jpg");

            uploadTask=fileReference.putFile(imageUri);

            uploadTask.continueWith(new Continuation()
            {
                @Override
                public Object then(@androidx.annotation.NonNull Task task) throws Exception
                {
                    if(!task.isSuccessful())
                    {
                        throw task.getException();
                    }
                    return fileReference.getDownloadUrl();
                }
            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                @Override
                public void onComplete(@androidx.annotation.NonNull Task<Uri> task)
                {
                    if(task.isSuccessful())
                    {
                        Uri downloadUri=task.getResult();
                        myUrl=downloadUri.toString();

                        DatabaseReference ref=FirebaseDatabase.getInstance().getReference().child("Users");

                        HashMap<String,Object> userMap=new HashMap<>();
                        userMap.put("name",fullnames.getText().toString());
                        userMap.put("address",addressChange.getText().toString());
                        userMap.put("phoneOrder",phoneNumber.getText().toString());
                        userMap.put("image",myUrl);
                        ref.child(Prevalent.onlineUsers.getPhone()).updateChildren(userMap);

                        progressDialog.dismiss();

                        startActivity(new Intent(Settings.this,MainActivity.class));
                        Toast.makeText(Settings.this, "Farmer info updated successfully", Toast.LENGTH_SHORT).show();
                        finish();

                    }
                    else{
                        progressDialog.dismiss();
                        Toast.makeText(Settings.this, "Error!", Toast.LENGTH_SHORT).show();

                    }

                }
            });

        }else
        {
            Toast.makeText(this, "Image is not selected", Toast.LENGTH_SHORT).show();
        }

    }

    private void userInfoDisplay(final CircleImageView profileImageView, final EditText fullnames,final EditText addressChange, final EditText phoneNumber, TextView profileChangebtn, TextView closeChangebtn, TextView updateBtn)
    {
        DatabaseReference usersRef= FirebaseDatabase.getInstance().getReference().child("Users").child(Prevalent.onlineUsers.getPhone());
        usersRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot dataSnapshot)
            {
                if(dataSnapshot.exists()){
                    if(dataSnapshot.child("image").exists())
                    {
                        String image=dataSnapshot.child("image").getValue().toString();
                        String name=dataSnapshot.child("name").getValue().toString();
                        String phone=dataSnapshot.child("phone").getValue().toString();
                        String address=dataSnapshot.child("address").getValue().toString();

                        Picasso.get().load(image).into(profileImageView);
                        fullnames.setText(name);
                        phoneNumber.setText(phone);
                        addressChange.setText(address);


                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError databaseError) {

            }
        });

    }
}
