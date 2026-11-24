package net.minecraft.server.management;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.net.SocketAddress;

import com.google.gson.JsonObject;

public class UserListIPBans extends UserList<String, UserListIPBansEntry>
{
	public UserListIPBans(VFile2 file) { super(file); }
	protected UserListEntry<String> createEntry(JsonObject json) { return new UserListIPBansEntry(json); }
	public boolean isBanned(SocketAddress address) { return hasEntry(addressToString(address.toString())); }
	public boolean isBanned(String address) { return hasEntry(addressToString(address)); }
	public UserListIPBansEntry getBanEntry(SocketAddress address) { return getEntry(addressToString(address.toString())); }
	public UserListIPBansEntry getBanEntry(String address) { return getEntry(addressToString(address)); }

	private String addressToString(String address)
	{
		int slash = address.indexOf('/');
		if (slash >= 0) address = address.substring(slash + 1);
		int colon = address.indexOf(':');
		return colon >= 0 ? address.substring(0, colon) : address;
	}
}
