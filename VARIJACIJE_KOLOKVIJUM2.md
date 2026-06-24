# Sve moguće varijacije zadatka — Kolokvijum 2 (Mobilne aplikacije)

Ovaj zadatak je "šablon" koji profesor/asistenti mešaju iz istih sastojaka (vežbe 6-9).
Skoro svaki kolokvijumski zadatak je kombinacija dole nabrojanih varijacija. Cilj ovog
vodiča je da prepoznate **obrazac**, ne da pamtite jedan konkretan zadatak.

---

## 1. Senzori (vežbe 9) — šta sve mogu tražiti

| Varijacija | Šta menja u kodu |
|---|---|
| Drugi tip senzora (LIGHT, PROXIMITY, MAGNETIC_FIELD, PRESSURE, LINEAR_ACCELERATION...) | Isti `SensorEventListener` obrazac, samo `getDefaultSensor(Sensor.TYPE_X)` i drugačiji broj vrednosti u `event.values[]` (npr. PROXIMITY i LIGHT imaju samo `values[0]`) |
| Prikaz u TextView umesto Toast/Button teksta | `tvX.setText(...)` unutar `onSensorChanged()` |
| **Shake detekcija** (vežbe 9, poglavlje 1.2) | Računanje `deltaX/Y/Z` između dva merenja, `Math.sqrt(...)`, poređenje sa pragom (`SHAKE_THRESHOLD`), throttling pomoću `currentTime - lastShakeTime > 500` |
| Senzor okidanje neke druge akcije (npr. notifikacija na shake, ne samo Toast) | Isti shake kod, ali pozivate `sendNotification()` umesto `setText()` |
| Kombinacija dva senzora istovremeno | Registrovati oba u `onResume()`, granati se u `onSensorChanged()` po `event.sensor.getType()` |

**Zamka:** ne zaboravite `unregisterListener()` u `onPause()` — inače curi baterija i može doći do duplih merenja.

---

## 2. Kamera i multimedija (vežbe 9) — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| Slikanje (ono što imamo) | `ActivityResultContracts.TakePicture()` + `FileProvider` |
| **Snimanje videa** umesto slike | `MediaStore.ACTION_VIDEO_CAPTURE` umesto `ACTION_IMAGE_CAPTURE` |
| **Snimanje zvuka** (vežbe 9, poglavlje 2) | `MediaRecorder` — setAudioSource/OutputFormat/AudioEncoder/OutputFile → prepare() → start()/stop()/release() |
| **Reprodukcija zvuka** (poglavlje 3) | `MediaPlayer` — setDataSource → prepare() → start(), `setOnCompletionListener` za čišćenje |
| Brisanje/izmena postojeće slike | Samo `imageUri = null; imgPhoto.setImageURI(null);` ili ponovni `openCamera()` |
| Reakcija na promenu slike (kao kod nas - Toast sa senzorom) | Stavlja se u `onActivityResult`/`registerForActivityResult` callback, nakon `setImageURI` |

**Zamka:** kamera ne radi na emulatoru bez podešene virtuelne kamere; snimanje zvuka ne radi na emulatoru nikako (eksplicitno navedeno u materijalu).

---

## 3. Lokacija i mape (vežbe 8) — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| Samo prikaz koordinata u TextView-u (naš slučaj) | `FusedLocationProviderClient` + `LocationCallback`, bez mape |
| **Prikaz mape** (Google Maps ili OSMDroid) | Treba `MapFragment`/`MapView`, `OnMapReadyCallback`, marker preko `addMarker()` |
| **Postavljanje pina klikom na mapu** | `map.setOnMapClickListener(latLng -> {...})` |
| **Crtanje rute** između dve adrese (OSMDroid) | Geokodiranje preko Nominatim API-ja + `Polyline` |
| Provera da li je lokacija uključena | `LocationManager.isProviderEnabled()` + dijalog koji vodi na Settings |
| Udaljenost između dve tačke | `Location.distanceTo()` ili `Location.distanceBetween()` |

**Zamka:** GPS_PROVIDER ne radi na svim emulatorima — koristiti "Extended controls" u emulatoru da pošaljete lažnu lokaciju za testiranje.

---

## 4. REST / Retrofit (vežbe 7) — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| GET lista (naš slučaj) | `Call<ArrayList<X>>` |
| GET pojedinačni resurs po ID-u | `@GET("posts/{id}")` + `@Path("id")` |
| **POST** (slanje podataka na server) | `@POST("posts")` + `@Body Post post`, `Call<Post> add(...)` |
| **PUT/PATCH** (izmena) | `@PUT("posts/{id}")` |
| **DELETE** | `@DELETE("posts/{id}")` |
| Query parametri (sortiranje, filtriranje) | `@Query("sort") String sort` u metodi interfejsa |
| Različiti entiteti (users, comments) — iz našeg zadatka vežbi 7 | Isti obrazac, samo druga klasa modela i druga ruta (`@GET("users")`, `@GET("comments")`) |
| Brojanje elemenata i prikaz u Toast-u (kao "koliko ima korisnika" iz vežbi 7 zadatka) | U `onResponse`: `list.size()` pa `Toast` |

**Zamka:** `baseUrl` MORA da se završava sa `/`, a putanja u `@GET("...")` NE SME da počinje sa `/` — često se ovde gubi vreme na debug.

---

## 5. SQLite (vežbe 6) — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| Upis liste sa servera u bazu (naš slučaj) | `insertAll()` petlja sa `db.insert(...)` |
| Čitanje po redosledu umetanja (ne po server ID-u) | `ORDER BY _id ASC LIMIT 1` |
| **Update** postojećeg reda | `db.update(table, values, "_id=?", new String[]{id})` |
| **Pretraga/filter** | `db.query(table, null, "title LIKE ?", new String[]{"%x%"}, null, null, null)` |
| Brojanje redova | `SELECT COUNT(*)` |
| Korišćenje preko **ContentProvider**-a umesto direktno (vežbe 6, poglavlje 4) | `getContentResolver().insert(CONTENT_URI, values)` umesto `db.insert(...)` direktno |

**Zamka:** kod izmene baze (`onUpgrade`), ako promenite strukturu tabele tokom razvoja, povećajte `DATABASE_VERSION` ili samo obrišite app sa emulatora — inače stara baza ostaje.

---

## 6. SharedPreferences (vežbe 6) — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| Čuvanje teksta (naš slučaj) | `putString("tekst", ...)` |
| Čuvanje boolean/int podešavanja (npr. sinhronizacija na X minuta) | `putBoolean`/`putInt` |
| Učitavanje sačuvane vrednosti pri pokretanju aplikacije | U `onCreate()`: `prefs.getString("tekst", "default")` |
| Korišćenje `PreferenceFragmentCompat` ekrana podešavanja (vežbe 6, poglavlje 1) | `preferences.xml` + `ListPreference`/`CheckBoxPreference` + `dependency` atribut |

---

## 7. ContentProvider / kontakti (vežbe 6) — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| Prvi kontakt (naš slučaj) | `cursor.moveToFirst()` |
| Svi kontakti u listi | `do { ... } while(cursor.moveToNext())` |
| Pretraga kontakta po imenu | `selection = DISPLAY_NAME + " = ?"`, `selectionArgs` |
| Telefonski broj kontakta umesto imena | `ContactsContract.CommonDataKinds.Phone.CONTENT_URI` (treba i `Phone.NUMBER` kolona) |
| Pristup sopstvenom ContentProvider-u (ne sistemskom) | Kreiranje `DBContentProvider` klase, `UriMatcher`, deklaracija u Manifestu (vežbe 6, poglavlje 4.2) |

---

## 8. Notifikacije — varijacije

| Varijacija | Šta menja u kodu |
|---|---|
| Notifikacija pri pražnjenju baze (naš slučaj) | `NotificationManager.notify()` nakon provere `getCount() == 0` |
| Notifikacija na shake događaj | Isti kod, okidač je shake umesto brisanja |
| Notifikacija sa akcijom (klik otvara aktivnost) | `PendingIntent` + `.setContentIntent(pendingIntent)` |

**Zamka:** od Android 13 (API 33) treba runtime dozvola `POST_NOTIFICATIONS` — ako je zaboravite, notifikacija se tiho ne prikazuje (nema crash-a, samo ne radi).

---

## 9. Opšti obrasci koje uvek primenjujete bez obzira na varijantu

1. **Runtime dozvole** — svaka "osetljiva" funkcionalnost (kamera, lokacija, kontakti, mikrofon, notifikacije) ide kroz:
   - Provera: `ContextCompat.checkSelfPermission(...) == PackageManager.PERMISSION_GRANTED`
   - Ako nema dozvole: `registerForActivityResult(new ActivityResultContracts.RequestPermission(), ...)` pa `.launch(permission)`
2. **Asinhroni pozivi** (Retrofit, lokacija) — sve što zavisi od rezultata ide UNUTAR callback-a (`onResponse`, `onLocationResult`), nikad odmah posle pozivanja metode.
3. **Senzori i lokacija trože baterija** — registruju se u `onResume()`, otkače u `onPause()`.
4. **Switch/CheckBox sa "stanje koje se pamti"** — kad zadatak kaže "prvi put... svaki sledeći put...", to znači da vam treba `boolean` zastavica kao **polje klase** (ne lokalna promenljiva), jer se metoda poziva iznova svaki put.
5. **"Prvi/sledeći" element u bazi** — gotovo uvek znači sortiranje po internom `_id` (autoincrement), NE po vrednosti koja dolazi sa servera.

---

## Kako da vežbate

Najbolji način da se pripremite: uzmite ovaj kod kao osnovu, i probajte sami da "rotirate" jednu varijaciju iz svake tabele (npr. zamenite žiroskop sa senzorom svetla, GET sa POST-om, SQLite čitanje sa ContentProvider čitanjem) i vidite da li možete da to uklopite u postojeću strukturu. Ako razumete **zašto** je kod organizovan baš ovako (npr. zašto su permisije launcher-i, zašto je `boolean` zastavica polje klase), lako prepoznajete isti obrazac u bilo kojoj varijaciji zadatka.
