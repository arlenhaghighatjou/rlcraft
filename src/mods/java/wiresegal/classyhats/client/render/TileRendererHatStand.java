package wiresegal.classyhats.client.render;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.OpenGlHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import wiresegal.classyhats.block.BlockHatContainer;

@SideOnly(Side.CLIENT)
@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000.\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0002\b\u0002\n\u0002\u0010\u0002\n\u0002\b\u0002\n\u0002\u0010\u0006\n\u0002\b\u0003\n\u0002\u0010\u0007\n\u0000\n\u0002\u0010\b\n\u0002\b\u0002\bÇ\u0002\u0018\u00002\b\u0012\u0004\u0012\u00020\u00020\u0001B\u0007\b\u0002¢\u0006\u0002\u0010\u0003J@\u0010\u0004\u001a\u00020\u00052\u0006\u0010\u0006\u001a\u00020\u00022\u0006\u0010\u0007\u001a\u00020\b2\u0006\u0010\t\u001a\u00020\b2\u0006\u0010\n\u001a\u00020\b2\u0006\u0010\u000b\u001a\u00020\f2\u0006\u0010\r\u001a\u00020\u000e2\u0006\u0010\u000f\u001a\u00020\fH\u0016¨\u0006\u0010"},
   d2 = {"Lwiresegal/classyhats/client/render/TileRendererHatStand;", "Lnet/minecraft/client/renderer/tileentity/TileEntitySpecialRenderer;", "Lwiresegal/classyhats/block/BlockHatContainer$TileHatContainer;", "()V", "render", "", "te", "x", "", "y", "z", "partialTicks", "", "destroyStage", "", "alpha", "classyhats"}
)
public final class TileRendererHatStand extends TileEntitySpecialRenderer<BlockHatContainer.TileHatContainer> {
   public static final TileRendererHatStand INSTANCE;

   public void render(@NotNull BlockHatContainer.TileHatContainer te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      Intrinsics.checkParameterIsNotNull(te, "te");
      ItemStack var10000 = ((ItemStackHandler)te.getInventory().getHandler()).getStackInSlot(0);
      Intrinsics.checkExpressionValueIsNotNull(var10000, "te.inventory.handler.getStackInSlot(0)");
      ItemStack stack = var10000;
      if (!stack.isEmpty()) {
         GlStateManager.pushMatrix();
         int amount = this.rendererDispatcher.world.getCombinedLight(te.getPos().up(), 0);
         float lX = (float)(amount % 65536);
         float lY = (float)(amount / 65536);
         float prevX = OpenGlHelper.lastBrightnessX;
         float prevY = OpenGlHelper.lastBrightnessY;
         GlStateManager.translate(x + 0.5D, y + 1.4625D, z + 0.5D);
         GlStateManager.scale(0.9375D, 0.9375D, 0.9375D);
         GlStateManager.rotate(te.getAngle(), 0.0F, -1.0F, 0.0F);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, lX, lY);
         Minecraft var17 = Minecraft.getMinecraft();
         Intrinsics.checkExpressionValueIsNotNull(var17, "Minecraft.getMinecraft()");
         var17.getRenderItem().renderItem(stack, TransformType.NONE);
         OpenGlHelper.setLightmapTextureCoords(OpenGlHelper.lightmapTexUnit, prevX, prevY);
         GlStateManager.popMatrix();
      }

   }

   private TileRendererHatStand() {
   }

   static {
      TileRendererHatStand var0 = new TileRendererHatStand();
      INSTANCE = var0;
   }
}
