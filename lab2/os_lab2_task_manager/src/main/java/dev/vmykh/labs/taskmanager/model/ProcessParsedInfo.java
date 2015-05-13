package dev.vmykh.labs.taskmanager.model;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class ProcessParsedInfo {

    private int pid;
    private String name;
    private String username;
    private String state;
    private long utime;
    private long ctime;
    private long memory;  //in KB

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public long getUtime() {
        return utime;
    }

    public void setUtime(long utime) {
        this.utime = utime;
    }

    public long getCtime() {
        return ctime;
    }

    public void setCtime(long ctime) {
        this.ctime = ctime;
    }

    public long getMemory() {
        return memory;
    }

    public void setMemory(long memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "ProcessParsedInfo{" +
                "pid=" + pid +
                ", name='" + name + '\'' +
                ", username='" + username + '\'' +
                ", state='" + state + '\'' +
                ", utime=" + utime +
                ", ctime=" + ctime +
                ", memory=" + memory +
                '}';
    }
}
