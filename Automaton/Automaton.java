package Automaton;

import java.util.ArrayList;
import java.util.List;

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
        }   }
}
