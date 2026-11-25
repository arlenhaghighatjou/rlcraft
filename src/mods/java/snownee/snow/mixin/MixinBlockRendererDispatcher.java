package snownee.snow.mixin;

import net.minecraft.block.BlockSnow;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.BlockModelRenderer;
import net.minecraft.client.renderer.BlockRendererDispatcher;
import net.lax1dude.eaglercraft.v1_8.opengl.WorldRenderer;
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
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import snownee.snow.BufferBuilderDuck;
import snownee.snow.ModConfig;
import snownee.snow.SnowTile;

@Mixin({BlockRendererDispatcher.class})
public abstract class MixinBlockRendererDispatcher {
   @Shadow
   @Final
   private BlockModelRenderer blockModelRenderer;

   public MixinBlockRendererDispatcher() {
   }

   @Inject(
      method = {"renderBlock"},
      at = {@At("RETURN")}
   )
   private void srm_renderBlock(IBlockState blockStateIn, BlockPos pos, IBlockAccess world, WorldRenderer bufferBuilderIn, CallbackInfoReturnable<Boolean> info) {
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

                  IBakedModel model = this.getModelForState(state);
                  state = state.getBlock().getExtendedState(state, world, pos);
                  boolean translate = ModConfig.yOffsetBlocksSet.contains(state.getBlock().getRegistryName());
                  if (translate) {
                     ((BufferBuilderDuck)bufferBuilderIn).translateY(0.125D);
                  }

                  this.blockModelRenderer.renderModel(world, model, state, pos, bufferBuilderIn, true);
                  if (translate) {
                     ((BufferBuilderDuck)bufferBuilderIn).translateY(-0.125D);
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

   @Shadow
   public abstract IBakedModel getModelForState(IBlockState var1);
}
