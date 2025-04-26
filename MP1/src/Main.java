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

                case 4 -> KartaDostepu.przydzielDostepDoDrzwi(scanner);
                case 5 -> KartaDostepu.odbierzDostepOdDrzwi(scanner);

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
                        karta.getDostepy().removeIf(dostep -> {
                            String[] parts = dostep.split(":");
                            return parts.length == 2 && parts[0].equals(budynekDoUsuniecia.getNazwa());
                        });
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

                    boolean istniejaDrzwi = b.getDrzwi().stream()
                            .anyMatch(d -> d.getNazwa().equalsIgnoreCase(nazwaDrzwi));
                    if (istniejaDrzwi) {
                        System.out.println("Drzwi o tej nazwie już istnieją w tym budynku!");
                    } else {
                        b.dodajDrzwi(nazwaDrzwi);
                        System.out.println("Dodano drzwi do budynku.");
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

                    if (budynek.getDrzwi().isEmpty()) {
                        System.out.println("Brak drzwi w tym budynku.");
                        break;
                    }

                    System.out.println("Lista drzwi:");
                    for (int i = 0; i < budynek.getDrzwi().size(); i++) {
                        System.out.println((i + 1) + ". " + budynek.getDrzwi().get(i).getNazwa());
                    }

                    System.out.print("Wybierz numer drzwi do usunięcia: ");
                    String wyborDrzwi = scanner.nextLine();
                    if (!wyborDrzwi.matches("\\d+")) {
                        System.out.println("Nieprawidłowy wybór (podaj liczbę).");
                        break;
                    }
                    int wybDrzwi = Integer.parseInt(wyborDrzwi);

                    if (wybDrzwi < 1 || wybDrzwi > budynek.getDrzwi().size()) {
                        System.out.println("Nieprawidłowy wybór drzwi.");
                        break;
                    }

                    Drzwi drzwiDoUsuniecia = budynek.getDrzwi().get(wybDrzwi - 1);
                    String nazwaDrzwi = drzwiDoUsuniecia.getNazwa();

                    for (KartaDostepu karta : KartaDostepu.getEkstensja().values()) {
                        karta.getDostepy().remove(nazwaDrzwi);
                    }

                    budynek.getDrzwi().remove(drzwiDoUsuniecia);

                    System.out.println("Usunięto drzwi: " + nazwaDrzwi + " oraz dostęp do tych drzwi z wszystkich kart.");
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
    private final List<String> dostepy = new ArrayList<>();
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
            imie = scanner.nextLine();
            if (imie.matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) break;
            System.out.println("Błąd: Imię może zawierać tylko litery.");
        }

        String nazwisko;
        while (true) {
            System.out.print("Podaj nazwisko: ");
            nazwisko = scanner.nextLine();
            if (nazwisko.matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) break;
            System.out.println("Błąd: Nazwisko może zawierać tylko litery.");
        }

        System.out.print("Podaj ulicę: ");
        String ulica = scanner.nextLine();
        System.out.print("Podaj miasto: ");
        String miasto = scanner.nextLine();
        System.out.print("Podaj kod pocztowy: ");
        String kod ;
        while (true) {
            System.out.print("Podaj kod pocztowy (format xx-xxx): ");
            kod = scanner.nextLine();
            if (kod.matches("\\d{2}-\\d{3}")) break;
            System.out.println("Błąd: Kod pocztowy musi być w formacie xx-xxx.");
        }
        String telefon;
        while (true) {
            System.out.print("Telefon (6 cyfr, opcjonalny, Enter aby pominąć): ");
            telefon = scanner.nextLine();
            if (telefon.isBlank() || telefon.matches("\\d{9}")) break;
            System.out.println("Błąd: Telefon musi mieć dokładnie 6 cyfr lub pozostaw puste.");
        }




        String uid;
        while (true) {
            System.out.print("Podaj UID karty (tylko cyfry): ");
            uid = scanner.nextLine();
            if (uid.matches("\\d+") && !ekstensja.containsKey(uid)) break;
            System.out.println("Błąd: UID musi być liczbą i nie może się powtarzać.");
        }

        Adres adres = new Adres(ulica, miasto, kod);
        Uzytkownik u = new Uzytkownik(imie, nazwisko, adres);
        u.setTelefon(telefon); // Nawet jeśli puste, będzie OK.

        new KartaDostepu(uid, u, LocalDate.now());
        System.out.println("Dodano użytkownika i kartę.");
    }

    public static void dodajNowaKarte(String imie, String nazwisko, String ulica, String miasto, String kod, String telefon, String uid) {
        Adres adres = new Adres(ulica, miasto, kod);
        Uzytkownik u = new Uzytkownik(imie, nazwisko, adres);
        u.setTelefon(telefon);
        new KartaDostepu(uid, u, LocalDate.now());
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
                Map<String, List<String>> dostepyGrupowane = new LinkedHashMap<>();

                for (String dostep : karta.dostepy) {
                    String[] parts = dostep.split(":");
                    if (parts.length == 2) {
                        String budynek = parts[0];
                        String drzwi = parts[1];
                        dostepyGrupowane.computeIfAbsent(budynek, k -> new ArrayList<>()).add(drzwi);
                    }
                }

                for (String drzwiNazwa : karta.dostepy) {
                    for (Budynek budynek : ListaBudynekow.budynki) {
                        for (Drzwi drzwi : budynek.getDrzwi()) {
                            if (drzwi.getNazwa().equals(drzwiNazwa)) {
                                dostepyGrupowane.computeIfAbsent(budynek.getNazwa(), k -> new ArrayList<>())
                                        .add(drzwiNazwa);

                            }
                        }
                    }
                }


                List<String> listaWyswietlania = new ArrayList<>();
                for (Map.Entry<String, List<String>> entry : dostepyGrupowane.entrySet()) {
                    String budynek = entry.getKey();
                    List<String> drzwiWbudynku = entry.getValue();
                    listaWyswietlania.add(budynek + " [" + String.join(", ", drzwiWbudynku) + "]");
                }

                System.out.println(String.join(", ", listaWyswietlania));
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

        System.out.println("Lista aktywnych kart:");
        List<KartaDostepu> listaKart = new ArrayList<>(ekstensja.values());

        for (int i = 0; i < listaKart.size(); i++) {
            System.out.println((i + 1) + ". UID: " + listaKart.get(i).uid + ", Użytkownik: " + listaKart.get(i).uzytkownik.getImieNazwisko());
        }

        System.out.print("Wybierz numer karty do usunięcia: ");
        String wyborKarty = scanner.nextLine();
        if (!wyborKarty.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int wybor = Integer.parseInt(wyborKarty);

        if (wybor < 1 || wybor > listaKart.size()) {
            System.out.println("Nieprawidłowy wybór karty.");
            return;
        }

        KartaDostepu kartaDoUsuniecia = listaKart.get(wybor - 1);
        ekstensja.remove(kartaDoUsuniecia.uid);
        System.out.println("Usunięto kartę UID: " + kartaDoUsuniecia.uid + ", Użytkownik: " + kartaDoUsuniecia.uzytkownik.getPelneDane());
    }


    private final List<ZdarzenieDostepu> historiaZdarzen = new ArrayList<>();

    public List<ZdarzenieDostepu> getHistoriaZdarzen() {
        return historiaZdarzen;
    }


    public static void przydzielDostepDoDrzwi(Scanner scanner) {
        if (ListaBudynekow.budynki.isEmpty()) {
            System.out.println("Brak budynków do przypisania.");
            return;
        }

        boolean saJakiesDrzwi = ListaBudynekow.budynki.stream()
                .anyMatch(b -> !b.getDrzwi().isEmpty());

        if (!saJakiesDrzwi) {
            System.out.println("Brak drzwi do przydzielenia. Najpierw dodaj drzwi do budynków.");
            return;
        }

        System.out.println("Lista użytkowników:");
        List<KartaDostepu> listaKart = new ArrayList<>(ekstensja.values());
        for (int i = 0; i < listaKart.size(); i++) {
            System.out.println((i + 1) + ". UID: " + listaKart.get(i).uid + ", Użytkownik: " + listaKart.get(i).uzytkownik.getImieNazwisko());
        }

        System.out.print("Wybierz numer użytkownika: ");
        String wyborKarty = scanner.nextLine();
        if (!wyborKarty.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int numerKarty = Integer.parseInt(wyborKarty);
        if (numerKarty < 1 || numerKarty > listaKart.size()) {
            System.out.println("Nieprawidłowy wybór karty.");
            return;
        }
        KartaDostepu karta = listaKart.get(numerKarty - 1);

        System.out.println("Lista budynków:");
        for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
            System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
        }

        System.out.print("Wybierz numer budynku: ");
        String wyborBudynku = scanner.nextLine();
        if (!wyborBudynku.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int wybBud = Integer.parseInt(wyborBudynku);
        if (wybBud < 1 || wybBud > ListaBudynekow.budynki.size()) {
            System.out.println("Nieprawidłowy wybór budynku.");
            return;
        }
        Budynek b = ListaBudynekow.budynki.get(wybBud - 1);

        if (b.getDrzwi().isEmpty()) {
            System.out.println("Brak drzwi w tym budynku.");
            return;
        }

        System.out.println("0. Przydziel dostęp do wszystkich drzwi");
        for (int i = 0; i < b.getDrzwi().size(); i++) {
            System.out.println((i + 1) + ". " + b.getDrzwi().get(i).getNazwa());
        }

        System.out.print("Wybierz numer drzwi (lub 0 - wszystkie): ");
        String wyborDrzwi = scanner.nextLine();
        if (!wyborDrzwi.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int wybDrzwi = Integer.parseInt(wyborDrzwi);

        if (wybDrzwi == 0) {
            for (Drzwi drzwi : b.getDrzwi()) {
                String pelnaNazwaDrzwi = b.getNazwa() + ":" + drzwi.getNazwa();
                if (!karta.dostepy.contains(pelnaNazwaDrzwi)) {
                    karta.dostepy.add(pelnaNazwaDrzwi);
                    karta.historiaZdarzen.add(new ZdarzenieDostepu("Przydzielono dostęp", pelnaNazwaDrzwi));
                } else {
                    System.out.println("Użytkownik już ma dostęp do drzwi: " + drzwi.getNazwa());
                }
            }

            System.out.println("Przydzielono dostęp do wszystkich drzwi.");
        } else if (wybDrzwi >= 1 && wybDrzwi <= b.getDrzwi().size()) {
            String nazwaWybranychDrzwi = b.getDrzwi().get(wybDrzwi - 1).getNazwa();
            String pelnaNazwaDrzwi = b.getNazwa() + ":" + nazwaWybranychDrzwi;

            if (!karta.dostepy.contains(pelnaNazwaDrzwi)) {
                karta.dostepy.add(pelnaNazwaDrzwi);
                karta.historiaZdarzen.add(new ZdarzenieDostepu("Przydzielono dostęp", pelnaNazwaDrzwi));
                System.out.println("Przydzielono dostęp do wybranych drzwi.");
            } else {
                System.out.println("Użytkownik już ma dostęp do tych drzwi: " + nazwaWybranychDrzwi);
            }

        } else {
            System.out.println("Nieprawidłowy wybór drzwi.");
        }
    }

    public String getUid() {
        return uid;
    }

    public Uzytkownik getUzytkownik() {
        return uzytkownik;
    }

    public static void odbierzDostepOdDrzwi(Scanner scanner) {
        if (ListaBudynekow.budynki.isEmpty()) {
            System.out.println("Brak budynków.");
            return;
        }

        boolean saJakiesDrzwi = ListaBudynekow.budynki.stream()
                .anyMatch(b -> !b.getDrzwi().isEmpty());

        if (!saJakiesDrzwi) {
            System.out.println("Brak drzwi w systemie.");
            return;
        }

        System.out.println("Lista użytkowników:");
        List<KartaDostepu> listaKart = new ArrayList<>(ekstensja.values());
        for (int i = 0; i < listaKart.size(); i++) {
            System.out.println((i + 1) + ". UID: " + listaKart.get(i).uid + ", Użytkownik: " + listaKart.get(i).uzytkownik.getImieNazwisko());
        }
        System.out.print("Wybierz numer użytkownika: ");
        String wyborKarty = scanner.nextLine();
        if (!wyborKarty.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int numerKarty = Integer.parseInt(wyborKarty);
        if (numerKarty < 1 || numerKarty > listaKart.size()) {
            System.out.println("Nieprawidłowy wybór karty.");
            return;
        }
        KartaDostepu karta = listaKart.get(numerKarty - 1);

        System.out.println("Lista budynków:");
        for (int i = 0; i < ListaBudynekow.budynki.size(); i++) {
            System.out.println((i + 1) + ". " + ListaBudynekow.budynki.get(i).getNazwa());
        }
        System.out.print("Wybierz numer budynku: ");
        String wyborBudynku = scanner.nextLine();
        if (!wyborBudynku.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int wybBud = Integer.parseInt(wyborBudynku);
        if (wybBud < 1 || wybBud > ListaBudynekow.budynki.size()) {
            System.out.println("Nieprawidłowy wybór budynku.");
            return;
        }
        Budynek b = ListaBudynekow.budynki.get(wybBud - 1);

        if (b.getDrzwi().isEmpty()) {
            System.out.println("Brak drzwi w tym budynku.");
            return;
        }

        System.out.println("0. Odbierz dostęp do wszystkich drzwi");
        for (int i = 0; i < b.getDrzwi().size(); i++) {
            System.out.println((i + 1) + ". " + b.getDrzwi().get(i).getNazwa());
        }
        System.out.print("Wybierz numer drzwi (lub 0 - wszystkie): ");
        String wyborDrzwi = scanner.nextLine();
        if (!wyborDrzwi.matches("\\d+")) {
            System.out.println("Nieprawidłowy wybór (podaj liczbę).");
            return;
        }
        int wybDrzwi = Integer.parseInt(wyborDrzwi);

        if (wybDrzwi == 0) {
            boolean jakikolwiekUsuniety = false;
            for (Drzwi drzwi : b.getDrzwi()) {
                if (karta.dostepy.remove(drzwi.getNazwa())) {
                    jakikolwiekUsuniety = true;
                    karta.historiaZdarzen.add(new ZdarzenieDostepu("Odebrano dostęp", drzwi.getNazwa()));
                }
            }
            if (jakikolwiekUsuniety) {
                System.out.println("Odebrano dostęp do wszystkich drzwi.");
            } else {
                System.out.println("Użytkownik nie miał dostępu do żadnych drzwi w tym budynku.");
            }
        } else if (wybDrzwi >= 1 && wybDrzwi <= b.getDrzwi().size()) {
            String nazwaWybranychDrzwi = b.getDrzwi().get(wybDrzwi - 1).getNazwa();
            if (karta.dostepy.remove(nazwaWybranychDrzwi)) {
                karta.historiaZdarzen.add(new ZdarzenieDostepu("Odebrano dostęp", nazwaWybranychDrzwi));
                System.out.println("Odebrano dostęp do wybranych drzwi.");
            } else {
                System.out.println("Użytkownik nie miał dostępu do tych drzwi: " + nazwaWybranychDrzwi);
            }
        } else {
            System.out.println("Nieprawidłowy wybór drzwi.");
        }
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

    public List<String> getDostepy() {
        return dostepy;
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

class Drzwi implements Serializable {
    private final String nazwa;
    private final Budynek budynek;

    public Drzwi(String nazwa, Budynek budynek) {
        this.nazwa = nazwa;
        this.budynek = budynek;
    }

    @Override
    public String toString() {
        return "Drzwi: " + nazwa + " (Budynek: " + budynek.getNazwa() + ")";
    }

    public String getNazwa() {
        return nazwa;
    }
}

class Budynek implements Serializable {
    private final String nazwa;
    private final List<Drzwi> drzwi = new ArrayList<>();

    public Budynek(String nazwa) {
        this.nazwa = nazwa;
    }

    public String getNazwa() {
        return nazwa;
    }

    public void dodajDrzwi(String nazwaDrzwi) {
        drzwi.add(new Drzwi(nazwaDrzwi, this));
    }

    public List<Drzwi> getDrzwi() {
        return drzwi;
    }

    public void pokazDrzwi() {
        if (drzwi.isEmpty()) {
            System.out.println("Brak drzwi w budynku " + nazwa);
        } else {
            for (int i = 0; i < drzwi.size(); i++) {
                System.out.println((i + 1) + ". " + drzwi.get(i));
            }
        }
    }

    public void usunWszystkieDrzwi() {
        drzwi.clear();
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
