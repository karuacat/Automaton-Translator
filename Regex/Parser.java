package Regex;

import java.util.*;

public class Parser {

    public static boolean isSymbol(char c) {
        return c != '(' && c != ')' && c != '*' && c != '|' && c != '.' && c != '+' && c != '?';
    }

    private static int priority(char c) {
        return switch (c) {
            case '*', '+', '?' -> 3;
            case '.' -> 2;
            case '|' -> 1;
            default -> 0;
        };
    }

    public static String addConcat(String r) {
        StringBuilder out = new StringBuilder();
        for (int i = 0; i < r.length(); i++) {
            char c1 = r.charAt(i);
            out.append(c1);
            if (i + 1 < r.length()) {
                char c2 = r.charAt(i + 1);
                if ((isSymbol(c1) || c1 == '*' || c1 == '+' || c1 == '?' || c1 == ')') && (isSymbol(c2) || c2 == '(')) {
                    out.append('.');
                }
            }
        }
        return out.toString();
    }

    public static String shuntingYard(String regex) {
        String r = addConcat(regex);
        StringBuilder output = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : r.toCharArray()) {
            if (isSymbol(c)) {
                output.append(c);
            } else if (c == '(') {
                stack.push(c);
            } else if (c == ')') {
                while (!stack.isEmpty() && stack.peek() != '(') {
                    output.append(stack.pop());
                }
                if (!stack.isEmpty()) {
                    stack.pop();
                }
            } else {
                while (!stack.isEmpty() && priority(stack.peek()) >= priority(c)) {
                    output.append(stack.pop());
                }
                stack.push(c);
            }
        }

        while (!stack.isEmpty()) {
            output.append(stack.pop());
        }
        
        return output.toString();
    }
}