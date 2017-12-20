import java.util.Random;
public class RandomPageTable{
    private RandomPageFrame[] frames;
    
    private int active;
    private int pageFaults;
    private int diskWrites;
    private Random rand;
    public RandomPageTable(int numFrames){
        frames = new RandomPageFrame[numFrames];
        for (int i=0; i < numFrames; i++) {
            frames[i] = new RandomPageFrame();
        }
        
        active = 0;
        pageFaults = 0;
        diskWrites = 0;
        rand = new Random();

    }

    public void write(String address){
        int location = search(address);
        
        // couldn't find, so insert
        if (location == -1) {
            if (active < frames.length) {
                // there's room to insert
                frames[active].setAddress(address);
                frames[active].setDirty(true);
               
                System.out.println("Address: " + address + " Action: Page Fault - No Evict");
                active++;
                pageFaults++;
            } else {
                // must evict an existing page and insert new
                replace(address, true);
                
            }
        } else {
            // frame already existed; update dirty flag
            frames[location].setDirty(true);         // dirty because this is a write
            System.out.println("Address: " + address + " Action: Hit");
        }
    }

    private void replace(String address, boolean isDirty) {
        int toEvict = rand.nextInt((frames.length));
        
        // see if the current frame needs written to disk before replacing
        if (frames[toEvict].isDirty()) {
            diskWrites++;
            System.out.println("Address: " + address + " Action: Page Fault - Evict Dirty");
        }
        else{
            System.out.println("Address: " + address + " Action: Page Fault - Evict Clean");
        }
        
        
        frames[toEvict].setAddress(address);
        frames[toEvict].setDirty(isDirty);
        
        // update total number of page faults
        pageFaults++;
        
       
        
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
                active++;
                System.out.println("Address: " + address + " Action: Page Fault - No Eviction");
                pageFaults++;
            } else {
                // must evict an existing page and insert new
                replace(address, false);
            }
        } else {
            
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