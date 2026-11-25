package ivorius.reccomplex.block;

import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface TileEntityWithGUI {
   void writeSyncedNBT(NBTTagCompound var1);

   void readSyncedNBT(NBTTagCompound var1);

   @SideOnly(Side.CLIENT)
   void openEditGUI();
}
