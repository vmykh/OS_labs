package dev.vmykh.labs.taskmanager.model;

import java.io.FileNotFoundException;
import java.util.List;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class OS {
//    private
    private static String PROC_FOLDER = "/proc";
    private List<ProcessInfo> currentProcsInfo;

    public OS()
    {

    }

    public ProcessInfo getProcessInfo(int pid)
    {
        ProcParser parser = new ProcParser();
        ProcessInfo pi = new ProcessInfo();

        long totalCpuTimeStart = 0;
        long totalCpuTimeFinish = 0;
        ProcessParsedInfo ppiStart = null;
        ProcessParsedInfo ppiFinish = null;

        try {
            ppiStart = parser.getParsedProcessInfo(pid);
            pi.setPid("" + ppiStart.getPid());
            pi.setName(ppiStart.getName());
            pi.setState(ppiStart.getState());
            pi.setUsername(ppiStart.getUsername());
            pi.setMemory("" + ppiStart.getMemory());
            totalCpuTimeStart = parser.getCpuTotalTime();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        try {
            ppiFinish = parser.getParsedProcessInfo(pid);
            totalCpuTimeFinish = parser.getCpuTotalTime();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

        double processCpuPercentage = 100.0 * ((ppiFinish.getCtime() + ppiFinish.getUtime())
                                    - (ppiStart.getCtime() + ppiStart.getUtime()))
                                / (double)(totalCpuTimeFinish - totalCpuTimeStart);

        pi.setCpu(String.format("%.2f", processCpuPercentage));

        return pi;
    }

    private int[] getAllPids
    {
        return null;
    }

    public List<ProcessInfo> getProcessesInfo()
    {
        return null;
    }

    public static void main(String[] args) {
        OS os = new OS();
        ProcessInfo pi = os.getProcessInfo(3160);
        System.out.println(pi);
    }
}
