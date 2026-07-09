
Beleske za kolokvijum

Uradjena priprema za kolokvijum

Koriscene stvari:

Retrofit, SharedPreferences, SQLite, SensorManager, LocationManager, Camera
Intent, FileProvider.

STEP 1: build.gradle (Groovy DSL)

STEP 2: AndroidManifest.xml

STEP 3: res/xml/file_paths.xml

STEP 4: Continent.java (Model)

STEP 5: ApiService.java (Interface)

STEP 6: DatabaseHelper.java

STEP 7: activity_main.xml

STEP 8: MainActivity.java

Obrati paznju na:

  - Koristi se Theme.AppCompat.Light.NoActionBar u Manifestu.
  - getSupportActionBar().hide() je u onCreate.
  - Checkbox (prvi element) ima marginTop="80dp".
  -  tag je dodat u Manifest zbog kamere na API 30.
  - Svi kodovi su plain-text bez boja i komentara.


Kolokvijum2 (Project Root)
├── app
│   ├── build.gradle (Groovy DSL)
│   ├── src
│   │   ├── main
│   │   │   ├── java
│   │   │   │   └── com
│   │   │   │       └── example
│   │   │   │           └── kolokvijum2
│   │   │   │               ├── ApiService.java
│   │   │   │               ├── Continent.java
│   │   │   │               ├── DatabaseHelper.java
│   │   │   │               └── MainActivity.java
│   │   │   ├── res
│   │   │   │   ├── layout
│   │   │   │   │   └── activity_main.xml
│   │   │   │   ├── xml
│   │   │   │   │   └── file_paths.xml (ovaj folder "xml" moraš sam da kreiraš u "res")
│   │   │   │   └── values
│   │   │   │       ├── strings.xml
│   │   │   │       └── themes.xml (ili styles.xml)
│   │   │   └── AndroidManifest.xml





STEP 1: build.gradle (dependencies)

dependencies {
    implementation 'androidx.appcompat:appcompat:1.3.1'
    implementation 'com.google.android.material:material:1.4.0'
    implementation 'androidx.constraintlayout:constraintlayout:2.1.0'
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.google.android.gms:play-services-location:18.0.0'
}

STEP 2: AndroidManifest.xml

<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.kolokvijum2">

    <uses-permission android:name="android.permission.INTERNET" />
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.CAMERA" />
    <uses-permission android:name="android.permission.WRITE_EXTERNAL_STORAGE" />

    <queries>
        <intent>
            <action android:name="android.media.action.IMAGE_CAPTURE" />
        </intent>
    </queries>

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="Kolokvijum 2"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/Theme.AppCompat.Light.NoActionBar">
        
        <activity android:name=".MainActivity"
            android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

    </application>
</manifest>

STEP 3: res/xml/file_paths.xml

<?xml version="1.0" encoding="utf-8"?>
<paths>
    <cache-path name="my_images" path="." />
</paths>

STEP 4: Continent.java

package com.example.kolokvijum2;

import java.util.List;

public class Continent {
    private String name;
    private int countries;
    private long population;
    private List<String> oceans;

    public Continent(String name, int countries, long population, List<String> oceans) {
        this.name = name;
        this.countries = countries;
        this.population = population;
        this.oceans = oceans;
    }

    public String getName() {
        return name;
    }

    public int getCountries() {
        return countries;
    }

    public long getPopulation() {
        return population;
    }

    public List<String> getOceans() {
        return oceans;
    }
    
    public int getOceansCount() {
        if (oceans == null) return 0;
        return oceans.size();
    }
}

STEP 5: ApiService.java

package com.example.kolokvijum2;

import java.util.List;
import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("continents")
    Call<List<Continent>> getContinents();
}

STEP 6: DatabaseHelper.java

package com.example.kolokvijum2;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {

    public DatabaseHelper(Context context) {
        super(context, "continents.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE continents (id INTEGER PRIMARY KEY AUTOINCREMENT, name TEXT, countries INTEGER, population LONG, oceansCount INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS continents");
        onCreate(db);
    }

    public void insertContinent(String name, int countries, long population, int oceansCount) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", name);
        values.put("countries", countries);
        values.put("population", population);
        values.put("oceansCount", oceansCount);
        db.insert("continents", null, values);
    }

    public Cursor getContinentsByOceans(float proximityValue) {
        SQLiteDatabase db = this.getReadableDatabase();
        return db.rawQuery("SELECT population FROM continents WHERE oceansCount > ?", new String[]{String.valueOf(proximityValue)});
    }
}

STEP 7: activity_main.xml

<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:fillViewport="true">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <CheckBox
            android:id="@+id/cb1"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="80dp"
            android:text="Sacuvaj kontinente (Acc)" />

        <CheckBox
            android:id="@+id/cb2"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Prikazi populaciju (Prox)" />

        <Button
            android:id="@+id/btnSlikaj"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="slikaj" />

        <ImageView
            android:id="@+id/ivSlika"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:scaleType="centerCrop"
            android:layout_marginTop="10dp" />

        <TextView
            android:id="@+id/tvLokacija"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:textSize="16sp" />

        <TextView
            android:id="@+id/tvPopulacija"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="10dp"
            android:textSize="16sp" />

    </LinearLayout>
</ScrollView>

STEP 8: MainActivity.java

package com.example.kolokvijum2;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.FileProvider;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {

    private CheckBox cb1, cb2;
    private Button btnSlikaj;
    private ImageView ivSlika;
    private TextView tvLokacija, tvPopulacija;

    private SensorManager sensorManager;
    private Sensor accelerometer, proximity;
    private float currentAccMagnitude = 0;
    private float currentProxValue = 0;

    private LocationManager locationManager;
    private DatabaseHelper dbHelper;
    private ApiService apiService;

    private File photoFile;
    private static final int REQUEST_IMAGE_CAPTURE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (getSupportActionBar() != null) {
            getSupportActionBar().hide();
        }

        cb1 = findViewById(R.id.cb1);
        cb2 = findViewById(R.id.cb2);
        btnSlikaj = findViewById(R.id.btnSlikaj);
        ivSlika = findViewById(R.id.ivSlika);
        tvLokacija = findViewById(R.id.tvLokacija);
        tvPopulacija = findViewById(R.id.tvPopulacija);

        dbHelper = new DatabaseHelper(this);

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("https://dummy-json.mock.beeceptor.com/")
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        apiService = retrofit.create(ApiService.class);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
        } else {
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, 100);
        }

        btnSlikaj.setOnClickListener(v -> {
            Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                try {
                    photoFile = File.createTempFile("foto_", ".jpg", getCacheDir());
                    Uri photoURI = FileProvider.getUriForFile(this, getPackageName() + ".provider", photoFile);
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, photoURI);
                    startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
                } catch (IOException e) {
                }
            }
        });

        cb1.setOnClickListener(v -> {
            if (cb1.isChecked()) {
                apiService.getContinents().enqueue(new Callback<List<Continent>>() {
                    @Override
                    public void onResponse(Call<List<Continent>> call, Response<List<Continent>> response) {
                        if (response.isSuccessful() && response.body() != null) {
                            for (Continent c : response.body()) {
                                if (c.getCountries() > currentAccMagnitude) {
                                    dbHelper.insertContinent(c.getName(), c.getCountries(), c.getPopulation(), c.getOceansCount());
                                }
                            }
                            Toast.makeText(MainActivity.this, "Podaci sacuvani", Toast.LENGTH_SHORT).show();
                        }
                    }
                    @Override
                    public void onFailure(Call<List<Continent>> call, Throwable t) {
                    }
                });
            }
        });

        cb2.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                Cursor cursor = dbHelper.getContinentsByOceans(currentProxValue);
                long totalPop = 0;
                if (cursor != null && cursor.moveToFirst()) {
                    do {
                        totalPop += cursor.getLong(0);
                    } while (cursor.moveToNext());
                    cursor.close();
                }
                tvPopulacija.setText("Ukupna populacija: " + totalPop);
            } else {
                tvPopulacija.setText("");
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (proximity != null) sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = event.values[0];
            float y = event.values[1];
            float z = event.values[2];
            currentAccMagnitude = (float) Math.sqrt(x * x + y * y + z * z);
        } else if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            currentProxValue = event.values[0];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(@NonNull Location location) {
        tvLokacija.setText("Lat: " + location.getLatitude() + " \nLong: " + location.getLongitude());
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {}

    @Override
    public void onProviderEnabled(@NonNull String provider) {}

    @Override
    public void onProviderDisabled(@NonNull String provider) {}

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            Bitmap bitmap = BitmapFactory.decodeFile(photoFile.getAbsolutePath());
            ivSlika.setImageBitmap(bitmap);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == 100 && grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 5000, 5, this);
            }
        }
    }
}


Samo proveri sledeće pre pokretanja:

Folder xml unutar res (desni klik na res -> New -> Directory -> nazovi ga xml).

U tom folderu napravi file_paths.xml.

Proveri da li se tvoj package name u svim Java klasama podudara sa onim u Manifestu (u mom kodu je com.example.kolokvijum2).

Ne zaboravi da klikneš Sync Project with Gradle Files nakon što dodaš zavisnosti u build.gradle.
