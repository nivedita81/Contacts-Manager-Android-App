package com.example.group8_inclass12;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.Continuation;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.Date;
import java.util.regex.Pattern;

public class NewContact extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 1;
    ImageView iv_camera;
    EditText et_contactname, et_contactemail, et_phone;
    Button btn_submit;
    Bitmap bitmap;
    FirebaseStorage storage;
    StorageReference reference, imageStorageReference;
    Boolean isPhotoPresent = false;
    User addContact;
    String imageURL;
    private FirebaseFirestore db;
    static String CONTACT = "contact";
    static FirebaseAuth uAuth;
    static Credentials userID = null;
    String fullname, contactemail;
    String emailPattern = "[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_contact);

        setTitle("Create New Contact");
        uAuth = FirebaseAuth.getInstance();
        final FirebaseUser user = uAuth.getCurrentUser();
        userID = new Credentials(user.getUid());

        iv_camera = findViewById(R.id.iv_newImage);
        et_contactname = findViewById(R.id.et_contactname);
        et_contactemail = findViewById(R.id.et_contactemail);
        et_phone = findViewById(R.id.et_contactphone);
        btn_submit = findViewById(R.id.btn_submit);

        fullname = et_contactname.getText().toString();
        contactemail = et_contactemail.getText().toString();

        storage = FirebaseStorage.getInstance();
        reference = storage.getReference();
        db = FirebaseFirestore.getInstance();

        iv_camera.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        //uploading the captured image to Firebase
        btn_submit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(et_contactname.getText().toString().equals("")){
                    et_contactname.setError("Please enter name");
                    Toast.makeText(NewContact.this, "Name is empty", Toast.LENGTH_SHORT).show();
                }
                else if(et_phone.getText().toString().equals("")){
                    et_phone.setError("Please enter Phone number");
                    Toast.makeText(NewContact.this, "Phone number is empty", Toast.LENGTH_SHORT).show();
                }
                else if(et_contactemail.getText().toString().equals("")){
                    et_contactemail.setError("Please enter email");
                    Toast.makeText(NewContact.this, "Email is empty", Toast.LENGTH_SHORT).show();
                }
                else{
//                    if(isCorrectFormat(et_contactemail.getText().toString())){
                    if(et_contactemail.getText().toString().matches(emailPattern)){
                        if(isPhotoPresent){
                            imageStorageReference = reference.child("images/"+new Date().getTime());
                            ByteArrayOutputStream baos = new ByteArrayOutputStream();
                            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                            byte[] data = baos.toByteArray();
                            UploadTask uploadTask = imageStorageReference.putBytes(data);

                            Task<Uri> urlTask = uploadTask.continueWithTask(new Continuation<UploadTask.TaskSnapshot, Task<Uri>>() {
                                @Override
                                public Task<Uri> then(@NonNull Task<UploadTask.TaskSnapshot> task) throws Exception {
                                    if (!task.isSuccessful()) {
                                        throw task.getException();
                                    }
                                    return imageStorageReference.getDownloadUrl();
                                }
                            }).addOnCompleteListener(new OnCompleteListener<Uri>() {
                                @Override
                                public void onComplete(@NonNull Task<Uri> task) {
                                    if (task.isSuccessful()) {
                                        Uri downloadUri = task.getResult();
                                        Log.d("demo", "Image URI: "+downloadUri);

                                        Long phone = Long.parseLong(et_phone.getText().toString());
                                        addContact = new User(et_contactname.getText().toString() , et_contactemail.getText().toString(), phone, downloadUri.toString(),"");
                                        addToDatabase(addContact);

                                    }
                                }
                            });
                        }else{
                            Long phone = Long.parseLong(et_phone.getText().toString());
                            addContact = new User(et_contactname.getText().toString() , et_contactemail.getText().toString(), phone, "","");
                            addToDatabase(addContact);
                        }
                    }
                    else{
                        Toast.makeText(NewContact.this, "Email Address format is incorrect. Please check.", Toast.LENGTH_SHORT).show();
                    }}}
        });
    }

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent("android.media.action.IMAGE_CAPTURE");
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    private void addToDatabase(final User contact){
        db.collection(userID.userID)
                .add(contact)
                .addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
                    @Override
                    public void onSuccess(DocumentReference documentReference) {
                        Log.d("demo", "Contact Successfully added!");
                        contact.docId = documentReference.getId();
                        Intent intent = new Intent();
                        intent.putExtra(CONTACT, contact); //sending values in Firebase and intent to MainActivity
                        setResult(200, intent);
                        finish();
                    }
                }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Log.d("demo", "Some Error occured");
            }
        });

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bundle extras = data.getExtras();
            Bitmap imageBitmap = (Bitmap) extras.get("data");
            bitmap = imageBitmap;
            iv_camera.setImageBitmap(imageBitmap);
            isPhotoPresent = true;
        }
    }
    public static boolean isCorrectFormat(String check)
    {
        String emailPattern = "^[a-zA-Z0-9_+&*-]+(?:\\."+ "[a-zA-Z0-9_+&*-]+)*@" + "(?:[a-zA-Z0-9-]+\\.)+[a-z" + "A-Z]{2,7}$";
        Pattern pattern = Pattern.compile(emailPattern);
        if (check == null)
            return false;
        return pattern.matcher(check).matches();
    }
}
