/**
 * OptimalCache
 */
public class OptimalCache implements Cache{
    Integer size;
    Integer assoc;
    Integer blockSize;
    Integer numOfSet;
    Integer replacementPolicy;
    Integer inclusionProperty;
    Integer[][] cacheData;
    Integer[][] order; // for FIFO counter which one is first
    Integer[] cnt; // 
    private Long tagLength;
    private Long idxLength;
    private Long blockLength;

    public OptimalCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new Integer[numOfSet][assoc];
        idxLength = (Long)Math.round(Math.log(Double.valueOf(numOfSet)));
        blockLength = (Long)Math.round(Math.log(Double.valueOf(blockSize * 8))); //16 in byte
        tagLength = 32 - idxLength - blockLength;
    }

    /**
     * Read the cache line; Return the content; Do not change the order 
     */
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
    public Long evict(Long address) {
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

    private Long getTag(Long address) {
        return address >> (blockLength + idxLength);
    }

    private Long getIndex(Long address) {
        return (address >> blockLength) & ((1L << idxLength) - 1L);
    }

    // public static void main(String[] args) {
    //     OptimalCache myCache = new OptimalCache(1024, 2, 16, "LRU", "non-inclusive");
    //     System.out.println(myCache.numOfSet);
    //     System.out.println(myCache.blockSize);
    // }
}