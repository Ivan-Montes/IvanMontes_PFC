package com.pfc.ui.profiletabs;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import androidx.fragment.app.Fragment;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.pfc.R;
import com.pfc.activities.AuthActivity;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.support.Checkers;
import com.pfc.ui.popups.DialogBox;
import com.pfc.ui.popups.DialogFragRequestSimpleData;
import com.pfc.ui.popups.Pop;

import java.util.Objects;

public class SesionFragment extends Fragment {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_sesion, container, false);

        init(v);

        return v;
    }

    private void init(View v){
        eventLogOutOnClick(v);
        eventDeleteUserOnClick(v);
        eventChangePasswdOnClick(v);
        manageResultsFromChildFragments();
    }

    private void logOutFromFireBaseByMail(){
        FirebaseAuth.getInstance().signOut();
    }

    private void runAway(){
        startActivity(new Intent(requireActivity().getApplicationContext(), AuthActivity.class));
    }

    private void eventLogOutOnClick(View v){
        Button btLogout = v.findViewById(R.id.btLogout);
        btLogout.setOnClickListener( (z) -> {
            logOutFromFireBaseByMail();
            runAway();
            //overridePendingTransition(0,0);

        });
    }

    private void eventDeleteUserOnClick(View v){
        Button btDelete = v.findViewById(R.id.btDeleteUser);
        btDelete.setOnClickListener( c -> DialogBox.confirmAction(requireContext(),
                bool -> deleteUser()));
    }

    private void deleteUser(){

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fbUser != null){
            Objects.requireNonNull(DbLittleHelperFactory
                            .getDbLittleHelper(DbLittleHelperFactory.FIRE))
                    .deleteUser(fbUser, bool -> {
                        if(bool){
                            Pop.showPopMsg(requireContext(),getResources().getString(R.string.succ));
                        }else{
                            Pop.showPopMsg(requireContext(),getResources().getString(R.string.proc_error_txt));
                        }
                        runAway();
                    });
        }else{
            Pop.showPopMsg(requireContext(),getResources().getString(R.string.log_again));
            runAway();
        }
    }

    private void eventChangePasswdOnClick(View v){
        Button btChPass = v.findViewById(R.id.btChPass);
        btChPass.setOnClickListener( c -> giveMeNewPass() );
    }

    private void giveMeNewPass(){

        Bundle bPack = new Bundle();
        bPack.putString("sender","btChPass");
        bPack.putString("oldValue", "");
        DialogFragRequestSimpleData df = new DialogFragRequestSimpleData();
        df.setArguments(bPack);
        df.show(getChildFragmentManager(), DialogFragRequestSimpleData.TAG);

    }

    private void manageResultsFromChildFragments() {

        getChildFragmentManager()
                .setFragmentResultListener("btChPass", this, (requestKey, bundle) -> {
                    String newValue = bundle.getString("newData");
                    if (Checkers.checkPass(newValue)) {
                        changePassword(newValue);
                    }
                    else{
                        Pop.showPopMsg(requireActivity(),getResources().getString(R.string.requirement_fail));
                    }
                });
    }

    private void changePassword(String newValue){

        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();

        if (fbUser != null){
            Objects.requireNonNull(DbLittleHelperFactory
                            .getDbLittleHelper(DbLittleHelperFactory.FIRE))
                    .changePassword(fbUser, newValue, bool -> {
                        if(bool){
                            Pop.showPopMsg(requireActivity(),getResources().getString(R.string.succ));
                        }else{
                            Pop.showPopMsg(requireActivity(),getResources().getString(R.string.proc_error_txt));
                        }
                    });
        }else{
            Pop.showPopMsg(requireActivity(),getResources().getString(R.string.log_again));
            runAway();
        }
    }

}//End