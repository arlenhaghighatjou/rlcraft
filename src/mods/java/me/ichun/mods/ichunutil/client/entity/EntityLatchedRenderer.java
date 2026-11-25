package me.ichun.mods.ichunutil.client.entity;

import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.module.worldportals.client.render.WorldPortalRenderer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.Entity;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class EntityLatchedRenderer extends Entity {
   public Entity latchedEnt;
   public long lastUpdate;
   public int maxDeathPersistTime;
   public int currentDeathPersistTime;

   public EntityLatchedRenderer(World par1World) {
      super(par1World);
      this.setSize(0.1F, 0.1F);
      this.lastUpdate = (long)iChunUtil.eventHandlerClient.ticks;
      this.maxDeathPersistTime = this.currentDeathPersistTime = 0;
      this.setInvisible(true);
   }

   public EntityLatchedRenderer(World par1World, Entity ent) {
      super(par1World);
      this.setSize(ent.width * 0.75F, ent.height * 0.75F);
      this.latchedEnt = ent;
      this.setLocationAndAngles(this.latchedEnt.posX, this.latchedEnt.posY, this.latchedEnt.posZ, this.latchedEnt.rotationYaw, this.latchedEnt.rotationPitch);
      this.lastUpdate = (long)iChunUtil.eventHandlerClient.ticks;
      this.setInvisible(true);
   }

   public EntityLatchedRenderer setIgnoreFrustumCheck() {
      this.ignoreFrustumCheck = true;
      return this;
   }

   public EntityLatchedRenderer setRenderSize(float width, float height) {
      if (!this.ignoreFrustumCheck) {
         if (width > this.width && height > this.height) {
            this.setSize(width, height);
         } else if (width > this.width) {
            this.setSize(width, this.height);
         } else if (height > this.height) {
            this.setSize(this.width, height);
         }
      }

      return this;
   }

   public EntityLatchedRenderer setDeathPersistTime(int ticks) {
      if (ticks > this.maxDeathPersistTime) {
         this.maxDeathPersistTime = ticks;
      }

      return this;
   }

   @SideOnly(Side.CLIENT)
   public boolean isInRangeToRenderDist(double distance) {
      return this.latchedEnt != null && this.latchedEnt.isInRangeToRenderDist(distance);
   }

   public void updatePos() {
      if (this.latchedEnt != null) {
         if (this.latchedEnt.isEntityAlive()) {
            this.lastTickPosX = this.latchedEnt.lastTickPosX;
            this.lastTickPosY = this.latchedEnt.lastTickPosY;
            this.lastTickPosZ = this.latchedEnt.lastTickPosZ;
            this.prevPosX = this.latchedEnt.prevPosX;
            this.prevPosY = this.latchedEnt.prevPosY;
            this.prevPosZ = this.latchedEnt.prevPosZ;
            this.posX = this.latchedEnt.posX;
            this.posY = this.latchedEnt.posY;
            this.posZ = this.latchedEnt.posZ;
         } else {
            this.lastTickPosX = this.prevPosX = this.posX = this.latchedEnt.posX;
            this.lastTickPosY = this.prevPosY = this.posY = this.latchedEnt.posY;
            this.lastTickPosZ = this.prevPosZ = this.posZ = this.latchedEnt.posZ;
         }

         this.setPosition(this.posX, this.posY, this.posZ);
      }

   }

   public void onUpdate() {
      if (this.latchedEnt != null) {
         ++this.ticksExisted;
         if (!this.latchedEnt.isEntityAlive()) {
            ++this.currentDeathPersistTime;
            if (this.currentDeathPersistTime > this.maxDeathPersistTime) {
               this.setDead();
               return;
            }
         }

         MinecraftForge.EVENT_BUS.post(new EntityLatchedRenderer.EntityLatchedRendererUpdateEvent(this));
         this.lastUpdate = (long)iChunUtil.eventHandlerClient.ticks;
      }
   }

   public boolean shouldRenderInPass(int pass) {
      return pass == 1;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return WorldPortalRenderer.renderLevel >= 1 && this.latchedEnt == Minecraft.getMinecraft().getRenderViewEntity() ? super.getBrightnessForRender() : this.latchedEnt.getBrightnessForRender();
   }

   public void entityInit() {
   }

   public boolean writeToNBTOptional(NBTTagCompound par1NBTTagCompound) {
      return false;
   }

   public void readEntityFromNBT(NBTTagCompound nbttagcompound) {
   }

   public void writeEntityToNBT(NBTTagCompound nbttagcompound) {
   }

   public class EntityLatchedRendererUpdateEvent extends EntityEvent {
      public final EntityLatchedRenderer ent;

      public EntityLatchedRendererUpdateEvent(EntityLatchedRenderer ent) {
         super(ent);
         this.ent = ent;
      }
   }
}
