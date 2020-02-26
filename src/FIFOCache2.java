import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
/**
 * FIFOCache2
 */
public class FIFOCache2 implements Cache {
    // Cache meta data
    Integer size;
    Integer assoc;
    Integer blockSize;
    Integer numOfSet;
    Integer replacementPolicy;
    Integer inclusionProperty;
    List<LinkedList<Long>> queue;  
    private Long tagLength;
    private Long idxLength;
    private Long blockLength;
    // Cache states
    public Integer numOfRead = 0;
    Integer numOfReadMiss = 0;
    Integer numOfWrite = 0;
    Integer numOfWriteMiss = 0;
    Double missRate = 0.0;
    Integer numOfWriteback = 0;

    public FIFOCache2(Integer inputSize, Integer inputAssoc, Integer inputBlockSize, Integer inputReplacementPolicy, Integer inputInclusionProperty) {
        size = inputSize;
        assoc = inputAssoc;
        blockSize = inputBlockSize;
        numOfSet = size / (assoc * blockSize);
        replacementPolicy = inputReplacementPolicy;
        inclusionProperty = inputInclusionProperty;
        for (int i = 0; i < numOfSet; i++) { //Init cache
            LinkedList<Long> set = new LinkedList<>();
            for (Long j = 0L; j < assoc; j++) {
                set.add(j << 32); // Using [32 : 32 + assoc] to store way index
            }
            queue.add(set); 
        }
        idxLength = log2(numOfSet);
        blockLength = log2(blockSize * 8);//16 in byte
        tagLength = 32 - idxLength - blockLength;
    }

    /**
     * Check whether is hit; update hit and miss counter here
     */
    @Override
    public Boolean isHit(Long address) {
        // TODO Auto-generated method stub
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            Long curTag = (queue.get(index).get(i) >> 2) & (1L << 30 - 1L); //Last 2 bit for dirty and valid, the 32 to
            if (curTag == tag) { 
                return true;
            }
        }
        return false;
    }

    /**
     * Read the cache line; Return the content; Do not change the order 
     */
    @Override
    public Long read(Long address) {
        // TODO update order and cache states
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        for (int i = 0; i < assoc; i++) {
            if ((queue.get(index).get(i) >> 3) == tag) { //Last bit for valid or invalid, the second last bit for dirty or non-dirty
                return  queue.get(index).get(i);
            }
        }
        return null;
    }

    @Override
    public Long write(Long address) {
        // TODO update order and cache states
        Long tag = getTag(address);
        Integer index = getIndex(address).intValue();
        // Write when hit, no need to change the block index
        for (int i = 0; i < assoc; i++) {
            Long curr = queue.get(index).get(i);
            if ((curr >> 3) == tag) { 
                queue.get(index).set(i, curr | 4L); // & 0...0000100 to set the dirty bit
                return queue.get(index).get(i);
            }
        }
        //Write when miss, need to change the block index
        for (int i = 0; i < assoc; i++) {
            Long curr = queue.get(index).get(i);
            if (((curr & 4L) == 1L) || (curr >> 1 == 0L)) { 
                queue.get(index).set(i, (tag << 3) & 1); // & 0...0000100 to set the dirty bit
                return queue.get(index).get(i);
            }
        }
        return null; // Impossible in my case
    }

    @Override
    public void writeAndSetDirty(Long address) {
        // TODO Auto-generated method stub

    }

    @Override
    public Long evict(Long address) {

        return null;
    }

    @Override
    public Boolean invalid(Long address) {
        // TODO Auto-generated method stub
        return false;
        
    }

    @Override
    public void printState() {
        // TODO Auto-generated method stub

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
        FIFOCache2 myCache = new FIFOCache2(1024, 2, 16, 0, 0);
        System.out.println(myCache.getIndex(1073955232L));
        System.out.println(myCache.getTag(1073955232L));
        // System.out.println(myCache.numOfSet);
        // System.out.println(myCache.tagLength);
        // System.out.println(myCache.idxLength);
        // System.out.println(myCache.blockLength);
    }
}