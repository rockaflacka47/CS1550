public class ClockPageTable{
    private ClockPageFrame[] frames;
    private int oldest;
    private int active;
    private int pageFaults;
    private int diskWrites;
    public ClockPageTable(int numFrames){
        frames = new ClockPageFrame[numFrames];
        for (int i=0; i < numFrames; i++) {
            frames[i] = new ClockPageFrame();
        }
        
        oldest = 0;
        active = 0;
        pageFaults = 0;
        diskWrites = 0;

    }

    public void write(String address){
        int location = search(address);
        
        // couldn't find, so insert
        if (location == -1) {
            if (active < frames.length) {
                // there's room to insert
                frames[active].setAddress(address);
                frames[active].setDirty(true);
                frames[active].setReferenced(true);
                System.out.println("Address: " + address + " Action: Page Fault - No Evict");
                active++;
                pageFaults++;
            } else {
                // must evict an existing page and insert new
                replace(address, true);
                
            }
        } else {
            // frame already existed; update referenced & dirty flag
            frames[location].setReferenced(true);
            frames[location].setDirty(true);         // dirty because this is a write
            System.out.println("Address: " + address + " Action: Hit");
        }
    }

    private void replace(String address, boolean isDirty) {
        while (frames[oldest].isReferenced()) {
            frames[oldest].setReferenced(false);
            oldest = (oldest+1) % frames.length;  // "wrap" around
        }
        
        // see if the current frame needs written to disk before replacing
        if (frames[oldest].isDirty()) {
            diskWrites++;
            System.out.println("Address: " + address + " Action: Page Fault - Evict Dirty");
        }
        else{
            System.out.println("Address: " + address + " Action: Page Fault - Evict Clean");
        }
        
        // overwrite the oldest unreferenced frame with the new address
        frames[oldest].setAddress(address);
        frames[oldest].setReferenced(true);
        frames[oldest].setDirty(isDirty);
        
        // update total number of page faults
        pageFaults++;
        
        // oldest is now next to the currently replaced page frame
        oldest = (oldest+1) % frames.length;  // "wrap" around
    }

    private int search(String address) {
        int location = -1;
        for (int i=0; i<active; i++) {
            if (frames[i].equalsAddress(address)) {
                location = i;
            }
        }
        
        return location;
    }
    public void read(String address){
        int location = search(address);
        
        // couldn't find, so insert
        if (location == -1) {
            if (active < frames.length) {
                // there's room to insert
                frames[active].setAddress(address);
                frames[active].setDirty(false);
                frames[active].setReferenced(true);
                active++;
                System.out.println("Address: " + address + " Action: Page Fault - No Eviction");
                pageFaults++;
            } else {
                // must evict an existing page and insert new
                replace(address, false);
            }
        } else {
            // frame already existed; update referenced flag
            frames[location].setReferenced(true);
            System.out.println("Address: " + address + " Action: Hit");
        }

    }
        
    public int getNumPageFaults(){
        return this.pageFaults;
    }
    public int getNumWritesToDisk(){
        return this.diskWrites;
    }
}