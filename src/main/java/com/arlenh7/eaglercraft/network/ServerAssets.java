package com.arlenh7.eaglercraft.network;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.EaglerInputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;

public final class ServerAssets
{
	private static final String[] DIRECTORIES = { "/recipes/", "/advancements/", "/loot_tables/", "/structures/" };

	private ServerAssets()
	{
	}

	public static byte[] pack()
	{
		List<String> all = EagRuntime.listResources("assets/");
		List<String> paths = new ArrayList<>();
		for (int i = 0, l = all.size(); i < l; ++i)
		{
			String path = all.get(i);
			for (int j = 0; j < DIRECTORIES.length; ++j)
			{
				if (path.indexOf(DIRECTORIES[j]) > 0)
				{
					paths.add(path);
					break;
				}
			}
		}
		EaglerOutputStream bytes = new EaglerOutputStream();
		try (DataOutputStream output = new DataOutputStream(bytes))
		{
			output.writeInt(paths.size());
			for (int i = 0, l = paths.size(); i < l; ++i)
			{
				String path = paths.get(i);
				byte[] file = EagRuntime.getResourceBytes(path);
				output.writeUTF(path);
				output.writeInt(file.length);
				output.write(file);
			}
		}
		catch (IOException ex)
		{
			throw new IllegalStateException("Could not pack the server assets", ex);
		}
		return bytes.toByteArray();
	}

	public static void unpack(byte[] data) throws IOException
	{
		try (DataInputStream input = new DataInputStream(new EaglerInputStream(data)))
		{
			for (int i = 0, l = input.readInt(); i < l; ++i)
			{
				String path = input.readUTF();
				byte[] file = new byte[input.readInt()];
				input.readFully(file);
				EagRuntime.putResource(path, file);
			}
		}
	}
}
