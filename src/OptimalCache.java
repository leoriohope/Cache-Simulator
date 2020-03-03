import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;

/**
 * OptimalCache
 */
public class OptimalCache implements Cache{
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

    // States for optimal preprocessing
    public Integer counter = -1; // Eachtime call isHit(), increment counter
    ArrayList<Integer> entryList = new ArrayList<>();
    ArrayList<Integer> optIndex = new ArrayList<>();

    public OptimalCache(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty, String trace) {
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

        // preprocess trace
        preprocess(trace);
    }

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
        //Write when miss
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if (entry == 0L) { //Find the first empty entry
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
            if (((entry & 1L) == 0L) && (getTag(entry >> 2)).equals(tag)) { //Find the first empty entry
                // System.out.println("find a write hit");
                cacheData[index][i] = (((address << 2) | 2L)); // Don't make dirty here
                updateOrder(address);
                return cacheData[index][i];
            } 
        } 
        //Write when miss
        for (int i = 0; i < assoc; i++) {
            Long entry = cacheData[index][i];
            if (entry == 0L) { //Find the first empty entry
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
            if (entry == 0) { 
                return null;
            } 
        }
        // If there is not empty or invalid line, evict the one with highest order
        Integer max = 0;
        Integer maxIndex = 0;
        for (int i = 0; i < assoc; i++) {
            Integer currOrder = order[index][i];
            if (currOrder == 2147483647) {
                maxIndex = i;
                break;
            }
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
        counter++;
        // System.out.println("counter:  " + counter);
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
                // order[index][i] = 0; // update the order either
                if ((entry & 2L) == 1L) {
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
            if (getTag(entry >> 2).equals(tag)) {
                order[index][i] = optIndex.get(counter); // Set the current order for the delta access
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

    private void printList(ArrayList list) {
        for (int i = 0; i < list.size(); i++) {
            System.out.println(list.get(i));
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

    private void preprocess(String trace) {
		try {
			File f = new File("../traces/" + trace);
			BufferedReader b = new BufferedReader(new FileReader(f));
			String readLine = "";
			String[] operationArr;
			while ((readLine = b.readLine()) != null) {
				// System.out.println(readLine);
                operationArr = readLine.split(" "); //["r", "FF2008CD"]
                Long address = Long.parseLong(operationArr[1], 16);
                Long tagAndIndex = address >> blockLength;
                // System.out.println(tagAndIndex);
                entryList.add(tagAndIndex.intValue());
            }
            // record the next apper of each entry
            for (int i = 0; i < entryList.size(); i++) {
                int j = i + 1;
                for (; j < entryList.size(); j++) {
                    if (entryList.get(i).equals(entryList.get(j))) {
                        optIndex.add(j);
                        break;
                    }
                }
                if (j == entryList.size()) {
                    optIndex.add(2147483647);
                }
            }
            // printList(optIndex);
            // System.out.println(optIndex.size());
		} catch (IOException e) {
            e.printStackTrace();
		}
    }





    public static void main(String[] args) {
        OptimalCache myCache = new OptimalCache(1024, 2, 16, 0, 0, "vortex_trace.txt");
        // System.out.println(myCache.getIndex(1073955232L));
        // System.out.println(myCache.getTag(1073955232L));
        System.out.println(myCache.isHit(2063808508L));
        System.out.println(myCache.writeAndSetDirty(2063808508L));
        
        System.out.println(myCache.isHit(2063808508L));
        System.out.println(myCache.read(2063808508L));

        System.out.println(myCache.isHit(2063812084L));
        System.out.println(myCache.writeAndSetDirty(2063812084L));

        System.out.println(myCache.evict(2063812084L));

        // System.out.println(myCache.isHit(2063808508L));
        // System.out.println(myCache.read(1073874784L));
        // System.out.println(myCache.writeAndSetDirty(14667688L));
        // // System.out.println(myCache.invalid(14667688L));
        // System.out.println("evicted: " + myCache.evict(14667688L));
        // System.out.println("Write: " + myCache.write(1111157664L));
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