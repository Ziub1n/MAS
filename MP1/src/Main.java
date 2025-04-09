
import java.io.IOException;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.*;
import java.io.*;
import java.util.Scanner;

public class Main {
    public static void main(String[] args) {

        Scanner scanner = new Scanner(System.in);
        boolean running = true;

        while (running) {
            System.out.println("\n╔══════════════════════════════╗");
            System.out.println("║   SYSTEM KART DOSTĘPU       ║");
            System.out.println("╠══════════════════════════════╣");
            System.out.println("║ 1. ➕ Dodaj dostęp           ║");
            System.out.println("║ 2. 👥 Wyświetl użytkowników ║");
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

                    new KartaDostepu(uid, u);
                    System.out.println("Dodano użytkownika i kartę.");
                }
                case 2 -> KartaDostepu.pokazEkstensje();

                case 3 -> {
                    System.out.print("Podaj UID karty do usunięcia: ");
                    String uid = scanner.nextLine();
                    KartaDostepu.usunKarte(uid);
                }

                case 4 -> {
                    System.out.print("Podaj UID karty: ");
                    String uid = scanner.nextLine();
                    System.out.println("Dostępne budynki: " + ListaBudynkow.budynki);
                    System.out.print("Podaj nazwę budynku: ");
                    String wejscie = scanner.nextLine();
                    KartaDostepu.przydzielWejscie(uid, wejscie);
                }

                case 5 -> {
                    System.out.print("Podaj UID karty: ");
                    String uid = scanner.nextLine();
                    KartaDostepu.odbierzWejscie(uid);
                }

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
    private String ulica;
    private String miasto;
    private String kodPocztowy;

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
    private String imie;
    private String nazwisko;
    private Adres adres;
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
        return getImie() +" "+ getNazwisko()+ ", Adres: " + adres + (telefon != null ? ", Tel: " + telefon : "");
    }

}


class KartaDostepu implements Serializable {
    private static List<KartaDostepu> ekstensja = new ArrayList<>();
    private static int licznikKart = 0;

    private String uid;
    private Uzytkownik uzytkownik;
    private List<ZdarzenieDostepu> historiaZdarzen = new ArrayList<>();
    private List<String> dostepy = new ArrayList<>();

    public KartaDostepu(String uid, Uzytkownik uzytkownik) {
        this.uid = uid;
        this.uzytkownik = uzytkownik;
        ekstensja.add(this);
        licznikKart++;
    }

    public void dodajZdarzenie(ZdarzenieDostepu zdarzenie) {
        historiaZdarzen.add(zdarzenie);
    }

    public int getLiczbaZdarzen() {
        return historiaZdarzen.size();
    }

    public static int getLiczbaKart() {
        return licznikKart;
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


    public static void usunKarte(String uid) {

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


    public static void przydzielWejscie(String uid, String wejscie) {
        if (!ListaBudynkow.budynki.contains(wejscie)) {
            System.out.println("Nie można przydzielić – budynek nie istnieje.");
            return;
        }
        for (KartaDostepu karta : ekstensja) {
            if (karta.uid.equals(uid)) {
                if (karta.dostepy.contains(wejscie)) {
                    System.out.println("Użytkownik ma już dostęp do " + wejscie);
                } else {
                    karta.dostepy.add(wejscie);
                    System.out.println("Przydzielono dostęp do " + wejscie);
                }
                return;
            }
        }
        System.out.println("Nie znaleziono karty o UID: " + uid);
    }

    public static void odbierzWejscie(String uid) {
        for (KartaDostepu karta : ekstensja) {
            if (karta.uid.equals(uid)) {
                if (karta.dostepy.isEmpty()) {
                    System.out.println("Brak dostępów do odebrania dla tej karty.");
                    return;
                }
                System.out.println("Dostępy użytkownika " + karta.uzytkownik.getImie()+" "+karta.uzytkownik.getNazwisko() + " (UID: " + karta.uid + "):");
                for (int i = 0; i < karta.dostepy.size(); i++) {
                    System.out.println((i + 1) + ". " + karta.dostepy.get(i));
                }
                System.out.print("Wybierz numer dostępu do usunięcia: ");
                Scanner scanner = new Scanner(System.in);
                int wybor = scanner.nextInt();
                if (wybor >= 1 && wybor <= karta.dostepy.size()) {
                    String removed = karta.dostepy.remove(wybor - 1);
                    System.out.println("Usunięto dostęp do: " + removed);
                } else {
                    System.out.println("Niepoprawny wybór.");
                }
                return;
            }
        }
        System.out.println("Nie znaleziono karty o UID: " + uid);
    }

    @Override
    public String toString() {
        return "UID: " + uid + ", Użytkownik: " + uzytkownik + ", Dostępy: " + dostepy;
    }
}


class ZdarzenieDostepu implements Serializable {
    private LocalDateTime data;
    private String typ;
    private String lokalizacja;

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