package cursedflames.bountifulbaubles.wormhole;

import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;

public class PlayerTarget implements IWormholeTarget {
   public EaglercraftUUID id;
   public String name = "";
   public boolean enabled = true;

   public boolean isEnabled() {
      return this.enabled;
   }

   public void setEnabled(boolean enabled) {
      this.enabled = enabled;
   }

   public PlayerTarget(EntityPlayer player) {
      this.id = player.getUniqueID();
      this.name = player.getName();
   }

   public PlayerTarget(EaglercraftUUID id) {
      this.id = id;
   }

   public PlayerTarget() {
   }

   public String getName() {
      return this.name.length() > 0 ? this.name : this.id.toString();
   }

   public EntityPlayer getPlayer(World world) {
      for(EntityPlayer other : world.playerEntities) {
         if (other.getUniqueID().equals(this.id)) {
            return other;
         }
      }

      return null;
   }

   public boolean teleportPlayerTo(EntityPlayer player) {
      EntityPlayer other = this.getPlayer(player.world);
      if (other == null) {
         return false;
      } else {
         WormholeUtil.doTeleport(player, other);
         return true;
      }
   }

   public NBTTagCompound toNBT() {
      NBTTagCompound tag = new NBTTagCompound();
      tag.setString("type", "player");
      tag.setUniqueId("id", this.id);
      tag.setString("name", this.name);
      return tag;
   }

   public void fromNBT(NBTTagCompound tag) {
      if (tag.hasUniqueId("id")) {
         this.id = tag.getUniqueId("id");
         this.name = tag.getString("name");
      }
   }

   public boolean isEqual(IWormholeTarget other) {
      return other instanceof PlayerTarget && this.id.equals(((PlayerTarget)other).id);
   }
}
