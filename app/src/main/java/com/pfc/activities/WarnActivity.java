package com.pfc.activities;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.pfc.R;
import com.pfc.adapters.WarnAdapter;
import com.pfc.db.DbLittleHelper;
import com.pfc.db.DbLittleHelperFactory;
import com.pfc.pojos.Warning;

import java.util.List;
import java.util.stream.Collectors;

public class WarnActivity extends AppCompatActivity {

    private String email = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_warn);

        Intent intent = getIntent();
        Bundle b =  intent.getExtras();

        if (b != null){
            email = b.getString("Email","The Void");

        }

       init();

    }

    private void init(){
        confNavBottonInWarnActivity();
        requesWarningtList();
    }

    private void confNavBottonInWarnActivity(){
        // Initialize and assign variable
        BottomNavigationView bottomNavigationView=findViewById(R.id.bottom_navigation);

        // Set Home selected
        bottomNavigationView.setSelectedItemId(R.id.toWarn);
        bottomNavigationView.setOnItemSelectedListener( (item) -> {
            if ( item.getItemId() == R.id.toProfile ) {
                startActivity((new Intent(getApplicationContext(), ProfileActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            } else if ( item.getItemId() == R.id.toRequest ) {
                startActivity((new Intent(getApplicationContext(), RequestActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }else if ( item.getItemId() == R.id.toMap ) {
                startActivity((new Intent(getApplicationContext(), MapActivity.class)).putExtra("Email",email));
                overridePendingTransition(android.R.anim.slide_in_left, android.R.anim.slide_out_right);
            }
            return false;
        });
    }

    private void requesWarningtList(){

        DbLittleHelper dbLittleHelper = DbLittleHelperFactory.getDbLittleHelper(DbLittleHelperFactory.FIRE);
        if (dbLittleHelper != null){
            dbLittleHelper.getCollectionWarn("Warnings", this::preparetWarningList);
        }
    }

    private void preparetWarningList(@NonNull List<QueryDocumentSnapshot> listaWarnDocuments){

        List<Warning> listWarn = listaWarnDocuments
                .stream()
                .map(doc -> {
                    //Unboxing Null-Object to primitive type results in NullPointerException
                    long nivel = 0;
                    Object objectNivel = doc.get("Nivel");
                    if (objectNivel instanceof  Long) nivel = (long) objectNivel;
                    long tipo = 0;
                    Object objectTipo = doc.get("Tipo");
                    if (objectTipo instanceof  Long) {tipo = (long) objectTipo;}

                    return new Warning.WarningBuilder()
                            .setActivo(Boolean.TRUE.equals(doc.getBoolean("Activo")))
                            .setFechaInicio(doc.getTimestamp("FechaInicio"))
                            .setFechaFin(doc.getTimestamp("FechaFin"))
                            .setLugar(doc.getString("Lugar"))
                            .setNivel(nivel)
                            .setTipo(tipo)
                            .setIdDoc(doc.getId())
                            .build();
                }).filter(w->w.isActivo())
                .collect(Collectors.toList());

        showWarningList(listWarn);
    }

    private void showWarningList(List<Warning> listWarn){

        WarnAdapter warnAdapter = new WarnAdapter(listWarn);
        RecyclerView rvWarn = findViewById(R.id.rvWarn);
        rvWarn.setLayoutManager(new LinearLayoutManager(this));
        rvWarn.setAdapter(warnAdapter);

    }


}//End
