package Automaton;

public class State {
    private String name;
    private boolean initial;
    private boolean accepting;
    private int x;
    private int y;

    public State(String name, boolean initial, boolean accepting, int x, int y  ) {
        this.name = name;
        this.initial = initial;
        this.accepting = accepting;
        this.x = x;
        this.y = y;
    }

    public String getName() {
        return this.name;
    }

    public boolean isInitial() {
        return this.initial;
    }

    public boolean isAccepting() {
        return this.accepting;
    }

    public int getX() {
        return this.x;
    }

    public int getY() {
        return this.y;
    }

    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }

    public void setInitial(boolean initial) {
        this.initial = initial;
    }

    public void setAccepting(boolean accepting) {
        this.accepting = accepting;
    }

    public void setName(String name) {
        this.name = name;
    }
}
