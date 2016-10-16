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
import java.util.Random;

import static javax.swing.border.TitledBorder.CENTER;
import static javax.swing.border.TitledBorder.DEFAULT_POSITION;

public class Perceptron {
    private static JMenuItem loadMenuItem;
    private static JMenuItem generateMenuItem;
    private static JFrame frame;
    private JPanel layoutPanel;
    private JPanel coordinatePanel;
    private JButton loadButton;
    private JLabel loadValue;
    private JButton generateButton;
    private JTextField learningTextField;
    private JTextField thresholdTextField;
    private JLabel trainingValue;
    private JLabel testingValue;
    private JLabel weightsValue;
    private JSlider zoomerSlider;
    private JLabel timesValue;
    private JLabel fThresholdValue;
    private JTextField maxTimesValue;
    private JTextField wRangeMinValue;
    private JTextField wRangeMaxValue;
    private DecimalFormat df = new DecimalFormat("####0.00");
    private Color[] colorArray = {Color.BLUE, Color.RED, Color.GREEN, Color.YELLOW, Color.CYAN, Color.PINK};
    private Float[] yTable = {1.0f, -1.0f, 1.0f, -1.0f, 1.0f, -1.0f};
    private ArrayList<Float[]> input = new ArrayList<>();
    private ArrayList<Float> output = new ArrayList<>();
    private ArrayList<Float> weight = new ArrayList<>();
    private Float[] weightFinal;
    private Point mouse;
    private int magnification = 50;
    private float rate = 0.1f;
    private float threshold = 0;
    private float minRange = -0.5f;
    private float maxRange = 0.5f;
    private int maxTimes = 1000;

    private Perceptron() {
        loadButton.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser);
            }
        });
        loadMenuItem.addActionListener(e -> {
            JFileChooser fileChooser = new JFileChooser();
            FileNameExtensionFilter filter = new FileNameExtensionFilter("Text Files(*.txt)", "txt", "text");
            fileChooser.setFileFilter(filter);
            if (fileChooser.showOpenDialog(layoutPanel) == JFileChooser.APPROVE_OPTION) {
                loadFile(fileChooser);
            }
        });
        generateButton.addActionListener(e -> trainPerceptron());
        generateMenuItem.addActionListener(e -> trainPerceptron());
        zoomerSlider.addChangeListener(e -> {
            zoomerSlider.setBorder(
                    BorderFactory.createTitledBorder(null,
                            Integer.toString(zoomerSlider.getValue()), CENTER, DEFAULT_POSITION));
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
                    alertBackground(learningTextField, false);
                    rate = Float.valueOf(learningTextField.getText());
                    trainPerceptron();
                } catch (NumberFormatException e) {
                    alertBackground(learningTextField, true);
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
                    alertBackground(thresholdTextField, false);
                    threshold = Float.valueOf(thresholdTextField.getText());
                    trainPerceptron();
                } catch (NumberFormatException e) {
                    alertBackground(thresholdTextField, true);
                    threshold = 0;
                }
            }
        });
        maxTimesValue.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changeMaxTimes();
            }

            public void removeUpdate(DocumentEvent e) {
                changeMaxTimes();
            }

            public void insertUpdate(DocumentEvent e) {
                changeMaxTimes();
            }

            void changeMaxTimes() {
                try {
                    alertBackground(maxTimesValue, false);
                    maxTimes = Integer.valueOf(maxTimesValue.getText());
                    trainPerceptron();
                } catch (NumberFormatException e) {
                    alertBackground(maxTimesValue, true);
                    maxTimes = 1000;
                }
            }
        });
        wRangeMinValue.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changeMinRange();
            }

            public void removeUpdate(DocumentEvent e) {
                changeMinRange();
            }

            public void insertUpdate(DocumentEvent e) {
                changeMinRange();
            }

            void changeMinRange() {
                try {
                    if (Float.valueOf(wRangeMinValue.getText()) > maxRange)
                        alertBackground(wRangeMinValue, true);
                    else {
                        alertBackground(wRangeMinValue, false);
                        minRange = Float.valueOf(wRangeMinValue.getText());
                        trainPerceptron();
                    }
                } catch (NumberFormatException e) {
                    alertBackground(wRangeMinValue, true);
                    minRange = -0.5f;
                }
            }
        });
        wRangeMaxValue.getDocument().addDocumentListener(new DocumentListener() {
            public void changedUpdate(DocumentEvent e) {
                changeMaxRange();
            }

            public void removeUpdate(DocumentEvent e) {
                changeMaxRange();
            }

            public void insertUpdate(DocumentEvent e) {
                changeMaxRange();
            }

            void changeMaxRange() {
                try {
                    if (Float.valueOf(wRangeMaxValue.getText()) < minRange)
                        alertBackground(wRangeMaxValue, true);
                    else {
                        alertBackground(wRangeMaxValue, false);
                        maxRange = Float.valueOf(wRangeMaxValue.getText());
                        trainPerceptron();
                    }
                } catch (NumberFormatException e) {
                    alertBackground(wRangeMaxValue, true);
                    maxRange = 0.5f;
                }
            }
        });
    }

    private void alertBackground(JTextField textField, boolean alert) {
        if (alert)
            textField.setBackground(Color.PINK);
        else
            textField.setBackground(Color.WHITE);
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
                Float[] numbers = new Float[lineSplit.length];
                for (int i = 0; i < lineSplit.length - 1; i++) {
                    numbers[i] = Float.parseFloat(lineSplit[i]);
                }
                numbers[lineSplit.length - 1] = -1.0f;
                input.add(numbers);
                output.add(Float.parseFloat(lineSplit[lineSplit.length - 1]));
                line = br.readLine();
            }
            if (!output.contains(0f)) {
                for (int i = 0; i < output.size(); i++) {
                    output.set(i, output.get(i) - 1);
                }
            }
            generateButton.setEnabled(true);
            trainPerceptron();
        } catch (IOException e1) {
            e1.printStackTrace();
        }
    }

    private void trainPerceptron() {
        if (input.size() == 0) return;
        weightFinal = null;
        weight.clear();
        weight.add(threshold);
        for (int i = 0; i < input.get(0).length - 1; i++) {
            weight.add(getRandomNumber());
        }
        int times = 0, correct = 0;
        while (times < maxTimes) {
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
        timesValue.setText(String.valueOf(times));
        weightsValue.setText(weightOutput.toString());
        fThresholdValue.setText(weightFinal[weightFinal.length - 1].toString());
        trainingValue.setText((float) correct / input.size() * 100 + "%");
        coordinatePanel.repaint();
    }

    private Float getRandomNumber() {
        Random r = new Random();
        return minRange + (maxRange - minRange) * r.nextFloat();
    }

    private void createUIComponents() {
        coordinatePanel = new GPanel();
        zoomerSlider = new JSlider();
        zoomerSlider.setBorder(
                BorderFactory.createTitledBorder(null,
                        Integer.toString(zoomerSlider.getValue()), CENTER, DEFAULT_POSITION));
    }

    private Float[] convertCoordinate(Float[] oldPoint) {
        Float[] newPoint = new Float[2];
        newPoint[0] = (oldPoint[0] * magnification) + 250;
        newPoint[1] = 250 - (oldPoint[1] * magnification);
        return newPoint;
    }

    public static void main(String[] args) {
        JMenuBar menuBar = new JMenuBar();
        JMenu filesMenu = new JMenu("Files");
        JMenu skinsMenu = new JMenu("Skins");
        // Files menu
        loadMenuItem = new JMenuItem("Load", KeyEvent.VK_L);
        generateMenuItem = new JMenuItem("Generate", KeyEvent.VK_G);
        filesMenu.setMnemonic(KeyEvent.VK_F);
        filesMenu.add(loadMenuItem);
        filesMenu.add(generateMenuItem);
        menuBar.add(filesMenu);
        // Skins menu
        skinsMenu.setMnemonic(KeyEvent.VK_S);
        ButtonGroup group = new ButtonGroup();
        JRadioButtonMenuItem skinsMetalMenuItem = new JRadioButtonMenuItem("Metal");
        skinsMetalMenuItem.setMnemonic(KeyEvent.VK_M);
        skinsMenu.add(skinsMetalMenuItem);
        group.add(skinsMetalMenuItem);
        skinsMetalMenuItem.addActionListener(e -> changeLAF(UIManager.getCrossPlatformLookAndFeelClassName(), frame));
        JRadioButtonMenuItem skinsDefaultMenuItem = new JRadioButtonMenuItem("Default");
        skinsDefaultMenuItem.setMnemonic(KeyEvent.VK_D);
        skinsMenu.add(skinsDefaultMenuItem);
        group.add(skinsDefaultMenuItem);
        skinsDefaultMenuItem.addActionListener(e -> changeLAF(UIManager.getSystemLookAndFeelClassName(), frame));
        JRadioButtonMenuItem skinsMotifMenuItem = new JRadioButtonMenuItem("Motif");
        skinsMotifMenuItem.setMnemonic(KeyEvent.VK_M);
        skinsMenu.add(skinsMotifMenuItem);
        group.add(skinsMotifMenuItem);
        skinsMotifMenuItem.addActionListener(e -> changeLAF("com.sun.java.swing.plaf.motif.MotifLookAndFeel", frame));
        JRadioButtonMenuItem skinsGTKMenuItem = new JRadioButtonMenuItem("GTK");
        skinsGTKMenuItem.setMnemonic(KeyEvent.VK_G);
        skinsMenu.add(skinsGTKMenuItem);
        group.add(skinsGTKMenuItem);
        skinsGTKMenuItem.addActionListener(e -> changeLAF("com.sun.java.swing.plaf.gtk.GTKLookAndFeel", frame));
        JRadioButtonMenuItem skinsWindowsMenuItem = new JRadioButtonMenuItem("Windows");
        skinsWindowsMenuItem.setMnemonic(KeyEvent.VK_G);
        skinsMenu.add(skinsWindowsMenuItem);
        group.add(skinsWindowsMenuItem);
        skinsWindowsMenuItem.addActionListener(e -> changeLAF("com.sun.java.swing.plaf.windows.WindowsLookAndFeel", frame));
        JRadioButtonMenuItem skinsNimbusMenuItem = new JRadioButtonMenuItem("Nimbus");
        skinsNimbusMenuItem.setMnemonic(KeyEvent.VK_N);
        skinsNimbusMenuItem.setSelected(true);
        skinsMenu.add(skinsNimbusMenuItem);
        group.add(skinsNimbusMenuItem);
        skinsNimbusMenuItem.addActionListener(e -> changeLAF("Nimbus", frame));
        menuBar.add(skinsMenu);
        // Main frame
        frame = new JFrame("Perceptron");
        frame.setContentPane(new Perceptron().layoutPanel);
        frame.setIconImage(Toolkit.getDefaultToolkit().getImage("src/icon.png"));
        frame.setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);
        frame.setJMenuBar(menuBar);
        changeLAF("Nimbus", frame);
        frame.setVisible(true);
        frame.setLocationRelativeTo(null);
    }

    private static void changeLAF(String name, JFrame frame) {
        try {
            if (name.equals("Nimbus")) {
                for (UIManager.LookAndFeelInfo info : UIManager.getInstalledLookAndFeels())
                    if ("Nimbus".equals(info.getName())) {
                        UIManager.setLookAndFeel(info.getClassName());
                        break;
                    }
            } else {
                UIManager.setLookAndFeel(name);
            }
            SwingUtilities.updateComponentTreeUI(frame);
            frame.pack();
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e1) {
            System.out.println("Failed to load the skin!");
        }
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
