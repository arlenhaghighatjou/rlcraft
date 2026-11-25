package net.silentchaos512.scalinghealth.entity;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.registry.IEntityAdditionalSpawnData;

public class EntityBlightFire extends Entity implements IEntityAdditionalSpawnData {
   private static final String NBT_PARENT = "ParentBlight";
   private EntityLivingBase parent;

   public EntityBlightFire(World worldIn) {
      super(worldIn);
   }

   public EntityBlightFire(EntityLivingBase parent) {
      super(parent.world);
      this.parent = parent;
   }

   public void onUpdate() {
      if (this.world.isRemote || this.parent != null && !this.parent.isDead) {
         if (this.parent instanceof EntityPlayer) {
            this.setDead();
         }

         if (this.parent != null) {
            this.posX = this.parent.posX;
            this.posY = this.parent.posY + (double)this.parent.height / 1.5D;
            this.posZ = this.parent.posZ;
         }

      } else {
         this.setDead();
      }
   }

   protected void entityInit() {
   }

   public int getBrightnessForRender() {
      return 15728880;
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      if (compound.hasKey("ParentBlight")) {
         int id = compound.getInteger("ParentBlight");
         Entity entity = this.world.getEntityByID(id);
         if (entity instanceof EntityLivingBase) {
            this.parent = (EntityLivingBase)entity;
         }
      }

   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      if (this.parent != null) {
         compound.setInteger("ParentBlight", this.parent.getEntityId());
      }

   }

   public EntityLivingBase getParent() {
      return this.parent;
   }

   public void writeSpawnData(ByteBuf buffer) {
      buffer.writeInt(this.parent == null ? -1 : this.parent.getEntityId());
   }

   public void readSpawnData(ByteBuf additionalData) {
      int id = additionalData.readInt();
      if (id != -1) {
         Entity entity = this.world.getEntityByID(id);
         if (entity instanceof EntityLivingBase) {
            this.parent = (EntityLivingBase)entity;
         }
      }

   }
}
