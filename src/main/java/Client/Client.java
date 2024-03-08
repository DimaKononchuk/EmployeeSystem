package Client;

import Connect.Connection;
import Form.EmployeePanel;
import Form.EmployeeTable;
import Form.Identification;
import Form.Login;
import Connect.MesageType;
import Connect.Message;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.time.Duration;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;



public class Client {
    private static Connection connection;
    private static Login login;
    private static EmployeePanel employee;
    private static Identification identification;
    private static EmployeeTable employeeTable;
    private volatile boolean isConnect = false;
    private static int port=3333;
    private LocalTime time;
    private boolean timer=true;
    private static Socket socket;

    public boolean isConnect() {
        return isConnect;
    }

    public void setConnect(boolean connect) {
        isConnect = connect;
    }

    public static void main(String[] args) throws IOException, ClassNotFoundException {
        try{
            Client client=new Client();
            login=new Login(null,client);
            login.setVisible(true);
            socket = new Socket("centre-tours.gl.at.ply.gg",61952);
            connection = new Connection(socket);
            connection.send(new Message(MesageType.TEXT,"Вхід в систему: "+InetAddress.getLocalHost().getHostAddress()));
            client.setConnect(false);
            while (!client.isConnect()){


                connection.send(new Message(MesageType.CHECK," "));
                connection.receive();
                try {

                    Thread.sleep(30000);
                } catch (InterruptedException e) {

                    throw new RuntimeException(e);
                }
            }
        }catch(Exception e){
            login.showDialogMessage();
        }

    }

    public void loginIdEmployee(){
        try{
            connection.send(new Message(MesageType.LOGIN_ID_EMPLOYEE,login.getEmailField()));
            Message result=connection.receive();
            MesageType type=result.getTypeMessage();
            System.out.println(type);
            if(type==MesageType.OK){

                String [] strNames =(String[]) result.getTextMessage();
                System.out.println(strNames[0]+" : "+strNames[1]);
                employee=new EmployeePanel(null,this);

                employee.setNameField(strNames[0]);
                employee.setEmailField(strNames[1]);
                login.setVisible(false);

                employee.setVisible(true);
                LocalTime timestart=LocalTime.now();
                connection.send(new Message(MesageType.TIME_START,timestart));
                Thread timerThread = new Thread(() -> {
                    while (timer) {
                        employee.setDateStart(String.valueOf(LocalDateTime.of(LocalDate.now(),timestart).format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"))));

                        System.out.println(socket.isClosed());
                        Duration duration=Duration.between(timestart,LocalTime.now());
                        time=LocalTime.of(duration.toHoursPart(),duration.toMinutesPart(), duration.toSecondsPart());
                        employee.setTimerLabel(String.valueOf(time));
                        try {
                              Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            throw new RuntimeException(e);
                        }



                    }

                });
                timerThread.start();


            }else if(type==MesageType.IDFAILED){
                login.showDialogId();
            }else{
                login.failedInformation();
                connection.send(new Message(MesageType.TEXT,"Невдала спроба! "));

            }
        }catch (Exception e){

        }
    }
    public void disableClient() {
        try {
            identification=new Identification(null,this);
            identification.setVisible(true);


        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void timeStop()  {

        try {
            if (!isConnect) {
                connection.send(new Message(MesageType.TIME_STOP,time));
                isConnect = false;
                timer=false;
                connection.close();
                identification.workSuccesfull();
                System.exit(0);
            }
        } catch (Exception e) {

        }

    }
    public void checkDisableEmployee() {
        try{
            connection.send(new Message(MesageType.DISABLE_USER,identification.getidField()));
            Message msg=connection.receive();
            MesageType type=msg.getTypeMessage();

            if(type==MesageType.OK){
                timeStop();

            }else{
                identification.failedInformation();
            }
        }catch (Exception e){
            e.printStackTrace();
        }

    }

    public void selectWorkTime() throws IOException, ClassNotFoundException {
        employeeTable=new EmployeeTable(null,this);
        connection.send(new Message(MesageType.TABLELIST));
        Message msg=  connection.receive();
        employeeTable.setVisible(true);
        employeeTable.table(msg);



    }


}
