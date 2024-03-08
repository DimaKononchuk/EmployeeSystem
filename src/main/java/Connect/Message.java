package Connect;

import Connect.MesageType;

import java.io.Serializable;


public class Message implements Serializable  {
    private MesageType typemessage;
    private Object textMessage;

    public Message(MesageType typeMessage, Object textMessage) {
        this.textMessage = textMessage;
        typemessage=typeMessage;

    }


    public Message(MesageType typeMessage) {
        typemessage = typeMessage;
        this.textMessage = null;

    }

    public MesageType getTypeMessage() {
        return  typemessage;
    }



    public Object getTextMessage() {
        return textMessage;
    }


}
