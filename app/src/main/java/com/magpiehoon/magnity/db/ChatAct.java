package com.magpiehoon.magnity.db;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.firebase.ui.database.FirebaseRecyclerAdapter;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.magpiehoon.magnity.R;
import com.magpiehoon.magnity.util.SignInResultNotifier;

import butterknife.BindView;
import butterknife.ButterKnife;
import butterknife.OnClick;

/**
 * Created by magpiehoon on 2017. 5. 29..
 */

public class ChatAct extends AppCompatActivity implements FirebaseAuth.AuthStateListener {

    private static final String TAG = "Chat";

    private FirebaseAuth mAuth;
    private DatabaseReference mRef;
    private DatabaseReference mChatRef;

    @BindView(R.id.btn_sendButton)
    Button mSendButton;

    @BindView(R.id.edt_messageEdit)
    EditText mMessageEdit;

    @BindView(R.id.rcv_messages_list)
    RecyclerView mMessages;

    @BindView(R.id.txv_chat_empty)
    TextView mEmptyListMessage;

    private LinearLayoutManager mManager;
    private FirebaseRecyclerAdapter<Chat, ChatHolder> mAdapter;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chat);
        ButterKnife.bind(this);

        mAuth = FirebaseAuth.getInstance();
        mAuth.addAuthStateListener(this);

        mRef = FirebaseDatabase.getInstance().getReference();
        mChatRef = mRef.child("chats");

        mManager = new LinearLayoutManager(this);
        mManager.setReverseLayout(false);

        mMessages.setHasFixedSize(false);
        mMessages.setLayoutManager(mManager);

    }

    @OnClick(R.id.btn_sendButton)
    public void sendMessage() {
        String uid = mAuth.getCurrentUser().getUid();
        String name = "User " + uid.substring(0, 6);

        Chat chat = new Chat(name, mMessageEdit.getText().toString(), uid);
        mChatRef.push().setValue(chat, new DatabaseReference.CompletionListener() {
            @Override
            public void onComplete(DatabaseError databaseError, DatabaseReference databaseReference) {
                if (databaseError != null) {
                    Log.e(TAG, "Failed to write message", databaseError.toException());
                }
            }
        });

        mMessageEdit.setText("");
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (isSignedIn()) {
            attachRecyclerViewAdapter();
        } else {
            signInAnonymously();
        }

    }

    @Override
    protected void onStop() {
        super.onStop();
        if (mAdapter != null) {
            mAdapter.cleanup();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mAuth != null) {
            mAuth.removeAuthStateListener(this);
        }
    }

    @Override
    public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
        updateUI();
    }

    private void attachRecyclerViewAdapter() {
        Query lastFifty = mChatRef.limitToLast(50);
        mAdapter = new FirebaseRecyclerAdapter<Chat, ChatHolder>(Chat.class, R.layout.message, ChatHolder.class, lastFifty) {
            @Override
            protected void populateViewHolder(ChatHolder viewHolder, Chat model, int position) {
                viewHolder.setName(model.getName());
                viewHolder.setText(model.getMessage());

                FirebaseUser currentUser = mAuth.getCurrentUser();
                if (currentUser != null && model.getUid().equals(currentUser.getUid())) {
                    viewHolder.setIsSender(true);
                } else {
                    viewHolder.setIsSender(false);
                }
            }

            @Override
            protected  void onDataChanged(){
                mEmptyListMessage.setVisibility(mAdapter.getItemCount() == 0 ? View.VISIBLE : View.GONE);
            }
        };

        mAdapter.registerAdapterDataObserver(new RecyclerView.AdapterDataObserver() {
            @Override
            public void onItemRangeInserted(int positionStart, int itemCount) {
                mManager.smoothScrollToPosition(mMessages,null,mAdapter.getItemCount());
            }
        });

        mMessages.setAdapter(mAdapter);
    }

    private void signInAnonymously() {
        Toast.makeText(this, "로그인 중..", Toast.LENGTH_SHORT).show();
        mAuth.signInAnonymously().addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                attachRecyclerViewAdapter();
            }
        }).addOnCompleteListener(new SignInResultNotifier(this));
    }

    private boolean isSignedIn() {
        return mAuth.getCurrentUser() != null;
    }

    private void updateUI() {
        mSendButton.setEnabled(isSignedIn());
        mMessageEdit.setEnabled(isSignedIn());
    }
}
