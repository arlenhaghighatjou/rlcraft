package net.minecraft.world.storage;

import java.io.File;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import javax.annotation.Nullable;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;

public interface ISaveHandler
{
    @Nullable
    WorldInfo loadWorldInfo();

    void checkSessionLock() throws MinecraftException;

    IChunkLoader getChunkLoader(WorldProvider provider);

    void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound);

    void saveWorldInfo(WorldInfo worldInformation);

    IPlayerFileData getPlayerNBTManager();

    void flush();

    File getWorldDirectory();

    File getMapFileFromName(String mapName);

	@Nullable
	default NBTTagCompound readMapData(String name, boolean compressed) throws IOException
	{
		File file = this.getMapFileFromName(name);
		if (file == null || !file.exists()) return null;
		try (DataInputStream input = new DataInputStream(new FileInputStream(file)))
		{
			return compressed ? CompressedStreamTools.readCompressed(input) : CompressedStreamTools.read(input);
		}
	}

	default void writeMapData(String name, NBTTagCompound data, boolean compressed) throws IOException
	{
		File file = this.getMapFileFromName(name);
		if (file == null) return;
		try (DataOutputStream output = new DataOutputStream(new FileOutputStream(file)))
		{
			if (compressed) CompressedStreamTools.writeCompressed(data, output);
			else CompressedStreamTools.write(data, output);
		}
	}

    TemplateManager getStructureTemplateManager();
}
