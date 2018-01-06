package com.app.owl;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

public class AddUserActivity extends AppCompatActivity {


    EditText editTextName;
    Button buttonAdd;

    DatabaseReference databaseSecondaryUsers;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_user);

        databaseSecondaryUsers = FirebaseDatabase.getInstance().getReference("secondaryUsers");

        editTextName = (EditText) findViewById(R.id.enterNameAddUser);
        buttonAdd = (Button) findViewById(R.id.add_user_btn);


        buttonAdd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                addSecondaryUser();
            }
        });
    }

    private void addSecondaryUser(){
        String name = editTextName.getText().toString().trim();

        if(!TextUtils.isEmpty(name)){
            String id = databaseSecondaryUsers.push().getKey();
            SecondaryUser secondaryUser = new SecondaryUser(id, name);

            databaseSecondaryUsers.child(id).setValue(secondaryUser);

            Toast.makeText(this, "Secondary user added. Name: " + name, Toast.LENGTH_SHORT).show();

        }else{
            Toast.makeText(this, "You need to enter a name", Toast.LENGTH_SHORT).show();
        }
    }


}