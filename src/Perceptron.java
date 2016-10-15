import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
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
        loadButton.addActionListener(e -> {
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
        });
    }

    private void createUIComponents() {
        coordinatePanel = new GPanel();
    }

    public static void main(String[] args) {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
        JFrame frame = new JFrame("Perceptron");
        frame.setContentPane(new Perceptron().layoutPanel);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/icon.png"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.pack();
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }
}

class GPanel extends JPanel {
    @Override
    public void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(250,0,250,500);
        g.drawLine(0,250,500,250);
    }
}
