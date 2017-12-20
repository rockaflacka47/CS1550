
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Arrays;
import java.util.HashMap;
import java.util.LinkedList;
import java.io.*;
import java.util.*;

public class OptPageTable {
    /**
     * Key - String value to hold memory address
     * Value - A LinkedList object holding the line-number(s) of the memory address
     */
    private Hashtable<String, Queue<String>> futureHash;
    OptPageFrame[] frames;

    private int activeFrames;
    private int pageFaults;
    private int diskWrites;
    private int lineNumber;
    private int time;
    
    public OptPageTable(int numFrames, String traceFile) {
        
        frames = new OptPageFrame[numFrames];
        for (int i=0; i < numFrames; i++) {
            frames[i] = new OptPageFrame();
        }
        futureHash = new Hashtable<String,Queue<String>>();
        
        this.activeFrames = 0;
        this.pageFaults = 0;
        this.diskWrites = 0;
        this.lineNumber = 0;
        this.time = 0;
       

        int lineNum = 0;
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(traceFile)));
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
              time++;
              String address = currentLine.split(" ")[0];
              //address = (address>>12) & 0xFFFFF;
              Queue temp = futureHash.get(address);
              if(temp == null)
                temp = new LinkedList<String>();
              temp.add(time);
              futureHash.put(address, temp);
            }
            time = 0;
            br.close();
          }catch(Exception e){
            e.printStackTrace();
          }
        
        lineNum = 0;
    }
    public void run(String traceFile){
        try{
            BufferedReader br = new BufferedReader(new FileReader(new File(traceFile)));
            String currentLine;
            while ((currentLine = br.readLine()) != null) {
                time++;  
                String address = currentLine.split(" ")[0];
                int loc = search(address);
                if(loc == -1){
                    OptPageFrame frame = new OptPageFrame();
                    frame.setAddress(address);
                    if(currentLine.split(" ")[1].equals("W")){
                        frame.setDirty(true);
                        
                    }
                    if(activeFrames < frames.length){                   //check if frames are full, if not put this one in the next active
                        frames[activeFrames] = frame;
                        activeFrames++;
                        pageFaults++;
                        System.out.println("Address: " + address + " Action: Page Fault - No Eviction");
                    }
                    else{
                        pageFaults++;
                        int latest = 0;
                        int index = -1;
                        for(int i = 0; i < activeFrames; i++){               //For each frame in physical memory, find the next reference to determine which page to replace
                          Queue temp = futureHash.get(frames[i].getAddress());
                          while(temp.peek() != null){
                            if(time > (int)temp.peek()){                   //Remove all references that happened already
                              temp.remove();
                            }else
                              break;
                          }
                          if(temp.peek() == null){                         //if temp.peek() == null, there is no more references to this page so evict this one
                            index = i;
                            break;
                          }else if((int)temp.peek() > latest){              //remove the frame referenced the furthest from now
                            latest = (int)temp.peek();
                            index = i;
                          }
                        }
                        if(frames[index].isDirty()){
                            diskWrites++;
                            System.out.println("Address: " + address + " Action: Page Fault - Evict Dirty");
                        }
                        else
                            System.out.println("Address: " + address + " Action: Page Fault - Evict Clean");
                        frames[index] = frame;

                     }
                     
                }
                else
                    System.out.println("Address: " + address + " Action: Hit");

            }     
        }        
        catch(Exception e){

        }                         
    }
    
    public int getNumPageFaults() {
        return this.pageFaults;
    }
    public int getNumWritesToDisk() {
        return this.diskWrites;
    }
    
    private int search(String address) {
        int frameLocation = -1;
        for (int i=0; i<activeFrames; i++) {
            if (frames[i].equalsAddress(address)) {
                frameLocation = i;
            }
        }
        
        return frameLocation;
    }  
}