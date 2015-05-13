package dev.vmykh.labs.taskmanager.model;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class ProcessInfo {
    private String pid;
    private String name;
    private String state;
    private String username;
    private String cpu;
    private String memory;

    public String getPid() {
        return pid;
    }

    public void setPid(String pid) {
        this.pid = pid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getState() {
        return state;
    }

    public void setState(String state) {
        this.state = state;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getCpu() {
        return cpu;
    }

    public void setCpu(String cpu) {
        this.cpu = cpu;
    }

    public String getMemory() {
        return memory;
    }

    public void setMemory(String memory) {
        this.memory = memory;
    }

    @Override
    public String toString() {
        return "ProcessInfo{" +
                "pid='" + pid + '\'' +
                ", name='" + name + '\'' +
                ", state='" + state + '\'' +
                ", username='" + username + '\'' +
                ", cpu='" + cpu + '\'' +
                ", memory='" + memory + '\'' +
                '}';
    }
}
