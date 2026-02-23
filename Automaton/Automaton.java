package Automaton;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Queue;
import java.util.Set;
import java.util.Stack;
import java.util.Map;
import java.util.HashMap;
import java.util.LinkedList;

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

    public void addState(List<State> states) { 
        this.states.addAll(states);
    }

    public void addTranslation(State from, State to, String symbol) {
        this.translations.add(new Translation(symbol, from, to));
    }

    public void addTranslation(List<Translation> translations) {
        this.translations.addAll(translations);
    }

    public int getStateCount() {
        return this.states.size();
    }

    public void removeAllStates() { 
        this.states.clear();
    }

    public void removeAllTranslations() {
        this.translations.clear();
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

    public void removeEpsilonTransitions() {
        List<Translation> newTranslations = new ArrayList<>();
        
        for (State s : new ArrayList<>(this.states)) {
            Set<State> startSet = new HashSet<>();
            startSet.add(s);
            Set<State> closure = getEpsilonClosure(startSet);
            
            for (State reachable : closure) {
                if (reachable.isAccepting()) {
                    s.setAccepting(true);
                }
                
                for (Translation t : this.translations) {
                    if (t.getFromState().equals(reachable) && !isEpsilon(t.getSymbol())) {
                        newTranslations.add(new Translation(t.getSymbol(), s, t.getToState()));
                    }
                }
            }
        }

        this.translations.clear();
        this.translations.addAll(newTranslations);
        
        removeDuplicateTranslations();
        removeUnreachableStates();
        reorderStates();
    }

    private boolean isEpsilon(String sym) {
        return sym == null || sym.equals("ε") || sym.equals("eps") || sym.equals("...");
    }

    public void removeDuplicateTranslations() {
        List<Translation> uniqueTranslations = new ArrayList<>();

        for (Translation t : this.translations) {
            boolean found = false;
            for (Translation u : uniqueTranslations) {
                if (u.getFromState().equals(t.getFromState()) && u.getToState().equals(t.getToState())) {
                    u.addSymbol(t.getSymbol());
                    found = true;
                    break;
                }
            }
            if (!found) {
                uniqueTranslations.add(new Translation(t.getSymbol(), t.getFromState(), t.getToState()));
            }
        }
        this.translations = uniqueTranslations;
    }

    public void removeUnreachableStates() {
        if (states.isEmpty()) return;

        Set<State> reachable = new HashSet<>();
        Stack<State> stack = new Stack<>();

        for (State s : states) {
            if (s.isInitial()) {
                stack.push(s);
                reachable.add(s);
                break;
            }
        }

        while (!stack.isEmpty()) {
            State current = stack.pop();
            for (Translation t : translations) {
                if (t.getFromState().equals(current)) {
                    if (!reachable.contains(t.getToState())) {
                        reachable.add(t.getToState());
                        stack.push(t.getToState());
                    }
                }
            }
        }

        this.states.retainAll(reachable);
        
        this.translations.removeIf(t -> !reachable.contains(t.getFromState()) || !reachable.contains(t.getToState()));
    }

    public Set<String> getAlphabet() {
        Set<String> alphabet = new HashSet<>();
        for (Translation t : translations) {
            if (!isEpsilon(t.getSymbol())) {
                String[] parts = t.getSymbol().split("[ ,]+");
                for (String s : parts) {
                    if (!s.isEmpty()) alphabet.add(s);
                }
            }
        }
        return alphabet;
    }

    public void determinize() {
        if (states.isEmpty()) {
            return;
        }
        removeEpsilonTransitions();
        List<State> dfaStates = new ArrayList<>();
        List<Translation> dfaTranslations = new ArrayList<>();
        Map<Set<State>, State> setsToStates = new HashMap<>();
        Queue<Set<State>> queue = new LinkedList<>();
        State initialNFA = states.stream().filter(State::isInitial).findFirst().orElse(null);
        Set<State> startSet = new HashSet<>();
        startSet.add(initialNFA);
        State startDFA = new State("Q0", true, initialNFA.isAccepting(), 0, 0);
        dfaStates.add(startDFA);
        setsToStates.put(startSet, startDFA);
        queue.add(startSet);
        Set<String> alphabet = getAlphabet();
        while (!queue.isEmpty()) {
            Set<State> currentSet = queue.poll();
            State fromDFA = setsToStates.get(currentSet);
            for (String symbol : alphabet) {
                Set<State> nextSet = new HashSet<>();
                for (State nfaState : currentSet) {
                    for (Translation t : translations) {
                        if (t.getFromState().equals(nfaState) && t.getSymbol().contains(symbol)) {
                            nextSet.add(t.getToState());
                        }
                    }
                }
                if (!nextSet.isEmpty()) {
                    if (!setsToStates.containsKey(nextSet)) {
                        boolean isAcc = nextSet.stream().anyMatch(State::isAccepting);
                        State newState = new State("Q" + dfaStates.size(), false, isAcc, 0, 0);
                        dfaStates.add(newState);
                        setsToStates.put(nextSet, newState);
                        queue.add(nextSet);
                    }
                    dfaTranslations.add(new Translation(symbol, fromDFA, setsToStates.get(nextSet)));
                }
            }
        }
        this.states = dfaStates;
        this.translations = dfaTranslations;
        reorderStates();
    }

    public boolean isDeterministic() {
        if (states.isEmpty()) {
            return true;
        }
        for (State s : states) {
            Set<String> seenSymbols = new HashSet<>();
            for (Translation t : translations) {
                if (t.getFromState().equals(s)) {
                    if (isEpsilon(t.getSymbol())) {
                        return false;
                    }
                    String[] symbols = t.getSymbol().split("[ ,]+");
                    for (String sym : symbols) {
                        if (seenSymbols.contains(sym)) {
                            return false;
                        }
                        seenSymbols.add(sym);
                    }
                }
            }
        }
        return true;
    }

    public void minimize() {
        if (states.isEmpty()) return;
        if (!isDeterministic()) determinize();

        Map<State, Integer> partition = new HashMap<>();
        for (State s : states) {
            partition.put(s, s.isAccepting() ? 1 : 0);
        }

        Set<String> alphabet = getAlphabet();
        boolean changed = true;
        while (changed) {
            changed = false;
            Map<State, List<Integer>> signatures = new HashMap<>();
            for (State s : states) {
                List<Integer> signature = new ArrayList<>();
                signature.add(partition.get(s));
                for (String sym : alphabet) {
                    State target = null;
                    for (Translation t : translations) {
                        if (t.getFromState().equals(s) && t.getSymbol().contains(sym)) {
                            target = t.getToState();
                            break;
                        }
                    }
                    signature.add(target == null ? -1 : partition.get(target));
                }
                signatures.put(s, signature);
            }

            Map<List<Integer>, Integer> sigToNewGroup = new HashMap<>();
            Map<State, Integer> nextPartition = new HashMap<>();
            int nextId = 0;
            for (State s : states) {
                List<Integer> sig = signatures.get(s);
                if (!sigToNewGroup.containsKey(sig)) sigToNewGroup.put(sig, nextId++);
                nextPartition.put(s, sigToNewGroup.get(sig));
            }
            if (new HashSet<>(nextPartition.values()).size() != new HashSet<>(partition.values()).size()) {
                partition = nextPartition;
                changed = true;
            } else changed = false;
        }

        Map<Integer, State> groupToState = new HashMap<>();
        List<State> newStates = new ArrayList<>();
        final Map<State, Integer> finalPartition = partition;
        for (State s : states) {
            int group = partition.get(s);
            if (!groupToState.containsKey(group)) {
                boolean isInit = states.stream().filter(st -> finalPartition.get(st) == group).anyMatch(State::isInitial);
                State newState = new State("m" + group, isInit, s.isAccepting(), 0, 0);
                groupToState.put(group, newState);
                newStates.add(newState);
            }
        }

        List<Translation> newTranslations = new ArrayList<>();
        for (Translation t : translations) {
            State from = groupToState.get(partition.get(t.getFromState()));
            State to = groupToState.get(partition.get(t.getToState()));

            Translation existing = null;
            for (Translation nt : newTranslations) {
                if (nt.getFromState() == from && nt.getToState() == to) {
                    existing = nt;
                    break;
                }
            }
            if (existing != null){
                existing.addSymbol(t.getSymbol());
            } else {
                newTranslations.add(new Translation(t.getSymbol(), from, to));
            }
        }
        this.states = newStates;
        this.translations = newTranslations;
        reorderStates();
    }

    public boolean isMinimized() {
        if (!isDeterministic()) {
            return false;
        }

        return checkCurrentMinimality();
    }

    private boolean checkCurrentMinimality() {
        if (states.isEmpty()) return true;
        
        Set<State> reachable = new HashSet<>();
        Stack<State> stack = new Stack<>();
        states.stream().filter(State::isInitial).forEach(s -> {
            stack.push(s);
            reachable.add(s);
        });
        while (!stack.isEmpty()) {
            State current = stack.pop();
            for (Translation t : translations) {
                if (t.getFromState().equals(current)) {
                    if (!reachable.contains(t.getToState())) {
                        reachable.add(t.getToState());
                        stack.push(t.getToState());
                    }
                }
            }
        }
        if (reachable.size() < states.size()) return false;

        Map<State, Integer> partition = new HashMap<>();
        for (State s : states) partition.put(s, s.isAccepting() ? 1 : 0);

        Set<String> alphabet = getAlphabet();
        boolean changed = true;
        while (changed) {
            changed = false;
            Map<State, List<Integer>> signatures = new HashMap<>();
            for (State s : states) {
                List<Integer> signature = new ArrayList<>();
                signature.add(partition.get(s));
                for (String sym : alphabet) {
                    State target = getTarget(s, sym);
                    signature.add(target == null ? -1 : partition.get(target));
                }
                signatures.put(s, signature);
            }
            Map<List<Integer>, Integer> sigToNewGroup = new HashMap<>();
            Map<State, Integer> nextPartition = new HashMap<>();
            int nextId = 0;
            for (State s : states) {
                List<Integer> sig = signatures.get(s);
                if (!sigToNewGroup.containsKey(sig)) sigToNewGroup.put(sig, nextId++);
                nextPartition.put(s, sigToNewGroup.get(sig));
            }
            if (new HashSet<>(nextPartition.values()).size() != new HashSet<>(partition.values()).size()) {
                partition = nextPartition;
                changed = true;
            } else changed = false;
        }

        return new HashSet<>(partition.values()).size() == states.size();
    }

    private State getTarget(State s, String sym) {
        for (Translation t : translations) {
            if (t.getFromState().equals(s) && t.getSymbol().contains(sym)) {
                return t.getToState();
            }
        }
        return null;
    }
}
