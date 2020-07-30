import javafx.stage.FileChooser;
import javafx.stage.Stage;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.Arrays;

public class MainWindow extends JFrame implements Thread.UncaughtExceptionHandler, ActionListener {
    private final int WIN_WIDTH = 500;
    private final int WIN_HEIGHT = 400;

    private JFileChooser fileChooser = null;
    private final JTextArea textArea = new JTextArea();
    private final JButton loadFileButton = new JButton("Load file");
    private final JButton encryptButton = new JButton("Encrypt");
    private final JButton decryptButton = new JButton("Decrypt");
    private final JPanel buttonPanel = new JPanel();
    private final JPanel textAreaPanel = new JPanel();

    public static void main(String[] args) {
        SwingUtilities.invokeLater(new Runnable() {
            @Override
            public void run() {
                new MainWindow();
            }
        });
    }

    private MainWindow(){
        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        Thread.setDefaultUncaughtExceptionHandler(this);

        setLocationRelativeTo(null);
        setSize(WIN_WIDTH, WIN_HEIGHT);
        setTitle("Simple encoder");
        setAlwaysOnTop(false);

        buttonPanel.add(loadFileButton);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);

        loadFileButton.addActionListener(this);
        encryptButton.addActionListener(this);
        decryptButton.addActionListener(this);

        add(buttonPanel, BorderLayout.NORTH);
        add(textArea, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {

    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == loadFileButton){
            loadFile();
        }

    }

    private void loadFile() {

        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выбор директории");

        int result = fileChooser.showOpenDialog(MainWindow.this);
        if (result == JFileChooser.APPROVE_OPTION ) {
            textArea.setText(null);
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.ready()){
                    String str = reader.readLine();
                    str.replaceAll("[^A-Za-zА-Яа-яё0-9 ]", " ");
                    str.trim();
                    System.out.println(str);
                    textArea.append(str+System.lineSeparator());
                }

            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
