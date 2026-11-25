package vazkii.quark.world.client.layer;

import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.world.entity.EntityStoneling;

@SideOnly(Side.CLIENT)
public class LayerStonelingItem implements LayerRenderer<EntityStoneling> {
   public LayerStonelingItem() {
   }

   public void doRenderLayer(@Nonnull EntityStoneling stoneling, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      ItemStack stack = stoneling.getCarryingItem();
      if (!stack.isEmpty()) {
         boolean isBlock = stack.getItem() instanceof ItemBlock;
         GlStateManager.pushMatrix();
         GlStateManager.translate(0.0F, 0.5F, 0.0F);
         if (!isBlock) {
            GlStateManager.rotate(stoneling.getItemAngle() + 180.0F, 0.0F, 1.0F, 0.0F);
            GlStateManager.rotate(90.0F, 1.0F, 0.0F, 0.0F);
         } else {
            GlStateManager.rotate(180.0F, 1.0F, 0.0F, 0.0F);
         }

         GlStateManager.scale(0.725F, 0.725F, 0.725F);
         Minecraft mc = Minecraft.getMinecraft();
         mc.getRenderItem().renderItem(stack, TransformType.FIXED);
         GlStateManager.popMatrix();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }
}
