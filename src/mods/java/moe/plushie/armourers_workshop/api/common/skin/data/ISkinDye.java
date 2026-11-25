package moe.plushie.armourers_workshop.api.common.skin.data;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;

public interface ISkinDye {
   byte[] getDyeColour(int var1);

   String getDyeName(int var1);

   boolean haveDyeInSlot(int var1);

   boolean hasName(int var1);

   void addDye(byte[] var1, String var2);

   void addDye(byte[] var1);

   void addDye(int var1, byte[] var2, String var3);

   void addDye(int var1, byte[] var2);

   void removeDye(int var1);

   int getNumberOfDyes();

   void writeToBuf(ByteBuf var1);

   void readFromBuf(ByteBuf var1);
}
