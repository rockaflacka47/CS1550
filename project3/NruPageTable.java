public class NruPageTable{
    private NruPageFrame[] frames;
    private int oldest;
    private int active;
    private int pageFaults;
    private int diskWrites;
    public NruPageTable(int numFrames){
        frames = new NruPageFrame[numFrames];
        for (int i=0; i < numFrames; i++) {
            frames[i] = new NruPageFrame();
        }
        
        oldest = 0;
        active = 0;
        pageFaults = 0;
        diskWrites = 0;

    }
    public void res(){
        for (int i=0; i < frames.length; i++) {
            this.frames[i].setReferenced(false);
        }
        
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
            frames[location].setDirty(true);         
            System.out.println("Address: " + address + " Action: Hit");
        }
    }
   
    private void replace(String address, boolean isDirty) {
        if(checkZero()){
            System.out.println("Address: " + address + " Action: Page Fault - Evict Clean");
            // overwrite the first unreferenced and clean frame with the new address
            frames[oldest].setAddress(address);
            frames[oldest].setReferenced(true);
            frames[oldest].setDirty(isDirty);
            
            // update total number of page faults
            pageFaults++;
        }
        else if(checkOne()){
            System.out.println("Address: " + address + " Action: Page Fault - Evict Dirty");
            // overwrite the first unreferenced frame with the new address
            frames[oldest].setAddress(address);
            frames[oldest].setReferenced(true);
            frames[oldest].setDirty(isDirty);
            
            // update total number of page faults
            diskWrites++;
            pageFaults++;
        }
        else if(checkTwo()){
            System.out.println("Address: " + address + " Action: Page Fault - Evict Clean");
            // overwrite the first referenced and clean frame with the new address
            frames[oldest].setAddress(address);
            frames[oldest].setReferenced(true);
            frames[oldest].setDirty(isDirty);
            
            // update total number of page faults
            pageFaults++;
        }
        else if(checkThree()){
            System.out.println("Address: " + address + " Action: Page Fault - Evict Dirty");
            // overwrite the first referenced and dirty frame with the new address
            frames[oldest].setAddress(address);
            frames[oldest].setReferenced(true);
            frames[oldest].setDirty(isDirty);
            
            // update total number of page faults
            diskWrites++;
            pageFaults++;
        }
    }
    private boolean checkThree(){
        for(int i = 0; i < frames.length; i++){
            if(frames[i].isDirty() && frames[i].isReferenced()){
                oldest = i;
                return true;
            }
        }
        return false;
    }
    private boolean checkTwo(){
        for(int i = 0; i < frames.length; i++){
            if(!frames[i].isDirty() && frames[i].isReferenced()){
                oldest = i;
                return true;
            }
        }
        return false;
    }
    private boolean checkOne(){
        for(int i = 0; i < frames.length; i++){
            if(!frames[i].isReferenced() && frames[i].isDirty()){
                oldest = i;
                return true;
            }
        }
        return false;
    }
    private boolean checkZero(){
        for(int i = 0; i < frames.length; i++){
            if(!frames[i].isReferenced() && !frames[i].isDirty()){
                oldest = i;
                return true;
            }
        }
        return false;
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