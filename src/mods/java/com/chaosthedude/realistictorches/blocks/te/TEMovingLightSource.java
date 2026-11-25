package com.chaosthedude.realistictorches.blocks.te;

import com.chaosthedude.realistictorches.blocks.BlockMovingLightSource;
import com.chaosthedude.realistictorches.config.ConfigHandler;
import com.chaosthedude.realistictorches.handler.LightSourceHandler;
import java.util.UUID;
import javax.annotation.Nullable;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ITickable;

public class TEMovingLightSource extends TileEntity implements ITickable {
   public static final String NAME = "te_moving_light_source";
   private UUID playerUUID;

   public TEMovingLightSource() {
   }

   public void update() {
      if (this.world.getTotalWorldTime() % (long)ConfigHandler.handheldLightUpdateTicks == 0L && this.shouldKill() && this.world.getBlockState(this.pos).getBlock() instanceof BlockMovingLightSource) {
         this.world.setBlockToAir(this.pos);
         this.world.removeTileEntity(this.pos);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound tag) {
      super.writeToNBT(tag);
      if (this.playerUUID != null) {
         tag.setString("PlayerUUID", this.playerUUID.toString());
      }

      return tag;
   }

   public void readFromNBT(NBTTagCompound tag) {
      super.readFromNBT(tag);
      if (tag.hasKey("PlayerUUID")) {
         this.playerUUID = UUID.fromString(tag.getString("PlayerUUID"));
      }

   }

   public boolean shouldKill() {
      EntityPlayer player = this.findLightSourceCreator();
      return player == null || player.getDistance((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ()) > 2.0D || !LightSourceHandler.containsLightSource(player.getHeldEquipment());
   }

   public TEMovingLightSource setPlayer(EntityPlayer player) {
      if (player != null) {
         this.playerUUID = player.getGameProfile() != null ? player.getGameProfile().getId() : null;
      }

      return this;
   }

   @Nullable
   public EntityPlayer findLightSourceCreator() {
      return this.playerUUID != null ? this.world.getPlayerEntityByUUID(this.playerUUID) : this.world.getClosestPlayer((double)this.pos.getX(), (double)this.pos.getY(), (double)this.pos.getZ(), 2.0D, false);
   }
}
