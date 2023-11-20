// FALR.java
// Author: Matthew Corbett
// SN: C3308222
// Course: COMP2240
// Desc: Simulates clock policy page replacement in a Fixed allocation - local scope.

import java.util.*;

public class FALR {
    private int time = 0; //current time algorithm has run
    private int totalFrames; //total frames available in 'main memory'
    private int quantum; //time quantum for round-robin algorithm
    private int allocatedFrames; //how many frames each process gets
    private  Frame[][] frameArray; //stores the allocated set of frames for each process

    private Queue<Process> blockedQ = new LinkedList<>(); //blocked processes
    private Queue<Process> readyQ; //processes ready to run
    private ArrayList<Process> finishQ = new ArrayList<Process>(); //all completed processes
    private Process currentJob; //holds current process being run
    private int currentPage; //the current page of the current process

    //constructor
    public FALR(int frames, int quantum, Queue<Process> processQ){
        this.totalFrames = frames;
        this.quantum = quantum;
        this.readyQ = processQ;
    }

    //runs Fixed Allocation Local Replacement Clock Policy in a Round-Robin algorithm.
    public void run() {

        // ---- create 2D array to represent allocated frames ----
        allocatedFrames = totalFrames/readyQ.size(); //determine frames per process
        //      each processes frameArray[#][] is based on PID-1 (taken from file)
        frameArray = new Frame[readyQ.size()][allocatedFrames];
        //instantiate each frame in array
        for (int i=0; i<readyQ.size(); i++){
            for (int j=0; j<allocatedFrames; j++){
                frameArray[i][j] = new Frame(); //dummy will have all -1 values
            }
        }

        // ---- loop until all jobs are complete ----
        while(!blockedQ.isEmpty() || !readyQ.isEmpty()) {

            moveBlockedJobs(); //fill readyQ if needed

            // ---- run next job ----
            currentJob = readyQ.remove(); //take next job
            for (int i = 1; i <= quantum; i++) {

                //first check if last instruction was run (i.e. not blocked due to swap)
                if(currentJob.isLastPageComplete() == false){
                    time++; //execute page
                    i++; //note: we can only hit this once at start of RR quantum
                    currentJob.setLastPageComplete(true);
                }

                //if another page in the process exists
                if (currentJob.hasNext()) {
                    currentPage = currentJob.next(); //grab next page
                    //check if page is already in a frame
                    boolean found = false; //tracks whether page was found in a frame
                    for (int j = 0; j < allocatedFrames; j++) {
                        if (frameArray[currentJob.getFrameIndex()][j].getPageNumber() == -1) break; //hit empty frame
                        else if (currentPage == frameArray[currentJob.getFrameIndex()][j].getPageNumber()) {
                            //page is found, reset use bit
                            found = true;
                            frameArray[currentJob.getFrameIndex()][j].setUseBit(1);
                            time++; //execute instruction/page 1 time unit.
                            break;
                        }
                    }
                    //page not found, call replacement policy
                    if (!found) {
                        currentJob.setLastPageComplete(false);
                        currentJob.getFaultTimesList().add(time);
                        replacePage();
                        break; //move to next process
                    }

                } else {
                    //no more pages left, job finished
                    finishQ.add(currentJob);
                    currentJob.setTurnaroundTime(time);
                    currentJob = null;
                    break;
                }
            }

            //---- time quantum expired or job finished ----
            if(currentJob!=null){ //if job finished
                //move over any blocked jobs that became ready while this was running
                if(!blockedQ.isEmpty() && blockedQ.peek().getReadyTime() <= time){
                    while(!blockedQ.isEmpty() && blockedQ.peek().getReadyTime() <= time){
                        readyQ.add(blockedQ.remove());
                    }
                }
                //check if job has any more pages after finishing its round
                if(!currentJob.hasNext()){ //no more pages, job finished
                    finishQ.add(currentJob);
                    currentJob.setTurnaroundTime(time);
                    currentJob = null;
                }
                //else job has more pages, so add to readyQ
                else {
                    readyQ.add(currentJob);
                    currentJob = null;
                }
            }
        }

        //output info of policy
        System.out.println("Clock - Fixed-Local Replacement:");
        System.out.println("PID\tProcess-Name\tTurnaround\t# Faults\tFault-Times");
        int z = 1; //to match process ID
        while(z!=finishQ.size()+1) {
            for (int i = 0; i < finishQ.size(); i++) {
                if (finishQ.get(i).getPID() == z) {
                    System.out.print(finishQ.get(i).getPID() + "\t");
                    System.out.print(finishQ.get(i).getName() + "\t");
                    System.out.print(finishQ.get(i).getTurnaroundTime() + "\t\t");
                    System.out.print(finishQ.get(i).getFaultCounter() + "\t\t");
                    System.out.println(finishQ.get(i).getFaultTimesList() + "\t");
                    z++;
                }
            }
        }
    } //end of run

    // This method will use clock policy to transfer a processes next page to a frame.
    // Due to local scope, it will only consider replacing pages/frames in its resident set
    // If there is an available frame: it will put the page here.
    // If all frames have a page: replace the next page with a 0 use-bit (clock policy).
    private void replacePage(){
        currentJob.addToFaultCounter(); //generate page fault
        blockedQ.add(currentJob); //block current process
        currentJob.setReadyTime(time+6); //update when the page swap will finish

        //clock policy to swap page:
        //  start at clockIndex and move through the frame array
        //  change any 1 use-bits to 0
        //  swap the page into the first frame you come across with a 0 use-bit
        for (int i=0; i <= allocatedFrames; i++){ //upper limit
            //if use-bit 0, swap this page
            if(frameArray[currentJob.getFrameIndex()][currentJob.getClockIndex()].getUseBit() == 0){
                frameArray[currentJob.getFrameIndex()][currentJob.getClockIndex()].setPageNumber(currentPage);
                frameArray[currentJob.getFrameIndex()][currentJob.getClockIndex()].setUseBit(1);
                currentJob.moveClockIndex(allocatedFrames); //move clock frame pointer
                break; //done
            }
            //else use-bit is 1, so set to 0.
            else {
                frameArray[currentJob.getFrameIndex()][currentJob.getClockIndex()].setUseBit(0);
                currentJob.moveClockIndex(allocatedFrames); //move clock frame pointer
            }
        }
        currentPage = -2;
        currentJob = null;
    }

    // This method will move any jobs that have finished their io page swapping to the readyQ.
    // If readyQ remains empty, it will move time to the next blocked job is ready.
    private void moveBlockedJobs(){
        //move over any blocked jobs that are ready
        if(!blockedQ.isEmpty() && blockedQ.peek().getReadyTime() <= time){
            while(!blockedQ.isEmpty() && blockedQ.peek().getReadyTime() <= time){
                readyQ.add(blockedQ.remove());
            }
        }
        //if ready q is still empty, wait until next un-blocked jobs, and move them
        if(!blockedQ.isEmpty() && readyQ.isEmpty()){
            time = blockedQ.peek().getReadyTime();
            while(!blockedQ.isEmpty() && blockedQ.peek().getReadyTime() == time){
                readyQ.add(blockedQ.remove());
            }
        }
    }
}
