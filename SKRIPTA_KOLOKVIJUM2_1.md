# KOMPLETNA SKRIPTA — Kolokvijum 2 (Mobilne aplikacije)
### Senzori • Multimedija • Lokacija i mape • Mobilne komunikacije (REST) • Dobavljači sadržaja

> Cilj ovog dokumenta: da bude **jedini materijal** koji vam treba za učenje. Sadrži teoriju,
> objašnjenje *zašto* se nešto radi na određeni način, kod koji se gotovo uvek koristi na istom
> obrascu, i najčešće zamke. Pročitajte redom — svaka oblast se nadovezuje na prethodnu.

---

# DEO 0 — Pre svega: zajednički obrasci koji se ponavljaju SVUDA

Bez obzira koju temu radite (senzor, kamera, lokacija, kontakti, notifikacije), uvek se vrti oko
ova **3 koraka**. Zapamtite ovo prvo, jer ćete ga viđati u svakom poglavlju:

### 1) Runtime dozvole (permissions)
Od Android 6.0 (API 23) nije dovoljno da dozvolu navedete samo u `AndroidManifest.xml` — za
"osetljive" dozvole (kamera, lokacija, kontakti, mikrofon, notifikacije od API 33) morate i da
pitate korisnika u trenutku rada aplikacije. Obrazac je UVEK isti:

```java
// 1. Definišite launcher (najčešće u onCreate ili kao polje klase)
ActivityResultLauncher<String> permissionLauncher = registerForActivityResult(
    new ActivityResultContracts.RequestPermission(),
    granted -> {
        if (granted) {
            // radi posao (npr. otvori kameru)
        } else {
            Toast.makeText(this, "Dozvola odbijena", Toast.LENGTH_SHORT).show();
        }
    });

// 2. Pre samog korišćenja funkcionalnosti, provera:
if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
        == PackageManager.PERMISSION_GRANTED) {
    openCamera(); // već imamo dozvolu
} else {
    permissionLauncher.launch(Manifest.permission.CAMERA); // tražimo
}
```

Za **više dozvola odjednom** (npr. fine + coarse location) koristi se
`ActivityResultContracts.RequestMultiplePermissions()` i rezultat je `Map<String, Boolean>`.

**Zašto baš `registerForActivityResult`, a ne stari `onRequestPermissionsResult`?** Jer je stari
način deprecated i materijal sa vežbi 6 (poglavlje 4.2.1) explicitno kaže da se ne preporučuje.

### 2) Asinhroni pozivi — UI se ažurira samo unutar callback-a
Mrežni poziv (Retrofit), čitanje lokacije (FusedLocationProviderClient) — sve to traje neko vreme
i ne blokira glavnu nit. Zato **nikad ne pišete kod odmah posle pozivanja metode**, već unutar
callback funkcije koja se izvrši kad odgovor stigne:

```java
call.enqueue(new Callback<...>() {
    @Override
    public void onResponse(...) { /* OVDE ažurirate UI/bazu */ }
    @Override
    public void onFailure(...) { /* OVDE hvatate grešku */ }
});
// kod ODMAH ovde NEMA smisla — odgovor još nije stigao!
```

### 3) Životni ciklus — registruj u onResume, otkači u onPause
Senzori i praćenje lokacije troše bateriju, pa se uvek:
- registruju u `onResume()`
- otkače (`unregisterListener`, `removeLocationUpdates`) u `onPause()`

---

# DEO 1 — SENZORI (Vežbe 9)

### Teorija
Senzor pretvara fizičku veličinu (ubrzanje, svetlost, magnetno polje...) u podatak koji aplikacija
može pročitati. Android grupiše senzore u 3 kategorije:

| Kategorija | Senzori |
|---|---|
| Pozicije | MAGNETIC_FIELD, PROXIMITY |
| Pokreta | ACCELEROMETER, GRAVITY, GYROSCOPE, LINEAR_ACCELERATION, ROTATION_VECTOR |
| Okruženja | AMBIENT_TEMPERATURE, LIGHT, PRESSURE, RELATIVE_HUMIDITY |

**Sensor Framework** (paket `android.hardware`) ima 4 ključne klase:
- `SensorManager` — dobavljanje i registrovanje senzora
- `Sensor` — predstavlja konkretan senzor i njegova svojstva
- `SensorEvent` — objekat sa rezultatom jednog merenja
- `SensorEventListener` — interfejs koji implementirate da primate merenja (2 metode: `onSensorChanged`, `onAccuracyChanged`)

### Standardni kod (pamtite ovaj redosled — uvek je isti)

```java
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    private SensorManager sensorManager;
    private Sensor accelerometer;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        sensorManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (accelerometer != null) {
            sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_GAME);
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);
    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        int type = event.sensor.getType();
        float x = event.values[0];
        float y = event.values.length > 1 ? event.values[1] : 0;
        float z = event.values.length > 2 ? event.values[2] : 0;

        if (type == Sensor.TYPE_ACCELEROMETER) {
            // koristi x, y, z
        } else if (type == Sensor.TYPE_PROXIMITY || type == Sensor.TYPE_LIGHT) {
            // ovi senzori imaju SAMO values[0] — pažljivo!
        }
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }
}
```

**Da listate sve senzore na uređaju:**
```java
List<Sensor> all = sensorManager.getSensorList(Sensor.TYPE_ALL);
```

### Shake detekcija (čest zadatak!)

Logika: pratite promenu (delta) ubrzanja po x/y/z između dva uzastopna merenja, računate
"silu" pomoću Pitagorine teoreme, i ako prelazi prag — to je shake.

```java
private boolean initialized = false;
private float lastX, lastY, lastZ;
private long lastShakeTime = 0;
private static final float SHAKE_THRESHOLD = 12f; // podesivo

// unutar onSensorChanged, za TYPE_ACCELEROMETER:
long currentTime = System.currentTimeMillis();
if (!initialized) {
    lastX = x; lastY = y; lastZ = z;
    initialized = true;
    return;
}
float deltaX = x - lastX, deltaY = y - lastY, deltaZ = z - lastZ;
lastX = x; lastY = y; lastZ = z;

float shakeForce = (float) Math.sqrt(deltaX*deltaX + deltaY*deltaY + deltaZ*deltaZ);
if (shakeForce > SHAKE_THRESHOLD && currentTime - lastShakeTime > 500) {
    lastShakeTime = currentTime;
    // notifikacija / Toast / tekst "Shake detektovan!"
}
```

**Zašto throttling (`currentTime - lastShakeTime > 500`)?** Senzor šalje merenja desetine puta u
sekundi — bez ograničenja, jedan tresak bi se registrovao kao 10 shake-ova.

### Najčešća pitanja/zamke
- `event.values[]` ima različitu dužinu zavisno od senzora (PROXIMITY i LIGHT imaju samo 1 vrednost).
- Vrednosti se NE čitaju "na zahtev" — senzor stalno šalje podatke, vi ih samo presrećete u `onSensorChanged` i čuvate u promenljive koje koristite kasnije (npr. kad se desi neki drugi event, kao klik na dugme).
- `SENSOR_DELAY_GAME` je dovoljno brzo za sve potrebe kolokvijuma (postoje i `_FASTEST`, `_NORMAL`, `_UI`).

---

# DEO 2 — MULTIMEDIJA (Vežbe 9)

## 2.1 Slikanje fotografije (Camera Intent + FileProvider)

### Zašto FileProvider?
Slike treba sačuvati negde gde i aplikacija i Camera-aplikacija mogu da pišu/čitaju. `FileProvider`
generiše `content://` URI umesto `file://` URI, što je bezbednije i obavezno od Android 7+.

### Koraci (uvek isti redosled)
1. Manifest: dozvola `CAMERA` + `<uses-feature>` + deklaracija `<provider>` sa `FileProvider`
2. `res/xml/file_paths.xml` — definiše koji direktorijum je dostupan
3. Generisanje jedinstvenog imena fajla (datum/vreme da se ne preklapaju)
4. `FileProvider.getUriForFile(...)` → dobijate `Uri`
5. Pokretanje kamere preko `ActivityResultContracts.TakePicture()` sa tim URI-jem
6. U callback-u: `imageView.setImageURI(uri)`

```xml
<!-- AndroidManifest.xml -->
<uses-permission android:name="android.permission.CAMERA" />
<uses-feature android:name="android.hardware.camera" android:required="false" />

<provider
    android:name="androidx.core.content.FileProvider"
    android:authorities="${applicationId}.provider"
    android:exported="false"
    android:grantUriPermissions="true">
    <meta-data android:name="android.support.FILE_PROVIDER_PATHS" android:resource="@xml/file_paths" />
</provider>
```

```xml
<!-- res/xml/file_paths.xml -->
<paths>
    <external-files-path name="my_images" path="Pictures" />
</paths>
```

```java
private Uri imageUri;
private ActivityResultLauncher<Uri> cameraLauncher;

cameraLauncher = registerForActivityResult(new ActivityResultContracts.TakePicture(), success -> {
    if (success) imageView.setImageURI(imageUri);
});

private void openCamera() {
    File file = createImageFile();
    imageUri = FileProvider.getUriForFile(this, getPackageName() + ".provider", file);
    cameraLauncher.launch(imageUri);
}

private File createImageFile() {
    String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(new Date());
    File dir = getExternalFilesDir(Environment.DIRECTORY_PICTURES);
    return new File(dir, "IMG_" + timeStamp + ".jpg");
}
```

**Alternativni (stariji, ali i u materijalu vežbi 9) pristup** koristi `registerForActivityResult(new ActivityResultContracts.StartActivityForResult(), ...)` sa ručno kreiranim `Intent(MediaStore.ACTION_IMAGE_CAPTURE)` i `EXTRA_OUTPUT` — funkcionalno identično, samo malo više koda. Oba su prihvatljiva.

## 2.2 Snimanje zvuka — MediaRecorder

**Radi SAMO na fizičkom uređaju, ne na emulatoru!** (eksplicitno navedeno u materijalu)

Koraci za snimanje (pamtite redosled metoda):
```java
mediaRecorder = new MediaRecorder();
mediaRecorder.setAudioSource(MediaRecorder.AudioSource.MIC);
mediaRecorder.setOutputFormat(MediaRecorder.OutputFormat.MPEG_4);
mediaRecorder.setAudioEncoder(MediaRecorder.AudioEncoder.AAC);
mediaRecorder.setOutputFile(outputFilePath);
mediaRecorder.prepare();
mediaRecorder.start();
// ... kasnije:
mediaRecorder.stop();
mediaRecorder.release();
```
Dozvola: `RECORD_AUDIO` (statička u manifestu + runtime).

## 2.3 Reprodukcija zvuka — MediaPlayer

```java
MediaPlayer mediaPlayer = new MediaPlayer();
mediaPlayer.setDataSource(outputFilePath);
mediaPlayer.prepare();
mediaPlayer.start();
mediaPlayer.setOnCompletionListener(mp -> { mp.release(); });
```

**Pravilo pamćenja:** `MediaRecorder` ima `setXxx()` metode pa `prepare→start→stop→release`.
`MediaPlayer` ima `setDataSource()` pa `prepare→start`, i očisti se u `setOnCompletionListener`.

---

# DEO 3 — LOKACIJA I MAPE (Vežbe 8)

### Teorija
GPS sistem određuje poziciju trilateracijom signala sa satelita. Android nudi 3 provajdera:
`GPS_PROVIDER`, `NETWORK_PROVIDER`, `PASSIVE_PROVIDER`.

Dve glavne klase: `LocationManager` (stariji, direktan pristup) i `FusedLocationProviderClient`
(noviji, deo Google Play Services, kombinuje sve izvore i lakši je za korišćenje — preporučeno
rešenje na kolokvijumu ako vam ne traže baš OSMDroid).

### A) Samo prikaz koordinata (najčešći slučaj na kolokvijumu)

```java
private FusedLocationProviderClient fusedLocationClient;
private LocationCallback locationCallback;

fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

LocationRequest locationRequest = new LocationRequest.Builder(
        Priority.PRIORITY_HIGH_ACCURACY, 5000) // interval u ms
        .setMinUpdateIntervalMillis(2000)
        .build();

locationCallback = new LocationCallback() {
    @Override
    public void onLocationResult(LocationResult result) {
        if (result.getLastLocation() == null) return;
        double lat = result.getLastLocation().getLatitude();
        double lng = result.getLastLocation().getLongitude();
        textView.setText("Lat: " + lat + ", Lng: " + lng);
    }
};

// nakon provere dozvole ACCESS_FINE_LOCATION:
fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null);

// u onPause():
fusedLocationClient.removeLocationUpdates(locationCallback);
```

**Zašto ne `getLastLocation()` samostalno?** Vraća `null` ako uređaj nema "poslednju poznatu"
lokaciju (čest slučaj na novom emulatoru) — `requestLocationUpdates` je pouzdaniji za kolokvijum.

### B) Google Maps (ako zadatak traži prikaz mape)

Koraci: API ključ u Manifestu → `SupportMapFragment`/`MapFragment` koji implementira
`OnMapReadyCallback` → u `onMapReady()`:

```java
@Override
public void onMapReady(GoogleMap googleMap) {
    map = googleMap;
    map.setMyLocationEnabled(true); // prikazuje plavu tačku korisnika

    // klik na mapu dodaje marker i zumira na njega
    map.setOnMapClickListener(latLng -> {
        map.addMarker(new MarkerOptions().position(latLng).title("Pin"));
        map.animateCamera(CameraUpdateFactory.newCameraPosition(
            new CameraPosition.Builder().target(latLng).zoom(15).build()));
    });
}
```

### C) OSMDroid (alternativa bez Google API ključa)

```java
Configuration.getInstance().setUserAgentValue(getPackageName());
MapView mapView = findViewById(R.id.map);
mapView.setMultiTouchControls(true);

GeoPoint point = new GeoPoint(lat, lng);
mapView.getController().setZoom(15.0);
mapView.getController().setCenter(point);

Marker marker = new Marker(mapView);
marker.setPosition(point);
mapView.getOverlays().add(marker);
mapView.invalidate(); // OBAVEZNO da se mapa ažurira
```

Crtanje rute (ako traže): geokodiranje adrese preko Nominatim API-ja (vraća lat/lng kao JSON),
zatim spajanje tačaka pomoću `Polyline` objekta dodatog u `mapView.getOverlays()`.

**Zamka:** OSMDroid zahteva da se mrežni pozivi (geokodiranje, ruta) izvršavaju u **pozadinskoj
niti** (`ExecutorService` ili `Thread`), a ažuriranje UI-ja vraća na glavnu nit preko
`runOnUiThread(...)`.

---

# DEO 4 — MOBILNE KOMUNIKACIJE / REST / JSON (Vežbe 7)

### Teorija — JSON
Format ključ-vrednost. Vrednost može biti broj, string, boolean, niz, objekat ili null.
```json
{"firstName":"John", "lastName":"Doe"}
```
Alat za generisanje Java klasa iz JSON-a: **jsonschema2pojo.org** (izaberite Source type=JSON,
Annotation style=Gson).

### Teorija — REST
- Zahtev = `<METOD> <URI>` (GET, POST, PUT, DELETE...)
- Odgovor = sadržaj + statusni kod (2xx uspeh, 3xx redirekcija, 4xx greška klijenta, 5xx greška servera)
- Path parametar: `/Persons/1` (konkretan resurs)
- Query parametar: `/Persons?sort=ASC&filter=mobile`

### Retrofit — kompletan setup (4 fajla, uvek isti redosled)

**1. Dependency (build.gradle):**
```gradle
implementation 'com.squareup.retrofit2:retrofit:2.9.0'
implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
implementation 'com.google.code.gson:gson:2.10.1'
```

**2. Model klasa** (polja moraju odgovarati JSON ključevima):
```java
public class Post {
    private int id;
    private String title;
    private String body;
    // getteri/setteri
}
```

**3. Servisni interfejs** (definiše metode i HTTP glagole):
```java
public interface PostService {
    @GET("posts")
    Call<ArrayList<Post>> getAll();

    @GET("posts/{id}")
    Call<Post> getById(@Path("id") int id);

    @POST("posts")
    Call<Post> add(@Body Post post);

    @PUT("posts/{id}")
    Call<Post> update(@Path("id") int id, @Body Post post);

    @DELETE("posts/{id}")
    Call<Void> delete(@Path("id") int id);

    @GET("posts")
    Call<ArrayList<Post>> getFiltered(@Query("sort") String sort);
}
```

**4. Retrofit instanca (singleton, najčešće u `ClientUtils` klasi):**
```java
public class ClientUtils {
    public static final String SERVICE_API_PATH = "https://app.beeceptor.com/mock-server/dummy-json/";
    public static Retrofit retrofit = new Retrofit.Builder()
            .baseUrl(SERVICE_API_PATH)
            .addConverterFactory(GsonConverterFactory.create())
            .build();
    public static PostService postService = retrofit.create(PostService.class);
}
```

**Pozivanje:**
```java
ClientUtils.postService.getAll().enqueue(new Callback<ArrayList<Post>>() {
    @Override
    public void onResponse(Call<ArrayList<Post>> call, Response<ArrayList<Post>> response) {
        if (response.isSuccessful() && response.body() != null) {
            ArrayList<Post> posts = response.body();
            // ažuriranje UI-ja / baze OVDE
        }
    }
    @Override
    public void onFailure(Call<ArrayList<Post>> call, Throwable t) {
        Log.e("REST", t.getMessage());
    }
});
```

**Zamka #1:** `baseUrl` se MORA završavati sa `/`, putanja u `@GET("...")` NE SME počinjati sa `/`.
**Zamka #2:** Dozvola `INTERNET` u manifestu (statička, nije runtime).
**Zamka #3:** Telo POST/PUT zahteva (`@Body`) mora biti objekat istog tipa kao model.

---

# DEO 5 — SHAREDPREFERENCES (Vežbe 6)

### Teorija
Skladište prostih tipova podataka u formi (ključ, vrednost), perzistentno (ostaje i nakon
zatvaranja aplikacije). Dobro za podešavanja, manje konfiguracije, ne za velike strukturirane podatke (za to SQLite).

### Direktno korišćenje (najčešći slučaj na kolokvijumu)
```java
// Pisanje
SharedPreferences prefs = getSharedPreferences("pref_file", MODE_PRIVATE);
prefs.edit().putString("tekst", "vrednost").apply(); // ili .commit()

// Čitanje
String value = prefs.getString("tekst", "default_vrednost"); // 2. parametar = default ako ne postoji
boolean flag = prefs.getBoolean("pref_sync", false);
prefs.contains("tekst"); // provera da li postoji
```
`apply()` je asinhrono (preporučeno), `commit()` je sinhrono i vraća `boolean` uspeha.

### Preko `PreferenceFragmentCompat` (ako traže pravi ekran podešavanja)
- `SettingsFragment extends PreferenceFragmentCompat`, override `onCreatePreferences()`
- `preferences.xml` u `res/xml/` sa `<PreferenceScreen>`, `<PreferenceCategory>`,
  `<CheckBoxPreference>`, `<ListPreference>`
- `ListPreference` ima `entries` (šta se prikazuje) i `entryValues` (šta se upisuje) — moraju biti
  isto duge liste, definisane u `arrays.xml`
- `dependency="pref_sync"` na listi — onemogućava listu dok checkbox nije čekiran

---

# DEO 6 — SQLite (Vežbe 6)

### Teorija
Ugrađena relaciona baza, izvršava se u istom procesu kao aplikacija. `SQLiteOpenHelper` upravlja
kreiranjem/verzionisanjem baze, `SQLiteDatabase` izvršava CRUD, `Cursor` je "pokazivač" na red
rezultata upita.

### SQLiteHelper (kreiranje/upgrade baze)
```java
public class SQLiteHelper extends SQLiteOpenHelper {
    public static final String TABLE = "POSTS";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_TITLE = "title";

    private static final String DB_NAME = "posts.db";
    private static final int DB_VERSION = 1;
    private static final String DB_CREATE = "create table " + TABLE + "("
            + COLUMN_ID + " integer primary key autoincrement, "
            + COLUMN_TITLE + " text)";

    public SQLiteHelper(Context context) { super(context, DB_NAME, null, DB_VERSION); }

    @Override public void onCreate(SQLiteDatabase db) { db.execSQL(DB_CREATE); }

    @Override public void onUpgrade(SQLiteDatabase db, int oldV, int newV) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE);
        onCreate(db);
    }
}
```

### CRUD operacije
```java
SQLiteDatabase db = helper.getWritableDatabase();

// CREATE (insert)
ContentValues values = new ContentValues();
values.put(SQLiteHelper.COLUMN_TITLE, "Naslov");
long id = db.insert(SQLiteHelper.TABLE, null, values);

// READ (query) — vraća Cursor
Cursor cursor = db.query(SQLiteHelper.TABLE, null, null, null, null, null,
        SQLiteHelper.COLUMN_ID + " ASC", "1"); // sortiranje + limit
if (cursor.moveToFirst()) {
    String title = cursor.getString(cursor.getColumnIndexOrThrow(SQLiteHelper.COLUMN_TITLE));
}
cursor.close();

// UPDATE
ContentValues newValues = new ContentValues();
newValues.put(SQLiteHelper.COLUMN_TITLE, "Novi naslov");
db.update(SQLiteHelper.TABLE, newValues, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

// DELETE
db.delete(SQLiteHelper.TABLE, COLUMN_ID + "=?", new String[]{String.valueOf(id)});

// ili "obriši prvi po redosledu":
db.execSQL("DELETE FROM " + TABLE + " WHERE " + COLUMN_ID
        + " = (SELECT MIN(" + COLUMN_ID + ") FROM " + TABLE + ")");

db.close();
```

### Kursor — navigacija
```java
cursor.moveToFirst(); cursor.moveToNext(); cursor.moveToLast(); cursor.moveToPrevious();
cursor.getCount(); cursor.getColumnIndex("naziv_kolone");
cursor.getString(i); cursor.getInt(i); cursor.getLong(i); cursor.getFloat(i);
```

**KLJUČNA ZAMKA (čest deo zadatka):** "prvi red u tabeli" znači najmanji `_id`
(autoincrement, redosled umetanja), **NE** vrednost ID-a koja dolazi sa servera (ta se može
zvati `post_id` da se ne pobrka sa `_id`)! Ako kasnije brišete redove, server ID-jevi mogu
"nedostajati", ali `_id` poredak ostaje logičan.

---

# DEO 7 — DOBAVLJAČI SADRŽAJA / ContentProvider (Vežbe 6)

### Teorija
`ContentProvider` upravlja podacima i omogućava drugim aplikacijama da im pristupe na
standardizovan način (preko URI-ja), bez direktnog pristupa bazi. Postoje **sistemski** (Contacts,
Calendar, CallLog...) i **aplikacioni** (sami ih pravite).

URI struktura: `content://<authority>/<path>` (npr. `content://com.example.app/products`).

### A) Pristup SISTEMSKOM provideru — kontakti (najčešći slučaj na kolokvijumu)

```java
// Dozvola: READ_CONTACTS (statička + runtime)
private String getFirstContactName() {
    String[] projection = {ContactsContract.Contacts._ID, ContactsContract.Contacts.DISPLAY_NAME};
    Cursor cursor = getContentResolver().query(
            ContactsContract.Contacts.CONTENT_URI, projection, null, null, null);
    String name = null;
    if (cursor != null && cursor.moveToFirst()) {
        name = cursor.getString(cursor.getColumnIndexOrThrow(ContactsContract.Contacts.DISPLAY_NAME));
        cursor.close();
    }
    return name;
}
```
Za broj telefona umesto imena: `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` +
kolona `Phone.NUMBER`.

### B) Pravljenje SVOG ContentProvider-a (ređe, ali može doći)

```java
public class DBContentProvider extends ContentProvider {
    private static final int PRODUCTS = 10;
    private static final int PRODUCT_ID = 20;
    private static final String AUTHORITY = "com.example.app";
    private static final String PATH = "products";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + PATH);

    private static final UriMatcher matcher = new UriMatcher(UriMatcher.NO_MATCH);
    static {
        matcher.addURI(AUTHORITY, PATH, PRODUCTS);
        matcher.addURI(AUTHORITY, PATH + "/#", PRODUCT_ID);
    }

    @Override public boolean onCreate() { /* inicijalizacija helper-a */ return true; }

    @Override public Uri insert(Uri uri, ContentValues values) {
        long id = db.insert(TABLE, null, values);
        getContext().getContentResolver().notifyChange(uri, null);
        return Uri.parse(PATH + "/" + id);
    }

    @Override public Cursor query(Uri uri, String[] proj, String sel, String[] selArgs, String sort) {
        // switch po matcher.match(uri), vraća Cursor
    }
    // update(), delete(), getType() analogno
}
```
Deklaracija u manifestu:
```xml
<provider android:name=".DBContentProvider" android:authorities="com.example.app" android:exported="false" />
```
Korišćenje iz aktivnosti: `getContentResolver().insert(DBContentProvider.CONTENT_URI, values)`
— ovo automatski poziva `insert()` metodu vašeg providera.

---

# DEO 8 — NOTIFIKACIJE (dolazi iz kombinacije tema, nije direktno u jednoj vežbi)

```java
// 1. Kanal (obavezno od Android 8+)
NotificationChannel channel = new NotificationChannel("channel_id", "Naziv",
        NotificationManager.IMPORTANCE_DEFAULT);
getSystemService(NotificationManager.class).createNotificationChannel(channel);

// 2. Dozvola POST_NOTIFICATIONS (runtime, samo od Android 13/API 33)
if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
    // standardni permission launcher obrazac
}

// 3. Slanje
Notification notification = new NotificationCompat.Builder(this, "channel_id")
        .setSmallIcon(android.R.drawable.ic_dialog_info)
        .setContentTitle("Naslov")
        .setContentText("Tekst poruke")
        .build();
getSystemService(NotificationManager.class).notify(1, notification);
```

---

# DEO 9 — FIREBASE (ukratko, manja šansa da bude glavna tema, ali može biti deo zadatka)

- **Realtime Database** — JSON stablo, realtime sinhronizacija, offline podrška.
- **Cloud Firestore** — dokumenti/kolekcije (NoSQL), realtime upiti, indeksiranje.
- Firestore CRUD obrazac (sve asinhrono, sa listener-ima):
```java
FirebaseFirestore db = FirebaseFirestore.getInstance();

// insert
db.collection("users").add(userMap)
    .addOnSuccessListener(ref -> {...})
    .addOnFailureListener(e -> {...});

// read
db.collection("users").get().addOnCompleteListener(task -> {
    if (task.isSuccessful()) {
        for (QueryDocumentSnapshot doc : task.getResult()) { ... }
    }
});

// update
db.collection("users").document(docId).update("firstName", "Novo")
    .addOnSuccessListener(...).addOnFailureListener(...);

// delete
db.collection("users").document(docId).delete()
    .addOnSuccessListener(...).addOnFailureListener(...);
```

---

# DEO 10 — Kompletan primer (sve spojeno) — "Priprema za kolokvijum 2"

Ovo je referentni zadatak koji kombinuje SVE gore navedeno u jednoj aplikaciji
(TextView + ImageButton + ImageView + Switch + Button):

1. **TextView** — prikazuje GPS lokaciju (Deo 3A)
2. **ImageButton** — otvara kameru (Deo 2.1); nakon slikanja prikazuje Toast sa očitavanjem žiroskopa (Deo 1)
3. **Retrofit model + servis** za postove (Deo 4)
4. **Switch ON** — prvi put: GET poziv + upis 10 postova u SQLite (Deo 4 + Deo 6); svaki sledeći put: čitanje prvog reda iz baze i prikaz u Toast-u
5. **Button** — briše prvi red iz baze (Deo 6); ako je baza prazna → notifikacija (Deo 8)
6. **Button tekst** — akcelerometar u realnom vremenu (Deo 1)
7. **Switch OFF** — čuva tekst u SharedPreferences (Deo 5) i zamenjuje TextView imenom prvog kontakta (Deo 7A)

Kompletan radni kod ovog primera sa svim klasama (MainActivity, Post, PostService, ClientUtils,
SQLiteHelper, PostDao, layout, manifest) je već generisan u prethodnom odgovoru
(`Kolokvijum2_projekat.zip`) — koristite ga kao referencu kako se ovi delovi povezuju u jednu
celinu.

---

# DEO 11 — TAČAN RASPORED FAJLOVA: gde se šta piše, fajl po fajl

Ovo poglavlje je **mapa projekta** — pokazuje kompletnu strukturu foldera Android Studio
projekta i, za svaku funkcionalnost iz prethodnih poglavlja, **tačno koji fajl se kreira/menja
i na kom mestu unutar njega** ide koja komanda. Ako ste se do sada pitali "dobro, a gde tačno
da nalepim ovaj kod?" — ovde je odgovor.

## 11.1 Kompletno stablo foldera (standardni Android Studio projekat)

```
NazivProjekta/
├── app/
│   ├── build.gradle                          ← (A) dependencies idu OVDE
│   └── src/
│       └── main/
│           ├── AndroidManifest.xml           ← (B) dozvole, provider, activity
│           ├── java/com/example/app/
│           │   ├── MainActivity.java         ← (C) sva Java logika aktivnosti
│           │   ├── model/
│           │   │   └── Post.java             ← (D) model klasa za REST/JSON
│           │   ├── network/
│           │   │   ├── PostService.java      ← (E) Retrofit interfejs
│           │   │   └── ClientUtils.java       ← (F) Retrofit instanca
│           │   └── database/
│           │       ├── SQLiteHelper.java      ← (G) definicija tabele
│           │       └── PostDao.java           ← (H) CRUD metode
│           └── res/
│               ├── layout/
│               │   └── activity_main.xml     ← (I) raspored UI elemenata
│               ├── xml/
│               │   └── file_paths.xml        ← (J) putanje za FileProvider (kamera)
│               └── values/
│                   ├── arrays.xml            ← (K) liste za ListPreference (ako treba)
│                   └── strings.xml
```

**Pravilo orijentacije:** sve što je *Java logika* ide u `app/src/main/java/...`, sve što je
*UI/izgled/konfiguracija* ide u `app/src/main/res/...`, a *dozvole i deklaracije komponenti*
idu u `AndroidManifest.xml` koji se nalazi direktno u `app/src/main/`.

---

## 11.2 (A) `app/build.gradle` — gde idu biblioteke

Otvorite **build.gradle modula `app`** (NE onaj na nivou projekta — u Android Studio levom
panelu, `Gradle Scripts → build.gradle (Module :app)`). Unutar njega postoji blok
`dependencies { ... }` — sve nove biblioteke se dodaju **unutar tih zagrada**, svaka u svom
redu:

```gradle
android { ... }   // ovaj blok ne dirate radi dependency-ja

dependencies {
    implementation 'androidx.appcompat:appcompat:1.6.1'   // ovo već postoji

    // OVDE DODAJETE NOVE LINIJE, npr.:
    implementation 'com.google.android.gms:play-services-location:21.2.0'   // za lokaciju (Deo 3)
    implementation 'com.squareup.retrofit2:retrofit:2.9.0'                  // za REST (Deo 4)
    implementation 'com.squareup.retrofit2:converter-gson:2.9.0'
    implementation 'com.google.code.gson:gson:2.10.1'
}
```

Nakon izmene, Android Studio prikazuje traku **"Sync Now"** na vrhu — OBAVEZNO kliknite, inače
biblioteka nije zaista preuzeta i kod se ne kompajlira.

---

## 11.3 (B) `AndroidManifest.xml` — gde ide šta unutra

Ovaj fajl ima tačno 2 glavna dela: `<manifest>` korenski element, a unutar njega
`<uses-permission>` elementi (IZNAD `<application>` taga) i `<application>` blok
(sve ostalo je unutar njega).

```xml
<?xml version="1.0" encoding="utf-8"?>
<manifest xmlns:android="http://schemas.android.com/apk/res/android">

    <!-- 1. SVE <uses-permission> linije idu OVDE, IZNAD <application> -->
    <uses-permission android:name="android.permission.INTERNET" />                 <!-- Deo 4: REST -->
    <uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />      <!-- Deo 3: lokacija -->
    <uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />
    <uses-permission android:name="android.permission.CAMERA" />                    <!-- Deo 2.1: kamera -->
    <uses-permission android:name="android.permission.RECORD_AUDIO" />              <!-- Deo 2.2: mikrofon -->
    <uses-permission android:name="android.permission.READ_CONTACTS" />             <!-- Deo 7: kontakti -->
    <uses-permission android:name="android.permission.POST_NOTIFICATIONS" />        <!-- Deo 8: notifikacije -->

    <!-- 2. <uses-feature> takođe IZNAD <application>, ispod permission-a -->
    <uses-feature android:name="android.hardware.camera" android:required="false" />

    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="MojaApp"
        android:theme="@style/Theme.AppCompat.Light.DarkActionBar">

        <!-- 3. Svaka aktivnost ide OVDE, unutar <application> -->
        <activity android:name=".MainActivity" android:exported="true">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>

        <!-- 4. FileProvider (Deo 2.1, kamera) ide OVDE, unutar <application>, kao "sibling" activity-a -->
        <provider
            android:name="androidx.core.content.FileProvider"
            android:authorities="${applicationId}.provider"
            android:exported="false"
            android:grantUriPermissions="true">
            <meta-data
                android:name="android.support.FILE_PROVIDER_PATHS"
                android:resource="@xml/file_paths" />
        </provider>

        <!-- 5. SOPSTVENI ContentProvider (Deo 7B), ako ga pravite, ide OVDE isto -->
        <provider
            android:name=".database.DBContentProvider"
            android:authorities="com.example.app"
            android:exported="false" />

        <!-- 6. Google Maps API ključ (Deo 3B), ako koristite Google Maps, ide OVDE -->
        <meta-data android:name="com.google.android.geo.API_KEY" android:value="VAS_KLJUC" />

    </application>
</manifest>
```

**Pravilo pamćenja:** "permission i feature su NAJAVA šta aplikacija traži (idu pre
`<application>`), provider i activity su KOMPONENTE koje aplikacija STVARNO ima (idu unutar
`<application>`)".

---

## 11.4 (J) `res/xml/file_paths.xml` — novi fajl, samo za kameru

U Android Studio: desni klik na `res` → `New → Directory` → upišite `xml` (ako folder `xml` ne
postoji) → zatim desni klik na `res/xml` → `New → XML Resource File` → ime `file_paths`.

```xml
<?xml version="1.0" encoding="utf-8"?>
<paths>
    <external-files-path name="my_images" path="Pictures" />
</paths>
```
Ovaj fajl se **referenciše** iz `AndroidManifest.xml` (tačka 4 iznad, `android:resource="@xml/file_paths"`) — to je jedina veza, sam fajl se nigde u Java kodu ne pominje direktno.

---

## 11.5 (I) `res/layout/activity_main.xml` — raspored elemenata

Otvara se automatski kad kreirate `EmptyActivity`, ili: desni klik na `res/layout` → `New → Layout Resource File`. Svi vidžeti idu **unutar** korenskog layout-a (`LinearLayout`/`ConstraintLayout`), jedan za drugim, redom kojim ih hoćete na ekranu:

```xml
<LinearLayout xmlns:android="http://schemas.android.com/apk/res/android"
    android:layout_width="match_parent"
    android:layout_height="match_parent"
    android:orientation="vertical">

    <TextView android:id="@+id/tvLocation" .../>       <!-- redosled = pozicija na ekranu -->
    <ImageButton android:id="@+id/btnCamera" .../>
    <ImageView android:id="@+id/imgPhoto" .../>
    <Switch android:id="@+id/switchToggle" .../>
    <Button android:id="@+id/btnAction" .../>
</LinearLayout>
```
**Veza sa Java kodom:** `android:id="@+id/tvLocation"` se u `MainActivity.java` povezuje preko
`findViewById(R.id.tvLocation)` — `R.id.X` se AUTOMATSKI generiše iz `android:id="@+id/X"`,
ne pišete ga ručno nigde.

---

## 11.6 (C) `MainActivity.java` — gde unutar klase ide svaki deo koda

Unutar jedne `MainActivity` klase, postoji jasan redosled koji Android očekuje
(ne morate ga slediti strogo, ali se profesori i asistenti uvek drže ovog obrasca):

```java
public class MainActivity extends AppCompatActivity implements SensorEventListener {

    // === 1. POLJA KLASE (sve promenljive koje treba da "žive" duže od jedne metode) ===
    private TextView tvLocation;                    // UI referentne promenljive (Deo 11.5)
    private SensorManager sensorManager;             // Deo 1
    private FusedLocationProviderClient fusedLocationClient;  // Deo 3
    private ActivityResultLauncher<Uri> cameraLauncher;       // Deo 2.1
    private PostDao postDao;                          // Deo 6
    private boolean postsAlreadyFetched = false;       // "zastavica" za Switch logiku (Deo 10, tačka 4)

    // === 2. onCreate() — SVA inicijalizacija ide ovde, redom ===
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);          // MORA biti prva linija nakon super()

        tvLocation = findViewById(R.id.tvLocation);       // zatim findViewById za SVE vidžete
        // ... ostali findViewById

        postDao = new PostDao(this);                      // inicijalizacija pomoćnih klasa (Deo 6)

        setupLocation();      // pozivi pomoćnih metoda (Deo 3) - definišu se niže u klasi
        setupSensors();       // (Deo 1)
        setupCameraLaunchers();  // (Deo 2.1)
        setupSwitch();         // (Deo 10)
        setupButton();          // (Deo 10)
    }

    // === 3. onResume() / onPause() — lifecycle metode, UVEK zajedno, ispod onCreate ===
    @Override
    protected void onResume() {
        super.onResume();
        sensorManager.registerListener(...);   // Deo 1 - senzori se PALE ovde
    }

    @Override
    protected void onPause() {
        super.onPause();
        sensorManager.unregisterListener(this);  // Deo 1 - senzori se GASE ovde
        fusedLocationClient.removeLocationUpdates(locationCallback);  // Deo 3
    }

    // === 4. Implementacije interfejsa (SensorEventListener metode) ===
    @Override
    public void onSensorChanged(SensorEvent event) { ... }   // Deo 1
    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) { }

    // === 5. Privatne "setup" i "helper" metode — SVE ispod, redosled nije bitan ===
    private void setupLocation() { ... }       // Deo 3 kod
    private void setupSensors() { ... }        // Deo 1 kod
    private void setupCameraLaunchers() { ... } // Deo 2.1 kod
    private void openCamera() { ... }
    private void setupSwitch() { ... }          // Deo 10 - Switch logika
    private void fetchAndSaveFirst10Posts() { ... }  // Deo 4 + Deo 6 zajedno
    private void setupButton() { ... }          // Deo 6 brisanje + Deo 8 notifikacija
    private void saveTextAndShowFirstContact() { ... }  // Deo 5 + Deo 7
    private boolean hasPermission(String permission) { ... }  // Deo 0, generička provera
}
```

**Zašto baš ovaj redosled?** `onCreate` → `onResume`/`onPause` su metode koje Android SISTEM
poziva automatski u određenom trenutku (zato moraju biti prepoznatljive/na vrhu), a sve "setup"
metode su VAŠ kod koji se samo poziva iz njih — njih možete pisati bilo gde ispod, ali se
po konvenciji ostavljaju na kraju klase da `onCreate` ostane čitljiv (kratak, "spisak koraka").

---

## 11.7 (D), (E), (F) — REST fajlovi: gde tačno koji deo ide

| Fajl | Šta sadrži | Šta NE sadrži |
|---|---|---|
| `model/Post.java` | SAMO polja + getteri/setteri, NIŠTA mrežno | nema Retrofit anotacija, nema `Call<>` |
| `network/PostService.java` | SAMO interfejs sa `@GET/@POST/...` metodama | nema implementacije, nema `enqueue()` |
| `network/ClientUtils.java` | SAMO `Retrofit.Builder()` + `retrofit.create(...)` | nema `Call`/`Callback` logiku |
| `MainActivity.java` (u nekoj `setupX()` metodi) | `ClientUtils.postService.getAll().enqueue(new Callback<...>(){...})` — OVDE i ONLY ovde ide `enqueue` | — |

Ovo razdvajanje (model / servis / klijent / pozivanje) je **uvek isto**, bez obzira na konkretan
zadatak — menjaju se samo nazivi klasa i polja unutar `Post.java`.

---

## 11.8 (G), (H) — SQLite fajlovi: gde tačno koji deo ide

| Fajl | Šta sadrži |
|---|---|
| `database/SQLiteHelper.java` | `extends SQLiteOpenHelper`; SAMO `onCreate()` (CREATE TABLE) i `onUpgrade()` (DROP+CREATE); konstante za nazive tabele/kolona |
| `database/PostDao.java` | Sve CRUD metode (`insertAll`, `getFirstPost`, `deleteFirstPost`, `getCount`) — svaka otvara `getWritableDatabase()`/`getReadableDatabase()`, radi posao, **zatvara `db.close()`** |
| `MainActivity.java` | Samo POZIVA metode iz `PostDao` (npr. `postDao.insertAll(posts)`) — nikad direktno `SQLiteDatabase` u aktivnosti |

**Zašto odvojiti `PostDao` od `MainActivity`?** Nije strogo obavezno (možete sve pisati i direktno
u aktivnosti, materijal sa vežbi 6 to i radi unutar same aktivnosti) — ali odvajanje čini kod
čitljivijim i lakšim za proveru na kolokvijumu. Ako vam vreme ističe, sasvim je u redu da CRUD
metode napišete kao privatne metode direktno u `MainActivity.java`.

---

## 11.9 Redosled KREIRANJA fajlova kad krećete zadatak od nule

Kad sednete na kolokvijum i krećete praznu/skoro praznu aplikaciju, evo preporučenog redosleda
(da ne skačete napred-nazad i gubite vreme):

1. **`build.gradle` (app)** — odmah dodajte SVE biblioteke koje mislite da će vam trebati (Retrofit, Play Services Location...), pa Sync
2. **`AndroidManifest.xml`** — odmah upišite SVE dozvole koje vidite da su potrebne iz teksta zadatka
3. **`res/layout/activity_main.xml`** — postavite SVE potrebne vidžete sa `id`-jevima
4. **Model klase** (`model/Post.java` i slično) — ako zadatak traži REST
5. **`network/PostService.java` + `ClientUtils.java`** — ako zadatak traži REST
6. **`database/SQLiteHelper.java` + DAO klasa** — ako zadatak traži bazu
7. **`res/xml/file_paths.xml`** — ako zadatak traži kameru
8. **`MainActivity.java`** — POSLEDNJE, jer ovde sve povezujete; pišite `onCreate` prvo (findViewById + pozivi setup metoda), pa redom dodajte `setupX()` metode jednu po jednu, testirajte (build/run) nakon svake da odmah uhvatite greške.

Ovaj redosled minimizuje vreme izgubljeno na sitne sintaksne greške koje se otkriju tek pri
kompajliranju — gradite "odozdo nagore" (konfiguracija → podaci → logika).

---

# DEO 12 — Checklist neposredno pred kolokvijum

- [ ] Znam da napravim `SensorEventListener` napamet (registracija, onSensorChanged, unregister)
- [ ] Znam shake-detekciju formulu (delta x/y/z → sqrt → prag → throttle)
- [ ] Znam FileProvider setup za kameru (manifest + file_paths.xml + getUriForFile)
- [ ] Znam MediaRecorder i MediaPlayer redosled metoda napamet
- [ ] Znam FusedLocationProviderClient (LocationRequest, LocationCallback, requestLocationUpdates)
- [ ] Znam napraviti model klasu + servisni interfejs + Retrofit instancu za bilo koji REST servis
- [ ] Znam SQLiteOpenHelper (onCreate, onUpgrade) i sve 4 CRUD operacije + Cursor navigaciju
- [ ] Znam razliku između "_id" (redosled u tabeli) i ID-a sa servera
- [ ] Znam SharedPreferences (put/get sa default vrednošću)
- [ ] Znam pročitati kontakte preko ContentResolver-a
- [ ] Znam runtime permission obrazac (registerForActivityResult + checkSelfPermission)
- [ ] Znam napraviti notifikaciju (kanal + builder + notify)
- [ ] Razumem da SVE mrežne/async operacije ažuriraju UI samo unutar callback-a

Ako za svaku stavku možete da napišete kod iz glave (bez gledanja), spremni ste.
