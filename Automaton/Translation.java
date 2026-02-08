package Automaton;

public class Translation {
    private String symbol;
    private State fromState;
    private State toState;

    public Translation(String symbol, State fromState, State toState) {
        this.symbol = sortSymbols(symbol);
        this.fromState = fromState;
        this.toState = toState;
    }

    public String getSymbol() {
        return this.symbol;
    }

    public State getFromState() {
        return this.fromState;
    }

    public State getToState() {
        return this.toState;
    }

    public void addSymbol(String newSymbol) {
        if (newSymbol == null || newSymbol.trim().isEmpty()) {
            return;
        }
        this.symbol = sortSymbols(this.symbol + ", " + newSymbol);
    }

    private String sortSymbols(String input) {
        if (input == null) {
            return "";
        }
        String[] parts = input.split("[ ,]+");
        java.util.Set<String> set = new java.util.TreeSet<>();
        for (String s : parts) {
            String trimmed = s.trim();
            if (trimmed.isEmpty()) {
                continue;
            }
            if (trimmed.equals("...") || trimmed.equals("ε") || trimmed.equals("eps")) {
                set.add(trimmed.equals("...") ? "..." : (trimmed.equals("ε") ? "ε" : "ε"));
            } else if (trimmed.length() == 1) {
                set.add(trimmed);
            }
        }
        return String.join(", ", set);
    }

    public void removeSymbol(String symbolToDelete) {
        if (symbolToDelete == null || symbolToDelete.trim().isEmpty()) {
            return;
        }
        String[] parts = this.symbol.split("[ ,]+");
        java.util.Set<String> set = new java.util.TreeSet<>();

        String target = symbolToDelete.trim();
        for (String s : parts) {
            if (!s.equals(target)) {
                set.add(s);
            }
        }
        this.symbol = String.join(", ", set);
    }

    public boolean hasNoSymbols() {
        return this.symbol == null || this.symbol.trim().isEmpty();
    }
}