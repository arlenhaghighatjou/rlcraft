package cursedflames.bountifulbaubles.wormhole;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public interface IWormholeTarget {
   String getName();

   boolean teleportPlayerTo(EntityPlayer var1);

   NBTTagCompound toNBT();

   void fromNBT(NBTTagCompound var1);

   boolean isEqual(IWormholeTarget var1);

   boolean isEnabled();

   void setEnabled(boolean var1);
}
