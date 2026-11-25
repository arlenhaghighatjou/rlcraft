package xzeroair.trinkets.client.renderLayers;

import java.util.function.Consumer;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.interfaces.IAccessoryInterface;

@SideOnly(Side.CLIENT)
public class TrinketsRenderLayer implements LayerRenderer<EntityPlayer> {
   private boolean isSlim;
   private RenderPlayer renderer;

   public TrinketsRenderLayer(boolean slim, RenderPlayer render) {
      this.isSlim = slim;
      this.renderer = render;
   }

   public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!player.isInvisible() && player.getActivePotionEffect(MobEffects.INVISIBILITY) == null && TrinketsConfig.CLIENT.rendering) {
         Minecraft.getMinecraft().mcProfiler.startSection("Trinkets Render Layer");
         Minecraft.getMinecraft().mcProfiler.startSection("Race Render Layer");
         Capabilities.getEntityProperties(player, (prop) -> {
            GlStateManager.pushMatrix();
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            prop.onRender(this.renderer, this.isSlim, limbSwing, limbSwingAmount, partialTicks, ageInTicks, netHeadYaw, headPitch, scale);
            GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
            GlStateManager.popMatrix();
         });
         Minecraft.getMinecraft().mcProfiler.endSection();
         Minecraft.getMinecraft().mcProfiler.startSection("Item Render Layer");
         TrinketHelper.applyToAccessories(player, (stack) -> {
            if (stack.getItem() instanceof IAccessoryInterface) {
               IAccessoryInterface trinket = (IAccessoryInterface)stack.getItem();
               GlStateManager.pushMatrix();
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               trinket.playerRenderLayer(stack, player, this.renderer, this.isSlim, partialTicks, scale);
               GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
               GlStateManager.popMatrix();
            }

         });
         Minecraft.getMinecraft().mcProfiler.endSection();
         Minecraft.getMinecraft().mcProfiler.endSection();
      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
