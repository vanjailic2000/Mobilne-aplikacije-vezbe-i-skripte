# VODIČ KROZ ANDROID STUDIO — korak po korak (klik po klik)

> Ovo je praktični vodič za **samu aplikaciju Android Studio** — gde se klikće, šta se otvara,
> kako se pokreće emulator, kako se čita greška. Koristite ga **zajedno** sa
> `SKRIPTA_KOLOKVIJUM2.md` (ona govori ŠTA da napišete, ovaj vodič govori KAKO da to uradite u
> programu).

---

# KORAK 1 — Otvaranje Android Studio i kreiranje projekta

1. Pokrenite **Android Studio**. Na startnom ekranu kliknite **"New Project"**.
2. U galeriji template-a izaberite **"Empty Views Activity"** (NE "Empty Activity" sa Compose —
   nama treba klasičan XML layout sistem, jer su svi primeri sa vežbi u tom obliku).
3. Popunite:
   - **Name:** npr. `Kolokvijum2`
   - **Package name:** npr. `com.example.kolokvijum2` (ovo mora da se slaže sa onim što kasnije
     koristite u `getPackageName() + ".provider"` za FileProvider)
   - **Language:** **Java** (proverite da nije Kotlin — vežbe su pisane u Javi)
   - **Minimum SDK:** API 24 ili više je sigurno
4. Kliknite **Finish**. Android Studio će sada "build-ovati" projekat — sačekajte da traka na dnu
   (status bar) završi (piše "Gradle build finished" ili slično).

---

# KORAK 2 — Snalaženje u glavnom prozoru

Levo imate **Project panel** (ako ga ne vidite: `View → Tool Windows → Project`). Na vrhu tog
panela postoji padajući meni — obavezno prebacite sa **"Android"** prikaza (default, pojednostavljen)
na **"Project"** prikaz ako želite da vidite TAČNU putanju fajlova kao u prethodnoj skripti
(`app/src/main/...`). Za svakodnevni rad, "Android" prikaz je obično lakši za navigaciju, ali
manje liči na stvarnu strukturu foldera.

Bitni delovi prozora:
- **Gore u sredini:** traka sa dugmićima — zeleni "Play" trougao (▶) = **Run** (pokreće aplikaciju)
- **Dole:** kartice `Build`, `Logcat`, `Terminal` — najbitnije za debug
- **Desno:** `Gradle` panel (alatka za build zadatke — koristili smo je u vežbama 8 za `signingReport`)

---

# KORAK 3 — Dodavanje biblioteka (dependencies) preko UI-ja

1. U **Project panelu**, otvorite `Gradle Scripts` (ako ste u "Android" prikazu) i pronađite
   **`build.gradle (Module :app)`** — kliknite dvoklikom da ga otvorite.
2. Pronađite blok koji počinje sa `dependencies {`.
3. Dodajte novu liniju **unutar** tih zagrada (kursor na novi red, otkucajte liniju, npr.):
   ```
   implementation 'com.squareup.retrofit2:retrofit:2.9.0'
   ```
4. Gore desno (ili na vrhu uređivača) pojaviće se žuta/plava traka sa natpisom
   **"Sync Now"** — KLIKNITE NA NJU. Bez ovog koraka, biblioteka postoji u tekstu fajla, ali
   Android Studio je još nije zaista preuzeo i kod neće moći da se kompajlira (videćete crvene
   linije ispod `import` izjava koje koriste tu biblioteku).
5. Sačekajte da traka na dnu ekrana završi sinhronizaciju (može potrajati 10-60 sekundi, zavisi
   od internet konekcije).

**Ako "Sync Now" ne postoji vidljivo:** `File → Sync Project with Gradle Files` radi istu stvar.

---

# KORAK 4 — Kreiranje novih fajlova i foldera

### Kreiranje novog paketa (foldera za Java klase)
1. Desni klik na `app/java/com.example.kolokvijum2` (glavni paket)
2. `New → Package`
3. Upišite ime, npr. `model` (ili `network`, `database`)

### Kreiranje nove Java klase
1. Desni klik na paket gde želite klasu (npr. `model`)
2. `New → Java Class`
3. Upišite ime, npr. `Post` — Android Studio automatski dodaje `.java` i otvara prazan fajl sa
   `package com.example.kolokvijum2.model;` na vrhu

### Kreiranje novog XML resource fajla (npr. `file_paths.xml`)
1. Desni klik na `app/res`
2. Ako folder `xml` ne postoji: `New → Android Resource Directory` → u polju **Resource type**
   izaberite `xml` → OK
3. Desni klik na novi `res/xml` folder → `New → XML Resource File` → ime: `file_paths` → OK

### Kreiranje novog layout fajla
1. Desni klik na `res/layout` → `New → Layout Resource File`
2. Upišite ime (npr. `activity_main` je već tu po default-u za prvu aktivnost)

---

# KORAK 5 — Uređivanje layout-a (dva moda: Design i Code)

Kada otvorite `activity_main.xml`, na vrhu uređivača (desno) imate dva/tri dugmeta:
**Code | Split | Design**.

- **Code** — pišete XML ručno (preporučeno za kolokvijum, brže je i preciznije kad znate tagove)
- **Design** — drag & drop vidžeta sa palete levo (korisno ako se ne sećate tačnog naziva taga,
  npr. da li se zove `Switch` ili `SwitchCompat`)
- **Split** — oba odjednom, korisno za provizornu vizuelnu proveru dok pišete kod

**Praktičan savet:** Pišite u Code modu, ali povremeno prebacite na Design da vidite da li
elementi imaju smisla na ekranu (npr. da ImageView nije visine 0).

---

# KORAK 6 — Pokretanje emulatora (virtuelnog uređaja)

1. `Tools → Device Manager` (ili ikonica telefona u toolbar-u)
2. Ako nemate kreiran uređaj: **"Create Device"** → izaberite model (npr. Pixel 6) → izaberite
   **system image** (preporučeno: najnoviji sa Google Play ikonicom, jer vam treba Google Play
   Services za lokaciju!) → Finish
3. Kliknite zeleni **Play (▶)** pored uređaja u listi da ga pokrenete, ili samo pritisnite glavni
   **Run** dugme u Android Studio toolbar-u (zeleni trougao) dok je vaš modul izabran — to će
   automatski pokrenuti emulator i instalirati/pokrenuti aplikaciju na njemu.

### Slanje lažne lokacije emulatoru (za testiranje GPS-a)
1. Dok je emulator pokrenut, kliknite na **"..."** (tri tačke, Extended controls) na bočnoj traci
   emulatora
2. Otvorite **Location** sekciju
3. Upišite lat/lng (ili izaberite tačku na mapi) → **"Send"**
4. Vaša aplikacija (preko `FusedLocationProviderClient`) bi trebalo da primi ovu lokaciju

### Slikanje na emulatoru (kamera)
Emulator ima simuliranu kameru — kad pritisnete dugme za slikanje, prikazaće se test-šablon
slike (siva mreža/krugovi) umesto stvarne fotografije. To je normalno i očekivano, samo provera
da li se `ImageView` ažurira je bitna.

**Mikrofon (snimanje zvuka) NE RADI na emulatoru** — ovo testirajte samo na fizičkom telefonu
ako vam zadatak to traži.

---

# KORAK 7 — Pokretanje aplikacije i čitanje grešaka

1. Izaberite svoj uređaj (emulator ili povezan telefon) iz padajućeg menija pored Run dugmeta
2. Kliknite zeleni **▶ Run** (ili `Shift+F10`)
3. Android Studio kompajlira, instalira `.apk` na uređaj i automatski ga otvara

### Ako kod ne kompajlira (crveni X u Build panelu)
- Otvorite karticu **Build** na dnu ekrana
- Crvene linije pokazuju tačan fajl i broj linije sa greškom — kliknite na grešku, Android Studio
  vas vodi direktno na to mesto u kodu
- Najčešće greške: zaboravljena `;`, nedostajući `import`, pogrešan tip parametra

### Ako se aplikacija pokrene ali "crash-uje" (sruši se)
1. Otvorite karticu **Logcat** na dnu ekrana
2. U padajućem meniju gore izaberite svoj uređaj i proces (paket vaše aplikacije)
3. U search/filter polje upišite `Exception` ili filtrirajte nivo na **Error**
4. Crveni tekst sa `FATAL EXCEPTION` i `at com.example.kolokvijum2.MainActivity.onCreate(...)`
   pokazuje TAČNO koja linija je izazvala crash — kliknite na plavi link u stack trace-u

**Najčešći crash razlozi na kolokvijumu:**
- `NullPointerException` — pozvali ste metodu na `findViewById` rezultatu koji ne postoji u
  layout-u (provrite da `id` u XML-u i `R.id.X` u kodu IMAJU ISTO IME)
- Nedostaje dozvola u Manifestu (čak i ako tražite runtime, statička linija mora postojati)
- Pokušaj pristupa internetu/bazi na glavnoj niti pre nego što su podaci stigli

---

# KORAK 8 — Praćenje sopstvenih log poruka (Log.d, Log.i)

Korisno da vidite "da li se ova linija koda izvršila":

```java
Log.d("REZ_DB", "Stigao odgovor sa servera, broj postova: " + posts.size());
```

U **Logcat** panelu, otkucajte `REZ_DB` u search polje da filtrirate samo svoje poruke.
Ovo je brže od korišćenja `Toast`-a za debug, jer ne morate da klikćete na ekranu telefona.

---

# KORAK 9 — Davanje dozvola RUČNO na emulatoru (ako dijalog ne iskoči)

Ponekad runtime permission dijalog ne iskoči odmah (npr. ako je dozvola već jednom odbijena
"zauvek"). Da resetujete:
1. Na emulatoru, dugo pritisnite ikonicu vaše aplikacije → **App info**
2. **Permissions** → uključite ručno dozvolu koja vam treba (Camera, Location, Contacts...)

Ili, brže: `Settings → Apps → [Vaša app] → Permissions` direktno na emulatoru.

---

# KORAK 10 — Dodavanje test kontakata na emulator (za ContentProvider deo)

Ako emulator nema kontakte (a treba vam za "ime prvog kontakta"):
1. Otvorite **Contacts** aplikaciju na emulatoru (ikonica u app drawer-u)
2. Dodajte 1-2 test kontakta ručno (ime + telefon)
3. Vaša aplikacija će ih moći pročitati preko `ContentResolver` koda iz skripte

---

# KORAK 11 — Generisanje Java klasa iz JSON-a (za REST model)

1. Otvorite u browseru **jsonschema2pojo.org**
2. U levo polje nalepite primer JSON odgovora sa beeceptor servisa (otvorite link iz zadatka u
   browseru da vidite kako odgovor izgleda)
3. Podesite: **Source type → JSON**, **Annotation style → Gson**
4. Desno se generišu Java klase — kopirajte sadržaj i nalepite u vašu `Post.java` (ili sličnu)
   klasu u Android Studio-u, pa samo izmenite `package` liniju na vrh da odgovara vašem paketu

---

# KORAK 12 — Pregledanje SQLite baze direktno (provera da li je upis uspeo)

1. Dok je aplikacija pokrenuta na emulatoru, otvorite **View → Tool Windows → App Inspection**
   (ili `Database Inspector` u starijim verzijama)
2. Izaberite proces vaše aplikacije
3. Videćete listu baza (npr. `posts.db`) — kliknite da otvorite tabele i vidite redove uživo,
   bez ikakvog dodatnog koda

Ovo je odličan način da brzo provertite da li `insertAll()` zaista upisuje podatke, bez potrebe
da pišete privremene `Toast` poruke za debug.

---

# KORAK 13 — Brza referenca: prečice koje štede vreme

| Prečica | Šta radi |
|---|---|
| `Shift + F10` | Run (pokreni aplikaciju) |
| `Ctrl + Alt + L` | Auto-format kod (sredi uvlačenja) |
| `Alt + Enter` | Brzi popravi (import, generate getter/setter, itd.) — KORISTITE OVO STALNO |
| `Ctrl + Space` | Auto-complete |
| `Ctrl + click` na ime metode/klase | Idi na definiciju |
| `Ctrl + Alt + O` | Ukloni nekorišćene import-e |

**`Alt+Enter` je vaš najbolji prijatelj na kolokvijumu** — kad Android Studio podvuče nešto
crveno (npr. nedostaje import), stavite kursor na to mesto i pritisnite `Alt+Enter` — najčešće
će vam ponuditi da automatski doda import ili kreira metodu/getter koji nedostaje.

---

# KORAK 14 — Redosled celog procesa, od početka do kraja (sažetak)

1. New Project → Empty Views Activity → Java
2. `build.gradle (Module :app)` → dodaj dependencies → Sync Now
3. `AndroidManifest.xml` → dodaj dozvole
4. `res/layout/activity_main.xml` → postavi vidžete sa id-jevima
5. Kreiraj pakete i klase (`model`, `network`, `database`) → popuni ih kodom iz skripte
6. `MainActivity.java` → `findViewById` → `setupX()` metode → poveži sve
7. Pokreni emulator (sa Google Play Services image-om)
8. Run (▶) → pratiti Logcat za greške
9. Testirati svaku funkcionalnost pojedinačno (klikni dugme, uključi switch...) i provericati
   Database Inspector / Logcat da li radi kako treba

Ovaj redosled odgovara redosledu iz **Deo 11.9** prethodne skripte — sada znate i ŠTA i KAKO
fizički da kliknete da to sprovedete u Android Studio-u.
