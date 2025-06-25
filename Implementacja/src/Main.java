import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

enum TypRoli {
    ADMIN("ADMIN"),  // zmień z "ADMINISTRATOR"
    PRACOWNIK("PRACOWNIK"),
    PRAKTYKANT("PRAKTYKANT"),
    TYMCZASOWY("TYMCZASOWY");

    private final String label;

    TypRoli(String label) {
        this.label = label;
    }

    public String getLabel() {
        return label;
    }

    @Override
    public String toString() {
        return label;
    }

    public static TypRoli fromLabel(String label) {
        for (TypRoli r : values()) {
            if (r.getLabel().equalsIgnoreCase(label)) {
                return r;
            }
        }
        throw new IllegalArgumentException("Nieprawidłowa rola: " + label);
    }

    public boolean mozeOtworzyc(Drzwi drzwi, Uzytkownik uzytkownik) {
        return switch (this) {
            case ADMIN -> true;
            case PRACOWNIK -> drzwi.getTyp() != TypDrzwi.TAJNE &&
                    (drzwi.getTyp() != TypDrzwi.ZABEZPIECZONE ||
                            uzytkownik.maUprawnienie(TypUprawnieniaSpecjalne.SERWISANT));
            case PRAKTYKANT, TYMCZASOWY -> drzwi.getTyp() == TypDrzwi.NORMALNE;
        };
    }
}


enum TypDrzwi {
    NORMALNE, ZABEZPIECZONE, TAJNE
}

enum TypUprawnieniaSpecjalne {
    AUDYTOR, SERWISANT, KONSERWATOR
}

enum StanZatrudnienia {
    ETATOWY, FIRMA_ZEWNETRZNA
}


interface UprawnieniaDoDrzwi {
    boolean mozeOtworzyc(Drzwi drzwi);

    default boolean wymagaAutoryzacji(Drzwi drzwi) {
        return drzwi.getTyp() == TypDrzwi.TAJNE;
    }

}


interface RolaZObslugaUzytkownika {
    void ustawWlasciciela(Uzytkownik u);
}


class Adres implements Serializable {
    private final String ulica;
    private final String miasto;
    private final String kodPocztowy;

    public Adres(String ulica, String miasto, String kodPocztowy) {
        this.ulica = ulica;
        this.miasto = miasto;
        this.kodPocztowy = kodPocztowy;
    }

    public String getUlica() {
        return ulica;
    }

    public String getMiasto() {
        return miasto;
    }

    public String getKodPocztowy() {
        return kodPocztowy;
    }

    @Override
    public String toString() {
        return ulica + ", " + kodPocztowy + " " + miasto;
    }
}

class Uzytkownik extends Osoba implements Serializable {
    private final Adres adres;
    private final EnumSet<TypRoli> role = EnumSet.noneOf(TypRoli.class);
    private final Set<TypUprawnieniaSpecjalne> uprawnieniaSpecjalne = new HashSet<>();
    private StanZatrudnienia stan;

    public Uzytkownik(String imie, String nazwisko, Adres adres, StanZatrudnienia stan) {
        super(imie, nazwisko);
        this.adres = adres;
        this.stan = stan;
    }

    public void dodajRole(TypRoli nowaRola) {
        role.add(nowaRola);
    }

    public String getTyp() {
        return role.isEmpty() ? "BRAK" :
                role.iterator().next().getLabel();
    }

    public Set<TypUprawnieniaSpecjalne> getUprawnieniaSpecjalne() {
        return uprawnieniaSpecjalne;
    }

    public void wyczyscRole() {
        role.clear();
    }

    public LocalDate getDataWaznosci(LocalDate dataWydania) {
        if (role.contains(TypRoli.PRAKTYKANT)) {
            return dataWydania.plusMonths(6);
        } else if (role.contains(TypRoli.TYMCZASOWY)) {
            return dataWydania.plusDays(1);
        } else if (role.contains(TypRoli.PRACOWNIK)) {
            return dataWydania.plusYears(1);
        } else if (role.contains(TypRoli.ADMIN)) {
            return dataWydania.plusYears(3);
        } else {
            return dataWydania.plusYears(1);
        }
    }


    public Set<TypRoli> getRole() {
        return Collections.unmodifiableSet(role);
    }

    public void dodajUprawnienie(TypUprawnieniaSpecjalne typ) {
        uprawnieniaSpecjalne.add(typ);
    }

    public boolean maUprawnienie(TypUprawnieniaSpecjalne typ) {
        return uprawnieniaSpecjalne.contains(typ);
    }


    public String getPelneDane() {
        return imie + " " + nazwisko + "\n" +
                adres.getUlica() + "\n" +
                adres.getKodPocztowy() + " " + adres.getMiasto();
    }

    @Override
    public String toString() {
        return imie + " " + nazwisko + " (" + adres + ")" +
                ", tel: " + (telefon != null && !telefon.isBlank() ? telefon : "brak") +
                ", role: " + role + ", stan: " + stan;
    }
}

class KartaDostepu implements Serializable {
    private static final Map<String, KartaDostepu> ekstensja = new HashMap<>();
    private final String uid;
    private final Uzytkownik uzytkownik;
    private final List<Dostep> dostepy = new ArrayList<>(); //asoscjacja zwykla
    private final List<ZdarzenieDostepu> historiaZdarzen = new ArrayList<>();
    private LocalDate dataWydania;

    public KartaDostepu(String uid, Uzytkownik uzytkownik, LocalDate dataWydania) {
        this.uid = uid;
        this.uzytkownik = uzytkownik;
        this.dataWydania = dataWydania;
        ekstensja.put(uid, this);

    }

    public static void dodajNowaKarte(String imie, String nazwisko, String ulica, String miasto, String kod, String telefon, String uid) {
        if (ekstensja.containsKey(uid)) {
            throw new IllegalArgumentException("UID już istnieje.");
        }

        Adres adres = new Adres(ulica, miasto, kod);
        Uzytkownik u = new Uzytkownik(imie, nazwisko, adres, StanZatrudnienia.ETATOWY);
        u.setTelefon(telefon);
        new KartaDostepu(uid, u, LocalDate.now());
    }

    public static void zapiszEkstensje(String plik) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(plik));
        out.writeObject(ekstensja);
        out.close();
    }

    public static void wczytajEkstensje(String plik) throws IOException, ClassNotFoundException {
        File f = new File(plik);
        if (f.exists()) {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(plik));
            Map<String, KartaDostepu> dane = (Map<String, KartaDostepu>) in.readObject();
            ekstensja.clear();
            ekstensja.putAll(dane);
            in.close();
        }
    }

    public static Map<String, KartaDostepu> getEkstensja() {
        return ekstensja;
    }

    //asocjacja zwykla
    public void dodajDostep(Dostep d) {
        if (!dostepy.contains(d)) dostepy.add(d);
    }

    public LocalDate getDataWydania() {
        return dataWydania;
    }

    public void usunDostep(Dostep d) {
        dostepy.remove(d);
    }

    public List<Dostep> getDostepy() {
        return Collections.unmodifiableList(dostepy);
    }

    public boolean maDostepDo(Drzwi drzwi) {
        return dostepy.stream().anyMatch(d -> d.getDrzwi().equals(drzwi));
    }

    public List<ZdarzenieDostepu> getHistoriaZdarzen() {
        return historiaZdarzen;
    }

    public String getUid() {
        return uid;
    }

    public Uzytkownik getUzytkownik() {
        return uzytkownik;
    }

    public void odswiezDostepyAutomatycznie() {
        LocalDate dataWaznosci = uzytkownik.getDataWaznosci(dataWydania);
        if (LocalDate.now().isAfter(dataWaznosci)) {
            for (Dostep d : new ArrayList<>(dostepy)) {
                d.getDrzwi().removeDostep(d);  // Usuń z drzwi
                this.usunDostep(d);            // Usuń z karty
            }
            historiaZdarzen.add(new ZdarzenieDostepu("Dostęp zablokowany – karta przeterminowana", LocalDate.now().toString()));
        }
    }


    @Override
    public String toString() {
        return "UID: " + uid + ", Użytkownik: " + uzytkownik + ", Dostępy: " + dostepy + ", Data wydania: " + dataWydania;
    }
}

class ZdarzenieDostepu implements Serializable {
    private final LocalDateTime data;
    private final String typ;
    private final String lokalizacja;

    public ZdarzenieDostepu(String typ, String lokalizacja) {
        this.data = LocalDateTime.now();
        this.typ = typ;
        this.lokalizacja = lokalizacja;
    }

    @Override
    public String toString() {
        return typ + " @ " + lokalizacja + " | " + data;
    }
}

class Budynek implements Serializable {
    private final String nazwa;
    private final Map<String, Drzwi> drzwiMap = new HashMap<>(); //kwalifikowana

    public Budynek(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa() {
        return nazwa;
    }

    // Kwalifikowana asocjacja
    public void dodajDrzwi(String nazwaDrzwi, TypDrzwi typ) {
        if (!drzwiMap.containsKey(nazwaDrzwi)) {
            Drzwi noweDrzwi = Drzwi.stworz(nazwaDrzwi, this, typ);
            drzwiMap.put(nazwaDrzwi, noweDrzwi);
        } else {
            System.out.println("Drzwi o tej nazwie już istnieją w budynku.");
        }
    }


    public List<Drzwi> getDrzwi() {
        return new ArrayList<>(drzwiMap.values());
    }

    public Drzwi getDrzwi(String nazwa) {
        return drzwiMap.get(nazwa); // kwalifikowana asocjacja
    }

    public void usunDrzwi(String nazwa) {
        Drzwi drzwi = drzwiMap.remove(nazwa);
        if (drzwi != null) {
            drzwi.usunWszystkieDostepy(); // kompozycja – usuń powiązania
        }
    }
}

class ListaBudynekow {
    public static final List<Budynek> budynki = new ArrayList<>();

    public static void zapiszBudynki(String plik) throws IOException {
        ObjectOutputStream out = new ObjectOutputStream(new FileOutputStream(plik));
        out.writeObject(budynki);
        out.close();
    }

    @SuppressWarnings("unchecked")
    public static void wczytajBudynki(String plik) throws IOException, ClassNotFoundException {
        File f = new File(plik);
        if (f.exists()) {
            ObjectInputStream in = new ObjectInputStream(new FileInputStream(plik));
            List<Budynek> dane = (List<Budynek>) in.readObject();
            budynki.clear();
            budynki.addAll(dane);
            in.close();
        }
    }

}

abstract class Osoba implements Serializable {
    protected String imie;
    protected String nazwisko;
    protected String telefon;

    public Osoba(String imie, String nazwisko) {
        this.imie = imie;
        this.nazwisko = nazwisko;
    }

    public Optional<String> getTelefon() {
        return Optional.ofNullable(telefon);
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public String getImieNazwisko() {
        return imie + " " + nazwisko;
    }
}

class Dostep implements Serializable {
    private final KartaDostepu karta;
    private final Drzwi drzwi;
    private final LocalDate dataPrzydzielenia;  //asocjacja z atrybutem

    public Dostep(KartaDostepu karta, Drzwi drzwi) {
        if (karta == null || drzwi == null) throw new IllegalArgumentException("Dostęp wymaga karty i drzwi");

        this.karta = karta;
        this.drzwi = drzwi;
        this.dataPrzydzielenia = LocalDate.now();

        karta.dodajDostep(this);
        drzwi.dodajDostep(this);
    }

    public KartaDostepu getKarta() {
        return karta;
    }

    public Drzwi getDrzwi() {
        return drzwi;
    }

    @Override
    public String toString() {
        return "Dostęp do drzwi: " + drzwi.getNazwa() + " (budynek: " + drzwi.getBudynek().getNazwa() +
                ") | karta UID: " + karta.getUid() + " | od: " + dataPrzydzielenia;
    }
}

class Drzwi implements Serializable {
    private final String nazwa;
    private final Budynek budynek;
    private final List<Dostep> dostepy = new ArrayList<>(); //asocjacja zwykla
    private final TypDrzwi typ;

    //kompozycja private
    private Drzwi(String nazwa, Budynek budynek, TypDrzwi typ) {
        this.nazwa = nazwa;
        this.budynek = budynek;
        this.typ = typ;
    }

    //jedyna droga tworzenia drzwi
    public static Drzwi stworz(String nazwa, Budynek budynek, TypDrzwi typ) {
        return new Drzwi(nazwa, budynek, typ);
    }


    public TypDrzwi getTyp() {
        return typ;
    }

    public String getNazwa() {
        return nazwa;
    }

    public Budynek getBudynek() {
        return budynek;
    }

    //asocjacja zwykla
    public void dodajDostep(Dostep d) {
        if (!dostepy.contains(d)) {
            dostepy.add(d);
        }
    }

    public void removeDostep(Dostep d) {
        dostepy.remove(d);
    }

    public void usunWszystkieDostepy() {
        for (Dostep d : new ArrayList<>(dostepy)) {
            d.getKarta().usunDostep(d);
        }
        dostepy.clear();
    }

    @Override
    public String toString() {
        return "Drzwi: " + nazwa + " (Budynek: " + budynek.getNazwa() + ")";
    }
}

abstract class RolaUzytkownika implements Serializable, UprawnieniaDoDrzwi {
    public abstract String getNazwa();
}

class Administrator extends RolaUzytkownika implements RolaZObslugaUzytkownika {
    private Uzytkownik wlasciciel;

    public String getNazwa() {
        return "Administrator";
    }

    @Override
    public boolean mozeOtworzyc(Drzwi drzwi) {
        return true;
    }

    public void ustawWlasciciela(Uzytkownik u) {
        this.wlasciciel = u;
    }

}

class Pracownik extends RolaUzytkownika implements RolaZObslugaUzytkownika {
    private Uzytkownik wlasciciel;

    @Override
    public String getNazwa() {
        return "Pracownik";
    }

    @Override
    public boolean mozeOtworzyc(Drzwi drzwi) {
        if (drzwi.getTyp() == TypDrzwi.TAJNE) return false;
        if (drzwi.getTyp() == TypDrzwi.ZABEZPIECZONE) {
            return wlasciciel != null && wlasciciel.maUprawnienie(TypUprawnieniaSpecjalne.SERWISANT);
        }
        return true;
    }

    @Override
    public void ustawWlasciciela(Uzytkownik u) {
        this.wlasciciel = u;
    }

}

class Student extends RolaUzytkownika {
    public String getNazwa() {
        return "Student";
    }

    public boolean mozeOtworzyc(Drzwi drzwi) {
        return drzwi.getTyp() == TypDrzwi.NORMALNE; // tylko zwykłe drzwi
    }
}