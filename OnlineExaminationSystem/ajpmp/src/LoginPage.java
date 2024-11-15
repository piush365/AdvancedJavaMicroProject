import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class LoginPage extends JFrame implements ActionListener {
    JTextField email_tf;
    JPasswordField password_tf;
    JButton loginbtn, rgstr_btn;

    public LoginPage() {
        setTitle("Login");
        setSize(500, 300);  
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setLocationRelativeTo(null);
        setLayout(null);

        Font font = new Font("Arial", Font.PLAIN, 18);

        JLabel email_lbl = new JLabel("Email:");
        email_lbl.setFont(font);
        email_lbl.setBounds(50, 40, 100, 30);

        email_tf = new JTextField();
        email_tf.setFont(font);
        email_tf.setBounds(150, 40, 250, 30);

        JLabel passwd_lbl = new JLabel("Password:");
        passwd_lbl.setFont(font);
        passwd_lbl.setBounds(50, 90, 100, 30);

        password_tf = new JPasswordField();
        password_tf.setFont(font);
        password_tf.setBounds(150, 90, 250, 30);

        loginbtn = new JButton("Login");
        loginbtn.setFont(font);
        loginbtn.setBackground(new Color(0, 123, 255));
        loginbtn.setForeground(Color.WHITE);
        loginbtn.setBounds(150, 150, 250, 40);  

        rgstr_btn = new JButton("Register as Student");
        rgstr_btn.setFont(font);
        rgstr_btn.setBackground(new Color(40, 167, 69));
        rgstr_btn.setForeground(Color.WHITE);
        rgstr_btn.setBounds(150, 200, 250, 40);  

        loginbtn.addActionListener(this);
        rgstr_btn.addActionListener(this);

        add(email_lbl);
        add(email_tf);
        add(passwd_lbl);
        add(password_tf);
        add(loginbtn);
        add(rgstr_btn);

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == loginbtn) {
            login();
        }
        if (ae.getSource() == rgstr_btn) {
            new StudentRegistration();
        }
    }

    void login() {
        String email = email_tf.getText();
        String password = new String(password_tf.getPassword());

        try {
            Connection con = DatabaseConnection.getConnection();
            String q1 = "SELECT user_id, role FROM users WHERE email = ? AND password = ?";
            PreparedStatement st = con.prepareStatement(q1, ResultSet.TYPE_SCROLL_INSENSITIVE, ResultSet.CONCUR_READ_ONLY);
            st.setString(1, email);
            st.setString(2, password);
            ResultSet rs = st.executeQuery();

            if (rs.next()) {
                String role = rs.getString("role");
                if (role.equalsIgnoreCase("admin")) {
                    new AdminDashboard();
                } else if (role.equalsIgnoreCase("student")) {
                    new StudentDashboard();
                }
                dispose();
            } else {
                JOptionPane.showMessageDialog(this, "Invalid email or password.");
            }
        } 
        catch (SQLException ex) {
            JOptionPane.showMessageDialog(this, "Error: " + ex.getMessage());
        }
    }

}
