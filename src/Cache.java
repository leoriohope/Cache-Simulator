/**
 * Cache
 */
public interface Cache {
    
    public Integer read();
    public Integer write();
    public boolean isHit();
    public void printState();
}
