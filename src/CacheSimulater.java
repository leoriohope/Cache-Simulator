/**
 * CacheSimulater
 */
public class CacheSimulater {
    Cache l1Cache;
    Cache l2Cache;
    
    public CacheSimulater(int blockSize, int l1Size, int l1Assoc, int l2Size, int l2Assoc, String replacementPolicy, String inclusionProperty) {
        l1Cache = new Cache(l1Size, l1Assoc, blockSize, replacementPolicy, inclusionProperty);
        if (l2Size != 0) {
            l2Cache = new Cache(l2Size, l2Assoc, blockSize, replacementPolicy, inclusionProperty);
        }
    }

    public static void main(String[] args) {
        new HelloWorld(args);
    }
}