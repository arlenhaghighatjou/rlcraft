package com.arlenh7.eaglercraft.forge;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.sp.server.WorldsDB;

public final class WorldTextFile
{
	private final VFile2 file;

	public WorldTextFile(VFile2 file)
	{
		this.file = file;
	}

	public boolean isFile()
	{
		return this.file.exists();
	}

	public WorldTextFile resolve(String path)
	{
		return new WorldTextFile(WorldsDB.newVFile(this.file, path));
	}

	public List<String> list(String suffix)
	{
		List<String> files = new ArrayList<>();
		String prefix = this.file.getPath() + "/";
		List<VFile2> children = this.file.listFiles(true);
		for (int i = 0, l = children.size(); i < l; ++i)
		{
			String path = children.get(i).getPath();
			if (path.startsWith(prefix) && path.endsWith(suffix)) files.add(path.substring(prefix.length()));
		}
		Collections.sort(files);
		return files;
	}

	public String read() throws IOException
	{
		String text = this.file.getAllChars();
		if (text == null) throw new IOException("Could not read " + this.file.getPath());
		return text;
	}

	public void write(String contents)
	{
		this.file.setAllChars(contents);
	}

	@Override
	public String toString()
	{
		return this.file.getPath();
	}
}
