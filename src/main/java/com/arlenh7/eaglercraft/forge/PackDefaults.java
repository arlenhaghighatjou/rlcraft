package com.arlenh7.eaglercraft.forge;

import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;

public final class PackDefaults
{
	private static final String ROOT = "pack-defaults/";

	private PackDefaults()
	{
	}

	public static void install()
	{
		List<String> files = EagRuntime.listResources(ROOT);
		for (int i = 0, l = files.size(); i < l; ++i)
		{
			String path = files.get(i);
			VFile2 target = new VFile2(path.substring(ROOT.length()));
			if (!target.exists())
			{
				target.setAllBytes(EagRuntime.getRequiredResourceBytes(path));
			}
		}
	}
}
