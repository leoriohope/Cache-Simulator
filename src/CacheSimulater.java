/**
 * CacheSimulater
 */
public class CacheSimulater {
    Cache l1Cache;
    Cache l2Cache;
    
    public CacheSimulater(Integer blockSize, Integer l1Size, Integer l1Assoc, Integer l2Size, Integer l2Assoc, Integer replacementPolicy, Integer inclusionProperty) {
        l1Cache = new Cache(l1Size, l1Assoc, blockSize, replacementPolicy, inclusionProperty);
        if (l2Size != 0) {
            l2Cache = new Cache(l2Size, l2Assoc, blockSize, replacementPolicy, inclusionProperty);
        }
    }

    public static void main(String[] args) {
        new HelloWorld(args);
    }
}