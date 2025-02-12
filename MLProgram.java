import javax.swing.*;
import javax.swing.border.LineBorder;
import javax.swing.border.TitledBorder;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;

public class MLProgram extends JFrame {

    private JTable resultTable;
    private JButton uploadButton;
    private DefaultTableModel tableModel;

    public MLProgram() {
        // Set up the frame
        setTitle("Hero Names and HP Display");
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
        UIManager.put("ScrollPane.background", new Color(45, 45, 45));

        // Create table model and JTable
        tableModel = new DefaultTableModel(new Object[]{"Hero Name", "HP"}, 0);
        resultTable = new JTable(tableModel);
        resultTable.setFillsViewportHeight(true);
        resultTable.setBackground(new Color(60, 63, 65));
        resultTable.setForeground(Color.WHITE);
        resultTable.setGridColor(Color.LIGHT_GRAY);
        resultTable.setSelectionBackground(new Color(70, 130, 180));
        resultTable.setSelectionForeground(Color.WHITE);

        // Center align the text in the table cells
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment(JLabel.CENTER);
        resultTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer); // Hero Name
        resultTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer); // HP

        JScrollPane resultScrollPane = new JScrollPane(resultTable);
        resultScrollPane.setBorder(BorderFactory.createTitledBorder(
                new LineBorder(Color.WHITE, 2), "Hero Names and HP", TitledBorder.LEFT, TitledBorder.TOP,
                new Font("Arial", Font.BOLD, 14), Color.WHITE));

        // Create a panel for the button with GridBagLayout
        JPanel buttonPanel = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.fill = GridBagConstraints.HORIZONTAL;
        gbc.gridx = 0; // Column 0 for the label
        gbc.weightx = 1; // Allow label to take available space
        gbc.insets = new Insets(5, 5, 5, 5); // Add some padding

        // Add instruction label
        JLabel instructionLabel = new JLabel("Display the names of all heroes in lowercase along with their HP values.");
        instructionLabel.setForeground(Color.WHITE); // Set label color
        buttonPanel.add(instructionLabel, gbc); // Add label to the panel

        // Create and add the upload button
        uploadButton = new JButton("Upload CSV");
        uploadButton.setFocusPainted(false);
        uploadButton.setPreferredSize(new Dimension(100, 30)); // Set preferred size for the button (narrower)
        uploadButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                uploadCSV();
            }
        });

        // Add the button to the panel
        gbc.gridx = 1; // Column 1 for the button
        gbc.weightx = 0; // Button does not need to take extra space
        buttonPanel.add(uploadButton, gbc);

        // Add components to the frame
        add(buttonPanel, BorderLayout.NORTH);
        add(resultScrollPane, BorderLayout.CENTER);

        // Add component listener for resizing
        addComponentListener(new ComponentAdapter() {
            @Override
            public void componentResized(ComponentEvent e) {
                adjustComponents();
            }
        });
    }

    private void adjustComponents() {
        // Adjust the table size to fill the available space
        resultTable.setPreferredScrollableViewportSize(new Dimension(getWidth(), getHeight() - 100)); // Adjust height as needed
        resultTable.revalidate(); // Refresh the table
    }

    private void uploadCSV() {
        JFileChooser fileChooser = new JFileChooser();
        int returnValue = fileChooser.showOpenDialog(this);
        if (returnValue == JFileChooser.APPROVE_OPTION) {
            String filePath = fileChooser.getSelectedFile().getAbsolutePath();
            try (BufferedReader br = new BufferedReader(new FileReader(filePath))) {
                String line;
                br.readLine(); // Skip header
                tableModel.setRowCount(0); // Clear existing rows
                while ((line = br.readLine()) != null) {
                    String[] values = line.split(",");
                    if (values.length >= 13) { // Ensure there are enough columns
                        String heroName = values[0].trim().toLowerCase(); // Get hero name and convert to lowercase
                        String hp = values[12].trim(); // Get HP value
                        // Add a new row to the table model
                        tableModel.addRow(new Object[]{heroName, hp});
                    }
                }
            } catch (IOException e) {
                JOptionPane.showMessageDialog(this, "Error reading the file: " + e.getMessage(), "Error", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(() -> {
            MLProgram frame = new MLProgram();
            frame.setVisible(true);
        });
    }
}
