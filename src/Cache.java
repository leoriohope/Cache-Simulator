public class Cache {
    int size;
    int assoc;
    int blockSize;
    int numOfSet;
    String replacementPolicy;
    String inclusionProperty;
    int[][] cacheData;

    public Cache(int inputSize, int inputAssoc, int inputBlockSize, String inputReplacementPolicy, String inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new int[numOfSet][assoc];
    }

    public static void main(String[] args) {
        Cache myCache = new Cache(1024, 2, 16, "LRU", "non-inclusive");
        System.out.println(myCache.numOfSet);
        System.out.println(myCache.blockSize);
    }
}