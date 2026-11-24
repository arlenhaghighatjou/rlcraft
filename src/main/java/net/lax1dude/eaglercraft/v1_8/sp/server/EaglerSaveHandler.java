/*
 * Copyright (c) 2023-2024 lax1dude. All Rights Reserved.
 * 
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED.
 * IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR ANY DIRECT,
 * INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT
 * NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR
 * PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 * 
 */

package net.lax1dude.eaglercraft.v1_8.sp.server;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.LinkedHashSet;
import java.util.Set;

import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.MinecraftException;
import net.minecraft.world.WorldProvider;
import net.minecraft.world.chunk.storage.IChunkLoader;
import net.minecraft.world.gen.structure.template.TemplateManager;
import net.minecraft.world.storage.SaveHandler;
import net.minecraft.world.storage.WorldInfo;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraftforge.event.ForgeEventFactory;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.StartupQuery;

public class EaglerSaveHandler extends SaveHandler {

	private final VFile2 worldDirectory;
	private final VFile2 playersDirectory;
	private final long sessionToken;
	private final boolean ownsSession;
	private final TemplateManager templates;

	public EaglerSaveHandler(VFile2 savesDirectory, String directoryName, boolean ownsSession, DataFixer fixer) {
		super(new File(savesDirectory.getPath()), directoryName, ownsSession, fixer, false);
		this.worldDirectory = WorldsDB.newVFile(savesDirectory, directoryName);
		this.playersDirectory = WorldsDB.newVFile(this.worldDirectory, "playerdata");
		this.templates = new TemplateManager(WorldsDB.newVFile(this.worldDirectory, "structures"), fixer);
		this.ownsSession = ownsSession;
		this.sessionToken = EaglercraftUUID.randomUUID().lsb;
		if (ownsSession) {
			EaglerOutputStream bytes = new EaglerOutputStream();
			try (DataOutputStream output = new DataOutputStream(bytes)) {
				output.writeLong(this.sessionToken);
			} catch (IOException ex) {
				throw new IllegalStateException("Could not create world session lock", ex);
			}
			WorldsDB.newVFile(this.worldDirectory, "session.lock").setAllBytes(bytes.toByteArray());
		}
	}

	public IChunkLoader getChunkLoader(WorldProvider provider) {
		return new EaglerChunkLoader(WorldsDB.newVFile(this.worldDirectory, "level" + provider.getDimension()), this.dataFixer);
	}

	@Override
	public TemplateManager getStructureTemplateManager() {
		return this.templates;
	}

	public VFile2 getWorldPath() {
		return this.worldDirectory;
	}

	@Override
	public NBTTagCompound readMapData(String name, boolean compressed) throws IOException {
		return readMapFile(this.worldDirectory, name, compressed);
	}

	@Override
	public void writeMapData(String name, NBTTagCompound data, boolean compressed) throws IOException {
		writeMapFile(this.worldDirectory, name, data, compressed);
	}

	public NBTTagCompound readDimensionMapData(int dimension, String name, boolean compressed) throws IOException {
		VFile2 directory = WorldsDB.newVFile(this.worldDirectory, "level" + dimension);
		NBTTagCompound data = readMapFile(directory, name, compressed);
		if (data == null && ((dimension == -1 && name.equalsIgnoreCase("FORTRESS"))
				|| (dimension == 1 && name.equalsIgnoreCase("VILLAGES_END")))) {
			data = readMapData(name, compressed);
			if (data != null) writeMapFile(directory, name, data, compressed);
		}
		return data;
	}

	public void writeDimensionMapData(int dimension, String name, NBTTagCompound data, boolean compressed) throws IOException {
		writeMapFile(WorldsDB.newVFile(this.worldDirectory, "level" + dimension), name, data, compressed);
	}

	private static VFile2 mapFile(VFile2 directory, String name) throws IOException {
		if (name.isEmpty() || name.contains("..") || name.indexOf('/') >= 0 || name.indexOf('\\') >= 0) {
			throw new IOException("Invalid map data name " + name);
		}
		return WorldsDB.newVFile(directory, "data", name + ".dat");
	}

	private static NBTTagCompound readMapFile(VFile2 directory, String name, boolean compressed) throws IOException {
		VFile2 file = mapFile(directory, name);
		if (!file.exists()) return null;
		try (DataInputStream input = new DataInputStream(file.getInputStream())) {
			return compressed ? CompressedStreamTools.readCompressed(input) : CompressedStreamTools.read(input);
		}
	}

	private void writeMapFile(VFile2 directory, String name, NBTTagCompound data, boolean compressed) throws IOException {
		try {
			this.checkSessionLock();
		} catch (MinecraftException ex) {
			throw new IOException(ex.getMessage(), ex);
		}
		VFile2 file = mapFile(directory, name);
		EaglerOutputStream encoded = new EaglerOutputStream();
		if (compressed) {
			CompressedStreamTools.writeCompressed(data, encoded);
		} else {
			try (DataOutputStream output = new DataOutputStream(encoded)) {
				CompressedStreamTools.write(data, output);
			}
		}
		file.setAllBytes(encoded.toByteArray());
	}

	@Override
	public void checkSessionLock() throws MinecraftException {
		if (!ownsSession) throw new MinecraftException("This save handler does not own a write session");
		VFile2 lock = WorldsDB.newVFile(this.worldDirectory, "session.lock");
		if (!lock.exists()) throw new MinecraftException("World session lock is missing");
		try (DataInputStream input = new DataInputStream(lock.getInputStream())) {
			if (input.readLong() != sessionToken) throw new MinecraftException("The save is being accessed from another location, aborting");
		} catch (IOException ex) {
			throw new MinecraftException("Failed to read the world session lock");
		}
	}

	@Override
	public WorldInfo loadWorldInfo() {
		WorldInfo info = loadWorldInfo("level.dat");
		if (info != null) return info;
		VFile2 primary = WorldsDB.newVFile(this.worldDirectory, "level.dat");
		VFile2 backup = WorldsDB.newVFile(this.worldDirectory, "level.dat_old");
		if (backup.exists()) {
			FMLCommonHandler.instance().confirmBackupLevelDatUse(this);
			info = loadWorldInfo("level.dat_old");
			if (info != null) return info;
		}
		if (primary.exists() || backup.exists()) {
			throw new IllegalStateException("World metadata is unreadable for " + this.worldDirectory);
		}
		return null;
	}

	private WorldInfo loadWorldInfo(String fileName) {
		try {
			NBTTagCompound root = readNBT(WorldsDB.newVFile(this.worldDirectory, fileName));
			if (root == null || !root.hasKey("Data", 10)) return null;
			WorldInfo info = new WorldInfo(this.dataFixer.process(FixTypes.LEVEL, root.getCompoundTag("Data")));
			FMLCommonHandler.instance().handleWorldDataLoad(this, info, root);
			return info;
		} catch (StartupQuery.AbortedException ex) {
			throw ex;
		} catch (IOException | RuntimeException ex) {
			EaglerIntegratedServerWorker.logger.error("Could not read world metadata {}", fileName, ex);
			return null;
		}
	}

	@Override
	public void saveWorldInfoWithPlayer(WorldInfo worldInformation, NBTTagCompound tagCompound) {
		try {
			this.checkSessionLock();
			worldInformation.setSaveVersion(19133);
			NBTTagCompound root = new NBTTagCompound();
			root.setTag("Data", worldInformation.cloneNBTCompound(tagCompound));
			FMLCommonHandler.instance().handleWorldDataSave(this, worldInformation, root);
			VFile2 current = WorldsDB.newVFile(this.worldDirectory, "level.dat");
			VFile2 backup = WorldsDB.newVFile(this.worldDirectory, "level.dat_old");
			byte[] encoded = encode(root);
			if (current.exists() && VFile2.copyFile(current, backup) < 0) {
				throw new IOException("Could not back up level.dat");
			}
			current.setAllBytes(encoded);
		} catch (IOException | MinecraftException | RuntimeException ex) {
			EaglerIntegratedServerWorker.logger.error("Could not save world metadata for {}", this.worldDirectory, ex);
		}
	}

	@Override
	public void writePlayerData(EntityPlayer player) {
		try {
			this.checkSessionLock();
			String id = player.getCachedUniqueIdString();
			WorldsDB.newVFile(this.playersDirectory, id + ".dat").setAllBytes(encode(player.writeToNBT(new NBTTagCompound())));
			ForgeEventFactory.firePlayerSavingEvent(player, this.getPlayersDirectory(), id);
		} catch (IOException | MinecraftException | RuntimeException ex) {
			EaglerIntegratedServerWorker.logger.warn("Could not save player {}", player.getName(), ex);
		}
	}

	private NBTTagCompound findPlayerNBT(String id, String name) {
		try {
			String file = id + ".dat";
			NBTTagCompound compound = readNBT(WorldsDB.newVFile(this.playersDirectory, file));
			if (compound == null) compound = readNBT(WorldsDB.newVFile(this.worldDirectory, "players", file));
			if (compound == null) compound = readNBT(WorldsDB.newVFile(this.worldDirectory, "player", file));
			return compound;
		} catch (IOException | RuntimeException ex) {
			EaglerIntegratedServerWorker.logger.warn("Could not read player {}", name, ex);
			return null;
		}
	}

	@Override
	public NBTTagCompound readPlayerData(EntityPlayer player) {
		String id = player.getCachedUniqueIdString();
		NBTTagCompound compound = findPlayerNBT(id, player.getName());
		if (compound != null) player.readFromNBT(this.dataFixer.process(FixTypes.PLAYER, compound));
		ForgeEventFactory.firePlayerLoadingEvent(player, this.getPlayersDirectory(), id);
		return compound;
	}

	@Override
	public NBTTagCompound getPlayerNBT(EntityPlayerMP player) {
		return findPlayerNBT(player.getCachedUniqueIdString(), player.getName());
	}

	@Override
	public String[] getAvailablePlayerDat() {
		Set<String> players = new LinkedHashSet<>();
		for (String directory : new String[] { "playerdata", "players", "player" }) {
			VFile2 folder = WorldsDB.newVFile(this.worldDirectory, directory);
			String prefix = folder.getPath() + "/";
			for (VFile2 file : folder.listFiles(false)) {
				String name = file.getName();
				if (file.getPath().startsWith(prefix) && name.endsWith(".dat")) players.add(name.substring(0, name.length() - 4));
			}
		}
		return players.toArray(new String[players.size()]);
	}

	private static NBTTagCompound readNBT(VFile2 file) throws IOException {
		if (!file.exists()) return null;
		try (InputStream input = file.getInputStream()) {
			return CompressedStreamTools.readCompressed(input);
		}
	}

	private static byte[] encode(NBTTagCompound data) throws IOException {
		EaglerOutputStream buffer = new EaglerOutputStream();
		CompressedStreamTools.writeCompressed(data, buffer);
		return buffer.toByteArray();
	}
}
