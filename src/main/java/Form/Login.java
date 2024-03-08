package Form;

import Client.*;

import Server.*;


import javax.swing.*;
import java.awt.*;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;

public class Login extends JFrame {
    private JPanel loginPanel;
    private JTextField emailField;
    private JButton loginButton;
    private JLabel emailLabel;
    private JLabel textLabel;
    private JLabel text1Label;
    private JLabel loginLabel;
    private JLabel IconLabel;
    private final Client client;



    public Login(JFrame parent,Client client){
        super();
        this.client=client;
        setContentPane(loginPanel);
        //setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle("Вхід");
        setMinimumSize(new Dimension(450,300));
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../employeeIcon.png")));
        //setModal(true);
        setLocationRelativeTo(parent);
        loginButton.addActionListener(e->{
            client.loginIdEmployee();
        });

        this.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                System.exit(0);
            }
        });

    }

    public String getEmailField() {
        return emailField.getText();
    }

    public void failedInformation(){
        JOptionPane.showMessageDialog(this,"Неправильно введено Id","Помилка",JOptionPane.ERROR_MESSAGE);

    }

    public void showDialogMessage() {
        JOptionPane.showMessageDialog(this,"Невдалося з'єднатися із сервером","Помилка",JOptionPane.INFORMATION_MESSAGE);

    }

    public void showDialogId() {
        JOptionPane.showMessageDialog(this,"Дане Id зареєстроване в системі","Помилка",JOptionPane.ERROR_MESSAGE);

    }
}
