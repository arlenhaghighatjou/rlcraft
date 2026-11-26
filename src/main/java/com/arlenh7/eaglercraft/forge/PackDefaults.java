package com.arlenh7.eaglercraft.forge;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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
		Set<String> listed = new HashSet<>();
		Set<String> existing = new HashSet<>();
		for (int i = 0, l = files.size(); i < l; ++i)
		{
			String path = files.get(i);
			int slash = path.indexOf('/', ROOT.length());
			if (slash == -1 || path.startsWith(PACKS))
			{
				continue;
			}
			String target = path.substring(ROOT.length());
			String directory = target.substring(0, slash - ROOT.length());
			if (listed.add(directory))
			{
				existing.addAll(new VFile2(directory).listFilenames(true));
			}
			if (!existing.contains(target))
			{
				new VFile2(target).setAllBytes(EagRuntime.getRequiredResourceBytes(path));
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
		JSONArray enabled = new JSONArray();
		List<String> options = EagRuntime.getResourceLines(ROOT + "options.txt");
		for (int i = 0, l = options.size(); i < l; ++i)
		{
			String line = options.get(i);
			if (line.startsWith("resourcePacks:"))
			{
				enabled = new JSONArray(line.substring(14));
				break;
			}
		}
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
