package cursedflames.bountifulbaubles.wormhole;

import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;

public class DebugTarget implements IWormholeTarget {
   public String name = "";
   public boolean enabled = true;

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public DebugTarget(String name) {
      this.name = name;
   }

   public DebugTarget() {
   }

   public String getName() {
      return this.name;
   }

   public boolean teleportPlayerTo(EntityPlayer player) {
      return false;
   }

   public NBTTagCompound toNBT() {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("type", "debug");
      tag.setString("name", this.name);
      return tag;
   }

   public void fromNBT(NBTTagCompound tag) {
      this.name = tag.getString("name");
   }

   public boolean isEqual(IWormholeTarget other) {
      return other instanceof DebugTarget && this.name.equals(((DebugTarget)other).name);
   }
}
