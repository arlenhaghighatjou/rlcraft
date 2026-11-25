package ivorius.reccomplex.block.legacy;

import ivorius.reccomplex.block.BlockLegacyScript;
import ivorius.reccomplex.block.GeneratingTileEntity;
import ivorius.reccomplex.nbt.NBTNone;
import ivorius.reccomplex.world.gen.script.WorldScriptCommand;
import javax.annotation.Nonnull;
import javax.annotation.ParametersAreNonnullByDefault;
import net.minecraft.block.state.IBlockState;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.world.World;

public class BlockSpawnCommand extends BlockLegacyScript {
   public BlockSpawnCommand() {
   }

   @Nonnull
   @ParametersAreNonnullByDefault
   public TileEntity createTileEntity(World world, IBlockState state) {
      return new BlockSpawnCommand.TileEntitySpawnCommand();
   }

   public static class TileEntitySpawnCommand extends BlockLegacyScript.TileLegacyScript<WorldScriptCommand, NBTNone> implements GeneratingTileEntity<NBTNone> {
      public TileEntitySpawnCommand() {
      }

      public WorldScriptCommand createScript() {
         return new WorldScriptCommand();
      }
   }
}
