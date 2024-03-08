package Form;

import Client.*;

import Server.*;


import javax.swing.*;
import java.awt.*;

public class Identification extends JDialog{
    private JTextField idField;
    private JButton stopBtn;
    private JLabel idLabel;
    private JPanel Identification;
    private final Client client;
    public Identification(JFrame parent, Client client) {
        super(parent);
        this.client=client;
        setContentPane(Identification);
        setTitle("Завершити роботу");
        setMinimumSize(new Dimension(200,150));
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../employeeIcon.png")));
        //this.setVisible(true);
        setLocationRelativeTo(parent);
        stopBtn.addActionListener(e->{
            client.checkDisableEmployee();
            System.out.println("disable");
        });

    }
    public String getidField() {
        return idField.getText() ;
    }

    public void failedInformation(){
        JOptionPane.showMessageDialog(this,"Неправильно введено Id","Помилка",JOptionPane.ERROR_MESSAGE);

    }
    public void workSuccesfull(){
        JOptionPane.showMessageDialog(this,"Роботу успішно завершено","Завершення",JOptionPane.INFORMATION_MESSAGE);
    }
}
