package driver;

import engine.SearchEngine;
import java.io.FileNotFoundException;
import java.io.IOException;
/**
 *
 * @author hkhoi
 */
public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        SearchEngine engine = new SearchEngine();
        engine.BSBConstruction();
    }
}
