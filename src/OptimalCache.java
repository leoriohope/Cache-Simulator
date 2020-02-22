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

    public OptimalCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new Integer[numOfSet][assoc];
    }

    @Override
    public Integer read() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public Integer write() {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public boolean isHit() {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public void printState() {
        // TODO Auto-generated method stub

    }

    // public static void main(String[] args) {
    //     OptimalCache myCache = new OptimalCache(1024, 2, 16, "LRU", "non-inclusive");
    //     System.out.println(myCache.numOfSet);
    //     System.out.println(myCache.blockSize);
    // }
}