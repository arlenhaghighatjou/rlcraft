package com.arlenh7.eaglercraft.forge;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.EaglerIntegratedServerWorker;
import net.minecraft.server.MinecraftServer;

public final class WorldBackup
{
	private WorldBackup()
	{
	}

	public static File destination(String world)
	{
		VFile2 folder = EaglerIntegratedServerWorker.saveFormat.getWorldPath(world);
		String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss.SSS", Locale.ROOT).format(new Date());
		return new File("world-backups", folder.getName() + "-" + timestamp + ".zip");
	}

	public static void backupActiveWorld() throws IOException
	{
		MinecraftServer server = EaglerIntegratedServerWorker.getServer();
		if (server == null) throw new IOException("There is no integrated world to back up");
		String folder = server.getFolderName();
		backup(folder, destination(folder));
	}

	public static void backup(String world, File destination) throws IOException
	{
		VFile2 folder = EaglerIntegratedServerWorker.saveFormat.getWorldPath(world);
		String prefix = folder.getPath() + "/";
		Path target = destination.toPath().toAbsolutePath();
		Files.createDirectories(target.getParent());
		Path temporary = Files.createTempFile(target.getParent(), "world-backup-", ".tmp");
		try
		{
			int count = 0;
			try (ZipOutputStream zip = new ZipOutputStream(Files.newOutputStream(temporary)))
			{
				for (VFile2 file : folder.listFiles(true))
				{
					if (!file.getPath().startsWith(prefix)) continue;
					String relative = file.getPath().substring(prefix.length());
					if (relative.isEmpty() || file.isRelative()) throw new IOException("Invalid world file " + relative);
					try (InputStream input = file.getInputStream())
					{
						zip.putNextEntry(new ZipEntry(relative));
						input.transferTo(zip);
						zip.closeEntry();
					}
					++count;
				}
			}
			if (count == 0) throw new IOException("World " + world + " has no files to back up");
			Files.move(temporary, target);
		}
		finally
		{
			Files.deleteIfExists(temporary);
		}
	}
}
