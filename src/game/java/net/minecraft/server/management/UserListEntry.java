package net.minecraft.server.management;

import com.google.gson.JsonObject;

public class UserListEntry<T>
{
	private final T value;

	public UserListEntry(T value)
	{
		this.value = value;
	}

	protected UserListEntry(T value, JsonObject json)
	{
		this.value = value;
	}

	T getValue()
	{
		return value;
	}

	boolean hasBanExpired()
	{
		return false;
	}

	protected void onSerialization(JsonObject data)
	{
	}
}
