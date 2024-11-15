import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class StudentRegistration extends JFrame implements ActionListener {
    JTextField name_tf;
    JTextField email_tf;
    JPasswordField passwd_tf;
    JButton rgstr_btn;

    public StudentRegistration() {
        setTitle("Student Registration");
        setSize(600, 350); 
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        setLayout(null); 

        Font lbl_font = new Font("Arial", Font.BOLD, 16);
        Font btn_font = new Font("Arial", Font.PLAIN, 14);

        JLabel name_lbl = new JLabel("Name:");
        name_tf = new JTextField();
        JLabel email_lbl = new JLabel("Email:");
        email_tf = new JTextField();
        JLabel passwordLabel = new JLabel("Password:");
        passwd_tf = new JPasswordField();

        rgstr_btn = new JButton("Register");

        name_lbl.setFont(lbl_font);
        email_lbl.setFont(lbl_font);
        passwordLabel.setFont(lbl_font);
        name_tf.setFont(new Font("Arial", Font.PLAIN, 14));
        email_tf.setFont(new Font("Arial", Font.PLAIN, 14));
        passwd_tf.setFont(new Font("Arial", Font.PLAIN, 14));
        rgstr_btn.setFont(btn_font);

        name_lbl.setBounds(50, 40, 100, 30);
        name_tf.setBounds(150, 40, 400, 30); 
        email_lbl.setBounds(50, 90, 100, 30);
        email_tf.setBounds(150, 90, 400, 30); 
        passwordLabel.setBounds(50, 140, 100, 30);
        passwd_tf.setBounds(150, 140, 400, 30); 
        rgstr_btn.setBounds(50, 200, 500, 40); 

        rgstr_btn.setBackground(new Color(0, 123, 255)); 
        rgstr_btn.setForeground(Color.WHITE); 
        rgstr_btn.setFont(new Font("Arial", Font.BOLD, 16)); 
        rgstr_btn.addActionListener(this);

        add(name_lbl);
        add(name_tf);
        add(email_lbl);
        add(email_tf);
        add(passwordLabel);
        add(passwd_tf);
        add(rgstr_btn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == rgstr_btn) {
            registerStudent();
        }
    }

    private void registerStudent() {
        String name = name_tf.getText();
        String email = email_tf.getText();
        String password = new String(passwd_tf.getPassword());

        if (name.isEmpty() || email.isEmpty() || password.isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please fill in all fields.");
            return;
        }

        try {
            Connection con = DatabaseConnection.getConnection();
            String q1 = "INSERT INTO users (name, email, password, role) VALUES (?, ?, ?, 'student')";
            PreparedStatement st = con.prepareStatement(q1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.setString(1, name);
            st.setString(2, email);
            st.setString(3, password);

            int count = st.executeUpdate();
            if (count > 0) {
                JOptionPane.showMessageDialog(this, "Registration successful! You can now log in.");
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Registration failed. Please try again.");
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Database Error: " + ex.getMessage());
        }
    }

    public static void main(String[] args) {
        new StudentRegistration();
    }
}
