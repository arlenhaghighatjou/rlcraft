package rtg.world.biome.realistic.novamterram;

import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import rtg.api.util.BlockUtil;
import rtg.api.world.biome.RealisticBiomeBase;

public class RealisticBiomeNTColdBlueTaiga extends RealisticBiomeNTBaseForest {
   public RealisticBiomeNTColdBlueTaiga(Biome biome) {
      super(biome, RealisticBiomeBase.RiverType.FROZEN, RealisticBiomeBase.BeachType.COLD);
   }

   public void initConfig() {
      super.initConfig();
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      super.initDecos();
      this.fallenTrees(new IBlockState[]{BlockUtil.getStateLog(EnumType.SPRUCE), BlockUtil.getStateLog(EnumType.SPRUCE)}, new int[]{2, 2});
   }
}
