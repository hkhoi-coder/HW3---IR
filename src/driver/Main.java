package driver;

import engine.SearchEngine;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

/**
 *
 * @author hkhoi
 */
public class Main {

    public static void main(String[] args) {
        System.out.println("********************************");
        System.out.println("Name: Hoang Khoi - 1351026");
        System.out.println("********************************");
        System.out.println();

        SearchEngine engine = new SearchEngine();
        Scanner scan = new Scanner(System.in);
        boolean loop = true;
        while (loop) {
            System.out.println("1. BSB Query");
            System.out.println("2. SPIMI Query");
            System.out.println("3. Exit");
            System.out.print("> Command: ");
            String command = scan.nextLine();

            switch (command) {
                case "1":
                    System.out.print("Input term: ");
                    String bsbTerm = scan.nextLine();
                    List<String> bsbList = engine.invertedIndiceQuery(bsbTerm, true);
                    printList(bsbList);
                    break;
                case "2":
                    System.out.print("Input term: ");
                    String spimiTerm = scan.nextLine();
                    List<String> spimiList = engine.invertedIndiceQuery(spimiTerm, false);
                    printList(spimiList);
                    break;
                case "3":
                    loop = false;
                    break;
                default:
                    System.out.println("> WARNING: Illegal command!");
                    break;
            }
        }
    }

    private static void printList(List<String> list) {
        System.out.println("*************************************");
        System.out.println("> RESULT:");
        if (list == null || list.isEmpty()) {
            System.out.println("\tNothing found...");
        } else {
            for (String it : list) {
                System.out.println("\t" + it);
            }
        }
        System.out.println("*************************************\n");
    }
}
