package de.Whitedraco.switchbow.entity;

import de.Whitedraco.switchbow.Initial;
import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;

public class EntityFire extends Entity {
   int livetime = 100;
   private List Mobliste;
   private double aoe = 0.9D;

   public EntityFire(World worldIn) {
      super(worldIn);
      this.setSize(0.0F, 0.0F);
   }

   public EntityFire(World worldIn, double posX, double posY, double posZ) {
      super(worldIn);
      this.setSize(0.0F, 0.0F);
      this.setPosition(posX, posY, posZ);
   }

   protected void entityInit() {
   }

   public void onUpdate() {
      super.onEntityUpdate();
      if (this.livetime <= 0) {
         this.setDead();
      }

      this.Mobliste();
      if (this.Mobliste != null && this.Mobliste.size() > 0) {
         for(int a = 0; a < this.Mobliste.size(); ++a) {
            EntityLivingBase Mob = (EntityLivingBase)this.Mobliste.get(a);
            Mob.setFire(10);
         }
      }

      if (this.world.isRemote) {
         for(int x = 0; x < 45; x += 5) {
            Initial.proxy.SpawnParticle(this.world, this.posX + this.motionX, this.posY + this.motionY, this.posZ + this.motionZ, -this.motionX, -this.motionY + 0.2D, -this.motionZ, this.posX, this.posZ, x);
         }
      }

      --this.livetime;
   }

   private void Mobliste() {
      this.Mobliste = this.world.getEntitiesWithinAABB(EntityLivingBase.class, new AxisAlignedBB(this.posX - this.aoe, this.posY - 1.0D, this.posZ - this.aoe, this.posX + this.aoe, this.posY + 2.0D, this.posZ + this.aoe));
   }

   protected void readEntityFromNBT(NBTTagCompound compound) {
      this.livetime = compound.getInteger("livetime");
   }

   protected void writeEntityToNBT(NBTTagCompound compound) {
      compound.setInteger("livetime", this.livetime);
   }
}
