/**
 * Cache
 */
public interface Cache {
    public Long read(Long address);
    public Long write(Long address);
    public Long evict();
    public Boolean isHit(Long address);
    public void printState();
}
