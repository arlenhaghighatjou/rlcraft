package com.dhanantry.scapeandrunparasites.entity.ai.misc;

import net.minecraft.util.DamageSource;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class EntityPCosmical extends EntityPMalleable {
   protected int shakeee;
   protected boolean canAvoidD;
   protected int showC;

   public EntityPCosmical(World worldIn) {
      super(worldIn);
      this.type = 71;
      this.canAvoidD = true;
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.shakeee > 0) {
         --this.shakeee;
      }

      if (this.showC > -10) {
         --this.showC;
      }

      if (this.rand.nextInt(10) == 0 && this.showC <= -10) {
         this.showC = this.rand.nextInt(1) * 60;
      }

   }

   public boolean attackEntityFrom(DamageSource source, float amount) {
      if (this.world.isRemote) {
         return false;
      } else if (this.canAvoidD) {
         this.shakeee = 15;
         this.showC = 15;
         this.world.setEntityState(this, (byte)41);
         return false;
      } else {
         return super.attackEntityFrom(source, amount);
      }
   }

   public int shakingC() {
      return this.shakeee;
   }

   public int showC() {
      return this.showC;
   }

   @SideOnly(Side.CLIENT)
   public void handleStatusUpdate(byte id) {
      switch(id) {
      case 41:
         this.shakeee = 15;
         this.showC = 15;
         break;
      default:
         super.handleStatusUpdate(id);
      }

   }
}
