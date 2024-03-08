package Client;

import lombok.Getter;
import lombok.Setter;

import java.beans.ConstructorProperties;

public class Employee {
    @Getter
    @Setter
    private String name;
    @Getter
    @Setter
    private String Surname;
    @Getter
    @Setter
    private String email;
    @Getter
    @Setter
    private String id;
    @Getter
    @Setter
    private int age;
    @Getter
    @Setter
    private String City;
    @Getter
    @Setter
    private String Address;

    public Employee(){

    }
}
