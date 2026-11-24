package com.arlenh7.eaglercraft.netty;

public final class Attribute<T> {

	private T value;

	public T get() {
		return value;
	}

	public void set(T value) {
		this.value = value;
	}

	public T getAndSet(T value) {
		T old = this.value;
		this.value = value;
		return old;
	}

	public T setIfAbsent(T value) {
		T old = this.value;
		if(old == null) {
			this.value = value;
		}
		return old;
	}

}
