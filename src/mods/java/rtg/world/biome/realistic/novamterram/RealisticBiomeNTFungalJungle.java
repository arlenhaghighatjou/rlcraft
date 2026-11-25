package rtg.world.biome.realistic.novamterram;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.world.biome.Biome;
import rtg.api.util.BlockUtil;

public class RealisticBiomeNTFungalJungle extends RealisticBiomeNTBaseForest {
   public RealisticBiomeNTFungalJungle(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      super.initConfig();
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      super.initDecos();
      this.fallenTrees(new IBlockState[]{BlockUtil.getStateLog(EnumType.JUNGLE), BlockUtil.getStateLog(EnumType.OAK)}, new int[]{4, 1});
   }

   protected IBlockState getMixBlock1() {
      return Blocks.MYCELIUM.getDefaultState();
   }

   protected IBlockState getMixBlock3() {
      return Blocks.MYCELIUM.getDefaultState();
   }
}
