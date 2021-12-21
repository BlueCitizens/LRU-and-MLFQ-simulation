package multiLevQue;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.*;


public class MFQSimulation {
    private static JFrame frame = new JFrame("进程&页面管理课程设计");
    private static Container container = frame.getContentPane();
    ;

    private static JPanel mpanel = new JPanel();
    private static JScrollPane scrollPane0 = new JScrollPane(mpanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    private static JPanel panel = new JPanel();
    private static JScrollPane scrollPane = new JScrollPane(panel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);
    //页表窗格
    private static JPanel pageTablePanel = new JPanel();
    private static JScrollPane scrollPane2 = new JScrollPane(pageTablePanel, ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED, ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED);

    //菜单组件
    private static JMenuBar menuBar = new JMenuBar();
    private static JMenu processSettingsMenu = new JMenu("设置");
    private static JMenuItem createProcessItem = new JMenuItem("创建进程");
    private static JMenuItem startMFQItem = new JMenuItem("开始");
    private static JMenuItem stopMFQItem = new JMenuItem("终止");
    private static JMenuItem setTimeSliceItem = new JMenuItem("设置时间片");
    private static JMenuItem exitSystemItem = new JMenuItem("退出");

    private static JMenu presetMenu = new JMenu("预设");
    private static JMenuItem presetItem = new JMenuItem("default1");
    private static JMenu helpMenu = new JMenu("帮助");
    private static JMenuItem tutorialItem = new JMenuItem("使用说明");
    private static JMenuItem aboutItem = new JMenuItem("关于");

    private static JButton stopButton = new JButton("暂停");
    private static JButton contButton = new JButton("继续");

    private static JLabel timeLbl = new JLabel();

    //设置优先级最高的队列的时间片大小默认值
    public static int timeSlice = 2;
    //队列数量
    public static final int queueSize = 5;
    //最大进程数
    public static final int memorySize = 11;
    //
    public static final int pageListSize = 10;
    //物理块大小
    public static final int stackSize = 10;

    public static int[] PCBsQueuesTimeSlice = new int[queueSize];

    //所有页面
    public static Page[] pages = new Page[pageListSize];

    //多级反馈队列
    public static PCBsQueue[] PCBsQueues = new PCBsQueue[queueSize];
    public static LinkedList<PCB> tmpQueue = new LinkedList<PCB>();

    //记录已经使用的pid
    public static int[] pidsUsed = new int[memorySize];

    //当前内存中的进程数
    public static int currentPCBsNum = 0;

    //全部已运行时间
    public static int currentTime = 0;

    //内存中能够容纳的最大进程数（这里取决于可分配的pid的个数）
    public static final int PCBS_MAX_NUM = 10;

    //是否停止执行
    public static boolean isStopScheduling;
    //是否暂停执行
    public static boolean isPauseScheduling;

    //main函数
    public static void main(String[] args) {
        new MFQSimulation().initWindow();
    }

    //执行窗口初始化
    public void initWindow() {
        //设置窗口风格为Windows风格
        setWindowsStyle();
        //创建菜单栏
        processSettingsMenu.add(createProcessItem);
        processSettingsMenu.addSeparator();
        processSettingsMenu.add(startMFQItem);
        processSettingsMenu.addSeparator();
        processSettingsMenu.add(stopMFQItem);
        processSettingsMenu.addSeparator();
        processSettingsMenu.add(setTimeSliceItem);
        processSettingsMenu.addSeparator();
        processSettingsMenu.add(exitSystemItem);

        presetMenu.add(presetItem);

        helpMenu.add(tutorialItem);
        processSettingsMenu.addSeparator();
        helpMenu.add(aboutItem);

        menuBar.add(processSettingsMenu);
        menuBar.add(presetMenu);
        menuBar.add(helpMenu);

        frame.setJMenuBar(menuBar);

        initMemory();

        panel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        mpanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
        pageTablePanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

        frame.setContentPane(container);
        scrollPane0.setBounds(0, 0, 960, 150);
        scrollPane.setBounds(0, 180, 960, 440);
        scrollPane2.setBounds(970, 0, 280, 620);
        stopButton.setBounds(20, 152, 50, 25);
        stopButton.setBorder(BorderFactory.createEtchedBorder());
        contButton.setBounds(100, 152, 50, 25);
        contButton.setBorder(BorderFactory.createEtchedBorder());
        timeLbl.setBounds(500, 152, 200, 25);
        timeLbl.setFont(new Font("微软雅黑", Font.BOLD, 16));
        container.add(scrollPane0);
        container.add(scrollPane);
        container.add(scrollPane2);
        container.add(stopButton);
        container.add(contButton);
        container.add(timeLbl);
        container.setLayout(null);
        frame.setSize(1280, 700);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);


        //为控件绑定监听器
        setComponentsListeners();
    }

    //设置Swing的控件显示风格为Windows风格
    public static void setWindowsStyle() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        } catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            e.printStackTrace();
        }
    }

    //初始化相关内存参数
    public static void initMemory() {
        currentPCBsNum = 0;
        currentTime = 0;
        /*timeSlice = 2;*/

        Arrays.fill(pidsUsed, 1, 11, 0);

        for (int i = 0; i < PCBsQueues.length; i++) {
            PCBsQueues[i] = new PCBsQueue(i);
        }

        int firstSlice = timeSlice;
        for (int i = PCBsQueuesTimeSlice.length - 1; i >= 0; i--) {
            //队列优先级每降一级，时间片增加
            PCBsQueuesTimeSlice[i] = timeSlice;
            timeSlice = firstSlice * 2;
        }
        timeLbl.setText("就绪。。。");
    }

    //给窗口中所有控件绑定监听器
    public static void setComponentsListeners() {
        createProcessItem.setAccelerator(KeyStroke.getKeyStroke('F', InputEvent.CTRL_MASK));
        createProcessItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                createProcess();
            }
        });


        startMFQItem.setAccelerator(KeyStroke.getKeyStroke('S', InputEvent.CTRL_MASK));
        startMFQItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                startMFQSimulation();
            }
        });

        stopMFQItem.setAccelerator(KeyStroke.getKeyStroke('P', InputEvent.CTRL_MASK));
        stopMFQItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                stopMFQSimulation();
            }
        });

        setTimeSliceItem.setAccelerator(KeyStroke.getKeyStroke('T', InputEvent.CTRL_MASK));
        setTimeSliceItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                setTimeSlice();
            }
        });


        exitSystemItem.setAccelerator(KeyStroke.getKeyStroke('E', InputEvent.CTRL_MASK));
        exitSystemItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                System.exit(0);
            }
        });

        tutorialItem.setAccelerator(KeyStroke.getKeyStroke('T', InputEvent.CTRL_MASK));
        tutorialItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Multilevel feedback queue simulation application\n\nCopyright © 2019, 20171344047戴翔宇@NUIST, All Rights Reserved.");
            }
        });

        aboutItem.setAccelerator(KeyStroke.getKeyStroke('A', InputEvent.CTRL_MASK));
        aboutItem.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                JOptionPane.showMessageDialog(frame, "Multilevel feedback queue simulation application\n\nCopyright © 2019, 20171344047戴翔宇@NUIST, All Rights Reserved.");
            }
        });

        stopButton.setMnemonic(KeyEvent.VK_A);
        stopButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                pauseMFQSimulation();
            }
        });

        contButton.setMnemonic(KeyEvent.VK_D);
        contButton.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                contMFQSimulation();
            }
        });

    }

    //创建新进程
    public static void createProcess() {
        if (currentPCBsNum == PCBS_MAX_NUM) {
            JOptionPane.showMessageDialog(frame, "内存已满！");
        } else {
            String inputArv = JOptionPane.showInputDialog(frame, "输入到达时间：", 0);
            int arvInput = Integer.parseInt(inputArv);
            while (arvInput < currentTime) {
                JOptionPane.showMessageDialog(frame, "非法输入！");
                inputArv = JOptionPane.showInputDialog(frame, "输入到达时间：", 0);
                arvInput = Integer.parseInt(inputArv);
            }
            String inputSrv = JOptionPane.showInputDialog(frame, "输入服务时间：", 1);
            int srvInput = Integer.parseInt(inputSrv);
            while (srvInput <= 0) {
                JOptionPane.showMessageDialog(frame, "非法输入！");
                inputSrv = JOptionPane.showInputDialog(frame, "输入服务时间：", 0);
                srvInput = Integer.parseInt(inputSrv);
            }

            currentPCBsNum++;

            int randomPid = 1;

            while (pidsUsed[randomPid] == 1) {
                randomPid++;
            }

            pidsUsed[randomPid] = 1;

            int curPriority = PCBsQueues.length - 1;

            PCB pcb = new PCB(randomPid, "Ready", curPriority, srvInput, arvInput);

            //LinkedList<PCB> queue = PCBsQueues[PCBsQueues.length - 1].getQueue();
            LinkedList<PCB> queue = tmpQueue;
            boolean insertFlag = false;
            for (PCB e : queue) {
                if (pcb.getArrival() < e.getArrival()) {
                    int index = queue.indexOf(e);
                    queue.add(index, pcb);
                    insertFlag = true;
                    break;
                } else if (pcb.getArrival() == e.getArrival()) {
                    if (pcb.getLife() < e.getLife()) {
                        int index = queue.indexOf(e);
                        queue.add(index, pcb);
                        insertFlag = true;
                        break;
                    } else if (pcb.getLife() == e.getLife()) {
                        int index = queue.indexOf(e);
                        index++;
                        queue.add(index, pcb);
                        insertFlag = true;
                        break;
                    }
                }
            }
            if (!insertFlag) {
                queue.addLast(pcb);
            }
            /*PCBsQueues[curPriority].setQueue(queue);*/
            tmpQueue = queue;
            showPCBQueues(PCBsQueues);
        }
    }

    //开始调度
    public static void startMFQSimulation() {
        isStopScheduling = false;
        isPauseScheduling = false;

        //更新界面使用多线程实现
        new Thread(new Runnable() {
            @Override
            public void run() {
                //当前内存中还留有进程未执行
                while (currentPCBsNum != 0 && !isStopScheduling) {
                    boolean isEmptyFlag = false;
                    if (!tmpQueue.isEmpty() && tmpQueue.getFirst().getArrival() == currentTime) {
                        for (int i = 0; i < tmpQueue.size(); i++) {
                            if (tmpQueue.get(i).getArrival() == currentTime) {
                                PCB newPcb = tmpQueue.getFirst();
                                PCBsQueues[PCBsQueues.length - 1].getQueue().offer(newPcb);
                                tmpQueue.remove(i);
                                i--;
                            }
                        }
                    }

                    for (int i = PCBsQueues.length - 1; i >= 0; i--) {
                        LinkedList<PCB> queue = PCBsQueues[i].getQueue();

                        if (queue.size() > 0) {
                            isEmptyFlag = true;
                            //读取该队列首个PCB
                            PCB pcb = queue.element();
                            pcb.setStatus("Running");
                            int pid = pcb.getPid();
                            int priority = pcb.getPriority();
                            int life = pcb.getLife();
                            int curTimeSlice = PCBsQueuesTimeSlice[i];
                            ArrayList<Integer> arr = pcb.getPageMap();
                            while (curTimeSlice > 0) {
                                while (isPauseScheduling) {
                                    try {
                                        Thread.sleep((int) (1000));
                                    } catch (InterruptedException e) {
                                        e.printStackTrace();
                                    }
                                }
                                showPCBQueues(PCBsQueues);
                                //显示物理块状态
                                if (pcb.getPageBlock() != null) {
                                    showBlock(pcb);
                                }

                                //修改pcb属性
                                life = life - 1;
                                curTimeSlice--;
                                timeLbl.setText("已执行时间： " + currentTime);
                                currentTime++;
                                //
                                if (!tmpQueue.isEmpty() && tmpQueue.getFirst().getArrival() == currentTime) {
                                       /* PCB newPcb = tmpQueue.getFirst();
                                        PCBsQueues[PCBsQueues.length - 1].getQueue().offer(newPcb);
                                        tmpQueue.remove(0);*/
                                    for (int j = 0; j < tmpQueue.size(); j++) {
                                        if (tmpQueue.get(j).getArrival() == currentTime) {
                                            PCB newPcb = tmpQueue.getFirst();
                                            PCBsQueues[PCBsQueues.length - 1].getQueue().offer(newPcb);
                                            tmpQueue.remove(j);
                                            j--;
                                        }
                                    }
                                }
                                pcb.setLife(life);
                                pcb.setAlive(pcb.getAlive() + 1);
                                runLru(pcb);
                                //延时模拟执行过程，方便观察
                                try {
                                    Thread.sleep((int) (2500));
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                if (life <= 0) {
                                    break;
                                }
                            }
                            //若该进程执行完成
                            if (life <= 0) {
                                //移除该队列的首个PCB
                                queue.poll();
                                pidsUsed[pid] = 0;
                                currentPCBsNum--;
                            }
                            //若该进程还未执行完成,则改变其PCB的相关参数,并插入其优先级所对应的队列尾部
                            else {
                                //移除该队列的首个PCB
                                queue.poll();
                                priority = priority - 1;
                                pcb.setPriority(priority);
                                pcb.setLife(life);
                                pcb.setStatus("Ready");
                                LinkedList<PCB> nextQueue = PCBsQueues[priority].getQueue();
                                nextQueue.offer(pcb);
                                PCBsQueues[priority].setQueue(nextQueue);
                            }
                            break;
                        }
                    }
                    if (!isEmptyFlag) {
                        currentTime++;
                    }
                }
                //初始化
                initMemory();
                showBlock(null);
                showPCBQueues(PCBsQueues);
                //所有进程均执行完成，进程调度完成
                JOptionPane.showMessageDialog(frame, "进程处理完成!");
            }
        }).start();

    }

    //强制结束进程调度
    public static void stopMFQSimulation() {
        isStopScheduling = true;
        initMemory();
    }

    public static void pauseMFQSimulation() {
        isPauseScheduling = true;
    }

    public static void contMFQSimulation() {
        isPauseScheduling = false;
    }

    //设置时间片大小
    public static void setTimeSlice() {
        String inputMsg = JOptionPane.showInputDialog(frame, "输入时间片大小(seconds)：", 2);

        int timeSliceInput = Integer.parseInt(inputMsg);

        while (timeSliceInput <= 0) {
            JOptionPane.showMessageDialog(frame, "非法");
            inputMsg = JOptionPane.showInputDialog(frame, "输入时间片大小(seconds)：", "Set Time Slice", JOptionPane.PLAIN_MESSAGE);
            timeSliceInput = Integer.parseInt(inputMsg);
        }

        timeSlice = timeSliceInput;
        initMemory();
    }

    /*public static PCB runOnce(PCB pcb) {
        ArrayList<Integer> arr = pcb.getPageMap();
        LinkedList<Page> pageBlock = pcb.getPageBlock();
        int pageId = arr.get(0);

        *//*ListIterator<Page> listIterator = pageBlock.listIterator();

        while (listIterator.hasNext()) {

            if(listIterator.next().getNum() == pageId){
                arr.remove(0);
                pcb.setPageMap(arr);
                return pcb;
            }
        }*//*
        for (Page page : pageBlock) {
            if (page.getNum() == pageId) {
                arr.remove(0);
                pcb.setPageMap(arr);
                return pcb;
            }
        }
        Page p = new Page(pageId);
        pageBlock.add(0, p);
        if (pageBlock.size() > 5) {
            pageBlock.remove(5);
        }
        arr.remove(0);
        pcb.setPageMap(arr);
        pcb.setPageBlock(pageBlock);
        return pcb;
    }*/

    //LRU
    public static PCB runLru(PCB pcb) {
        ArrayList<Integer> arr = pcb.getPageMap();
        LinkedList<Page> pageBlock = pcb.getPageBlock();
        LinkedList<Page> pageStack = pcb.getLruSerial();
        int pageId = arr.get(0);
        for (int i = 0; i < pageStack.size(); i++) {
            if (pageStack.get(i).getNum() == pageId) {
                Page newPage = new Page();
                newPage.setNum(pageId);
                pageStack.remove(i);
                pageStack.add(0, newPage);
                arr.remove(0);
                pcb.setPageMap(arr);
                pcb.setLruSerial(pageStack);
                return pcb;
            }
        }
        Page p = new Page(pageId);
        pageStack.add(0, p);
        if (pageStack.size() > 5) {
            for (int j = 0; j < pageBlock.size(); j++) {
                if (pageBlock.get(j).getNum() == pageStack.getLast().getNum()) {
                    pageBlock.set(j, p);
                    break;
                }
            }
            pageStack.remove(5);
        } else {
            pageBlock.add(0, p);
        }
        arr.remove(0);
        pcb.setPageMap(arr);
        pcb.setLruSerial(pageStack);
        pcb.setPageBlock(pageBlock);
        return pcb;
    }

    //显示物理块状态
    public static void showBlock(PCB pcb) {
        if (pcb != null) {

            int queueLocationY = 0;
            JPanel queuesPanel = new JPanel();
            JPanel pageTabPanel = new JPanel();

            LinkedList<Page> blockQue = pcb.getPageBlock();//物理块状态
            LinkedList<Page> pageStack = pcb.getLruSerial();//LRU栈状态
            ArrayList<Integer> pageMap = pcb.getPageMap();//剩余页

            //访问序列
            if (pageMap.size() > 0) {
                //创建一个PCB队列
                JPanel pageQue = new JPanel();
                // pageQue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                pageQue.setLayout(new FlowLayout(FlowLayout.LEFT));
                pageQue.setBounds(0, queueLocationY, 800, 700);

                queueLocationY += 50;

                //创建队列前面的优先级提示块
                JLabel pageQuePriorityLabel = new JLabel("进程号 " + pcb.getPid() + " 访问序列 ");
                pageQuePriorityLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
                pageQuePriorityLabel.setOpaque(true);
                Border border = BorderFactory.createLineBorder(Color.BLACK);
                pageQuePriorityLabel.setBorder(border);

                JPanel pageQuePriorityBlock = new JPanel();
                pageQuePriorityBlock.add(pageQuePriorityLabel);

                pageQue.add(pageQuePriorityBlock);

                int i = 0;
                for (Integer entry : pageMap) {

                    JLabel keyLabel = new JLabel(" 页号: " + String.valueOf(entry));
                    keyLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
                    keyLabel.setOpaque(true);
                    keyLabel.setBackground(Color.DARK_GRAY);
                    keyLabel.setForeground(Color.ORANGE);
                    keyLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    JPanel PCBPanel = new JPanel();
                    PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    PCBPanel.setBackground(Color.LIGHT_GRAY);
                    PCBPanel.add(keyLabel);
                    if (entry == pageMap.get(0) && i == 0) {
                        JLabel stLabel = new JLabel("waiting");
                        stLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
                        stLabel.setOpaque(true);
                        stLabel.setBackground(Color.DARK_GRAY);
                        stLabel.setForeground(Color.ORANGE);
                        stLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                        PCBPanel.add(stLabel);
                    }
                    i++;
                    pageQue.add(new DrawLinePanel());
                    pageQue.add(PCBPanel);
                }

                queuesPanel.add(pageQue);
            }
            //物理块状态
            if (blockQue.size() > 0) {
                JPanel PCBsQueue = new JPanel();
                // PCBsQueue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                PCBsQueue.setLayout(new FlowLayout(FlowLayout.LEFT));
                PCBsQueue.setBounds(0, queueLocationY, 800, 700);

                queueLocationY += 50;

                JLabel PCBsQueuePriorityLabel = new JLabel("进程号 " + pcb.getPid() + " 物理块状态 ");
                PCBsQueuePriorityLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
                PCBsQueuePriorityLabel.setOpaque(true);
                Border border = BorderFactory.createLineBorder(Color.BLACK);
                PCBsQueuePriorityLabel.setBorder(border);

                JPanel PCBsQueuePriorityBlock = new JPanel();
                PCBsQueuePriorityBlock.add(PCBsQueuePriorityLabel);

                PCBsQueue.add(PCBsQueuePriorityBlock);

                int i = blockQue.size() - 1;
                for (Page page : blockQue) {

                    JLabel pidLabel = new JLabel("   " + String.valueOf(page.getNum()) + "   ");
                    pidLabel.setOpaque(true);
                    pidLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
                    pidLabel.setBackground(Color.DARK_GRAY);
                    pidLabel.setForeground(Color.cyan);
                    pidLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    JLabel blockLabel = new JLabel("   块号：" + i + "   ");
                    blockLabel.setOpaque(true);
                    blockLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
                    blockLabel.setBackground(Color.lightGray);
                    blockLabel.setForeground(Color.black);
                    blockLabel.setBorder(BorderFactory.createLineBorder(Color.lightGray));

                    //绘制一个PCB
                    JPanel PCBPanel = new JPanel();
                    PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    PCBPanel.setBackground(Color.LIGHT_GRAY);
                    PCBPanel.add(pidLabel);
                    PCBPanel.add(blockLabel);

                    //将PCB加入队列
                    PCBsQueue.add(new DrawLinePanel());
                    PCBsQueue.add(PCBPanel);
                    i--;
                }

                queuesPanel.add(PCBsQueue);
            }
            //LRU算法栈
            if (pageStack.size() > 0) {
                JPanel stackQue = new JPanel();
                // stackQue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                stackQue.setLayout(new FlowLayout(FlowLayout.LEFT));
                stackQue.setBounds(0, queueLocationY, 800, 700);

                queueLocationY += 50;

                JLabel pageQuePriorityLabel = new JLabel("进程号 " + pcb.getPid() + "     LRU栈    ");
                pageQuePriorityLabel.setFont(new Font("微软雅黑", Font.BOLD, 16));
                pageQuePriorityLabel.setOpaque(true);
                Border border = BorderFactory.createLineBorder(Color.BLACK);
                pageQuePriorityLabel.setBorder(border);

                JPanel pageQuePriorityBlock = new JPanel();
                pageQuePriorityBlock.add(pageQuePriorityLabel);

                stackQue.add(pageQuePriorityBlock);

                int i = 0;
                for (Page page : pageStack) {
                    if (i == 0) {
                        JLabel topLabel = new JLabel("   " + "栈顶" + "   ");
                        topLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
                        topLabel.setOpaque(true);
                        topLabel.setBackground(Color.lightGray);
                        topLabel.setForeground(Color.black);
                        topLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                        //绘制一个PCB
                        JPanel topPanel = new JPanel();
                        topPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//                        topPanel.setBackground(Color.LIGHT_GRAY);
                        topPanel.add(topLabel);
                        //将PCB加入队列
                        stackQue.add(new DrawLinePanel());
                        stackQue.add(topPanel);
                    }

                    JLabel pidLabel = new JLabel("   " + String.valueOf(page.getNum()) + "   ");
                    pidLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
                    pidLabel.setOpaque(true);
                    pidLabel.setBackground(Color.DARK_GRAY);
                    pidLabel.setForeground(Color.ORANGE);
                    pidLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                    //绘制一个PCB
                    JPanel PCBPanel = new JPanel();
                    PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    PCBPanel.setBackground(Color.LIGHT_GRAY);
                    PCBPanel.add(pidLabel);

                    //将PCB加入队列
                    stackQue.add(new DrawLinePanel());
                    stackQue.add(PCBPanel);

                    if (i == (pageStack.size() - 1)) {
                        JLabel bottomLabel = new JLabel("   " + "栈底" + "   ");
                        bottomLabel.setFont(new Font("微软雅黑", Font.BOLD, 12));
                        bottomLabel.setOpaque(true);
                        bottomLabel.setBackground(Color.lightGray);
                        bottomLabel.setForeground(Color.black);
                        bottomLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));

                        //绘制一个PCB
                        JPanel signPanel = new JPanel();
                        signPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
//                        signPanel.setBackground(Color.LIGHT_GRAY);
                        signPanel.add(bottomLabel);

                        //将PCB加入队列
                        stackQue.add(new DrawLinePanel());
                        stackQue.add(signPanel);
                    }
                    i++;
                }

                queuesPanel.add(stackQue);
            }


            /*创建页表*/


            // 表头（列名）
            Object[] columnNames = {"页号", "块号", "标志位"};
            // 创建表格模型
            Object[][] rowData = new Object[10][columnNames.length];
            for (int i = 0; i < 10; i++) {
                rowData[i][0] = i;
                rowData[i][2] = 0;
            }
            int j = blockQue.size() - 1;
            for (Page block : blockQue) {
                System.out.println(block.getNum());
                rowData[block.getNum()][1] = j;
                rowData[block.getNum()][2] = 1;
                j--;
            }
            DefaultTableModel dataModel = new DefaultTableModel(rowData, columnNames);
            // 创建JTable表格组件
            JTable table = new JTable();
            table.setModel(dataModel);

            table.setRowHeight(55);
            // 设置表头文字居中显示
            DefaultTableCellRenderer renderer = (DefaultTableCellRenderer) table.getTableHeader().getDefaultRenderer();
            renderer.setHorizontalAlignment(renderer.CENTER);

            // 设置表格中的数据居中显示
            DefaultTableCellRenderer r = new DefaultTableCellRenderer();
            r.setHorizontalAlignment(JLabel.CENTER);
            table.setDefaultRenderer(Object.class, r);

            table.setFocusable(false);

            table.setFont(new Font("新宋体", Font.PLAIN, 18));

            JScrollPane scrollPane = new JScrollPane(table);
            scrollPane.setPreferredSize(new Dimension(240, 590));
//            pageTabPanel.add(table.getTableHeader(), BorderLayout.EAST);
            pageTabPanel.add(scrollPane, BorderLayout.NORTH);

            //设置queuesPanel中的所有PCB队列（PCBsQueue组件）按垂直方向排列
            BoxLayout boxLayout = new BoxLayout(queuesPanel, BoxLayout.Y_AXIS);
            queuesPanel.setLayout(boxLayout);

            queuesPanel.setSize(800, 700);

            mpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            mpanel.removeAll();
            mpanel.add(queuesPanel);
            mpanel.updateUI();
            mpanel.repaint();
            pageTablePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            pageTablePanel.removeAll();
            pageTablePanel.add(pageTabPanel);
            pageTablePanel.updateUI();
            pageTablePanel.repaint();
        } else {
            mpanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            mpanel.removeAll();
            mpanel.updateUI();
            mpanel.repaint();
            pageTablePanel.setLayout(new FlowLayout(FlowLayout.LEFT));
            pageTablePanel.removeAll();
            pageTablePanel.updateUI();
            pageTablePanel.repaint();
        }
    }

    //显示内存中的多级反馈队列
    public static void showPCBQueues(PCBsQueue[] PCBsQueues) {
        int queueLocationY = 0;
        JPanel queuesPanel = new JPanel();

        if (tmpQueue.size() > 0) {
            //创建一个PCB队列
            JPanel PCBsQueue = new JPanel();
            // PCBsQueue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
            PCBsQueue.setLayout(new FlowLayout(FlowLayout.LEFT));
            PCBsQueue.setBounds(0, queueLocationY, 800, 700);

            queueLocationY += 50;

            //创建队列前面的优先级提示块
            JLabel PCBsQueuePriorityLabel = new JLabel("即将到达");
            PCBsQueuePriorityLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
            PCBsQueuePriorityLabel.setOpaque(true);
            Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
            PCBsQueuePriorityLabel.setBorder(border);

            JPanel PCBsQueuePriorityBlock = new JPanel();
            PCBsQueuePriorityBlock.add(PCBsQueuePriorityLabel);

            PCBsQueue.add(PCBsQueuePriorityBlock);

            for (PCB pcb : tmpQueue) {

                JLabel pidLabel = new JLabel(" 进程号: " + String.valueOf(pcb.getPid()));
                pidLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
                pidLabel.setOpaque(true);
                pidLabel.setBackground(Color.DARK_GRAY);
                pidLabel.setForeground(Color.ORANGE);
                pidLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                //设置status标签
                JLabel statusLabel = new JLabel(" 状态: " + pcb.getStatus());
                statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                statusLabel.setOpaque(true);
                statusLabel.setBackground(Color.DARK_GRAY);
                statusLabel.setForeground(Color.ORANGE);
                statusLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                //设置priority标签
                    /*JLabel priorityLabel = new JLabel(" Priority: " + String.valueOf(pcb.getPriority()));
                    priorityLabel.setOpaque(true);
                    priorityLabel.setBackground(Color.DARK_GRAY);
                    priorityLabel.setForeground(Color.ORANGE);
                    priorityLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));*/

                //设置life标签
                JLabel lifeLabel = new JLabel(" 剩余时间: " + String.valueOf(pcb.getLife()));
                lifeLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                lifeLabel.setOpaque(true);
                lifeLabel.setBackground(Color.DARK_GRAY);
                lifeLabel.setForeground(Color.ORANGE);
                lifeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                //设置life标签
                JLabel aliveLabel = new JLabel(" 活动时间: " + String.valueOf(pcb.getAlive()));
                aliveLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                aliveLabel.setOpaque(true);
                aliveLabel.setBackground(Color.DARK_GRAY);
                aliveLabel.setForeground(Color.ORANGE);
                aliveLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                //设置arrival标签
                JLabel arrivalLabel = new JLabel(" 到达时间: " + String.valueOf(pcb.getArrival()));
                arrivalLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                arrivalLabel.setOpaque(true);
                arrivalLabel.setBackground(Color.DARK_GRAY);
                arrivalLabel.setForeground(Color.ORANGE);
                arrivalLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                JLabel pageLabel = new JLabel(" 剩余页数: " + String.valueOf(pcb.getPageMap().size()));
                pageLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                pageLabel.setOpaque(true);
                pageLabel.setBackground(Color.DARK_GRAY);
                pageLabel.setForeground(Color.ORANGE);
                pageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                //绘制一个PCB
                JPanel PCBPanel = new JPanel();
                PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                PCBPanel.setLayout(new GridLayout(2, 3));
                PCBPanel.setBackground(Color.LIGHT_GRAY);
                PCBPanel.add(pidLabel);
                PCBPanel.add(arrivalLabel);
                PCBPanel.add(pageLabel);
                PCBPanel.add(statusLabel);
                //PCBPanel.add(priorityLabel);
                PCBPanel.add(aliveLabel);
                PCBPanel.add(lifeLabel);

                //将PCB加入队列
                PCBsQueue.add(new DrawLinePanel());
                PCBsQueue.add(PCBPanel);
            }

            queuesPanel.add(PCBsQueue);
        }

        for (int i = PCBsQueues.length - 1; i >= 0; i--) {
            LinkedList<PCB> queue = PCBsQueues[i].getQueue();

            if (queue.size() > 0) {
                //创建一个PCB队列
                JPanel PCBsQueue = new JPanel();
                // PCBsQueue.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                PCBsQueue.setLayout(new FlowLayout(FlowLayout.LEFT));
                PCBsQueue.setBounds(0, queueLocationY, 800, 700);

                queueLocationY += 50;

                //创建队列前面的优先级提示块
                JLabel PCBsQueuePriorityLabel = new JLabel(" # " + String.valueOf(queueSize - i) + "  ");
                PCBsQueuePriorityLabel.setFont(new Font("微软雅黑", Font.BOLD, 18));
                PCBsQueuePriorityLabel.setOpaque(true);
                Border border = BorderFactory.createLineBorder(Color.LIGHT_GRAY);
                PCBsQueuePriorityLabel.setBorder(border);

                JPanel PCBsQueuePriorityBlock = new JPanel();
                PCBsQueuePriorityBlock.add(PCBsQueuePriorityLabel);

                PCBsQueue.add(PCBsQueuePriorityBlock);

                for (PCB pcb : queue) {

                    JLabel pidLabel = new JLabel(" 进程号: " + String.valueOf(pcb.getPid()));
                    pidLabel.setFont(new Font("微软雅黑", Font.BOLD, 15));
                    pidLabel.setOpaque(true);
                    pidLabel.setBackground(Color.DARK_GRAY);
                    pidLabel.setForeground(Color.ORANGE);
                    pidLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                    //设置status标签
                    JLabel statusLabel = new JLabel(" 状态: " + pcb.getStatus());
                    statusLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                    statusLabel.setOpaque(true);
                    statusLabel.setBackground(Color.DARK_GRAY);
                    statusLabel.setForeground(Color.ORANGE);
                    statusLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                    //设置priority标签
                    /*JLabel priorityLabel = new JLabel(" Priority: " + String.valueOf(pcb.getPriority()));
                    priorityLabel.setOpaque(true);
                    priorityLabel.setBackground(Color.DARK_GRAY);
                    priorityLabel.setForeground(Color.ORANGE);
                    priorityLabel.setBorder(BorderFactory.createLineBorder(Color.BLACK));*/

                    //设置life标签
                    JLabel lifeLabel = new JLabel(" 剩余时间: " + String.valueOf(pcb.getLife()));
                    lifeLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                    lifeLabel.setOpaque(true);
                    lifeLabel.setBackground(Color.DARK_GRAY);
                    lifeLabel.setForeground(Color.ORANGE);
                    lifeLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                    //设置life标签
                    JLabel aliveLabel = new JLabel(" 活动时间: " + String.valueOf(pcb.getAlive()));
                    aliveLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                    aliveLabel.setOpaque(true);
                    aliveLabel.setBackground(Color.DARK_GRAY);
                    aliveLabel.setForeground(Color.ORANGE);
                    aliveLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                    //设置arrival标签
                    JLabel arrivalLabel = new JLabel(" 到达时间: " + String.valueOf(pcb.getArrival()));
                    arrivalLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                    arrivalLabel.setOpaque(true);
                    arrivalLabel.setBackground(Color.DARK_GRAY);
                    arrivalLabel.setForeground(Color.ORANGE);
                    arrivalLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                    //设置arrival标签
                    JLabel pageLabel = new JLabel(" 剩余页数: " + String.valueOf(pcb.getPageMap().size()));
                    pageLabel.setFont(new Font("微软雅黑", Font.BOLD, 13));
                    pageLabel.setOpaque(true);
                    pageLabel.setBackground(Color.DARK_GRAY);
                    pageLabel.setForeground(Color.ORANGE);
                    pageLabel.setBorder(BorderFactory.createLineBorder(Color.LIGHT_GRAY));

                    //绘制一个PCB
                    JPanel PCBPanel = new JPanel();
                    PCBPanel.setBorder(BorderFactory.createLineBorder(Color.BLACK));
                    PCBPanel.setLayout(new GridLayout(2, 3));
                    PCBPanel.setBackground(Color.LIGHT_GRAY);
                    PCBPanel.add(pidLabel);
                    PCBPanel.add(arrivalLabel);
                    PCBPanel.add(pageLabel);
                    PCBPanel.add(statusLabel);
                    //PCBPanel.add(priorityLabel);
                    PCBPanel.add(aliveLabel);
                    PCBPanel.add(lifeLabel);

                    //将PCB加入队列
                    PCBsQueue.add(new DrawLinePanel());
                    PCBsQueue.add(PCBPanel);
                }

                queuesPanel.add(PCBsQueue);
            }
        }


        //设置queuesPanel中的所有PCB队列（PCBsQueue组件）按垂直方向排列
        BoxLayout boxLayout = new BoxLayout(queuesPanel, BoxLayout.Y_AXIS);
        queuesPanel.setLayout(boxLayout);

        queuesPanel.setSize(800, 700);

        panel.setLayout(new FlowLayout(FlowLayout.LEFT));
        panel.removeAll();
        panel.add(queuesPanel);
        panel.updateUI();
        panel.repaint();
    }

}


//绘制直线类
class DrawLinePanel extends JPanel {
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawLine(0, this.getSize().height / 2, this.getSize().width, this.getSize().height / 2);

    }

}


