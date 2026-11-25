package cursedflames.bountifulbaubles.client.layer;

import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import baubles.api.render.IRenderBauble.RenderType;
import javax.annotation.Nonnull;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import org.lwjgl.opengl.GL11;

public class BountfulRenderLayer implements LayerRenderer<EntityPlayer> {
   private boolean isSlim;
   private RenderPlayer renderer;

   public BountfulRenderLayer(boolean slim, RenderPlayer render) {
      this.isSlim = slim;
      this.renderer = render;
   }

   public void doRenderLayer(@Nonnull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      if (!player.isInvisible() && player.getActivePotionEffect(MobEffects.INVISIBILITY) == null) {
         if (Loader.isModLoaded("baubles")) {
            IBaublesItemHandler baubles = BaublesApi.getBaublesHandler(player);

            for(int i = 0; i < baubles.getSlots(); ++i) {
               ItemStack stack = baubles.getStackInSlot(i);
               if (stack.getItem() instanceof IRenderObject) {
                  IRenderObject renderObj = (IRenderObject)stack.getItem();
                  GlStateManager.pushMatrix();
                  GL11.glColor3ub((byte)-1, (byte)-1, (byte)-1);
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  if (player.isSneaking()) {
                     GlStateManager.translate(0.0D, 0.2D, 0.0D);
                  }

                  this.renderType(renderObj.getRenderType(), scale);
                  renderObj.onRenderObject(stack, player, this.isSlim, partialTicks, scale);
                  GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
                  GlStateManager.popMatrix();
               }
            }
         }

      }
   }

   private void renderType(RenderType type, float scale) {
      if (type != null) {
         if (type == RenderType.HEAD) {
            this.renderer.getMainModel().bipedHead.postRender(scale);
         } else if (type == RenderType.BODY) {
            this.renderer.getMainModel().bipedBody.postRender(scale);
         }

      }
   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
