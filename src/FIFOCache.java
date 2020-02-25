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
    private Long tagLength;
    private Long idxLength;
    private Long blockLength;

    public FIFOCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new Integer[numOfSet][assoc];
        idxLength = log2(numOfSet);
        blockLength = log2(blockSize * 8);//16 in byte
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

    private static Long log2(Integer n) {
        return Long.valueOf((Integer)(int)(Math.log(n) / Math.log(2)));
    }

    private Long getTag(Long address) {
        return address >> (blockLength + idxLength);
    }

    private Long getIndex(Long address) {
        return (address >> blockLength) & ((1L << idxLength) - 1L);
    }



    public static void main(String[] args) {
        FIFOCache myCache = new FIFOCache(1024, 2, 16, 0, 0);
        System.out.println(myCache.numOfSet);
        System.out.println(myCache.tagLength);
        System.out.println(myCache.idxLength);
        System.out.println(myCache.blockLength);
    }
}