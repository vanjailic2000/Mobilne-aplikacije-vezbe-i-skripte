# Kolokvijum 2 Android – skripta za snalaženje i brzo menjanje zadatka

Ova skripta služi da na kolokvijumu ne učiš svaki zadatak napamet, nego da odmah prepoznaš šta treba da zameniš.

Profesor uglavnom ne menja suštinu. Menja:
- koje komponente stoje u layout-u
- koji senzor ide u koji TextView
- da li je kamera ili zvuk
- koji Checkbox/Button pokreće Retrofit
- šta se prikazuje iz baze
- koji prag šalje Toast
- da li ide lokacija, SharedPreferences ili notifikacija

Zato zadatak čitaš kao šablon.

---

# 1. Kako čitaš zadatak

Nemoj prvo gledati kod. Prvo podeli tekst na blokove.

## Primer

Zadatak kaže:

Unutar MainActivity postaviti dva Checkbox-a, dva Button-a i dva TextView-a jedno ispod drugoga.
Unutar prvog TextView-a prikazati vrednosti akcelerometra.
Klikom na prvi Button pokreće se snimanje zvuka.
Klikom na drugi Button zaustavlja se snimanje i čuva se u cache direktorijumu.
Klikom na prvi Checkbox dobaviti sve korisnike i sačuvati ih u bazi preko ContentProvider-a.
Kada se drugi Checkbox čekira, prikazati email desetog korisnika iz baze u drugi TextView.
Kada se odčekira, prikazati proximity senzor.
Kada se pređe prag, Toast: Daleko.

## Ti odmah pišeš mapu

UI:
- cb1
- cb2
- btn1
- btn2
- tv1
- tv2

tv1:
- akcelerometar

btn1:
- startAudio()

btn2:
- stopAudio()

cb1:
- Retrofit getUsers()
- saveUsersToDatabase preko ContentProvider-a

cb2 checked:
- tv2 = email desetog korisnika

cb2 unchecked:
- tv2 = proximity

if proximity > prag:
- Toast "Daleko"

Ovo je glavna fora. Kada ovako prevedeš zadatak, kod više nije strašan.

---

# 2. Rečnik zadatka

Ovo moraš da naučiš.

| Ako u zadatku piše | U kodu znači |
|---|---|
| prikazati u TextView | textView.setText(...) |
| tekst Button-a predstavlja vrednost senzora | button.setText(...) |
| klikom na Button | button.setOnClickListener(...) |
| kada se Checkbox čekira | checkbox.setOnCheckedChangeListener(...) |
| kada se Switch prebaci na on/off | switch.setOnCheckedChangeListener(...) |
| dobaviti podatke sa sajta | Retrofit GET |
| sačuvati u bazi | SQLite insert |
| preko ContentProvider-a | getContentResolver().insert(...) |
| prikazati iz baze | query iz SQLite/ContentProvider-a |
| cache direktorijum | getCacheDir() |
| snimanje zvuka | MediaRecorder |
| kamera | Intent MediaStore.ACTION_IMAGE_CAPTURE |
| lokacija | LocationManager |
| SharedPreferences | getSharedPreferences(...) |
| prag | if uslov |
| Toast poruka | Toast.makeText(...).show() |
| notifikacija | NotificationCompat.Builder |

---

# 3. Univerzalna mapa za svaki zadatak

Kad dobiješ novi zadatak, popuni ovo:

UI elementi:
- ______________________

Senzor 1:
- gde se prikazuje: ______________________
- tip senzora: ______________________
- prag: ______________________
- Toast: ______________________

Multimedija:
- kamera ili zvuk: ______________________
- ko pokreće: ______________________
- gde se čuva/prikazuje: ______________________

Retrofit:
- entitet: ______________________
- kada se poziva: ______________________
- šta se filtrira: ______________________
- šta se čuva: ______________________

Baza:
- tabela: ______________________
- šta se upisuje: ______________________
- šta se čita: ______________________
- koja pozicija: ______________________

Drugi događaji:
- Checkbox checked: ______________________
- Checkbox unchecked: ______________________
- Switch on: ______________________
- Switch off: ______________________
- Button klik: ______________________

---

# 4. Kako menjaš senzor

Senzori se menjaju samo na dva mesta.

## Akcelerometar

Koristiš:

Sensor.TYPE_ACCELEROMETER

U onSensorChanged:

float x = event.values[0];
float y = event.values[1];
float z = event.values[2];

Prikaz:

tv1.setText("X: " + x + "\nY: " + y + "\nZ: " + z);

## Žiroskop

Menjaš samo tip:

Sensor.TYPE_GYROSCOPE

Isto ima X, Y, Z:

float x = event.values[0];
float y = event.values[1];
float z = event.values[2];

## Proximity

Menjaš tip:

Sensor.TYPE_PROXIMITY

Ima uglavnom jednu vrednost:

float value = event.values[0];

Prikaz:

tv1.setText("Proximity: " + value);

Prag:

if (value < 5) {
    Toast.makeText(this, "Blizu", Toast.LENGTH_SHORT).show();
}

ili:

if (value > 5) {
    Toast.makeText(this, "Daleko", Toast.LENGTH_SHORT).show();
}

## Kako znaš da li je Blizu ili Daleko

Ako zadatak kaže:

- ispod praga → Blizu

onda pišeš:

if (value < prag)

Ako zadatak kaže:

- preko praga → Daleko

onda pišeš:

if (value > prag)

---

# 5. Kako menjaš TextView

Ako piše:

Unutar prvog TextView-a prikazati akcelerometar.

Onda:

tv1.setText(...);

Ako piše:

Kada se drugi Checkbox čekira, prikazati email desetog korisnika u drugi TextView.

Onda:

tv2.setText(email);

Ako piše:

Tekst Button-a predstavlja vrednosti akcelerometra.

Onda ne koristiš TextView nego:

button.setText(...);

---

# 6. Kako menjaš Button

Ako piše:

Klikom na prvi Button pokreće se snimanje zvuka.

Onda:

btn1.setOnClickListener(v -> startRecording());

Ako piše:

Klikom na drugi Button zaustavlja se snimanje.

Onda:

btn2.setOnClickListener(v -> stopRecording());

Ako piše:

Klikom na Button obrisati prvi zapis iz baze.

Onda:

btn.setOnClickListener(v -> deleteFirstFromDatabase());

Ako piše:

Klikom na Button prikazati lokaciju.

Onda:

btn.setOnClickListener(v -> startLocation());

---

# 7. Kako menjaš Checkbox

Checkbox skoro uvek znači dva stanja: checked i unchecked.

## Šablon

checkbox.setOnCheckedChangeListener((buttonView, isChecked) -> {
    if (isChecked) {
        radiOvo();
    } else {
        radiOnoDrugo();
    }
});

## Primer 1

Kada se drugi Checkbox čekira, prikazati email desetog korisnika.
Kada se odčekira, prikazati proximity.

Prevod:

if (isChecked) {
    prikaziEmailDesetogKorisnika();
} else {
    prikaziProximity = true;
}

## Primer 2

Kada se prvi Checkbox čekira, dobaviti sve kontinente.
Prevod:

if (isChecked) {
    getContinentsFromApi();
}

Ako zadatak ne kaže šta kad se odčekira, else ne mora ništa.

---

# 8. Kako menjaš Retrofit

Retrofit deo uvek ima isti tok.

1. Napraviš model
2. Napraviš ApiService
3. Napraviš RetrofitClient
4. U MainActivity pozoveš get zahtev
5. U onResponse čuvaš podatke u bazu ili prikazuješ

## Šta menjaš

Ako je Users:
- model: User
- metoda: getUsers()
- tabela: users
- polja: id, username, email, firstName, lastName

Ako je Posts:
- model: Post
- metoda: getPosts()
- tabela: posts
- polja: id, title, body

Ako je Comments:
- model: Comment
- metoda: getComments()
- tabela: comments
- polja: id, body, email

Ako je Continents:
- model: Continent
- metoda: getContinents()
- tabela: continents
- polja: id, name, population, countriesCount

---

# 9. Kako menjaš bazu

Baza se menja kroz:

- naziv tabele
- kolone
- insert metodu
- query metodu

## Primer Users

Tabela:

users

Kolone:

id
username
email

Ako zadatak traži email desetog korisnika:

query sve korisnike
pomeri cursor na poziciju 9
uzmi kolonu email

## Primer Posts

Tabela:

posts

Kolone:

id
title
body

Ako zadatak traži title prvog posta:

query sve postove
moveToFirst()
uzmi title

## Primer Continents

Tabela:

continents

Kolone:

id
name
population
countriesCount

Ako zadatak traži broj država trećeg kontinenta:

query sve kontinente
moveToPosition(2)
uzmi countriesCount

---

# 10. Najvažnije za pozicije u bazi

Ovo zapamti.

| U zadatku piše | U kodu |
|---|---|
| prvi | moveToPosition(0) ili moveToFirst() |
| drugi | moveToPosition(1) |
| treći | moveToPosition(2) |
| deseti | moveToPosition(9) |
| poslednji | moveToLast() |

Ako piše prvi zapis u tabeli, ne znači ID = 1.
Znači prvi red koji cursor vrati.

---

# 11. Kako menjaš ContentProvider

Ako zadatak kaže:

sačuvati u bazi korišćenjem ContentProvider-a

onda ne zoveš direktno dbHelper.insertUser(user), nego praviš ContentValues.

## Šablon

ContentValues values = new ContentValues();
values.put("email", user.getEmail());
values.put("username", user.getUsername());

getContentResolver().insert(UserProvider.CONTENT_URI, values);

## Šta menjaš

Za User:
values.put("email", user.getEmail());

Za Post:
values.put("title", post.getTitle());

Za Continent:
values.put("name", continent.getName());
values.put("population", continent.getPopulation());

---

# 12. Kako menjaš kameru

Ako piše:

pokreće se kamera

treba ti:

Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

Ako piše:

slika se prikazuje u ImageView

onda u rezultatu:

imageView.setImageBitmap(bitmap);

Ako piše:

slika se čuva u cache direktorijumu

onda praviš File:

File file = new File(getCacheDir(), "slika.jpg");

Ako piše:

ispisati putanju u Toast poruci

onda:

Toast.makeText(this, file.getAbsolutePath(), Toast.LENGTH_LONG).show();

---

# 13. Kako menjaš zvuk

Ako piše:

pokreće se snimanje zvuka

koristiš:

MediaRecorder recorder = new MediaRecorder();

start:

recorder.start();

stop:

recorder.stop();
recorder.release();

Cache fajl:

File audioFile = new File(getCacheDir(), "audio.3gp");

Putanja:

audioFile.getAbsolutePath()

---

# 14. Kako menjaš lokaciju

Ako piše:

prikazati lokaciju uređaja

onda ti treba:

LocationManager
ACCESS_FINE_LOCATION
requestLocationUpdates
onLocationChanged

Prikaz:

tv.setText("Lat: " + location.getLatitude() + "\nLon: " + location.getLongitude());

Ako piše:

kada se Checkbox odčekira prikazati lokaciju

onda u else delu checkbox-a pozoveš:

startLocation();

---

# 15. Kako menjaš SharedPreferences

Ako piše:

sačuvati sadržaj TextView-a u polju "tekst"

onda:

SharedPreferences prefs = getSharedPreferences("prefs", MODE_PRIVATE);
prefs.edit().putString("tekst", tv1.getText().toString()).apply();

Ako piše:

prikazati sačuvanu vrednost

onda:

String tekst = prefs.getString("tekst", "");
tv2.setText(tekst);

---

# 16. Kako menjaš Toast

Toast je uvek isti.

Toast.makeText(this, "Poruka", Toast.LENGTH_SHORT).show();

Menja se samo poruka.

Primeri:
- "Blizu"
- "Daleko"
- "Nema podataka"
- "Nema više postova!"
- putanja slike
- broj korisnika

---

# 17. Kako menjaš Notification

Ako piše:

poslati notifikaciju

onda to obično ide kada je neki uslov ispunjen.

Primer:

Ako su svi postovi obrisani, poslati notifikaciju "Nema više postova!"

Prevod:

deleteFirstPost();

if (getPostCount() == 0) {
    sendNotification("Nema više postova!");
}

---

# 18. Kako da znaš šta ide u onCreate

U onCreate ide:

1. setContentView
2. povezivanje UI elemenata
3. init senzora
4. init baze
5. init Retrofit-a ako treba
6. listener-i za dugmad/checkbox/switch
7. permission provere

## Redosled

setContentView(R.layout.activity_main);

tv1 = findViewById(R.id.tv1);
tv2 = findViewById(R.id.tv2);
btn1 = findViewById(R.id.btn1);
cb1 = findViewById(R.id.cb1);

dbHelper = new DatabaseHelper(this);

sensorManager = (SensorManager) getSystemService(SENSOR_SERVICE);

btn1.setOnClickListener(...);

cb1.setOnCheckedChangeListener(...);

---

# 19. Kako da znaš šta ide u onResume i onPause

Senzori se registruju u onResume.

onResume:
- registerListener

onPause:
- unregisterListener

To profesor može da pita.

---

# 20. Šta radiš kada dobiješ potpuno novi tekst zadatka

Korak 1:
Precrtaj rečenice i napiši pored njih šablon.

Korak 2:
Napravi UI iz teksta.

Korak 3:
Odmah poveži findViewById.

Korak 4:
Ubaci senzor.

Korak 5:
Ubaci dugmad.

Korak 6:
Ubaci Retrofit.

Korak 7:
Ubaci bazu.

Korak 8:
Ubaci dodatke: Toast, SharedPreferences, Notification, lokacija.

Nemoj kretati od Retrofit-a. Prvo UI + senzori + klikovi. Tako će aplikacija pre početi da se kompajlira.

---

# 21. Mini primer brzog prevođenja zadatka

## Tekst

Kada se prvi Checkbox čekira, dobaviti sve korisnike i sačuvati ih u bazi.

## Prevod

cb1.setOnCheckedChangeListener

if isChecked:
- api.getUsers()
- onResponse
- for User u listi
- ContentValues
- insert u provider

---

# 22. Drugi mini primer

## Tekst

Kada se drugi Checkbox čekira, prikazati email desetog korisnika iz baze.

## Prevod

cb2.setOnCheckedChangeListener

if isChecked:
- query users
- if cursor.moveToPosition(9)
- email = cursor.getString(...)
- tv2.setText(email)

---

# 23. Treći mini primer

## Tekst

Kada se odčekira, prikazati očitavanje proximity senzora.

## Prevod

else:
- aktiviraj promenljivu prikaziProximity = true
- u onSensorChanged ako je proximity:
- tv2.setText("Proximity: " + value)

---

# 24. Najčešće zamene koje profesor pravi

## Zamena 1

Akcelerometar u TextView

može postati:

Akcelerometar u Button tekst

Menjaš:

tv1.setText(...)

u:

btn.setText(...)

## Zamena 2

Kamera

može postati:

Snimanje zvuka

Menjaš ceo blok kamere za MediaRecorder.

## Zamena 3

Prikazati email desetog korisnika

može postati:

Prikazati username prvog korisnika

Menjaš:

moveToPosition(9)
email

u:

moveToFirst()
username

## Zamena 4

Sačuvati sve korisnike

može postati:

Sačuvati samo one koji ispunjavaju uslov

Dodaješ if u for petlji.

Primer:

if (user.getEmail().contains("@")) {
    insert
}

## Zamena 5

Prikazati Toast Blizu

može postati:

Prikazati Toast Daleko

Menjaš samo uslov:

value < prag

ili:

value > prag

---

# 25. Formula za svaki zadatak

Zadatak = UI + događaji + senzori + podaci + baza + uslovi

Ne čitaš ga kao teoriju.
Čitaš ga kao listu akcija.

Primer:

UI:
šta se vidi na ekranu

Događaji:
šta se dešava na klik/check/switch

Senzori:
šta stalno meri telefon

Podaci:
šta dolazi sa interneta

Baza:
šta se čuva i šta se čita

Uslovi:
kada ide Toast/Notification

---

# 26. Najbitnije da zapamtiš pred kolokvijum

Ako znaš da napišeš:

- jedan senzor
- jedan Button listener
- jedan Checkbox listener
- jedan Retrofit GET
- jedan SQLite insert/query
- jedan ContentProvider insert
- jedan Toast

možeš sastaviti 80% zadataka.

Ako još znaš:
- kameru
- zvuk
- lokaciju
- SharedPreferences
- Notification

onda si pokrila skoro sve kombinacije.

---

# 27. Šta da radiš sa mnom dok vežbamo

Pošalješ mi tekst zadatka.

Ja ću ti ga prvo prevesti ovako:

UI:
Senzor:
Button:
Checkbox:
Retrofit:
Baza:
Uslov:

Onda zajedno menjamo kod deo po deo.

Tako ćeš naučiti kako da ne paničiš kada zadatak izgleda drugačije.
