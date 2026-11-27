package snownee.snow.hooks;

import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockRenderLayer;
import net.minecraft.util.EnumBlockRenderType;
import net.minecraft.util.ReportedException;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.IBlockAccess;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.MinecraftForgeClient;
import snownee.snow.ModConfig;
import snownee.snow.SnowTile;

public class BlockRendererDispatcherHook {
   public BlockRendererDispatcherHook() {
   }

   public static void renderBlock(BlockRendererDispatcher dispatcher, BlockModelRenderer blockModelRenderer, IBlockState blockStateIn, BlockPos pos, IBlockAccess world, WorldRenderer bufferBuilderIn) {
      if (blockStateIn.getBlock() == Blocks.SNOW_LAYER && ((Integer)blockStateIn.getValue(BlockSnow.LAYERS)).intValue() < 8) {
         TileEntity tileEntity = world.getTileEntity(pos);
         if (tileEntity instanceof SnowTile) {
            IBlockState state = ((SnowTile)tileEntity).getState();
            BlockRenderLayer layer = MinecraftForgeClient.getRenderLayer();
            if (!state.getBlock().canRenderInLayer(state, layer)) {
               return;
            }

            try {
               if (state.getRenderType() == EnumBlockRenderType.MODEL) {
                  if (world.getWorldType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
                     try {
                        state = state.getActualState(world, pos);
                     } catch (Exception var12) {
                        ;
                     }
                  }

                  IBakedModel model = dispatcher.getModelForState(state);
                  state = state.getBlock().getExtendedState(state, world, pos);
                  boolean translate = ModConfig.yOffsetBlocksSet.contains(state.getBlock().getRegistryName());
                  if (translate) {
                     bufferBuilderIn.translateY(0.125D);
                  }

                  blockModelRenderer.renderModel(world, model, state, pos, bufferBuilderIn, true);
                  if (translate) {
                     bufferBuilderIn.translateY(-0.125D);
                  }
               }
            } catch (Throwable var13) {
               CrashReport crashreport = CrashReport.makeCrashReport(var13, "Tesselating block in world");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Block being tesselated");
               CrashReportCategory.addBlockInfo(crashreportcategory, pos, blockStateIn);
               throw new ReportedException(crashreport);
            }
         }
      }

   }
}
