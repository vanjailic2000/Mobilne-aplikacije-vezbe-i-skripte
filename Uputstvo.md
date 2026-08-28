Mobilne aplikacije – Kolokvijum 2 (brzo uputstvo za test)

Ovaj README služi kao brzi podsetnik koji gotov projekat da otvorim na GitLab-u kada dobijem zadatak na kolokvijumu.

Setup koji smo koristili za ove primere:

Android Studio

Java

Minimum SDK: API 30 / Android 11

Empty Views Activity

Groovy DSL

Glavna aktivnost: MainActivity

Retrofit projekti koriste Beeceptor dummy JSON API

Najvažnije: nemoj pokušavati da zapamtiš ceo kod. Na testu prvo prepoznaj koje funkcionalnosti zadatak traži, pa otvori projekat ispod koji ima najsličnije stvari i gledaj odgovarajuće fajlove.

BRZI IZBOR PROJEKTA

Ako zadatak traži...

Gledaj projekat

Kamera + slika u ImageView + GPS + SharedPreferences + roles

Kolokvijum_2a

Snimanje zvuka u files + države + SQLite + proximity

Kolokvijum_2b

Snimanje zvuka u cache + korisnici + ContentProvider + proximity

Kolokvijum_2c

Kamera u cache + kontinenti + filter u bazi + proximity + GPS

Kolokvijum_2d

Kamera + postovi + SQLite + notifikacije + kontakti + SharedPreferences

Kolokvijum_2e

Kolokvijum_2a

Šta ima

TextView

CheckBox

Button

ImageView

GPS lokacija – latitude i longitude

Kamera

Slika se čuva u cache direktorijumu

Slika se prikazuje u ImageView

Akcelerometar

Žiroskop

Retrofit

Beeceptor roles

SharedPreferences

Kada da gledam 2a

Ako na zadatku vidiš nešto poput:

„pokrenuti kameru“

„fotografiju prikazati u ImageView“

„sačuvati fotografiju u cache“

„prikazati akcelerometar u Toast-u“

„prikazati žiroskop“

„dobaviti role/ulogu“

„sačuvati nešto u SharedPreferences“

onda je 2a dobar primer.

Najbitniji fajlovi

MainActivity.java

Gledaj ovde za:

GPS

dozvole za lokaciju

kameru

TakePicture()

FileProvider

akcelerometar

žiroskop

Checkbox brojanje

Retrofit poziv

SharedPreferences

activity_main.xml

Raspored:

TextView

CheckBox

Button

ImageView

Role.java

Model za Retrofit objekat.

ApiService.java

Primer GET zahteva:

@GET("roles/{role_id}")
Call<Role> getRole(@Path("role_id") int roleId);

RetrofitClient.java

Podešavanje Retrofit-a.

file_paths.xml

Gledaj kada kamera mora da snimi fajl u:

cache/images

AndroidManifest.xml

Gledaj:

INTERNET

ACCESS_FINE_LOCATION

ACCESS_COARSE_LOCATION

FileProvider

Posebno zapamti

SharedPreferences ključ u ovom zadatku je:

Uloga

Kolokvijum_2b

Šta ima

dva CheckBox-a

Button

dva TextView-a

GPS

snimanje zvuka

MediaRecorder

snimak se čuva u files direktorijumu

proximity senzor

Retrofit

Beeceptor countries

SQLite baza

brisanje poslednje države iz baze

Toast sa brojem preostalih država

Kada da gledam 2b

Ako zadatak traži:

snimanje zvuka

MediaRecorder

čuvanje zvuka u files

države

SQLite

brisanje poslednjeg reda

broj redova u bazi

proximity

onda gledaj 2b.

Najbitniji fajlovi

MainActivity.java

Gledaj za:

GPS

snimanje zvuka

start/stop MediaRecorder

proximity

Retrofit GET

Checkbox logiku

Country.java

Model države.

CountryDbHelper.java

Veoma koristan SQLite primer.

Ovde gledaj kako se radi:

SQLiteOpenHelper

CREATE TABLE

insert više objekata

COUNT(*)

brisanje poslednjeg reda

Za poslednji red:

ORDER BY id DESC LIMIT 1

ApiService.java

GET svih država:

@GET("countries")
Call<List<Country>> getCountries();

RetrofitClient.java

Ovde smo koristili i LENIENT Gson, jer Beeceptor odgovor može praviti problem parseru.

Bitna razlika

2b čuva zvuk u:

getFilesDir()

Ako zadatak kaže files direktorijum, gledaj 2b.

Kolokvijum_2c

Šta ima

dva CheckBox-a

dva Button-a

dva TextView-a

akcelerometar

snimanje zvuka

zvuk se čuva u cache

Retrofit

Beeceptor users

SQLite baza

ContentProvider

email desetog korisnika

proximity

Toast "Daleko!"

Kada da gledam 2c

Ako zadatak pominje:

ContentProvider

ContentResolver

korisnike

„deseti korisnik“

email korisnika

audio u cache

proximity prag

Toast kada je uređaj daleko

onda odmah gledaj 2c.

Najbitniji fajlovi

MainActivity.java

Gledaj:

akcelerometar

audio

Retrofit users

ContentResolver

bulkInsert

query baze preko ContentProvider-a

čitanje desetog korisnika

proximity prag

User.java

Model korisnika sa Beeceptor-a.

UserContract.java

Gledaj kada treba napraviti ContentProvider.
Sadrži:

AUTHORITY

CONTENT_URI

imena tabele i kolona

UserDbHelper.java

Kreira SQLite tabelu za korisnike.

UserContentProvider.java

Glavni primer za ContentProvider.

Ovde imaš:

query

insert

bulkInsert

delete

update

getType

UriMatcher

Ako na testu piše „sačuvati u bazu korišćenjem ContentProvider-a“, ovaj fajl ti je najvažniji.

AndroidManifest.xml

ContentProvider mora da bude prijavljen u manifestu:

<provider ... />

Bitna razlika

2c čuva zvuk u:

getCacheDir()

Ako zadatak kaže audio u cache, gledaj 2c.

Kolokvijum_2d

Šta ima

dva CheckBox-a

ImageButton

dva TextView-a

proximity

prag za proximity

Toast "Blizu!"

kamera

slika u cache

Toast sa putanjom slike

Retrofit

Beeceptor continents

SQLite baza

filtriranje podataka pre čuvanja

broj država trećeg kontinenta

GPS lokacija

Kada da gledam 2d

Ako zadatak traži:

proximity + "Blizu!"

ImageButton za kameru

sačuvati sliku u cache

prikazati putanju fajla

kontinente

sačuvati samo objekte koji ispunjavaju neki uslov

uzeti treći red iz baze

GPS

onda gledaj 2d.

Najbitniji fajlovi

MainActivity.java

Gledaj:

proximity

kamera

FileProvider

Toast putanja slike

Retrofit continents

drugi Checkbox

GPS

Continent.java

Model kontinenta.

Posebno:

private long population;

Za velike brojeve koristi long.

ContinentDbHelper.java

Dobar primer za filtriranje pre INSERT-a.

Primer:

if (continent.getPopulation() > 10000)

Takođe ima primer kako uzeti treći red iz baze:

ORDER BY id ASC LIMIT 1 OFFSET 2

Podsetnik:

drugi objekat → OFFSET 1

treći objekat → OFFSET 2

četvrti objekat → OFFSET 3

Kamera

Za kameru opet gledaj:

MainActivity.java

file_paths.xml

AndroidManifest.xml

Kolokvijum_2e

Šta ima

TextView

ImageButton

ImageView

Switch

Button

GPS

kamera

slika u ImageView

žiroskop

akcelerometar

Retrofit

Beeceptor posts

SQLite

prvih 10 postova

prvi red u tabeli

brisanje prvog reda

Android notifikacija

SharedPreferences

Contacts

prvi kontakt

Kada da gledam 2e

Ako zadatak traži:

Switch

postove

prvih 10 rezultata

brisanje prvog reda

notification

"Nema više ..."

Contacts

ime prvog kontakta

SharedPreferences

kombinaciju kamere + baze + senzora

onda je 2e veoma dobar univerzalni primer.

Najbitniji fajlovi

MainActivity.java

Gledaj ovde za:

GPS

kameru

ImageView

žiroskop

akcelerometar

Switch ON/OFF

Retrofit

SQLite pozive

brisanje posta

notifikaciju

SharedPreferences

Contacts permission

čitanje prvog kontakta

Post.java

Model posta.

PostDbHelper.java

Dobar SQLite primer za:

čuvanje samo prvih 10 objekata

uzimanje prvog reda

brisanje prvog reda

brojanje redova

Za prvi red:

ORDER BY id ASC LIMIT 1

Važno:

prvi red u tabeli nije isto što i ID = 1.

Ako obrišeš prvi red, sledeći red postaje prvi.

AndroidManifest.xml

Gledaj dozvole:

INTERNET
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION
READ_CONTACTS
POST_NOTIFICATIONS

i FileProvider.

BRZI PODSETNIK – KOJI FAJL ZA ŠTA?

Kamera

Najbolji primeri:

Kolokvijum_2a
Kolokvijum_2d
Kolokvijum_2e

Gledaj:

MainActivity.java
AndroidManifest.xml
file_paths.xml

Traži u MainActivity:

ActivityResultContracts.TakePicture()

i:

FileProvider.getUriForFile(...)

ImageView

Gledaj:

Kolokvijum_2a
Kolokvijum_2e

Traži:

imageView.setImageURI(...)

GPS / lokacija

Gledaj:

2a
2b
2d
2e

Traži u MainActivity.java:

LocationManager
LocationListener
ACCESS_FINE_LOCATION
ACCESS_COARSE_LOCATION
requestLocationUpdates
getLatitude()
getLongitude()

Ako emulator nema lokaciju:

Emulator
→ ...
→ Location
→ Set location

Akcelerometar

Gledaj:

2a
2c
2e

Traži:

Sensor.TYPE_ACCELEROMETER

Očitavanje:

event.values[0]
event.values[1]
event.values[2]

To su X, Y, Z.

Žiroskop

Gledaj:

2a
2e

Traži:

Sensor.TYPE_GYROSCOPE

Proximity

Gledaj:

2b
2c
2d

Traži:

Sensor.TYPE_PROXIMITY

Primer praga:

if (proximity < 0.5f) {
    // blizu
}

Ako treba test na emulatoru:

Emulator
→ ...
→ Virtual Sensors
→ Proximity

Snimanje zvuka

Ako zadatak kaže files

Gledaj:

Kolokvijum_2b

Traži:

getFilesDir()

Ako zadatak kaže cache

Gledaj:

Kolokvijum_2c

Traži:

getCacheDir()

Za oba je bitan:

MediaRecorder

i manifest:

<uses-permission android:name="android.permission.RECORD_AUDIO" />

Retrofit

Isti princip skoro svuda.

Gledaj bilo koji:

2a – roles
2b – countries
2c – users
2d – continents
2e – posts

Uvek uglavnom imaš:

Model.java
ApiService.java
RetrofitClient.java
MainActivity.java

Primer:

@GET("posts")
Call<List<Post>> getPosts();

U MainActivity:

call.enqueue(new Callback<...>() {
    ...
});

Ne zaboravi Gradle:

implementation 'com.squareup.retrofit2:retrofit:2.12.0'
implementation 'com.squareup.retrofit2:converter-gson:2.12.0'

Ako piše:

Cannot resolve symbol retrofit2

prvo:

Sync Now

SQLite baza

Najbolji primeri:

2b – CountryDbHelper
2d – ContinentDbHelper
2e – PostDbHelper

Osnova:

extends SQLiteOpenHelper

Treba ti:

onCreate()
onUpgrade()

Insert:

ContentValues values = new ContentValues();
db.insert(...);

Broj redova:

SELECT COUNT(*) FROM tabela

Prvi red:

ORDER BY id ASC LIMIT 1

Poslednji red:

ORDER BY id DESC LIMIT 1

Treći red:

ORDER BY id ASC LIMIT 1 OFFSET 2

ContentProvider

Ako vidiš reč:

ContentProvider

odmah otvori:

Kolokvijum_2c

Fajlovi:

UserContract.java
UserDbHelper.java
UserContentProvider.java
MainActivity.java
AndroidManifest.xml

U Activity-ju gledaj:

getContentResolver()

Za više objekata:

bulkInsert(...)

Za čitanje:

query(...)

SharedPreferences

Primer 2a

Čuva ulogu:

ključ = "Uloga"

Primer 2e

Čuva sadržaj TextView-a:

ključ = "tekst"

Osnovni kod:

SharedPreferences preferences =
        getSharedPreferences("Naziv", MODE_PRIVATE);

preferences.edit()
        .putString("kljuc", vrednost)
        .apply();

Čitanje:

String vrednost =
        preferences.getString("kljuc", "");

Contacts

Gledaj:

Kolokvijum_2e

Manifest:

<uses-permission android:name="android.permission.READ_CONTACTS" />

U kodu traži:

ContactsContract

i:

getContentResolver().query(...)

Ako emulator nema kontakte:

Contacts aplikacija
→ +
→ napravi kontakt

Notifikacije

Gledaj:

Kolokvijum_2e

Potrebno:

NotificationChannel
NotificationCompat.Builder
NotificationManagerCompat

Za novije Android verzije:

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

Ako se notifikacija ne vidi odmah, spusti notification panel.

FileProvider

Ako kamera čuva punu fotografiju u fajl, vrlo često će trebati FileProvider.

Gledaj:

2a
2d
2e

Potrebna su TRI mesta:

1. MainActivity.java

FileProvider.getUriForFile(...)

2. AndroidManifest.xml

<provider
    android:name="androidx.core.content.FileProvider"
    ...
/>

3. res/xml/file_paths.xml

Primer:

<cache-path
    name="camera_images"
    path="images/" />

DOZVOLE – BRZI PODSETNIK

Lokacija

<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

Internet / Retrofit

<uses-permission android:name="android.permission.INTERNET" />

Mikrofon

<uses-permission android:name="android.permission.RECORD_AUDIO" />

Kontakti

<uses-permission android:name="android.permission.READ_CONTACTS" />

Notifikacije

<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

KAKO DA SE SNAĐEM NA TESTU

Kada dobiješ zadatak, nemoj odmah pisati kod.

1. Pročitaj ceo zadatak

Izdvoji ključne reči.

Primer:

kamera
cache
proximity
Retrofit
korisnici
ContentProvider

Odmah vidiš:

kamera/cache → 2a / 2d / 2e
proximity → 2b / 2c / 2d
users + ContentProvider → 2c

Glavni projekat za kopiranje je onda:

2c

a kameru uzmeš iz:

2a ili 2d

2. Napravi projekat sa tačnim package-om

Ako kopiraš ceo Java fajl iz starog projekta, promeni prvu liniju:

package com.example.STARI_PROJEKAT;

u package novog projekta.

Ovo je jedna od najlakših grešaka na testu.

3. Prvo napravi XML

Najpre napravi sve kontrole koje zadatak traži:

TextView
Button
CheckBox
Switch
ImageView
ImageButton

i daj im ID-eve.

Tek posle radi Java kod.

4. Zatim Manifest

Odmah dodaj dozvole koje će trebati.

GPS → location permissions
Retrofit → INTERNET
Audio → RECORD_AUDIO
Contacts → READ_CONTACTS
Notification → POST_NOTIFICATIONS

Ako ima kamera + FileProvider, ubaci i provider.

5. Napravi pomoćne Java fajlove

Za Retrofit:

Model
ApiService
RetrofitClient

Za SQLite:

NestoDbHelper

Za ContentProvider:

Contract
DbHelper
ContentProvider

6. Tek onda MainActivity

Pronađi projekat koji ima najsličniji MainActivity.java i kopiraj delove koji ti trebaju.

7. Gradle Sync

Ako si dodao Retrofit i vidiš:

Cannot resolve symbol retrofit2

klikni:

Sync Now

Tek ako posle Sync-a ostane greška, proveravaj kod.

8. Testiraj TAČKU PO TAČKU

Nemoj samo proveriti da se aplikacija otvara.

Na primer:

1. GPS radi?
2. Kamera radi?
3. Slika se prikazuje?
4. Toast radi?
5. Retrofit vraća podatke?
6. Baza čuva?
7. Checkbox/Switch sledeći klik radi?
8. Notifikacija stiže?

NAJVAŽNIJA MAPA ZA PAMĆENJE

2a
→ kamera + GPS + ImageView
→ accelerometer + gyro
→ roles
→ SharedPreferences

2b
→ AUDIO U FILES
→ countries
→ SQLite
→ delete LAST
→ proximity

2c
→ AUDIO U CACHE
→ users
→ CONTENT PROVIDER
→ 10. korisnik email
→ proximity + Daleko

2d
→ proximity + Blizu
→ kamera CACHE + putanja
→ continents
→ filter population
→ 3. red baze
→ GPS

2e
→ kamera + ImageView + gyro
→ posts
→ SQLite
→ prvih 10
→ delete FIRST
→ notification
→ accelerometer na Button
→ SharedPreferences
→ Contacts

AKO DOBIJEM KOMBINOVANI ZADATAK

Nije problem ako novi zadatak nije identičan nijednom primeru.

Primer:

Traži:
- GPS
- snimanje zvuka u cache
- ContentProvider
- notifikaciju

Uzimaš:

GPS → 2a / 2b / 2d / 2e
audio cache → 2c
ContentProvider → 2c
notification → 2e

Projekti služe kao biblioteka gotovih primera – ne mora ceo novi zadatak da postoji u jednom projektu.

POSLEDNJI PODSETNIK

Ako nemaš vremena:

KAMERA           → 2a
AUDIO files      → 2b
AUDIO cache      → 2c
CONTENT PROVIDER → 2c
PROXIMITY        → 2d
SQLITE           → 2e / 2b
NOTIFICATION     → 2e
CONTACTS         → 2e
SHARED PREFS     → 2a / 2e
GPS              → 2a
RETROFIT         → bilo koji 2a–2e

Ako vidiš nešto što ne znaš, pretraži GitLab projekte po ključnim rečima:

MediaRecorder
FileProvider
Sensor.TYPE_PROXIMITY
Sensor.TYPE_ACCELEROMETER
Sensor.TYPE_GYROSCOPE
SQLiteOpenHelper
ContentProvider
SharedPreferences
NotificationCompat
ContactsContract
LocationManager
Retrofit

To će te mnogo brže dovesti do tačnog primera nego da otvaraš svaki fajl redom.