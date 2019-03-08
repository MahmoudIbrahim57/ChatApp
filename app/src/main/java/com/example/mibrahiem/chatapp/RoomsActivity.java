package com.example.mibrahiem.chatapp;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;
import java.util.Set;

public class RoomsActivity extends AppCompatActivity {
ProgressBar progressBar;
    private ListView listView;
    private EditText etRoomName,etUsername,etPassword,loginName,loginPassword;
    String roomName;
String password;
    private Button btAccess,bt_create;
    ArrayList<String> arrayList;
    ArrayAdapter adapter;
    DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rooms);

        progressBar= (ProgressBar) findViewById(R.id.pb_load);
        progressBar.setProgress(10);
        listView= (ListView) findViewById(R.id.lv_items);
        etUsername= (EditText) findViewById(R.id.et_user_name);
        etPassword= (EditText) findViewById(R.id.et_user_password);
        //floating buttton action
        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                CreateMastaba();

            }
        });
        databaseReference  = FirebaseDatabase.getInstance().getReference().getRoot();

        arrayList=new ArrayList<>();
        adapter=new ArrayAdapter(RoomsActivity.this,android.R.layout.simple_list_item_1,arrayList);
        listView.setAdapter(adapter);
         databaseReference.addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Set<String> set=new HashSet<String>();
                Iterator iterator = dataSnapshot.getChildren().iterator();
                set.add(((DataSnapshot) iterator.next()).getKey());
                while (iterator.hasNext())
                {

                    set.add(((DataSnapshot) iterator.next()).getKey());

                }
                arrayList.clear();
                arrayList.addAll(set);
                adapter.notifyDataSetChanged();
                progressBar.setVisibility(View.GONE);


            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, final View view, int position, long id) {

                roomName= (String) listView.getItemAtPosition(position);
                final Dialog dialog = new Dialog(RoomsActivity.this);
                dialog.setContentView(R.layout.access_room_dialog);
                dialog.setTitle("دخول للمصطبه");
                dialog.show();

                loginName= (EditText) dialog.findViewById(R.id.et_user_name);
                loginPassword= (EditText) dialog.findViewById(R.id.et_user_password);
                // retieve the password

                DatabaseReference databaseReference1 = databaseReference.child(roomName).child("PASSWORD");
                databaseReference1.addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                          password = dataSnapshot.getValue(String.class);

                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

//                //////////////////////////
                 btAccess= (Button) dialog.findViewById(R.id.bt_access_room);
                btAccess.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        try{
                            String userNameLogin=loginName.getText().toString();
                        String userPasswordLogin=loginPassword.getText().toString();
                        if(password.equals(userPasswordLogin)) {
                            Intent intent = new Intent(RoomsActivity.this, ChatRoom.class);
                            intent.putExtra("room_name", roomName);
                            intent.putExtra("USER_NAME", userNameLogin);
                            intent.putExtra("USER_PASSWORD", userPasswordLogin);
                            startActivity(intent);
                            finish();
                            dialog.dismiss();
                        }
                        else
                        {
                            Toast.makeText(RoomsActivity.this, "كلمة السر غلط", Toast.LENGTH_SHORT).show();
                            dialog.dismiss();

                        }

                        }
                        catch (Exception e){
                            Toast.makeText(RoomsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();

                        }
                    }

                });

                    }
                });


            }

    private void CreateMastaba() {
          final Dialog dialog=new Dialog(RoomsActivity.this);
        dialog.setContentView(R.layout.create_room_dialog);
        etRoomName= (EditText) dialog.findViewById(R.id.et_room_name);
        etPassword= (EditText) dialog.findViewById(R.id.et_create_password);
        etUsername= (EditText) dialog.findViewById(R.id.et_create_user);

        dialog.setTitle("بناء مصطبه");
        dialog.show();
        bt_create= (Button) dialog.findViewById(R.id.bt_create_room);
        bt_create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
               try{
                String room=etRoomName.getText().toString();
                String password=etPassword.getText().toString();
                String userName=etUsername.getText().toString();

                if(room.equals("")&&password.equals("")&&userName.equals("")){

                    Toast.makeText(RoomsActivity.this, "اكتب البيانات", Toast.LENGTH_SHORT).show();
              }
              else{
                    databaseReference  = FirebaseDatabase.getInstance().getReference().child(room);

                    Map<String,Object> map1 = new HashMap<String,Object>();
                    map1.put("PASSWORD",password);
                    databaseReference.updateChildren(map1);

                    Intent intent=new Intent(RoomsActivity.this,ChatRoom.class);
                    intent.putExtra("USER_NAME",userName);
                    intent.putExtra("room_name",room);
                    intent.putExtra("USER_PASSWORD",password);
                    startActivity(intent);
                    finish();
                    dialog.dismiss();
                    Toast.makeText(RoomsActivity.this,"خش ياابوعمو" , Toast.LENGTH_LONG).show();

                }
               }
            catch (Exception e){

                Toast.makeText(RoomsActivity.this, ""+e.getMessage(), Toast.LENGTH_SHORT).show();
            }
            }
        });





        }
}
