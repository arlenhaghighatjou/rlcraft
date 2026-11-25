package com.arlenh7.eaglercraft.forge;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;

import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.lax1dude.eaglercraft.v1_8.minecraft.EaglerFolderResourcePack;
import net.minecraft.client.settings.GameSettings;

public final class PackDefaults
{
	private static final Logger logger = LogManager.getLogger("PackDefaults");
	private static final String ROOT = "pack-defaults/";
	private static final String PACKS = ROOT + "resourcepacks/";

	private PackDefaults()
	{
	}

	public static void install()
	{
		List<String> files = EagRuntime.listResources(ROOT);
		for (int i = 0, l = files.size(); i < l; ++i)
		{
			String path = files.get(i);
			if (path.startsWith(PACKS) || path.startsWith(ROOT + "options"))
			{
				continue;
			}
			VFile2 target = new VFile2(path.substring(ROOT.length()));
			if (!target.exists())
			{
				target.setAllBytes(EagRuntime.getRequiredResourceBytes(path));
			}
		}
	}

	public static void installResourcePacks(GameSettings settings)
	{
		if (!EaglerFolderResourcePack.isSupported() || new VFile2(EaglerFolderResourcePack.RESOURCE_PACKS, "manifest.json").exists())
		{
			return;
		}
		List<String> files = EagRuntime.listResources(PACKS);
		if (files.isEmpty())
		{
			return;
		}
		String options = EagRuntime.getResourceString(ROOT + "options.txt");
		JSONArray enabled = new JSONArray(options.substring(options.indexOf("resourcePacks:") + 14, options.indexOf('\n', options.indexOf("resourcePacks:"))).trim());
		String[] names = new String[enabled.length()];
		for (int i = 0, l = files.size(); i < l; ++i)
		{
			String path = files.get(i);
			String file = path.substring(PACKS.length());
			try
			{
				EaglerFolderResourcePack pack = EaglerFolderResourcePack.importResourcePack(file, EaglerFolderResourcePack.RESOURCE_PACKS, EagRuntime.getRequiredResourceBytes(path));
				for (int j = 0; j < names.length; ++j)
				{
					if (file.equals(enabled.getString(j)))
					{
						names[j] = pack.getPackName();
					}
				}
			}
			catch (IOException ex)
			{
				logger.error("Could not import resource pack {}", file);
				logger.error(ex);
			}
		}
		for (int i = 0; i < names.length; ++i)
		{
			if (names[i] != null)
			{
				settings.resourcePacks.add(names[i]);
			}
		}
	}
}
