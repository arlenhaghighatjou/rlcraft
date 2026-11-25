package ejektaflex.bountiful.worldgen;

import kotlin.Metadata;
import kotlin.jvm.internal.Intrinsics;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraft.world.gen.structure.template.BlockRotationProcessor;
import net.minecraft.world.gen.structure.template.PlacementSettings;
import net.minecraft.world.gen.structure.template.Template.BlockInfo;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@Metadata(
   mv = {1, 1, 13},
   bv = {1, 0, 3},
   k = 1,
   d1 = {"\u0000,\n\u0002\u0018\u0002\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\t\n\u0002\u0018\u0002\n\u0000\n\u0002\u0018\u0002\n\u0002\b\u0003\u0018\u00002\u00020\u0001B7\b\u0016\u0012\u0006\u0010\u0002\u001a\u00020\u0003\u0012\u0006\u0010\u0004\u001a\u00020\u0005\u0012\u0006\u0010\u0006\u001a\u00020\u0007\u0012\u0006\u0010\b\u001a\u00020\u0007\u0012\u0006\u0010\t\u001a\u00020\u0007\u0012\u0006\u0010\n\u001a\u00020\u0007\u00a2\u0006\u0002\u0010\u000bJ\"\u0010\u0010\u001a\u0004\u0018\u00010\u00112\u0006\u0010\u0012\u001a\u00020\u00132\u0006\u0010\u0014\u001a\u00020\u00032\u0006\u0010\u0015\u001a\u00020\u0011H\u0016R\u000e\u0010\f\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\r\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000e\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000R\u000e\u0010\u000f\u001a\u00020\u0007X\u0082\u000e\u00a2\u0006\u0002\n\u0000\u00a8\u0006\u0016"},
   d2 = {"Lejektaflex/bountiful/worldgen/VillageBoardProcessor;", "Lnet/minecraft/world/gen/structure/template/BlockRotationProcessor;", "pos", "Lnet/minecraft/util/math/BlockPos;", "settings", "Lnet/minecraft/world/gen/structure/template/PlacementSettings;", "path", "Lnet/minecraft/block/state/IBlockState;", "planks", "gravel", "cobble", "(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/world/gen/structure/template/PlacementSettings;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;Lnet/minecraft/block/state/IBlockState;)V", "cobbleState", "gravelState", "pathState", "planksState", "processBlock", "Lnet/minecraft/world/gen/structure/template/Template$BlockInfo;", "world", "Lnet/minecraft/world/World;", "initialpos", "blockInfo", "Bountiful"}
)
public final class VillageBoardProcessor extends BlockRotationProcessor {
   private IBlockState pathState;
   private IBlockState planksState;
   private IBlockState gravelState;
   private IBlockState cobbleState;

   @Nullable
   public BlockInfo processBlock(@NotNull World world, @NotNull BlockPos initialpos, @NotNull BlockInfo blockInfo) {
      Intrinsics.checkParameterIsNotNull(world, "world");
      Intrinsics.checkParameterIsNotNull(initialpos, "initialpos");
      Intrinsics.checkParameterIsNotNull(blockInfo, "blockInfo");
      if (!Intrinsics.areEqual(blockInfo.blockState.getBlock(), Blocks.GRASS_PATH)) {
         return super.processBlock(world, initialpos, blockInfo);
      } else {
         BlockPos pos = initialpos;

         while(pos.getY() >= world.getSeaLevel() - 1) {
            IBlockState state = world.getBlockState(pos);
            Block block = state.getBlock();
            if (!Intrinsics.areEqual(block, Blocks.GRASS) && !Intrinsics.areEqual(block, Blocks.DIRT)) {
               if (state.getMaterial().isLiquid()) {
                  world.setBlockState(initialpos, this.planksState);
                  break;
               }

               if (!Intrinsics.areEqual(block, Blocks.SAND) && !Intrinsics.areEqual(block, Blocks.SANDSTONE) && !Intrinsics.areEqual(block, Blocks.RED_SANDSTONE)) {
                  BlockPos var10000 = pos.down();
                  Intrinsics.checkExpressionValueIsNotNull(var10000, "pos.down()");
                  pos = var10000;
                  continue;
               }

               IBlockState lowerState = world.getBlockState(initialpos.down());
               if (!Intrinsics.areEqual(lowerState.getBlock(), Blocks.AIR) && !lowerState.getMaterial().isLiquid()) {
                  world.setBlockState(initialpos, this.gravelState);
                  break;
               }

               world.setBlockState(initialpos, this.cobbleState);
               break;
            }

            world.setBlockToAir(initialpos.up());
            world.setBlockState(initialpos, this.pathState);
            break;
         }

         return null;
      }
   }

   public VillageBoardProcessor(@NotNull BlockPos pos, @NotNull PlacementSettings settings, @NotNull IBlockState path, @NotNull IBlockState planks, @NotNull IBlockState gravel, @NotNull IBlockState cobble) {
      Intrinsics.checkParameterIsNotNull(pos, "pos");
      Intrinsics.checkParameterIsNotNull(settings, "settings");
      Intrinsics.checkParameterIsNotNull(path, "path");
      Intrinsics.checkParameterIsNotNull(planks, "planks");
      Intrinsics.checkParameterIsNotNull(gravel, "gravel");
      Intrinsics.checkParameterIsNotNull(cobble, "cobble");
      super(pos, settings);
      this.pathState = path;
      this.planksState = planks;
      this.gravelState = gravel;
      this.cobbleState = cobble;
   }
}
