# Sve moguće realne kombinacije za Kolokvijum 2 – Android Studio

Ovo nije jedna aplikacija, nego skripta za učenje kombinacija koje profesor najrealnije može da pomeša.

Glavne oblasti koje se ponavljaju:
- Retrofit + Beeceptor JSON
- SQLite baza
- ContentProvider
- SharedPreferences
- senzori: akcelerometar, proximity, žiroskop
- kamera
- snimanje zvuka
- lokacija
- Toast / Notification

## Najrealniji princip zadatka

Profesor obično spoji:

1. Layout elementi
2. Jedan senzor koji se stalno prikazuje
3. Kamera ili snimanje zvuka
4. Retrofit GET za neki entitet
5. Upis u bazu, često preko ContentProvider-a
6. Checkbox/Switch/Button koji nešto prikazuje iz baze
7. Toast ili notifikacija na neki uslov

---

# KOMBINACIJA 1

## Tekst zadatka

MainActivity ima dva Checkbox-a, dva Button-a i dva TextView-a.
U prvom TextView-u prikazati akcelerometar.
Klikom na prvi Button počinje snimanje zvuka.
Klikom na drugi Button se zaustavlja snimanje i zvuk se čuva u cache direktorijumu.
Podesiti Retrofit za Users.
Klikom na prvi Checkbox dobaviti sve korisnike i sačuvati ih u bazu preko ContentProvider-a.
Kada se drugi Checkbox čekira, prikazati email desetog korisnika iz baze u drugi TextView.
Kada se odčekira, prikazati proximity senzor.
Ako proximity pređe prag, prikazati Toast: Daleko.

## Šta moraš znati

- Akcelerometar ide preko SensorManager-a.
- Audio ide preko MediaRecorder-a.
- Cache putanja ide preko getCacheDir().
- Retrofit dobavlja listu korisnika.
- Baza čuva korisnike.
- ContentProvider ubacuje korisnike.
- Drugi checkbox menja ponašanje drugog TextView-a.

---

# KOMBINACIJA 2

## Tekst zadatka

MainActivity ima dva Checkbox-a, dva ImageButton-a i dva TextView-a.
U prvom TextView-u prikazati proximity senzor.
Ako je vrednost ispod proizvoljnog praga, poslati Toast: Blizu.
Klikom na ImageButton pokreće se kamera.
Potvrdom snimanja slika se čuva u cache direktorijumu i putanja se prikazuje u Toast poruci.
Kreirati model u bazi za kontinente sa Beeceptor sajta.
Klikom na prvi Checkbox dobaviti sve kontinente i sačuvati samo one čija populacija prelazi 1000.
Kada se drugi Checkbox čekira, prikazati broj država trećeg kontinenta iz baze u drugi TextView.
Kada se odčekira, prikazati očitavanje lokacije.

## Šta moraš znati

- Proximity obično ima jednu vrednost: event.values[0].
- Kamera se pokreće Intent-om MediaStore.ACTION_IMAGE_CAPTURE.
- Slika u cache-u se pravi kao File u getCacheDir().
- Kontinent mora imati ime, populaciju i broj država ili listu država.
- Lokacija ide preko LocationManager-a ili FusedLocationProviderClient-a.

---

# KOMBINACIJA 3

## Tekst zadatka

MainActivity ima TextView, ImageButton, ImageView, Switch i Button.
U TextView prikazati lokaciju uređaja.
Klikom na ImageButton pokreće se kamera.
Nakon snimanja fotografija se prikazuje u ImageView.
Svaki put kada se slika zameni, u Toast poruci prikazati žiroskop X, Y i Z.
Podesiti Retrofit za Postove.
Kada se Switch prvi put uključi, dobaviti i u bazu upisati prvih 10 postova.
Sledeći put prikazati title prvog posta iz baze u Toast poruci.
Klikom na Button obrisati prvi post iz baze.
Ako su svi postovi obrisani, poslati notifikaciju: Nema više postova!
Tekst Button-a predstavlja akcelerometar u realnom vremenu.
Kada se Switch prebaci na off, sadržaj TextView-a sačuvati u SharedPreferences i u TextView prikazati ime prvog kontakta.

## Šta moraš znati

- Ovo je najteža kombinacija jer spaja skoro sve.
- Switch mora imati boolean kontrolu da znaš da li je prvi put.
- Notification se šalje kada baza ostane prazna.
- Contacts zahtevaju READ_CONTACTS dozvolu.
- SharedPreferences čuvaju tekst pod ključem, npr. "tekst".

---

# KOMBINACIJA 4

## Tekst zadatka

MainActivity ima Button, Switch, TextView, ImageView i Checkbox.
U TextView prikazati žiroskop.
Klikom na Button pokreće se kamera i slika se prikazuje u ImageView.
Podesiti Retrofit za Users.
Klikom na Switch dobaviti korisnike i upisati u bazu.
Ako se Checkbox čekira, prikazati username prvog korisnika.
Ako se odčekira, prikazati email poslednjeg korisnika.

## Šta se menja

Ovo je lakša varijanta kombinacije 1.
Umesto zvuka ide kamera.
Umesto desetog korisnika može biti prvi, poslednji ili proizvoljni.

---

# KOMBINACIJA 5

## Tekst zadatka

MainActivity ima dva Button-a, dva TextView-a i Switch.
U prvom TextView-u prikazati proximity senzor.
Ako je vrednost manja od praga, Toast: Blizu.
Klikom na prvi Button počinje snimanje zvuka.
Klikom na drugi Button snimanje staje i čuva se u cache.
Podesiti Retrofit za Comments.
Kada se Switch uključi, dobaviti komentare i upisati ih u bazu.
U drugi TextView prikazati body drugog komentara iz baze.

## Šta se menja

Entitet nije User/Post nego Comment.
Kod je isti, menjaju se model, tabela i polje koje prikazuješ.

---

# KOMBINACIJA 6

## Tekst zadatka

MainActivity ima Checkbox, Button i dva TextView-a.
U prvom TextView-u prikazati akcelerometar.
Klikom na Button prikazati trenutnu lokaciju u drugom TextView-u.
Podesiti Retrofit za Posts.
Kada se Checkbox čekira, dobaviti sve postove i sačuvati samo one čiji je id manji od 10.
Kada se Checkbox odčekira, obrisati prvi post iz baze.

## Šta se menja

Ovde nema kamere ni zvuka.
Fokus je na senzor + lokacija + retrofit + baza.

---

# KOMBINACIJA 7

## Tekst zadatka

MainActivity ima dva Checkbox-a, Button, TextView i ImageView.
U TextView-u prikazati proximity senzor.
Klikom na Button pokreće se kamera.
Slika se prikazuje u ImageView.
Podesiti Retrofit za Continents.
Prvi Checkbox dobavlja kontinente i čuva u bazu.
Drugi Checkbox prikazuje naziv trećeg kontinenta iz baze.
Ako nema trećeg kontinenta, prikazati Toast: Nema podataka.

## Šta se menja

Ovo je skoro kao tvoj drugi zadatak, samo se traži naziv umesto broj država.

---

# KOMBINACIJA 8

## Tekst zadatka

MainActivity ima Switch, Button i dva TextView-a.
U prvom TextView-u prikazati žiroskop.
Klikom na Button sačuvati trenutno očitavanje žiroskopa u SharedPreferences.
Kada se Switch uključi, dobaviti korisnike preko Retrofit-a i upisati ih u bazu.
Kada se Switch isključi, prikazati sačuvanu vrednost iz SharedPreferences u drugi TextView.

## Šta se menja

Ovde nema ContentProvider-a ako ga profesor ne naglasi.
Ako ga naglasi, ubacivanje u bazu radiš preko getContentResolver().insert().

---

# KOMBINACIJA 9

## Tekst zadatka

MainActivity ima ImageButton, Button, TextView i Checkbox.
ImageButton pokreće kameru i čuva sliku u cache.
Button briše poslednji zapis iz baze.
TextView prikazuje akcelerometar.
Checkbox dobavlja postove preko Retrofit-a i čuva ih u bazu.
Ako je baza prazna nakon brisanja, poslati notifikaciju.

## Šta se menja

Ovo je varijanta gde je Notification vezan za brisanje.

---

# KOMBINACIJA 10

## Tekst zadatka

MainActivity ima dva TextView-a, dva Button-a i Switch.
Prvi TextView prikazuje lokaciju.
Drugi TextView prikazuje proximity.
Klikom na prvi Button pokreće se snimanje zvuka.
Klikom na drugi Button snimanje se zaustavlja.
Switch dobavlja korisnike preko Retrofit-a.
Ako korisnik ima email koji sadrži "@", upisuje se u bazu.
U Toast poruci prikazati broj upisanih korisnika.

## Šta se menja

Ovo je filtriranje pre upisa u bazu.
Isti princip kao kontinenti sa populacijom preko 1000.

---

# KOMBINACIJA 11

## Tekst zadatka

MainActivity ima Checkbox, Switch, Button i dva TextView-a.
Prvi TextView prikazuje akcelerometar.
Button pokreće kameru.
Checkbox dobavlja Comments i čuva ih u bazu.
Switch prikazuje email prvog korisnika iz Contacts aplikacije.
Kada se Switch isključi, u drugi TextView prikazati komentar iz baze.

## Šta se menja

Ovo spaja bazu sa Contacts ContentProvider-om.

---

# KOMBINACIJA 12

## Tekst zadatka

MainActivity ima Button, ImageButton, TextView, ImageView i Switch.
Button tekst se menja u vrednosti akcelerometra.
ImageButton pokreće kameru.
ImageView prikazuje sliku.
Switch dobavlja postove i čuva ih u bazu.
Kada se slika zameni, prikazati title prvog posta u Toast-u.

## Šta se menja

Senzor ne mora u TextView, može biti tekst dugmeta.

---

# NAJBITNIJI ŠABLONI ZA PREPOZNAVANJE

## Ako vidiš "prikazati vrednost senzora"

Treba ti:
- SensorManager
- Sensor
- implements SensorEventListener
- onResume registerListener
- onPause unregisterListener
- onSensorChanged

## Ako vidiš "pokreće se kamera"

Treba ti:
- Intent(MediaStore.ACTION_IMAGE_CAPTURE)
- ActivityResultLauncher ili startActivityForResult
- Bitmap ili FileProvider
- dozvola CAMERA
- queries u manifestu za API 30

## Ako vidiš "snimanje zvuka"

Treba ti:
- MediaRecorder
- RECORD_AUDIO permission
- outputFile = new File(getCacheDir(), "zvuk.3gp")
- start()
- stop()
- release()

## Ako vidiš "Retrofit"

Treba ti:
- model klasa
- ApiService interface
- RetrofitClient
- Call<List<Model>>
- enqueue callback

## Ako vidiš "baza"

Treba ti:
- SQLiteOpenHelper
- onCreate CREATE TABLE
- insert metoda
- get metoda
- delete metoda

## Ako vidiš "ContentProvider"

Treba ti:
- Provider klasa extends ContentProvider
- AUTHORITY
- CONTENT_URI
- insert/query/delete/update
- upis preko getContentResolver().insert()

## Ako vidiš "SharedPreferences"

Treba ti:
- getSharedPreferences("prefs", MODE_PRIVATE)
- edit().putString("tekst", vrednost).apply()
- getString("tekst", "")

## Ako vidiš "lokacija"

Treba ti:
- ACCESS_FINE_LOCATION permission
- LocationManager
- requestLocationUpdates
- onLocationChanged
- TextView = latitude + longitude

## Ako vidiš "notifikacija"

Treba ti:
- NotificationChannel za API 26+
- NotificationCompat.Builder
- NotificationManager
- POST_NOTIFICATIONS samo ako je noviji API, ali za API 30 nije obavezno

---

# Najrealnije šta može da se desi na kolokvijumu

Najverovatnije kombinacije:

1. Akcelerometar + zvuk + Users + email iz baze
2. Proximity + kamera + Continents + broj država
3. Lokacija + kamera + Posts + SharedPreferences
4. Žiroskop + kamera + Users + Toast
5. Proximity + zvuk + Comments + SQLite
6. Akcelerometar + lokacija + Posts + Notification

Ako naučiš ova 6 obrasca, realno pokrivaš skoro sve.
