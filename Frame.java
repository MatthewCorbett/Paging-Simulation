// Frame.java
// Author: Matthew Corbett
// SN: C3308222
// Course: COMP2240
// Desc: This class represents a Frame (block of physical memory).
//       It will contain a page(int) of a process to be executed.

public class Frame {
    private int pageNumber; //the page in the frame
    private int use_bit; //used for clock policy, 0 = no chances, 1 = one chance
    private int linkedPID; //processor that this frame/page is a resident of

    //Constructor
    public Frame(int pageNumber, int use_bit, int currentPID){
        this.pageNumber = pageNumber;
        this.use_bit = use_bit;
        this.linkedPID = currentPID;
    }

    //Dummy Constructor
    public Frame(){
        this.pageNumber = -1;
        this.use_bit = -1;
        this.linkedPID = -1;
    }

    //getters
    public int getPageNumber(){return pageNumber;}
    public int getUseBit(){return use_bit;}
    public int getLinkedPID(){return linkedPID;}

    //setters
    public void setPageNumber(int pageNumber){this.pageNumber = pageNumber;}
    public void setUseBit(int use_bit){this.use_bit = use_bit;}
    public void setLinkedPID(int linkedPID){this.linkedPID = linkedPID;}

}
