package dev.satyrn.wolfarmor.compatibility.dogslie.model;

import dev.satyrn.wolfarmor.client.model.ModelWolfBackpack;
import javax.annotation.Nonnull;
import me.ichun.letsleepingdogslie.common.LetSleepingDogsLie;
import me.ichun.letsleepingdogslie.common.core.TickHandlerClient.WolfInfo;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.passive.EntityWolf;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DogsLieWolfBackpackModel extends ModelWolfBackpack {
   public DogsLieWolfBackpackModel(float scale) {
      super(scale);
   }

   public void setLivingAnimations(@Nonnull EntityLivingBase entity, float limbSwing, float limbSwingAmount, float partialTickTime) {
      super.setLivingAnimations(entity, limbSwing, limbSwingAmount, partialTickTime);
      if (entity instanceof EntityWolf) {
         EntityWolf wolf = (EntityWolf)entity;
         if (wolf.isSitting()) {
            WolfInfo wolfInfo = LetSleepingDogsLie.tickHandlerClient.getWolfInfo(wolf);
            if (wolfInfo.isLying()) {
               this.backpackLeftTop.rotateAngleX = this.backpackLeftBottom.rotateAngleX = 0.0F;
               this.backpackRightTop.rotateAngleX = this.backpackRightBottom.rotateAngleX = 0.0F;
               this.backpackRightTop.setRotationPoint(0.0F, 20.9F, 2.0F);
               this.backpackRightBottom.setRotationPoint(0.0F, 20.9F, 2.0F);
               this.backpackLeftTop.setRotationPoint(0.0F, 20.9F, 2.0F);
               this.backpackLeftBottom.setRotationPoint(0.0F, 20.9F, 2.0F);
               if ("iChun".equals(wolf.getName())) {
                  this.backpackLeftBottom.rotateAngleY = this.backpackLeftTop.rotateAngleY = ((float)wolf.ticksExisted + partialTickTime) / 3.5F;
                  this.backpackLeftBottom.rotateAngleZ = this.backpackLeftTop.rotateAngleZ = ((float)wolf.ticksExisted + partialTickTime) / 3.5F;
                  this.backpackRightBottom.rotateAngleY = this.backpackRightTop.rotateAngleY = ((float)wolf.ticksExisted + partialTickTime) / 3.5F;
                  this.backpackRightBottom.rotateAngleZ = this.backpackRightTop.rotateAngleZ = ((float)wolf.ticksExisted + partialTickTime) / 3.5F;
               }

               String[] poses = wolfInfo.getCompatiblePoses(wolf);
               String var8 = poses[1];
               byte var9 = -1;
               switch(var8.hashCode()) {
               case 2057096158:
                  if (var8.equals("hindlegSideL")) {
                     var9 = 0;
                  }
                  break;
               case 2057096164:
                  if (var8.equals("hindlegSideR")) {
                     var9 = 1;
                  }
               }

               switch(var9) {
               case 0:
                  this.backpackRightTop.rotateAngleZ = this.backpackRightBottom.rotateAngleZ = -0.610865F;
                  this.backpackLeftTop.rotateAngleZ = this.backpackLeftBottom.rotateAngleZ = -0.610865F;
                  break;
               case 1:
                  this.backpackRightTop.rotateAngleZ = this.backpackRightBottom.rotateAngleZ = 0.610865F;
                  this.backpackLeftTop.rotateAngleZ = this.backpackLeftBottom.rotateAngleZ = 0.610865F;
               }
            }
         }
      }

   }
}
