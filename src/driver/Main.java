package driver;

import engine.SearchEngine;
import java.util.List;

/**
 *
 * @author hkhoi
 */
public class Main {

    public static void main(String[] args) {
        SearchEngine engine = new SearchEngine();
        List<String> list = engine.invertedIndiceQuery("undermining", true);
        System.out.println(list);
    }
}
