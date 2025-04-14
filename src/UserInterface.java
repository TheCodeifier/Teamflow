import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

import database.Database;
import database.model.*;
import util.*;

public class UserInterface {
    private static final Scanner scanner = new Scanner(System.in);
    private static Gebruiker currentUser;
    private static int currentSprint;
    private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    private static final DateTimeFormatter DATE_TIME_FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
    private static boolean loggedIn = false;

    public static void main(String[] args) {
        boolean running = true;

        while (running) {
            if (handleLogin()) {
                while (loggedIn) {
                    handleMainMenu();
                }

            }
        }

        scanner.close();
    }

    private static boolean handleLogin() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow!    =====");
        System.out.println("| ");
        System.out.println("| Voer je gebruikersnaam in om verder te gaan.");
        System.out.println("| ");

        String gebruikersnaam = CLI.acceptUserInput("| Gebruikersnaam: ", CLI.SanitizationType.Alphanumeric);
        String weergavenaam = CLI.acceptUserInput("| Weergavenaam: ", CLI.SanitizationType.AlphanumericWithSpaces);
        String sprintNummerString = CLI.acceptUserInput("| Sprint #: ", CLI.SanitizationType.PositiveNumber);
        int sprintNummer = Integer.parseInt(sprintNummerString);

        currentUser = new Gebruiker(gebruikersnaam, weergavenaam);
        currentSprint = sprintNummer;

        //System.out.println("| Sprint #: ");
        System.out.println("| ");
        System.out.println("| ");

        loggedIn = true;
        return true;
    }



    private static void handleMainMenu() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");
        System.out.println("| Dit is sprint " + currentSprint + ". Kies een van de onderstaande opties om verder te gaan.");
        System.out.println("| ");
        System.out.println("| 1) Bericht versturen");
        System.out.println("| 2) Chatgeschiedenis weergeven");
        System.out.println("| 3) Zoeken in berichten");
        System.out.println("|");
        System.out.println("| 0) Uitloggen");
        System.out.println("|");


        int choice = Integer.parseInt(CLI.acceptUserInput("| Kies een optie: ", CLI.SanitizationType.PositiveNumber, new String[]{"0", "1", "2", "3"}));

        switch (choice) {
            case 1:
                displayMessageScreen();
                break;
            case 2:
                displayChatHistory();
                break;
            case 3:
                displaySearchScreen();
                break;
            case 0:
                loggedIn = false;
                currentUser = null;
                currentSprint = -1;
                break;
            default:
                System.out.println("Ongeldige keuze. Probeer opnieuw."); //Zou nooit moeten gebeuren
        }

    }

    private static void displayMessageScreen() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");

        String bericht = CLI.acceptUserInput("| Typ uw bericht: ", CLI.SanitizationType.None);
        String taakKoppelen = CLI.acceptUserInput("| Wilt u een taak koppelen? [J/N] ", CLI.SanitizationType.YesNo);
        String trelloUrl;

        if (taakKoppelen.equals("J"))
        {
            trelloUrl = CLI.acceptUserInput("| Trello URL: ", CLI.SanitizationType.YesNo);
        }

        System.out.println("| ");
        System.out.println("| Druk op [Enter] om te verzenden of op [ESC] om te annuleren.");

        long charCode = CLI.readSingleKey();

        if (charCode == 10) //[ENTER]
        {
            Bericht berichtObj = new Bericht(0, bericht, LocalDateTime.now(), currentUser.getGebruikersnaam(), currentSprint);
            try {
                berichtObj.save();
                System.out.println("| Bericht verzonden.");
            } catch (SQLException e) {
                System.out.println("| Fout bij opslaan van bericht: " + e.getMessage());
                e.printStackTrace();
            }
            return;
        }

        if (charCode == 27) //[ESC]
        {
            System.out.println("| Bericht geannuleerd.");
            return;
        }
    }

    private static void printBerichten(List<Bericht> berichten)
    {
        for (Bericht b : berichten) {
            System.out.printf("| [%s | %s] %s%n", b.getTijdstip().format(DATE_TIME_FORMATTER), b.getAfzender(), b.getInhoud());
        }

        if (berichten.isEmpty())
        {
            System.out.println("| <geen berichten om weer te geven>");
        }
    }

    private static void displayChatHistory() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");

        System.out.println("| Chat geschiedenis:");

        List<Bericht> berichten = Bericht.getAll();
        printBerichten(berichten);


        System.out.println("| ");
        System.out.println("| Druk op [ENTER] om naar het vorige scherm te gaan.");

        scanner.nextLine();
    }

    private static void displaySearchScreen() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");

        String zoekterm = CLI.acceptUserInput("| Zoekterm: ", CLI.SanitizationType.None);

//        System.out.println("| Zoeken in berichten: ");
//        System.out.println("|");
//        System.out.println("| Filter op datum:");
//        System.out.println("| Van: ____-__-__");
//        System.out.println("| Tot: ____-__-__ ");
        System.out.println("| ");
        //System.out.println("| [Enter] Zoeken  [ESC] Terug naar hoofdmenu");

        List<Bericht> berichten = Bericht.getAll();
        List<Bericht> gefilterdeBerichten = new ArrayList<>();

        for (Bericht b : berichten) {
            if (b.getInhoud().contains(zoekterm))
                gefilterdeBerichten.add(b);
        }

        printBerichten(gefilterdeBerichten);

        System.out.println("| ");
        System.out.println("| Druk op [ENTER] om naar het vorige scherm te gaan.");

        scanner.nextLine();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

}