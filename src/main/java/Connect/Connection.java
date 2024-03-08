package Connect;

import lombok.SneakyThrows;

import java.io.*;
import java.net.Socket;
import java.net.SocketException;

public class Connection implements Serializable {
    private final Socket socket;
    private  ObjectOutputStream out;
    private  ObjectInputStream in;

    public Connection(Socket socket) throws IOException {
        this.socket = socket;

        this.out = new ObjectOutputStream(socket.getOutputStream());
        this.in = new ObjectInputStream(socket.getInputStream());

    }


    @SneakyThrows
    public void send(Message message) {

            out.writeObject(message);
            out.flush();

    }

    @SneakyThrows
    public Message receive()  {
        Object message =in.readObject();

        return (Message) message;

    }

    public void close() throws IOException {
        in.close();
        out.close();
        //socket.close();
    }

    public void restar() throws IOException {
        out.reset();
        in.reset();
    }
}
