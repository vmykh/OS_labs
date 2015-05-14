package dev.vmykh.labs.taskmanager.model;

import java.io.*;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.NoSuchElementException;
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
        String statFileName = String.format(PROCESS_STAT_FILENAME_TEMPLATE, pid);
        String statusFileName = String.format(PROCESS_STATUS_FILENAME_TEMPLATE, pid);
//        System.out.println(statFileName + " ~~~ " + statusFileName);

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

//            System.out.println(Arrays.asList(proc_info));

            ppi.setPid(pid);
            String rawName = proc_info[keysPositions.get("name")];
            ppi.setName(rawName.substring(1, rawName.length() - 1));
            ppi.setState(proc_info[keysPositions.get("status")]);
            ppi.setUtime(Long.parseLong(proc_info[keysPositions.get("utime")]));
            ppi.setCtime(Long.parseLong(proc_info[keysPositions.get("ctime")]));


        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
//                throw new RuntimeException(ex);
            }
        }

        br = null;
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

            //
//            int memStartIndex = 0;
            long mem = -1;
            Pattern pattern = Pattern.compile("VmRSS:\\s+(\\d+)\\s+kB");
//            System.out.println("Match: " + currentLine.matches(pattern));
            if (currentLine != null) {
//                long mem = Long.parseLong(currentLine.substring(memStartIndex));
                Matcher matcher = pattern.matcher(currentLine);
                if (matcher.find()){
                    mem =  Long.parseLong(matcher.group(1));
//                    ppi.setMemory(mem);
                }
            } else {
                throw new FileNotFoundException("Cannot read memory usage line of process #" + pid);
            }
            if (mem != -1) {
                ppi.setMemory(mem);
            } else {
                throw new RuntimeException("Cannot parse memory usage of process #" + pid);
            }

//            System.out.println("Memory: " + mem);
//
//            System.out.println("Memory: " + ppi.getMemory());

        } catch (FileNotFoundException e) {
//            e.printStackTrace();
//            throw new RuntimeException(e);
            throw e;
        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
//                throw new RuntimeException(ex);
            }
        }

        try {
            String script = String.format(GET_PROC_USERNAME_SCRIPT, pid);
//            System.out.println("Script: " + script);
            int cmdParametersAmount = 3;
            String[] cmd = new String[cmdParametersAmount];
            cmd[0] = SH_INTERPRETER;
            cmd[1] = SH_FLAG;
            cmd[2] = script;
            Process usernameScript = Runtime.getRuntime().exec(cmd);
            BufferedReader in = new BufferedReader(
                    new InputStreamReader(usernameScript.getInputStream()) );
            String username = in.readLine();
//            System.out.println("Username: " + username);
            ppi.setUsername(username);
        } catch (IOException e) {
            throw new RuntimeException("Error while executing script to get username of process #" + pid, e);
        }

//        System.out.println(ppi);
        return ppi;
    }

    public long getCpuTotalTime()
    {
        BufferedReader br = null;

        long total_time = 0;
        try {

            String currentLine;

            br = new BufferedReader(new FileReader(PROCESSOR_STAT_FILENAME));

//            while ((sCurrentLine = br.readLine()) != null) {
//                System.out.println(sCurrentLine);
//            }
            currentLine = br.readLine();
            if (!currentLine.startsWith("cpu ")) {
                throw new RuntimeException("Error. Strange content in file " + PROCESSOR_STAT_FILENAME);
            }

            String[] proc_times = currentLine.split("\\s");
//            System.out.println(Arrays.asList(proc_times));
            for (int i = 2; i < proc_times.length; ++i) {
                total_time += Long.parseLong(proc_times[i]);
            }


        } catch (IOException e) {
            e.printStackTrace();
//            throw new RuntimeException(e);
        } finally {
            try {
                if (br != null)br.close();
            } catch (IOException ex) {
                ex.printStackTrace();
//                throw new RuntimeException(ex);
            }
        }

        return total_time > 1 ? total_time : -1;   //to avoid division by zero
    }

    public static void main(String[] args) throws FileNotFoundException {
        ProcParser parser = new ProcParser();
//        long total = parser.getCpuTotalTime();
//        System.out.println("total time: " + total);
        parser.getParsedProcessInfo(3160);
    }

}
