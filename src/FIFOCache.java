/**
 * FIFOCache
 */
public class FIFOCache implements Cache {
    Integer size;
    Integer assoc;
    Integer blockSize;
    Integer numOfSet;
    Integer replacementPolicy;
    Integer inclusionProperty;
    Integer[][] cacheData;
    Integer[][] order; // for FIFO counter which one is first
    Integer[] cnt; // 

    public FIFOCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new Integer[numOfSet][assoc];
    }

    @Override
    public Long read(Long address) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long write(Long address) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Long evict() {
        return null;
    }

    @Override
    public Boolean isHit(Long address) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void printState() {
        // TODO Auto-generated method stub

    }

    // public static void main(String[] args) {
    //     FIFOCache myCache = new FIFOCache(1024, 2, 16, "LRU", "non-inclusive");
    //     System.out.println(myCache.numOfSet);
    //     System.out.println(myCache.blockSize);
    // }
}