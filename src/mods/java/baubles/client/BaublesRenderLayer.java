package baubles.client;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble;
import baubles.common.Config;
import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;

public final class BaublesRenderLayer implements LayerRenderer<EntityPlayer> {
   public BaublesRenderLayer() {
   }

   public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (Config.renderBaubles && player.getActivePotionEffect(MobEffects.INVISIBILITY) == null) {
         IBaublesItemHandler inv = BaublesApi.getBaublesHandler(player);
         this.dispatchRenders(inv, player, IRenderBauble.RenderType.BODY, partialTicks);
         float yaw = player.prevRotationYawHead + (player.rotationYawHead - player.prevRotationYawHead) * partialTicks;
         float yawOffset = player.prevRenderYawOffset + (player.renderYawOffset - player.prevRenderYawOffset) * partialTicks;
         float pitch = player.prevRotationPitch + (player.rotationPitch - player.prevRotationPitch) * partialTicks;
         GlStateManager.pushMatrix();
         GlStateManager.rotate(yawOffset, 0.0F, -1.0F, 0.0F);
         GlStateManager.rotate(yaw - 270.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.rotate(pitch, 0.0F, 0.0F, 1.0F);
         this.dispatchRenders(inv, player, IRenderBauble.RenderType.HEAD, partialTicks);
         GlStateManager.popMatrix();
      }
   }

   private void dispatchRenders(IBaublesItemHandler inv, EntityPlayer player, IRenderBauble.RenderType type, float partialTicks) {
      for(int i = 0; i < inv.getSlots(); ++i) {
         ItemStack stack = inv.getStackInSlot(i);
         if (stack != null && !stack.isEmpty()) {
            Item item = stack.getItem();
            if (item instanceof IRenderBauble) {
               GlStateManager.pushMatrix();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               ((IRenderBauble)stack.getItem()).onPlayerBaubleRender(stack, player, type, partialTicks);
               GlStateManager.popMatrix();
            }
         }
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
