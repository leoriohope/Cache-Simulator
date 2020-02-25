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

    /**
     * Run the simulation using 1 input
     * Input: operation(String)
     * Output: viod
     * 
     */
    public void runSimulationByStep(String operation) {
        Long address = getAddress(operation);
        Integer operand = getOperand(operation);
        // Check whether L1 cache hit
        if (l1Cache.isHit(address)) {  //increment hit count in cache itself
            if (operand == 0) {
                l1Cache.read(address);
            } else {
                l1Cache.write(address);  // TODO: handle mark  dirty in write
            }
        } else { // If not hit
                // Handle L2 first
            if (l2Cache.isHit(address)) {
                if (operand == 0) {
                    l2Cache.read(address);
                } else {
                    l2Cache.write(address);
                }
            } else {
                Long l2Evicted = l2Cache.evict();
                l2Cache.write(address);
                if (operand == 0) {
                    l2Cache.read(address);       
                }            
            }

            Long l1Evicted = l1Cache.evict();
            l1Cache.write(address);
            if (operand == 0) {
                l1Cache.read(address);       
            } 

            if (l1Evicted != null) {
                if (l2Cache.isHit(l1Evicted)) {
                    if (operand == 0) {
                        l2Cache.read(l1Evicted);
                    } else {
                        l2Cache.write(l1Evicted); //Should mark dirty
                    }
                } else {
                    Long l2Evicted = l2Cache.evict();
                    l2Cache.write(l1Evicted);
                    if (operand == 0) {
                        l2Cache.read(l1Evicted);       
                    }            
                }
            }
        }
        return;
    }

    public void printStates() {
        System.out.println("statestatestate");;
    }

    /**
     * Get Integer address from operation String
     * @param String operation
     */
    private Long getAddress(String operation) {
        String address = operation.split(" ")[1];
        return Long.parseLong(address, 16);
    }

    /**
     * Get "r" or "w", r is 0, w is 1
     * @param String operation
     */
    private Integer getOperand(String operation) {
        String operand = operation.split(" ")[0];
        if (operand.equals("r")) {
            return 0;
        } else {
            return 1;
        }
    }


    public static void main(String[] args) {
        CacheSimulater mySimulater = new CacheSimulater(16, 1024, 2, 0, 0, 1, 0);
        System.out.println(mySimulater.getAddress("w dfcfa8"));
        System.out.println(mySimulater.getOperand("w dfcfa8"));
    }
}