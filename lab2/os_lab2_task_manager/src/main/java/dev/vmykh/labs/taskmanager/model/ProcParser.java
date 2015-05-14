package dev.vmykh.labs.taskmanager.model;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by mrgibbs on 13.05.15.
 */
public class ProcParser {
    private static String PROCESSOR_STAT_FILENAME = "/proc/stat";
    private static String PROCESS_STAT_FILENAME_TEMPLATE = "/proc/%d/stat";
    private static String PROCESS_STATUS_FILENAME_TEMPLATE = "/proc/%d/status";

    private static String SH_INTERPRETER = "/bin/sh";
    private static String SH_FLAG = "-c";
    private static String GET_PROC_USERNAME_SCRIPT =
            "uid=$(awk '/^Uid:/{print $2}' /proc/%d/status); getent passwd \"$uid\" | awk -F: '{print $1}'";

    private Map<String, Integer> keysPositions;

    public ProcParser(){
        keysPositions = new HashMap<>();
        keysPositions.put("pid", 0);
        keysPositions.put("name", 1);
        keysPositions.put("status", 2);
        keysPositions.put("utime", 13);
        keysPositions.put("ctime", 14);
    }

    public ProcessParsedInfo getParsedProcessInfo(int pid) throws FileNotFoundException
    {
        ProcessParsedInfo ppi = parseBasicProcessInfo(pid);
        ppi.setMemory(fetchMemoryUsage(pid));
        ppi.setUsername(fetchUserName(pid));

        return ppi;
    }

    private ProcessParsedInfo parseBasicProcessInfo(int pid) throws FileNotFoundException{
        String statFileName = String.format(PROCESS_STAT_FILENAME_TEMPLATE, pid);
        ProcessParsedInfo ppi = new ProcessParsedInfo();
        BufferedReader br = null;

        try {

            String currentLine;

            br = new BufferedReader(new FileReader(statFileName));

            currentLine = br.readLine();
            if (!currentLine.startsWith("" + pid)) {
                throw new RuntimeException("Error. Strange content in file " + PROCESSOR_STAT_FILENAME);
            }

            String[] proc_info = currentLine.split("\\s");

            ppi.setPid(pid);
            String rawName = proc_info[keysPositions.get("name")];
            ppi.setName(rawName.substring(1, rawName.length() - 1));
            ppi.setState(proc_info[keysPositions.get("status")]);
            ppi.setUtime(Long.parseLong(proc_info[keysPositions.get("utime")]));
            ppi.setCtime(Long.parseLong(proc_info[keysPositions.get("ctime")]));


        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return ppi;
    }

    private long fetchMemoryUsage(int pid) throws FileNotFoundException {
        String statusFileName = String.format(PROCESS_STATUS_FILENAME_TEMPLATE, pid);

        long mem = 0;  //fetched memory usage

        BufferedReader br = null;
        try {
            String currentLine;

            br = new BufferedReader(new FileReader(statusFileName));

            currentLine = br.readLine();
            if (!currentLine.startsWith("Name:")) {
                throw new RuntimeException("Error. Strange content in file " + PROCESSOR_STAT_FILENAME);
            }

            while ((currentLine = br.readLine()) != null) {
                if (currentLine.startsWith("VmRSS:")) {
                    break;
                }
            }


            String memoryLineRegex = "VmRSS:\\s+(\\d+)\\s+kB";
            Pattern pattern = Pattern.compile(memoryLineRegex);

            if (currentLine != null) {
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find()){
                    mem =  Long.parseLong(matcher.group(1));
                }
            } else {
                throw new FileNotFoundException("Cannot read memory usage line of process #" + pid);
            }

        } catch (FileNotFoundException e) {
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return mem;
    }

    private String fetchUserName(int pid) {
        String username = "";
        try {
            String script = String.format(GET_PROC_USERNAME_SCRIPT, pid);
            int cmdParametersAmount = 3;
            String[] cmd = new String[cmdParametersAmount];
            cmd[0] = SH_INTERPRETER;
            cmd[1] = SH_FLAG;
            cmd[2] = script;
            Process usernameScript = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(usernameScript.getInputStream()) );
            username = in.readLine();

        } catch (IOException e) {
//            throw new RuntimeException("Error while executing script to get username of process #" + pid, e);
            e.printStackTrace();
        }

        return username;
    }

    public long getCpuTotalTime()
    {
        BufferedReader br = null;

        long total_time = 0;
        try {

            String line;
            br = new BufferedReader(new FileReader(PROCESSOR_STAT_FILENAME));

            line = br.readLine();
            if (!line.startsWith("cpu ")) {
                throw new RuntimeException("Error. Strange content in file " + PROCESSOR_STAT_FILENAME);
            }

            String[] proc_times = line.split("\\s");
            for (int i = 2; i < proc_times.length; ++i) {
                total_time += Long.parseLong(proc_times[i]);
            }

        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
            }
        }

        return total_time > 1 ? total_time : -1;   //to avoid division by zero
    }


}
