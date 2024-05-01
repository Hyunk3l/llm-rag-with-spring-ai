package bootiful.model;

import java.util.Map;

public class ConsolePrinter {
    public void print(String message) {
        System.out.println(Map.of("response", message));
    }
}
