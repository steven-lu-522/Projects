import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;

/**
 * Created by Steven Lu on 10/11/2016.
 */
public class PredictNextNumber {
    private static ArrayList<Integer> data;
    public static void main(String[] args) {
        // Create Display
        JFrame frame = new JFrame();
        JButton action = new JButton("Predict 100 Times");
        JButton showData = new JButton("Show Data");
        JPanel panel = new JPanel();
        JLabel label0 = new JLabel("Prediction Accuracy: ");
        JLabel label = new JLabel("0 %");
        data = new ArrayList<Integer>(0);
        label.setSize(500,30);
        panel.setBorder(BorderFactory.createEmptyBorder(100, 100, 100, 100));
        panel.setLayout(new GridLayout(0, 1));
        panel.add(action);
        panel.add(showData);
        frame.add(panel, BorderLayout.CENTER);
        panel.add(label0);
        panel.add(label);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setTitle("GUI");
        frame.pack();
        frame.setVisible(true);
        //Predict the next number
        PatternPrediction p = new PatternPrediction();
        for (int i = 0; i < 500; i++) {
            int element = (int) (Math.random() * 10);
            p.addNumber(element);
            data.add(element);
        }
        class ButtonListener1 implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                //
                int n1, n2;
                for (int i = 0; i < 100; i++) {
                    n1 = (int) (Math.random() * 10);
                    n2 = p.predictNext().getPrediction();
                    if (n1 == n2) {
                        p.correctPrediction();
                    } else {
                        p.wrongPrediction();
                    }
                    p.addNumber(n1);
                }
                label.setText(((double)((int)(p.getAccuracy() * 100000))/1000) + "%");
            }
        }
        action.addActionListener(new ButtonListener1());
        class ButtonListener2 implements ActionListener {
            public void actionPerformed(ActionEvent e) {
                JFrame newFrame = new JFrame();
                newFrame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
                JTextArea display = new JTextArea(20,40);
                JScrollPane scroller = new JScrollPane(display);
                display.setFont(Font.getFont("Arial"));
                display.setEditable(false);
                display.setWrapStyleWord(true);
                scroller.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
                scroller.setHorizontalScrollBarPolicy(ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
                newFrame.getContentPane().add(scroller);
                int counter = 0;
                for (Integer i : data) {
                    if (counter == 20) {
                        display.append("\n");
                        counter = 0;
                    }
                    display.append(i + "");
                    counter++;
                }
                newFrame.setSize(180,180);
                newFrame.setLocationByPlatform(true);
                newFrame.setVisible(true);
            }
        }
        showData.addActionListener(new ButtonListener2());
    }
}
