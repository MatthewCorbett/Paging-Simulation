// A3.java
// Author: Matthew Corbett
// SN: C3308222
// Course: COMP2240
// Desc: This program simulates clock policy page replacement in FALR and VAGR Scope.
//       FALR: Fixed allocation - local replacement.
//       VAGR: Variable allocation - global replacement.
//       It will take multiple command line arguments in the order of:
//       Frames(Integer), Quantum(Integer), file names containing process info.

import java.io.*;
import java.util.*;
import java.util.List;
import java.util.Scanner;

public class A3{
    public static void main(String[] args) throws FileNotFoundException {

        int frames = 0; //total frames assigned to a replacement policy
        int quantum = 0; //time quantum for round-robin algorithm
        List<File> fileList = new ArrayList<>(); //list of process files

        //grab command line arguments
        for(int i=0; i < args.length; i++) {

            if(i==0){frames = Integer.parseInt(args[0]);}
            if(i==1){quantum = Integer.parseInt(args[1]);}
            if(i>1) {
                File f = new File(args[i]);
                fileList.add(f);
            }
        }

        //read files and form process Q
        Queue<Process> processQ = readFiles(fileList);

        //Create FALR clock policy and run
        FALR FALR = new FALR(frames, quantum, processQ);
        FALR.run();

        System.out.println("\n----------------------------------------------\n");

        //reset the processes
        processQ = readFiles(fileList);

        //create VAGR clock policy and run
        VAGR VAGR = new VAGR(frames, quantum, processQ);
        VAGR.run();
    }

    // @param: Takes a list of files that each contain a processes' information.
    // This method will turn each file into a process class, and then return
    // a queue of processes.
    private static Queue<Process> readFiles(List<File> fileList) throws FileNotFoundException {
        Queue<Process> tempQ = new LinkedList<>(); //to hold the processes

        for(int i = 0; i < fileList.size(); i++){
            Scanner scan = new Scanner(fileList.get(i)); //scans across each line
            scan.useDelimiter("[^0-9]+"); //delimit based on all char not between brackets
            int processNumber = scan.nextInt(); //first int we encounter will be processNumber
            List<Integer> pageList = new ArrayList<>(); //list of pages(int) from files
            while (scan.hasNextInt()){pageList.add(scan.nextInt());} //add every page
            Process process = new Process(processNumber, pageList); //create the process
            tempQ.add(process); //add to processList
        }
        return tempQ;
    }
}