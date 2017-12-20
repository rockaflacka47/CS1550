public class RandomPageFrame {
    private String address;
    private boolean dirty;
    
    public RandomPageFrame() {
        dirty = false;
    }
    
    public RandomPageFrame(String address, boolean dirty) {
        this.address = address;
        this.dirty = dirty;
    }

    public boolean isEmpty(){
        if(this.address == null)
            return true;
        return false;
    }

    public String getAddress(){
        return this.address;
    }

    public void setAddress(String address){
        this.address = address;
    }
    public boolean equalsAddress(String address) {
        return this.address.equals(address);
    }
    public boolean isDirty(){
        return this.dirty;
    }
    public void setDirty(boolean isDirty){
        this.dirty = isDirty;
    }


}