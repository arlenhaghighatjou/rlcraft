package com.arlenh7.eaglercraft.forge;

import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;

public final class ModAssets
{
	private ModAssets()
	{
	}

	public static List<String> prefixes()
	{
		List<String> ret = new ArrayList<>();
		ret.add("assets/");
		List<ModContainer> mods = Loader.instance().getActiveModList();
		for (int i = 0, l = mods.size(); i < l; ++i)
		{
			String prefix = mods.get(i).getAssetsPrefix();
			if (!ret.contains(prefix))
			{
				ret.add(prefix);
			}
		}
		return ret;
	}

	public static String find(String path)
	{
		List<String> prefixes = prefixes();
		for (int i = 0, l = prefixes.size(); i < l; ++i)
		{
			String full = prefixes.get(i) + path;
			if (EagRuntime.getResourceExists("/" + full))
			{
				return full;
			}
		}
		return null;
	}
}
