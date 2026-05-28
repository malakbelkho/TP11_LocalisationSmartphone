package com.malak.geopulse;


import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.Settings;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class MainActivity extends AppCompatActivity {

    private static final int LOCATION_PERMISSION_CODE = 77;

    private static final String SERVER_URL =
            "http://10.0.2.2/geopulse_api/syncPosition.php";

    private TextView tvSyncState;
    private TextView tvLatitude;
    private TextView tvLongitude;
    private TextView tvAltitude;
    private TextView tvPrecision;
    private TextView tvCaptureDate;
    private TextView tvDeviceCode;
    private TextView tvServerReply;
    private Button btnSendNow;

    private RequestQueue geoRequestQueue;
    private LocationManager geoManager;

    private double currentLatitude = 0.0;
    private double currentLongitude = 0.0;
    private double currentAltitude = 0.0;
    private float currentAccuracy = 0f;
    private String currentDate = "";
    private String deviceSignature = "";

    private boolean hasValidLocation = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        linkViews();
        prepareTools();
        askLocationAccess();
        configureActions();
    }

    private void linkViews() {
        tvSyncState = findViewById(R.id.tvSyncState);
        tvLatitude = findViewById(R.id.tvLatitude);
        tvLongitude = findViewById(R.id.tvLongitude);
        tvAltitude = findViewById(R.id.tvAltitude);
        tvPrecision = findViewById(R.id.tvPrecision);
        tvCaptureDate = findViewById(R.id.tvCaptureDate);
        tvDeviceCode = findViewById(R.id.tvDeviceCode);
        tvServerReply = findViewById(R.id.tvServerReply);
        btnSendNow = findViewById(R.id.btnSendNow);
    }

    private void prepareTools() {
        geoRequestQueue = Volley.newRequestQueue(getApplicationContext());
        geoManager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);

        String androidId = Settings.Secure.getString(
                getContentResolver(),
                Settings.Secure.ANDROID_ID
        );

        deviceSignature = "GeoPulse-" + androidId;
        tvDeviceCode.setText("Appareil : " + deviceSignature);
    }

    private void askLocationAccess() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(
                    this,
                    new String[]{
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                    },
                    LOCATION_PERMISSION_CODE
            );

        } else {
            startGeoTracking();
        }
    }

    private void startGeoTracking() {
        tvSyncState.setText("● GPS activé - en écoute");

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            return;
        }

        geoManager.requestLocationUpdates(
                LocationManager.GPS_PROVIDER,
                60000,
                50,
                new LocationListener() {
                    @Override
                    public void onLocationChanged(Location location) {
                        refreshLocationData(location);
                        sendLocationToServer();
                    }

                    @Override
                    public void onProviderEnabled(String provider) {
                        tvSyncState.setText("● Fournisseur activé : " + provider);
                    }

                    @Override
                    public void onProviderDisabled(String provider) {
                        tvSyncState.setText("● GPS désactivé");
                        Toast.makeText(MainActivity.this,
                                "Active le GPS pour continuer.",
                                Toast.LENGTH_LONG).show();
                    }
                }
        );

        Location lastLocation = geoManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
        if (lastLocation != null) {
            refreshLocationData(lastLocation);
        }
    }

    private void refreshLocationData(Location location) {
        currentLatitude = location.getLatitude();
        currentLongitude = location.getLongitude();
        currentAltitude = location.getAltitude();
        currentAccuracy = location.getAccuracy();
        currentDate = new SimpleDateFormat(
                "yyyy-MM-dd HH:mm:ss",
                Locale.getDefault()
        ).format(new Date());

        hasValidLocation = true;

        tvLatitude.setText("Latitude : " + currentLatitude);
        tvLongitude.setText("Longitude : " + currentLongitude);
        tvAltitude.setText("Altitude : " + currentAltitude + " m");
        tvPrecision.setText("Précision : " + currentAccuracy + " m");
        tvCaptureDate.setText("Date : " + currentDate);
        tvSyncState.setText("● Position détectée");
    }

    private void configureActions() {
        btnSendNow.setOnClickListener(v -> {
            if (!hasValidLocation) {
                Toast.makeText(this,
                        "Aucune position détectée pour le moment.",
                        Toast.LENGTH_SHORT).show();
                return;
            }

            sendLocationToServer();
        });
    }

    private void sendLocationToServer() {
        tvSyncState.setText("● Envoi vers le serveur...");

        StringRequest request = new StringRequest(
                Request.Method.POST,
                SERVER_URL,
                response -> {
                    tvSyncState.setText("● Synchronisation réussie");
                    tvServerReply.setText("Réponse serveur : position enregistrée avec succès.");
                    Toast.makeText(this,
                            "Position envoyée avec succès.",
                            Toast.LENGTH_SHORT).show();
                },
                error -> {
                    tvSyncState.setText("● Erreur réseau");
                    tvServerReply.setText("Erreur : " + error.toString());
                    Toast.makeText(this,
                            "Impossible d'envoyer la position.",
                            Toast.LENGTH_SHORT).show();
                }
        ) {
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> payload = new HashMap<>();

                payload.put("latitude", String.valueOf(currentLatitude));
                payload.put("longitude", String.valueOf(currentLongitude));
                payload.put("date_position", currentDate);
                payload.put("imei", deviceSignature);

                return payload;
            }
        };

        geoRequestQueue.add(request);
    }

    @Override
    public void onRequestPermissionsResult(
            int requestCode,
            String[] permissions,
            int[] grantResults
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (requestCode == LOCATION_PERMISSION_CODE) {
            if (grantResults.length > 0
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                startGeoTracking();
            } else {
                tvSyncState.setText("● Permission refusée");
                Toast.makeText(this,
                        "La permission GPS est nécessaire pour ce TP.",
                        Toast.LENGTH_LONG).show();
            }
        }
    }
}