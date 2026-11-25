package invtweaks.api;

import net.minecraft.nbt.NBTTagCompound;

public interface IItemTreeItem extends Comparable<IItemTreeItem> {
   String getName();

   String getId();

   int getDamage();

   NBTTagCompound getExtraData();

   int getOrder();

   String getPath();
}
