# LRU-and-MLFQ-simulation
操作系统原理，多级反馈队列MLFQ算法结合LRU页面置换算法的操作系统进程处理图形化模拟程序。

本科课程设计一枚，纯Java，Windows风格swing界面。内含课程设计论文。

## Updates
2021/12/21

修复了页面序列中重复请求某一页时同时显示waiting状态的bug

新增了当前进程的页表显示功能（数据结构很粗糙，仅作展示目的）

图形界面相关的若干优化，简洁易懂

## 程序实现&操作方法
详见design document doc

物理块大小为5，hard code。

时间片可调，默认为2。

## 程序演示
![screenshot](https://github.com/BlueCitizens/LRU-and-MLFQ-simulation/blob/master/screenshot.gif)
  
## 致谢
受(https://github.com/Yuziquan/MultilevelFeedbackQueueSimulation)启发，感谢开源力量。
