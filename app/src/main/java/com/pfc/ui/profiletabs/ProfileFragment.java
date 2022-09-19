package com.pfc.ui.profiletabs;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentResultListener;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import com.pfc.R;
import com.pfc.db.DbLittleHelper;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.pojos.User;
import com.pfc.support.FirestoreCallbackUser;
import com.pfc.support.Checkers;
import com.pfc.ui.popups.DialogFragBox;
import com.pfc.ui.popups.DialogFragRequestSimpleData;
import com.pfc.ui.popups.Pop;


public class ProfileFragment extends Fragment {

    private final String email;
    private View viewFragment;

    public ProfileFragment(String email){
        this.email = email;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        viewFragment = inflater.inflate(R.layout.fragment_profile, container, false);

        init();

        return viewFragment;
    }

    public void init(){
        fillGaps();
        eventForEditCity();
        eventForEditPhone();
        manageResultsFromChildFragments();
    }

    private void fillGaps(){

        if ( !email.isEmpty() ){

            EditText etEmail = viewFragment.findViewById(R.id.etEmail);
            etEmail.setText(email);

            DbLittleHelper db = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);
            assert db != null;
            db.getUser(email, new FirestoreCallbackUser() {
                @Override
                public void onCallback(User user) {
                    EditText etCity = viewFragment.findViewById(R.id.etCity);
                    etCity.setText(user.getCity() );
                    EditText etPhone = viewFragment.findViewById(R.id.etPhone);
                    etPhone.setText(user.getPhone() );
                }
            });
        }
    }

    private void eventForEditCity(){
        EditText etCity = viewFragment.findViewById(R.id.etCity);
        etCity.setOnClickListener( (c) -> requestNewInputValueForCity());
    }

    private void eventForEditPhone(){
        EditText etCity = viewFragment.findViewById(R.id.etPhone);
        etCity.setOnClickListener( (c) -> requestNewInputValueForPhone());
    }

    private void requestNewInputValueForCity(){

        Bundle bPack = new Bundle();
        bPack.putString("sender","etCity");
        bPack.putString("oldValue", ((EditText)(viewFragment.findViewById(R.id.etCity)))
                                    .getText()
                                    .toString());
        DialogFragRequestSimpleData df = new DialogFragRequestSimpleData();
        df.setArguments(bPack);
        df.show(getChildFragmentManager(), DialogFragRequestSimpleData.TAG);

    }

    private void requestNewInputValueForPhone(){

        Bundle bPack = new Bundle();
        bPack.putString("sender","etPhone");
        bPack.putString("oldValue", ((EditText)(viewFragment.findViewById(R.id.etPhone)))
                                    .getText()
                                    .toString());
        DialogFragRequestSimpleData df = new DialogFragRequestSimpleData();
        df.setArguments(bPack);
        df.show(getChildFragmentManager(), DialogFragRequestSimpleData.TAG);

    }


    private void manageResultsFromChildFragments(){

        getChildFragmentManager()
                .setFragmentResultListener("etCity", this, new FragmentResultListener() {
                    @Override
                    public void onFragmentResult(@NonNull String requestKey, @NonNull Bundle bundle) {
                        String newValue = bundle.getString("newData");
                        String oldValue = bundle.getString("oldValue");
                        boolean checkValue = checkCityForInsertDB(newValue, oldValue);

                        if ( checkValue ){

                            DbLittleHelperFactory
                                    .getDbLittleHelper(DbLittleHelperFactory.FIRE)
                                    .updateCity(email, newValue, bool -> {

                                        if (bool){
                                            new DialogFragBox(getResources().getString(R.string.info),
                                                    getResources().getString(R.string.succ))
                                                    .show(getChildFragmentManager(), DialogFragBox.TAG);

                                            EditText etCity = viewFragment.findViewById(R.id.etCity);
                                            etCity.setText(newValue);
                                        }else{
                                            Pop.showPopMsg(requireActivity().getApplicationContext(),
                                                    getResources().getString(R.string.proc_error_txt));
                                        }
                                    });
                        }
                    }
                });

        getChildFragmentManager()
                .setFragmentResultListener("etPhone", this, (requestKey, bundle) -> {
                    String newValue = bundle.getString("newData");
                    String oldValue = bundle.getString("oldValue");

                    if(Checkers.checkPhone(newValue) && !newValue.equals(oldValue)){

                        DbLittleHelperFactory
                                .getDbLittleHelper(DbLittleHelperFactory.FIRE)
                                .updatePhone(email, newValue, bool -> {

                                    if (bool){
                                        new DialogFragBox(getResources().getString(R.string.info),
                                                getResources().getString(R.string.succ))
                                                .show(getChildFragmentManager(), DialogFragBox.TAG);

                                        EditText etPhone = viewFragment.findViewById(R.id.etPhone);
                                        etPhone.setText(newValue);
                                    }else{
                                        Pop.showPopMsg(requireActivity().getApplicationContext(),
                                                getResources().getString(R.string.proc_error_txt));
                                    }
                                });
                    }else{
                        Pop.showPopMsg(requireActivity().getApplicationContext(),
                                        getResources().getString(R.string.check_phone));
                    }
                });
    }

    private boolean checkCityForInsertDB(String newValue, String oldValue){

        int count = 0;
        boolean result = true;

        if ( !Checkers.checkCity(newValue)) {
            count += 1;
            Pop.showPopMsg(getContext(),getResources().getString(R.string.requirement_fail));
        }
        else if(newValue.equals(oldValue)   ){
            count += 1;
            Pop.showPopMsg(getContext(),getResources().getString(R.string.eq));
        }

        if ( count > 0 ){
            result = false;
        }

        return result;
    }

}//End