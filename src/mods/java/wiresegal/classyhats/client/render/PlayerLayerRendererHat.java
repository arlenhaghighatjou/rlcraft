package wiresegal.classyhats.client.render;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.model.ModelRenderer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.entity.layers.LayerRenderer;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.capability.CapabilityHatContainer;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0007\n\u0002\b\u0007\n\u0002\u0010\u000b\n\u0000\b\u0007\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\r\u0012\u0006\u0010\u0003\u001a\u00020\u0004¢\u0006\u0002\u0010\u0005JH\u0010\u0006\u001a\u00020\u00072\u0006\u0010\b\u001a\u00020\u00022\u0006\u0010\t\u001a\u00020\n2\u0006\u0010\u000b\u001a\u00020\n2\u0006\u0010\f\u001a\u00020\n2\u0006\u0010\r\u001a\u00020\n2\u0006\u0010\u000e\u001a\u00020\n2\u0006\u0010\u000f\u001a\u00020\n2\u0006\u0010\u0010\u001a\u00020\nH\u0016J\b\u0010\u0011\u001a\u00020\u0012H\u0016R\u000e\u0010\u0003\u001a\u00020\u0004X\u0082\u0004¢\u0006\u0002\n\u0000¨\u0006\u0013"},
   d2 = {"Lwiresegal/classyhats/client/render/PlayerLayerRendererHat;", "Lnet/minecraft/client/renderer/entity/layers/LayerRenderer;", "Lnet/minecraft/entity/player/EntityPlayer;", "modelRenderer", "Lnet/minecraft/client/model/ModelRenderer;", "(Lnet/minecraft/client/model/ModelRenderer;)V", "doRenderLayer", "", "player", "limbSwing", "", "limbSwingAmount", "partialTicks", "ageInTicks", "netHeadYaw", "headPitch", "scale", "shouldCombineTextures", "", "classyhats"}
)
public final class PlayerLayerRendererHat implements LayerRenderer<EntityPlayer> {
   private final ModelRenderer modelRenderer;

   public void doRenderLayer(@NotNull EntityPlayer player, float limbSwing, float limbSwingAmount, float partialTicks, float ageInTicks, float netHeadYaw, float headPitch, float scale) {
      Intrinsics.checkParameterIsNotNull(player, "player");
      ItemStack stack = CapabilityHatContainer.Companion.getCapability(player).getEquipped();
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         if (player.isSneaking()) {
            GlStateManager.translate(0.0F, 0.2F, 0.0F);
         }

         this.modelRenderer.postRender(0.0625F);
         GlStateManager.color(1.0F, 1.0F, 1.0F, 1.0F);
         GlStateManager.translate(0.0F, -0.25F, 0.0F);
         GlStateManager.rotate(180.0F, 0.0F, 1.0F, 0.0F);
         GlStateManager.scale(0.625F, -0.625F, -0.625F);
         Minecraft var10000 = Minecraft.getMinecraft();
         Intrinsics.checkExpressionValueIsNotNull(var10000, "Minecraft.getMinecraft()");
         var10000.getItemRenderer().renderItem((EntityLivingBase)player, stack, TransformType.HEAD);
         GlStateManager.popMatrix();
      }

   }

   public boolean shouldCombineTextures() {
      return false;
   }

   public PlayerLayerRendererHat(@NotNull ModelRenderer modelRenderer) {
      Intrinsics.checkParameterIsNotNull(modelRenderer, "modelRenderer");
      super();
      this.modelRenderer = modelRenderer;
   }
}
