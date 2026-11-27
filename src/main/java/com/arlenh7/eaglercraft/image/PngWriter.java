package com.arlenh7.eaglercraft.image;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import net.lax1dude.eaglercraft.v1_8.EaglerZLIB;

public class PngWriter {
	private static final int[] CRC_TABLE = new int[256];
	private final ByteArrayOutputStream out = new ByteArrayOutputStream();
	private final ByteArrayOutputStream data = new ByteArrayOutputStream();
	private final OutputStream deflater;
	private final int width;
	private final byte[] row;

	public PngWriter(int width, int height) throws IOException {
		this.width = width;
		this.row = new byte[1 + width * 4];
		this.out.write(new byte[]{-119, 80, 78, 71, 13, 10, 26, 10});
		byte[] header = new byte[13];
		writeInt(header, 0, width);
		writeInt(header, 4, height);
		header[8] = 8;
		header[9] = 6;
		this.writeChunk("IHDR", header);
		this.deflater = EaglerZLIB.newDeflaterOutputStream(this.data);
	}

	public void writeRow(int[] pixels, int offset) throws IOException {
		byte[] row = this.row;
		row[0] = 0;

		for(int x = 0; x < this.width; ++x) {
			int argb = pixels[offset + x];
			int i = 1 + x * 4;
			row[i] = (byte)(argb >>> 16);
			row[i + 1] = (byte)(argb >>> 8);
			row[i + 2] = (byte)argb;
			row[i + 3] = (byte)(argb >>> 24);
		}

		this.deflater.write(row);
	}

	public byte[] finish() throws IOException {
		this.deflater.close();
		this.writeChunk("IDAT", this.data.toByteArray());
		this.writeChunk("IEND", new byte[0]);
		return this.out.toByteArray();
	}

	private void writeChunk(String type, byte[] chunk) {
		byte[] length = new byte[4];
		writeInt(length, 0, chunk.length);
		this.out.write(length, 0, 4);
		byte[] name = new byte[4];

		for(int i = 0; i < 4; ++i) {
			name[i] = (byte)type.charAt(i);
		}

		this.out.write(name, 0, 4);
		this.out.write(chunk, 0, chunk.length);
		int crc = update(-1, name);
		crc = update(crc, chunk);
		byte[] check = new byte[4];
		writeInt(check, 0, ~crc);
		this.out.write(check, 0, 4);
	}

	private static int update(int crc, byte[] bytes) {
		for(int i = 0; i < bytes.length; ++i) {
			crc = CRC_TABLE[(crc ^ bytes[i]) & 255] ^ crc >>> 8;
		}

		return crc;
	}

	private static void writeInt(byte[] bytes, int offset, int value) {
		bytes[offset] = (byte)(value >>> 24);
		bytes[offset + 1] = (byte)(value >>> 16);
		bytes[offset + 2] = (byte)(value >>> 8);
		bytes[offset + 3] = (byte)value;
	}

	static {
		for(int n = 0; n < 256; ++n) {
			int c = n;

			for(int k = 0; k < 8; ++k) {
				c = (c & 1) != 0 ? -306674912 ^ c >>> 1 : c >>> 1;
			}

			CRC_TABLE[n] = c;
		}

	}
}
