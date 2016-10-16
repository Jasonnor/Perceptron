import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.*;
import java.awt.geom.Line2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.concurrent.ThreadLocalRandom;

public class Perceptron {
    private JPanel layoutPanel;
    private JPanel coordinatePanel;
    private JButton loadButton;
    private JLabel loadValue;
    private JButton generateButton;
    private JLabel learningLabel;
    private JTextField learningTextField;
    private JLabel thresholdLabel;
    private JTextField thresholdTextField;
    private JLabel trainingLabel;
    private JLabel trainingValue;
    private JLabel testingLabel;
    private JLabel testingValue;
    private JLabel weightsLabel;
    private JLabel weightsValue;
    private JSlider zoomerSlider;
    private JLabel zoomerLabel;
    private DecimalFormat df = new DecimalFormat("####0.00");
    private Color[] colorArray = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PINK};
    private Float[] yTable = {1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f};
    private ArrayList<Float[]> input = new ArrayList<>();
    private ArrayList<Float> output = new ArrayList<>();
    private ArrayList<Float> weight = new ArrayList<>();
    private Float[] weightFinal;
    private Point mouse;
    private int magnification = 50;
    private float rate = 0.5f;
    private float threshold = 0;
    // TODO - Editable
    private int rangeMin = -1;
    private int rangeMax = 1;
    private int maxTrainTimes = 1000;

    private Perceptron() {
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser);
            }
        });
        zoomerSlider.addChangeListener(e -> {
            zoomerSlider.setBorder(
                    BorderFactory.createTitledBorder(null,
                            Integer.toString(zoomerSlider.getValue()),
                            javax.swing.border.TitledBorder.CENTER,
                            javax.swing.border.TitledBorder.DEFAULT_POSITION));
            magnification = zoomerSlider.getValue();
            coordinatePanel.repaint();
        });
        coordinatePanel.addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                mouse = e.getPoint();
                coordinatePanel.repaint();
            }
        });
        learningTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changeRate();
            }

            public void removeUpdate(DocumentEvent e) {
                changeRate();
            }

            public void insertUpdate(DocumentEvent e) {
                changeRate();
            }

            void changeRate() {
                try {
                    rate = Float.valueOf(learningTextField.getText());
                } catch (NumberFormatException e) {
                    System.out.println("Error learning rate input!");
                    rate = 0.5f;
                }
            }
        });

        thresholdTextField.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changeThreshold();
            }

            public void removeUpdate(DocumentEvent e) {
                changeThreshold();
            }

            public void insertUpdate(DocumentEvent e) {
                changeThreshold();
            }

            void changeThreshold() {
                try {
                    threshold = Float.valueOf(thresholdTextField.getText());
                } catch (NumberFormatException e) {
                    System.out.println("Error threshold input!");
                    threshold = 0;
                }
            }
        });
    }

    private void loadFile(JFileChooser fileChooser) {
        File loadedFile = fileChooser.getSelectedFile();
        loadValue.setText(loadedFile.getPath());
        input.clear();
        output.clear();
        weight.clear();
        weightFinal = null;
        try (BufferedReader br = new BufferedReader(new FileReader(loadedFile))) {
            String line = br.readLine();
            while (line != null) {
                // Split by space or tab
                String[] lineSplit = line.split("\\s+");
                // Remove empty elements
                lineSplit = Arrays.stream(lineSplit).
                        filter(s -> (s != null && s.length() > 0)).
                        toArray(String[]::new);
                Float[] numbers = new Float[lineSplit.length];
                for (int i = 0; i < lineSplit.length - 1; i++) {
                    numbers[i] = Float.parseFloat(lineSplit[i]);
                }
                numbers[lineSplit.length - 1] = -1.0f;
                input.add(numbers);
                output.add(Float.parseFloat(lineSplit[lineSplit.length - 1]));
                line = br.readLine();
            }
            weight.add(threshold);
            for (int i = 0; i < input.get(0).length - 1; i++) {
                weight.add((float) getRandomNumber());
            }
            // TODO check it again, can I change it?
            if (!output.contains(0f)) {
                for (int i = 0; i < output.size(); i++) {
                    output.set(i, output.get(i) - 1);
                }
            }
            trainPerceptron();
            coordinatePanel.repaint();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void trainPerceptron() {
        int times = 0, correct = 0;
        while (times < maxTrainTimes) {
            correct = 0;
            for (int cycle = 0; cycle < input.size(); cycle++) {
                Float[] x = input.get(cycle);
                Float sum = 0f;
                for (int i = 0; i < weight.size(); i++) {
                    sum += weight.get(i) * x[i];
                }
                Float fx = Math.signum(sum);
                Float y = yTable[Math.round(output.get(cycle))];
                Float e = y - fx;
                if (e == 0) ++correct;
                for (int i = 0; i < weight.size(); i++) {
                    weight.set(i, weight.get(i) + rate * e * x[i]);
                }
            }
            if (correct == input.size()) break;
            ++times;
        }
        StringBuilder weightOutput = new StringBuilder("(");
        weightFinal = weight.toArray(new Float[weight.size()]);
        weightOutput.append(df.format(weightFinal[0]));
        for (int i = 1; i < weightFinal.length - 1; i++) {
            weightOutput.append(", ").append(df.format(weightFinal[i]));
        }
        weightOutput.append(")");
        System.out.println("Convergence Times: " + times);
        System.out.println("Synaptic Weights: " + weightOutput);
        System.out.println("Final Threshold: " + weightFinal[weightFinal.length - 1]);
        System.out.println("Training Recognition Rate: " + (float) correct / input.size() * 100 + "%");
    }

    private int getRandomNumber() {
        return ThreadLocalRandom.current().nextInt(rangeMin, rangeMax + 1);
    }

    private void createUIComponents() {
        coordinatePanel = new GPanel();
        zoomerSlider = new JSlider();
        zoomerSlider.setBorder(
                BorderFactory.createTitledBorder(null,
                        Integer.toString(zoomerSlider.getValue()),
                        javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION));
    }

    private Float[] convertCoordinate(Float[] oldPoint) {
        Float[] newPoint = new Float[2];
        newPoint[0] = (oldPoint[0] * magnification) + 250;
        newPoint[1] = 250 - (oldPoint[1] * magnification);
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
            //TODO - option for scale and grid
            Graphics2D g2 = (Graphics2D) g;
            g2.setStroke(new BasicStroke(2));
            // Draw mouse position
            if (mouse != null) {
                Double mouse_x = (mouse.getX() - 250) / magnification;
                Double mouse_y = (250 - mouse.getY()) / magnification;
                g2.drawString("(" + df.format(mouse_x) + ", " + df.format(mouse_y) + ")", 420, 20);
            }
            // Draw point of file
            if (input.size() > 0 && input.get(0).length == 3 && input.size() == output.size()) {
                for (int i = 0; i < input.size() && i < output.size(); i++) {
                    Float[] point = convertCoordinate(input.get(i));
                    g2.setColor(colorArray[Math.round(output.get(i))]);
                    g2.draw(new Line2D.Double(point[0], point[1], point[0], point[1]));
                }
            }
            // Draw line of perceptron
            if (weightFinal != null && weightFinal.length == 3) {
                g2.setColor(Color.MAGENTA);
                Float[] lineStart = convertCoordinate(
                        new Float[]{-250.0f / magnification,
                                (weightFinal[2] + 250.0f / magnification * weightFinal[0]) / weightFinal[1]});
                Float[] lineEnd = convertCoordinate(
                        new Float[]{250.0f / magnification,
                                (weightFinal[2] - 250.0f / magnification * weightFinal[0]) / weightFinal[1]});
                g2.draw(new Line2D.Double(lineStart[0], lineStart[1], lineEnd[0], lineEnd[1]));
            }
        }
    }
}
