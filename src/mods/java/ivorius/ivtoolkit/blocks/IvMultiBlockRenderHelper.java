package ivorius.ivtoolkit.blocks;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;

public class IvMultiBlockRenderHelper {
   public IvMultiBlockRenderHelper() {
   }

   public static void transformFor(IvTileEntityMultiBlock tileEntity, double renderX, double renderY, double renderZ) {
      double[] center = tileEntity.getActiveCenterCoords();
      GlStateManager.translate(renderX + center[0] - (double)tileEntity.getPos().getX(), renderY + center[1] - (double)tileEntity.getPos().getY(), renderZ + center[2] - (double)tileEntity.getPos().getZ());
      GlStateManager.rotate(IvRotatableBlockRenderHelper.getAngleFromSouth(tileEntity.getFacing()), 0.0F, 1.0F, 0.0F);
   }
}
