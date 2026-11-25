package ivorius.ivtoolkit.blocks;

import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.EnumFacing;

public class IvRotatableBlockRenderHelper {
   public IvRotatableBlockRenderHelper() {
   }

   public static void transformFor(IvTileEntityRotatable tileEntity, double renderX, double renderY, double renderZ) {
      GlStateManager.translate(renderX + 0.5D, renderY + 0.5D, renderZ + 0.5D);
      GlStateManager.rotate(getAngleFromSouth(tileEntity.getFacing()), 0.0F, 1.0F, 0.0F);
   }

   public static float getAngleFromSouth(EnumFacing facing) {
      switch(facing) {
      case SOUTH:
         return 180.0F;
      case WEST:
         return 90.0F;
      case NORTH:
         return 0.0F;
      case EAST:
         return 270.0F;
      default:
         throw new IllegalArgumentException();
      }
   }
}
