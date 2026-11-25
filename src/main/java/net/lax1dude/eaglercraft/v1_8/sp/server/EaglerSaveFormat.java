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

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.google.common.collect.Lists;

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;
import net.minecraft.world.storage.ISaveHandler;
import net.minecraft.world.storage.WorldSummary;
import net.minecraft.world.storage.SaveFormatOld;
import net.minecraft.world.storage.WorldInfo;

public class EaglerSaveFormat extends SaveFormatOld {

	private final VFile2 virtualSavesDirectory;
	private DataFixer worldFixer;

	public EaglerSaveFormat(VFile2 parFile) {
		super(new File(parFile.getPath()), null, false);
		this.virtualSavesDirectory = parFile;
	}

	private DataFixer fixer() {
		if (this.worldFixer == null) {
			this.worldFixer = DataFixesManager.createFixer();
		}
		return this.worldFixer;
	}

	public static final VFile2 worldsList = WorldsDB.newVFile("worlds_list.txt");
	public static final VFile2 worldsFolder = WorldsDB.newVFile("worlds");

	public String getName() {
		return "eagler";
	}

	public ISaveHandler getSaveLoader(String s, boolean flag) {
		validateFolder(s);
		return new EaglerSaveHandler(this.virtualSavesDirectory, s, flag, fixer());
	}

	public VFile2 getWorldPath(String folder) {
		validateFolder(folder);
		return WorldsDB.newVFile(this.virtualSavesDirectory, folder);
	}

	private static void validateFolder(String folder) {
		if (folder == null || folder.trim().isEmpty() || !folder.equals(folder.trim()) || folder.equals(".") || folder.contains("..")
				|| folder.indexOf('/') >= 0 || folder.indexOf('\\') >= 0 || folder.indexOf(':') >= 0) {
			throw new IllegalArgumentException("Invalid world folder " + folder);
		}
	}

	@Override
	public WorldInfo getWorldInfo(String folder) {
		VFile2 world = getWorldPath(folder);
		for (String name : new String[] { "level.dat", "level.dat_old" }) {
			VFile2 level = WorldsDB.newVFile(world, name);
			if (!level.exists()) continue;
			try (InputStream input = level.getInputStream()) {
				NBTTagCompound root = CompressedStreamTools.readCompressed(input);
				if (root.hasKey("Data", 10)) {
					return new WorldInfo(fixer().process(FixTypes.LEVEL, root.getCompoundTag("Data")));
				}
			} catch (IOException | RuntimeException ex) {
				EaglerIntegratedServerWorker.logger.warn("Could not read world metadata {}", level.getPath(), ex);
			}
		}
		return null;
	}

	@Override
	public boolean canLoadWorld(String folder) {
		return getWorldInfo(folder) != null;
	}

	@Override
	public boolean isNewLevelIdAcceptable(String folder) {
		return filesUnder(getWorldPath(folder)).isEmpty();
	}

	@Override
	public boolean deleteWorldDirectory(String folder) {
		return deleteVirtualFiles(filesUnder(getWorldPath(folder)), "singleplayer.busy.deleting");
	}

	private static List<VFile2> filesUnder(VFile2 directory) {
		String prefix = directory.getPath() + "/";
		List<VFile2> files = directory.listFiles(true);
		files.removeIf(file -> !file.getPath().startsWith(prefix));
		return files;
	}

	private static boolean deleteVirtualFiles(List<VFile2> files, String progressKey) {
		boolean success = true;
		for (int i = 0; i < files.size(); ++i) {
			VFile2 file = files.get(i);
			if (!file.delete() && file.exists()) success = false;
			if (progressKey != null && (i + 1) % 128 == 0) {
				EaglerIntegratedServerWorker.sendProgress(progressKey, (float)(i + 1) / files.size());
			}
		}
		return success;
	}

	@Override
	public void renameWorld(String folder, String displayName) {
		renameWorldChecked(folder, displayName);
	}

	public boolean renameWorldChecked(String folder, String displayName) {
		VFile2 world = getWorldPath(folder);
		VFile2 level = WorldsDB.newVFile(world, "level.dat");
		VFile2 source = level.exists() ? level : WorldsDB.newVFile(world, "level.dat_old");
		if (!source.exists()) return false;
		try {
			NBTTagCompound root;
			try (InputStream input = source.getInputStream()) {
				root = CompressedStreamTools.readCompressed(input);
			}
			if (!root.hasKey("Data", 10)) return false;
			root.getCompoundTag("Data").setString("LevelName", displayName);
			EaglerOutputStream encoded = new EaglerOutputStream();
			CompressedStreamTools.writeCompressed(root, encoded);
			level.setAllBytes(encoded.toByteArray());
			return true;
		} catch (IOException | RuntimeException ex) {
			EaglerIntegratedServerWorker.logger.error("Could not rename world {}", folder, ex);
			return false;
		}
	}

	public List<WorldSummary> getSaveList() {
		ArrayList<WorldSummary> arraylist = Lists.newArrayList();
		if(worldsList.exists()) {
			String[] lines = worldsList.getAllLines();
			for (int i = 0; i < lines.length; ++i) {
				String s = lines[i];
				WorldInfo worldinfo = this.getWorldInfo(s);
				if (worldinfo != null
						&& (worldinfo.getSaveVersion() == 19132 || worldinfo.getSaveVersion() == 19133)) {
					boolean flag = worldinfo.getSaveVersion() != this.getSaveVersion();
					String s1 = worldinfo.getWorldName();
					if (StringUtils.isEmpty(s1)) {
						s1 = s;
					}

					arraylist.add(new WorldSummary(worldinfo, s, s1, 0l, flag));
				}
			}
		}
		return arraylist;
	}

	public void clearPlayers(String worldFolder) {
		VFile2 world = getWorldPath(worldFolder);
		for (String directory : new String[] { "player", "players", "playerdata" }) {
			deleteVirtualFiles(filesUnder(WorldsDB.newVFile(world, directory)), null);
		}
	}

	protected int getSaveVersion() {
		return 19133; // why notch?
	}

	public boolean duplicateWorld(String worldFolder, String displayName) {
		String newFolderName = displayName.trim().replace('.', '_').replace('/', '_')
				.replace('\\', '_').replace('"', '_').replace(':', '_');
		if (newFolderName.isEmpty()) newFolderName = "World";
		VFile2 newFolder = getWorldPath(newFolderName);
		while (!filesUnder(newFolder).isEmpty()) {
			newFolderName += "_";
			newFolder = getWorldPath(newFolderName);
		}
		VFile2 oldFolder = getWorldPath(worldFolder);
		String oldPath = oldFolder.getPath();
		int totalSize = 0;
		int lastUpdate = 0;
		final VFile2 finalNewFolder = newFolder;
		List<VFile2> vfl = filesUnder(oldFolder);
		if (vfl.isEmpty()) return false;
		for(int i = 0, l = vfl.size(); i < l; ++i) {
			VFile2 vf = vfl.get(i);
			String fileNameRelative = vf.getPath().substring(oldPath.length() + 1);
			int copied = VFile2.copyFile(vf, WorldsDB.newVFile(finalNewFolder, fileNameRelative));
			if (copied < 0) return false;
			totalSize += copied;
			if (totalSize - lastUpdate > 10000) {
				lastUpdate = totalSize;
				EaglerIntegratedServerWorker.sendProgress("singleplayer.busy.duplicating", totalSize);
			}
		}
		if (!renameWorldChecked(newFolderName, displayName)) return false;
		String[] worldsTxt = worldsList.getAllLines();
		if(worldsTxt == null || worldsTxt.length <= 0) {
			worldsTxt = new String[] { newFolderName };
		}else {
			String[] tmp = worldsTxt;
			worldsTxt = new String[worldsTxt.length + 1];
			System.arraycopy(tmp, 0, worldsTxt, 0, tmp.length);
			worldsTxt[worldsTxt.length - 1] = newFolderName;
		}
		worldsList.setAllChars(String.join("\n", worldsTxt));
		return true;
	}
}
