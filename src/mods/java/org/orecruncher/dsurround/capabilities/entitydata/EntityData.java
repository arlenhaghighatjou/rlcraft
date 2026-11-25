package org.orecruncher.dsurround.capabilities.entitydata;

import javax.annotation.Nonnull;
import net.minecraft.entity.EntityLiving;
import net.minecraft.nbt.NBTTagCompound;
import org.orecruncher.dsurround.network.Network;
import org.orecruncher.dsurround.network.PacketEntityData;

public final class EntityData implements IEntityDataSettable {
   public static final int NO_ENTITY = -1;
   private final EntityLiving entity;
   private boolean isAttacking;
   private boolean isFleeing;
   private boolean sync;

   public EntityData() {
      this.entity = null;
   }

   public EntityData(@Nonnull EntityLiving entity) {
      this.entity = entity;
   }

   public EntityLiving getEntity() {
      return this.entity;
   }

   public int getEntityId() {
      return this.entity != null ? this.entity.getEntityId() : -1;
   }

   public boolean isAttacking() {
      return this.isAttacking;
   }

   public void setAttacking(boolean flag) {
      this.sync |= this.isAttacking != flag;
      this.isAttacking = flag;
   }

   public boolean isFleeing() {
      return this.isFleeing;
   }

   public void setFleeing(boolean flag) {
      this.sync |= this.isFleeing != flag;
      this.isFleeing = flag;
   }

   public boolean needsSync() {
      return this.sync;
   }

   private void clearSync() {
      this.sync = false;
   }

   public void sync() {
      if (this.needsSync() && this.entity != null && !this.entity.getEntityWorld().isRemote) {
         Network.sendToEntityViewers(this.entity, new PacketEntityData(this));
         this.clearSync();
      }

   }

   @Nonnull
   public NBTTagCompound serializeNBT() {
      NBTTagCompound nbt = new NBTTagCompound();
      nbt.setBoolean("a", this.isAttacking());
      nbt.setBoolean("f", this.isFleeing());
      return nbt;
   }

   public void deserializeNBT(@Nonnull NBTTagCompound nbt) {
      this.setAttacking(nbt.getBoolean("a"));
      this.setFleeing(nbt.getBoolean("f"));
   }

   private static class NBT {
      public static final String ATTACKING = "a";
      public static final String FLEEING = "f";

      private NBT() {
      }
   }
}
