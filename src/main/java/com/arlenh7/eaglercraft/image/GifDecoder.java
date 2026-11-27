package com.arlenh7.eaglercraft.image;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;

public class GifDecoder {

	private final byte[] data;
	private int pos;

	private GifDecoder(byte[] data) {
		this.data = data;
	}

	public static List<ImageData> readFrames(byte[] data) throws IOException {
		return (new GifDecoder(data)).decode();
	}

	private int readByte() throws IOException {
		if (pos >= data.length) {
			throw new IOException("Unexpected end of GIF data");
		}
		return data[pos++] & 0xFF;
	}

	private int readShort() throws IOException {
		int lo = readByte();
		return lo | (readByte() << 8);
	}

	private int[] readColorTable(int size) throws IOException {
		int[] table = new int[256];
		for (int i = 0; i < size; ++i) {
			int r = readByte();
			int g = readByte();
			int b = readByte();
			table[i] = 0xFF000000 | (r << 16) | (g << 8) | b;
		}
		return table;
	}

	private void skipSubBlocks() throws IOException {
		int len;
		while ((len = readByte()) != 0) {
			pos += len;
		}
	}

	private byte[] readSubBlocks() throws IOException {
		int start = pos;
		int total = 0;
		int len;
		while ((len = readByte()) != 0) {
			total += len;
			pos += len;
		}
		byte[] out = new byte[total];
		int p = start;
		int o = 0;
		while ((len = data[p++] & 0xFF) != 0) {
			System.arraycopy(data, p, out, o, len);
			o += len;
			p += len;
		}
		return out;
	}

	private List<ImageData> decode() throws IOException {
		if (data.length < 13 || data[0] != 'G' || data[1] != 'I' || data[2] != 'F') {
			throw new IOException("Not a GIF file");
		}
		pos = 6;
		readShort();
		readShort();
		int packed = readByte();
		readByte();
		readByte();
		int[] globalTable = null;
		if ((packed & 0x80) != 0) {
			globalTable = readColorTable(2 << (packed & 7));
		}
		List<ImageData> frames = new ArrayList<>();
		int transparentIndex = -1;
		while (pos < data.length) {
			int block = readByte();
			if (block == 0x21) {
				int label = readByte();
				if (label == 0xF9) {
					int size = readByte();
					int start = pos;
					int flags = readByte();
					readShort();
					int index = readByte();
					transparentIndex = (flags & 1) != 0 ? index : -1;
					pos = start + size;
					skipSubBlocks();
				} else {
					skipSubBlocks();
				}
			} else if (block == 0x2C) {
				readShort();
				readShort();
				int width = readShort();
				int height = readShort();
				int flags = readByte();
				int[] table = globalTable;
				if ((flags & 0x80) != 0) {
					table = readColorTable(2 << (flags & 7));
				}
				if (table == null) {
					throw new IOException("GIF frame has no color table");
				}
				int minCodeSize = readByte();
				byte[] lzw = readSubBlocks();
				byte[] indices = decodeLZW(lzw, minCodeSize, width * height);
				int[] pixels = new int[width * height];
				boolean interlaced = (flags & 0x40) != 0;
				int src = 0;
				if (interlaced) {
					for (int pass = 0; pass < 4; ++pass) {
						int startRow = pass == 0 ? 0 : (pass == 1 ? 4 : (pass == 2 ? 2 : 1));
						int step = pass == 0 ? 8 : (pass == 1 ? 8 : (pass == 2 ? 4 : 2));
						for (int y = startRow; y < height; y += step) {
							for (int x = 0; x < width; ++x) {
								pixels[y * width + x] = colorOf(table, indices[src++] & 0xFF, transparentIndex);
							}
						}
					}
				} else {
					for (int i = 0; i < pixels.length; ++i) {
						pixels[i] = colorOf(table, indices[i] & 0xFF, transparentIndex);
					}
				}
				frames.add(new ImageData(width, height, pixels, true));
				transparentIndex = -1;
			} else if (block == 0x3B) {
				break;
			} else {
				throw new IOException("Unknown GIF block: " + block);
			}
		}
		return frames;
	}

	private static int colorOf(int[] table, int index, int transparentIndex) {
		return index == transparentIndex ? 0 : table[index];
	}

	private static byte[] decodeLZW(byte[] in, int minCodeSize, int pixelCount) {
		byte[] out = new byte[pixelCount];
		short[] prefix = new short[4096];
		byte[] suffix = new byte[4096];
		byte[] stack = new byte[4097];
		int clear = 1 << minCodeSize;
		int end = clear + 1;
		int codeSize = minCodeSize + 1;
		int codeMask = (1 << codeSize) - 1;
		int available = clear + 2;
		int oldCode = -1;
		int first = 0;
		for (int i = 0; i < clear; ++i) {
			prefix[i] = 0;
			suffix[i] = (byte) i;
		}
		int bits = 0;
		int datum = 0;
		int inPos = 0;
		int outPos = 0;
		int top = 0;
		while (outPos < pixelCount) {
			if (top == 0) {
				if (bits < codeSize) {
					if (inPos >= in.length) {
						break;
					}
					datum |= (in[inPos++] & 0xFF) << bits;
					bits += 8;
					continue;
				}
				int code = datum & codeMask;
				datum >>>= codeSize;
				bits -= codeSize;
				if (code > available || code == end) {
					break;
				}
				if (code == clear) {
					codeSize = minCodeSize + 1;
					codeMask = (1 << codeSize) - 1;
					available = clear + 2;
					oldCode = -1;
					continue;
				}
				if (oldCode == -1) {
					stack[top++] = suffix[code];
					oldCode = code;
					first = code;
					continue;
				}
				int inCode = code;
				if (code == available) {
					stack[top++] = (byte) first;
					code = oldCode;
				}
				while (code > clear) {
					stack[top++] = suffix[code];
					code = prefix[code];
				}
				first = suffix[code] & 0xFF;
				if (available >= 4096) {
					break;
				}
				stack[top++] = (byte) first;
				prefix[available] = (short) oldCode;
				suffix[available] = (byte) first;
				++available;
				if ((available & codeMask) == 0 && available < 4096) {
					++codeSize;
					codeMask += available;
				}
				oldCode = inCode;
			}
			--top;
			out[outPos++] = stack[top];
		}
		return out;
	}
}
