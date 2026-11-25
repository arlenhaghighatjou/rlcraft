package com.lycanitesmobs.client.model;

import org.lwjgl.opengl.GL11;

public class Animator {
   public Animator() {
   }

   public void doAngle(float rotation, float angleX, float angleY, float angleZ) {
      GL11.glRotatef(rotation, angleX, angleY, angleZ);
   }

   public void doRotate(float rotX, float rotY, float rotZ) {
      GL11.glRotatef(rotX, 1.0F, 0.0F, 0.0F);
      GL11.glRotatef(rotY, 0.0F, 1.0F, 0.0F);
      GL11.glRotatef(rotZ, 0.0F, 0.0F, 1.0F);
   }

   public void doTranslate(float posX, float posY, float posZ) {
      GL11.glTranslatef(posX, posY, posZ);
   }

   public void doScale(float scaleX, float scaleY, float scaleZ) {
      GL11.glScalef(scaleX, scaleY, scaleZ);
   }
}
