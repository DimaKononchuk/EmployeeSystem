package Server;

import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;
import java.sql.Time;
import java.util.Date;

public class Table implements Serializable {
    @Setter
    @Getter
    private Date date;
    @Setter
    @Getter
    private Time start;
    @Setter
    @Getter
    private Time stop;
    @Setter
    @Getter
    private Time time;
    public Table(){};
    public Table(Date date,Time start,Time stop,Time time){
        this.date=date;
        this.start=start;
        this.stop=stop;
        this.time=time;
    }
}
