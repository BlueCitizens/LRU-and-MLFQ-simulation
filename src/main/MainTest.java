package main;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

public class MainTest {
	/*static LevelQueue<Process> lq = new LevelQueue<Process>(1, 1);
	public static void main(String[] args) {
		LevelQueue<Process> Level1Queue = new LevelQueue<Process>(1, 1); // 一级队列，轮转时间片为1
		LevelQueue<Process> Level2Queue = new LevelQueue<Process>(2, 2); // 二级队列，轮转时间片为2
		LevelQueue<Process> Level3Queue = new LevelQueue<Process>(3, 4); // 三级队列，轮转时间片为4
		LevelQueue<Process> Level4Queue = new LevelQueue<Process>(4, 999); // 四级队列，轮转时间片为999（认为是无限大）
		List<Process> processes = new ArrayList<Process>(); // 存储初始化准备进程的ArrayList
		int[] arrivalTime; // 进程到达时间数组
		int currentTime = 0; // 存储当前时间
		int processSum; // 总进程数
		int processRemaining; // 剩余进程数

		// 初始化进程信息

		Scanner sc = new Scanner(System.in);
		System.out.println("先输入总进程数， 再依次输入各个进程的到达时间和服务时间");
		processSum = sc.nextInt();
		processRemaining = processSum;
		arrivalTime = new int[processSum];// 初始化到达时间数组
		for (int i = 0; i < processSum; i++) {
			int a = sc.nextInt(); // 到达时间
			int s = sc.nextInt(); // 服务时间
			Process p = new Process("P" + i, a, s);
			arrivalTime[i] = a;
			processes.add(p);
		}
		sc.close();
		// //返回当前用户输入的数据
		// Iterator it = processes.iterator();
		// while (it.hasNext()){
		// Process p = (Process)it.next();
		// System.out.println(p.toString());
		// }
		Process.ifAllCompleted(); // 输出当前进程数

		// 将进程按照到达时间顺序进行排序 并获得到达时间数组

		for (int i = 0; i < processSum; i++) {
			for (int j = i + 1; j < processSum; j++) {
				Process p1 = (Process) processes.get(i);
				Process p2 = (Process) processes.get(j);
				if (p2.arrivalTime < p1.arrivalTime) {
					processes.set(i, p2);
					processes.set(j, p1);
					arrivalTime[i] = p2.arrivalTime;
					arrivalTime[j] = p1.arrivalTime;

				} else if (p2.arrivalTime == p1.arrivalTime) { // 若同时到达 则短作业优先
					if (p2.serviceTime < p1.serviceTime) {
						processes.set(i, p2);
						processes.set(j, p1);
						arrivalTime[i] = p2.arrivalTime;
						arrivalTime[j] = p1.arrivalTime;
					} else if (p2.serviceTime == p1.serviceTime) { // 若服务时间也相等，则将p2放在p1后面，并将p1后面的进程和p2交换
						Process tmp = (Process) processes.get(i + 1);
						processes.set(i + 1, p2);
						processes.set(j, tmp);
						arrivalTime[i + 1] = p2.arrivalTime;
						arrivalTime[j] = tmp.arrivalTime;
					}
				}

			}
		}
		// 验证 排序是否正确
		Iterator<Process> it2 = processes.iterator();
		while (it2.hasNext()) {
			Process p = (Process) it2.next();
			System.out.println(p.toString());
		}
		for (int i = 0; i < arrivalTime.length; i++) {
			System.out.println(arrivalTime[i]);
		}
		// 开始模拟运行

		int position = 0;// 记录当前运行的进程位置，从0第一个进程开始
		while (true) {
			// 首先判断是否有新的作业到达
			if (position < processSum) { // 若大于等于进程总数则证明全部入队
				if (arrivalTime[position] <= currentTime) { // 取出数组第一个进程的到达时间与当前时间进行比较
															// ,若等于或者小于当前时间，
															// 说明有程序到达并进入第一队列

					Process p = (Process) processes.get(position);
					Level1Queue.add(p); // 进程入一级队列
					System.out.println(p.name + "进程于" + p.arrivalTime + "时间，加入一级队列");
					if (position == 0) { // 若是第一个到达并开始的作业，则更新当前时间为其到达时间
						currentTime = p.arrivalTime;
					}
					System.out.println("当前时间:" + currentTime);
					p.queue = Level1Queue; // 为进程初始化当前所在队列信息
					position++;
				}
			} else {
				if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
					break;
				}
			}

			if (!Level1Queue.isEmpty()) // 若一级队列非空，则优先执行一级队列
			{
				Process p = Level1Queue.peek();
				if (p.serviceTime <= Level1Queue.timeSlice) { // 如果队头进程服务时间小于等于该队列时间片。
																// 则进程完成
					p.Finished(); // 进程结束 并自动出队
					processRemaining -= 1; // 进程总数-1
				} else {
					// 一级出队并打入下一级队列
					Level1Queue.poll();
					p.excutedTime += Level1Queue.timeSlice; // 更新进程的运行时间
					p.queue = Level2Queue; // 为进程更新当前所在队列信息
					Level2Queue.add(p); // 进入二级队列
				}

				// 更新时间 判断是否完成所有进程
				currentTime += Level1Queue.timeSlice; // 当前时间等于执行前时间加上当前队列时间片
				System.out.println("当前时间:" + currentTime);
				if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
					break;
				}

			} else if (!Level2Queue.isEmpty()) // 若一级队列为空，二级队列非空，则优先执行二级队列
			{
				Process p = Level2Queue.peek();
				int timeRemaining = p.serviceTime - p.excutedTime; // 进程剩余时间
				if (timeRemaining <= Level2Queue.timeSlice) { // 如果队头进程服务时间小于等于该队列时间片。
					// 则进程完成
					p.Finished(); // 进程结束 并自动出队
					processRemaining -= 1; // 进程总数-1

					// 更新时间 判断是否完成所有进程
					currentTime += timeRemaining; // 当前时间等于执行前时间加上当前队列时间片
					System.out.println("当前时间:" + currentTime);
					if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
						break;
					}

				} else {
					// 二级出队并打入下一级队列
					Level2Queue.poll();
					p.excutedTime += Level2Queue.timeSlice; // 更新进程的运行时间
					p.queue = Level3Queue; // 为进程更新当前所在队列信息
					Level3Queue.add(p); // 进入三级队列

					// 更新时间 判断是否完成所有进程
					currentTime += Level2Queue.timeSlice; // 当前时间等于执行前时间加上当前队列时间片
					System.out.println("当前时间:" + currentTime);
					if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
						break;
					}
				}

			} else if (!Level3Queue.isEmpty()) // 若二级队列为空，执行三级队列
			{
				Process p = Level3Queue.peek();
				int timeRemaining = p.serviceTime - p.excutedTime; // 进程剩余时间
				if (timeRemaining <= Level3Queue.timeSlice) { // 如果队头进程服务时间小于等于该队列时间片。
					// 则进程完成
					p.Finished(); // 进程结束 并自动出队
					processRemaining -= 1; // 进程总数-1

					// 更新时间 判断是否完成所有进程
					currentTime += timeRemaining; // 当前时间等于执行前时间加上当前队列时间片
					System.out.println("当前时间:" + currentTime);
					if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
						break;
					}
				} else {
					// 三级出队并打入下一级队列
					Level3Queue.poll();
					p.excutedTime += Level3Queue.timeSlice; // 更新进程的运行时间
					p.queue = Level4Queue; // 为进程更新当前所在队列信息
					Level4Queue.add(p); // 进入三级队列

					// 更新时间 判断是否完成所有进程
					currentTime += Level3Queue.timeSlice; // 当前时间等于执行前时间加上当前队列时间片
					System.out.println("当前时间:" + currentTime);
					if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
						break;
					}
				}
			} else if (!Level4Queue.isEmpty()) { // 最后执行四级队列
				Process p = Level4Queue.peek();
				int timeRemaining = p.serviceTime - p.excutedTime; // 进程剩余时间
				Process.ifAllCompleted();
				p.Finished(); // 进程结束 并自动出队
				processRemaining -= 1; // 进程总数-1
				currentTime += timeRemaining; // 当前时间等于执行前时间加上当前队列时间片
				System.out.println("当前时间:" + currentTime);
				if (Process.ifAllCompleted()) { // 如果所有进程完毕则退出循环
					break;
				}
			}
			System.out.println(processRemaining);
		}

	}*/
}
