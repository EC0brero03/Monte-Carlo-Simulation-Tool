import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Random;

public class MonteCarloSimulationTool extends JFrame {
    private JTextField sampleInputField;
    private JButton startButton;
    private JLabel probabilityLabel;
    private ConvergencePanel convergencePanel;

    private int totalSamples;
    private double[] estimates;

    public MonteCarloSimulationTool() {
        setTitle("Monte Carlo Simulation Tool");
        setSize(800, 600);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        // Top panel for inputs and controls
        JPanel topPanel = new JPanel();
        topPanel.setLayout(new FlowLayout(FlowLayout.LEFT, 20, 10));
        topPanel.setBackground(new Color(40, 44, 52));

        JLabel sampleLabel = new JLabel("Number of Samples:");
        sampleLabel.setForeground(Color.WHITE);
        topPanel.add(sampleLabel);

        sampleInputField = new JTextField("1000", 10);
        sampleInputField.setFont(new Font("SansSerif", Font.PLAIN, 14));
        topPanel.add(sampleInputField);

        startButton = new JButton("Start Simulation");
        startButton.setFocusPainted(false);
        topPanel.add(startButton);

        probabilityLabel = new JLabel("Estimated Probability: N/A");
        probabilityLabel.setForeground(Color.WHITE);
        probabilityLabel.setFont(new Font("SansSerif", Font.BOLD, 16));
        topPanel.add(probabilityLabel);

        add(topPanel, BorderLayout.NORTH);

        // Panel for convergence visualization
        convergencePanel = new ConvergencePanel();
        convergencePanel.setBackground(Color.BLACK);
        add(convergencePanel, BorderLayout.CENTER);

        startButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startSimulation();
            }
        });
    }

    private void startSimulation() {
        String input = sampleInputField.getText();
        try {
            totalSamples = Integer.parseInt(input);
            if (totalSamples <= 0) {
                JOptionPane.showMessageDialog(this, "Please enter a positive integer for samples.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
                return;
            }
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Please enter a valid integer for samples.", "Invalid Input", JOptionPane.ERROR_MESSAGE);
            return;
        }

        estimates = new double[totalSamples];
        Random rand = new Random();
        int countLessThanHalf = 0;

        // Simulate and compute estimates at each step
        for (int i = 0; i < totalSamples; i++) {
            double sample = rand.nextDouble(); 
            if (sample < 0.5) {
                countLessThanHalf++;
            }
            estimates[i] = (double) countLessThanHalf / (i + 1);
        }

        probabilityLabel.setText(String.format("Estimated Probability P(X < 0.5): %.5f", estimates[totalSamples - 1]));
        convergencePanel.setEstimates(estimates);
    }

    // Panel to display convergence plot
    private class ConvergencePanel extends JPanel {
        private double[] estimates;

        public void setEstimates(double[] estimates) {
            this.estimates = estimates;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            if (estimates == null || estimates.length == 0) {
                drawInitialMessage(g);
                return;
            }

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int width = getWidth();
            int height = getHeight();
            int margin = 50;

            // Background
            g2.setColor(new Color(25, 25, 25));
            g2.fillRect(0, 0, width, height);

            // Axis
            g2.setColor(Color.WHITE);
            g2.drawLine(margin, height - margin, width - margin, height - margin); // x-axis
            g2.drawLine(margin, margin, margin, height - margin); // y-axis

            // Labels for axes
            g2.setFont(new Font("SansSerif", Font.PLAIN, 12));
            g2.drawString("Number of Samples", width / 2 - 40, height - 10);
            g2.drawString("Estimated Probability", 10, margin - 10);

            // True probability line (0.5)
            int yTrue = margin + (int) ((1 - 0.5) * (height - 2 * margin));
            g2.setColor(Color.RED);
            g2.setStroke(new BasicStroke(2));
            g2.drawLine(margin, yTrue, width - margin, yTrue);
            g2.drawString("True Probability = 0.5", width - margin - 140, yTrue - 5);

            // Convergence line chart
            g2.setColor(new Color(0, 150, 255));
            g2.setStroke(new BasicStroke(2));

            int n = estimates.length;
            double maxX = n - 1;
            double maxY = 1.0;

            // Plot points scaled
            for (int i = 1; i < n; i++) {
                int x1 = margin + (int) ((i - 1) / maxX * (width - 2 * margin));
                int y1 = margin + (int) ((maxY - estimates[i - 1]) * (height - 2 * margin));
                int x2 = margin + (int) (i / maxX * (width - 2 * margin));
                int y2 = margin + (int) ((maxY - estimates[i]) * (height - 2 * margin));
                g2.drawLine(x1, y1, x2, y2);
            }

            // Last estimate point
            int xLast = margin + (int) ((n - 1) / maxX * (width - 2 * margin));
            int yLast = margin + (int) ((maxY - estimates[n - 1]) * (height - 2 * margin));
            g2.fillOval(xLast - 4, yLast - 4, 8, 8);
        }

        private void drawInitialMessage(Graphics g) {
            g.setColor(Color.WHITE);
            g.setFont(new Font("SansSerif", Font.BOLD, 24));
            String message = "Enter number of samples and start simulation";
            FontMetrics fm = g.getFontMetrics();
            int x = (getWidth() - fm.stringWidth(message)) / 2;
            int y = getHeight() / 2;
            g.drawString(message, x, y);
        }
    }

    public static void main(String[] args) {
        
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (Exception ignored) {}

        SwingUtilities.invokeLater(() -> {
            MonteCarloSimulationTool tool = new MonteCarloSimulationTool();
            tool.setVisible(true);
        });
    }
}

