import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import javax.swing.border.TitledBorder;

public class ML_WIN_PERCENTAGE extends JFrame {

    private JComboBox<String> hero1ComboBox;
    private JComboBox<String> hero2ComboBox;
    private JButton uploadButton;
    private JButton calculateButton;
    private JTextArea resultArea;
    private Map<String, double[]> heroStatsMap;

    public ML_WIN_PERCENTAGE() {
        // Set up the frame
        setTitle("Hero Win Percentage Calculator");
        setSize(600, 400);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(false); // Disable resizing
        setLayout(new BorderLayout());

        // Set a modern look and feel
        UIManager.put("Panel.background", new Color(45, 45, 45));
        UIManager.put("TextArea.background", new Color(60, 63, 65));
        UIManager.put("TextArea.foreground", Color.WHITE);
        UIManager.put("Button.background", new Color(70, 130, 180)); // Steel Blue
        UIManager.put("Button.foreground", Color.WHITE);
        UIManager.put("Label.foreground", Color.WHITE);

        // Create input panel
        JPanel inputPanel = new JPanel();
        inputPanel.setLayout(new GridLayout(3, 2)); // 3 rows, 2 columns

        // Hero 1 selection
        inputPanel.add(new JLabel("Hero 1:"));
        hero1ComboBox = new JComboBox<>();
        inputPanel.add(hero1ComboBox);

        // Hero 2 selection
        inputPanel.add(new JLabel("Hero 2:"));
        hero2ComboBox = new JComboBox<>();
        inputPanel.add(hero2ComboBox);

        // Upload button
        uploadButton = new JButton("Upload CSV");
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadCSV();
            }
        });
        inputPanel.add(uploadButton);

        // Calculate button
        calculateButton = new JButton("Calculate Win Percentage");
        calculateButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                calculateWinPercentage();
            }
        });
        inputPanel.add(calculateButton);

        // Add input panel to the frame
        add(inputPanel, BorderLayout.NORTH);

        // Result area
        resultArea = new JTextArea();
        resultArea.setEditable(false);
        resultArea.setBackground(new Color(60, 63, 65));
        resultArea.setForeground(Color.WHITE);
        resultArea.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.WHITE, 2), "Results", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        // Initialize hero stats map
        heroStatsMap = new HashMap<>();
    }

    private void uploadCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                br.readLine(); // Skip header
                heroStatsMap.clear(); // Clear existing stats
                hero1ComboBox.removeAllItems(); // Clear previous items
                hero2ComboBox.removeAllItems(); // Clear previous items
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length >= 8) { // Ensure there are enough columns
                        String heroName = values[0].trim().toLowerCase(); // Get hero name and convert to lowercase
                        double movementSpd = Double.parseDouble(values[6].trim());
                        double magicDefense = Double.parseDouble(values[7].trim());
                        double mana = Double.parseDouble(values[8].trim());
                        double hpRegen = Double.parseDouble(values[9].trim());
                        double physicalAtk = Double.parseDouble(values[10].trim());
                        double physicalDefense = Double.parseDouble(values[11].trim());
                        double hp = Double.parseDouble(values[12].trim());
                        double attackSpeed = Double.parseDouble(values[13].trim());

                        // Store hero stats in the map
                        heroStatsMap.put(heroName, new double[]{
                                movementSpd, magicDefense, mana, hpRegen,
                                physicalAtk, physicalDefense, hp, attackSpeed
                        });

                        // Add hero name to combo boxes
                        hero1ComboBox.addItem(capitalize(heroName));
                        hero2ComboBox.addItem(capitalize(heroName));
                    }
                }
                resultArea.append("File successfully uploaded!\n");
            } catch (IOException e) {
                resultArea.append("Error reading the file: " + e.getMessage() + "\n");
            }
        }
    }

    private void calculateWinPercentage() {
        String hero1 = hero1ComboBox.getSelectedItem().toString().trim().toLowerCase();
        String hero2 = hero2ComboBox.getSelectedItem().toString().trim().toLowerCase();

        if (!heroStatsMap.containsKey(hero1) || !heroStatsMap.containsKey(hero2)) {
            resultArea.append("One or both heroes not found. Please upload the CSV file again.\n");
            return;
        }

        double[] stats1 = heroStatsMap.get(hero1);
        double[] stats2 = heroStatsMap.get(hero2);

        // Calculate win percentage based on stats
        double score1 = calculateScore(stats1);
        double score2 = calculateScore(stats2);

        double winPercentage1 = (score1 / (score1 + score2)) * 100;
        double winPercentage2 = (score2 / (score1 + score2)) * 100;

        // Display results in the result area
        resultArea.setText(String.format("%s Win Percentage: %.2f%%\n%s Win Percentage: %.2f%%\n\n", 
            capitalize(hero1), winPercentage1, capitalize(hero2), winPercentage2));

        // Compare stats
        resultArea.append("Stat Comparison:\n");
        resultArea.append("--------------------------------------------------------------------------------------------------------------------------------------------\n");
        String[] statNames = {"Movement Speed", "Magic Defense", "Mana", "HP Regen", "Physical Attack", "Physical Defense", "HP", "Attack Speed"};
        
        for (int i = 0; i < statNames.length; i++) {
            double stat1 = stats1[i];
            double stat2 = stats2[i];
            String comparison;

            if (stat1 > stat2) {
                comparison = String.format("%s has %.2f more than %s (%.2f vs %.2f)", 
                    capitalize(hero1), stat1 - stat2, capitalize(hero2), stat1, stat2);
            } else if (stat2 > stat1) {
                comparison = String.format("%s has %.2f more than %s (%.2f vs %.2f)", 
                    capitalize(hero2), stat2 - stat1, capitalize(hero1), stat2, stat1);
            } else {
                comparison = String.format("Both heroes have the same %s (%.2f)", statNames[i], stat1);
            }

            resultArea.append(String.format("%s: %s\n", statNames[i], comparison));
        }
        resultArea.append("--------------------------------------------------------------------------------------------------------------------------------------------\n");
    }

    private double calculateScore(double[] stats) {
        // Example scoring based on stats
        return stats[0] * 0.2 + stats[1] * 0.15 + stats[2] * 0.1 + stats[3] * 0.1 +
               stats[4] * 0.25 + stats[5] * 0.1 + stats[6] * 0.1 + stats[7] * 0.05;
    }

    private String capitalize(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return name.substring(0, 1).toUpperCase() + name.substring(1);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            ML_WIN_PERCENTAGE frame = new ML_WIN_PERCENTAGE();
            frame.setVisible(true);
        });
    }
}
