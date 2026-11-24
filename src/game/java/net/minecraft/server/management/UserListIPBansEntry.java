package net.minecraft.server.management;

import java.util.Date;

import com.google.gson.JsonObject;

public class UserListIPBansEntry extends UserListEntryBan<String>
{
	public UserListIPBansEntry(String value) { this(value, null, null, null, null); }
	public UserListIPBansEntry(String value, Date start, String banner, Date end, String reason) { super(value, start, banner, end, reason); }
	public UserListIPBansEntry(JsonObject json) { super(json.has("ip") ? json.get("ip").getAsString() : null, json); }
	protected void onSerialization(JsonObject data)
	{
		if (getValue() != null) { data.addProperty("ip", getValue()); super.onSerialization(data); }
	}
}
