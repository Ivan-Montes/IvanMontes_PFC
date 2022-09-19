package com.pfc.activities;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.auth.FirebaseAuth;
import com.pfc.R;
import com.pfc.db.DbLittleHelper;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.support.Checkers;
import com.pfc.ui.popups.DialogBox;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {

    private FirebaseAuth mAuth;
    private DbLittleHelper db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // Initialize Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Initialize Firebase Firestore
        db = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);

        //Events
        eventRegisterOnClick();
        eventGoToAuthOnclick();
    }

    private void eventRegisterOnClick(){
        Button btReg = findViewById(R.id.btRegistrar);
        btReg.setOnClickListener( (v) -> startRegisterProcess() );
    }

    private void startRegisterProcess(){

        String eMail = ( (EditText)findViewById(R.id.etEmail) ).getText().toString();
        String pass = ( (EditText)findViewById(R.id.etPass) ).getText().toString();
        String pass2 = ( (EditText)findViewById(R.id.etPass2) ).getText().toString();
        String city = ( (EditText)findViewById(R.id.etCity) ).getText().toString();
        String phone = ( (EditText)findViewById(R.id.etPhone) ).getText().toString();

        boolean sentryEmail = Checkers.checkEmail( eMail );
        boolean sentryPass = Checkers.checkPass( pass );
        boolean sentryPass2 = Checkers.checkPass( pass2 );
        boolean sentryPassEquals = pass2.equals( pass );

        if ( sentryEmail && sentryPass && sentryPass2 & sentryPassEquals){
            mAuth.createUserWithEmailAndPassword( eMail, pass ).addOnCompleteListener(task -> {
                if( task.isSuccessful() ){

                    Map<String, Object> mapNewUser = new HashMap<>();
                    mapNewUser.put("Email", Objects.requireNonNull(task.getResult().getUser()).getEmail());
                    mapNewUser.put("City",city);
                    mapNewUser.put("Phone",phone);
                    mapNewUser.put("Avatar","");

                    if (db != null){
                        db.registerNewUser(mapNewUser);
                    }

                    goToWarnActivity(task.getResult().getUser().getEmail());

                }else{
                    DialogBox.dialogErrorProcess( getApplication().getApplicationContext() );
                    Log.w("createUserWithEmailAndPassword:failure", task.getException());
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


    private void eventGoToAuthOnclick(){
        TextView tvAuth = findViewById(R.id.tvAuth);
        tvAuth.setOnClickListener(view -> {
            Intent intent = new Intent(getApplicationContext(), AuthActivity.class);
            startActivity(intent);
        });
    }

}//End