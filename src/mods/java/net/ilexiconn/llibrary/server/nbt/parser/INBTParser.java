package net.ilexiconn.llibrary.server.nbt.parser;

import net.minecraft.nbt.NBTBase;

public interface INBTParser<V, T extends NBTBase> {
   V parseTag(T var1);

   T parseValue(V var1);
}
