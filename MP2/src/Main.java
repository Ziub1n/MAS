import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

public class Main {
    public static void main(String[] args) {
        try {
            KartaDostepu.wczytajEkstensje("karty.dat");
            ListaBudynekow.wczytajBudynki("budynki.dat");
            System.out.println("Dane zostały wczytane.");
        } catch (IOException | ClassNotFoundException e) {
            System.out.println("Błąd wczytywania danych.");
        }


        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n╔══════════════════════════════════╗");
            System.out.println("║      SYSTEM KART DOSTĘPU         ║");
            System.out.println("╠══════════════════════════════════╣");
            System.out.println("║ 1.    Dodaj uzytkownika i kartę  ║");
            System.out.println("║ 2.    Wyświetl użytkowników      ║");
            System.out.println("║ 3.    Usun użytkownika           ║");
            System.out.println("║ 4.    Przydziel dostęp do drzwi  ║");
            System.out.println("║ 5.    Odbierz dostęp do drzwi    ║");
            System.out.println("║----------------------------------║");
            System.out.println("║ 6.    Dodaj budynek              ║");
            System.out.println("║ 7.  ️ Usun budynek               ║");
            System.out.println("║ 8.    Dodaj drzwi do budynku     ║");
            System.out.println("║ 9.    Usun drzwi do budynku      ║");
            System.out.println("║ 10.   Pokaż drzwi w budynku      ║");
            System.out.println("║ 11.   Pokaż historię zdarzeń     ║");
            System.out.println("║----------------------------------║");
            System.out.println("║ 0. X  Wyjście                    ║");
            System.out.println("╚══════════════════════════════════╝");
            System.out.print("Wybierz opcję: ");


            String opcjaStr = scanner.nextLine();
            if (!opcjaStr.matches("\\d+")) {
                System.out.println("Nieprawidłowa opcja, podaj liczbę.");
                continue;
            }
            int opcja = Integer.parseInt(opcjaStr);


            switch (opcja) {
                case 1 -> KartaDostepu.dodajNowaKarte(scanner);
                case 2 -> KartaDostepu.pokazEkstensje();
                case 3 -> KartaDostepu.usunKarte(scanner);

                case 4 -> {
                    List<KartaDostepu> listaKart = new ArrayList<>(KartaDostepu.getEkstensja().values());
                    if (listaKart.isEmpty()) {
                        System.out.println("Brak kart.");
                        break;
                    }

                    System.out.println("Lista kart:");
                    for (int i = 0; i < listaKart.size(); i++) {
                        System.out.println((i + 1) + ". UID: " + listaKart.get(i).getUid() + ", " + listaKart.get(i).getUzytkownik().getImieNazwisko());
                    }

                    System.out.print("Wybierz numer karty: ");
                    String input = scanner.nextLine();
                    if (!input.matches("\\d+")) break;
                    int wyborKarty = Integer.parseInt(input);
                    if (wyborKarty < 1 || wyborKarty > listaKart.size()) break;

                    KartaDostepu karta = listaKart.get(wyborKarty - 1);

                    if (ListaBudynekow.budynki.isEmpty()) {
                        System.out.println("Brak budynków.");
                        break;
                    }

                    System.out.println("Lista budynków:");
                    for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
                        System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer budynku: ");
                    input = scanner.nextLine();
                    if (!input.matches("\\d+")) break;
                    int wybBud = Integer.parseInt(input);
                    if (wybBud < 1 || wybBud > ListaBudynekow.budynki.size()) break;

                    Budynek budynek = ListaBudynekow.budynki.get(wybBud - 1);
                    List<Drzwi> drzwiLista = new ArrayList<>(budynek.getDrzwi());
                    if (drzwiLista.isEmpty()) {
                        System.out.println("Brak drzwi w tym budynku.");
                        break;
                    }

                    System.out.println("Lista drzwi:");
                    for (int i = 0; i < drzwiLista.size(); i++) {
                        System.out.println((i + 1) + ". " + drzwiLista.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer drzwi: ");
                    input = scanner.nextLine();
                    if (!input.matches("\\d+")) break;
                    int wybDrzwi = Integer.parseInt(input);
                    if (wybDrzwi < 1 || wybDrzwi > drzwiLista.size()) break;

                    Drzwi drzwi = drzwiLista.get(wybDrzwi - 1);

                    if (karta.maDostepDo(drzwi)) {
                        System.out.println("Użytkownik już ma dostęp do tych drzwi.");
                    } else {
                        new Dostep(karta, drzwi); // automatycznie dodaje się do obu stron
                        karta.getHistoriaZdarzen().add(new ZdarzenieDostepu("Przydzielono dostęp", budynek.getNazwa() + ":" + drzwi.getNazwa()));
                        System.out.println("Przydzielono dostęp.");
                    }
                }

                case 5 -> {
                    List<KartaDostepu> listaKart = new ArrayList<>(KartaDostepu.getEkstensja().values());
                    if (listaKart.isEmpty()) {
                        System.out.println("Brak kart.");
                        break;
                    }

                    System.out.println("Lista kart:");
                    for (int i = 0; i < listaKart.size(); i++) {
                        System.out.println((i + 1) + ". UID: " + listaKart.get(i).getUid() + ", " + listaKart.get(i).getUzytkownik().getImieNazwisko());
                    }

                    System.out.print("Wybierz numer karty: ");
                    String input = scanner.nextLine();
                    if (!input.matches("\\d+")) break;
                    int wyborKarty = Integer.parseInt(input);
                    if (wyborKarty < 1 || wyborKarty > listaKart.size()) break;

                    KartaDostepu karta = listaKart.get(wyborKarty - 1);
                    List<Dostep> dostepy = new ArrayList<>(karta.getDostepy());

                    if (dostepy.isEmpty()) {
                        System.out.println("Ta karta nie ma żadnych dostępów.");
                        break;
                    }

                    System.out.println("Lista dostępów:");
                    for (int i = 0; i < dostepy.size(); i++) {
                        Dostep d = dostepy.get(i);
                        System.out.println((i + 1) + ". " + d.getDrzwi().getBudynek().getNazwa() + ":" + d.getDrzwi().getNazwa());
                    }

                    System.out.print("Wybierz numer dostępu do usunięcia: ");
                    input = scanner.nextLine();
                    if (!input.matches("\\d+")) break;
                    int wyb = Integer.parseInt(input);
                    if (wyb < 1 || wyb > dostepy.size()) break;

                    Dostep d = dostepy.get(wyb - 1);
                    karta.usunDostep(d);
                    d.getDrzwi().removeDostep(d);
                    karta.getHistoriaZdarzen().add(new ZdarzenieDostepu("Odebrano dostęp", d.getDrzwi().getBudynek().getNazwa() + ":" + d.getDrzwi().getNazwa()));
                    System.out.println("Dostęp został odebrany.");
                }


                case 6 -> {
                    System.out.print("Podaj nazwę budynku: ");
                    String nazwa = scanner.nextLine();
                    boolean istnieje = ListaBudynekow.budynki.stream()
                            .anyMatch(b -> b.getNazwa().equalsIgnoreCase(nazwa));
                    if (istnieje) {
                        System.out.println("Budynek o tej nazwie już istnieje!");
                    } else {
                        ListaBudynekow.budynki.add(new Budynek(nazwa));
                        System.out.println("Dodano budynek: " + nazwa);
                    }
                }


                case 7 -> {
                    if (ListaBudynekow.budynki.isEmpty()) {
                        System.out.println("Brak budynków do usunięcia.");
                        break;
                    }

                    System.out.println("Lista budynków:");
                    for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
                        System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer budynku do usunięcia: ");
                    String wybor = scanner.nextLine();
                    if (!wybor.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór (podaj liczbę).");
                        break;
                    }
                    int wyb = Integer.parseInt(wybor);

                    if (wyb < 1 || wyb > ListaBudynekow.budynki.size()) {
                        System.out.println("Nieprawidłowy numer budynku.");
                        break;
                    }

                    Budynek budynekDoUsuniecia = ListaBudynekow.budynki.get(wyb - 1);

                    for (KartaDostepu karta : KartaDostepu.getEkstensja().values()) {
                        List<Dostep> doUsuniecia = karta.getDostepy().stream()
                                .filter(d -> d.getDrzwi().getBudynek() == budynekDoUsuniecia)
                                .toList();
                        for (Dostep d : doUsuniecia) {
                            karta.usunDostep(d);
                            d.getDrzwi().removeDostep(d);
                        }
                    }


                    budynekDoUsuniecia.usunWszystkieDrzwi();
                    ListaBudynekow.budynki.remove(budynekDoUsuniecia);

                    System.out.println("Usunięto budynek i jego drzwi.");
                }


                case 8 -> {
                    if (ListaBudynekow.budynki.isEmpty()) {
                        System.out.println("Brak budynków.");
                        break;
                    }

                    System.out.println("Lista budynków:");
                    for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
                        System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer budynku: ");
                    String wybor = scanner.nextLine();
                    if (!wybor.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór (podaj liczbę).");
                        break;
                    }
                    int wyb = Integer.parseInt(wybor);

                    if (wyb < 1 || wyb > ListaBudynekow.budynki.size()) {
                        System.out.println("Nieprawidłowy numer budynku.");
                        break;
                    }

                    Budynek b = ListaBudynekow.budynki.get(wyb - 1);

                    System.out.print("Podaj nazwę drzwi: ");
                    String nazwaDrzwi = scanner.nextLine();

                    if (b.getDrzwi(nazwaDrzwi) != null) {
                        System.out.println("Drzwi o tej nazwie już istnieją w tym budynku!");
                    } else {
                        b.dodajDrzwi(nazwaDrzwi);
                        System.out.println("Dodano drzwi do budynku: " + nazwaDrzwi);
                    }
                }


                case 9 -> {
                    if (ListaBudynekow.budynki.isEmpty()) {
                        System.out.println("Brak budynków.");
                        break;
                    }

                    System.out.println("Lista budynków:");
                    for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
                        System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer budynku: ");
                    String wyborBudynku = scanner.nextLine();
                    if (!wyborBudynku.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór (podaj liczbę).");
                        break;
                    }
                    int wybBud = Integer.parseInt(wyborBudynku);
                    if (wybBud < 1 || wybBud > ListaBudynekow.budynki.size()) {
                        System.out.println("Nieprawidłowy wybór budynku.");
                        break;
                    }

                    Budynek budynek = ListaBudynekow.budynki.get(wybBud - 1);
                    List<Drzwi> listaDrzwi = new ArrayList<>(budynek.getDrzwiMap().values());

                    if (listaDrzwi.isEmpty()) {
                        System.out.println("Brak drzwi w tym budynku.");
                        break;
                    }

                    System.out.println("Lista drzwi:");
                    for (int i = 0; i < listaDrzwi.size(); i++) {
                        System.out.println((i + 1) + ". " + listaDrzwi.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer drzwi do usunięcia: ");
                    String wyborDrzwi = scanner.nextLine();
                    if (!wyborDrzwi.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór (podaj liczbę).");
                        break;
                    }
                    int wybDrzwi = Integer.parseInt(wyborDrzwi);

                    if (wybDrzwi < 1 || wybDrzwi > listaDrzwi.size()) {
                        System.out.println("Nieprawidłowy wybór drzwi.");
                        break;
                    }

                    Drzwi drzwiDoUsuniecia = listaDrzwi.get(wybDrzwi - 1);
                    String nazwaDrzwi = drzwiDoUsuniecia.getNazwa();


                    for (Dostep d : new ArrayList<>(drzwiDoUsuniecia.getDostepy())) {
                        d.getKarta().usunDostep(d);
                        drzwiDoUsuniecia.removeDostep(d);
                    }

                    budynek.usunDrzwi(nazwaDrzwi);

                    System.out.println("Usunięto drzwi: " + nazwaDrzwi + " oraz wszystkie dostępy.");
                }


                case 10 -> {
                    if (ListaBudynekow.budynki.isEmpty()) {
                        System.out.println("Brak budynków.");
                        break;
                    }

                    System.out.println("Lista budynków:");
                    for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
                        System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer budynku: ");
                    String wyborBudynku = scanner.nextLine();
                    if (!wyborBudynku.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór (podaj liczbę).");
                        break;
                    }
                    int wyb = Integer.parseInt(wyborBudynku);

                    if (wyb < 1 || wyb > ListaBudynekow.budynki.size()) {
                        System.out.println("Nieprawidłowy wybór budynku.");
                        break;
                    }

                    Budynek b = ListaBudynekow.budynki.get(wyb - 1);
                    b.pokazDrzwi();
                }

                case 11 -> {
                    if (KartaDostepu.getEkstensja().isEmpty()) {
                        System.out.println("Brak kart.");
                        break;
                    }

                    System.out.println("Lista kart:");
                    List<KartaDostepu> listaKart = new ArrayList<>(KartaDostepu.getEkstensja().values());
                    for (int i = 0; i < listaKart.size(); i++) {
                        System.out.println((i + 1) + ". UID: " + listaKart.get(i).getUid() + ", Użytkownik: " + listaKart.get(i).getUzytkownik().getImieNazwisko());
                    }

                    System.out.print("Wybierz numer karty: ");
                    String wyborStr = scanner.nextLine();
                    if (!wyborStr.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór.");
                        break;
                    }
                    int wybor = Integer.parseInt(wyborStr);
                    if (wybor < 1 || wybor > listaKart.size()) {
                        System.out.println("Nieprawidłowy numer.");
                        break;
                    }

                    KartaDostepu karta = listaKart.get(wybor - 1);

                    System.out.println("Historia zdarzeń dla karty UID: " + karta.getUid());
                    if (karta.getHistoriaZdarzen().isEmpty()) {
                        System.out.println("Brak historii zdarzeń.");
                    } else {
                        for (ZdarzenieDostepu zdarzenie : karta.getHistoriaZdarzen()) {
                            System.out.println(zdarzenie);
                        }
                    }
                }


                case 0 -> {
                    try {
                        KartaDostepu.zapiszEkstensje("karty.dat");
                        ListaBudynekow.zapiszBudynki("budynki.dat");
                        System.out.println("Zapisano dane. Zamykam program.");
                    } catch (IOException e) {
                        System.out.println("Błąd zapisu danych.");
                    }
                    running = false;
                }


                default -> System.out.println("Niepoprawny wybór!");

            }
        }
        scanner.close();
    }
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

    public Uzytkownik(String imie, String nazwisko, Adres adres) {
        super(imie, nazwisko);
        this.adres = adres;
    }

    public Adres getAdres() {
        return adres;
    }

    public String getPelneDane() {
        return imie + " " + nazwisko + "\n" +
                adres.getUlica() + "\n" +
                adres.getKodPocztowy() + " " + adres.getMiasto();
    }

    @Override
    public String toString() {
        return imie + " " + nazwisko + " (" + adres + ")" +
                ", tel: " + (telefon != null && !telefon.isBlank() ? telefon : "brak");
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

    public static void dodajNowaKarte(Scanner scanner) {
        String imie;
        while (true) {
            System.out.print("Podaj imię: ");

            imie = scanner.nextLine().trim();
            if (imie.matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) break;
            System.out.println("Błąd: Imię może zawierać tylko litery.");
        }

        String nazwisko;
        while (true) {
            System.out.print("Podaj nazwisko: ");
            nazwisko = scanner.nextLine().trim();
            if (nazwisko.matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) break;
            System.out.println("Błąd: Nazwisko może zawierać tylko litery.");
        }

        System.out.print("Podaj ulicę: ");
        String ulica = scanner.nextLine();
        System.out.print("Podaj miasto: ");
        String miasto = scanner.nextLine();
        System.out.print("Podaj kod pocztowy: ");
        String kod;
        while (true) {
            System.out.print("Podaj kod pocztowy (format xx-xxx): ");
            kod = scanner.nextLine().trim();
            if (kod.matches("\\d{2}-\\d{3}")) break;
            System.out.println("Błąd: Kod pocztowy musi być w formacie xx-xxx.");
        }
        String telefon;
        while (true) {
            System.out.print("Telefon (6 cyfr, opcjonalny, Enter aby pominąć): ");
            telefon = scanner.nextLine().trim();
            if (telefon.isBlank() || telefon.matches("\\d{9}")) break;
            System.out.println("Błąd: Telefon musi mieć dokładnie 6 cyfr lub pozostaw puste.");
        }


        String uid;
        while (true) {
            System.out.print("Podaj UID karty (tylko cyfry): ");
            uid = scanner.nextLine().trim();
            if (uid.matches("\\d+") && !ekstensja.containsKey(uid)) break;
            System.out.println("Błąd: UID musi być liczbą i nie może się powtarzać.");
        }

        Adres adres = new Adres(ulica, miasto, kod);
        Uzytkownik u = new Uzytkownik(imie, nazwisko, adres);
        u.setTelefon(telefon);

        new KartaDostepu(uid, u, LocalDate.now());
        System.out.println("Dodano użytkownika i kartę.");
    }

    public static void pokazEkstensje() {
        for (KartaDostepu karta : ekstensja.values()) {
            System.out.println("UID: " + karta.uid);
            System.out.println("Użytkownik: " + karta.uzytkownik.getPelneDane());
            System.out.println("Telefon: " + karta.uzytkownik.getTelefon().orElse("brak"));

            System.out.print("Dostępy: ");
            if (karta.dostepy.isEmpty()) {
                System.out.println("brak");
            } else {
                Map<String, List<String>> grupy = new LinkedHashMap<>();

                for (Dostep d : karta.dostepy) {
                    String budynek = d.getDrzwi().getBudynek().getNazwa();
                    String drzwiInfo = d.getDrzwi().getNazwa() + " (" + d.getDataPrzydzielenia() + ")";
                    grupy.computeIfAbsent(budynek, k -> new ArrayList<>()).add(drzwiInfo);
                }

                List<String> doWyswietlenia = new ArrayList<>();
                for (Map.Entry<String, List<String>> entry : grupy.entrySet()) {
                    doWyswietlenia.add(entry.getKey() + " [" + String.join(", ", entry.getValue()) + "]");
                }

                System.out.println(String.join(", ", doWyswietlenia));
            }

            System.out.println("Data wydania: " + karta.dataWydania);
            System.out.println("----------------------------------");
        }
    }

    public static void usunKarte(Scanner scanner) {
        if (ekstensja.isEmpty()) {
            System.out.println("Brak kart do usunięcia.");
            return;
        }

        List<KartaDostepu> listaKart = new ArrayList<>(ekstensja.values());
        for (int i = 0; i < listaKart.size(); i++) {
            KartaDostepu k = listaKart.get(i);
            System.out.println((i + 1) + ". UID: " + k.uid + ", Użytkownik: " + k.uzytkownik.getImieNazwisko());
        }

        System.out.print("Wybierz numer karty do usunięcia: ");
        String wyborKarty = scanner.nextLine();
        if (!wyborKarty.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }

        int index = Integer.parseInt(wyborKarty) - 1;
        if (index < 0 || index >= listaKart.size()) {
            System.out.println("Nieprawidłowy numer.");
            return;
        }

        KartaDostepu karta = listaKart.get(index);

        // usuniecie wszystkich asocjacji Dostep
        List<Dostep> kopiaDostepow = new ArrayList<>(karta.dostepy);
        for (Dostep d : kopiaDostepow) {
            d.getDrzwi().removeDostep(d);
            karta.dostepy.remove(d);
        }

        ekstensja.remove(karta.uid);
        System.out.println("Usunięto kartę UID: " + karta.uid + ", Użytkownik: " + karta.uzytkownik.getImieNazwisko());
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
    private final Map<String, Drzwi> drzwiMap = new HashMap<>(); // kwalifikowana

    public Budynek(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa() {
        return nazwa;
    }

    // Kwalifikowana asocjacja
    public void dodajDrzwi(String nazwaDrzwi) {
        if (!drzwiMap.containsKey(nazwaDrzwi)) {
            Drzwi noweDrzwi = Drzwi.stworz(nazwaDrzwi, this);
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

    public Map<String, Drzwi> getDrzwiMap() {
        return Collections.unmodifiableMap(drzwiMap);
    }

    public void usunDrzwi(String nazwa) {
        Drzwi drzwi = drzwiMap.remove(nazwa);
        if (drzwi != null) {
            drzwi.usunWszystkieDostepy(); // kompozycja – usuń powiązania
            Drzwi.usunZGlobalnegoZbioru(drzwi); // usuń z globalnego zbioru
        }
    }

    public void usunWszystkieDrzwi() {
        for (Drzwi drzwi : drzwiMap.values()) {
            drzwi.usunWszystkieDostepy();
            Drzwi.usunZGlobalnegoZbioru(drzwi); // usuń z globalnego zbioru
        }
        drzwiMap.clear();
    }

    public void pokazDrzwi() {
        if (drzwiMap.isEmpty()) {
            System.out.println("Brak drzwi w budynku " + nazwa);
        } else {
            int i = 1;
            for (Drzwi d : drzwiMap.values()) {
                System.out.println((i++) + ". " + d);
            }
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

class Osoba implements Serializable {
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

    //asocjacja automatycznie laczy obie strony
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

    public LocalDate getDataPrzydzielenia() {
        return dataPrzydzielenia;
    }

    @Override
    public String toString() {
        return "Dostęp do drzwi: " + drzwi.getNazwa() + " (budynek: " + drzwi.getBudynek().getNazwa() +
                ") | karta UID: " + karta.getUid() + " | od: " + dataPrzydzielenia;
    }
}

class Drzwi implements Serializable {
    private static final Set<Drzwi> wszystkieDrzwi = new HashSet<>(); // globalny zbiór do kompozycji

    private final String nazwa;
    private final Budynek budynek;
    private final List<Dostep> dostepy = new ArrayList<>(); // asocjacja zwykła

    //kompozycja
    private Drzwi(String nazwa, Budynek budynek) {
        this.nazwa = nazwa;
        this.budynek = budynek;
    }

    //jedyny sposób tworzenia drzwi
    public static Drzwi stworz(String nazwa, Budynek budynek) {
        if (nazwa == null || budynek == null) {
            throw new IllegalArgumentException("Nazwa i budynek nie mogą być null");
        }
        Drzwi noweDrzwi = new Drzwi(nazwa, budynek);
        if (wszystkieDrzwi.contains(noweDrzwi)) {
            throw new IllegalArgumentException("Drzwi już istnieją (nie mogą być współdzielone między budynkami)");
        }
        wszystkieDrzwi.add(noweDrzwi);
        return noweDrzwi;
    }

    public static void usunZGlobalnegoZbioru(Drzwi drzwi) {
        wszystkieDrzwi.remove(drzwi);
    }

    public String getNazwa() {
        return nazwa;
    }

    public Budynek getBudynek() {
        return budynek;
    }

    public List<Dostep> getDostepy() {
        return Collections.unmodifiableList(dostepy);
    }

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

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Drzwi drzwi = (Drzwi) o;
        return Objects.equals(nazwa, drzwi.nazwa) && Objects.equals(budynek, drzwi.budynek);
    }

    @Override
    public int hashCode() {
        return Objects.hash(nazwa, budynek);
    }
}

//
