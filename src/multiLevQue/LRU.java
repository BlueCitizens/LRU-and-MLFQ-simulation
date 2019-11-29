package multiLevQue;

import java.util.*;

public class LRU {

    public PCB runOnce(PCB pcb) {
        ArrayList<Integer> arr = pcb.getPageMap();
        LinkedList<Page> pageBlock = pcb.getPageBlock();
        int pageId = arr.get(0);

        ListIterator<Page> listIterator = pageBlock.listIterator();

        while (listIterator.hasNext()) {

            if(listIterator.next().getNum() == pageId){
                arr.remove(0);
                pcb.setPageMap(arr);
                return pcb;
            }
        }
        Page p = new Page(pageId);
        pageBlock.add(0, p);
        if(pageBlock.size() > 5){
            pageBlock.remove(5);
        }
        arr.remove(0);
        pcb.setPageMap(arr);
        pcb.setPageBlock(pageBlock);
        return pcb;
    }
}
