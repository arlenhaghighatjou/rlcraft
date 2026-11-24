package com.arlenh7.eaglercraft.forge;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

public interface RegionStorage extends DataInput, DataOutput, Closeable
{
	long length() throws IOException;

	void seek(long position) throws IOException;
}
