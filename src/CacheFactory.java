/**
 * CacheFactory
 */
public class CacheFactory {
    public Cache getCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty, String trace) {
        if (inputReplacementPolicy == null) {
            return null;
        }

        if (inputReplacementPolicy == 0) {
            return new LRUCache(inputSize, inputAssoc, inputBlockSize, inputReplacementPolicy, inputInclusionProperty, trace);
        } else if (inputReplacementPolicy == 1) {
            return new FIFOCache(inputSize, inputAssoc, inputBlockSize, inputReplacementPolicy, inputInclusionProperty, trace);
        } else if (inputReplacementPolicy == 2) {
            return new OptimalCache(inputSize, inputAssoc, inputBlockSize, inputReplacementPolicy, inputInclusionProperty, trace);
        } else {
            System.out.println("Cache replacement policy invalid!!!\n");
            return null;
        }
    }
}