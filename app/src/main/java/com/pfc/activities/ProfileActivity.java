package com.pfc.activities;

import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.ImageView;

import androidx.activity.result.ActivityResult;
import androidx.activity.result.ActivityResultCallback;
import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewpager2.widget.ViewPager2;

import com.google.android.gms.tasks.Task;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.pfc.R;
import com.pfc.adapters.ProfileTabsAdapter;
import com.pfc.db.DbLittleHelper;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.pojos.User;
import com.pfc.support.FirestoreCallbackUser;
import com.pfc.support.PermissionHelper;
import com.pfc.ui.popups.DialogFragBox;
import com.pfc.ui.popups.Pop;
import com.pfc.ui.profiletabs.ProfileTabFragmentFactory;
import com.pfc.warehouse.Constants;
import com.pfc.warehouse.EnumPermission;

import java.util.UUID;

public class ProfileActivity extends AppCompatActivity {

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        Intent intent = getIntent();
        Bundle b =  intent.getExtras();

        if (b != null){

            email = b.getString("Email","The Void");

        }

        init();

    }

    private void init(){
        confNavBottonInProfileActivity();
        eventPhotoOnClick();
        confTabs();
        //downAvatarFromFirebaseToMem();
        DoIHaveAnAvatar();
    }

    private void confNavBottonInProfileActivity() {
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView = findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.toProfile);
        bottomNavigationView.setOnItemSelectedListener((item) -> {
            if (item.getItemId() == R.id.toWarn) {
                startActivity((new Intent(getApplicationContext(), WarnActivity.class)).putExtra("Email", email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if ( item.getItemId() == R.id.toRequest) {
                startActivity((new Intent(getApplicationContext(), RequestActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if ( item.getItemId() == R.id.toMap) {
                startActivity((new Intent(getApplicationContext(), MapActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
            return true;
        });
    }

    //Manage permission requests
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        switch (requestCode){
            case Constants.COD_REQUEST_CAMERA_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("###","Swith caso 1");
                }
                break;
            case Constants.COD_REQUEST_READ_EXT_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("###","Swith caso 2");
                }
                break;
            case Constants.COD_REQUEST_WRITE_EXT_PERMISSION:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("###","Swith caso 3");
                }
                break;
            case Constants.COD_REQUEST_ARRAY_PERMISSION_AVATAR:
                if (grantResults.length > 0 &&
                        grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Log.e("###","Swith PERMISOS 4RRAY");
                    pickPhoto();
                }  else {
                    DialogFragBox dfb = new DialogFragBox( getString(R.string.perm_denied),
                                                            getString(R.string.perm_denied_txt) );
                    dfb.show(getSupportFragmentManager(), dfb.getTag());
                }
                break;
        }

    }

    /*
    Function with a up to date class adapter and a ViewPager2 element for showing tabs in the profile section
     */
    private void confTabs(){

        ViewPager2 viewPager2 = findViewById(R.id.vp2Pr);
        TabLayout tabLayout = findViewById(R.id.tlP);
        ProfileTabsAdapter profileTabsAdapter = new ProfileTabsAdapter(this);

        // Add the fragments
        profileTabsAdapter.add(ProfileTabFragmentFactory.getFragment(ProfileTabFragmentFactory.PROFILEFRAGMENT,email),getResources().getString(R.string.perfil));
        profileTabsAdapter.add(ProfileTabFragmentFactory.getFragment(ProfileTabFragmentFactory.SETTINGSFRAGMENT,email), getResources().getString(R.string.conf));
        profileTabsAdapter.add(ProfileTabFragmentFactory.getFragment(ProfileTabFragmentFactory.SESIONFRAGMENT,email), getResources().getString(R.string.sesion));

        //Set adapter in viewer
        viewPager2.setAdapter(profileTabsAdapter);

        // Integrating TabLayout with ViewPager2
        TabLayoutMediator mediator = new TabLayoutMediator(tabLayout, viewPager2,
                (tab, position) -> tab.setText(profileTabsAdapter.getPageTitle(position)));
        mediator.attach();
    }

    private void eventPhotoOnClick(){
        ImageView ivAvatar = findViewById(R.id.ivAvatar);
        ivAvatar.setOnClickListener( v -> firstCheckGrantedPermissionForAvatarFeature());

        ImageView ivPickPhoto = findViewById(R.id.ivPickPhoto);
        ivPickPhoto.setOnClickListener( v -> firstCheckGrantedPermissionForAvatarFeature());
    }

    private void firstCheckGrantedPermissionForAvatarFeature(){

        //Check Permission
        if (PermissionHelper.checkArrayPermission(getApplicationContext(), EnumPermission.getArrayPermisoAvatar())){
            pickPhoto();
        } else{
            // Request Permission with Array form Enum  && manage in onRequestPermissionsResult
            PermissionHelper.requestArrayPermission(this,
                                                    EnumPermission.getArrayPermisoAvatar(),
                                                    Constants.COD_REQUEST_ARRAY_PERMISSION_AVATAR);
        }
    }
    //Field for pickPhoto() where the photo is saved
    private Uri imageUri;
    //Launcher and Callback for photo intent
    ActivityResultLauncher<Intent> startCamera = registerForActivityResult(
            new ActivityResultContracts.StartActivityForResult(),
            new ActivityResultCallback<ActivityResult>() {
                @Override
                public void onActivityResult(ActivityResult result) {
                    if (result.getResultCode() == RESULT_OK) {

                        if (imageUri != null){
                            ImageView ivAvatar = findViewById(R.id.ivAvatar);
                            ivAvatar.setImageURI(imageUri);
                            ivAvatar.setAdjustViewBounds(true);
                            uploadAvatarToFirebase();
                            saveAvatarLocalPathInSharedPref(imageUri);
                        }
                    }
                }
            });

    private void saveAvatarLocalPathInSharedPref(@NonNull Uri imageUri){
        SharedPreferences sharedPreferences = getSharedPreferences(email,MODE_PRIVATE);
        SharedPreferences.Editor myEdit = sharedPreferences.edit();
        myEdit.putString("avatar", imageUri.toString());
        myEdit.apply();
    }

    public void pickPhoto() {

        ContentValues values = new ContentValues();
        values.put(MediaStore.Images.Media.TITLE, "New Picture");
        values.put(MediaStore.Images.Media.DESCRIPTION, "From Camera");
        imageUri = getContentResolver().insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values);

        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        cameraIntent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

        if(getApplicationContext().getPackageManager().resolveActivity(cameraIntent,0) != null){
            startCamera.launch(cameraIntent);
        }
    }

    private void uploadAvatarToFirebase(){

        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.FIREBASE_DEFAULTBUCKET);
        StorageReference storageRef = storage.getReference();
        StorageReference refImg  = storageRef.child("images/" + UUID.randomUUID().toString());

        refImg.putFile(imageUri)
                .addOnSuccessListener(taskSnapshot -> {
                    Task<Uri> downloadUrl = refImg.getDownloadUrl();

                    //Asincrono
                    downloadUrl.addOnSuccessListener(uri -> {
                        FirebaseUser fbUser = FirebaseAuth.getInstance().getCurrentUser();
                        assert fbUser != null;
                        DbLittleHelper db = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);
                        if (db != null){
                            db.addAvatarDownloadLink(fbUser, uri);
                        }
                    });
                });
    }

    private void DoIHaveAnAvatar(){
        SharedPreferences sh = getSharedPreferences(email, MODE_PRIVATE);
        String avatarLocalUrl = sh.getString("avatar", "");
        if ( !avatarLocalUrl.isEmpty() ){
            ImageView ivAvatar = findViewById(R.id.ivAvatar);
            ivAvatar.setImageURI(Uri.parse(avatarLocalUrl));
            ivAvatar.setAdjustViewBounds(true);
        }else{
            giveMeUser();
        }
    }

    private void giveMeUser(){
        FirestoreCallbackUser firestoreCallbackUser = this::manageUser;
        DbLittleHelper db = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);
        assert db != null;
        db.getUser(email, firestoreCallbackUser);
    }

    private void manageUser(User usu){
        FirebaseStorage storage = FirebaseStorage.getInstance(Constants.FIREBASE_DEFAULTBUCKET);
        //StorageReference storageRef = storage.getReference();
        if (usu != null && usu.getAvatar() != null && !usu.getAvatar().isEmpty() ){
            String donloadLink = usu.getAvatar();
            StorageReference httpsReference = storage.getReferenceFromUrl(donloadLink);
            httpsReference.getBytes(Constants.ONE_MEGABYTE).addOnSuccessListener(bytes -> {
                if (bytes.length > 0){
                    Bitmap bitmap = BitmapFactory.decodeByteArray(bytes, 0, bytes.length);
                    ImageView ivAvatar = findViewById(R.id.ivAvatar);
                    ivAvatar.setImageBitmap(bitmap);
                    ivAvatar.setAdjustViewBounds(true);
                }
            }).addOnFailureListener(exception -> Pop.showPopMsg(getApplicationContext(),exception.getMessage()));
        }
    }


}//End