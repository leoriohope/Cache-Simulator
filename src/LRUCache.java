/**
 * LRUCache
 */
public class LRUCache implements Cache {
    // Cache meta data
    Integer size;
    Integer assoc;
    Integer blockSize;
    Integer numOfSet;
    Integer replacementPolicy;
    Integer inclusionProperty;
    Long[][] cacheData; // 0 bit valid, 1 bit dirty, 2 - 33 bits address!!!
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

    public LRUCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty, String trace) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        cacheData = new Long[numOfSet][assoc];
        for (int i = 0; i < numOfSet; i++) { // Init cache
            for (int j = 0; j < assoc; j++) {
                cacheData[i][j] = 0L;
            }
        }
        order = new Integer[numOfSet][assoc];
        for (int i = 0; i < numOfSet; i++) { // Init order
            for (int j = 0; j < assoc; j++) {
                order[i][j] = 0;
            }
        }
        cnt = new Integer[numOfSet];
        idxLength = log2(numOfSet);
        blockLength = log2(blockSize);//16 in byte
        tagLength = 32 - idxLength - blockLength;
    }

    /**
     * Read the cache line; Return the content; Do not change the order 
     */
    @Override
    public Long read(Long address) {
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        // System.out.println("tag from read(): " + tag);
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if ((getTag(entry >> 2)).equals(tag)) { //Last bit for valid or invalid, the second last bit for dirty or non-dirty
                // System.out.println("tag in compare: " + getTag(entry >> 2));
                updateOrder(address);
                return address;
            }
        }
        return null;
    }
    

    @Override
    public Long write(Long address) {
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        // System.out.println(index);
        //Write when hit
        // for (int i = 0; i < assoc; i++) {
        //     Long entry = cacheData[index][i];
        //     if ((entry >> 2) == address) { //Find the first empty entry
        //         cacheData[index][i] = (address << 2); // Don't make dirty here
        //         // updateOrder(address);
        //         return cacheData[index][i];
        //     } 
        // }      
        //Write when miss
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if ((entry & 1L) == 1L || entry == 0L) { //Find the first empty entry
                cacheData[index][i] = (address << 2); // Don't make dirty here
                updateOrder(address);
                return cacheData[index][i];
            } 
        }
        return null; 
    }

    @Override
    public Long writeAndSetDirty(Long address) {
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        // Write when hit
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if (((entry & 1L) == 0L) && ((getTag(entry >> 2)).equals(tag))) { //Find the first empty entry
                // System.out.println("find a write hit");
                cacheData[index][i] = (((address << 2) | 2L)); // Don't make dirty here
                updateOrder(address);
                return cacheData[index][i];
            } 
        } 
        //Write when miss
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if ((entry & 1L) != 0 || entry == 0L) { //Find the first empty entry
                cacheData[index][i] = ((address << 2) | 2L); // make dirty here
                updateOrder(address);
                return cacheData[index][i];
            }
        }
        return null;
    }

    /**
     * return Long, which is 34 bits entry, not only the address
     */
    @Override
    public Long evict(Long address) {
        Integer index = getIndex(address).intValue();
        //If there is invalid or empty line, return null
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if ((entry & 1L) != 0 || entry == 0) { 
                return null;
            } 
        }
        // If there is not empty or invalid line, evict the one with highest order
        Integer max = 0;
        Integer maxIndex = 0;
        for (int i = 0; i < assoc; i++) {
            Integer currOrder = order[index][i];
            if (currOrder > max) {
                max = currOrder;
                maxIndex = i;
            }
        }
        Long evicted = cacheData[index][maxIndex];
        cacheData[index][maxIndex] = 0L;
        order[index][maxIndex] = 0; //reset the order of the cache line
        return evicted;
    }

    @Override
    public Boolean isHit(Long address) {
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if ((entry & 1L) == 0L && getTag(entry >> 2).equals(tag)) {
                return true;
            }
        }
        return false;
    }

    /**
     * return boolean
     *  if true: the invalid block is dirty, need to update the writeback counter;
     *  if false: Just invalid it.
     */
    @Override
    public Boolean invalid(Long address) {
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if (((entry & 1L) != 1L) && getTag(entry >> 2).equals(tag)) {
                cacheData[index][i] |= 1L; // set the last bit to 1
                order[index][i] = 0; // update the order either
                if ((entry & 2L) != 0) {
                    return true; // the invlided block is dirty
                }
                return false;
            }
        }   
        return false;
    }

    private void updateOrder(Long address) {
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if ((entry & 1L) == 0 && entry != 0) { // update the counter for each entry
                order[index][i]++;
            }
            if ((entry & 1L) == 0 && getTag(entry >> 2).equals(tag)) {
                order[index][i] = 0;
            }
        }
    }

    @Override
    public void printState() {
        // Set     0:      20018a    20028d D 
        for (int i = 0; i < numOfSet; i++) {
            System.out.print("Set     " + i + ":   ");
            for (int j = 0; j < assoc; j++) {
                Long entry = cacheData[i][j];
                Long tag = getTag(entry >> 2);
                // System.out.println(tag);
                System.out.print("    " + Long.toHexString(tag));
                if ((entry & 2L) != 0) {
                    // System.out.println("ldfdsl  " + (entry & 2L));
                    System.out.print(" D  ");
                } 
                if ((entry & 1L) != 0) {
                    System.out.print(" I  ");
                }
            }
            System.out.println();
        }
    }

    
    private void printOrder() {
        for (int i = 0; i < numOfSet; i++) {
            System.out.print("Set     " + i + ":   ");
            for (int j = 0; j < assoc; j++) {
                System.out.print(order[i][j] + " ");
            }
            System.out.println();
        }
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
        LRUCache myCache = new LRUCache(1024, 2, 16, 0, 0, "gcc_trace.txt");
        // System.out.println(myCache.getIndex(1073955232L));
        // System.out.println(myCache.getTag(1073955232L));

        // System.out.println(myCache.write(1073955232L));
        System.out.println(myCache.writeAndSetDirty(1073955232L));
        System.out.println(myCache.writeAndSetDirty(14667688L));
        System.out.println(myCache.read(1073955232L));
        // System.out.println(myCache.invalid(14667688L));
        System.out.println("evicted: " + myCache.evict(14667688L));
        System.out.println("Write: " + myCache.write(1111157664L));
        // System.out.println(myCache.isHit(1073955232L));
        // System.out.println(myCache.read(1073955232L));
        // System.out.println(myCache.isHit(1073955232L));


        myCache.printState();
        myCache.printOrder();


        // System.out.println(myCache.numOfSet);
        // System.out.println("tag length:  " + myCache.tagLength);
        // System.out.println("idx length:  " + myCache.idxLength);
        // System.out.println("blocklength: " + myCache.blockLength);
    }
}