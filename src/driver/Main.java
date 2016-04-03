package driver;

import engine.SearchEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Scanner;

/**
 *
 * @author hkhoi
 */
public class Main {

    public static void main(String[] args) throws FileNotFoundException, IOException {
        System.out.println("***************************************************");
        System.out.println("IMPORTANT NOTE: Because of lacking of time,"
                + "\nI could not implement the querying functionality,"
                + "\nhowever, it is familiar to previous assignments,"
                + "\nso please do not take it so seriously, thank you :)");
        System.out.println("***************************************************\n");

        SearchEngine engine = new SearchEngine();
        Scanner scan = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("1. Build BSB Indexing");
            System.out.println("2. Build SPIMI Indexing");
            System.out.println("3. Exit");
            System.out.print("> Command = ");
            String command = scan.nextLine();

            if (command.equals("3")) {
                loop = false;
            } else {
                switch (command) {
                    case "1":
                        engine.BSBConstruction();
                        break;
                    case "2":
                        engine.SPIMIConstruction();
                        break;
                    default:
                        System.out.println("> ERROR: Command not found!");
                        break;
                }
            }
        }
    }
}
