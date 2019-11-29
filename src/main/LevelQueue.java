package main;

public class LevelQueue<T> implements Queue<Process> {
	protected Node<Process> front, rear;// front 和rear 分别指向队头和队尾节点
	protected int levelOfQueue; // 队列等级 1,2,3
	protected int timeSlice; // 队列时间片 1,2,4
	protected int account = 0; // 队列中排队的进程数

	public LevelQueue(int levelOfQueue, int timeSlice) { // 构造队列
		super();
		this.levelOfQueue = levelOfQueue;
		this.timeSlice = timeSlice;
	}

	@Override
	public boolean isEmpty() { // 判断队列是否为空 若为空返回true
		// TODO Auto-generated method stub
		if (this.front == null && this.rear == null) {
			System.out.println(this.levelOfQueue + "级队列当前没有进程正在运行");
			return true;
		} else {
			System.out.println(this.levelOfQueue + "级队列当前有" + this.account + "个进程正在运行");
			return false;
		}

	}

	@Override
	public boolean add(Process x) // 入队操作
	{
		// TODO Auto-generated method stub

		if (x == null) {
			return false; // 元素x 入队 空对象不能入队
		}
		// 进程到达cpu
		Node<Process> q = new Node<Process>(x, null); // 局部变量q接收数据内容。
		if (this.front == null) {
			this.front = q; // 空队插入
			account++;
		} else {
			this.rear.next = q; // 队列尾插入
			account++;
		}
		this.rear = q; // 尾指针位置更新
		return true; // 插入成功

	}

	@Override // 返回队头操作
	public Process peek() {
		// TODO Auto-generated method stub
		// 只返回队头元素，不进行任何操作，若队列为空则返回null;
		return this.isEmpty() ? null : this.front.data;
	}

	@Override
	public Process poll() { // 出队操作 返回队头元素，若队列为空返回 null
		// TODO Auto-generated method stub
		if (isEmpty()) {
			return null;
		}
		Process x = this.front.data; // 取得队头元素
		this.front = this.front.next; // 删除队头元素
		if (this.front == null) {
			this.rear = null;
		}
		account--;
		return x;
	}

}
