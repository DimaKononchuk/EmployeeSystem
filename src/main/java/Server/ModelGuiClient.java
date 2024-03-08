package Server;

import java.util.HashSet;
import java.util.Set;

public class ModelGuiClient {
    private Set<String> users = new HashSet<>();


    protected void addUser(String nameUser) {
        users.add(nameUser);
    }

    protected void removeUser(String nameUser) {
        users.remove(nameUser);
    }

    protected void setUsers(Set<String> users) {
        this.users = users;
    }
    protected boolean equalsUsers(String id){
        return users.contains(id);
    }
}
