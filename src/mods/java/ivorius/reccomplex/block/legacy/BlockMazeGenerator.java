package ivorius.reccomplex.block.legacy;

import ivorius.reccomplex.block.BlockLegacyScript;
import ivorius.reccomplex.world.gen.script.WorldScriptMazeGenerator;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockMazeGenerator extends BlockLegacyScript {
   public BlockMazeGenerator() {
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public TileEntity createTileEntity(World var1, IBlockState var2) {
      return new BlockMazeGenerator.TileEntityMazeGenerator();
   }

   public static class TileEntityMazeGenerator extends BlockLegacyScript.TileLegacyScript<WorldScriptMazeGenerator, WorldScriptMazeGenerator.InstanceData> {
      public TileEntityMazeGenerator() {
      }

      public WorldScriptMazeGenerator createScript() {
         return new WorldScriptMazeGenerator();
      }
   }
}
