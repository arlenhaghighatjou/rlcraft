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

import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import net.lax1dude.eaglercraft.v1_8.EaglerOutputStream;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.math.ChunkPos;
import net.minecraft.world.chunk.storage.AnvilChunkLoader;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;

public class EaglerChunkLoader extends AnvilChunkLoader {

	private static final String hex = "0123456789ABCDEF";

	public static String getChunkPath(int x, int z) {
		int unsignedX = x + 1900000;
		int unsignedZ = z + 1900000;
		
		char[] path = new char[12];
		for(int i = 5; i >= 0; --i) {
			path[i] = hex.charAt((unsignedX >>> (i << 2)) & 0xF);
			path[i + 6] = hex.charAt((unsignedZ >>> (i << 2)) & 0xF);
		}
		
		return new String(path);
	}

	public static ChunkPos getChunkCoords(String filename) {
		String strX = filename.substring(0, 6);
		String strZ = filename.substring(6);

		int retX = 0;
		int retZ = 0;

		for(int i = 0; i < 6; ++i) {
			retX |= hex.indexOf(strX.charAt(i)) << (i << 2);
			retZ |= hex.indexOf(strZ.charAt(i)) << (i << 2);
		}

		return new ChunkPos(retX - 1900000, retZ - 1900000);
	}

	public final VFile2 chunkDirectory;

	public EaglerChunkLoader(VFile2 chunkDirectory, DataFixer fixer) {
		super(new File(chunkDirectory.getPath()), fixer);
		this.chunkDirectory = chunkDirectory;
	}

	private VFile2 chunkFile(int x, int z) {
		return WorldsDB.newVFile(this.chunkDirectory, getChunkPath(x, z) + ".dat");
	}

	@Override
	protected boolean isStoredChunk(int x, int z) {
		return chunkFile(x, z).exists();
	}

	@Override
	protected NBTTagCompound readChunkData(int x, int z) throws IOException {
		VFile2 file = chunkFile(x, z);
		if (!file.exists()) return null;
		try (InputStream input = file.getInputStream()) {
			if (input == null) throw new IOException("Failed to open chunk " + x + ", " + z);
			NBTTagCompound data = CompressedStreamTools.readCompressed(input);
			++EaglerMinecraftServer.counterChunkRead;
			return data;
		}
	}

	@Override
	protected void writeChunkData(ChunkPos pos, NBTTagCompound data) throws IOException {
		EaglerOutputStream encoded = new EaglerOutputStream();
		CompressedStreamTools.writeCompressed(data, encoded);
		chunkFile(pos.x, pos.z).setAllBytes(encoded.toByteArray());
		++EaglerMinecraftServer.counterChunkWrite;
	}

}
