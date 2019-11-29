package main;

public interface Queue<T> {

	public abstract boolean isEmpty();

	public abstract boolean add(T x);// 添加成功返回true

	public abstract T peek(); // 返回队头元素，若为空返回null

	public abstract T poll(); // 出队，并返回队头元素，若为空返回null

}
