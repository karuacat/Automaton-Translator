package Affichage;

import java.awt.*;
import java.awt.event.*;
import java.awt.geom.AffineTransform;
import java.awt.geom.QuadCurve2D;

import javax.swing.*;

import Automaton.*;
import Thompson.*;
import Regex.*;

public class AutomatonPanel extends JPanel {
    private Automaton automaton;
    private State selectedState;
    private State translationState;
    private int offsetX;
    private int offsetY;
    private int mouseX;
    private int mouseY;
    
    private static final int STATE_RADIUS = 25;
    private static final int ARROW_SIZE = 10;

    public AutomatonPanel(Automaton automaton) {
        this.automaton = automaton;
        this.selectedState = null;
        this.translationState = null;
        Point mousePos = MouseInfo.getPointerInfo().getLocation();
        SwingUtilities.convertPointFromScreen(mousePos, this);
        this.mouseX = mousePos.x;
        this.mouseY = mousePos.y;

        this.setLayout(new BorderLayout());
        setBackground(Color.WHITE);

        setupTestBar();
        setupRegexBar();

        MouseAdapter mouseHandler = new MouseAdapter() {

            @Override
            public void mousePressed(MouseEvent e) {
                State s = findState(e.getX(), e.getY());

                if (SwingUtilities.isRightMouseButton(e) && s != null){
                    JPopupMenu menu = new JPopupMenu();

                    JMenuItem initialItem = new JMenuItem(s.isInitial() ? "Unset Initial" : "Set Initial");
                    initialItem.addActionListener(a -> {
                        s.setInitial(!s.isInitial());
                        repaint();
                    });

                    JMenuItem acceptingItem = new JMenuItem(s.isAccepting() ? "Unset Accepting" : "Set Accepting");
                    acceptingItem.addActionListener(a -> {
                        s.setAccepting(!s.isAccepting());
                        repaint();
                    });

                    JMenuItem deleteItem = new JMenuItem("Delete State");
                    deleteItem.addActionListener(a -> {
                        automaton.removeState(s);
                        automaton.getTranslations().removeIf(t -> t.getFromState() == s || t.getToState() == s);
                        automaton.reorderStates();
                        repaint();
                    });

                    menu.add(initialItem);
                    menu.add(acceptingItem);
                    menu.addSeparator();
                    menu.add(deleteItem);
                    menu.addSeparator();

                    JMenu deleteTransMenu = new JMenu("Delete Transition to...");
                    boolean hasTransitions = false;

                    for (Translation t : automaton.getTranslations()) {
                        if (t.getFromState() == s) {
                            hasTransitions = true;
                            
                            JMenu specificTransMenu = new JMenu("to " +t.getToState().getName());

                            JMenuItem deleteAll = new JMenuItem("Delete all (" + t.getSymbol() + ")");
                            deleteAll.addActionListener(a -> {
                                automaton.getTranslations().remove(t);
                                repaint();
                            });

                            JMenuItem deleteOne = new JMenuItem("Delete specific symbol...");
                            deleteOne.addActionListener(a -> {
                                JPanel delPanel = new JPanel(new BorderLayout(5, 5));
                                JTextField delField = new JTextField(10);
                                JButton delEpsilonBtn = new JButton("ε");

                                delEpsilonBtn.addActionListener(al -> delField.setText("ε"));
                                delPanel.add(new JLabel("Enter symbol to delete from: " + t.getSymbol()), BorderLayout.NORTH);
                                delPanel.add(delField, BorderLayout.CENTER);
                                delPanel.add(delEpsilonBtn, BorderLayout.EAST);
                                int result = JOptionPane.showConfirmDialog(AutomatonPanel.this, delPanel, "Delete Symbol", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
                                if (result == JOptionPane.OK_OPTION) {
                                    String toDelete = delField.getText().trim();
                                    if (!toDelete.isEmpty()) {
                                        if (toDelete.equalsIgnoreCase("eps")) {
                                            toDelete = "ε";
                                        }
                                        t.removeSymbol(toDelete);
                                        if (t.hasNoSymbols()) {
                                            automaton.getTranslations().remove(t);
                                        }
                                        repaint();
                                    }
                                }
                            });

                            specificTransMenu.add(deleteOne);
                            specificTransMenu.add(deleteAll);
                            deleteTransMenu.add(specificTransMenu);
                        }
                    }
                    
                    deleteTransMenu.setEnabled(hasTransitions);

                    menu.add(deleteTransMenu);
                    menu.show(AutomatonPanel.this, e.getX(), e.getY());
                    return;
                }

                if (SwingUtilities.isLeftMouseButton(e)) {
                    if (s != null) {
                        if (translationState != null) {
                            JPanel panel = new JPanel(new BorderLayout(5, 5));
                            JTextField textField = new JTextField(10);
                            JButton epsilonBtn = new JButton("ε");
                        
                            epsilonBtn.addActionListener(al -> {
                                String current = textField.getText().trim();
                                if (current.isEmpty()) textField.setText("ε");
                                else textField.setText(current + ", ε");
                            });

                            panel.add(new JLabel("Enter symbol(s):"), BorderLayout.NORTH);
                            panel.add(textField, BorderLayout.CENTER);
                            panel.add(epsilonBtn, BorderLayout.EAST);

                            int result = JOptionPane.showConfirmDialog(AutomatonPanel.this, panel, 
                                    "New Transition", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);

                            if (result == JOptionPane.OK_OPTION) {
                                String input = textField.getText();
                                if (input != null && !input.trim().isEmpty()) {
                                    String cleanedInput = input.trim();
                                    String[] tokens = cleanedInput.split("[ ,]+");
                                    boolean hasInvalid = false;
                                    
                                    for (String token : tokens) {
                                        if (token.length() > 1 && !token.equals("...") && !token.equals("ε") && !token.equals("eps")) {
                                            hasInvalid = true;
                                            break;
                                        }
                                    }

                                    if (hasInvalid) {
                                        JOptionPane.showMessageDialog(AutomatonPanel.this, 
                                            "Invalid input! Use single characters, 'ε', or '...'", 
                                            "Error", JOptionPane.ERROR_MESSAGE);
                                    } else {
                                        Translation existing = findExistingTranslation(translationState, s);
                                        if (existing != null) {
                                            existing.addSymbol(cleanedInput);
                                        } else {
                                            automaton.addTranslation(translationState, s, cleanedInput);
                                        }
                                        repaint();
                                    }
                                }
                            }
                            translationState = null;
                            repaint();
                        } else {
                            selectedState = s;
                            translationState = s;
                            offsetX = e.getX() - s.getX();
                            offsetY = e.getY() - s.getY();
                        }
                    } else {
                        selectedState = null;
                        translationState = null;
                    }
                }

            }

            @Override
            public void mouseDragged(MouseEvent e) {
                if (selectedState != null) {
                    translationState = null;

                    selectedState.setX(e.getX() - offsetX);
                    selectedState.setY(e.getY() - offsetY);
                    repaint();
                }
            }

            @Override
            public void mouseReleased(MouseEvent e) {
                selectedState = null;
            }

            @Override
            public void mouseClicked(MouseEvent e){
                if (SwingUtilities.isLeftMouseButton(e) && e.getClickCount() == 2) {
                    State s = findState(e.getX(), e.getY());
                    if (s == null) {
                        String nextName = automaton.getNextStateName();
                        automaton.addState(new State(nextName, false, false, e.getX(), e.getY()));
                        repaint();
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e) {
                mouseX = e.getX();
                mouseY = e.getY();
                repaint();
            }
        };
        addMouseListener(mouseHandler);
        addMouseMotionListener(mouseHandler);

        this.setFocusable(true);
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        Graphics2D g2 = (Graphics2D) g;
        g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_ON);
        g2.setRenderingHint(RenderingHints.KEY_RENDERING, RenderingHints.VALUE_RENDER_QUALITY);

        for (Translation t : automaton.getTranslations()) {
            drawTranslation(g2, t);
        }

        for (State s : automaton.getStates()) {
            drawState(g2, s);
        }

        if (translationState != null) {
            g2.setColor(Color.GRAY);
            drawArrow(g2, translationState.getX(), translationState.getY(), mouseX, mouseY);
            g2.setColor(Color.BLACK);
        }
    }

    private void drawTranslation(Graphics2D g, Translation t) {
        State from = t.getFromState();
        State to = t.getToState();

        if (from == to) {
            int x = from.getX();
            int y = from.getY();
            
            int loopSize = 30;
            int xPos = x - loopSize / 2;
            int yPos = y - STATE_RADIUS - loopSize + 5;

            g.setStroke(new BasicStroke(2));
            g.drawArc(xPos, yPos, loopSize, loopSize, -30, 240);

            drawArrowHead(g, x + 8, y - STATE_RADIUS, Math.PI / 3);
            String sym = t.getSymbol();
            if (sym == null || sym.isEmpty() || sym.equals("eps")) {
                sym = "ε";
            }
            drawCenteredString(g, sym, x, yPos - 5);
        } else {
            if (hasReverseTransition(from, to)) {
                drawCurvedArrow(g, from, to, 40);
                Point labelPos = computeLabelPosition(from, to, 35);
                drawCenteredString(g, t.getSymbol(), labelPos.x, labelPos.y);
            } else {
                drawArrow(g, from.getX(), from.getY(), to.getX(), to.getY());
                Point labelPos = computeLabelPosition(from, to, 15);
                drawCenteredString(g, t.getSymbol(), labelPos.x, labelPos.y);
            }
        }
    }

    private Point computeLabelPosition(State from, State to, int offset) {
        int mx = (from.getX() + to.getX()) / 2;
        int my = (from.getY() + to.getY()) / 2;

        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) {
            len = 1;
        }

        int ox = (int) (-dy / len * offset);
        int oy = (int) (dx / len * offset);

        return new Point(mx + ox, my + oy);
    }

    private boolean hasReverseTransition(State from, State to) {
        if (from == null || to == null) {
            return false;
        }
        for (Translation t : automaton.getTranslations()) {
            if (t.getFromState() == to && t.getToState() == from) {
                return true;
            }
        }
        return false;
    }

    private void drawCurvedArrow(Graphics2D g, State from, State to, int curvature) {
        int dx = to.getX() - from.getX();
        int dy = to.getY() - from.getY();

        double len = Math.sqrt(dx * dx + dy * dy);
        if (len == 0) {
            return;
        }

        double nx = -dy / len;
        double ny = dx / len;

        int controlX = (from.getX() + to.getX()) / 2 + (int) (nx * curvature);
        int controlY = (from.getY() + to.getY()) / 2 + (int) (ny * curvature);

        double tdx = to.getX() - controlX;
        double tdy = to.getY() - controlY;
        double tlen = Math.sqrt(tdx * tdx + tdy * tdy);

        int endX = (int) (to.getX() - tdx / tlen * STATE_RADIUS);
        int endY = (int) (to.getY() - tdy / tlen * STATE_RADIUS);

        QuadCurve2D curve = new QuadCurve2D.Float();
        curve.setCurve(from.getX(), from.getY(), controlX, controlY, endX, endY);

        g.setStroke(new BasicStroke(2));
        g.draw(curve);

        drawArrowHead(g, endX, endY, Math.atan2(tdy, tdx));
    }

    private void drawCenteredString(Graphics2D g, String str, int x, int y) {
        FontMetrics fm = g.getFontMetrics();
        int textWidth = fm.stringWidth(str);
        int textHeight = fm.getHeight();

        g.setColor(new Color(255, 255, 255, 200));
        g.fillRoundRect(x - textWidth / 2 - 2, y - textHeight / 2 - 2, textWidth + 4, textHeight, 5, 5);

        g.setColor(Color.BLACK);
        g.drawString(str, x - textWidth / 2, y + textHeight / 4);
    }

    private void drawState(Graphics2D g, State s) {
        int x = s.getX() - STATE_RADIUS;
        int y = s.getY() - STATE_RADIUS;

        g.setColor(new Color(0, 0, 0, 30));
        g.fillOval(x + 3, y + 3, STATE_RADIUS * 2, STATE_RADIUS * 2);

        GradientPaint gradient = new GradientPaint(x, y, Color.WHITE, x + STATE_RADIUS * 2, y + STATE_RADIUS * 2, new Color(240, 240, 240));
        g.setPaint(gradient);
        g.fillOval(x, y, STATE_RADIUS * 2, STATE_RADIUS * 2);

        if(s == selectedState) {
            g.setColor(new Color(52, 152, 219));
            g.setStroke(new BasicStroke(3));
        } else {
            g.setColor(new Color(44, 62, 80));
            g.setStroke(new BasicStroke(2));
        }
        g.drawOval(x, y, STATE_RADIUS * 2, STATE_RADIUS * 2);

        if (s.isAccepting()) {
            g.setStroke(new BasicStroke(1));
            g.drawOval(x + 5, y + 5, 2 * STATE_RADIUS - 10, 2 * STATE_RADIUS - 10);
        }
        if (s.isInitial()) {
            g.drawLine(s.getX() - 40, s.getY(), s.getX() - STATE_RADIUS, s.getY());
        }

        g.setColor(Color.DARK_GRAY);
        g.setFont(new Font("SansSerif", Font.BOLD, 13));
        FontMetrics fm = g.getFontMetrics();
        int textWidth = s.getX() - fm.stringWidth(s.getName()) / 2;
        int textHeight = s.getY() + fm.getAscent() / 2 - 2;
        g.drawString(s.getName(), textWidth, textHeight);
    }

    private State findState(int x, int y) {
        for (State s : automaton.getStates()) {
            int dx = s.getX() - x;
            int dy = s.getY() - y;
            if (dx * dx + dy * dy <= STATE_RADIUS * STATE_RADIUS) {
                return s;
            }
        }
        return null;
    }

    private void drawArrow(Graphics2D g, int x1, int y1, int x2, int y2) {
        double dx = x2 - x1;
        double dy = y2 - y1;
        double distance = Math.sqrt(dx * dx + dy * dy);
        if (distance == 0) {
            return;
        }

        double ux = dx / distance;
        double uy = dy / distance;

        int endX = (int) (x2 - ux * STATE_RADIUS);
        int endY = (int) (y2 - uy * STATE_RADIUS);

        int startX = (int) (x1 + ux * STATE_RADIUS);
        int startY = (int) (y1 + uy * STATE_RADIUS);

        g.setStroke(new BasicStroke(2));
        g.drawLine(startX, startY, endX, endY);

        drawArrowHead(g, endX, endY, Math.atan2(dy, dx));
    }
    
    private void drawArrowHead(Graphics2D g, int x, int y, double angle) {
        AffineTransform old = g.getTransform();
        g.translate(x, y);
        g.rotate(angle);

        Polygon arrowHead = new Polygon();
        arrowHead.addPoint(0, 0);
        arrowHead.addPoint(-ARROW_SIZE, -ARROW_SIZE / 2);
        arrowHead.addPoint(-ARROW_SIZE, ARROW_SIZE / 2);
        
        g.fill(arrowHead);
        g.setTransform(old);
    }

    private Translation findExistingTranslation(State from, State to) {
        for (Translation t : automaton.getTranslations()) {
            if (t.getFromState() == from && t.getToState() == to) {
                return t;
            }
        }
        return null;
    }

    private void setupTestBar() {
        JPanel testBar = new JPanel(new FlowLayout(FlowLayout.RIGHT, 15, 10));
        testBar.setOpaque(false); 

        JTextField wordInput = new JTextField(12);
        wordInput.setFont(new Font("SansSerif", Font.PLAIN, 14));
        
        JButton testButton = new JButton("Test word");
        
        JLabel resLabel = new JLabel("Status: Waiting");
        resLabel.setFont(new Font("SansSerif", Font.BOLD, 12));
        resLabel.setOpaque(true);
        resLabel.setBackground(new Color(245, 245, 245));
        resLabel.setBorder(BorderFactory.createEmptyBorder(5, 10, 5, 10));

        testButton.addActionListener(e -> {
            String word = wordInput.getText().trim();
            if (word.equals("ε") || word.equalsIgnoreCase("eps")) {
                word = "";
            }

            boolean accepted = automaton.accepts(word);
            
            if (accepted) {
                resLabel.setText("✅ WORD ACCEPTED");
                resLabel.setBackground(new Color(200, 255, 200));
                resLabel.setForeground(new Color(0, 100, 0));
            } else {
                resLabel.setText("❌ WORD REJECTED");
                resLabel.setBackground(new Color(255, 200, 200));
                resLabel.setForeground(new Color(150, 0, 0));
            }
            this.repaint();
        });

        wordInput.addActionListener(e -> testButton.doClick());

        JPanel glassPanel = new JPanel();
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        glassPanel.add(new JLabel("Word:"));
        glassPanel.add(wordInput);
        glassPanel.add(testButton);
        glassPanel.add(resLabel);

        testBar.add(glassPanel);
        
        this.add(testBar, BorderLayout.NORTH);
    }

    private void setupRegexBar() {
        JPanel regexBar = new JPanel(new FlowLayout(FlowLayout.CENTER, 15, 10));
        regexBar.setOpaque(false);

        JTextField regexInput = new JTextField(12);
        regexInput.setFont(new Font("SansSerif", Font.PLAIN, 14));

        JButton regexButton = new JButton("From Regex");

        regexButton.addActionListener(e -> {
            String regex = regexInput.getText().trim();
            if (!regex.isEmpty()) {
                String postfix = Parser.shuntingYard(regex);
                Automaton a = Thompson.build(postfix);
                a.removeEpsilonTransitions();
                a.reorderStates();
                automaton.removeAllStates();
                automaton.removeAllTranslations();
                layoutStatesInCircle(a.getStates());
                automaton.addState(a.getStates());
                automaton.addTranslation(a.getTranslations());
                automaton.reorderStates();
            }
            this.repaint();
        });

        JPanel glassPanel = new JPanel();
        glassPanel.setBackground(new Color(255, 255, 255, 200));
        glassPanel.setBorder(BorderFactory.createLineBorder(new Color(200, 200, 200)));
        glassPanel.add(new JLabel("Regex:"));
        glassPanel.add(regexInput);
        glassPanel.add(regexButton);

        regexBar.add(glassPanel);
        
        this.add(regexBar, BorderLayout.SOUTH);
    }

    private void layoutStatesInCircle(java.util.List<State> states) {
        if (states.isEmpty()) {
            return;
        }
        
        int centerX = Math.max(getWidth() / 2, 400);
        int centerY = Math.max(getHeight() / 2, 300);
        int radius = Math.min(centerX, centerY) / 2;

        for (int i = 0; i < states.size(); i++) {
            double angle = 2 * Math.PI * i / states.size();
            int x = (int) (centerX + radius * Math.cos(angle));
            int y = (int) (centerY + radius * Math.sin(angle));
            states.get(i).setX(x);
            states.get(i).setY(y);
        } 
    } 

}