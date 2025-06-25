import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class MainGUI {
    private DefaultComboBoxModel<String> budynekModel = new DefaultComboBoxModel<>();
    private DefaultComboBoxModel<String> kartaModel = new DefaultComboBoxModel<>();
    private JComboBox<String> kartaCombo = new JComboBox<>();
    private JComboBox<String> budynekCombo = new JComboBox<>();
    private JComboBox<String> drzwiCombo = new JComboBox<>();
    private JComboBox<String> kartaComboUprawnienia = new JComboBox<>();
    private JComboBox<String> kartaComboRole = new JComboBox<>();
    private JComboBox<String> kartaComboHistoria = new JComboBox<>();
    private DefaultListModel<String> uzytkownikListaModel = new DefaultListModel<>();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            try {

                UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());

                Color pastelBlue = new Color(204, 229, 255);
                Color pastelGreen = new Color(204, 255, 229);
                Color pastelPink = new Color(255, 204, 229);
                Color pastelLavender = new Color(230, 230, 250);
                Color pastelPurple = new Color(220, 200, 255);
                Color softGray = new Color(240, 240, 240);

                Font baseFont = new Font("Segoe UI", Font.PLAIN, 14);


                UIManager.put("Panel.background", pastelLavender);
                UIManager.put("Label.foreground", Color.DARK_GRAY);
                UIManager.put("Label.font", baseFont);
                UIManager.put("Button.background", pastelBlue);
                UIManager.put("Button.foreground", Color.DARK_GRAY);
                UIManager.put("Button.font", baseFont);
                UIManager.put("Button.select", pastelPurple);
                UIManager.put("TextField.background", Color.WHITE);
                UIManager.put("TextField.foreground", Color.DARK_GRAY);
                UIManager.put("TextField.font", baseFont);
                UIManager.put("TextField.border", BorderFactory.createLineBorder(pastelBlue));
                UIManager.put("ComboBox.background", Color.WHITE);
                UIManager.put("ComboBox.foreground", Color.DARK_GRAY);
                UIManager.put("ComboBox.font", baseFont);
                UIManager.put("ComboBox.selectionBackground", pastelPink);
                UIManager.put("ComboBox.selectionForeground", Color.BLACK);
                UIManager.put("ScrollPane.background", pastelLavender);
                UIManager.put("ScrollPane.border", BorderFactory.createLineBorder(pastelPurple));
                UIManager.put("TabbedPane.background", pastelBlue);
                UIManager.put("TabbedPane.foreground", Color.DARK_GRAY);
                UIManager.put("TabbedPane.selected", pastelPink);
                UIManager.put("TabbedPane.font", baseFont);
                UIManager.put("List.background", Color.WHITE);
                UIManager.put("List.foreground", Color.DARK_GRAY);
                UIManager.put("List.selectionBackground", pastelGreen);
                UIManager.put("List.selectionForeground", Color.BLACK);
                UIManager.put("List.font", baseFont);
                UIManager.put("TextArea.background", softGray);
                UIManager.put("TextArea.foreground", Color.DARK_GRAY);
                UIManager.put("TextArea.font", baseFont);
                UIManager.put("OptionPane.messageForeground", Color.DARK_GRAY);
                UIManager.put("OptionPane.font", baseFont);


                UIManager.put("Button.border", BorderFactory.createCompoundBorder(
                        BorderFactory.createLineBorder(new Color(180, 180, 255)),
                        BorderFactory.createEmptyBorder(5, 15, 5, 15)
                ));

            } catch (Exception ex) {
                System.err.println("Nie udało się ustawić LookAndFeel: " + ex.getMessage());
            }


            try {
                KartaDostepu.wczytajEkstensje("karty.dat");
                ListaBudynekow.wczytajBudynki("budynki.dat");
                System.out.println("Dane wczytane.");
            } catch (Exception e) {
                System.out.println("Błąd wczytywania danych: " + e.getMessage());
            }


            MainGUI gui = new MainGUI();
            JFrame frame = gui.createAndShowGUI();
            gui.odswiezDanePoWczytaniu();


            frame.addWindowListener(new WindowAdapter() {
                @Override
                public void windowClosing(WindowEvent e) {
                    try {
                        KartaDostepu.zapiszEkstensje("karty.dat");
                        ListaBudynekow.zapiszBudynki("budynki.dat");
                        System.out.println("Dane zapisane.");
                    } catch (Exception ex) {
                        System.out.println("Błąd zapisu danych: " + ex.getMessage());
                    }
                }
            });
        });
    }


    private void odswiezDanePoWczytaniu() {
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            k.odswiezDostepyAutomatycznie();
        }
        odswiezKartyUID(kartaCombo);
        odswiezUIDWUprawnienia();
        odswiezUIDWRola();
        odswiezUIDWHistoria();
        odswiezListeUzytkownikow();
        odswiezBudynekCombo();
    }


    private JPanel createUprawnieniaPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        kartaComboUprawnienia = new JComboBox<>();
        odswiezUIDWUprawnienia();


        JPanel top = new JPanel();
        top.add(new JLabel("Karta UID:"));
        top.add(kartaComboUprawnienia);

        JPanel srodek = new JPanel(new GridLayout(0, 1));
        JCheckBox[] checkboxes = new JCheckBox[TypUprawnieniaSpecjalne.values().length];
        for (int i = 0; i < checkboxes.length; i++) {
            checkboxes[i] = new JCheckBox(TypUprawnieniaSpecjalne.values()[i].name());
            srodek.add(checkboxes[i]);
        }

        JButton zapiszBtn = new JButton("Zapisz zmiany");
        zapiszBtn.addActionListener(e -> {
            String uid = (String) kartaComboUprawnienia.getSelectedItem();
            if (uid == null) return;
            KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
            Uzytkownik u = karta.getUzytkownik();
            u.getUprawnieniaSpecjalne().clear();
            for (int i = 0; i < checkboxes.length; i++) {
                if (checkboxes[i].isSelected()) {
                    u.dodajUprawnienie(TypUprawnieniaSpecjalne.values()[i]);
                }
            }
            JOptionPane.showMessageDialog(panel, "Zaktualizowano uprawnienia użytkownika.");
            odswiezListeUzytkownikow();
        });

        kartaComboUprawnienia.addActionListener(e -> {
            String uid = (String) kartaComboUprawnienia.getSelectedItem();
            if (uid != null) {
                KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
                Uzytkownik u = karta.getUzytkownik();
                for (int i = 0; i < checkboxes.length; i++) {
                    checkboxes[i].setSelected(u.getUprawnieniaSpecjalne().contains(TypUprawnieniaSpecjalne.values()[i]));
                }
            }
        });

        if (kartaComboUprawnienia.getItemCount() > 0)
            kartaComboUprawnienia.setSelectedIndex(0);

        panel.add(top, BorderLayout.NORTH);
        panel.add(srodek, BorderLayout.CENTER);
        panel.add(zapiszBtn, BorderLayout.SOUTH);

        return panel;
    }


    private JPanel createRolePanel() {
        JPanel panel = new JPanel(new BorderLayout());

        kartaComboRole = new JComboBox<>();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaComboRole.addItem(k.getUid());
        }

        JPanel top = new JPanel();
        top.add(new JLabel("Karta UID:"));
        top.add(kartaComboRole);

        JPanel srodek = new JPanel(new GridLayout(0, 1));
        JRadioButton[] radioButtons = new JRadioButton[TypRoli.values().length];
        ButtonGroup group = new ButtonGroup();

        for (int i = 0; i < TypRoli.values().length; i++) {
            radioButtons[i] = new JRadioButton(TypRoli.values()[i].name());
            group.add(radioButtons[i]);
            srodek.add(radioButtons[i]);
        }

        JButton zapiszBtn = new JButton("Zapisz zmiany");
        zapiszBtn.addActionListener(e -> {
            String uid = (String) kartaComboRole.getSelectedItem();
            if (uid == null) return;

            KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
            Uzytkownik u = karta.getUzytkownik();

            u.wyczyscRole();

            for (int i = 0; i < radioButtons.length; i++) {
                if (radioButtons[i].isSelected()) {
                    u.dodajRole(TypRoli.values()[i]);
                    break;
                }
            }

            odswiezListeUzytkownikow();
            JOptionPane.showMessageDialog(panel, "Zaktualizowano rolę użytkownika.");
        });

        kartaComboRole.addActionListener(e -> {
            String uid = (String) kartaComboRole.getSelectedItem();
            if (uid != null) {
                KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
                Uzytkownik u = karta.getUzytkownik();

                group.clearSelection();
                for (int i = 0; i < radioButtons.length; i++) {
                    if (u.getRole().contains(TypRoli.values()[i])) {
                        radioButtons[i].setSelected(true);
                        break;
                    }
                }
            }
        });

        if (kartaComboRole.getItemCount() > 0)
            kartaComboRole.setSelectedIndex(0);

        panel.add(top, BorderLayout.NORTH);
        panel.add(srodek, BorderLayout.CENTER);
        panel.add(zapiszBtn, BorderLayout.SOUTH);

        return panel;
    }


    private JFrame createAndShowGUI() {
        JFrame frame = new JFrame("System Kart Dostępu");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(1000, 700);

        JTabbedPane tabbedPane = new JTabbedPane();
        tabbedPane.addTab("Użytkownicy", createKartyPanel());
        tabbedPane.addTab("Budynki i drzwi", createBudynkiDrzwiPanel());
        tabbedPane.addTab("Zarządzanie dostępem", createDostepPanel());
        tabbedPane.addTab("Historia", createHistoriaPanel());
        tabbedPane.addTab("Uprawnienia", createUprawnieniaPanel());
        tabbedPane.addTab("Role", createRolePanel());

        frame.getContentPane().add(tabbedPane);
        frame.setVisible(true);
        return frame;
    }

    private JPanel createBudynkiDrzwiPanel() {
        JPanel panel = new JPanel(new GridLayout(2, 1));
        panel.add(createDodajUsunBudynekPanel());
        panel.add(createDodajUsunDrzwiPanel());
        return panel;
    }


    private JPanel createDodajUsunDrzwiPanel() {
        JPanel panel = new JPanel(new BorderLayout());

        JPanel topPanel = new JPanel(new FlowLayout());
        JComboBox<String> budynekCombo = new JComboBox<>(budynekModel);
        topPanel.add(new JLabel("Budynek:"));
        topPanel.add(budynekCombo);

        DefaultListModel<String> drzwiModel = new DefaultListModel<>();
        JList<String> drzwiList = new JList<>(drzwiModel);
        JScrollPane drzwiScroll = new JScrollPane(drzwiList);

        JPanel bottomPanel = new JPanel(new FlowLayout());
        JTextField drzwiField = new JTextField(10);
        JComboBox<TypDrzwi> typDrzwiCombo = new JComboBox<>(TypDrzwi.values());
        JButton dodajBtn = new JButton("Dodaj drzwi");
        JButton usunBtn = new JButton("Usuń drzwi");

        bottomPanel.add(new JLabel("Nazwa drzwi:"));
        bottomPanel.add(drzwiField);
        bottomPanel.add(new JLabel("Typ:"));
        bottomPanel.add(typDrzwiCombo);
        bottomPanel.add(dodajBtn);
        bottomPanel.add(usunBtn);

        ActionListener updateDrzwiList = e -> {
            drzwiModel.clear();
            String selected = (String) budynekCombo.getSelectedItem();
            if (selected != null) {
                ListaBudynekow.budynki.stream()
                        .filter(b -> b.getNazwa().equals(selected))
                        .findFirst()
                        .ifPresent(b -> b.getDrzwi().forEach(d -> drzwiModel.addElement(d.getNazwa() + " [" + d.getTyp().name() + "]")));

            }
        };
        budynekCombo.addActionListener(updateDrzwiList);
        updateDrzwiList.actionPerformed(null);

        dodajBtn.addActionListener(e -> {
            String budynekNazwa = (String) budynekCombo.getSelectedItem();
            String nazwaDrzwi = drzwiField.getText().trim();
            TypDrzwi typ = (TypDrzwi) typDrzwiCombo.getSelectedItem();

            if (nazwaDrzwi.isEmpty()) {
                JOptionPane.showMessageDialog(panel, "Nazwa drzwi nie może być pusta");
                return;
            }

            ListaBudynekow.budynki.stream()
                    .filter(b -> b.getNazwa().equals(budynekNazwa))
                    .findFirst()
                    .ifPresent(b -> {
                        if (b.getDrzwi(nazwaDrzwi) == null) {
                            b.dodajDrzwi(nazwaDrzwi, typ);
                            drzwiModel.addElement(nazwaDrzwi + " [" + typ.name() + "]");
                            odswiezDrzwiCombo();
                            JOptionPane.showMessageDialog(panel, "Dodano drzwi: " + nazwaDrzwi);
                        } else {
                            JOptionPane.showMessageDialog(panel, "Drzwi o tej nazwie już istnieją");
                        }
                    });
        });

        usunBtn.addActionListener(e -> {
            String budynekNazwa = (String) budynekCombo.getSelectedItem();
            String drzwiNazwa = drzwiList.getSelectedValue().split(" \\[")[0];
            if (drzwiNazwa == null) {
                JOptionPane.showMessageDialog(panel, "Wybierz drzwi do usunięcia");
                return;
            }

            ListaBudynekow.budynki.stream()
                    .filter(b -> b.getNazwa().equals(budynekNazwa))
                    .findFirst()
                    .ifPresent(b -> {
                        Drzwi drzwi = b.getDrzwi(drzwiNazwa);
                        if (drzwi != null) {
                            drzwi.usunWszystkieDostepy();
                            b.usunDrzwi(drzwiNazwa);
                            drzwiModel.removeElement(drzwiNazwa);
                            odswiezDrzwiCombo();
                            JOptionPane.showMessageDialog(panel, "Usunięto drzwi: " + drzwiNazwa);
                        }
                    });
        });

        panel.add(topPanel, BorderLayout.NORTH);
        panel.add(drzwiScroll, BorderLayout.CENTER);
        panel.add(bottomPanel, BorderLayout.SOUTH);
        return panel;
    }

    private void odswiezListeUzytkownikow() {
        uzytkownikListaModel.clear();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            Uzytkownik u = k.getUzytkownik();
            LocalDate dataWaznosci = u.getDataWaznosci(k.getDataWydania());
            boolean nieaktywna = LocalDate.now().isAfter(dataWaznosci);

            String uprawnieniaStr = u.getUprawnieniaSpecjalne().isEmpty()
                    ? ""
                    : " [" + u.getUprawnieniaSpecjalne().stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")) + "]";

            String status = nieaktywna ? " [NIEAKTYWNA]" : "";

            uzytkownikListaModel.addElement(
                    k.getUid() + " - " + u.getImieNazwisko() + " (" + u.getTyp() + ")" + status + uprawnieniaStr);

        }

    }

    private void odswiezUIDWUprawnienia() {
        kartaComboUprawnienia.removeAllItems();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaComboUprawnienia.addItem(k.getUid());
        }
    }

    private void odswiezUIDWRola() {
        kartaComboRole.removeAllItems();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaComboRole.addItem(k.getUid());
        }
    }

    private void odswiezUIDWHistoria() {
        kartaComboHistoria.removeAllItems();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaComboHistoria.addItem(k.getUid());
        }
    }


    private JPanel createKartyPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        panel.add(createDodajKartePanel(), BorderLayout.NORTH);

        JList<String> listaUzytkownikow = new JList<>(uzytkownikListaModel);
        listaUzytkownikow.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        listaUzytkownikow.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent evt) {
                if (evt.getClickCount() == 2) {
                    int index = listaUzytkownikow.locationToIndex(evt.getPoint());
                    String selected = listaUzytkownikow.getModel().getElementAt(index);
                    String uid = selected.split(" ")[0];
                    KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
                    if (karta != null) {
                        showUserDetailsDialog(karta);
                    }
                }
            }
        });

        JScrollPane scrollPane = new JScrollPane(listaUzytkownikow);
        panel.add(scrollPane, BorderLayout.CENTER);

        return panel;
    }

    private JPanel createDodajUsunBudynekPanel() {
        JPanel panel = new JPanel(new FlowLayout());

        JTextField budynekField = new JTextField(15);
        JButton dodajBtn = new JButton("Dodaj budynek");
        JComboBox<String> budynekCombo = new JComboBox<>(budynekModel);
        JButton usunBtn = new JButton("Usuń budynek");

        dodajBtn.addActionListener(e -> {
            String nazwa = budynekField.getText().trim();
            if (!nazwa.isEmpty() && ListaBudynekow.budynki.stream().noneMatch(b -> b.getNazwa().equalsIgnoreCase(nazwa))) {
                ListaBudynekow.budynki.add(new Budynek(nazwa));
                budynekModel.addElement(nazwa);
                odswiezBudynekCombo();
                JOptionPane.showMessageDialog(panel, "Dodano budynek: " + nazwa);
            }
        });

        usunBtn.addActionListener(e -> {
            String selected = (String) budynekCombo.getSelectedItem();
            if (selected != null) {

                Budynek budynek = ListaBudynekow.budynki.stream()
                        .filter(b -> b.getNazwa().equals(selected))
                        .findFirst()
                        .orElse(null);

                if (budynek != null) {

                    for (Drzwi drzwi : budynek.getDrzwi()) {
                        drzwi.usunWszystkieDostepy();
                    }

                    ListaBudynekow.budynki.remove(budynek);
                    budynekModel.removeElement(selected);
                    odswiezBudynekCombo();
                    odswiezListeUzytkownikow();
                    JOptionPane.showMessageDialog(panel, "Usunięto budynek: " + selected);
                }
            }
        });

        panel.add(new JLabel("Nazwa budynku:"));
        panel.add(budynekField);
        panel.add(dodajBtn);
        panel.add(budynekCombo);
        panel.add(usunBtn);
        return panel;
    }

    private JPanel createDodajKartePanel() {
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField imieField = new JTextField();
        JTextField nazwiskoField = new JTextField();
        JTextField ulicaField = new JTextField();
        JTextField miastoField = new JTextField();
        JTextField kodField = new JTextField();
        JTextField telefonField = new JTextField();
        JTextField uidField = new JTextField();

        String[] role = {"PRAKTYKANT", "PRACOWNIK", "ADMIN", "TYMCZASOWY"};
        JComboBox<String> rolaCombo = new JComboBox<>(role);

        JButton dodajButton = new JButton("Dodaj kartę");
        dodajButton.addActionListener(e -> {
            String imie = imieField.getText().trim();
            String nazwisko = nazwiskoField.getText().trim();
            String ulica = ulicaField.getText().trim();
            String miasto = miastoField.getText().trim();
            String kod = kodField.getText().trim();
            String telefon = telefonField.getText().trim();
            String uid = uidField.getText().trim();
            String wybranaEtykieta = (String) rolaCombo.getSelectedItem();
            TypRoli wybranaRola = TypRoli.fromLabel(wybranaEtykieta);
            if (!imie.matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) {
                JOptionPane.showMessageDialog(panel, "Imię może zawierać tylko litery.");
                return;
            }
            if (!nazwisko.matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+")) {
                JOptionPane.showMessageDialog(panel, "Nazwisko może zawierać tylko litery.");
                return;
            }
            if (!kod.matches("\\d{2}-\\d{3}")) {
                JOptionPane.showMessageDialog(panel, "Kod pocztowy musi być w formacie xx-xxx.");
                return;
            }
            if (!telefon.isBlank() && !telefon.matches("\\d{9}")) {
                JOptionPane.showMessageDialog(panel, "Telefon musi mieć dokładnie 9 cyfr lub być pusty.");
                return;
            }
            if (!uid.matches("\\d+")) {
                JOptionPane.showMessageDialog(panel, "UID musi być liczbą.");
                return;
            }
            if (KartaDostepu.getEkstensja().containsKey(uid)) {
                JOptionPane.showMessageDialog(panel, "UID już istnieje.");
                return;
            }

            try {
                KartaDostepu.dodajNowaKarte(imie, nazwisko, ulica, miasto, kod, telefon, uid);
                Uzytkownik u = KartaDostepu.getEkstensja().get(uid).getUzytkownik();
                u.dodajRole(wybranaRola);
                uzytkownikListaModel.addElement(uid + " - " + u.getImieNazwisko() + " (" + u.getTyp() + ")");
                kartaModel.addElement(uid);
                odswiezKartyUID(kartaCombo);
                odswiezUIDWUprawnienia();
                odswiezUIDWRola();
                odswiezUIDWHistoria();
                JOptionPane.showMessageDialog(panel, "Dodano użytkownika i kartę.");
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(panel, "Błąd: " + ex.getMessage());
            }
        });

        panel.add(new JLabel("Imię:"));
        panel.add(imieField);
        panel.add(new JLabel("Nazwisko:"));
        panel.add(nazwiskoField);
        panel.add(new JLabel("Ulica:"));
        panel.add(ulicaField);
        panel.add(new JLabel("Miasto:"));
        panel.add(miastoField);
        panel.add(new JLabel("Kod pocztowy:"));
        panel.add(kodField);
        panel.add(new JLabel("Telefon:"));
        panel.add(telefonField);
        panel.add(new JLabel("UID karty:"));
        panel.add(uidField);
        panel.add(new JLabel("Rola:"));
        panel.add(rolaCombo);
        panel.add(new JLabel(""));
        panel.add(dodajButton);

        return panel;
    }

    private void odswiezDrzwiCombo() {
        drzwiCombo.removeAllItems();
        String selected = (String) budynekCombo.getSelectedItem();
        if (selected != null) {
            for (Budynek b : ListaBudynekow.budynki) {
                if (b.getNazwa().equals(selected)) {
                    for (Drzwi d : b.getDrzwi()) {
                        drzwiCombo.addItem(d.getNazwa());
                    }
                }
            }
        }
    }


    private void odswiezBudynekCombo() {
        budynekModel.removeAllElements();
        budynekCombo.removeAllItems();

        for (Budynek b : ListaBudynekow.budynki) {
            budynekModel.addElement(b.getNazwa());
            budynekCombo.addItem(b.getNazwa());
        }
        odswiezDrzwiCombo();
    }


    private void showUserDetailsDialog(KartaDostepu karta) {
        karta.odswiezDostepyAutomatycznie();
        Uzytkownik u = karta.getUzytkownik();
        LocalDate dataWaznosci = u.getDataWaznosci(karta.getDataWydania());

        StringBuilder sb = new StringBuilder();
        sb.append("UID: ").append(karta.getUid()).append("\n");
        sb.append("Użytkownik: ").append(u.getPelneDane()).append("\n");
        sb.append("Telefon: ").append(u.getTelefon().orElse("brak")).append("\n");
        sb.append("Typ: ").append(u.getTyp()).append("\n");
        sb.append("Uprawnienia specjalne: ");
        Set<TypUprawnieniaSpecjalne> uprawnienia = u.getUprawnieniaSpecjalne();
        if (uprawnienia.isEmpty()) {
            sb.append("brak");
        } else {
            sb.append(uprawnienia.stream()
                    .map(Enum::name)
                    .collect(Collectors.joining(", ")));
        }
        sb.append("\n");
        sb.append("Data ważności: ").append(dataWaznosci);
        if (LocalDate.now().isAfter(dataWaznosci)) {
            sb.append(" [NIEAKTYWNA]");
        }
        sb.append("\nDostępy:\n");

        if (karta.getDostepy().isEmpty()) {
            sb.append(" - brak");
        } else {
            for (Dostep dostep : karta.getDostepy()) {
                sb.append(" - ")
                        .append(dostep.getDrzwi().getBudynek().getNazwa())
                        .append(":")
                        .append(dostep.getDrzwi().getNazwa())
                        .append("\n");
            }
        }

        JTextArea daneArea = new JTextArea(sb.toString());
        daneArea.setEditable(false);
        daneArea.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10));

        JButton editBtn = new JButton("Edytuj");
        JButton deleteBtn = new JButton("Usuń");

        JPanel buttonPanel = new JPanel();
        buttonPanel.add(editBtn);
        buttonPanel.add(deleteBtn);

        JPanel mainPanel = new JPanel(new BorderLayout());
        mainPanel.add(new JScrollPane(daneArea), BorderLayout.CENTER);
        mainPanel.add(buttonPanel, BorderLayout.SOUTH);

        JDialog dialog = new JDialog((JFrame) null, "Dane użytkownika", true);
        dialog.setContentPane(mainPanel);
        dialog.setSize(400, 300);
        dialog.setLocationRelativeTo(null);


        editBtn.addActionListener(e -> {
            dialog.dispose();
            odswiezListeUzytkownikow();
            showEditUserDialog(karta);
        });

        deleteBtn.addActionListener(e -> {
            int confirm = JOptionPane.showConfirmDialog(dialog, "Czy na pewno usunąć tego użytkownika?", "Potwierdź", JOptionPane.YES_NO_OPTION);
            if (confirm == JOptionPane.YES_OPTION) {
                KartaDostepu.getEkstensja().remove(karta.getUid());
                for (int i = 0; i < uzytkownikListaModel.size(); i++) {
                    if (uzytkownikListaModel.get(i).startsWith(karta.getUid())) {
                        uzytkownikListaModel.remove(i);
                        break;
                    }
                }
                kartaModel.removeElement(karta.getUid());

                odswiezKartyUID(kartaCombo);
                odswiezUIDWUprawnienia();
                odswiezUIDWRola();
                odswiezUIDWHistoria();

                dialog.dispose();
                JOptionPane.showMessageDialog(null, "Użytkownik został usunięty");
            }
        });

        dialog.setVisible(true);
    }

    private void odswiezKartyUID(JComboBox<String> kartaCombo) {
        kartaCombo.removeAllItems();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaCombo.addItem(k.getUid());
        }
    }

    private void showEditUserDialog(KartaDostepu karta) {
        Uzytkownik u = karta.getUzytkownik();
        JPanel panel = new JPanel(new GridLayout(0, 2));

        JTextField imie = new JTextField(u.imie);
        JTextField nazwisko = new JTextField(u.nazwisko);
        JTextField telefon = new JTextField(u.getTelefon().orElse(""));

        panel.add(new JLabel("Imię:"));
        panel.add(imie);
        panel.add(new JLabel("Nazwisko:"));
        panel.add(nazwisko);
        panel.add(new JLabel("Telefon:"));
        panel.add(telefon);

        int result = JOptionPane.showConfirmDialog(null, panel, "Edycja danych użytkownika",
                JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

        if (result == JOptionPane.OK_OPTION) {
            if (!imie.getText().matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+") ||
                    !nazwisko.getText().matches("[A-Za-ząćęłńóśźżĄĆĘŁŃÓŚŹŻ]+") ||
                    (!telefon.getText().isBlank() && !telefon.getText().matches("\\d{9}"))) {
                JOptionPane.showMessageDialog(null, "Błąd walidacji danych");
                return;
            }
            u.imie = imie.getText();
            u.nazwisko = nazwisko.getText();
            u.setTelefon(telefon.getText());


            for (int i = 0; i < uzytkownikListaModel.size(); i++) {
                if (uzytkownikListaModel.get(i).startsWith(karta.getUid())) {
                    uzytkownikListaModel.set(i, karta.getUid() + " - " + u.getImieNazwisko() + " (" + u.getTyp() + ")");
                    break;
                }
            }

            JOptionPane.showMessageDialog(null, "Dane zaktualizowano.");
        }
    }


    private JPanel createDostepPanel() {
        JPanel panel = new JPanel(new GridLayout(3, 1));

        JButton przydzielBtn = new JButton("Przydziel dostęp");
        JButton odbierzBtn = new JButton("Odbierz dostęp");

        kartaCombo.removeAllItems();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaCombo.addItem(k.getUid());
        }

        budynekCombo.removeAllItems();
        for (Budynek b : ListaBudynekow.budynki) {
            budynekCombo.addItem(b.getNazwa());
        }

        budynekCombo.addActionListener(e -> odswiezDrzwiCombo());
        odswiezDrzwiCombo();

        przydzielBtn.addActionListener(e -> {
            String uid = (String) kartaCombo.getSelectedItem();
            String budynekNazwa = (String) budynekCombo.getSelectedItem();
            String drzwiNazwa = ((String) drzwiCombo.getSelectedItem()).split(" \\[")[0];

            if (uid != null && budynekNazwa != null && drzwiNazwa != null) {
                KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
                Budynek budynek = ListaBudynekow.budynki.stream()
                        .filter(b -> b.getNazwa().equals(budynekNazwa))
                        .findFirst().orElse(null);
                if (budynek == null) return;
                Drzwi drzwi = budynek.getDrzwi(drzwiNazwa);
                if (drzwi == null) return;

                if (karta.maDostepDo(drzwi)) {
                    JOptionPane.showMessageDialog(panel, "Użytkownik już ma dostęp do tych drzwi.");
                    return;
                }

                boolean moze = karta.getUzytkownik().getRole().stream()
                        .anyMatch(rola -> rola.mozeOtworzyc(drzwi, karta.getUzytkownik()));
                if (!moze) {
                    String wymagania = switch (drzwi.getTyp()) {
                        case NORMALNE -> "Wymagania: STUDENT, PRAKTYKANT, PRACOWNIK lub ADMIN";
                        case ZABEZPIECZONE -> "Wymagania: PRACOWNIK z uprawnieniem SERWISANT lub ADMIN";
                        case TAJNE -> "Wymagania: tylko ADMIN";
                    };

                    JOptionPane.showMessageDialog(panel,
                            "Użytkownik nie spełnia wymagań do uzyskania dostępu.\n" + wymagania);
                    return;
                }

                new Dostep(karta, drzwi);
                karta.getHistoriaZdarzen().add(new ZdarzenieDostepu("Przydzielono dostęp", budynekNazwa + ":" + drzwiNazwa));
                JOptionPane.showMessageDialog(panel, "Przydzielono dostęp do: " + drzwiNazwa);
                odswiezListeUzytkownikow();
            }
        });

        odbierzBtn.addActionListener(e -> {
            String uid = (String) kartaCombo.getSelectedItem();
            String budynekNazwa = (String) budynekCombo.getSelectedItem();
            String drzwiNazwa = ((String) drzwiCombo.getSelectedItem()).split(" \\[")[0];

            if (uid != null && budynekNazwa != null && drzwiNazwa != null) {
                KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
                Budynek budynek = ListaBudynekow.budynki.stream()
                        .filter(b -> b.getNazwa().equals(budynekNazwa))
                        .findFirst().orElse(null);
                if (budynek == null) return;
                Drzwi drzwi = budynek.getDrzwi(drzwiNazwa);
                if (drzwi == null) return;

                Dostep doUsuniecia = karta.getDostepy().stream()
                        .filter(d -> d.getDrzwi().equals(drzwi))
                        .findFirst().orElse(null);

                if (doUsuniecia != null) {
                    karta.usunDostep(doUsuniecia);
                    drzwi.removeDostep(doUsuniecia);
                    karta.getHistoriaZdarzen().add(new ZdarzenieDostepu("Odebrano dostęp", budynekNazwa + ":" + drzwiNazwa));
                    JOptionPane.showMessageDialog(panel, "Odebrano dostęp do: " + drzwiNazwa);
                    odswiezListeUzytkownikow();
                } else {
                    JOptionPane.showMessageDialog(panel, "Brak takiego dostępu do usunięcia.");
                }
            }
        });

        JPanel controls = new JPanel();
        controls.add(new JLabel("Karta UID:"));
        controls.add(kartaCombo);
        controls.add(new JLabel("Budynek:"));
        controls.add(budynekCombo);
        controls.add(new JLabel("Drzwi:"));
        controls.add(drzwiCombo);
        controls.add(przydzielBtn);
        controls.add(odbierzBtn);

        panel.add(controls);

        return panel;
    }

    private JPanel createHistoriaPanel() {
        JPanel panel = new JPanel(new BorderLayout());
        JTextArea area = new JTextArea();
        area.setEditable(false);

        kartaComboHistoria = new JComboBox<>();
        for (KartaDostepu k : KartaDostepu.getEkstensja().values()) {
            kartaComboHistoria.addItem(k.getUid());
        }

        JButton pokazBtn = new JButton("Pokaż historię");
        pokazBtn.addActionListener(e -> {
            String uid = (String) kartaComboHistoria.getSelectedItem();
            if (uid != null) {
                KartaDostepu karta = KartaDostepu.getEkstensja().get(uid);
                List<ZdarzenieDostepu> historia = karta.getHistoriaZdarzen();
                String wynik = historia.stream().map(Object::toString).collect(Collectors.joining("\n"));
                area.setText(wynik.isEmpty() ? "Brak historii." : wynik);
            }
        });

        JPanel top = new JPanel();
        top.add(new JLabel("Karta UID:"));
        top.add(kartaComboHistoria);
        top.add(pokazBtn);

        panel.add(top, BorderLayout.NORTH);
        panel.add(new JScrollPane(area), BorderLayout.CENTER);
        return panel;
    }
}