import javax.swing.*;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.io.*;
import java.math.BigInteger;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Random;

public class Encryption {
    private int length;
    private BigInteger p, q, N, num, e, d;
    public Encryption() {
        length = 1024;
        generateEncryptionKey();
    }
    public Encryption(int len) {
        length = len;
        generateEncryptionKey();
    }
    public void generateEncryptionKey() {
        p = new BigInteger(length, 128, new Random());
        q = new BigInteger(length, 128, new Random());
        N = p.multiply(q);
        num = p.subtract(BigInteger.ONE).multiply(q.subtract(BigInteger.ONE)); // num = (p-1)(q-1)
        e = new BigInteger(length * 2, 128, new Random()).mod(num);
        while (num.gcd(e).compareTo(BigInteger.ONE) != 0) {
            e = new BigInteger(length * 2, 128, new Random()).mod(num);
        }
        d = e.modInverse(num);
    }
    public BigInteger encryptMessage(int msg) {
        return padMessage(BigInteger.valueOf(msg)).modPow(e, N);
    }
    public BigInteger decryptMessage(BigInteger msg) {
        return unpadMessage(msg.modPow(d, N));
    }
    private BigInteger padMessage(BigInteger message) {
        int numBits = (int) (Math.random() * 256);
        message = message.shiftLeft(numBits * 2);
        for (int i = 0; i < numBits; i++) {
            message = message.setBit(numBits + i);
        }
        return message;
    }
    private BigInteger unpadMessage(BigInteger padded) {
        return padded.shiftRight(padded.getLowestSetBit() * 2);
    }
    public static void main(String[] args) {
        Encryption encrypter = new Encryption();
        JFrame newFrame = new JFrame();
        newFrame.setSize(200,300);
        JLabel label = new JLabel("Encrypt a Message! (written to encrypted.txt)");
        JLabel label1 = new JLabel("Integer:");
        JLabel label2 = new JLabel("File:");
        JLabel label3 = new JLabel("Decrypt File: (default is encrypted.txt)");
        JTextField textField = new JTextField();
        JTextField textField1 = new JTextField();
        JTextField textField2 = new JTextField();
        class EnterListener implements KeyListener {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    try {
                        byte[] arr = encrypter.encryptMessage(Integer.parseInt(textField.getText())).toByteArray();
                        FileOutputStream outStream = new FileOutputStream("encrypted.txt");
                        outStream.write(arr);
                        outStream.close();
                        textField.setText("");
                    } catch (IOException i) {}
                }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        }
        class EnterListener1 implements KeyListener {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    try {
                        Writer w = new BufferedWriter(new OutputStreamWriter( new FileOutputStream("encrypted.txt"), "utf-8"));
                        BufferedReader br = new BufferedReader(new FileReader(textField1.getText()));
                        try {
                            String line = br.readLine();
                            while (line != null) {
                                w.write(encrypter.encryptMessage(Integer.parseInt(line)) + "");
                                line = br.readLine();
                            }
                        } catch (IOException io) {
                            System.out.println("Error reading file. Please try again.");
                        }
                    } catch (IOException i) {}
                }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        }
        class EnterListener2 implements KeyListener {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyChar() == '\n') {
                    String fileName = textField2.getText();
                    if (fileName.length() == 0) {
                        fileName = "encrypted.txt";
                    }
                    try {
                        Path path = Paths.get(fileName);
                        BigInteger num1 = new BigInteger(Files.readAllBytes(path));
                        textField2.setText("Decrypted: " + encrypter.decryptMessage(num1));
                    } catch (IOException i) {
                        System.out.println("Error reading file. Please try again.");
                    }
                }
            }
            public void keyReleased(KeyEvent e) {}
            public void keyTyped(KeyEvent e) {}
        }
        textField.addKeyListener(new EnterListener());
        textField1.addKeyListener(new EnterListener1());
        textField2.addKeyListener(new EnterListener2());
        newFrame.setLayout(new BoxLayout(newFrame.getContentPane(), BoxLayout.Y_AXIS));
        newFrame.add(label);
        newFrame.add(label1);
        newFrame.add(textField);
        newFrame.add(label2);
        newFrame.add(textField1);
        newFrame.add(label3);
        newFrame.add(textField2);
        newFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        newFrame.setVisible(true);
    }
}