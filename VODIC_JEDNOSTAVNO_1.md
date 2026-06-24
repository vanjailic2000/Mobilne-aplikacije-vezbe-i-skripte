# NAJPROSTIJI VODIČ — šta da radim u Android Studio-u

> Zamislite Android Studio kao **veliku kancelariju sa fasciklama**. Svaki fajl je papir u
> nekoj fascikli. Vi samo treba da znate: (1) gde da nađete pravu fasciklu, (2) šta da napišete
> na papir, (3) kako da provertite da je sve ispravno.

---

## 1. Prvo — otvaranje praznog projekta

To je kao da uzmete **praznu fasciklu** i napišete na nju ime. Klik na "New Project", izaberete
"Empty Views Activity" (obična, bez dodatnih ukrasa), upišete ime, kliknete Finish.

**Zašto baš "Empty Views Activity"?** Jer postoji i druga vrsta (Compose) koja izgleda
potpuno drugačije i ne odgovara onome šta smo radili na vežbama.

---

## 2. Prozor programa — šta je šta

Zamislite ekran podeljen na 3 dela:

```
┌─────────────┬────────────────────────────┐
│             │     ovde pišete kod        │
│  SPISAK     │                            │
│  FAJLOVA    │                            │
│  (levo)     │                            │
│             ├────────────────────────────┤
│             │  ovde vidite GREŠKE (dole) │
└─────────────┴────────────────────────────┘
```

- **Levo** = spisak svih fajlova (kao fascikle u ormanu)
- **Sredina** = papir na kome pišete (kod)
- **Dole** = "kantica za smeće" gde vam program kaže šta nije dobro

---

## 3. Dodavanje "tuđih alata" (dependencies) — već objašnjeno ranije

Ukratko opet: otvorite fajl `build.gradle (Module :app)`, dodate jednu liniju teksta, kliknete
"Sync Now" (to je dugme "Poruči"). Gotovo.

---

## 4. Kreiranje novog fajla — UVEK ISTI POKRET

Bez obzira da li pravite novu klasu, novi XML, novi folder — pokret je isti:

```
DESNI KLIK na fasciklu  →  "New"  →  izaberete šta hoćete da kreirate  →  upišete ime
```

To je to. Nema potrebe pamtiti 5 različitih postupaka — uvek je "desni klik → New → ...".

---

## 5. Pisanje izgleda ekrana (layout)

Ovde imate **dve opcije** kako da "crtate" ekran:
- **Code** = pišete tekst (kao pisanje liste namirnica) — BRŽE
- **Design** = vučete dugmiće mišem na ekran — SPORIJE, ali vizuelno

Za kolokvijum: pišite u Code modu, samo na kraju kliknite Design da vidite da li "izgleda kao
ekran", a ne kao gomila teksta.

---

## 6. Pokretanje na "virtuelnom telefonu" (emulator)

Emulator = telefon koji postoji **samo unutar računara**, ne morate imati pravi telefon.

- Da ga uključite: kliknete zeleno dugme ▶ (Play)
- Telefon se otvori kao novi prozor na ekranu, kao da gledate snimak ekrana pravog telefona

**Bitno da znate:** taj virtuelni telefon **ne ume da snima zvuk** i kamera mu daje "lažnu"
sliku (šarena mreža umesto stvarne fotografije) — to je normalno, nije greška u vašem kodu.

### Kako da "lažirate" lokaciju (GPS) na emulatoru
Telefon ne zna gde se "nalazi" jer nije stvaran. Zato mu vi RUČNO kažete koordinate:
klik na "..." (tri tačkice) pored slike telefona → Location → upišete brojeve → Send.

---

## 7. Kad nešto "puca" (greška)

Postoje 2 vrste problema:

**A) Kod se ne kompajlira** (program ga ni ne pokreće)
→ Gledate karticu **Build** na dnu, crveni tekst kaže gde je problem.

**B) Aplikacija se pokrene, ali se SAMA zatvori** (crash)
→ Gledate karticu **Logcat** na dnu, tražite crveni tekst `FATAL EXCEPTION` — on kaže TAČNO
koja linija koda je krivac.

**Najčešći uzrok #1 na kolokvijumu:** napisali ste pogrešno ime u kodu (npr. `tvLokacija`) koje
se ne poklapa sa imenom u XML fajlu (`tvLocation`). Mora biti IDENTIČNO slovo po slovo.

---

## 8. Sopstvene poruke za "praćenje" (Log)

Ako hoćete da provertite "da li je ova linija koda uopšte pozvana", dodate:
```java
Log.d("TEST", "stigao sam ovde");
```
i u Logcat-u tražite reč "TEST". Kao da ostavljate "trag" da znate gde se program kretao.

---

## 9-10. Dozvole i kontakti na emulatoru

- Ako se ne pojavi prozorče "Da li dozvoljavaš kameru?" — idite ručno: dugo pritisnite ikonicu
  aplikacije → App info → Permissions → uključite ručno.
- Ako vam treba "ime kontakta" a telefon nema kontakte — otvorite Contacts app na emulatoru i
  sami dodate jedan kontakt, baš kao na pravom telefonu.

---

## 11. Pretvaranje JSON-a u Java kod (kad radite REST)

Sajt **jsonschema2pojo.org** radi to umesto vas: nalepite primer odgovora sa servera, kliknete
generiši, i on vam ispiše Java klasu — samo je kopirate u svoj projekat.

---

## 12. Gledanje baze podataka "uživo"

Da provertite da li se podaci stvarno upisuju u bazu (SQLite), ne morate pisati dodatni kod —
postoji alat **Database Inspector** (View → Tool Windows → App Inspection) koji vam pokaže
tabelu kao Excel tabelu, dok je aplikacija upaljena.

---

## 13. Prečice (samo jedna je BAŠ bitna)

**`Alt + Enter`** = "Android Studio, popravi ovo za mene". Kad vidite crveno podvučeno u kodu,
stavite kursor tu i pritisnite `Alt+Enter` — najčešće sam dodaje `import` koji nedostaje.

---

## 14. Redosled radova — kao recept, korak po korak

1. Napravi projekat
2. Dodaj "tuđe alate" u build.gradle (ako treba internet/lokacija)
3. Upiši dozvole u Manifest
4. Nacrtaj ekran (layout)
5. Napravi pomoćne klase (model, baza, mreža)
6. Napiši `MainActivity` koji sve povezuje
7. Pokreni virtuelni telefon
8. Pritisni Run, gledaj da li puca, popravljaj
9. Testiraj svaki deo (klikni dugme, uključi prekidač...) jedan po jedan
