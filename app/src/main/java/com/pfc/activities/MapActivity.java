package com.pfc.activities;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.content.res.AppCompatResources;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pfc.R;
import com.pfc.db.DbLittleHelper;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.pojos.Request;
import com.pfc.support.Tools;
import com.pfc.warehouse.Constants;

import org.osmdroid.api.IMapController;
import org.osmdroid.bonuspack.location.NominatimPOIProvider;
import org.osmdroid.bonuspack.location.POI;
import org.osmdroid.config.Configuration;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.FolderOverlay;
import org.osmdroid.views.overlay.Marker;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class MapActivity extends AppCompatActivity {

    private String email;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_map);

        Intent intent = getIntent();
        Bundle b =  intent.getExtras();

        if (b != null){
            email = b.getString("Email","The Void");
        }

        init();

    }

    private void init(){
        confNavBottonInMapActivity();
        launchMap();
    }

    private void confNavBottonInMapActivity(){
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        bottomNavigationView.setSelectedItemId(R.id.toMap);
        bottomNavigationView.setOnItemSelectedListener( (item) -> {
            if ( item.getItemId() == R.id.toProfile ) {
                startActivity((new Intent(getApplicationContext(), ProfileActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if ( item.getItemId() == R.id.toRequest ) {
                startActivity((new Intent(getApplicationContext(), RequestActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }else if ( item.getItemId() == R.id.toWarn ) {
                startActivity((new Intent(getApplicationContext(), WarnActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
            return false;
        });
    }

    private void launchMap(){
        Configuration.getInstance().setUserAgentValue("MyOwnUserAgent/1.0");
        //setContentView(R.layout.main);
        MapView map = findViewById(R.id.mvMap);
        map.setMultiTouchControls(true);
        map.setTilesScaledToDpi(true);
        //Here you decide the initial point, it would be possible to get the phone location
        GeoPoint startPoint = new GeoPoint( Constants.GUADA_LAT, Constants.GUADA_LON );
        IMapController mapController = map.getController();
        mapController.setZoom(10d);
        mapController.setCenter(startPoint);

        Marker startMarker = new Marker(map);
        startMarker.setPosition(startPoint);
        startMarker.setAnchor(Marker.ANCHOR_CENTER, Marker.ANCHOR_BOTTOM);
        startMarker.setIcon(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.poi_red));
        startMarker.setTitle("Aquí empezó todo");
        map.getOverlays().add(startMarker);
        map.invalidate();

        DbLittleHelper dbLittleHelper = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);
        if (dbLittleHelper != null) {
            dbLittleHelper.getCollection("Requests", listResult -> addMyPoints(map, prepareMeTheList(listResult)));
        }

        addHospitalPOI(map,startPoint);

    }
//https://github.com/MKergall/osmbonuspack/wiki/Tutorial_2
    private void addMyPoints(MapView map, List<Request> listRequest){

        new Thread(() -> {

            FolderOverlay poiMarkers = new FolderOverlay();
            map.getOverlays().add(poiMarkers);
            Drawable icon = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.poi_green);
            for (Request request:listRequest) {
                Marker poiMarker = new Marker(map);
                poiMarker.setTitle(request.getTitle() + "\n"
                        + getString(R.string.id) + ": " + request.getIdDoc());
                poiMarker.setSnippet(request.getDescription() + "\n"
                        + Tools.fullTimeStampToEasy(request.getCreationDate()) + "\n"
                        + ((request.isStatus()) ? getResources().getString(R.string.active) : getResources().getString(R.string.inactive)));
                poiMarker.setPosition(new GeoPoint(request.getLocation().getLatitude(), request.getLocation().getLongitude()));
                poiMarker.setIcon(icon);
                poiMarker.setImage(AppCompatResources.getDrawable(getApplicationContext(), R.drawable.whatdo1920));

                /* We can substitute the whole bubble layout with our fragment
                poiMarker.setOnMarkerClickListener((marker, mapView) -> {
                    new DetailRequestFragment(request).show(getSupportFragmentManager(), email);
                  return false;
                });*/

                poiMarkers.add(poiMarker);
            }
            map.invalidate();

        }).start();
    }

    private List<Request> prepareMeTheList(@NonNull List<QueryDocumentSnapshot> listResult) {
        return listResult
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
                .collect(Collectors.toList());
    }

    private void addHospitalPOI(MapView map, GeoPoint startPoint){

        Thread th = new Thread(() -> {
            NominatimPOIProvider poiProvider = new NominatimPOIProvider("OSMBonusPackTutoUserAgent");
            ArrayList<POI> pois = poiProvider.getPOICloseTo(startPoint, "hospital", 27, 0.2);
            FolderOverlay poiMarkers = new FolderOverlay();
            map.getOverlays().add(poiMarkers);
            Drawable icon = AppCompatResources.getDrawable(getApplicationContext(),R.drawable.poi_yellow);
            for (POI poi:pois){
                Marker poiMarker = new Marker(map);
                poiMarker.setTitle(poi.mType.toUpperCase());
                poiMarker.setSnippet(poi.mDescription);
                poiMarker.setPosition(poi.mLocation);
                poiMarker.setIcon(icon);
                if (poi.mThumbnail != null){
                    poiMarker.setImage(AppCompatResources.getDrawable(getApplicationContext(),R.drawable.hospital_960_720 ));
                }
                poiMarkers.add(poiMarker);
            }
            map.invalidate();
        });

        th.start();
    }

}//End