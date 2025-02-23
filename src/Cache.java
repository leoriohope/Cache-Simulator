/**
 * Cache
 */
public interface Cache {
    public Long read(Long address);
    public Long write(Long address);
    public Long evict(Long address);
    public Boolean invalid(Long address);
    public Long writeAndSetDirty(Long address);
    public Boolean isHit(Long address);
    public void printState();
}
