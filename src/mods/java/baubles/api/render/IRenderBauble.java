package baubles.api.render;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.MathHelper;

public interface IRenderBauble {
   void onPlayerBaubleRender(ItemStack var1, EntityPlayer var2, IRenderBauble.RenderType var3, float var4);

   public static final class Helper {
      public Helper() {
      }

      public static void rotateIfSneaking(EntityPlayer player) {
         if (player.isSneaking()) {
            applySneakingRotation();
         }

      }

      public static void applySneakingRotation() {
         GlStateManager.translate(0.0F, 0.2F, 0.0F);
         GlStateManager.rotate(28.647888F, 1.0F, 0.0F, 0.0F);
      }

      public static void translateToHeadLevel(EntityPlayer player) {
         GlStateManager.translate(0.0F, -player.getDefaultEyeHeight(), 0.0F);
         if (player.isSneaking()) {
            GlStateManager.translate(0.25F * MathHelper.sin(player.rotationPitch * 3.1415927F / 180.0F), 0.25F * MathHelper.cos(player.rotationPitch * 3.1415927F / 180.0F), 0.0F);
         }

      }

      public static void translateToFace() {
         GlStateManager.rotate(90.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -4.35F, -1.27F);
      }

      public static void defaultTransforms() {
         GlStateManager.translate(0.0D, 3.0D, 1.0D);
         GlStateManager.scale(0.55D, 0.55D, 0.55D);
      }

      public static void translateToChest() {
         GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         GlStateManager.translate(0.0F, -3.2F, -0.85F);
      }
   }

   public static enum RenderType {
      BODY,
      HEAD;

      private RenderType() {
      }
   }
}
