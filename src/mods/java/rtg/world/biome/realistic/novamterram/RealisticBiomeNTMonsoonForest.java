package rtg.world.biome.realistic.novamterram;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import rtg.api.util.BlockUtil;

public class RealisticBiomeNTMonsoonForest extends RealisticBiomeNTBaseForest {
   public RealisticBiomeNTMonsoonForest(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      super.initConfig();
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      super.initDecos();
      this.fallenTrees(new IBlockState[]{BlockUtil.getStateLog(EnumType.JUNGLE), BlockUtil.getStateLog(EnumType.ACACIA), BlockUtil.getStateLog(EnumType.OAK)}, new int[]{4, 1, 1});
   }
}
