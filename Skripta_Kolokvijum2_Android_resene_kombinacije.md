# Skripta za kolokvijum 2 – Android Studio Java API 30

Ovo je skripta za brzo učenje kombinacija zadataka. Rađena je kao šablon koji možeš da prepravljaš na kolokvijumu.

Paket u primerima je:

```
package com.example.kolokvijum;
```

Ako je kod tvog projekta drugačiji, promeni prvu liniju u svakoj Java klasi.

---

# 0. Obavezna podešavanja

## build.gradle dependencies

```
implementation 'androidx.appcompat:appcompat:1.3.1'
implementation 'com.google.android.material:material:1.4.0'
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.google.code.gson:gson:2.8.9'
```

## AndroidManifest.xml osnova

```
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

  <?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.kolokvijum">

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
        android:theme="@style/Theme.AppCompat.Light.NoActionBar"
        android:usesCleartextTraffic="true">

        <provider
            android:name=".MyContentProvider"
            android:authorities="com.example.kolokvijum2.provider"
            android:exported="true" />

        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

    </application>
</manifest>
```

---

# ZADATAK 1 – korisnici + akcelerometar + snimanje zvuka + proximity

## Tekst zadatka

Napraviti projekat Kolokvijum2. U MainActivity staviti dva CheckBox-a, dva Button-a i dva TextView-a jedno ispod drugog. U prvom TextView-u prikazati vrednosti akcelerometra. Klik na prvi Button pokreće snimanje zvuka. Klik na drugi Button zaustavlja snimanje i čuva zvuk u cache direktorijumu aplikacije. Kreirati model za korisnike sa dummy-json Beeceptor sajta i podesiti Retrofit za GET. Klikom na prvi CheckBox dobaviti sve korisnike i sačuvati ih u bazu preko ContentProvider-a. Kada se drugi CheckBox čekira, prikazati email desetog korisnika iz baze u drugi TextView. Kada se odčekira, prikazati očitavanje proximity senzora. Kada se pređe prag, poslati Toast poruku "Daleko".

## Logika koju pamtiš

1. Akcelerometar ide preko SensorManager-a i SensorEventListener-a.
2. Snimanje zvuka ide preko MediaRecorder-a.
3. Fajl ide u getCacheDir().
4. Retrofit dobavlja listu korisnika.
5. Ubacivanje u bazu radi preko getContentResolver().insert(...).
6. Čitanje emaila radi preko getContentResolver().query(...).
7. Proximity senzor ima jednu vrednost: event.values[0].

## activity_main.xml

```
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <CheckBox
            android:id="@+id/cbUsers"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="80dp"
            android:text="Dobavi korisnike" />

        <CheckBox
            android:id="@+id/cbSecond"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Prikaz email/proximity" />

        <Button
            android:id="@+id/btnSnimi"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Snimi" />

        <Button
            android:id="@+id/btnSacuvaj"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Sacuvaj" />

        <TextView
            android:id="@+id/tvFirst"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Akcelerometar"
            android:textSize="18sp" />

        <TextView
            android:id="@+id/tvSecond"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Drugi TextView"
            android:textSize="18sp" />

    </LinearLayout>
</ScrollView>
```

## User.java

```
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;

public class User {
    @SerializedName("id")
    private int id;
    @SerializedName("firstName")
    private String firstName;
    @SerializedName("lastName")
    private String lastName;
    @SerializedName("email")
    private String email;

    public int getId() {
        return id;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }
}
```

## UsersResponse.java

```
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class UsersResponse {
    @SerializedName("users")
    private List<User> users;

    public List<User> getUsers() {
        return users;
    }
}
```

## ApiService.java

```
package com.example.kolokvijum2;

import retrofit2.Call;
import retrofit2.http.GET;

public interface ApiService {
    @GET("users")
    Call<UsersResponse> getUsers();

    @GET("continents")
    Call<ContinentsResponse> getContinents();

    @GET("posts")
    Call<PostsResponse> getPosts();
}
```

## ClientUtils.java

```
package com.example.kolokvijum2;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class ClientUtils {
    public static final String BASE_URL = "https://app.beeceptor.com/mock-server/dummy-json/";

    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build();

    public static ApiService apiService = retrofit.create(ApiService.class);
}
```

## DatabaseHelper.java

```
package com.example.kolokvijum2;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DB_NAME = "kolokvijum2.db";
    public static final int DB_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE users (_id INTEGER PRIMARY KEY AUTOINCREMENT, user_id INTEGER, first_name TEXT, last_name TEXT, email TEXT)");
        db.execSQL("CREATE TABLE continents (_id INTEGER PRIMARY KEY AUTOINCREMENT, continent_id INTEGER, name TEXT, population INTEGER, countries INTEGER)");
        db.execSQL("CREATE TABLE posts (_id INTEGER PRIMARY KEY AUTOINCREMENT, post_id INTEGER, title TEXT, body TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS users");
        db.execSQL("DROP TABLE IF EXISTS continents");
        db.execSQL("DROP TABLE IF EXISTS posts");
        onCreate(db);
    }
}
```

## MyContentProvider.java

```
package com.example.kolokvijum2;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;

public class MyContentProvider extends ContentProvider {
    public static final String AUTHORITY = "com.example.kolokvijum2.provider";
    public static final Uri USERS_URI = Uri.parse("content://" + AUTHORITY + "/users");
    public static final Uri CONTINENTS_URI = Uri.parse("content://" + AUTHORITY + "/continents");
    public static final Uri POSTS_URI = Uri.parse("content://" + AUTHORITY + "/posts");
    private static final int USERS = 1;
    private static final int CONTINENTS = 2;
    private static final int POSTS = 3;
    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    private DatabaseHelper helper;

    static {
        matcher.addURI(AUTHORITY, "users", USERS);
        matcher.addURI(AUTHORITY, "continents", CONTINENTS);
        matcher.addURI(AUTHORITY, "posts", POSTS);
    }

    @Override
    public boolean onCreate() {
        helper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteDatabase db = helper.getReadableDatabase();
        String table = getTable(uri);
        Cursor cursor = db.query(table, projection, selection, selectionArgs, null, null, sortOrder);
        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = getTable(uri);
        long id = db.insert(table, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return ContentUris.withAppendedId(uri, id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = getTable(uri);
        int count = db.delete(table, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        SQLiteDatabase db = helper.getWritableDatabase();
        String table = getTable(uri);
        int count = db.update(table, values, selection, selectionArgs);
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public String getType(Uri uri) {
        return null;
    }

    private String getTable(Uri uri) {
        int match = matcher.match(uri);
        if (match == USERS) return "users";
        if (match == CONTINENTS) return "continents";
        if (match == POSTS) return "posts";
        throw new IllegalArgumentException("Nepoznat URI");
    }
}
```

## MainActivity.java za zadatak 1

```
package com.example.kolokvijum2;

import android.Manifest;
import android.content.ContentValues;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaRecorder;
import android.os.Bundle;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TextView;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener {
    private TextView tvFirst;
    private TextView tvSecond;
    private CheckBox cbUsers;
    private CheckBox cbSecond;
    private Button btnSnimi;
    private Button btnSacuvaj;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor proximity;
    private MediaRecorder recorder;
    private String audioPath;
    private float proximityValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 1);

        tvFirst = findViewById(R.id.tvFirst);
        tvSecond = findViewById(R.id.tvSecond);
        cbUsers = findViewById(R.id.cbUsers);
        cbSecond = findViewById(R.id.cbSecond);
        btnSnimi = findViewById(R.id.btnSnimi);
        btnSacuvaj = findViewById(R.id.btnSacuvaj);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);

        btnSnimi.setOnClickListener(v -> startRecording());
        btnSacuvaj.setOnClickListener(v -> stopRecording());

        cbUsers.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) fetchAndSaveUsers();
        });

        cbSecond.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) showTenthUserEmail();
            else tvSecond.setText("Proximity: " + proximityValue);
        });
    }

    private void startRecording() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.RECORD_AUDIO) != PackageManager.PERMISSION_GRANTED) return;
        File file = new File(getCacheDir(), "zvuk.3gp");
        audioPath = file.getAbsolutePath();
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        recorder.setOutputFile(audioPath);
        try {
            recorder.prepare();
            recorder.start();
            Toast.makeText(this, "Snimanje pokrenuto", Toast.LENGTH_SHORT).show();
        } catch (IOException e) {
            Toast.makeText(this, "Greska", Toast.LENGTH_SHORT).show();
        }
    }

    private void stopRecording() {
        if (recorder != null) {
            recorder.stop();
            recorder.release();
            recorder = null;
            Toast.makeText(this, audioPath, Toast.LENGTH_LONG).show();
        }
    }

    private void fetchAndSaveUsers() {
        ClientUtils.apiService.getUsers().enqueue(new Callback<UsersResponse>() {
            @Override
            public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getUsers() != null) {
                    List<User> users = response.body().getUsers();
                    for (User user : users) {
                        ContentValues values = new ContentValues();
                        values.put("user_id", user.getId());
                        values.put("first_name", user.getFirstName());
                        values.put("last_name", user.getLastName());
                        values.put("email", user.getEmail());
                        getContentResolver().insert(MyContentProvider.USERS_URI, values);
                    }
                    Toast.makeText(MainActivity.this, "Korisnici sacuvani", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<UsersResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Retrofit greska", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showTenthUserEmail() {
        Cursor cursor = getContentResolver().query(MyContentProvider.USERS_URI, null, null, null, "_id ASC");
        if (cursor != null && cursor.moveToPosition(9)) {
            int index = cursor.getColumnIndex("email");
            tvSecond.setText(cursor.getString(index));
            cursor.close();
        } else {
            tvSecond.setText("Nema desetog korisnika");
        }
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
            tvFirst.setText("X: " + x + "\nY: " + y + "\nZ: " + z);
        }
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            proximityValue = event.values[0];
            if (!cbSecond.isChecked()) tvSecond.setText("Proximity: " + proximityValue);
            if (proximityValue > 5) Toast.makeText(this, "Daleko", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }
}
```

---

# ZADATAK 2 – kontinenti + proximity + kamera + lokacija

## Tekst zadatka

U MainActivity postaviti dva CheckBox-a, dva ImageButton-a i dva TextView-a jedno ispod drugog. U prvom TextView-u prikazati vrednost proximity senzora. Postaviti prag i poslati Toast "blizu" ako je vrednost ispod praga. Klikom na ImageButton pokreće se kamera. Potvrdom snimanja slika se čuva u cache direktorijumu aplikacije i putanja slike se prikazuje u Toast poruci. Kreirati model u bazi za kontinente sa dummy-json Beeceptor sajta. Klikom na prvi CheckBox dobaviti sve kontinente i u bazu sačuvati kontinente čija populacija prelazi 1000. Kada se drugi CheckBox čekira, prikazati broj država trećeg kontinenta iz baze u drugi TextView. Kada se odčekira, prikazati očitavanje lokacije.

## Continent.java

```
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;

public class Continent {
    @SerializedName("id")
    private int id;
    @SerializedName("name")
    private String name;
    @SerializedName("population")
    private int population;
    @SerializedName("countries")
    private int countries;

    public int getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public int getPopulation() {
        return population;
    }

    public int getCountries() {
        return countries;
    }
}
```

## ContinentsResponse.java

```
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class ContinentsResponse {
    @SerializedName("continents")
    private List<Continent> continents;

    public List<Continent> getContinents() {
        return continents;
    }
}
```

## activity_main.xml za zadatak 2

```
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <CheckBox
            android:id="@+id/cbContinents"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="80dp"
            android:text="Dobavi kontinente" />

        <CheckBox
            android:id="@+id/cbSecond"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Treći kontinent / lokacija" />

        <ImageButton
            android:id="@+id/btnCamera"
            android:layout_width="match_parent"
            android:layout_height="80dp"
            android:src="@android:drawable/ic_menu_camera" />

        <ImageButton
            android:id="@+id/btnSecond"
            android:layout_width="match_parent"
            android:layout_height="80dp"
            android:src="@android:drawable/ic_menu_gallery" />

        <TextView
            android:id="@+id/tvFirst"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Proximity"
            android:textSize="18sp" />

        <TextView
            android:id="@+id/tvSecond"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Drugi TextView"
            android:textSize="18sp" />

    </LinearLayout>
</ScrollView>
```

## MainActivity.java za zadatak 2

```
package com.example.kolokvijum2;

import android.Manifest;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.CheckBox;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private static final int CAMERA_REQUEST = 100;
    private TextView tvFirst;
    private TextView tvSecond;
    private CheckBox cbContinents;
    private CheckBox cbSecond;
    private ImageButton btnCamera;
    private SensorManager sensorManager;
    private Sensor proximity;
    private LocationManager locationManager;
    private String locationText = "Lokacija nije dobavljena";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION}, 1);

        tvFirst = findViewById(R.id.tvFirst);
        tvSecond = findViewById(R.id.tvSecond);
        cbContinents = findViewById(R.id.cbContinents);
        cbSecond = findViewById(R.id.cbSecond);
        btnCamera = findViewById(R.id.btnCamera);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        proximity = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnCamera.setOnClickListener(v -> openCamera());

        cbContinents.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) fetchAndSaveContinents();
        });

        cbSecond.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) showThirdContinentCountries();
            else tvSecond.setText(locationText);
        });
    }

    private void openCamera() {
        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        startActivityForResult(intent, CAMERA_REQUEST);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            File file = new File(getCacheDir(), "slika.jpg");
            try {
                FileOutputStream outputStream = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, outputStream);
                outputStream.flush();
                outputStream.close();
                Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();
            } catch (IOException e) {
                Toast.makeText(this, "Greska", Toast.LENGTH_SHORT).show();
            }
        }
    }

    private void fetchAndSaveContinents() {
        ClientUtils.apiService.getContinents().enqueue(new Callback<ContinentsResponse>() {
            @Override
            public void onResponse(Call<ContinentsResponse> call, Response<ContinentsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getContinents() != null) {
                    List<Continent> continents = response.body().getContinents();
                    for (Continent continent : continents) {
                        if (continent.getPopulation() > 1000) {
                            ContentValues values = new ContentValues();
                            values.put("continent_id", continent.getId());
                            values.put("name", continent.getName());
                            values.put("population", continent.getPopulation());
                            values.put("countries", continent.getCountries());
                            getContentResolver().insert(MyContentProvider.CONTINENTS_URI, values);
                        }
                    }
                    Toast.makeText(MainActivity.this, "Kontinenti sacuvani", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<ContinentsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Retrofit greska", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showThirdContinentCountries() {
        Cursor cursor = getContentResolver().query(MyContentProvider.CONTINENTS_URI, null, null, null, "_id ASC");
        if (cursor != null && cursor.moveToPosition(2)) {
            int index = cursor.getColumnIndex("countries");
            tvSecond.setText("Broj drzava: " + cursor.getInt(index));
            cursor.close();
        } else {
            tvSecond.setText("Nema treceg kontinenta");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (proximity != null) sensorManager.registerListener(this, proximity, SensorManager.SENSOR_DELAY_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_PROXIMITY) {
            float value = event.values[0];
            tvFirst.setText("Proximity: " + value);
            if (value < 5) Toast.makeText(this, "Blizu", Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
        locationText = "Lat: " + location.getLatitude() + "\nLon: " + location.getLongitude();
        if (!cbSecond.isChecked()) tvSecond.setText(locationText);
    }
}
```

---

# ZADATAK 3 – postovi + lokacija + kamera + žiroskop + akcelerometar + SharedPreferences + kontakti

## Tekst zadatka

TextView, ImageButton, ImageView, Switch i Button jedno ispod drugog. U TextView prikazati lokaciju. Klik na ImageButton pokreće kameru i slika se prikazuje u ImageView. Svaki put kada se slika zameni, u Toast prikazati očitavanje žiroskopa po X, Y i Z osi. Kreirati model u bazi za postove i podesiti Retrofit GET. Kada se Switch prvi put postavi na on, dobaviti i u bazu upisati prvih 10 postova. Svaki sledeći put ispisati title prvog posta u bazi u Toast poruci. Klikom na Button obrisati post na prvoj poziciji. Ako su svi obrisani, poslati notifikaciju "Nema više postova!". Tekst Button-a predstavlja vrednosti akcelerometra u realnom vremenu. Kada se Switch prebaci na off, sadržaj iz TextView-a sačuvati u SharedPreferences u polje tekst i zameniti vrednost TextView-a imenom prvog kontakta.

## Post.java

```
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;

public class Post {
    @SerializedName("id")
    private int id;
    @SerializedName("title")
    private String title;
    @SerializedName("body")
    private String body;

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getBody() {
        return body;
    }
}
```

## PostsResponse.java

```
package com.example.kolokvijum2;

import com.google.gson.annotations.SerializedName;
import java.util.List;

public class PostsResponse {
    @SerializedName("posts")
    private List<Post> posts;

    public List<Post> getPosts() {
        return posts;
    }
}
```

## activity_main.xml za zadatak 3

```
<?xml version="1.0" encoding="utf-8"?>
<ScrollView xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent">

    <LinearLayout
        android:layout_width="match_parent"
        android:layout_height="wrap_content"
        android:orientation="vertical"
        android:padding="16dp">

        <TextView
            android:id="@+id/tvLocation"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:layout_marginTop="80dp"
            android:text="Lokacija"
            android:textSize="18sp" />

        <ImageButton
            android:id="@+id/btnCamera"
            android:layout_width="match_parent"
            android:layout_height="80dp"
            android:src="@android:drawable/ic_menu_camera" />

        <ImageView
            android:id="@+id/imageView"
            android:layout_width="match_parent"
            android:layout_height="200dp"
            android:scaleType="centerCrop" />

        <Switch
            android:id="@+id/swPosts"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Postovi" />

        <Button
            android:id="@+id/btnDelete"
            android:layout_width="match_parent"
            android:layout_height="wrap_content"
            android:text="Akcelerometar" />

    </LinearLayout>
</ScrollView>
```

## MainActivity.java za zadatak 3

```
package com.example.kolokvijum2;

import android.Manifest;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.ContentValues;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
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
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.app.NotificationCompat;
import java.util.List;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MainActivity extends AppCompatActivity implements SensorEventListener, LocationListener {
    private static final int CAMERA_REQUEST = 55;
    private TextView tvLocation;
    private ImageButton btnCamera;
    private ImageView imageView;
    private Switch swPosts;
    private Button btnDelete;
    private SensorManager sensorManager;
    private Sensor accelerometer;
    private Sensor gyroscope;
    private LocationManager locationManager;
    private float gyroX;
    private float gyroY;
    private float gyroZ;
    private boolean insertedOnce = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (getSupportActionBar() != null) getSupportActionBar().hide();

        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.CAMERA, Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.READ_CONTACTS}, 1);

        tvLocation = findViewById(R.id.tvLocation);
        btnCamera = findViewById(R.id.btnCamera);
        imageView = findViewById(R.id.imageView);
        swPosts = findViewById(R.id.swPosts);
        btnDelete = findViewById(R.id.btnDelete);

        sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        gyroscope = sensorManager.getDefaultSensor(Sensor.TYPE_GYROSCOPE);
        locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);

        btnCamera.setOnClickListener(v -> startActivityForResult(new Intent(MediaStore.ACTION_IMAGE_CAPTURE), CAMERA_REQUEST));
        btnDelete.setOnClickListener(v -> deleteFirstPost());

        swPosts.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                if (!insertedOnce && countPosts() == 0) fetchFirstTenPosts();
                else showFirstPostTitle();
            } else {
                SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
                preferences.edit().putString("tekst", tvLocation.getText().toString()).apply();
                tvLocation.setText(getFirstContactName());
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK && data != null) {
            Bitmap bitmap = (Bitmap) data.getExtras().get("data");
            imageView.setImageBitmap(bitmap);
            Toast.makeText(this, "X: " + gyroX + " Y: " + gyroY + " Z: " + gyroZ, Toast.LENGTH_LONG).show();
        }
    }

    private void fetchFirstTenPosts() {
        ClientUtils.apiService.getPosts().enqueue(new Callback<PostsResponse>() {
            @Override
            public void onResponse(Call<PostsResponse> call, Response<PostsResponse> response) {
                if (response.isSuccessful() && response.body() != null && response.body().getPosts() != null) {
                    List<Post> posts = response.body().getPosts();
                    int limit = Math.min(10, posts.size());
                    for (int i = 0; i < limit; i++) {
                        Post post = posts.get(i);
                        ContentValues values = new ContentValues();
                        values.put("post_id", post.getId());
                        values.put("title", post.getTitle());
                        values.put("body", post.getBody());
                        getContentResolver().insert(MyContentProvider.POSTS_URI, values);
                    }
                    insertedOnce = true;
                    Toast.makeText(MainActivity.this, "Prvih 10 postova sacuvano", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<PostsResponse> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Retrofit greska", Toast.LENGTH_SHORT).show();
            }
        });
    }

    private int countPosts() {
        Cursor cursor = getContentResolver().query(MyContentProvider.POSTS_URI, null, null, null, null);
        int count = 0;
        if (cursor != null) {
            count = cursor.getCount();
            cursor.close();
        }
        return count;
    }

    private void showFirstPostTitle() {
        Cursor cursor = getContentResolver().query(MyContentProvider.POSTS_URI, null, null, null, "_id ASC");
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("title");
            Toast.makeText(this, cursor.getString(index), Toast.LENGTH_LONG).show();
            cursor.close();
        }
    }

    private void deleteFirstPost() {
        Cursor cursor = getContentResolver().query(MyContentProvider.POSTS_URI, null, null, null, "_id ASC");
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex("_id");
            int id = cursor.getInt(index);
            cursor.close();
            getContentResolver().delete(MyContentProvider.POSTS_URI, "_id=?", new String[]{String.valueOf(id)});
        }
        if (countPosts() == 0) sendNotification();
    }

    private void sendNotification() {
        NotificationManager manager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        String channelId = "posts_channel";
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel channel = new NotificationChannel(channelId, "Posts", NotificationManager.IMPORTANCE_DEFAULT);
            manager.createNotificationChannel(channel);
        }
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this, channelId)
                .setSmallIcon(android.R.drawable.ic_dialog_info)
                .setContentTitle("Obavestenje")
                .setContentText("Nema više postova!")
                .setPriority(NotificationCompat.PRIORITY_DEFAULT);
        manager.notify(1, builder.build());
    }

    private String getFirstContactName() {
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) return "Nema dozvole";
        Cursor cursor = getContentResolver().query(ContactsContract.Contacts.CONTENT_URI, null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            int index = cursor.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME);
            String name = cursor.getString(index);
            cursor.close();
            return name;
        }
        return "Nema kontakta";
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        if (gyroscope != null) sensorManager.registerListener(this, gyroscope, SensorManager.SENSOR_DELAY_NORMAL);
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED || ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
        locationManager.removeUpdates(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        if (event.sensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            btnDelete.setText("X: " + event.values[0] + " Y: " + event.values[1] + " Z: " + event.values[2]);
        }
        if (event.sensor.getType() == Sensor.TYPE_GYROSCOPE) {
            gyroX = event.values[0];
            gyroY = event.values[1];
            gyroZ = event.values[2];
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {
    }

    @Override
    public void onLocationChanged(Location location) {
        tvLocation.setText("Lat: " + location.getLatitude() + "\nLon: " + location.getLongitude());
    }
}
```

---

# Brzi šabloni koje treba da naučiš

## 1. Senzor

```
sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);
sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
sensorManager.registerListener(this, sensor, SensorManager.SENSOR_DELAY_NORMAL);
```

U onSensorChanged:

```
float x = event.values[0];
float y = event.values[1];
float z = event.values[2];
```

## 2. Proximity

```
float value = event.values[0];
if (value < 5) Toast.makeText(this, "Blizu", Toast.LENGTH_SHORT).show();
if (value > 5) Toast.makeText(this, "Daleko", Toast.LENGTH_SHORT).show();
```

## 3. Retrofit

```
ClientUtils.apiService.getUsers().enqueue(new Callback<UsersResponse>() {
    @Override
    public void onResponse(Call<UsersResponse> call, Response<UsersResponse> response) {
    }

    @Override
    public void onFailure(Call<UsersResponse> call, Throwable t) {
    }
});
```

## 4. Insert preko ContentProvider-a

```
ContentValues values = new ContentValues();
values.put("email", user.getEmail());
getContentResolver().insert(MyContentProvider.USERS_URI, values);
```

## 5. Query preko ContentProvider-a

```
Cursor cursor = getContentResolver().query(MyContentProvider.USERS_URI, null, null, null, "_id ASC");
if (cursor != null && cursor.moveToPosition(9)) {
    String email = cursor.getString(cursor.getColumnIndex("email"));
    cursor.close();
}
```

## 6. Cache fajl

```
File file = new File(getCacheDir(), "naziv.jpg");
String path = file.getAbsolutePath();
```

## 7. SharedPreferences

```
SharedPreferences preferences = getSharedPreferences("prefs", MODE_PRIVATE);
preferences.edit().putString("tekst", tv.getText().toString()).apply();
```

## 8. Lokacija

```
locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
locationManager.requestLocationUpdates(LocationManager.GPS_PROVIDER, 1000, 1, this);
```

U onLocationChanged:

```
tv.setText("Lat: " + location.getLatitude() + " Lon: " + location.getLongitude());
```

---

# Šta najviše da pamtiš za kolokvijum

1. Ako piše TextView prikazuje senzor, to ide u onSensorChanged.
2. Ako piše klik na dugme dobavlja podatke, to ide u setOnClickListener ili setOnCheckedChangeListener.
3. Ako piše čuvanje u bazu preko ContentProvider-a, ne koristi direktno helper.insert, nego getContentResolver().insert.
4. Ako piše deseti korisnik, koristi cursor.moveToPosition(9).
5. Ako piše treći kontinent, koristi cursor.moveToPosition(2).
6. Ako piše prvi post u tabeli, koristi sortOrder "_id ASC" i cursor.moveToFirst().
7. Ako piše cache direktorijum, koristi getCacheDir().
8. Ako piše kamera, koristi Intent MediaStore.ACTION_IMAGE_CAPTURE.
9. Ako piše zvuk, koristi MediaRecorder.
10. Ako piše lokacija, traži permission i koristi LocationManager.
