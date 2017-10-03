package com.shmeli.firebasechat;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.firebase.ui.auth.AuthUI;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import com.shmeli.firebasechat.Model.Message;

import com.firebase.ui.database.FirebaseListAdapter;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;

    private FirebaseListAdapter<Message> adapter;

    private RelativeLayout  activityMain;
    private Button          button;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        activityMain = (RelativeLayout) findViewById(R.id.activity_main);

        button = (Button) findViewById(R.id.button2);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                EditText input = (EditText) findViewById(R.id.editText);

                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(  input.getText().toString(),
                                                                                            FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                input.setText("");
            }
        });

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult(AuthUI.getInstance().createSignInIntentBuilder().build(),
                    SIGN_IN_REQUEST_CODE);
        }
        else {
            displayChat();
        }
    }

    private void displayChat() {

        ListView messageList = (ListView) findViewById(R.id.listView);

        adapter = new FirebaseListAdapter<Message>( this,
                                                    Message.class,
                                                    R.layout.item,
                                                    FirebaseDatabase.getInstance().getReference()) {
            @Override
            protected void populateView(View v, Message model, int position) {

                TextView textMessage, author, timeMessage;

                textMessage = (TextView) v.findViewById(R.id.tvMessage);
                textMessage.setText(model.getTextMessage());

                author      = (TextView) v.findViewById(R.id.tvUser);
                author.setText(model.getAuthor());

                timeMessage = (TextView) v.findViewById(R.id.tvTime);
                timeMessage.setText(DateFormat.format(  "dd-MM-yyyy (HH:mm:ss)",
                                                        model.getTimeMessage()));
            }
        };

        messageList.setAdapter(adapter);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if(requestCode == SIGN_IN_REQUEST_CODE) {

            if(resultCode == RESULT_OK) {
                Snackbar.make(activityMain, "Вход выполнен", Snackbar.LENGTH_SHORT).show();
                displayChat();
            }
            else {
                Snackbar.make(activityMain, "Вход не выполнен", Snackbar.LENGTH_SHORT).show();
                finish();
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu, menu);

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.menu_signout) {

            AuthUI.getInstance().signOut(this).addOnCompleteListener(new OnCompleteListener<Void>() {
                @Override
                public void onComplete(@NonNull Task<Void> task) {

                    Snackbar.make(activityMain, "Выход выполнен", Snackbar.LENGTH_SHORT).show();
                    finish();
                }
            });
        }

        return true;
    }
}
