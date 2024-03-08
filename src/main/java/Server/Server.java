package Server;

import Client.*;
import Connect.*;
import Connect.MesageType;
import Connect.Message;

import java.io.Closeable;
import java.io.EOFException;
import java.io.IOException;
import java.net.*;
import java.sql.*;
import java.sql.Connection;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;

public class Server {

    private static ServerSocket serverSocket;
    private static Thread thread;
    private static GUIServer gui;


    private String URL="jdbc:mysql://127.0.0.1:3306/systememployee";
    private String USER="root";
    private String PASSWORD="admin";
    private java.sql.Connection DBconnection;
    private LocalTime start;
    private ModelGuiClient modelGuiClient=new ModelGuiClient();
    private LocalTime stop;
    private LocalDate date;
    private Statement statement;
    private PreparedStatement preparedStatement;
    private static volatile boolean isServerStart = false;
    private java.sql.Connection connection;


    public static void main(String[] args) {
        Server server = new Server();
        gui = new GUIServer(server);
        gui.initFrameServer();

        while(true){
            if (isServerStart) {
                server.acceptServer();
                isServerStart = false;
            }
        }

    }
    protected void startServer() {
        try {

            serverSocket = new ServerSocket(5445);
            serverSocket.setSoTimeout(0);
            isServerStart = true;
            System.out.print("Сервер запущений.\n");
            System.out.print("IP Сервер: "+ InetAddress.getLocalHost().getHostAddress()+"\n");
            System.out.print("Port: "+ serverSocket.getLocalPort()+"\n");
            gui.refreshDialogWindowServer("Сервер запущений.\n");
            gui.refreshDialogWindowServer("IP Сервер: "+ InetAddress.getLocalHost().getHostAddress()+"\n");
            gui.refreshDialogWindowServer("Port: "+ serverSocket.getLocalPort()+"\n");
            DBconnection= DriverManager.getConnection(URL,USER,PASSWORD);
            gui.refreshDialogWindowServer("Зв'язок з базою даних встановлено!\n");
            System.out.print("Зв'язок з базою даних встановлено!\n");
        }catch (SQLException e){
            gui.refreshDialogWindowServer("Проблема з'єднання з базою даних\n");
        } catch(Exception e) {
            gui.refreshDialogWindowServer("Не вдалося запустити сервер\n");
        }
    }

    protected void stopServer() {
        try {

            if (serverSocket != null && !serverSocket.isClosed()) {
                serverSocket.close();
                gui.refreshDialogWindowServer("Сервер зупинено.\n");
            } else gui.refreshDialogWindowServer("Сервер не запущено- зупиняти немає що!\n");
        } catch (Exception e) {
            gui.refreshDialogWindowServer("Зупинити сервер не вдалося\n");
        }
    }
    protected void acceptServer() {
        while (!serverSocket.isClosed()){
            try {
                Socket socket = serverSocket.accept();

                thread=new Thread(new ClientServerConnection(socket));
                thread.start();
            } catch (Exception e) {
                gui.refreshDialogWindowServer("Зв'язок із сервером втрачено!\n");
                gui.refreshDialogWindowServer("accept server\n");
                break;
            }
        }


    }

    public class ClientServerConnection implements Runnable {
        private Socket client;
        private Connect.Connection connection;
        private Employee employee=new Employee();
        ClientServerConnection(Socket socket) throws IOException, ClassNotFoundException {
            this.client=socket;
        }
        @Override
        public void run() {
            try {
                    connection=new Connect.Connection(client);
                    this.receiveMessage();

            }catch (IOException | ClassNotFoundException e){
                gui.refreshDialogWindowServer("Клієнт достроково завершив роботу\n");

                e.printStackTrace();
            }




        }
        public void receiveMessage() throws IOException, ClassNotFoundException {
            try{

                while(!client.isClosed()){
                    Message message=connection.receive();

                    if(message.getTypeMessage()== MesageType.LOGIN_ID_EMPLOYEE){
                        connection.send(LoginEmployee(String.valueOf(message.getTextMessage())));
                    }else if(message.getTypeMessage()==MesageType.TEXT){
                        gui.refreshDialogWindowServer(message.getTextMessage()+"\n");
                    }else if(message.getTypeMessage()==MesageType.TIME_START){
                        start= (LocalTime) message.getTextMessage();
                        preparedStatement=DBconnection.prepareStatement("insert into employeetimer(idEmployee,date,start) values (?,?,?);");
                        preparedStatement.setString(1,employee.getId());
                        date=LocalDate.now();
                        preparedStatement.setDate(2, Date.valueOf(date));
                        preparedStatement.setTime(3, Time.valueOf(start));
                        preparedStatement.executeUpdate();
                        gui.refreshDialogWindowServer("Дані занесено в базу даних\n");
                    }else if(message.getTypeMessage()==MesageType.TIME_STOP){
                        preparedStatement=DBconnection.prepareStatement("update employeetimer set stop=?,timeWork=? where idEmployee=? and start=? and date=?");
                        stop=LocalTime.now();
                        preparedStatement.setTime(1,Time.valueOf(stop));
                        preparedStatement.setTime(2,Time.valueOf((LocalTime) message.getTextMessage()));
                        preparedStatement.setString(3,employee.getId());
                        preparedStatement.setTime(4, Time.valueOf(start));
                        preparedStatement.setDate(5,Date.valueOf(LocalDate.now()));
                        preparedStatement.executeUpdate();
                        gui.refreshDialogWindowServer(String.format("Користувач %s вийшов із системи!\n",employee.getName()));
                        connection.close();
                        client.close();
                    }else if (message.getTypeMessage() == MesageType.DISABLE_USER) {
                        Message msg=checkDisableEmployee((String) message.getTextMessage());
                        MesageType type=msg.getTypeMessage();
                        if(type==MesageType.OK){
                            connection.send(msg);
                            modelGuiClient.removeUser(employee.getId());

                        }else{
                            connection.send(msg);
                        }

                    }else if(message.getTypeMessage()==MesageType.TABLELIST){
                        try{
                            System.out.println("tsbe server");
                            statement=DBconnection.createStatement();
                            ResultSet resultSet=statement.executeQuery("SELECT idEmployee,date,Min(start) as start ,Max(stop) as stop,SEC_TO_TIME(SUM(TIME_TO_SEC(timeWork))) as time FROM employeetimer group by idEmployee,date having idEmployee="+employee.getId()+";");
                            List<Table> tableHistory=new ArrayList<>();
                            while(resultSet.next()){
                                Table table=new Table();
                                table.setDate(resultSet.getDate("date"));
                                table.setStart(resultSet.getTime("start"));
                                table.setStop(resultSet.getTime("stop"));
                                table.setTime(resultSet.getTime("time"));
                                tableHistory.add(table);
                            }

                            connection.send(new Message(MesageType.TABLELIST,tableHistory));
                        }catch(SQLException e){
                            e.printStackTrace();
                        }

                    }else if(message.getTypeMessage()==MesageType.CHECK){
                        System.out.println("OK!");
                        connection.send(new Message(MesageType.CHECK));
                    }


                }
            }catch (EOFException e){
                connection.close();
                connection=new Connect.Connection(client);
                receiveMessage();
            }catch (SQLException e){
                gui.refreshDialogWindowServer("Проблема з базой даних!\n");
                e.printStackTrace();
            }
        }

        public Message LoginEmployee(String str) throws SQLException {
            try{
                statement=DBconnection.createStatement();
                ResultSet resultSet=statement.executeQuery("select * from  employee as e inner join  idperson as p where e.id=p.id;");

                while(resultSet.next()){

                    if(resultSet.getString("p.idEmployee").equals(str)){
                        if(!modelGuiClient.equalsUsers(str)){
                            employee.setName(resultSet.getString("e.name")+" "+resultSet.getString("e.surname"));
                            employee.setEmail(String.valueOf(resultSet.getString("email")));
                            employee.setId(String.valueOf(resultSet.getInt("p.idEmployee")));
                            gui.refreshDialogWindowServer("Працівник "+employee.getName()+" успішно авторизувався!\n");
                            System.out.print("Працівник "+employee.getName()+" успішно авторизувався!\n");
                            modelGuiClient.addUser(str);
                            return new Message(MesageType.OK,new String[]{employee.getName(),employee.getEmail()});

                        }else{

                            return new Message(MesageType.IDFAILED);

                        }

                    }else{

                    }
                }
            }catch (SQLException e){

                gui.refreshDialogWindowServer("Проблема з базой даних!\n");
            }
            return new Message(MesageType.FAILED);
        }
        public Message checkDisableEmployee(String str){
                if(str.equals(employee.getId())){
                    gui.refreshDialogWindowServer("Працівник "+employee.getName()+" завершив роботу!\n");

                    return new Message(MesageType.OK,new String[]{employee.getName(),employee.getEmail()});

                }
                return new Message(MesageType.FAILED);
        }

    }


}
