package com.example.mibrahiem.chatapp;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;
import com.google.firebase.database.ChildEventListener;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class ChatRoom extends AppCompatActivity {
    private Button sendButton;
    private EditText typing;
    private TextView textView,tvTitle;
    String room,user,key,passwrod,msg;
    ProgressBar progressBar;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat_room);
        progressBar= (ProgressBar) findViewById(R.id.pb_loadMessages);
        progressBar.setProgress(10);
        tvTitle= (TextView) findViewById(R.id.tv_title);
        sendButton= (Button) findViewById(R.id.bt_go);
        typing = (EditText) findViewById(R.id.et_typing);
        textView= (TextView) findViewById(R.id.tv_msg);
            room= getIntent().getExtras().getString("room_name");
            user= getIntent().getExtras().getString("USER_NAME");
            passwrod= getIntent().getExtras().getString("USER_PASSWORD");
        tvTitle.setText(""+room);
        databaseReference  = FirebaseDatabase.getInstance().getReference().child(room).child(passwrod);
        sendButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                msg=typing.getText().toString();

                if(msg.equals("")){
                    Toast.makeText(ChatRoom.this, ""+"اكتب الرساله", Toast.LENGTH_SHORT).show();
            }
            else{

                 key=databaseReference.push().getKey();
                DatabaseReference databaseReference1 = databaseReference.child(key);
                Map<String,Object> map1 = new HashMap<String,Object>();
                map1.put("USER_NAME",user);
                map1.put("MESSAGE",msg);
                databaseReference1.updateChildren(map1);
                typing.setText("");
            }
            }
        });
        databaseReference.addChildEventListener(new ChildEventListener() {
            @Override
            public void onChildAdded(DataSnapshot dataSnapshot, String s) {
            append_text(dataSnapshot);
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onChildChanged(DataSnapshot dataSnapshot, String s) {
                append_text(dataSnapshot);

            }

            @Override
            public void onChildRemoved(DataSnapshot dataSnapshot) {

            }

            @Override
            public void onChildMoved(DataSnapshot dataSnapshot, String s) {

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


    }
        String name,message;
    private void append_text(DataSnapshot dataSnapshot) {
        try{ Iterator iterator = dataSnapshot.getChildren().iterator();
        while (iterator.hasNext())
        {
            name=(String) ((DataSnapshot)iterator.next()).getValue();
            message=(String) ((DataSnapshot)iterator.next()).getValue();
            textView.append("\n"+message+" : "+name+"\n");

        }

        }
    catch (Exception e){
        Toast.makeText(this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

    }
    }
}
