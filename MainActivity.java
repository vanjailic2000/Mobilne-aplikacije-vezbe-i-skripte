package com.example.kolokvijum2;

import android.Manifest;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import androidx.core.content.ContextCompat;
import androidx.core.content.FileProvider;

import com.example.kolokvijum2.database.PostDao;
import com.example.kolokvijum2.model.Post;
import com.example.kolokvijum2.network.ClientUtils;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // ---- UI ----
    private TextView tvLocation;
    private ImageButton btnCamera;
    private ImageView imgPhoto;
    private Switch switchToggle;
    private Button btnAction;

    // ---- Lokacija ----
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;

    // ---- Senzori ----
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private float lastGyroX, lastGyroY, lastGyroZ;

    // ---- Kamera ----
    private Uri imageUri;
    private ActivityResultLauncher<Uri> cameraLauncher;
    private ActivityResultLauncher<String> cameraPermissionLauncher;

    // ---- Baza i Switch logika ----
    private PostDao postDao;
    private boolean postsAlreadyFetched = false;

    // ---- Permisije (lokacija + kontakti grupisano) ----
    private ActivityResultLauncher<String[]> multiPermissionLauncher;
    private ActivityResultLauncher<String> contactsPermissionLauncher;
    private ActivityResultLauncher<String> notificationPermissionLauncher;

    private static final String CHANNEL_ID = "posts_channel";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        tvLocation = findViewById(R.id.tvLocation);
        btnCamera = findViewById(R.id.btnCamera);
        imgPhoto = findViewById(R.id.imgPhoto);
        switchToggle = findViewById(R.id.switchToggle);
        btnAction = findViewById(R.id.btnAction);

        postDao = new PostDao(this);

        createNotificationChannel();
        setupLocation();
        setupSensors();
        setupCameraLaunchers();
        setupSwitch();
        setupButton();

        btnCamera.setOnClickListener(v -> checkCameraPermissionAndOpen());

        requestNotificationPermissionIfNeeded();
    }

    // =========================================================
    // TAČKA 3: Lokacija u TextView-u
    // =========================================================
    private void setupLocation() {
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        multiPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                permissions -> {
                    Boolean fine = permissions.getOrDefault(Manifest.permission.ACCESS_FINE_LOCATION, false);
                    Boolean coarse = permissions.getOrDefault(Manifest.permission.ACCESS_COARSE_LOCATION, false);
                    if ((fine != null && fine) || (coarse != null && coarse)) {
                        startLocationUpdates();
                    } else {
                        Toast.makeText(this, "Lokacija nije dozvoljena", Toast.LENGTH_SHORT).show();
                    }
                });

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            startLocationUpdates();
        } else {
            multiPermissionLauncher.launch(new String[]{
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION
            });
        }
    }

    private void startLocationUpdates() {
        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, 5000)
                .setMinUpdateIntervalMillis(2000)
                .build();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult result) {
                if (result.getLastLocation() == null) return;
                double lat = result.getLastLocation().getLatitude();
                double lng = result.getLastLocation().getLongitude();
                String text = String.format(Locale.getDefault(),
                        "Lat: %.5f, Lng: %.5f", lat, lng);
                tvLocation.setText(text);
            }
        };

        if (hasPermission(Manifest.permission.ACCESS_FINE_LOCATION)) {
            fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);
        }
    }

    // =========================================================
    // TAČKA 4 i 8: Senzori (žiroskop za Toast, akcelerometar za dugme)
    // =========================================================
    private void setupSensors() {
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
        if (gyroscope != null) {
            sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        if (locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            // samo čuvamo poslednje vrednosti, koristimo ih kad se slika promeni (tačka 4)
            lastGyroX = event.values[0];
            lastGyroY = event.values[1];
            lastGyroZ = event.values[2];
        } else if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            // TAČKA 8: tekst dugmeta = akcelerometar u realnom vremenu
            String text = String.format(Locale.getDefault(),
                    "X:%.2f Y:%.2f Z:%.2f", event.values[0], event.values[1], event.values[2]);
            btnAction.setText(text);
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
        // nije potrebno za ovaj zadatak
    }

    // =========================================================
    // TAČKA 4: Kamera (slikanje) -> ImageView + Toast sa žiroskopom
    // =========================================================
    private void setupCameraLaunchers() {
        cameraLauncher = registerForActivityResult(
                new ActivityResultContracts.TakePicture(),
                success -> {
                    if (success) {
                        imgPhoto.setImageURI(imageUri);
                        showGyroscopeToast();
                    } else {
                        Toast.makeText(this, "Slikanje otkazano", Toast.LENGTH_SHORT).show();
                    }
                });

        cameraPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestPermission(),
                granted -> {
                    if (granted) {
                        openCamera();
                    } else {
                        Toast.makeText(this, "Dozvola za kameru je obavezna", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    private void checkCameraPermissionAndOpen() {
        if (hasPermission(Manifest.permission.CAMERA)) {
            openCamera();
        } else {
            cameraPermissionLauncher.launch(Manifest.permission.CAMERA);
        }
    }

    private void openCamera() {
        File imageFile = createImageFile();
        if (imageFile == null) {
            Toast.makeText(this, "Greška pri kreiranju fajla", Toast.LENGTH_SHORT).show();
            return;
        }
        imageUri = FileProvider.getUriForFile(this,
                getPackageName() + ".provider", imageFile);
        cameraLauncher.launch(imageUri);
    }

    private File createImageFile() {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
        File storageDir = getExternalFilesDir(android.os.Environment.DIRECTORY_PICTURES);
        if (storageDir == null) return null;
        return new File(storageDir, "IMG_" + timeStamp + ".jpg");
    }

    private void showGyroscopeToast() {
        String msg = String.format(Locale.getDefault(),
                "Gyro X:%.2f Y:%.2f Z:%.2f", lastGyroX, lastGyroY, lastGyroZ);
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }

    // =========================================================
    // TAČKA 6 i 9: Switch logika
    // =========================================================
    private void setupSwitch() {
        switchToggle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                // TAČKA 6
                if (!postsAlreadyFetched) {
                    fetchAndSaveFirst10Posts();
                } else {
                    showFirstPostTitleFromDb();
                }
            } else {
                // TAČKA 9
                saveTextAndShowFirstContact();
            }
        });
    }

    private void fetchAndSaveFirst10Posts() {
        ClientUtils.postService.getAll().enqueue(new Callback<ArrayList<Post>>() {
            @Override
            public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    ArrayList<Post> all = response.body();
                    ArrayList<Post> first10 = new ArrayList<>(all.subList(0, Math.min(10, all.size())));
                    postDao.insertAll(first10);
                    postsAlreadyFetched = true;
                    Toast.makeText(MainActivity.this, "Sačuvano " + first10.size() + " postova", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(MainActivity.this, "Greška: " + response.code(), Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ArrayList<Post>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Neuspešan poziv: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showFirstPostTitleFromDb() {
        Post post = postDao.getFirstPost();
        if (post != null) {
            Toast.makeText(this, post.getTitle(), Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(this, "Baza je prazna", Toast.LENGTH_SHORT).show();
        }
    }

    // =========================================================
    // TAČKA 7: Brisanje prvog posta + notifikacija ako je baza prazna
    // =========================================================
    private void setupButton() {
        btnAction.setOnClickListener(v -> {
            postDao.deleteFirstPost();
            if (postDao.getCount() == 0) {
                sendEmptyNotification();
            }
        });
    }

    private void createNotificationChannel() {
        NotificationChannel channel = new NotificationChannel(
                CHANNEL_ID, "Posts notifications", NotificationManager.IMPORTANCE_DEFAULT);
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.createNotificationChannel(channel);
    }

    private void requestNotificationPermissionIfNeeded() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            notificationPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), granted -> {
                        // nije neophodno reagovati, samo zatražiti dozvolu na vreme
                    });
            if (!hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
                notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS);
            }
        }
    }

    private void sendEmptyNotification() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU
                && !hasPermission(Manifest.permission.POST_NOTIFICATIONS)) {
            return; // nema dozvole, ne možemo poslati notifikaciju
        }
        Notification notification = new NotificationCompat.Builder(this, CHANNEL_ID)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Info")
                .setContentText("Nema više postova!")
                .build();
        NotificationManager manager = getSystemService(NotificationManager.class);
        manager.notify(1, notification);
    }

    // =========================================================
    // TAČKA 9 (deo 1): SharedPreferences + prvi kontakt
    // =========================================================
    private void saveTextAndShowFirstContact() {
        SharedPreferences prefs = getSharedPreferences("pref_file", MODE_PRIVATE);
        prefs.edit().putString("tekst", tvLocation.getText().toString()).apply();

        if (hasPermission(Manifest.permission.READ_CONTACTS)) {
            String name = getFirstContactName();
            tvLocation.setText(name != null ? name : "Nema kontakata");
        } else {
            contactsPermissionLauncher = registerForActivityResult(
                    new ActivityResultContracts.RequestPermission(), granted -> {
                        if (granted) {
                            String name = getFirstContactName();
                            tvLocation.setText(name != null ? name : "Nema kontakata");
                        } else {
                            Toast.makeText(this, "Dozvola za kontakte odbijena", Toast.LENGTH_SHORT).show();
                        }
                    });
            contactsPermissionLauncher.launch(Manifest.permission.READ_CONTACTS);
        }
    }

    private String getFirstContactName() {
        String[] projection = new String[]{ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
        Cursor cursor = getContentResolver().query(
                ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);

        String name = null;
        if (cursor != null && cursor.moveToFirst()) {
            name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
            cursor.close();
        }
        return name;
    }

    // =========================================================
    // Pomoćna metoda
    // =========================================================
    private boolean hasPermission(String permission) {
        return ContextCompat.checkSelfPermission(this, permission) == PackageManager.PERMISSION_GRANTED;
    }
}
