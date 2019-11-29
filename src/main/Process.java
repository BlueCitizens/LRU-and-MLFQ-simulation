package main;

public class Process {

		static int amountOfProcess = 0; // 所有进程总数

		protected String name; // 进程名
		protected int arrivalTime; // 进程到达内存的时间
		protected int serviceTime; // 进程完成需要占用cpu的服务时间
		protected int excutedTime = 0; // 进程已经执行时间
		protected LevelQueue<Process> queue = null; // 当前进程所在队列

		public Process(String name, int arrivalTime, int serviceTime) {
			super();
			this.name = name;
			this.arrivalTime = arrivalTime;
			this.serviceTime = serviceTime;
			amountOfProcess += 1;// 每创建一个进程对象，进程总数+1
		}

		public void Finished() { // 进程结束
			// 执行出队操作
			queue.poll();
			System.out.println("-------------------");
			System.out.println("进程：" + this.name + "执行完毕,结束时位于第" + this.queue.levelOfQueue + "队列。");
			System.out.println(this.toString());
			amountOfProcess -= 1;// 每完成一个进程对象，进程总数-1
		}

		@Override
		public String toString() {
			// TODO Auto-generated method stub
			if (queue != null) {
				return "进程：" + this.name + ",到达时间:" + this.arrivalTime + ",服务时间" + this.serviceTime + ",进程已经执行时间:"
						+ this.excutedTime + ",进程当前所在队列" + queue.levelOfQueue + "。";
			} else {
				return "进程：" + this.name + ",到达时间:" + this.arrivalTime + ",服务时间" + this.serviceTime;
			}
		}

		public static boolean ifAllCompleted() {
			if (amountOfProcess == 0) {
				System.out.println("当前没有进程正在运行");
				return true;
			} else {
				System.out.println("当前有" + amountOfProcess + "个进程等待运行。");
				return false;
			}

		}


}
