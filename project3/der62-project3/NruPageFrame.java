public class NruPageFrame {
    private String address;
    private boolean dirty;
    private boolean referenced;
    
    public NruPageFrame() {
        this.referenced = false;
        this.dirty = false;
    }
    
    public NruPageFrame(String address, boolean isDirty) {
        this.address = address;
        this.referenced = true;
        this.dirty = isDirty;
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

    public boolean isReferenced(){
        return this.referenced;
    }

    public boolean isDirty(){
        return this.dirty;
    }
    public void setReferenced(boolean referenced) {
        this.referenced = referenced;
    }

    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }

}