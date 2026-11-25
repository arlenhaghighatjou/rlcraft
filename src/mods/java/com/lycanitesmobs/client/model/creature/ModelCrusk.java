package com.lycanitesmobs.client.model.creature;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.client.model.ModelObjOld;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ModelCrusk extends ModelObjOld {
   public ModelCrusk() {
      this(1.0F);
   }

   public ModelCrusk(float shadowSize) {
      this.initModel("Crusk", LycanitesMobs.modInfo, "entity/crusk");
      this.setPartCenter("head", 0.0F, 0.7F, 2.4F);
      this.setPartCenter("topleftmouth", 0.0F, 0.7F, 2.4F);
      this.setPartCenter("toprightmouth", 0.0F, 0.7F, 2.4F);
      this.setPartCenter("bottomleftmouth", 0.0F, 0.7F, 2.4F);
      this.setPartCenter("bottomrightmouth", 0.0F, 0.7F, 2.4F);
      this.setPartCenter("body", 0.0F, 0.7F, 1.8F);
      this.setPartCenter("body01", 0.0F, 0.7F, 1.2F);
      this.setPartCenter("body02", 0.0F, 0.7F, 0.6F);
      this.setPartCenter("body03", 0.0F, 0.7F, 0.0F);
      this.setPartCenter("body04", 0.0F, 0.7F, -0.6F);
      this.setPartCenter("body05", 0.0F, 0.7F, -1.2F);
      this.setPartCenter("body06", 0.0F, 0.7F, -1.8F);
      this.setPartCenter("body07", 0.0F, 0.7F, -2.4F);
      this.setPartCenter("body08", 0.0F, 0.7F, -3.0F);
      this.setPartCenter("body09", 0.0F, 0.7F, -3.6F);
      this.lockHeadX = true;
      this.lockHeadY = true;
      this.trophyScale = 0.6F;
      this.trophyOffset = new float[]{0.0F, 0.0F, -0.4F};
   }

   public void animatePart(String partName, EntityLiving entity, float time, float distance, float loop, float lookY, float lookX, float scale) {
      super.animatePart(partName, entity, time, distance, loop, lookY, lookX, scale);
      float pi = 3.1415927F;
      float posX = 0.0F;
      float posY = 0.0F;
      float posZ = 0.0F;
      float angleX = 0.0F;
      float angleY = 0.0F;
      float angleZ = 0.0F;
      float rotation = 0.0F;
      float rotX = 0.0F;
      float rotY = 0.0F;
      float rotZ = 0.0F;
      float scaleX = 1.0F;
      float scaleY = 1.0F;
      float scaleZ = 1.0F;
      if (scale >= 0.0F) {
         if (partName.equals("topleftmouth")) {
            this.translate(0.3F, 0.3F, 0.7F);
            angleX = -0.5F;
            angleY = 0.5F;
         }

         if (partName.equals("toprightmouth")) {
            this.translate(-0.3F, 0.3F, 0.7F);
            angleX = -0.5F;
            angleY = -0.5F;
         }

         if (partName.equals("bottomleftmouth")) {
            this.translate(0.3F, -0.3F, 0.7F);
            angleX = 0.5F;
            angleY = 0.5F;
         }

         if (partName.equals("bottomrightmouth")) {
            this.translate(-0.3F, -0.3F, 0.7F);
            angleX = 0.5F;
            angleY = -0.5F;
         }

         if (partName.equals("topleftmouth") || partName.equals("toprightmouth") || partName.equals("bottomleftmouth") || partName.equals("bottomrightmouth")) {
            rotation = (float)((double)rotation + -Math.toDegrees((double)(MathHelper.cos(loop * 0.09F) * 0.05F - 0.05F)));
            if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).isAttackOnCooldown()) {
               rotation = -20.0F;
            }

            if (entity instanceof TameableCreatureEntity && ((TameableCreatureEntity)entity).isSitting()) {
               rotation += 20.0F;
            }

            this.rotate(rotation, angleX, angleY, angleZ);
            rotation = 0.0F;
         }

         if (partName.equals("topleftmouth")) {
            this.translate(-0.3F, -0.3F, -0.7F);
         }

         if (partName.equals("toprightmouth")) {
            this.translate(0.3F, -0.3F, -0.7F);
         }

         if (partName.equals("bottomleftmouth")) {
            this.translate(-0.3F, 0.3F, -0.7F);
         }

         if (partName.equals("bottomrightmouth")) {
            this.translate(0.3F, 0.3F, -0.7F);
         }

         float walkSwing = 0.8F;
         if (entity instanceof BaseCreatureEntity && ((BaseCreatureEntity)entity).getStealth() > 0.0F && ((BaseCreatureEntity)entity).getStealth() < 1.0F) {
            time = loop;
         }

         time = time / 2.0F;
         if (partName.equals("head") || partName.equals("topleftmouth") || partName.equals("toprightmouth") || partName.equals("bottomleftmouth") || partName.equals("bottomrightmouth")) {
            posX += MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
         }

         if (partName.equals("body")) {
            posX += MathHelper.sin(time * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time - walkSwing) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body01")) {
            posX += MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin(time * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body02")) {
            posX += MathHelper.sin((time + walkSwing * 2.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body03")) {
            posX += MathHelper.sin((time + walkSwing * 3.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 2.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body04")) {
            posX += MathHelper.sin((time + walkSwing * 4.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 3.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body05")) {
            posX += MathHelper.sin((time + walkSwing * 5.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 4.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body06")) {
            posX += MathHelper.sin((time + walkSwing * 6.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 5.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body07")) {
            posX += MathHelper.sin((time + walkSwing * 7.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 6.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body08")) {
            posX += MathHelper.sin((time + walkSwing * 8.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 7.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (partName.equals("body09")) {
            posX += MathHelper.sin((time + walkSwing * 9.0F) * walkSwing) * walkSwing;
            float parentX = MathHelper.sin((time + walkSwing * 8.0F) * walkSwing) * walkSwing;
            rotY = (float)((double)rotY + this.rotateToPoint(0.0D, (double)posX, -0.6000000238418579D, (double)parentX));
         }

         if (entity instanceof BaseCreatureEntity) {
            posY -= 2.0F * ((BaseCreatureEntity)entity).getStealth();
         }

         this.translate(posX, posY, posZ);
         this.rotate(rotation, angleX, angleY, angleZ);
         this.rotate(rotX, rotY, rotZ);
         this.scale(scaleX, scaleY, scaleZ);
      }
   }
}
