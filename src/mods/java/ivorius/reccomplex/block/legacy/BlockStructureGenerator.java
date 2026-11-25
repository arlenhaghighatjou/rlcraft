package ivorius.reccomplex.block.legacy;

import ivorius.reccomplex.block.BlockLegacyScript;
import ivorius.reccomplex.world.gen.script.WorldScriptStructureGenerator;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockStructureGenerator extends BlockLegacyScript {
   public BlockStructureGenerator() {
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public TileEntity createTileEntity(World world, IBlockState state) {
      return new BlockStructureGenerator.TileEntityStructureGenerator();
   }

   public static class TileEntityStructureGenerator extends BlockLegacyScript.TileLegacyScript<WorldScriptStructureGenerator, WorldScriptStructureGenerator.InstanceData> {
      public TileEntityStructureGenerator() {
      }

      public WorldScriptStructureGenerator createScript() {
         return new WorldScriptStructureGenerator();
      }
   }
}
