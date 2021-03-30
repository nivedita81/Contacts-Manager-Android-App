package com.example.group8_inclass12;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;

public class ContactList extends AppCompatActivity implements MyAdapter.InteractWithRecyclerView{
    Button btn_newcontact;
    ImageView iv_logout;
    RecyclerView recyclerView;
    MyAdapter rv_adapter;
    RecyclerView.LayoutManager layoutManager;
    private FirebaseFirestore db;
    static ArrayList<User> userArrayList = new ArrayList<>();
    private static FirebaseAuth uAuth;
    Credentials userID;

    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        recyclerView = (RecyclerView) findViewById(R.id.recycler_contacts);
        layoutManager = new LinearLayoutManager(ContactList.this);
        recyclerView.setLayoutManager(layoutManager);
        if (requestCode == 100 && resultCode == 200)
            if(data!= null && data.getExtras()!=null){
                if(data.getExtras().containsKey(NewContact.CONTACT)){
                    userArrayList.add((User) data.getExtras().getSerializable(NewContact.CONTACT));
                    rv_adapter = new MyAdapter(userArrayList, ContactList.this);
                    recyclerView.setAdapter(rv_adapter);
                    rv_adapter.notifyDataSetChanged();
                }
            }
    }


    @SuppressLint("WrongViewCast")
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_contact_list);

        setTitle("Contacts");
        uAuth = FirebaseAuth.getInstance();
        FirebaseUser user = uAuth.getCurrentUser();
        userID = new Credentials(user.getUid());
        db = FirebaseFirestore.getInstance();
        btn_newcontact = findViewById(R.id.btn_newcontact);
        iv_logout = findViewById(R.id.iv_logout);

        final Boolean flag;
        flag = getIntent().getExtras().containsKey("NEWUSER");

        btn_newcontact.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                Intent i = new Intent(ContactList.this, NewContact.class);

                startActivityForResult(i, 100);
            }
        });

        db.collection(userID.userID)
                .get()
                .addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<QuerySnapshot> task) {
                        if (task.isSuccessful()) {
                            userArrayList.clear();
                            for (QueryDocumentSnapshot document : task.getResult()) {
                                User usercontact = new User(document.getString("name"), document.get("email").toString(), document.getLong("phone"), document.getString("picture"),document.getId());
                                userArrayList.add(usercontact);
                                Log.d("demo", document.getId() + " => " + document.getData());
                            }
                            if(userArrayList.size()>0){
                                recyclerView = (RecyclerView) findViewById(R.id.recycler_contacts);
                                layoutManager = new LinearLayoutManager(ContactList.this);
                                recyclerView.setLayoutManager(layoutManager);
                                // specify an adapter
                                rv_adapter = new MyAdapter(userArrayList, ContactList.this);
                                recyclerView.setAdapter(rv_adapter);
                            }
                        } else {
                            Log.d("demo", "Error getting documents: ", task.getException());
                            Toast.makeText(ContactList.this, "Some Error occured in retrieving the documents", Toast.LENGTH_SHORT).show();
                        }
                    }

                });

        iv_logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                uAuth.getInstance().signOut();
                Intent intent = new Intent(ContactList.this, MainActivity.class);
                startActivity(intent);
                finish();
            }
        });
    }


    @Override
    public void deleteItem(int position) {
        userArrayList.remove(position);
        Toast.makeText(this, "Contact Deleted!", Toast.LENGTH_SHORT).show();
        rv_adapter.notifyDataSetChanged();
    }
}
