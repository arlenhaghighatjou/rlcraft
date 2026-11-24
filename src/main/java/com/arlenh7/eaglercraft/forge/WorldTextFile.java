package com.arlenh7.eaglercraft.forge;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Stream;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;

public final class WorldTextFile
{
	private final File diskFile;
	private final VFile2 virtualFile;

	public WorldTextFile(File file)
	{
		this.diskFile = file;
		this.virtualFile = null;
	}

	public WorldTextFile(VFile2 file)
	{
		this.diskFile = null;
		this.virtualFile = file;
	}

	public boolean isFile()
	{
		return this.virtualFile != null ? this.virtualFile.exists() : this.diskFile.isFile();
	}

	public WorldTextFile resolve(String path)
	{
		return this.virtualFile != null ? new WorldTextFile(WorldsDB.newVFile(this.virtualFile, path))
				: new WorldTextFile(new File(this.diskFile, path));
	}

	public List<String> list(String suffix) throws IOException
	{
		List<String> files = new ArrayList<>();
		if (this.virtualFile != null)
		{
			String prefix = this.virtualFile.getPath() + "/";
			for (VFile2 file : this.virtualFile.listFiles(true))
			{
				String path = file.getPath();
				if (path.startsWith(prefix) && path.endsWith(suffix)) files.add(path.substring(prefix.length()));
			}
		}
		else if (this.diskFile.isDirectory())
		{
			Path root = this.diskFile.toPath();
			try (Stream<Path> paths = Files.walk(root))
			{
				paths.filter(path -> Files.isRegularFile(path) && path.toString().endsWith(suffix))
						.forEach(path -> files.add(root.relativize(path).toString().replace(File.separatorChar, '/')));
			}
		}
		files.sort(String::compareTo);
		return files;
	}

	public String read() throws IOException
	{
		if (this.virtualFile == null) return Files.readString(this.diskFile.toPath(), StandardCharsets.UTF_8);
		String text = this.virtualFile.getAllChars();
		if (text == null) throw new IOException("Could not read " + this.virtualFile.getPath());
		return text;
	}

	public void write(String contents) throws IOException
	{
		if (this.virtualFile != null)
		{
			this.virtualFile.setAllChars(contents);
		}
		else
		{
			Path path = this.diskFile.toPath();
			if (path.getParent() != null) Files.createDirectories(path.getParent());
			Files.writeString(path, contents, StandardCharsets.UTF_8);
		}
	}

	@Override
	public String toString()
	{
		return this.virtualFile != null ? this.virtualFile.getPath() : this.diskFile.getPath();
	}
}
