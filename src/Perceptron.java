import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

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
    // TODO - Make it resizeable
    private int Magnification = 50;
    private Color[] colorArray = {Color.CYAN, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK};

    private Perceptron() {
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser);
            }
        });
    }

    private void loadFile(JFileChooser fileChooser) {
        File loadedFile = fileChooser.getSelectedFile();
        loadValue.setText(loadedFile.getPath());
        input.clear();
        output.clear();
        try (BufferedReader br = new BufferedReader(new FileReader(loadedFile))) {
            String line = br.readLine();
            while (line != null) {
                // Split by space or tab
                String[] lineSplit = line.split("\\s+");
                // Remove empty elements
                lineSplit = Arrays.stream(lineSplit).
                        filter(s -> (s != null && s.length() > 0)).
                        toArray(String[]::new);
                Float[] numbers = new Float[lineSplit.length - 1];
                for (int i = 0; i < lineSplit.length - 1; i++) {
                    numbers[i] = Float.parseFloat(lineSplit[i]);
                }
                input.add(numbers);
                output.add(Float.parseFloat(lineSplit[lineSplit.length - 1]));
                line = br.readLine();
            }
            coordinatePanel.repaint();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void createUIComponents() {
        coordinatePanel = new GPanel();
    }

    private Float[] convertCoordinate(Float[] oldPoint) {
        Float[] newPoint = new Float[2];
        newPoint[0] = (oldPoint[0] * Magnification) + 250;
        newPoint[1] = 250 - (oldPoint[1] * Magnification);
        return newPoint;
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

    private class GPanel extends JPanel {
        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawLine(250, 0, 250, 500);
            g.drawLine(0, 250, 500, 250);
            if (input.size() > 0 && input.get(0).length == 2 && input.size() == output.size()) {
                Graphics2D g2 = (Graphics2D) g;
                g2.setStroke(new BasicStroke(2));
                for (int i = 0; i < input.size() && i < output.size(); i++) {
                    Float[] point = convertCoordinate(input.get(i));
                    g2.setColor(colorArray[Math.round(output.get(i))]);
                    g2.draw(new Line2D.Double(point[0], point[1], point[0], point[1]));
                }
            }
        }
    }
}
