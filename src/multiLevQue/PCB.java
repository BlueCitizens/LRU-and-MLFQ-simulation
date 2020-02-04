package multiLevQue;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Random;

//进程控制块类
public class PCB {

    private int pid;//进程标识符
    private String status;//进程状态标识
    private int priority;//进程优先级
    private int life;//进程服务时间
    private int alive;//进程已运行时间
    private int arrival;//进程到达时间
    private ArrayList<Integer> pageMap = new ArrayList<Integer>();//进程请求页序列
    //进程分配的物理块
    private LinkedList<Page> pageBlock = new LinkedList<Page>();
    private LinkedList<Page> lruSerial = new LinkedList<Page>();
    public PCB() {
    }
    public PCB(int pid, String status, int priority, int life, int arrival) {
        this.pid = pid;
        this.status = status;
        this.priority = priority;
        this.life = life;
        this.alive = 0;
        this.arrival = arrival;
        for (int i = 0; i < life; i++) {
            Random random = new Random();
            int randomPage = random.nextInt(10);
            this.pageMap.add(randomPage);
        }
    }
    public PCB(int pid, String status, int priority, int life, int arrival, int[] pageNum) {
        this.pid = pid;
        this.status = status;
        this.priority = priority;
        this.life = life;
        this.alive = 0;
        this.arrival = arrival;
    }

    public int getPid() {
        return pid;
    }

    public void setPid(int pid) {
        this.pid = pid;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getLife() {
        return life;
    }

    public void setLife(int life) {
        this.life = life;
    }

    public int getAlive() {
        return alive;
    }

    public void setAlive(int alive) {
        this.alive = alive;
    }

    public int getArrival() {
        return arrival;
    }

    public void setArrival(int arrival) {
        this.arrival = arrival;
    }

    public ArrayList<Integer> getPageMap() {
        return pageMap;
    }

    public void setPageMap(ArrayList<Integer> pageMap) {
        this.pageMap = pageMap;
    }

    public LinkedList<Page> getPageBlock() {
        return pageBlock;
    }

    public void setPageBlock(LinkedList<Page> pageBlock) {
        this.pageBlock = pageBlock;
    }

    public LinkedList<Page> getLruSerial() {
        return lruSerial;
    }

    public void setLruSerial(LinkedList<Page> lruSerial) {
        this.lruSerial = lruSerial;
    }
}
