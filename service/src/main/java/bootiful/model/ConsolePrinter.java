package bootiful.model;

import java.util.Map;

public class ConsolePrinter implements Printer {
    @Override
    public void print(String toPrint) {
        System.out.println(Map.of("response", toPrint));
    }
}
