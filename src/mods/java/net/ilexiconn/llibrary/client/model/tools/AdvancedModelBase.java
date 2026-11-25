package net.ilexiconn.llibrary.client.model.tools;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraft.client.model.ModelBase;
import net.minecraft.util.math.MathHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class AdvancedModelBase extends ModelBase {
   private float movementScale = 1.0F;

   public AdvancedModelBase() {
   }

   public void updateDefaultPose() {
      this.boxList.stream().filter((modelRenderer) -> {
         return modelRenderer instanceof AdvancedModelRenderer;
      }).forEach((modelRenderer) -> {
         AdvancedModelRenderer advancedModelRenderer = (AdvancedModelRenderer)modelRenderer;
         advancedModelRenderer.updateDefaultPose();
      });
   }

   public void resetToDefaultPose() {
      this.boxList.stream().filter((modelRenderer) -> {
         return modelRenderer instanceof AdvancedModelRenderer;
      }).forEach((modelRenderer) -> {
         AdvancedModelRenderer advancedModelRenderer = (AdvancedModelRenderer)modelRenderer;
         advancedModelRenderer.resetToDefaultPose();
      });
   }

   public void faceTarget(float yaw, float pitch, float rotationDivisor, AdvancedModelRenderer... boxes) {
      float actualRotationDivisor = rotationDivisor * (float)boxes.length;
      float yawAmount = yaw / 57.295776F / actualRotationDivisor;
      float pitchAmount = pitch / 57.295776F / actualRotationDivisor;

      for(AdvancedModelRenderer box : boxes) {
         box.rotateAngleY += yawAmount;
         box.rotateAngleX += pitchAmount;
      }

   }

   public void chainSwing(AdvancedModelRenderer[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
      float offset = this.calculateChainOffset(rootOffset, boxes);

      for(int index = 0; index < boxes.length; ++index) {
         boxes[index].rotateAngleY += this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
      }

   }

   public void chainWave(AdvancedModelRenderer[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
      float offset = this.calculateChainOffset(rootOffset, boxes);

      for(int index = 0; index < boxes.length; ++index) {
         boxes[index].rotateAngleX += this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
      }

   }

   public void chainFlap(AdvancedModelRenderer[] boxes, float speed, float degree, double rootOffset, float swing, float swingAmount) {
      float offset = this.calculateChainOffset(rootOffset, boxes);

      for(int index = 0; index < boxes.length; ++index) {
         boxes[index].rotateAngleZ += this.calculateChainRotation(speed, degree, swing, swingAmount, offset, index);
      }

   }

   private float calculateChainRotation(float speed, float degree, float swing, float swingAmount, float offset, int boxIndex) {
      return MathHelper.cos(swing * speed * this.movementScale + offset * (float)boxIndex) * swingAmount * degree * this.movementScale;
   }

   private float calculateChainOffset(double rootOffset, AdvancedModelRenderer... boxes) {
      return (float)(rootOffset * 3.141592653589793D / (double)(2 * boxes.length));
   }

   public float getMovementScale() {
      return this.movementScale;
   }

   public void setMovementScale(float movementScale) {
      this.movementScale = movementScale;
   }

   public void walk(AdvancedModelRenderer box, float speed, float degree, boolean invert, float offset, float weight, float walk, float walkAmount) {
      box.walk(speed, degree, invert, offset, weight, walk, walkAmount);
   }

   public void flap(AdvancedModelRenderer box, float speed, float degree, boolean invert, float offset, float weight, float flap, float flapAmount) {
      box.flap(speed, degree, invert, offset, weight, flap, flapAmount);
   }

   public void swing(AdvancedModelRenderer box, float speed, float degree, boolean invert, float offset, float weight, float swing, float swingAmount) {
      box.swing(speed, degree, invert, offset, weight, swing, swingAmount);
   }

   public void bob(AdvancedModelRenderer box, float speed, float degree, boolean bounce, float f, float f1) {
      box.bob(speed, degree, bounce, f, f1);
   }

   public float moveBox(float speed, float degree, boolean bounce, float f, float f1) {
      return bounce ? -MathHelper.abs(MathHelper.sin(f * speed) * f1 * degree) : MathHelper.sin(f * speed) * f1 * degree - f1 * degree;
   }
}
