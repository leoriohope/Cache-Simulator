/**
 * CacheSimulater
 */
public class CacheSimulater {
    Cache l1Cache;
    Cache l2Cache;
    //Simulater states
    Integer l1Reads = 0;
    Integer l1ReadMiss = 0;
    Integer l1Writes = 0;
    Integer l1WriteMiss = 0;
    Integer l1Writebacks = 0;
    Double  l1MissRate = 0.0;
    Integer l2Reads = 0;
    Integer l2ReadMiss = 0;
    Integer l2Writes = 0;
    Integer l2WriteMiss = 0;
    Integer l2Writebacks = 0;
    Double  l2MissRate = 0.0;

    
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

        if (operand == 0) {
            read(address);
        } else {
            write(address);
        }
        // Check whether L1 cache hit
        // if (l1Cache.isHit(address)) {  //increment hit count in cache itself
        //     if (operand == 0) {
        //         l1Cache.read(address);
        //     } else {
        //         l1Cache.write(address);  // TODO: handle mark dirty in write
        //     }
        // } else { // If not hit
        //     // Handle L2 first
        //     if (l2Cache != null) {
        //         if (l2Cache.isHit(address)) {
        //             if (operand == 0) {
        //                 l2Cache.read(address);
        //             } else {
        //                 l2Cache.write(address);
        //             }
        //         } else {
        //             Long l2Evicted = l2Cache.evict(address);
        //             l2Cache.write(address);
        //             if (operand == 0) {
        //                 l2Cache.read(address);       
        //             }            
        //         }
        //     }
        //     Long l1Evicted = l1Cache.evict(address);
        //     l1Cache.write(address);
        //     if (operand == 0) {
        //         l1Cache.read(address);       
        //     } 

        //     if (l1Evicted != null) {
        //         if (l2Cache != null) {
        //             if (l2Cache.isHit(l1Evicted)) {
        //                 if (operand == 0) {
        //                     l2Cache.read(l1Evicted);
        //                 } else {
        //                     l2Cache.write(l1Evicted); //Should mark dirty
        //                 }
        //             } else {
        //                 Long l2Evicted = l2Cache.evict(address);
        //                 l2Cache.write(l1Evicted);
        //                 if (operand == 0) {
        //                     l2Cache.read(l1Evicted);       
        //                 }            
        //             }
        //         }   
        //     }
        // }
        // return;
    }

    public void read(Long address) {
        if (l1Cache.isHit(address)) {
            l1Cache.read(address); //update 
            return;
        } else {
            Long l1Evict = l1Cache.evict(address);
            if (l2Cache != null) {
                if (l1Evict != null) {
                    //Init a l2 write
                    if (l2Cache.isHit(l1Evict)) {
                        l2Cache.writeAndSetDirty(l1Evict);
                    } else { //if l2 not hit on write
                        Long l2Evict = l2Cache.evict(l1Evict);
                        l2Cache.writeAndSetDirty(l1Evict);
                    }
                //Init a l2 read
                if (l2Cache.isHit(address)) {
                    l2Cache.read(address);
                } else {
                    Long l2Evict = l2Cache.evict(address);
                    l2Cache.write(address);
                }
            }
            l1Cache.write(address); //Write here is allocate
            l1Cache.read(address);
        }
    }

    public void write(Long address) {
        if (l1Cache.isHit(address)) {
            l1Cache.writeAndSetDirty(address);
        } else {
            Long l1Evict = l1Cache.evict(address);
            if (l2Cache != null && )
        }
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