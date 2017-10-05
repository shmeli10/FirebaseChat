package com.shmeli.firebasechat;

import android.content.Intent;
import android.media.MediaPlayer;
import android.provider.ContactsContract;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
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

import java.util.List;

public class MainActivity extends AppCompatActivity {

    private static int SIGN_IN_REQUEST_CODE = 1;

    private FirebaseListAdapter<Message> adapter;

    private RelativeLayout  activityMain;
    private Button          button;

//    private Button          clearBtn;

//    private boolean canPlaySound = false;

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

//                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(  input.getText().toString(),
//                                                                                            FirebaseAuth.getInstance().getCurrentUser().getEmail()));

                FirebaseDatabase.getInstance().getReference().push().setValue(new Message(  input.getText().toString(),
                                                                                            FirebaseAuth.getInstance().getCurrentUser().getEmail(),
                                                                                            true));

                input.setText("");
            }
        });

//        clearBtn = (Button) findViewById(R.id.btnClearAll);
//        clearBtn.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                Log.e("LOG", "MainActivity: onCreate: clearBtn onClick(): adapter.getCount= " +adapter.getCount());
//
//                for(int i=0; i<adapter.getCount(); i++) {
//                    //Message message = adapter.getItem(i);
//                    FirebaseDatabase.getInstance().getReference().removeValue();
//                }
//
//                adapter.cleanup();
//            }
//        });

        Log.e("LOG", "MainActivity: onCreate: user is null: " +(FirebaseAuth.getInstance().getCurrentUser() == null));

        if(FirebaseAuth.getInstance().getCurrentUser() == null) {
            startActivityForResult( AuthUI.getInstance().createSignInIntentBuilder().build(),
                                    SIGN_IN_REQUEST_CODE);
        }
        else {
            displayChat();
        }
    }

    private void displayChat() {
        Log.e("LOG", "MainActivity: displayChat()");

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

                if(model.isUnread()) {
                    playMsgSound();

//                    String msgText = model.getTextMessage();

//                    FirebaseDatabase.getInstance().getReference().push().removeValue();
//                    FirebaseDatabase.getInstance().getReference().push().setValue(  new Message(msgText,
//                                                                                    FirebaseAuth.getInstance().getCurrentUser().getEmail(),
//                                                                                    false));

                    model.setUnread(false);
                    //model.setUnread(false);
                }
            }
        };

        messageList.setAdapter(adapter);

        //adapter.cleanup();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        Log.e("LOG", "MainActivity: onActivityResult()");

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

    private void playMsgSound() {
        Log.e("LOG", "MainActivity: playMsgSound()");

        //Log.e("LOG", "MainActivity: playMsgSound(): canPlaySound: " +canPlaySound);

//        if(canPlaySound) {

            MediaPlayer mediaPlayer = MediaPlayer.create(this, R.raw.msg_sound);

            if (mediaPlayer != null) {
                mediaPlayer.start();
                mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                    public void onCompletion(MediaPlayer mp) {
                        mp.release();
                    }

                    ;
                });
            }
//        }
//        else
//            canPlaySound = !canPlaySound;
    }
}
