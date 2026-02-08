import javax.swing.*;

import Automaton.*;
import Affichage.*;

public class Main {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            Automaton automaton = new Automaton();

            JFrame frame = new JFrame("Automaton Editor");
            frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

            AutomatonPanel panel = new AutomatonPanel(automaton);
            frame.add(panel);

            frame.setExtendedState(JFrame.MAXIMIZED_BOTH);
            frame.setVisible(true);
        });
    }
}