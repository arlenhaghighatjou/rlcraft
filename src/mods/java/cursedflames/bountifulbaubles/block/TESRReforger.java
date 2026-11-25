package cursedflames.bountifulbaubles.block;

import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.block.model.ItemCameraTransforms.TransformType;
import net.minecraft.client.renderer.tileentity.TileEntitySpecialRenderer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumFacing;
import net.minecraftforge.items.CapabilityItemHandler;
import net.minecraftforge.items.IItemHandler;

public class TESRReforger extends TileEntitySpecialRenderer<TileReforger> {
   public TESRReforger() {
   }

   public void render(TileReforger te, double x, double y, double z, float partialTicks, int destroyStage, float alpha) {
      IItemHandler handler = (IItemHandler)te.getCapability(CapabilityItemHandler.ITEM_HANDLER_CAPABILITY, (EnumFacing)null);
      if (handler != null && !handler.getStackInSlot(0).isEmpty()) {
         ItemStack stack = handler.getStackInSlot(0);
         GlStateManager.pushAttrib();
         GlStateManager.pushMatrix();
         GlStateManager.translate(x, y, z);
         GlStateManager.disableRescaleNormal();
         RenderHelper.enableStandardItemLighting();
         GlStateManager.enableLighting();
         float tick = (float)te.getWorld().getTotalWorldTime() + partialTicks;
         float vert = (float)(Math.sin((double)(tick / 10.0F)) * 0.07000000029802322D);
         IBlockState state = Minecraft.getMinecraft().world.getBlockState(te.getPos());
         EnumFacing dir = (EnumFacing)state.getValue(BlockReforger.FACING);
         GlStateManager.translate(0.5F, 0.0F, 0.5F);
         GlStateManager.rotate(-dir.getHorizontalAngle(), 0.0F, 1.0F, 0.0F);
         GlStateManager.translate(-0.28125D, 0.7D + (double)vert, 0.21875D);
         GlStateManager.scale(0.3F, 0.3F, 0.3F);
         float rot = tick / 20.0F * 180.0F / 3.1415927F;
         GlStateManager.rotate(rot, 0.0F, 1.0F, 0.0F);
         Minecraft.getMinecraft().getRenderItem().renderItem(stack, TransformType.NONE);
         GlStateManager.popMatrix();
         GlStateManager.popAttrib();
      }
   }
}
