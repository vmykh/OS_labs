package dev.vmykh.labs.taskmanager.model;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class OS {
//    private
    private List<ProcessInfo> currentProcsInfo;

    public OS()
    {

    }

    public ProcessInfo getProcessInfo(int pid)
    {
        ProcParser parser = new ProcParser();
        ProcessInfo pi = new ProcessInfo();



        try {
            ProcessParsedInfo ppi = parser.getParsedProcessInfo(pid);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }

    public List<ProcessInfo> getProcessesInfo()
    {
        return null;
    }
}
