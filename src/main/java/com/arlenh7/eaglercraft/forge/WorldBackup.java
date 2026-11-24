package com.arlenh7.eaglercraft.forge;

import java.io.IOException;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerIntegratedServerWorker;
import net.lax1dude.eaglercraft.v1_8.sp.server.export.WorldConverterEPK;
import net.minecraft.server.MinecraftServer;

public final class WorldBackup
{
	private WorldBackup()
	{
	}

	public static VFile2 destination(String world)
	{
		return new VFile2("world-backups", world + "-" + System.currentTimeMillis() + ".epk");
	}

	public static void backupActiveWorld() throws IOException
	{
		MinecraftServer server = EaglerIntegratedServerWorker.getServer();
		if (server == null) throw new IOException("There is no integrated world to back up");
		String folder = server.getFolderName();
		backup(folder, destination(folder));
	}

	public static void backup(String world, VFile2 destination) throws IOException
	{
		destination.setAllBytes(WorldConverterEPK.exportWorld(world));
	}
}
