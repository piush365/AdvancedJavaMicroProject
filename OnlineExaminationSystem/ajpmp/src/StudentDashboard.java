import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentDashboard extends JFrame implements ActionListener {
    JComboBox<String> examComboBox;
    int[] examIds;
    JButton startExamButton;

    public StudentDashboard() {
        setTitle("Student Dashboard");
        setSize(600, 300); // Increased size for better layout
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null); // Using null layout for setBounds positioning

        // Set custom font
        Font labelFont = new Font("Arial", Font.BOLD, 16);
        Font buttonFont = new Font("Arial", Font.PLAIN, 14);

        // Create components
        JLabel selectExamLabel = new JLabel("Select an Exam:");
        examComboBox = new JComboBox<>();
        startExamButton = new JButton("Start Exam");

        // Set font for components
        selectExamLabel.setFont(labelFont);
        examComboBox.setFont(new Font("Arial", Font.PLAIN, 14));
        startExamButton.setFont(buttonFont);

        // Set bounds for components
        selectExamLabel.setBounds(50, 50, 200, 30);
        examComboBox.setBounds(50, 100, 500, 30); // Increased width
        startExamButton.setBounds(50, 160, 500, 40); // Bigger button

        // Style the button to make it more prominent
        startExamButton.setBackground(new Color(0, 123, 255)); // Blue background
        startExamButton.setForeground(Color.WHITE); // White text
        startExamButton.setFocusPainted(false); // Remove the focus border
        startExamButton.setBorder(BorderFactory.createEmptyBorder(10, 10, 10, 10)); // Padding inside button
        startExamButton.setFont(new Font("Arial", Font.BOLD, 16)); // Bold font for button

        loadExams();
        startExamButton.addActionListener(this);

        // Add components to the frame
        add(selectExamLabel);
        add(examComboBox);
        add(startExamButton);

        // Make the window visible
        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == startExamButton) {
            startExam();
        }
    }

    private void loadExams() {
        try {
            Connection connection = DatabaseConnection.getConnection();
            String sql = "SELECT exam_id, exam_name FROM exams";
            PreparedStatement statement = connection.prepareStatement(sql, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet resultSet = statement.executeQuery();

            // Dynamically determine the number of exams
            resultSet.last();
            int rowCount = resultSet.getRow();
            resultSet.beforeFirst();

            // Create an array to store exam IDs
            examIds = new int[rowCount];
            int index = 0;

            while (resultSet.next()) {
                examIds[index] = resultSet.getInt("exam_id");
                examComboBox.addItem(resultSet.getString("exam_name"));
                index++;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    private void startExam() {
        int selectedExamIndex = examComboBox.getSelectedIndex();
        if (selectedExamIndex >= 0) {
            int examId = examIds[selectedExamIndex];  // Use array instead of ArrayList
            System.out.println("Starting exam with ID: " + examId); // Debugging output
            new ExamPage(examId);
            dispose();
        } else {
            JOptionPane.showMessageDialog(null, "Please select an exam.");
        }
    }

    public static void main(String[] args) {
        new StudentDashboard();
    }
}
