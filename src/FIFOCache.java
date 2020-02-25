/**
 * FIFOCache
 */
public class FIFOCache implements Cache {
    // Cache meta data
    Integer size;
    Integer assoc;
    Integer blockSize;
    Integer numOfSet;
    Integer replacementPolicy;
    Integer inclusionProperty;
    Long[][] cacheData;
    Integer[][] order; // for FIFO counter which one is first
    Integer[] cnt; // 
    private Long tagLength;
    private Long idxLength;
    private Long blockLength;

    // Cache states
    Integer numOfRead = 0;
    Integer numOfReadMiss = 0;
    Integer numOfWrite = 0;
    Integer numOfWriteMiss = 0;
    Double missRate = 0.0;
    Integer numOfWriteback = 0;

    public FIFOCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new Long[numOfSet][assoc];
        order = new Integer[numOfSet][assoc];
        cnt = new Integer[numOfSet];
        idxLength = log2(numOfSet);
        blockLength = log2(blockSize * 8);//16 in byte
        tagLength = 32 - idxLength - blockLength;
    }

    /**
     * Read the cache line; Return the content; Do not change the order 
     */
    @Override
    public Long read(Long address) {
        // TODO update order and cache states
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            if ((cacheData[index][i] >> 2) == tag) { //Last bit for valid or invalid, the second last bit for dirty or non-dirty
                return cacheData[index][i];
            }
        }
        return null;
    }

    @Override
    public Long write(Long address) {
        // TODO update order and cache states
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        // Write when hit
        for (int i = 0; i < assoc; i++) {
            if ((cacheData[index][i] >> 2) == tag) { 
                cacheData[index][i] |= 2L; // & 0...000010 to set the dirty bit
                return cacheData[index][i];
            }
        }
        //Write when miss
        for (int i = 0; i < assoc; i++) {
            if ((cacheData[index][i] & 1L) == 0 || cacheData[index][i] == 0) { 
                cacheData[index][i] = (address << 2) | 2L; //Set the cache line with address and set the dirty bit
                return cacheData[index][i];
            }
        }
        return null; // Impossible in my case
    }

    @Override
    public Long evict(Long address) {
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            if ((cacheData[index][i] & 1L) == 0 || cacheData[index][i] == 0) { 
                return null;
            } 
        }
        for (int i = 0; i < assoc; i++) {
            // TODO choose the one with smallest order and evict it
        }
        return null;
    }

    @Override
    public Boolean isHit(Long address) {
        // TODO Auto-generated method stub
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            if ((cacheData[index][i] >> 2) == tag) {
                return true;
            }
        }
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
        System.out.println(myCache.getIndex(1073955232L));
        System.out.println(myCache.getTag(1073955232L));
        // System.out.println(myCache.numOfSet);
        // System.out.println(myCache.tagLength);
        // System.out.println(myCache.idxLength);
        // System.out.println(myCache.blockLength);
    }
}