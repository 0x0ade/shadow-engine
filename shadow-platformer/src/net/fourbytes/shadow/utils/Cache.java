package net.fourbytes.shadow.utils;

public final class Cache<T> {

	private Class<T> clazz;
	private T[] cache;
	private int pos;
	private Object[] args;
	private Class[] types;

	public Cache(Class<T> clazz) {
		this(clazz, 8);
	}

	public Cache(Class<T> clazz, int amount) {
		this(clazz, amount, new Object[0]);
	}

	public Cache(Class<T> clazz, Object[] args) {
		this(clazz, 8, args);
	}

	public Cache(Class<T> clazz, int amount, Object[] args) {
		this.clazz = clazz;

		cache = (T[]) new Object[amount];
		this.args = args;

		types = new Class[args.length];
		for (int i = 0; i < args.length; i++) {
			types[i] = args[i].getClass();
		}
	}

	public Cache<T> previous() {
		return move(1);
	}

	public Cache<T> next() {
		return move(-1);
	}

	public Cache<T> move(int i) {
		return position(pos + i);
	}

	public Cache<T> position(int i) {
		if (i <= 0) {
			pos = (cache.length-i)%cache.length;
		} else {
			pos = i%cache.length;
		}
		return this;
	}

	public T getPrevious() {
		return cache[previous().fill().pos];
	}

	public T getNext() {
		return cache[next().fill().pos];
	}

	public T getPosition(int i) {
		return cache[position(i).fill().pos];
	}

	public T get() {
		return cache[fill().pos];
	}

	public Cache<T> fill() {
		if (cache[pos] == null) {
			try {
				cache[pos] = clazz.getConstructor(types).newInstance(args);
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return this;
	}

	public Cache<T> set(T obj) {
		cache[pos] = obj;
		return this;
	}

}
