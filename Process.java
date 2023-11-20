// Process.java
// Author: Matthew Corbett
// SN: C3308222
// Course: COMP2240
// Desc: Process class for A3. Each process contains a list of integers
//       that represent pages/instructions to be executed.

import java.util.ArrayList;
import java.util.List;

public class Process {
    private String name; //the process name
    private int PID; //the process ID
    private List<Integer> pageList; //list of pages
    private int currentPageIndex = 0; //index of current page in pageList
    private int readyTime = 0; //time unit that process will be ready next
    private boolean lastPageComplete = true; //false: last page was not executed due to replacement
    private List<Integer> faultTimesList; //list of the time units when page faults occurred
    private int faultCounter = 0; //total amount of page faults
    private int turnaroundTime = 0; //simply finish time as all processes arrive at 0

    //index variables used for the 2D frame array in FALR clock policy
    private int frameIndex; //first dimension of frame array to define resident set
    private int clockIndex; //current clock index in second dimension (the allocated frames)

    //Constructor
    public Process(int processNumber, List<Integer> pageList){
        this.name = "Process" + processNumber;
        this.PID = processNumber;
        this.pageList = pageList;
        this.frameIndex = processNumber-1;
        faultTimesList = new ArrayList<>();
    }

    //getters
    public String getName(){return name;}
    public int getReadyTime(){return readyTime;}
    public int getClockIndex(){return clockIndex;}
    public int getFrameIndex(){return frameIndex;}
    public boolean isLastPageComplete(){return lastPageComplete;}
    public List<Integer> getFaultTimesList(){return faultTimesList;}
    public int getFaultCounter(){return faultCounter;}
    public int getTurnaroundTime(){return turnaroundTime;}
    public int getPID(){return PID;}

    //setters
    public void setReadyTime(int readyTime){this.readyTime = readyTime;}
    public void setFrameIndex(int frameIndex){this.frameIndex = frameIndex;}
    public void setLastPageComplete(boolean bool){this.lastPageComplete = bool;}
    public void setTurnaroundTime(int turnaroundTime){this.turnaroundTime = turnaroundTime;}
    public void addToFaultCounter(){faultCounter++;}

    //Moves the clock index 1 forward in FALR 2D frame array.
    //If end of array is reached, moves index back to start (0).
    public void moveClockIndex(int frameSize){
        if (clockIndex == frameSize-1) {
            clockIndex = 0;
        } else {
            clockIndex++;
        }
    }

    //Returns the next page of the process
    public int next(){
        int page = pageList.get(currentPageIndex);
        currentPageIndex++;
        return page;
    }

    //Returns true if the process has remaining pages left.
    public Boolean hasNext(){
        return currentPageIndex < pageList.size();
    }
}

