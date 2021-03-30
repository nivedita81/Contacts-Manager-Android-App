package com.example.group8_inclass12;

import android.graphics.drawable.Drawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;

public class MyAdapter extends RecyclerView.Adapter<MyAdapter.ViewHolder> {
    private final ArrayList<User> contactData;
    private ContactList ctx;
    User contact;
    public InteractWithRecyclerView interact;
    private FirebaseFirestore db;
    FirebaseAuth uAuth;


    public MyAdapter(ArrayList<User> contactData, ContactList contactList) {
        this.contactData = contactData;
        this.ctx = (ContactList) contactList;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(parent.getContext()).inflate(R.layout.recyclerview,parent,false);
        ViewHolder vh = new ViewHolder(layout);
        return vh;
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, final int position) {

        interact = (InteractWithRecyclerView) ctx;
        contact = contactData.get(position);

        holder.tv_contactName.setText(contact.getName());
        String phone = String.valueOf(contact.getPhone());
        holder.tv_contactPhone.setText(phone);
        holder.tv_contactEmail.setText(contact.getEmail());
        String imageURL = contact.getPicture();
        String defImage = "@drawable/android";
        if(imageURL == ""){
            int imageResource = ctx.getResources().getIdentifier(defImage, null, ctx.getPackageName());
            Drawable image_res = ctx.getDrawable(imageResource);
            holder.iv_contactImage.setImageDrawable(image_res);
        }
        else{
            Picasso.get().load(imageURL).into(holder.iv_contactImage);

        }
        holder.user_contacts = contact;
        uAuth = FirebaseAuth.getInstance();
        FirebaseUser user = uAuth.getCurrentUser();
        final Credentials userID = new Credentials(user.getUid());
        holder.recycler_linearlayout.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                db = FirebaseFirestore.getInstance();
                db.collection(userID.userID).document(contactData.get(position).docId)
                        .delete()
                        .addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                interact.deleteItem(position);
                            }
                        })
                        .addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                            }
                        });
                return false;
            }
        });

    }


    @Override
    public int getItemCount() {
        return contactData.size();
    }
    public interface InteractWithRecyclerView {
        void deleteItem(int position);
    }

    public class ViewHolder extends RecyclerView.ViewHolder{
        TextView tv_contactName, tv_contactEmail, tv_contactPhone;
        ImageView iv_contactImage;
        User user_contacts;
        LinearLayout recycler_linearlayout;

        @Override
        public String toString() {
            return "ViewHolder{" +
                    "tv_contactName=" + tv_contactName +
                    ", tv_contactEmail=" + tv_contactEmail +
                    ", tv_contactPhone=" + tv_contactPhone +
                    ", iv_contactImage=" + iv_contactImage +
                    ", user_contacts=" + user_contacts +
                    ", recycler_linearlayout=" + recycler_linearlayout +
                    '}';
        }

        public ViewHolder(final @NonNull View itemView) {
            super(itemView);
            tv_contactName = itemView.findViewById(R.id.rv_fullname);
            tv_contactEmail = itemView.findViewById(R.id.rv_email);
            tv_contactPhone = itemView.findViewById(R.id.rv_phone);
            iv_contactImage = itemView.findViewById(R.id.iv_contacticon);
            recycler_linearlayout = itemView.findViewById(R.id.rv_linearLayout);
        }
    }
}
