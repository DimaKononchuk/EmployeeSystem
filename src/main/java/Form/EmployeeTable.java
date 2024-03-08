package Form;
import Client.*;

import Connect.Message;
import Server.*;


import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;

public class EmployeeTable extends  JFrame{
    private JPanel panel1;
    private JButton searchButton;
    private JTextField textField1;
    private JScrollPane scrollPane;
    private JLabel dateField;
    private JLabel startField;
    private JLabel stopField;
    private JLabel timeField;
    private JTable table1;
    private final Client client;

    public EmployeeTable(JFrame parent,Client client){
        super();
        this.client=client;

        setContentPane(panel1);
        setTitle("Інформація про час");
        setMinimumSize(new Dimension(550,300));
        setLocationRelativeTo(parent);
        //setIconImage(Toolkit.getDefaultToolkit().getImage(getClass().getResource("../employeeIcon.png")));

    }

    public void table(Message msg)  {
        DefaultTableModel model = new DefaultTableModel(new Object[]{"Дата", "Початок","Кінець","Робочий час"}, 0);
        List<Table> timeList= (List<Table>) msg.getTextMessage();
        for (Table people:timeList){
            model.addRow(new Object[]{people.getDate(),people.getStart(),people.getStop(),people.getTime()});

        }
        JTable table=new JTable(model);
        int selectedRowIndex = table.getSelectedRow();
        scrollPane.setViewportView(table);
        table.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {

                int selectedRowIndex = table.getSelectedRow();
                dateField.setText(model.getValueAt(selectedRowIndex, 0).toString());
                startField.setText(model.getValueAt(selectedRowIndex, 1).toString());
                stopField.setText(model.getValueAt(selectedRowIndex, 2).toString());
                timeField.setText(model.getValueAt(selectedRowIndex, 3).toString());
            }
        });
        searchButton.addActionListener(e->{
            TableRowSorter<DefaultTableModel> sorter = new TableRowSorter<>(model);
            table.setRowSorter(sorter);

            String searchText = textField1.getText();
            if (searchText.trim().length() == 0) {
                sorter.setRowFilter(null); // Видалити фільтр, якщо поле пошуку пусте
                JOptionPane.showMessageDialog(this,"Пошук не дав результатів","Некоректні дані",JOptionPane.INFORMATION_MESSAGE);

            } else {
                sorter.setRowFilter(RowFilter.regexFilter(searchText)); // Встановити фільтр за регулярним виразом
            }
        });





    }


    private void createUIComponents() {
        // TODO: place custom component creation code here
    }
}
