package Automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;

public class Automaton {
    List<State> states;
    List<Translation> translations;

    public Automaton() {
        this.states = new ArrayList<>();
        this.translations = new ArrayList<>();
    }

    public Automaton(List<State> states, List<Translation> translations) {
        this.states = states;
        this.translations = translations;
    }

    public List<State> getStates() {
        return this.states;
    }

    public List<Translation> getTranslations() {
        return this.translations;
    }
    
    public void addState(State state) {
        this.states.add(state);
    }

    public int getStateCount() {
        return this.states.size();
    }

    public void removeState(State state) {
        this.states.remove(state);
    }

    public String getNextStateName() {
        java.util.Set<Integer> usedIds = new java.util.HashSet<>();

        for (State state : states) {
            try {
                String name = state.getName();
                if (name.startsWith("q")) {
                    int id = Integer.parseInt(name.substring(1));
                    usedIds.add(id);
                }
            } catch (Exception e) {
                // Ignore states with non-standard names
            }
        }

        int nextId = 0;
        while (usedIds.contains(nextId)) {
            nextId++;
        }

        return "q" + nextId;
    }

    public void reorderStates() {
        for (int i = 0; i < states.size(); i++) {
            states.get(i).setName("q" + i);
        }
    }

    public Set<State> getEpsilonClosure(Set<State> states) {
        Set<State> closure = new HashSet<>(states);
        Stack<State> stack = new Stack<>();
        stack.addAll(states);

        while (!stack.isEmpty()) {
            State current = stack.pop();
            for (Translation t: this.translations) {
                if (t.getFromState().equals(current)) {
                    if (t.getSymbol().equals("ε") || t.getSymbol().equals("eps") || t.getSymbol().equals("...")) {
                        if (!closure.contains(t.getToState())) {
                            closure.add(t.getToState());
                            stack.push(t.getToState());
                        }
                    }
                }
            }
        }
        return closure;
    }

    public boolean accepts(String input) {
        if (states.isEmpty()) {
            return false;
        }
        State initialState = null;
        for (State s : states) {
            if (s.isInitial()) {
                initialState = s;
                break;
            }
        }
        if (initialState == null) {
            return false;
        }

        Set<State> currentStates = new HashSet<>();
        currentStates.add(initialState);
        currentStates = getEpsilonClosure(currentStates);
        
        for (char c : input.toCharArray()) {
            String symbol = String.valueOf(c);
            Set<State> nextStates = new HashSet<>();

            for (State s : currentStates) {
                for (Translation t : this.translations) {
                    if (t.getFromState().equals(s)) {
                        String[] symbols = t.getSymbol().split("[ ,]+");
                        for (String sym : symbols) {
                            if (sym.equals(symbol)) {
                                nextStates.add(t.getToState());
                                break;
                            }
                        }
                    }
                }
            }
            currentStates = getEpsilonClosure(nextStates);

            if (currentStates.isEmpty()) {
                return false;
            }
        }

        for (State s : currentStates) {
            if (s.isAccepting()) {
                return true;
            }
        }
        return false;
    }

    public Set<State> computeLastVisited(String input) {
        Set<State> visited = new HashSet<>();

        Set<State> currentStates = new HashSet<>();
        for (State s : states) {
            if (s.isInitial()) {
                currentStates.add(s);
                visited.add(s);
            }
        }

        for (char symbol : input.toCharArray()) {
            String sym = String.valueOf(symbol);
            Set<State> nextStates = new HashSet<>();

            for (State current : currentStates) {
                for (Translation t : this.translations) {
                    if (t.getFromState().equals(current) && t.getSymbol().contains(sym)) {
                        nextStates.add(t.getToState());
                        visited.add(t.getToState());
                    }
                }
            }
            currentStates = nextStates;
            if (currentStates.isEmpty()) {
                break;
            }
        }
        return visited;
    }
}
