package util;

import java.io.IOException;
import java.io.Reader;
import java.util.Arrays;
import java.util.Scanner;

public class CLI {
    private static Scanner scanner = new Scanner(System.in);

    public enum SanitizationType
    {
        None,
        Number,
        PositiveNumber,
        YesNo,
        Alphabetic,
        Alphanumeric,
        AlphanumericWithSpaces
    }

    /**
     * Reads a single key from the console and returns its ASCII code.
     * @return The ASCII code of the pressed key, or -1 if an error occurs
     */
    public static int readSingleKey() {
        try {
            Reader reader = System.console().reader();
            int key = reader.read();
            return key;
        } catch (IOException e) {
            e.printStackTrace();
            return -1;
        } catch (NullPointerException e) {
            // This happens if System.console() returns null (e.g., when running in some IDEs)
            System.err.println("Console is not available in this environment");
            e.printStackTrace();
            return -1;
        }
    }

    public static String acceptUserInput(String query, SanitizationType sanitizationType)
    {
        return acceptUserInput(query, sanitizationType, null);
    }

    public static String acceptUserInput(String query, SanitizationType sanitizationType, String[] validValues)
    {
        System.out.print(query);
        String response = scanner.nextLine();

        //check if input satisfies sanitization type
        // if not, print error and restate query
        //if yes return user input

        boolean isValid = false;

        while (!isValid) {
            // First check if validValues is specified and if the response matches any value
            if (validValues != null && validValues.length > 0) {
                isValid = Arrays.asList(validValues).contains(response);
                if (!isValid) {
                    System.out.println("Fout: Invoer komt niet overeen met de toegestane waarden: " +
                            String.join(", ", validValues));
                    System.out.print(query);
                    response = scanner.nextLine();
                    continue;
                }
            }

            // If validValues check passes or is not required, check sanitization type
            switch (sanitizationType) {
                case None:
                    isValid = true;
                    break;
                case Number:
                    isValid = response.matches("^\\d+$");
                    break;
                case PositiveNumber:
                    isValid = response.matches("^\\d+$") && Integer.parseInt(response) >= 0;
                    break;
                case YesNo:
                    response = response.toUpperCase(); //Make sure to accept uncapitalized letters too
                    isValid = response.equals("J") || response.equals("N");
                    break;
                case Alphabetic:
                    isValid = response.matches("^[a-zA-Z]+$");
                    break;
                case Alphanumeric:
                    isValid = response.matches("^[a-zA-Z0-9]+$");
                    break;
                case AlphanumericWithSpaces:
                    isValid = response.matches("^[a-zA-Z0-9\\s]+$");
                    break;
            }

            if (isValid) {
                return response;
            } else {
                // Provide more specific error messages in Dutch based on the sanitization type
                switch (sanitizationType) {
                    case Number:
                        System.out.println("Fout: Invoer komt niet overeen met het vereiste formaat (alleen cijfers zijn toegestaan).");
                        break;
                    case PositiveNumber:
                        System.out.println("Fout: Invoer komt niet overeen met het vereiste formaat (alleen positieve getallen groter dan 0 zijn toegestaan).");
                        break;
                    case Alphabetic:
                        System.out.println("Fout: Invoer komt niet overeen met het vereiste formaat (alleen letters zijn toegestaan).");
                        break;
                    case Alphanumeric:
                        System.out.println("Fout: Invoer komt niet overeen met het vereiste formaat (alleen letters en cijfers zijn toegestaan).");
                        break;
                    case AlphanumericWithSpaces:
                        System.out.println("Fout: Invoer komt niet overeen met het vereiste formaat (alleen letters, cijfers en spaties zijn toegestaan).");
                        break;
                    default:
                        System.out.println("Fout: Invoer komt niet overeen met het vereiste formaat.");
                        break;
                }
                System.out.print(query);
                response = scanner.nextLine();
            }
        }

        return response; // This line should never be reached, but is needed for compilation
    }
}