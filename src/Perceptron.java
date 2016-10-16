import javax.swing.*;
import javax.swing.filechooser.FileNameExtensionFilter;
import java.awt.*;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.Line2D;
import java.io.*;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;

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
    private ArrayList<Float[]> input = new ArrayList<>();
    private ArrayList<Float> output = new ArrayList<>();
    private Color[] colorArray = {Color.CYAN, Color.RED, Color.BLUE, Color.GREEN, Color.YELLOW, Color.PINK};
    private Point mouse;
    private int Magnification = 50;

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
            Magnification = zoomerSlider.getValue();
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
        zoomerSlider = new JSlider();
        zoomerSlider.setBorder(
                BorderFactory.createTitledBorder(null,
                        Integer.toString(zoomerSlider.getValue()),
                        javax.swing.border.TitledBorder.CENTER,
                        javax.swing.border.TitledBorder.DEFAULT_POSITION));
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
            Graphics2D g2 = (Graphics2D) g;
            // Draw mouse position
            if (mouse != null) {
                Double mouse_x = (mouse.getX() - 250) / Magnification;
                Double mouse_y = (250 - mouse.getY()) / Magnification;
                DecimalFormat df = new DecimalFormat("####0.00");
                g2.drawString("(" + df.format(mouse_x) + ", " + df.format(mouse_y) + ")", 425, 20);
            }
            // Draw point of file
            if (input.size() > 0 && input.get(0).length == 2 && input.size() == output.size()) {
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
