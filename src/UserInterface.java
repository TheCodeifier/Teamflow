import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
        System.out.println("| Typ uw bericht: ");
        System.out.println("| Wilt u een taak koppelen? [J/N]");
        System.out.println("| J: Trello URL: ");
        System.out.println("| N: Druk op enter om te verzenden of op ESC om te annuleren.");

        System.out.print("\033[4A"); // Cursor 4 regels omhoog
        System.out.print("\033[16C"); // Cursor naar positie na "Typ uw bericht: "
        String messageContent = scanner.nextLine();

        System.out.print("\033[1B"); // Cursor 1 regel omlaag
        System.out.print("\033[25C"); // Cursor naar positie na "Wilt u een taak koppelen? [J/N]"
        String taskChoice = scanner.nextLine().toUpperCase();

        if (taskChoice.equals("J")) {
            System.out.print("\033[1B"); // Cursor 1 regel omlaag
            System.out.print("\033[14C"); // Cursor naar positie na "J: Trello URL: "
            String trelloUrl = scanner.nextLine();

            LocalDateTime now = LocalDateTime.now();
            System.out.println("Bericht verzonden met taak gekoppeld aan " + trelloUrl);

            // Nieuw bericht aanmaken
            Bericht bericht = new Bericht(0, messageContent, now, currentUser.getGebruikersnaam(), currentSprint);
        } else {
            LocalDateTime now = LocalDateTime.now();
            System.out.println("Bericht verzonden!");

            // Nieuw bericht aanmaken
            Bericht bericht = new Bericht(0, messageContent, now, currentUser.getGebruikersnaam(), currentSprint);
        }

        waitForEnter();
    }

    private static void displayChatHistory() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");

        // Dummy berichten voor demo, haal hier de berichten op uit de database en print ze in hetzelfde format als hieronder:
        System.out.println("| [2025-03-24 12:38 | Jairmeli] Hi guys!!!!");
        System.out.println("| [2025-03-24 12:39 | Roderick] Hi Jar, what's on the agenda for today?");
        System.out.println("| [2025-03-24 12:40 | Sjoerd] I don't know, lets just look on Trello?");
        System.out.println("| ....");
        System.out.println("| ....");

        waitForEnter();
    }

    private static void displaySearchScreen() {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");
        System.out.println("| Zoeken in berichten: ");
        System.out.println("|");
        System.out.println("| Filter op datum:");
        System.out.println("| Van: ____-__-__");
        System.out.println("| Tot: ____-__-__ ");
        System.out.println("| ");
        System.out.println("| [Enter] Zoeken  [ESC] Terug naar hoofdmenu");
        System.out.println("|");

        System.out.print("\033[4A"); // Cursor 4 regels omhoog
        System.out.print("\033[22C"); // Cursor naar positie na "Zoeken in berichten: "
        String searchText = scanner.nextLine();

        System.out.print("\033[3B"); // Cursor 3 regels omlaag
        System.out.print("\033[7C"); // Cursor naar positie na "Van: "
        String fromDateString = scanner.nextLine();

        System.out.print("\033[1B"); // Cursor 1 regel omlaag
        System.out.print("\033[7C"); // Cursor naar positie na "Tot: "
        String toDateString = scanner.nextLine();

        // Toon zoekresultaten
        displaySearchResults(searchText);
    }

    private static void displaySearchResults(String searchText) {
        clearScreen();
        System.out.println("+----------------------------------------------------------------------------------------------------------------");
        System.out.println("| ");
        System.out.println("| ======    Welkom bij TeamFlow, " + currentUser.getWeergavenaam() + "!    =====");
        System.out.println("| ");
        System.out.println("| Zoekresultaten:");
        System.out.println("|");

        // Dummy zoekresultaten voor demo
        if (searchText.toLowerCase().contains("trello")) {
            System.out.println("| [2025-03-24 12:38 | Jairmeli] Hi guys, we need to update our Trello board!");
            System.out.println("| [2025-03-25 15:42 | Sjoerd] I've added all tasks to Trello, please check");
            System.out.println("| [2025-03-26 09:15 | Roderick] Trello board is now up-to-date");
        } else {
            System.out.println("| Geen resultaten gevonden voor '" + searchText + "'");
        }

        System.out.println("|");
        System.out.println("| [Enter] Nieuwe zoekopdracht [ESC] Terug naar hoofdmenu");

        waitForEnter();
    }

    private static void clearScreen() {
        System.out.print("\033[H\033[2J");
        System.out.flush();
    }

    private static void waitForEnter() {
        System.out.println("\nDruk op Enter om door te gaan...");
        scanner.nextLine();
    }
}