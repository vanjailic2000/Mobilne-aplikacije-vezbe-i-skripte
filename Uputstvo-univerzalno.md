Univerzalno uputstvo za rad Android projekata

Ovaj README služi kao kratak redosled rada na kolokvijumu kada dobiješ novi Android zadatak i koristiš stare GitLab projekte kao primere.

1. Napravi projekat

Najčešći setup koji smo koristili:

Empty Views Activity
Language: Java
Minimum SDK: API 30 / Android 11
Build configuration: Groovy DSL
MainActivity

Ako kopiraš Java fajl iz starog projekta, OBAVEZNO promeni package na vrhu:

package com.example.IME_NOVOG_PROJEKTA;

2. Prvo pročitaj ceo zadatak

Izdvoji ključne reči, na primer:

kamera
GPS
RecyclerView
Fragment
Retrofit
SQLite
ContentProvider
BroadcastReceiver
Service
SharedPreferences
Notification
Contacts
MediaRecorder
proximity
accelerometer
gyroscope

Onda na GitLab-u pronađi stari projekat koji ima najsličniju funkcionalnost.

Ne mora ceo novi zadatak da postoji u jednom starom projektu.
Slobodno kombinuješ delove iz više projekata.

3. Prvo uradi activity_main.xml

Napravi sve komponente koje zadatak traži:

TextView
Button
CheckBox
Switch
ImageView
ImageButton
RecyclerView
Toolbar
FragmentContainerView
EditText

Daj im jasne ID-eve, npr:

buttonSnimi
textViewLocation
checkBoxUsers
imageViewPhoto

Tek posle radi Java kod.

4. Zatim sredi AndroidManifest.xml

Dodaj potrebne dozvole.

Najčešće:

<!-- Internet -->
<uses-permission android:name="android.permission.INTERNET" />

<!-- Lokacija -->
<uses-permission android:name="android.permission.ACCESS_FINE_LOCATION" />
<uses-permission android:name="android.permission.ACCESS_COARSE_LOCATION" />

<!-- Mikrofon -->
<uses-permission android:name="android.permission.RECORD_AUDIO" />

<!-- Kontakti -->
<uses-permission android:name="android.permission.READ_CONTACTS" />

<!-- Notifikacije -->
<uses-permission android:name="android.permission.POST_NOTIFICATIONS" />

Ako koristiš kameru sa punom slikom u fajlu, proveri i:

FileProvider
res/xml/file_paths.xml

Ako koristiš ContentProvider, proveri da je <provider> dodat u Manifest.

5. Napravi pomoćne Java fajlove

Retrofit

Obično trebaju:

Model.java
ApiService.java
RetrofitClient.java

i Gradle dependencies:

implementation 'com.squareup.retrofit2:retrofit:2.12.0'
implementation 'com.squareup.retrofit2:converter-gson:2.12.0'

Posle obavezno:

Sync Now

SQLite

Napravi npr:

BookDbHelper.java
PostDbHelper.java
CountryDbHelper.java

sa:

extends SQLiteOpenHelper

ContentProvider

Obično trebaju:

Contract.java
DbHelper.java
ContentProvider.java

RecyclerView

Obično trebaju:

Model.java
Adapter.java
layout za jedan red
Fragment/Activity

6. Tek onda radi MainActivity.java / Fragment

U ovom fajlu spoji UI sa funkcionalnostima:

findViewById(...)
setOnClickListener(...)
setOnCheckedChangeListener(...)

i dodaj logiku koju zadatak traži.

Ako kopiraš kod iz starog projekta, proveri:

package

ID-eve iz XML-a

nazive klasa

Manifest dozvole

Gradle dependencies

dodatne XML fajlove

7. Šta mora da se kopira ZAJEDNO

Kamera

MainActivity.java
+ AndroidManifest.xml
+ file_paths.xml

Retrofit

Model
+ ApiService
+ RetrofitClient
+ Gradle dependencies
+ INTERNET permission

Audio

MediaRecorder kod
+ RECORD_AUDIO permission
+ runtime permission

ContentProvider

Contract
+ DbHelper
+ ContentProvider
+ Manifest provider
+ ContentResolver kod

Notification

NotificationChannel
+ NotificationCompat
+ POST_NOTIFICATIONS

Contacts

ContactsContract
+ READ_CONTACTS
+ runtime permission

8. Ako nešto pocrveni

Najčešće greške:

Cannot resolve retrofit2
→ Sync Now

R.id.nešto ne postoji
→ proveri ID u XML-u

Class not found / package error
→ proveri package na vrhu Java fajla

SecurityException
→ proveri Manifest + runtime permission

FileProvider crash
→ proveri authorities + file_paths.xml

ContentProvider not found
→ authority u Contract-u i Manifestu mora biti isti

GPS ne radi
→ emulator → Location → Set location

Proximity se ne menja
→ emulator → Virtual Sensors

Nema kontakata
→ dodaj kontakt u Contacts aplikaciji

Notification se ne vidi
→ proveri dozvolu i notification panel

9. Obavezni redosled pred pokretanje

1. XML
2. Manifest
3. pomoćne Java klase
4. MainActivity / Fragment
5. Gradle Sync
6. Build → Make Project
7. Run
8. testiranje tačku po tačku

10. Testiraj SVAKU stavku zadatka

Nemoj stati samo zato što se aplikacija pokrenula.

Na kraju proveri redom:

[ ] UI izgleda kako zadatak traži
[ ] dugmad rade
[ ] Checkbox/Switch rade
[ ] dozvole rade
[ ] kamera/audio/senzor radi
[ ] Retrofit dobija podatke
[ ] baza zaista čuva/čita/briše
[ ] Toast se pojavljuje
[ ] Notification stiže
[ ] sledeći klik / ponovno čekiranje radi

Ako svaka tačka radi, tek tada je projekat gotov.

NAJKRAĆI PODSETNIK

PROČITAJ ZADATAK
↓
PREPOZNAJ FUNKCIONALNOSTI
↓
NAĐI SLIČAN GITLAB PRIMER
↓
XML
↓
MANIFEST
↓
POMOĆNE KLASE
↓
MAINACTIVITY / FRAGMENT
↓
GRADLE SYNC
↓
BUILD
↓
TESTIRAJ SVAKU TAČKU

Ne pokušavaj da pišeš sve od nule ako već imaš ispravan primer na GitLab-u.