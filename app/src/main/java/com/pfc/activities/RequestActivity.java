package com.pfc.activities;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.LocationManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.Button;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.CurrentLocationRequest;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.tasks.CancellationToken;
import com.google.android.gms.tasks.OnTokenCanceledListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.Timestamp;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.GeoPoint;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pfc.R;
import com.pfc.adapters.ReqAdapter;
import com.pfc.db.DbLittleHelper;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.pojos.Request;
import com.pfc.support.Checkers;
import com.pfc.support.PermissionHelper;
import com.pfc.ui.popups.DialogFragBox;
import com.pfc.ui.popups.Pop;
import com.pfc.ui.requestfrag.DetailRequestFragment;
import com.pfc.ui.requestfrag.NewRequestFragment;
import com.pfc.warehouse.Constants;
import com.pfc.warehouse.EnumPermission;

import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.stream.Collectors;

public class RequestActivity extends AppCompatActivity {

    private String email;
    private GeoPoint ubik;
    private static final String TAG = "RequestActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_request);

        Intent intent = getIntent();
        Bundle b = intent.getExtras();

        if (b != null) {
            email = b.getString("Email", "The Void");
        }

        init();
    }

    private void init() {
        confNavBottonInRequestActivity();
        giveMeTheList();
        eventClickAdd();
        manageResultsFromChildFragments();
    }


    private void confNavBottonInRequestActivity() {

        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.toRequest);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.toProfile) {
                startActivity((new Intent(getApplicationContext(), ProfileActivity.class)).putExtra("Email", email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if (item.getItemId() == R.id.toWarn) {
                startActivity((new Intent(getApplicationContext(), WarnActivity.class)).putExtra("Email", email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if (item.getItemId() == R.id.toMap) {
                startActivity((new Intent(getApplicationContext(), MapActivity.class)).putExtra("Email", email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
            return false;
        });
    }

    private void giveMeTheList() {

        DbLittleHelper dbLittleHelper = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);
        if (dbLittleHelper != null) {
            dbLittleHelper.getCollection("Requests", this::prepareMeTheList);
        }
    }

    private void prepareMeTheList(@NonNull List<QueryDocumentSnapshot> listReqDocuments) {

        List<Request> listReq = listReqDocuments
                .stream()
                .map(doc -> {
                    Request rq = new Request();
                    rq.setIdDoc(doc.getId());
                    rq.setTitle(doc.getString("Title"));
                    rq.setStatus(Boolean.TRUE.equals(doc.getBoolean("Status")));
                    rq.setRequestor(doc.getString("Requestor"));
                    rq.setDescription(doc.getString("Description"));
                    rq.setLocation(doc.getGeoPoint("Location"));
                    rq.setCreationDate(doc.getTimestamp("CreationDate"));
                    return rq;
                })
                //.filter(Request::isStatus)
                .sorted(Comparator.comparing(Request::getCreationDate))
                .collect(Collectors.toList());

        showMeTheList(listReq);
    }

    private void showMeTheList(List<Request> listReq) {
        //Consumer as interface listener
        Consumer<Request> consumerReq = r -> new DetailRequestFragment(r).show(getSupportFragmentManager(), TAG);
        ReqAdapter reqAdapter = new ReqAdapter(listReq,consumerReq);
        RecyclerView rvWarn = findViewById(R.id.rvReq);
        rvWarn.setLayoutManager(new LinearLayoutManager(this));
        rvWarn.setAdapter(reqAdapter);
    }

    private void eventClickAdd() {
        Button btAdd = findViewById(R.id.btAddReq);
        btAdd.setOnClickListener(view -> firstCheckGrantedPermissionForNewRequest());
    }

    /*
    Open the FragmentDialog for enter a new request
     */
    private void openStargate() {
        new NewRequestFragment().show(getSupportFragmentManager(), NewRequestFragment.TAG);
    }

    /*
    Find out the LAST user's location. This can be NULL.
    See https://developer.android.com/training/location/retrieve-current#java
     */
    private void findOutLastUbik(){

        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if ( PermissionHelper.checkArrayPermission(getApplicationContext(), EnumPermission.getArrayPermisoGps()) ) {

            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {

                        if (location != null) {
                            ubik = new GeoPoint(location.getLatitude(), location.getLongitude());
                        } else {
                            Pop.showPopMsg(getApplicationContext(), getString(R.string.lastlocation_unknow));
                            checkLocationAvailibility();
                        }

                    })
                    .addOnFailureListener(this,location -> {
                        Pop.showPopMsg(getApplicationContext(), getString(R.string.weird_gps_fail));
                        checkLocationAvailibility();
                    });
        }
    }
    /*
    Find out the CURRENT user's location. It can be NULL.
    https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient#getCurrentLocation(int,%20com.google.android.gms.tasks.CancellationToken)
    */
    private void findOutPrimeUbik(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if ( PermissionHelper.checkArrayPermission(getApplicationContext(), EnumPermission.getArrayPermisoGps()) ) {

            //https://developers.google.com/android/reference/com/google/android/gms/location/CurrentLocationRequest
            CurrentLocationRequest currentLocationRequest = new CurrentLocationRequest.Builder()
                    .setPriority(Priority.PRIORITY_HIGH_ACCURACY)
                    .setMaxUpdateAgeMillis(0)
                    .build();

            CancellationToken cancellationToken = new CancellationToken() {
                @NonNull
                @Override
                public CancellationToken onCanceledRequested(@NonNull OnTokenCanceledListener onTokenCanceledListener) {
                    return this;
                }

                @Override
                public boolean isCancellationRequested() {
                    return false;
                }
            };

            fusedLocationClient.getCurrentLocation(currentLocationRequest,cancellationToken)
                    .addOnFailureListener(location -> Pop.showPopMsg(getApplicationContext(), getString(R.string.weird_gps_fail)))
                    .addOnSuccessListener(location -> {
                        if (location !=null){
                            ubik = new GeoPoint(location.getLatitude(), location.getLongitude());
                        }else{
                            checkLocationAvailibility();
                            //If null, let's try with a different method
                            findOutLastUbik();
                        }
                    });
        }
    }

    private boolean checkGpsStatus(){

        LocationManager locationManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);

    }

    /*
    I'm not sure if it's useful
    https://developers.google.com/android/reference/com/google/android/gms/location/FusedLocationProviderClient#getLocationAvailability()
     */
    private void checkLocationAvailibility(){
        FusedLocationProviderClient fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        if ( PermissionHelper.checkArrayPermission(getApplicationContext(), EnumPermission.getArrayPermisoGps()) ) {

            fusedLocationClient.getLocationAvailability()
                    .addOnFailureListener(command -> Log.e("getLocationAvailability","Failure"))
                    .addOnSuccessListener(command -> Log.e("getLocationAvailability","Success"));

        }
    }

    private void firstCheckGrantedPermissionForNewRequest() {

        if (PermissionHelper.checkArrayPermission(getApplicationContext(), EnumPermission.getArrayPermisoGps())) {

            if (checkGpsStatus() ){
                findOutPrimeUbik();
                openStargate();
            }else{
                Pop.showPopMsg(getApplicationContext(),getString(R.string.active_gps));
            }


        } else {
            PermissionHelper.requestArrayPermission(this,
                    EnumPermission.getArrayPermisoGps(),
                    Constants.COD_REQUEST_ARRAY_PERMISSION_GEO);
        }
    }

    /*
    Manage permission requests
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == Constants.COD_REQUEST_ARRAY_PERMISSION_GEO) {
            if (grantResults.length > 0 &&
                    grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                firstCheckGrantedPermissionForNewRequest();

            } else {
                DialogFragBox dfb = new DialogFragBox(getString(R.string.perm_denied),
                        getString(R.string.perm_gps_den));
                dfb.show(getSupportFragmentManager(), dfb.getTag());
            }
        }

    }

    private void manageResultsFromChildFragments() {

        getSupportFragmentManager()
                .setFragmentResultListener(NewRequestFragment.TAG, this, (requestKey, bundle) -> {
                    String title = bundle.getString("title");
                    String descrip = bundle.getString("descrip");
                    String requestor = FirebaseAuth.getInstance().getUid();

                    boolean checkTitle = Checkers.checkRequestTitle(title);
                    boolean checkDescrip = Checkers.checkRequestDescription(descrip);

                    if (checkTitle && checkDescrip
                            && !Objects.requireNonNull(requestor).isEmpty()
                                && ubik != null) {

                        Map<String, Object> mapNewRequest = new HashMap<>();
                        mapNewRequest.put("Requestor", requestor);
                        mapNewRequest.put("Title", title);
                        mapNewRequest.put("Description", descrip);
                        mapNewRequest.put("Location", ubik);
                        mapNewRequest.put("CreationDate", new Timestamp(new Date()));
                        mapNewRequest.put("Status", true);


                        Objects.requireNonNull(DbLittleHelperFactory
                                        .getDbLittleHelper(DbLittleHelperFactory.FIRE))
                                .addRequest(mapNewRequest, bool -> {

                                    if (bool){
                                        new DialogFragBox(getResources().getString(R.string.info),
                                                getResources().getString(R.string.succ))
                                                .show(getSupportFragmentManager(), DialogFragBox.TAG);

                                       reloadList();

                                    }else{
                                        Pop.showPopMsg(getApplicationContext().getApplicationContext(),
                                                getResources().getString(R.string.proc_error_txt));
                                    }
                                });
                    } else {
                        Pop.showPopMsg(getApplicationContext().getApplicationContext(),
                                getResources().getString(R.string.requirement_fail));
                    }
                });
    }

    private void reloadList() {
        giveMeTheList();
    }



}//End