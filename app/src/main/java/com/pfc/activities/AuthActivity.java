package com.pfc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pfc.R;
import com.pfc.support.Checkers;
import com.pfc.ui.popups.DialogBox;
import com.pfc.ui.popups.Pop;

import java.util.Objects;

public class AuthActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        init();
    }

    private void init(){
        eventGoToRegisterActivityOnclick();
        eventLogInOnClick();
    }

    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser != null){
            goToProfileActivity(currentUser.getEmail());
        }
    }

    private void eventGoToRegisterActivityOnclick(){
        TextView tvRegistrar = findViewById(R.id.tvTodavia);
        tvRegistrar.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), RegisterActivity.class);
            startActivity(intent);
        });
    }

    private void eventLogInOnClick(){
        Button btLogarse = findViewById(R.id.btLogarse);
        btLogarse.setOnClickListener( (v) -> startLogInProcess() );
    }

    private void startLogInProcess(){

        String etEmail = ( (EditText)findViewById(R.id.etEmail) ).getText().toString();
        EditText etPass = findViewById(R.id.etPass);
        boolean sentryEmail = Checkers.checkEmail(etEmail);
        boolean sentryPass = Checkers.checkPass(etPass.getText().toString());

        if ( sentryEmail && sentryPass ){
            mAuth.signInWithEmailAndPassword(etEmail,etPass.getText().toString().trim())
                    .addOnCompleteListener(task -> {
                           if( task.isSuccessful() ){
                               if ( task.getResult().getUser() != null ){
                                   goToWarnActivity(task.getResult().getUser().getEmail());
                               }else{
                                   Pop.showPopMsg(getApplicationContext(),getResources().getString(R.string.proc_error_txt));
                               }
                           }else{
                               Pop.showPopMsg( getApplicationContext(), Objects.requireNonNull(task.getException()).getMessage());

                           }
                    });

        }else{
            DialogBox.dialogErrorCredentials(this);
        }
    }

    private void goToWarnActivity(String eMail){
       Intent i = new Intent(getApplicationContext(), WarnActivity.class);
       i.putExtra("Email",eMail);
       startActivity(i);
    }

    private void goToProfileActivity(String eMail){
        Intent i = new Intent(getApplicationContext(), ProfileActivity.class);
        i.putExtra("Email",eMail);
        startActivity(i);
    }

}//End