/**
 * CacheSimulater
 */
public class CacheSimulater {
    Cache l1Cache;
    Cache l2Cache;
    
    public CacheSimulater(Integer blockSize, Integer l1Size, Integer l1Assoc, Integer l2Size, Integer l2Assoc, Integer replacementPolicy, Integer inclusionProperty) {
        CacheFactory cacheFactory = new CacheFactory();

        l1Cache = cacheFactory.getCache(l1Size, l1Assoc, blockSize, replacementPolicy, inclusionProperty);
        if (l2Size != 0) {
                l2Cache = cacheFactory.getCache(l2Size, l2Assoc, blockSize, replacementPolicy, inclusionProperty);
        }
    }

    public void runSimulationByStep(String operation) {
        System.out.println("runrunrun");
    }

    public void printStates() {
        System.out.println("statestatestate");;
    }


    public static void main(String[] args) {
        new HelloWorld(args);
    }
}