package Automaton;

public class Fragment {
    private State start;
    private State end;

    public Fragment(State start, State end) {
        this.start = start;
        this.end = end;
    }

    public State getStart() {
        return this.start;
    }

    public State getEnd() {
        return this.end;
    }
}