/**
 * CacheSimulater
 */
public class CacheSimulater {
    Cache l1Cache;
    Cache l2Cache;
    Integer inclusion;
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
    Integer totalMemoryTraffic = 0;

    
    public CacheSimulater(Integer blockSize, Integer l1Size, Integer l1Assoc, Integer l2Size, Integer l2Assoc, Integer replacementPolicy, Integer inclusionProperty, String trace) {
        CacheFactory cacheFactory = new CacheFactory();

        l1Cache = cacheFactory.getCache(l1Size, l1Assoc, blockSize, replacementPolicy, inclusionProperty, trace);
        if (l2Size != 0) {
                l2Cache = cacheFactory.getCache(l2Size, l2Assoc, blockSize, replacementPolicy, inclusionProperty, trace);
        }
        inclusion = inclusionProperty;
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
    }

    public void read(Long address) {
        l1Reads++;  //Each read count
        if (l1Cache.isHit(address)) {
            l1Cache.read(address); //update 
            return;
        } else {
            l1ReadMiss++;
            Long l1Evict = l1Cache.evict(address);
            if (l2Cache != null) {
                if ((l1Evict != null) && ((l1Evict & 2) != 0)) { //Init the write requres only when the evicted block is dirty
                    Long l1EvictAddress = l1Evict >> 2;
                    l1Writebacks++;
                    l2Writes++;
                    //Init a l2 write
                    if (l2Cache.isHit(l1EvictAddress)) {
                        l2Cache.writeAndSetDirty(l1EvictAddress);
                    } else { //if l2 not hit on write
                        l2WriteMiss++;
                        totalMemoryTraffic++;
                        Long l2Evict = l2Cache.evict(l1EvictAddress);
                        if (l2Evict != null && ((l2Evict & 1L) != 1)) {
                            Long l2EvictAddress = l2Evict >> 2;
                            if ((l2Evict & 2L) != 0) { 
                                l2Writebacks++;
                                totalMemoryTraffic++;
                            }
                            if (inclusion == 1) {
                                if (l1Cache.invalid(l2EvictAddress)) {
                                    totalMemoryTraffic++;
                                }
                            }
                        }
                        l2Cache.writeAndSetDirty(l1EvictAddress);
                    }
                }
                //Init a l2 read
                l2Reads++;
                if (l2Cache.isHit(address)) {
                    l2Cache.read(address);
                } else {
                    l2ReadMiss++;
                    totalMemoryTraffic++;
                    Long l2Evict = l2Cache.evict(address);
                    if (l2Evict != null && ((l2Evict & 1L) != 1)) {
                        Long l2EvictAddress = l2Evict >> 2;
                        if ((l2Evict & 2L) != 0) { 
                            l2Writebacks++;
                            totalMemoryTraffic++;
                        }
                        if (inclusion == 1) {
                            if (l1Cache.invalid(l2EvictAddress)) {
                                totalMemoryTraffic++;
                            }
                        }
                    }
                    l2Cache.write(address);
                }
            } else {
                if ((l1Evict != null) && ((l1Evict & 2L) != 0)) { //Init the write requres only when the evicted block is dirty
                    // System.out.println("A write back!");
                    l1Writebacks++;
                    totalMemoryTraffic++;
                }
                totalMemoryTraffic++;
            }
            l1Cache.write(address); //Write here is allocate
            l1Cache.read(address);
        }
    }

    public void write(Long address) {
        l1Writes++;
        if (l1Cache.isHit(address)) {
            l1Cache.writeAndSetDirty(address);
        } else {
            l1WriteMiss++;
            Long l1Evict = l1Cache.evict(address);
            if (l2Cache != null) {
                if ((l1Evict != null) && ((l1Evict & 2) != 0)) { //Init the write requres only when the evicted block is dirty
                    Long l1EvictAddress = l1Evict >> 2;
                    l1Writebacks++;
                    l2Writes++;
                    //Init a l2 write
                    if (l2Cache.isHit(l1EvictAddress)) {
                        l2Cache.writeAndSetDirty(l1EvictAddress);
                    } else { //if l2 not hit on write
                        l2WriteMiss++;
                        totalMemoryTraffic++;
                        Long l2Evict = l2Cache.evict(l1EvictAddress);
                        if (l2Evict != null && ((l2Evict & 1L) != 1)) {
                            Long l2EvictAddress = l2Evict >> 2;
                            if ((l2Evict & 2L) != 0) { 
                                l2Writebacks++;
                                totalMemoryTraffic++;
                            }
                            if (inclusion == 1) {
                                if (l1Cache.invalid(l2EvictAddress)) {
                                    totalMemoryTraffic++;
                                }
                            }
                        }
                        l2Cache.writeAndSetDirty(l1EvictAddress);
                    }
                }
                //Init a l2 read
                l2Reads++;
                if (l2Cache.isHit(address)) {
                    l2Cache.read(address);
                } else {
                    l2ReadMiss++;
                    totalMemoryTraffic++;
                    Long l2Evict = l2Cache.evict(address);
                    if (l2Evict != null && ((l2Evict & 1L) != 1)) {
                        Long l2EvictAddress = l2Evict >> 2;
                        if ((l2Evict & 2L) != 0) {
                            l2Writebacks++;
                            totalMemoryTraffic++;
                        }
                        if (inclusion == 1) {
                            if (l1Cache.invalid(l2EvictAddress)) {
                                totalMemoryTraffic++;
                            }
                        }
                    }
                    l2Cache.write(address);
                }
            } else {
                if ((l1Evict != null) && (l1Evict & 2L) != 0) { //Init the write requres only when the evicted block is dirty
                        l1Writebacks++;
                        totalMemoryTraffic++;
                }
                totalMemoryTraffic++;
            }
            l1Cache.writeAndSetDirty(address); //Write here is allocate
        }
    }

    public void printStates() {
        System.out.println("===== Simulation results (raw) ===== ");
        System.out.println("a. number of L1 reads:   " + l1Reads);
        System.out.println("b. number of L1 read misses:   " + l1ReadMiss);
        System.out.println("c. number of L1 writes:   " + l1Writes);
        System.out.println("d. number of L1 write misses:   " + l1WriteMiss);
        System.out.printf("e. L1 miss rate:     %.6f", (l1ReadMiss + l1WriteMiss) / (double)(l1Reads + l1Writes));
        System.out.println();
        System.out.println("f. number of L1 writebacks:    " + l1Writebacks);
        System.out.println("g. number of L2 reads:    " + l2Reads);
        System.out.println("h. number of L2 read misses:    " + l2ReadMiss);
        System.out.println("i. number of L2 writes:     " + l2Writes);
        System.out.println("j. number of L2 write misses:    " + l2WriteMiss);
        System.out.print("k. L2 miss rate:     ");
        if ((l2Reads + l2Writes) == 0) {
            System.out.println(0);
        } else {
            System.out.println(String.format("%.6f", (l2ReadMiss) / (double)(l2Reads)));
        }
        System.out.println("l. number of L2 writebacks:     " + l2Writebacks);
        System.out.println("m. total memory traffic:      " + totalMemoryTraffic);

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
        CacheSimulater mySimulater = new CacheSimulater(16, 1024, 2, 0, 0, 1, 0, "gcc_trace.txt");
        mySimulater.runSimulationByStep("w 400341a0");
        mySimulater.runSimulationByStep("r dfcfa8");
        // System.out.println(mySimulater.getAddress("w dfcfa8"));
        // System.out.println(mySimulater.getOperand("w dfcfa8"));
        // mySimulater.write(1073955232L);
        // mySimulater.read(1073955232L);
        mySimulater.l1Cache.printState();
        mySimulater.printStates();
    }
}