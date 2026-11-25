package com.arlenh7.eaglercraft.ref;

public class WeakReference<T>
{
	private T referent;

	public WeakReference(T referent)
	{
		this.referent = referent;
	}

	public T get()
	{
		return this.referent;
	}

	public void clear()
	{
		this.referent = null;
	}
}
