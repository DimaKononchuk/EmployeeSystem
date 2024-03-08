package Form;

import Client.Client;

import Client.*;
import Server.*;
import javax.swing.*;
import java.awt.*;
import java.io.IOException;

public class EmployeePanel extends JFrame {
    private JPanel employeePanel;
    private JButton TimeBtn;
    private JButton stopBtn;
    private JLabel nameField;
    private JLabel NameLabel;
    private JLabel emailLabel;

    private JLabel emailField;
    private JLabel dateStart;
    private JLabel timerLabel;
    private final Client client;
    private Login login;
    private Identification identification;
    public EmployeePanel(JFrame parent,Client client) throws IOException, ClassNotFoundException, InterruptedException {
        super();

        this.client=client;
        setContentPane(employeePanel);
        setDefaultCloseOperation(JDialog.DO_NOTHING_ON_CLOSE);
        setTitle("Роботу розпочато");
        setMinimumSize(new Dimension(550,300));
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../employeeIcon.png")));
        //setModal(false);
        setLocationRelativeTo(parent);
        stopBtn.addActionListener(e->{
            client.disableClient();
            //System.exit(0);
        });
        TimeBtn.addActionListener(e->{
            System.out.println("Timebtn click");

            try {
                client.selectWorkTime();
            }catch (ClassNotFoundException ex) {
                ex.printStackTrace();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        });




    }

    public void setNameField(String nameField) {
        this.nameField.setText(nameField);
    }

    public void setDateStart(String dateStart) {
        this.dateStart.setText(dateStart);
    }

    public void setEmailField(String emailField) {
        this.emailField.setText(emailField);
    }

    private void createUIComponents() {
        // TODO: place custom component creation code here
    }

    public  void setTimerLabel(String timerLabel) {
        this.timerLabel.setText(timerLabel);
    }


}
