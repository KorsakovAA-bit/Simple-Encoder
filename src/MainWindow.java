import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.nio.charset.StandardCharsets;

public class MainWindow extends JFrame implements Thread.UncaughtExceptionHandler, ActionListener {
    private final int WIN_WIDTH = 500;
    private final int WIN_HEIGHT = 400;

    private JFileChooser fileChooser = null;
    private final JTextArea textArea = new JTextArea();
    private final JTextField keyWordField = new JTextField(15);
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
        JScrollPane scrollTextArea = new JScrollPane(textArea);


        buttonPanel.add(loadFileButton);
        buttonPanel.add(keyWordField);
        buttonPanel.add(encryptButton);
        buttonPanel.add(decryptButton);


        loadFileButton.addActionListener(this);
        encryptButton.addActionListener(this);
        decryptButton.addActionListener(this);

        add(buttonPanel, BorderLayout.NORTH);
        add(scrollTextArea, BorderLayout.CENTER);

        setVisible(true);
    }

    @Override
    public void uncaughtException(Thread t, Throwable e) {
        e.printStackTrace();
        StackTraceElement[] ste = e.getStackTrace();
        String msg = String.format("Exception in thread %s: \n\t%s: $s\n\t at \n\t%s", t.getName(), e.getClass().getCanonicalName(), e.getMessage(), ste);
        JOptionPane.showMessageDialog(null, msg, "Exception", JOptionPane.ERROR_MESSAGE);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        Object src = e.getSource();
        if(src == loadFileButton){
            loadFile();
        } else if(src == encryptButton){
            encrypt(src);
        } else if(src == decryptButton) {
            decrypt(src);
        }else{
            throw new RuntimeException("Unknown source: " + src);
        }

    }

    private void decrypt(Object src) {
        char[] keyWordInChar = keyWordField.getText().toCharArray();
        char[] textForDecryptInChar = textArea.getText().toCharArray();
        String result = "";
        int resultValue = 0;
        for (int i = 0; i < keyWordInChar.length; i++) {
            resultValue += keyWordInChar[i];
        }
        if (resultValue == 0) {
            JOptionPane.showMessageDialog(null, "Please enter the key", "Exception", JOptionPane.INFORMATION_MESSAGE);
        } else {
            resultValue = resultValue / keyWordInChar.length * 3;

            for (int i = 0; i < textForDecryptInChar.length; i++) {
                if (textForDecryptInChar[i] != ' ') {
                    textForDecryptInChar[i] -= resultValue;
                }
                result += textForDecryptInChar[i];
            }
            textArea.setText(result);
        }
    }

    private void encrypt(Object src) {
        String textForEncrypt = textArea.getText();
        String keyWord = keyWordField.getText();
        char[] keyWordInChar = keyWord.toCharArray();
        char[] textForEncryptInChar = textForEncrypt.toCharArray();
        textForEncrypt = "";

        int resultValue = 0;
        for (int i = 0; i < keyWordInChar.length; i++) {
            resultValue += keyWordInChar[i];
        }
        if(resultValue == 0){
            JOptionPane.showMessageDialog(null, "Please enter the key", "Exception", JOptionPane.INFORMATION_MESSAGE);
        } else {
            resultValue = resultValue / keyWordInChar.length * 3;

            for (int i = 0; i < textForEncryptInChar.length; i++) {
                if (textForEncryptInChar[i] != ' ') {
                    textForEncryptInChar[i] += resultValue;
                }
                textForEncrypt += textForEncryptInChar[i];
            }

            fileChooser.setDialogTitle("Сохранение файла");
            fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
            int result = fileChooser.showSaveDialog(MainWindow.this);
            if (result == JFileChooser.APPROVE_OPTION )

                try (OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(fileChooser.getSelectedFile()), StandardCharsets.UTF_8)) {
                    BufferedWriter writer = new BufferedWriter(osw);
                    writer.write(textForEncrypt);
                    JOptionPane.showMessageDialog(MainWindow.this, "Файл '" + fileChooser.getSelectedFile() +
                            " ) сохранен");

                    writer.close();
                } catch (IOException ex) {
                    throw new RuntimeException("File is not found " + src);
                }
        }
    }

    private void loadFile() {
        fileChooser = new JFileChooser();
        fileChooser.setDialogTitle("Выбор директории");
        fileChooser.setFileSelectionMode(JFileChooser.FILES_ONLY);
        int result = fileChooser.showOpenDialog(MainWindow.this);
        if (result == JFileChooser.APPROVE_OPTION ) {
            textArea.setText(null);
            File file = fileChooser.getSelectedFile();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                while (reader.ready()){
                    String str = reader.readLine();
                    str.replaceAll("[^A-Za-zА-Яа-яё0-9 ]", " ");
                    str.trim();
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
