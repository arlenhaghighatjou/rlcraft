package me.ichun.mods.ichunutil.client.entity.head;

import me.ichun.mods.ichunutil.api.client.head.HeadBase;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.monster.EntityShulker;

public class HeadShulker extends HeadBase<EntityShulker> {
   public HeadShulker() {
      this.eyeOffset = new float[]{0.0F, -0.21875F, 0.1875F};
      this.halfInterpupillaryDistance = 0.125F;
      this.eyeScale = 0.6F;
   }

   public float getEyeScale(EntityShulker living, float partialTick, int eye) {
      return living.getClientPeekAmount(partialTick) <= 0.0F ? 0.0F : this.eyeScale;
   }

   public float[] getHeadJointOffset(EntityShulker living, float partialTick, int eye) {
      switch(living.getAttachmentFacing()) {
      case DOWN:
      default:
         break;
      case EAST:
         GlStateManager.rotate(90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(1.0F, -1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case WEST:
         GlStateManager.rotate(-90.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(-1.0F, -1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         break;
      case NORTH:
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0F, -1.0F);
         break;
      case SOUTH:
         GlStateManager.rotate(180.0F, 0.0F, 0.0F, 1.0F);
         GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -1.0F, 1.0F);
         break;
      case UP:
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -2.0F, 0.0F);
      }

      return super.getHeadJointOffset(living, partialTick, eye);
   }

   public float getHeadPitch(EntityShulker living, float partialTick, int eye) {
      return 0.0F;
   }
}
