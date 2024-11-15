import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.sql.*;

public class ExamPage extends JFrame implements ActionListener {
    String[] questions, options1, options2, options3, options4, answers, std_ans;
    int current_que = 0;
    int score = 0;
    int exam_id;
    JLabel que_lbl;
    JRadioButton option1, option2, option3, option4;
    ButtonGroup optionsGroup;
    JButton nxt_btn, submit_btn;
    Timer timer;
    int timeLeft = 30 * 60;

    public ExamPage(int exam_id) {
        this.exam_id = exam_id;

        questions = new String[100];
        options1 = new String[100];
        options2 = new String[100];
        options3 = new String[100];
        options4 = new String[100];
        answers = new String[100];
        std_ans = new String[100];

        setTitle("Online Exam");
        setSize(800, 600);
        setLayout(null);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);

        que_lbl = new JLabel();
        que_lbl.setFont(new Font("Arial", Font.BOLD, 18));
        que_lbl.setHorizontalAlignment(SwingConstants.CENTER);
        que_lbl.setBounds(50, 70, 700, 40);
        add(que_lbl);

        option1 = new JRadioButton();
        option1.setFont(new Font("Arial", Font.PLAIN, 16));
        option1.setBounds(100, 130, 600, 30);
        option2 = new JRadioButton();
        option2.setFont(new Font("Arial", Font.PLAIN, 16));
        option2.setBounds(100, 180, 600, 30);
        option3 = new JRadioButton();
        option3.setFont(new Font("Arial", Font.PLAIN, 16));
        option3.setBounds(100, 230, 600, 30);
        option4 = new JRadioButton();
        option4.setFont(new Font("Arial", Font.PLAIN, 16));
        option4.setBounds(100, 280, 600, 30);

        optionsGroup = new ButtonGroup();
        optionsGroup.add(option1);
        optionsGroup.add(option2);
        optionsGroup.add(option3);
        optionsGroup.add(option4);

        add(option1);
        add(option2);
        add(option3);
        add(option4);

        nxt_btn = new JButton("Next");
        nxt_btn.setFont(new Font("Arial", Font.BOLD, 18));
        nxt_btn.setBounds(200, 350, 150, 40);
        nxt_btn.addActionListener(this);
        add(nxt_btn);

        submit_btn = new JButton("Submit");
        submit_btn.setFont(new Font("Arial", Font.BOLD, 18));
        submit_btn.setBounds(450, 350, 150, 40);
        submit_btn.setEnabled(false);
        submit_btn.addActionListener(this);
        add(submit_btn);

        loadQuestions();
        startTimer();
        displayQuestion();

        setVisible(true);
    }

    public void actionPerformed(ActionEvent ae) {
        if (ae.getSource() == nxt_btn) {
            nextQuestion();
        }
        if (ae.getSource() == submit_btn) {
            submitExam();
        }
    }

    void loadQuestions() {
        try {
            Connection con = DatabaseConnection.getConnection();
            String q1 = "SELECT question, option1, option2, option3, option4, answer FROM questions WHERE exam_id = ?";
            PreparedStatement st = con.prepareStatement(q1);
            st.setInt(1, exam_id);
            ResultSet rs = st.executeQuery();

            int index = 0;
            while (rs.next()) {
                questions[index] = rs.getString("question");
                options1[index] = rs.getString("option1");
                options2[index] = rs.getString("option2");
                options3[index] = rs.getString("option3");
                options4[index] = rs.getString("option4");
                answers[index] = rs.getString("answer");
                index++;
            }
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    void startTimer() {
        timer = new Timer(1000, this::onTimerTick);
        timer.start();
    }

    void onTimerTick(ActionEvent e) {
        timeLeft--;

        if (timeLeft <= 0) {
            submitExam();
        }
    }

    void displayQuestion() {
        if (current_que < questions.length && questions[current_que] != null) {
            que_lbl.setText(questions[current_que]);
            option1.setText(options1[current_que]);
            option2.setText(options2[current_que]);
            option3.setText(options3[current_que]);
            option4.setText(options4[current_que]);
        } else {
            nxt_btn.setEnabled(false);
            submit_btn.setEnabled(true);
        }
    }

    void nextQuestion() {
        String selectedAnswer = "";
        if (optionsGroup.getSelection() != null) {
            selectedAnswer = optionsGroup.getSelection().getActionCommand();
        }

        std_ans[current_que] = selectedAnswer;
        if (selectedAnswer.equals(answers[current_que])) {
            score++;
        }
        current_que++;
        optionsGroup.clearSelection();
        displayQuestion();
    }

    void submitExam() {
        timer.stop();
        JOptionPane.showMessageDialog(this, "Exam completed! Your score: " + score);
        saveScore();
        dispose();
    }

    void saveScore() {
        try {
            Connection con = DatabaseConnection.getConnection();
            String q1 = "INSERT INTO scores (user_id, exam_id, score) VALUES (?, ?, ?)";
            PreparedStatement st = con.prepareStatement(q1);
            st.setInt(1, getUserId());
            st.setInt(2, exam_id);
            st.setInt(3, score);
            st.executeUpdate();
        } catch (SQLException ex) {
            JOptionPane.showMessageDialog(null, "Error: " + ex.getMessage());
        }
    }

    int getUserId() {
        return 1;
    }
}
