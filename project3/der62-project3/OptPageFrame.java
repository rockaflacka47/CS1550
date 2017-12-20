public class OptPageFrame {
    private String address;
    private boolean dirty;
    
    public OptPageFrame() {
        this.dirty = false;
    }
  
    public boolean isEmpty() {
        return address==null;
    }
        
    public String getAddress() {
        return this.address;
    }
    
    public void setAddress(String address) {
        this.address = address;
    }
    
    public boolean equalsAddress(String address) {
        return this.address.equals(address);
    }
    
    public boolean isDirty() {
        return this.dirty;
    }
    
    public void setDirty(boolean dirty) {
        this.dirty = dirty;
    }
}