import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {
        try {
            KartaDostepu.wczytajEkstensje("karty.txt");
            System.out.println("Dane zostały wczytane.");
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
            System.out.println("Błąd wczytywania danych.");
        }

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║   SYSTEM KART DOSTĘPU        ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. ➕ Dodaj dostęp           ║");
            System.out.println("║ 2. 👥 Wyświetl użytkowników  ║");
            System.out.println("║ 3. ❌ Usuń użytkownika       ║");
            System.out.println("║ 4. 🏢 Przydziel dostęp       ║");
            System.out.println("║ 5. 🚫 Odbierz dostęp         ║");
            System.out.println("║ 0. 🔚 Wyjście                ║");
            System.out.println("╚══════════════════════════════╝");
            System.out.print("Wybierz opcję: ");

            int opcja = scanner.nextInt();
            scanner.nextLine();

            switch (opcja) {
                case 1 -> {
                    String imie, nazwisko, ulica, miasto, kod, tel, uid;

                    while (true) {
                        System.out.print("Imię: ");
                        imie = scanner.nextLine();
                        if (imie.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) break;
                        System.out.println("Błąd: Imię może zawierać tylko litery.");
                    }

                    while (true) {
                        System.out.print("Nazwisko: ");
                        nazwisko = scanner.nextLine();
                        if (nazwisko.matches("[a-zA-ZąćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) break;
                        System.out.println("Błąd: Nazwisko może zawierać tylko litery.");
                    }

                    System.out.print("Ulica: ");
                    ulica = scanner.nextLine();
                    System.out.print("Miasto: ");
                    miasto = scanner.nextLine();

                    while (true) {
                        System.out.print("Kod pocztowy (bez myślnika): ");
                        kod = scanner.nextLine();
                        if (kod.matches("\\d{5}")) break;
                        System.out.println("Błąd: Kod pocztowy musi zawierać dokładnie 5 cyfr.");
                    }

                    while (true) {
                        System.out.print("Telefon (opcjonalny): ");
                        tel = scanner.nextLine();
                        if (tel.isBlank() || tel.matches("\\d+")) break;
                        System.out.println("Błąd: Telefon może zawierać tylko cyfry.");
                    }

                    Adres adres = new Adres(ulica, miasto, kod);
                    Uzytkownik u = new Uzytkownik(imie, nazwisko, adres);
                    if (!tel.isBlank()) u.setTelefon(tel);

                    while (true) {
                        System.out.print("Podaj UID karty (tylko cyfry): ");
                        uid = scanner.nextLine();
                        if (uid.matches("\\d+")) break;
                        System.out.println("Błąd: UID może zawierać tylko cyfry.");
                    }

                    new KartaDostepu(uid, u, LocalDate.now());  // Przekazujemy również datę wydania
                    System.out.println("Dodano użytkownika i kartę.");
                }
                case 2 -> KartaDostepu.pokazEkstensje();
                case 3 -> KartaDostepu.usunKarte();
                case 4 -> KartaDostepu.przydzielWejscie();
                case 5 -> KartaDostepu.odbierzWejscie();
                case 0 -> {
                    try {
                        KartaDostepu.zapiszEkstensje("karty.txt");
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    running = false;
                }
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

    public String toCSV() {
        return ulica + "," + miasto + "," + kodPocztowy;
    }

    @Override
    public String toString() {
        return ulica + ", " + kodPocztowy + " " + miasto;
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
}

class Uzytkownik implements Serializable {
    private final String imie;
    private final String nazwisko;
    private final Adres adres;
    private String telefon;

    public Uzytkownik(String imie, String nazwisko, Adres adres) {
        this.imie = imie;
        this.nazwisko = nazwisko;
        this.adres = adres;
    }

    public void setTelefon(String telefon) {
        this.telefon = telefon;
    }

    public Optional<String> getTelefon() {
        return Optional.ofNullable(telefon);
    }

    public String getImie() {
        return imie;
    }

    public String getNazwisko() {
        return nazwisko;
    }

    public Adres getAdres() {
        return adres;
    }

    @Override
    public String toString() {
        return getImie() + " " + getNazwisko() + ", Adres: " + adres + (telefon != null ? ", Tel: " + telefon : "");
    }
}

class KartaDostepu implements Serializable {
    private static final List<KartaDostepu> ekstensja = new ArrayList<>();
    private static int licznikKart = 0;
    private final String uid;
    private final Uzytkownik uzytkownik;
    private final List<ZdarzenieDostepu> historiaZdarzen = new ArrayList<>();
    private final List<String> dostepy = new ArrayList<>();
    private LocalDate dataWydania;

    public KartaDostepu(String uid, Uzytkownik uzytkownik, LocalDate dataWydania) {
        this.uid = uid;
        this.uzytkownik = uzytkownik;
        this.dataWydania = dataWydania;
        ekstensja.add(this);
        licznikKart++;
    }

    public static void pokazEkstensje() {
        for (KartaDostepu karta : ekstensja) {
            System.out.println(karta);
        }
    }

    public static void zapiszEkstensje(String plik) throws IOException {
        PrintWriter out = new PrintWriter(new FileWriter(plik));
        for (KartaDostepu karta : ekstensja) {
            out.println(karta.uid + ";" + karta.uzytkownik.getImie() + ";" + karta.uzytkownik.getNazwisko() + ";" + karta.uzytkownik.getAdres().toCSV() + ";" + karta.uzytkownik.getTelefon().orElse("") + ";" + String.join(",", karta.dostepy));
        }
        out.close();
    }

    public static void usunKarte() {
        System.out.println("Aktywne karty:");
        for (int i = 0; i < ekstensja.size(); i++) {
            KartaDostepu karta = ekstensja.get(i);
            System.out.println((i + 1) + ". UID: " + karta.uid + " | " + karta.uzytkownik.getNazwisko());
        }

        System.out.print("Wybierz numer użytkownika do usunięcia: ");
        Scanner scanner = new Scanner(System.in);
        int wybor = scanner.nextInt();

        if (wybor >= 1 && wybor <= ekstensja.size()) {
            KartaDostepu kartaDoUsuniecia = ekstensja.get(wybor - 1);
            ekstensja.remove(kartaDoUsuniecia);
            System.out.println("Usunięto kartę UID: " + kartaDoUsuniecia.uid + " | Użytkownik: " + kartaDoUsuniecia.uzytkownik.getNazwisko());
        } else {
            System.out.println("Niepoprawny wybór.");
        }
    }

    public static void przydzielWejscie() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wybierz użytkownika, któremu chcesz przydzielić dostęp do budynku:");

        for (int i = 0; i < ekstensja.size(); i++) {
            KartaDostepu karta = ekstensja.get(i);
            System.out.println((i + 1) + ". UID: " + karta.uid + " | " + karta.uzytkownik.getNazwisko());
            System.out.println("  Dostępy: " + (karta.dostepy.isEmpty() ? "Brak dostępów" : String.join(", ", karta.dostepy)));
        }

        int wybor = -1;
        while (wybor < 1 || wybor > ekstensja.size()) {
            System.out.println("Wybierz numer użytkownika: ");
            if (scanner.hasNextInt()) {
                wybor = scanner.nextInt();
                scanner.nextLine();
                if (wybor < 1 || wybor > ekstensja.size()) {
                    System.out.println("Niepoprawny numer użytkownika. Wybierz numer z listy.");
                }
            } else {
                System.out.println("Błąd: Wprowadź poprawną liczbę.");
                scanner.nextLine();
            }
        }

        KartaDostepu karta = ekstensja.get(wybor - 1);
        System.out.print("Dostęp do jakiego budynku chcesz przydzielić?");

        for (int i = 0; i < ListaBudynkow.budynki.size(); i++) {
            System.out.println((i + 1) + ". " + ListaBudynkow.budynki.get(i));
        }

        int wyborBudynek = -1;
        while (wyborBudynek < 1 || wyborBudynek > ListaBudynkow.budynki.size()) {
            System.out.print("Wybierz numer budynku: ");
            if (scanner.hasNextInt()) {
                wyborBudynek = scanner.nextInt();
                scanner.nextLine();
                if (wyborBudynek < 1 || wyborBudynek > ListaBudynkow.budynki.size()) {
                    System.out.println("Niepoprawny numer budynku. Wybierz numer z listy.");
                }
            } else {
                System.out.println("Błąd: Wprowadź poprawną liczbę.");
                scanner.nextLine();
            }
        }

        String budynek = ListaBudynkow.budynki.get(wyborBudynek - 1);

        if (karta.dostepy.contains(budynek)) {
            System.out.println("Błąd: Użytkownik już ma dostęp do " + budynek);
        } else {
            karta.dostepy.add(budynek);
            System.out.println("Przydzielono dostęp do " + budynek + " użytkownikowi " + karta.uzytkownik.getImie() + " " + karta.uzytkownik.getNazwisko());
        }
    }

    public static void odbierzWejscie() {
        Scanner scanner = new Scanner(System.in);
        System.out.println("Wybierz użytkownika, któremu chcesz odebrać dostęp:");

        for (int i = 0; i < ekstensja.size(); i++) {
            KartaDostepu karta = ekstensja.get(i);
            System.out.println((i + 1) + ". UID: " + karta.uid + " | " + karta.uzytkownik.getNazwisko());
            System.out.println("  Dostępy: " + (karta.dostepy.isEmpty() ? "Brak dostępów" : String.join(", ", karta.dostepy)));
        }

        int wybor = -1;
        while (wybor < 1 || wybor > ekstensja.size()) {
            System.out.print("Wybierz numer użytkownika: ");
            if (scanner.hasNextInt()) {
                wybor = scanner.nextInt();
                scanner.nextLine();
                if (wybor < 1 || wybor > ekstensja.size()) {
                    System.out.println("Niepoprawny numer użytkownika. Wybierz numer z listy.");
                }
            } else {
                System.out.println("Błąd: Wprowadź poprawną liczbę.");
                scanner.nextLine();
            }
        }

        KartaDostepu karta = ekstensja.get(wybor - 1);

        if (karta.dostepy.isEmpty()) {
            System.out.println("Brak dostępów do odebrania dla tej karty.");
            return;
        }

        System.out.println("Dostępy użytkownika " + karta.uzytkownik.getImie() + " " + karta.uzytkownik.getNazwisko() + " (UID: " + karta.uid + "):");

        for (int i = 0; i < karta.dostepy.size(); i++) {
            System.out.println((i + 1) + ". " + karta.dostepy.get(i));
        }

        int wyborDostepu = -1;
        while (wyborDostepu < 1 || wyborDostepu > karta.dostepy.size()) {
            System.out.print("Wybierz numer dostępu do usunięcia: ");
            if (scanner.hasNextInt()) {
                wyborDostepu = scanner.nextInt();
                if (wyborDostepu < 1 || wyborDostepu > karta.dostepy.size()) {
                    System.out.println("Niepoprawny wybór dostępu. Wybierz numer z listy.");
                }
            } else {
                System.out.println("Błąd: Wprowadź poprawną liczbę.");
                scanner.nextLine();
            }
        }

        String removed = karta.dostepy.remove(wyborDostepu - 1);
        System.out.println("Usunięto dostęp do: " + removed);
    }

    public static void wczytajEkstensje(String plik) throws IOException, ClassNotFoundException {
        BufferedReader reader = new BufferedReader(new FileReader(plik));
        String line;
        while ((line = reader.readLine()) != null) {
            String[] data = line.split(";");

            if (data.length < 6) {
                System.out.println("Błąd formatu danych w pliku. Pomijam linię: " + line);
                continue;
            }

            String uid = data[0];
            String imie = data[1];
            String nazwisko = data[2];

            String[] adresData = data[3].split(",");
            if (adresData.length != 3) {
                System.out.println("Błąd w danych adresu. Pomijam linię: " + line);
                continue;
            }
            Adres adres = new Adres(adresData[0], adresData[1], adresData[2]);

            Uzytkownik uzytkownik = new Uzytkownik(imie, nazwisko, adres);
            uzytkownik.setTelefon(data[4]);

            List<String> dostepy = new ArrayList<>();
            if (data.length > 5 && !data[5].isEmpty()) {
                String[] dostepyData = data[5].split(",");
                Collections.addAll(dostepy, dostepyData);
            }

            boolean kartaIstnieje = ekstensja.stream().anyMatch(k -> k.uid.equals(uid));
            if (kartaIstnieje) {
                System.out.println("Karta o UID " + uid + " już istnieje. Pomijam duplikat.");
                continue;
            }

            KartaDostepu karta = new KartaDostepu(uid, uzytkownik, LocalDate.now());
            karta.dostepy.addAll(dostepy);
        }
        reader.close();
    }

    @Override
    public String toString() {
        return "UID: " + uid + ", Użytkownik: " + uzytkownik + ", Dostępy: " + dostepy;
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

class ListaBudynkow {
    public static final List<String> budynki = Arrays.asList("Budynek A", "Budynek B", "Budynek C");
}
