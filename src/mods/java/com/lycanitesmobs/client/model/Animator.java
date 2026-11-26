package com.lycanitesmobs.client.model;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;


public class Animator {
   public Animator() {
   }

   public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
      GlStateManager.rotate(rotation, angleX, angleY, angleZ);
   }

   public void doRotate(float rotX, float rotY, float rotZ) {
      GlStateManager.rotate(rotX, 1.0F, 0.0F, 0.0F);
      GlStateManager.rotate(rotY, 0.0F, 1.0F, 0.0F);
      GlStateManager.rotate(rotZ, 0.0F, 0.0F, 1.0F);
   }

   public void doTranslate(float posX, float posY, float posZ) {
      GlStateManager.translate(posX, posY, posZ);
   }

   public void doScale(float scaleX, float scaleY, float scaleZ) {
      GlStateManager.scale(scaleX, scaleY, scaleZ);
   }
}
