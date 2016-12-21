
import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;

//Time duration between specific keystrokes (e.g. e ->t), highlight weaknesses, maybe have an option to emphasize this key pairing
public class TypeFaster {
    static BackEnd backEnd;
    public static void main(String[] args) {
        //First load in everything in gray, then each time a key is typed, if it matches take it out of the gray,
        //add it to the black. If not, make it red (maybe highlighted).
        backEnd = new BackEnd();
        backEnd.importFile("Stranger.txt");
        backEnd.newSample();
        JFrame menu = new JFrame();
        menu.setSize(300,200);
        JButton openApp = new JButton("Start Typing");
        JButton importer = new JButton("Import File");
        JFrame frame = new JFrame();
        class startListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                menu.setVisible(false);
                frame.setVisible(true);
            }
        }
        class importListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JFrame importWindow = new JFrame();
                JLabel label = new JLabel("Please enter the name of the file you want to import:");
                importWindow.setSize(300,100);
                importWindow.setLayout(new BoxLayout(importWindow.getContentPane(), BoxLayout.Y_AXIS));
                importWindow.add(label);
                JTextArea fileName = new JTextArea();
                class enterListener implements KeyListener {
                    public void keyPressed(KeyEvent event) {
                        if (event.getKeyChar() == '\n') {
                            int successful = backEnd.importFile(fileName.getText());
                            if (successful == 0) {
                                label.setText("File could not be found. Please try again.");
                            } else {
                                importWindow.setVisible(false);
                            }
                        }
                    }
                    public void keyReleased(KeyEvent event) {}
                    public void keyTyped(KeyEvent event) {}
                }
                fileName.addKeyListener(new enterListener());
                importWindow.add(fileName);
                importWindow.setVisible(true);
            }
        }
        openApp.addActionListener(new startListener());
        importer.addActionListener(new importListener());
        menu.setLayout(new BoxLayout(menu.getContentPane(), BoxLayout.Y_AXIS));
        menu.add(openApp);
        menu.add(importer);
        menu.setVisible(true);
        menu.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setLayout(new BoxLayout(frame.getContentPane(), BoxLayout.Y_AXIS));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("Type Faster!");
        frame.pack();
        JTextArea display = new JTextArea();
        display.setLineWrap(true);
        display.setWrapStyleWord(true);
        display.setEditable(false);
        display.setText(backEnd.getCurrentSample());
        JTextField typingArea = new JTextField();
        JButton retry = new JButton("Continue typing?");
        JFrame popup = new JFrame();
        JTextArea timeStats = new JTextArea();
        JTextArea errorStats = new JTextArea();
        timeStats.setEditable(false);
        errorStats.setEditable(false);
        popup.setAlwaysOnTop(true);
        class RetryListener implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                backEnd.newSample();
                display.setText(backEnd.getCurrentSample());
                typingArea.setText("");
                popup.setVisible(false);
            }
        }
        class KeyPressListener implements KeyListener {
            public void keyPressed(KeyEvent event) {}
            public void keyReleased(KeyEvent event) {};
            public void keyTyped(KeyEvent event) {
                char c = event.getKeyChar();
                if (c == backEnd.expected()) {
                    //backEnd.correct registers the correct action, returns whether or not
                    //there are more characters to type (end of sample not yet reached).
                    if (!backEnd.correct()) {
                        //Display statistics, asks if they want to try again,
                        popup.setSize(250,300);
                        Trie[] timeStatistics = backEnd.findSlowestSequences();
                        timeStats.setText("Slowest Sequences: \n");
                        for(int i = 0; i < 5; i++) {
                            char firstChar = StringCompression.charForInt(timeStatistics[i].getPath() / 100);
                            char secondChar = StringCompression.charForInt(timeStatistics[i].getPath() % 100);
                            timeStats.append(i + ": " + firstChar + " -> " + secondChar+ " - Time is " +
                                    timeStatistics[i].getTime() + "\n");
                        }
                        Trie[] errorStatistics = backEnd.findMostErrors();
                        errorStats.setText("Most Errors: \n");
                        for(int i = 0; i < 5; i++) {
                            char firstChar = StringCompression.charForInt(errorStatistics[i].getPath() / 100);
                            char secondChar = StringCompression.charForInt(errorStatistics[i].getPath() % 100);
                            errorStats.append(i + ": " + firstChar + " -> " + secondChar +
                                    " - Average number of Errors " + errorStatistics[i].getAvgErrors() + "\n");
                        }
                        popup.setLayout(new BoxLayout(popup.getContentPane(), 1));
                        popup.getContentPane().add(timeStats, BorderLayout.CENTER);
                        popup.getContentPane().add(errorStats, BorderLayout.EAST);
                        popup.getContentPane().add(retry, BorderLayout.SOUTH);
                        retry.addActionListener(new RetryListener());
                        popup.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
                        popup.setVisible(true);

                    } else {
                        display.setForeground(Color.BLACK);
                        backEnd.start();
                    }
                } else {
                    display.setForeground(Color.RED);
                    backEnd.incorrect();
                    System.out.println(backEnd.expected());
                    System.out.println(StringCompression.intForChar(backEnd.expected()));
                }
            };
        }

        typingArea.addKeyListener(new KeyPressListener());
        frame.getContentPane().add(display);
        frame.getContentPane().add(typingArea);
        frame.setSize(500,500);
    }

}
