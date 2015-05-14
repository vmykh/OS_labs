package dev.vmykh.labs.taskmanager.model;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FilenameFilter;
import java.util.*;

/**
 * Created by mrgibbs on 07.05.15.
 */
public class OS {

    private static final String PROC_FOLDER = "/proc";
    private static final String PROC_CPU_USAGE_PERCENTAGE_TEMPLATE = "%.1f";
    private static final int SLEEP_MILLIS_FOR_MEASURING_CPU_TIME = 1000;
    private static final int UPDATER_PAUSE_MILLIS = 0;

    private static volatile OS instance;


    private List<ProcessInfo> currentProcsInfo;
    private ProcParser parser;

    private OS() {
        parser = new ProcParser();
        startProcessesInfoUpdater();
    }

    public static OS getInstance() {
        synchronized (OS.class) {
            if (instance == null) {
                instance = new OS();
            }
        }
        return instance;
    }

    private void startProcessesInfoUpdater() {
        this.new ProcessesInfoUpdater().start();
    }


    private List<Integer> fetchCurrentPids() {
        File procDir = new File(PROC_FOLDER);
        String processFolderPattern = "\\d+";
        String[] directories = procDir.list(new FilenameFilter() {
            @Override
            public boolean accept(File dir, String name) {
                return new File(dir, name).isDirectory() && name.matches(processFolderPattern);
            }
        });
        List<Integer> pids = new ArrayList<>(directories.length);
        for (String dir : directories){
            pids.add(Integer.parseInt(dir));
        }
        return pids;
    }

    public synchronized List<ProcessInfo> getCurrentProcsInfo() {
        return currentProcsInfo;
    }

    public synchronized void setCurrentProcsInfo(List<ProcessInfo> currentProcsInfo) {
        this.currentProcsInfo = currentProcsInfo;
    }

    private void updateProcessesInfo(){
        List<Integer> pids = fetchCurrentPids();

        long totalCpuTimeStart = parser.getCpuTotalTime();
        Map<Integer, ProcessParsedInfo> ppisStart = fetchAllProcessesParsedInfo(pids);

        try {
            Thread.sleep(SLEEP_MILLIS_FOR_MEASURING_CPU_TIME);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        long totalCpuTimeFinish = parser.getCpuTotalTime();
        Map<Integer, ProcessParsedInfo> ppisFinish = fetchAllProcessesParsedInfo(pids);

        List<ProcessInfo> updatedProcessesInfo = new ArrayList<>(ppisFinish.size());
        for (int pid : ppisFinish.keySet()) {
            if (ppisStart.containsKey(pid)) {
                ProcessParsedInfo ppiStart = ppisStart.get(pid);
                ProcessParsedInfo ppiFinish = ppisFinish.get(pid);
                ProcessInfo pi = new ProcessInfo();

                pi.setPid("" + ppiStart.getPid());
                pi.setName(ppiStart.getName());
                pi.setState(ppiStart.getState());
                pi.setUsername(ppiStart.getUsername());
                pi.setMemory("" + ppiStart.getMemory());

                long totalProcessCpuTimeStart = ppiStart.getUtime() + ppiStart.getCtime();
                long totalProcessCpuTimeFinish = ppiFinish.getUtime() + ppiFinish.getCtime();
                double processCpuUsage = 100.0 * (totalProcessCpuTimeFinish - totalProcessCpuTimeStart)
                                    / (double)(totalCpuTimeFinish - totalCpuTimeStart);

                pi.setCpu(String.format(PROC_CPU_USAGE_PERCENTAGE_TEMPLATE, processCpuUsage));
                updatedProcessesInfo.add(pi);

            }

        }

        setCurrentProcsInfo(updatedProcessesInfo);

    }

    private Map<Integer, ProcessParsedInfo> fetchAllProcessesParsedInfo(List<Integer> pids) {
        Map<Integer, ProcessParsedInfo> ppis = new HashMap<>(pids.size());
        for (int pid : pids) {
            try {
                ProcessParsedInfo ppi = parser.getParsedProcessInfo(pid);
                if (ppi != null) {
                    ppis.put(pid, ppi);
                }
            } catch (FileNotFoundException e) {
//                System.err.println("Process with pid=" + pid + " does not exist");
            }
        }
        return ppis;
    }

    private class ProcessesInfoUpdater extends Thread {
        @Override
        public void run() {
            int counter = 0;
            while(true) {
                updateProcessesInfo();
                try {
                    Thread.sleep(UPDATER_PAUSE_MILLIS);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Processes info updated " + counter);
                counter++;
            }
        }
    }
}
