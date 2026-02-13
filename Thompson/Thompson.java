package Thompson;

import Automaton.*;
import Regex.*;

import java.util.Stack;

public class Thompson {

    public static Automaton build(String postfix) {
        Stack<Fragment> stack = new Stack<>();
        Automaton a = new Automaton();
        int stateCounter = 0;

        for (char c : postfix.toCharArray()) {
            if (Parser.isSymbol(c)) {
                State from = new State("q" + (stateCounter++), false, false, 0, 0);
                State to = new State("q" + (stateCounter++), false, false, 0, 0);
                a.addState(from);
                a.addState(to);
                a.addTranslation(from, to, String.valueOf(c));
                stack.push(new Fragment(from, to));
            } else if (c == '.') {
                Fragment b = stack.pop();
                Fragment a1 = stack.pop();
                a.addTranslation(a1.getEnd(), b.getStart(), "eps");
                stack.push(new Fragment(a1.getStart(), b.getEnd()));
            } else if ( c == '|') {
                Fragment b = stack.pop();
                Fragment a1 = stack.pop();
                State from = new State("q" + (stateCounter++), false, false, 0, 0);
                State to = new State("q" + (stateCounter++), false, false, 0, 0);
                a.addState(from);
                a.addState(to);
                a.addTranslation(from, a1.getStart(), "eps");
                a.addTranslation(from, b.getStart(), "eps");
                a.addTranslation(a1.getEnd(), to, "eps");
                a.addTranslation(b.getEnd(), to, "eps");
                stack.push(new Fragment(from, to));
            } else if (c == '*') {
                Fragment a1 = stack.pop();
                State from = new State("q" + (stateCounter++), false, false, 0, 0);
                State to = new State("q" + (stateCounter++), false, false, 0, 0);
                a.addState(from);
                a.addState(to);
                a.addTranslation(from, a1.getStart(), "eps");
                a.addTranslation(from, to, "eps");
                a.addTranslation(a1.getEnd(), a1.getStart(), "eps");
                a.addTranslation(a1.getEnd(), to, "eps");
                stack.push(new Fragment(from, to));
            } else if (c == '+') {
                Fragment a1 = stack.pop();
                State from = new State("q" + (stateCounter++), false, false, 0, 0);
                State to = new State("q" + (stateCounter++), false, false, 0, 0);
                a.addState(from);
                a.addState(to);
                a.addTranslation(from, a1.getStart(), "eps");
                a.addTranslation(a1.getEnd(), a1.getStart(), "eps");
                a.addTranslation(a1.getEnd(), to, "eps");
                stack.push(new Fragment(from, to));
            } else if (c== '?') {
                Fragment a1 = stack.pop();
                State from = new State("q" + (stateCounter++), false, false, 0, 0);
                State to = new State("q" + (stateCounter++), false, false, 0, 0);
                a.addState(from);
                a.addState(to);
                a.addTranslation(from, a1.getStart(), "eps");
                a.addTranslation(a1.getEnd(), to, "eps");
                a.addTranslation(from, to, "eps");
                stack.push(new Fragment(from, to));
            }
        }
        if (!stack.isEmpty()) {
            Fragment r = stack.pop();
            r.getStart().setInitial(true);
            r.getEnd().setAccepting(true);
        }
        return a;
    }
}