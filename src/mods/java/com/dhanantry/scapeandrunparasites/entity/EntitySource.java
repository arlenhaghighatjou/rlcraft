package com.dhanantry.scapeandrunparasites.entity;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.BossInfoServer;
import net.minecraft.world.World;
import net.minecraft.world.BossInfo.Color;
import net.minecraft.world.BossInfo.Overlay;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntitySource extends Entity {
   private final BossInfoServer bossInfo = (BossInfoServer)(new BossInfoServer(this.getDisplayName(), Color.RED, Overlay.PROGRESS)).setDarkenSky(false);
   private byte type;
   private float total;
   private float charging;

   public EntitySource(World parent) {
      super(parent);
      this.setSize(0.5F, 0.5F);
      this.total = 100.0F;
   }

   public void onUpdate() {
      super.onUpdate();
      this.bossInfo.setPercent(this.charging / this.total);
      if (this.ticksExisted % 20 == 0 && !this.world.isRemote) {
         ++this.charging;
         if (this.charging > this.total) {
            this.attack();
         }
      }

   }

   private void attack() {
      if (this.charging > 200.0F) {
         this.setDead();
      }

   }

   public void setCustomNameTag(String name) {
      super.setCustomNameTag(name);
      this.bossInfo.setName(this.getDisplayName());
   }

   public void addTrackingPlayer(EntityPlayerMP player) {
      super.addTrackingPlayer(player);
      this.bossInfo.addPlayer(player);
   }

   public void removeTrackingPlayer(EntityPlayerMP player) {
      super.removeTrackingPlayer(player);
      this.bossInfo.removePlayer(player);
   }

   public boolean canBeCollidedWith() {
      return false;
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString("The Source");
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
   }

   protected void entityInit() {
   }

   public boolean isEntityEqual(Entity entityIn) {
      return this == entityIn;
   }

   @SideOnly(Side.CLIENT)
   public void setPositionAndRotationDirect(double x, double y, double z, float yaw, float pitch, int posRotationIncrements, boolean teleport) {
   }
}
