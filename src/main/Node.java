package main;

public class Node<T> // 链式队列节点类
{
	public Process data; // 数据域 存储数据元素
	public Node<Process> next; // 地址域，引用后继节点

	public Node(Process data, Node<Process> next) {
		super();
		this.data = data;
		this.next = next;
	}

	public Node() {
		super();
	}

	/*
	 * @see java.lang.Object#toString()
	 */
	@Override
	public String toString() {
		// TODO Auto-generated method stub
		return this.data.toString();
	}

}
