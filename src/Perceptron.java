import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.*;
import java.util.ArrayList;

public class Perceptron {
    private JPanel layoutPanel;
    private JPanel coordinatePanel;
    private JButton loadButton;
    private JButton generateButton;
    private JTextField learningTextField;
    private JLabel learningLabel;
    private JLabel thresholdLabel;
    private JTextField thresholdTextField;
    private JLabel trainingLabel;
    private JLabel trainingValue;
    private JLabel testingLabel;
    private JLabel testingValue;
    private JLabel loadValue;
    private JLabel weightsLabel;
    private JLabel weightsValue;
    private ArrayList<Float[]> input = new ArrayList<>();
    private ArrayList<Float> output = new ArrayList<>();

    public Perceptron() {
        loadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JFileChooser fileChooser = new JFileChooser();
                FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
                fileChooser.setFileFilter(filter);
                if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                    File loadedFile = fileChooser.getSelectedFile();
                    loadValue.setText(loadedFile.getPath());
                    input.clear();
                    output.clear();
                    try(BufferedReader br = new BufferedReader(new FileReader(loadedFile))) {
                        String line = br.readLine();
                        while (line != null) {
                            String[] lineSplit = line.split("\\s+");
                            Float[] numbers = new Float[lineSplit.length - 1];
                            for (int i = 0; i < lineSplit.length - 1; i++) {
                                numbers[i] = Float.parseFloat(lineSplit[i]);
                            }
                            input.add(numbers);
                            output.add(Float.parseFloat(lineSplit[lineSplit.length - 1]));
                            line = br.readLine();
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        });
    }

    public static void main(String[] args) {
        JFrame frame = new JFrame("Perceptron");
        frame.setContentPane(new Perceptron().layoutPanel);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/icon.png"));
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
    }
}
