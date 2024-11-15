import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.io.*;
import java.sql.*;

public class AdminDashboard extends JFrame implements ActionListener {
    JComboBox<String> exam_cmb;
    int[] exam_ids;
    JButton upload_btn, report_btn;

    public AdminDashboard() {
        setTitle("Admin Dashboard");
        setSize(600, 400);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null); 

   
        exam_cmb = new JComboBox<String>();
        loadExams();
        exam_cmb.setBounds(150, 30, 300, 40); 

  
        upload_btn = new JButton("Upload Exam Questions");
        upload_btn.setBounds(150, 100, 300, 40);
        upload_btn.addActionListener(this);

        report_btn = new JButton("Generate Exam Report");
        report_btn.setBounds(150, 160, 300, 40);
        report_btn.addActionListener(this);

    
        add(exam_cmb);
        add(upload_btn);
        add(report_btn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == upload_btn) {
            uploadExamFile();
        }
        if (ae.getSource() == report_btn) {
            generateReport();
        }
    }

    void loadExams() {
        try {
            Connection con = DatabaseConnection.getConnection();
            String q1 = "SELECT exam_id, exam_name FROM exams";
            PreparedStatement st = con.prepareStatement(q1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            ResultSet rs = st.executeQuery();

            int examCount = 0;
            while (rs.next()) {
                examCount++;
            }

            exam_ids = new int[examCount];
            rs.beforeFirst(); 

            int index = 0;
            while (rs.next()) {
                exam_ids[index] = rs.getInt("exam_id");
                exam_cmb.addItem(rs.getString("exam_name"));
                index++;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    void uploadExamFile() {
        FileDialog fd1 = new FileDialog(this, "Select Exam questions file", FileDialog.LOAD);
        fd1.setVisible(true);
    
        String filePath = fd1.getDirectory() + fd1.getFile();
        if (filePath != null && !filePath.isEmpty()) {
            File file = new File(filePath);
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));
                Connection con = DatabaseConnection.getConnection();
    
                String exam_name = JOptionPane.showInputDialog(this, "Enter the exam name:");
                String exam_date = JOptionPane.showInputDialog(this, "Enter the exam date (YYYY-MM-DD):");
    
                if (exam_name == null || exam_date == null || exam_name.isEmpty() || exam_date.isEmpty()) {
                    JOptionPane.showMessageDialog(this, "Exam name and date cannot be empty.");
                    reader.close();
                    return;
                }
    
                String q1 = "INSERT INTO exams (exam_name, exam_date) VALUES (?, ?)";
                PreparedStatement st1 = con.prepareStatement(q1);
                st1.setString(1, exam_name);
                st1.setString(2, exam_date);
                st1.executeUpdate();
    
                String q2 = "SELECT exam_id FROM exams WHERE exam_name = ? AND exam_date = ?";
                PreparedStatement st2 = con.prepareStatement(q2);
                st2.setString(1, exam_name);
                st2.setString(2, exam_date);
                ResultSet rs = st2.executeQuery();

                int examId = rs.getInt("exam_id");
    
                String insertQuestionSql = "INSERT INTO questions (exam_id, question, option1, option2, option3, option4, answer) VALUES (?, ?, ?, ?, ?, ?, ?)";
                PreparedStatement questionStatement = con.prepareStatement(insertQuestionSql);
    
                String line;
                while ((line = reader.readLine()) != null) {
                    String[] parts = line.split(";");
                    if (parts.length == 6) {
                        questionStatement.setInt(1, examId);
                        questionStatement.setString(2, parts[0]);
                        questionStatement.setString(3, parts[1]);
                        questionStatement.setString(4, parts[2]);
                        questionStatement.setString(5, parts[3]);
                        questionStatement.setString(6, parts[4]);
                        questionStatement.setString(7, parts[5]);
                        questionStatement.executeUpdate();
                    }
                }
    
                reader.close();
                JOptionPane.showMessageDialog(this, "Exam questions uploaded successfully.");
                loadExams();
    
            } catch (Exception ex) {
                JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
            }
        }
    }
    

    private void generateReport() {
        int selected_index = exam_cmb.getSelectedIndex();
        if (selected_index >= 0) {
            int examId = exam_ids[selected_index];

            FileDialog fileDialog = new FileDialog(new Frame(), "Save Report", FileDialog.SAVE);
            fileDialog.setVisible(true);

            String fileName = fileDialog.getFile();
            String directory = fileDialog.getDirectory();

            if (fileName != null) {
                File file = new File(directory + fileName);

                try {
                    BufferedWriter writer = new BufferedWriter(new FileWriter(file));
                    Connection con = DatabaseConnection.getConnection();

                    String q1 = "SELECT email FROM users";
                    PreparedStatement st1 = con.prepareStatement(q1);
                    ResultSet usersResultSet = st1.executeQuery();

                    String q2 = "SELECT user_id, score FROM scores WHERE exam_id = ?";
                    PreparedStatement st2 = con.prepareStatement(q2);
                    st2.setInt(1, examId);
                    ResultSet scoresResultSet = st2.executeQuery();

                    writer.write("Student Email\tScore\n");

                    while (usersResultSet.next()) {
                        String email = usersResultSet.getString("email");
                        int score = -1;

                        while (scoresResultSet.next()) {
                            if (email.equals(scoresResultSet.getString("user_id"))) {
                                score = scoresResultSet.getInt("score");
                                break;
                            }
                        }

                        writer.write(email + "\t" + score + "\n");
                    }

                    writer.close();
                    JOptionPane.showMessageDialog(this, "Report generated successfully.");
                } catch (Exception ex) {
                    JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
                }
            }
        } else {
            JOptionPane.showMessageDialog(this, "Please select an exam.");
        }
    }

    public static void main(String[] args) {
        new AdminDashboard();
    }
}
