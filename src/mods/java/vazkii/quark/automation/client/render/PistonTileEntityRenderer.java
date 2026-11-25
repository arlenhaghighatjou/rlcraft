package vazkii.quark.automation.client.render;

import net.minecraft.block.Block;
import net.minecraft.block.BlockDirectional;
import net.minecraft.block.BlockHorizontal;
import net.minecraft.block.state.IBlockState;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.RenderHelper;
import net.minecraft.client.renderer.tileentity.TileEntityRendererDispatcher;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.tileentity.TileEntityChest;
import net.minecraft.tileentity.TileEntityPiston;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.ResourceLocation;
import vazkii.quark.automation.feature.PistonsMoveTEs;
import vazkii.quark.base.module.ModuleLoader;

public class PistonTileEntityRenderer {
   public PistonTileEntityRenderer() {
   }

   public static boolean renderPistonBlock(TileEntityPiston piston, double x, double y, double z, float pTicks) {
      if (ModuleLoader.isFeatureEnabled(PistonsMoveTEs.class) && piston.getProgress(pTicks) <= 1.0F) {
         IBlockState state = piston.getPistonState();
         Block block = state.getBlock();
         String id = ((ResourceLocation)Block.REGISTRY.getNameForObject(block)).toString();

         try {
            TileEntity tile = PistonsMoveTEs.getMovement(piston.getWorld(), piston.getPos());
            if (tile == null || PistonsMoveTEs.renderBlacklist.contains(id)) {
               return false;
            }

            GlStateManager.pushMatrix();
            tile.setWorld(piston.getWorld());
            tile.validate();
            if (tile instanceof TileEntityChest) {
               TileEntityChest chest = (TileEntityChest)tile;
               chest.adjacentChestXPos = null;
               chest.adjacentChestXNeg = null;
               chest.adjacentChestZPos = null;
               chest.adjacentChestZNeg = null;
            }

            EnumFacing facing = null;
            if (state.getPropertyKeys().contains(BlockHorizontal.FACING)) {
               facing = (EnumFacing)state.getValue(BlockHorizontal.FACING);
            } else if (state.getPropertyKeys().contains(BlockDirectional.FACING)) {
               facing = (EnumFacing)state.getValue(BlockDirectional.FACING);
            }

            GlStateManager.translate(x + (double)piston.getOffsetX(pTicks), y + (double)piston.getOffsetY(pTicks), z + (double)piston.getOffsetZ(pTicks));
            if (facing != null) {
               float rotation = 0.0F;
               switch(facing) {
               case NORTH:
                  rotation = 180.0F;
                  break;
               case EAST:
                  rotation = 90.0F;
                  break;
               case WEST:
                  rotation = -90.0F;
               }

               GlStateManager.translate(0.5D, 0.5D, 0.5D);
               GlStateManager.rotate(rotation, 0.0F, 1.0F, 0.0F);
               GlStateManager.translate(-0.5D, -0.5D, -0.5D);
            }

            RenderHelper.enableStandardItemLighting();
            TileEntityRendererDispatcher.instance.render(tile, 0.0D, 0.0D, 0.0D, pTicks);
            RenderHelper.disableStandardItemLighting();
            GlStateManager.popMatrix();
         } catch (Throwable var14) {
            (new RuntimeException(id + " can't be rendered for piston TE moving", var14)).printStackTrace();
            PistonsMoveTEs.renderBlacklist.add(id);
            return false;
         }

         return state.getRenderType() != EnumBlockRenderType.MODEL;
      } else {
         return false;
      }
   }
}
