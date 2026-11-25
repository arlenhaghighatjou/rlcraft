package rtg.world.biome.realistic.novamterram;

import net.minecraft.block.BlockDirt.DirtType;
import net.minecraft.block.BlockPlanks.EnumType;
import net.minecraft.block.state.IBlockState;
import net.minecraft.world.biome.Biome;
import rtg.api.util.BlockUtil;

public class RealisticBiomeNTMegaAutumnTaiga extends RealisticBiomeNTBaseForest {
   public RealisticBiomeNTMegaAutumnTaiga(Biome biome) {
      super(biome);
   }

   public void initConfig() {
      super.initConfig();
      this.getConfig().addProperty(this.getConfig().ALLOW_LOGS).set(true);
      this.getConfig().addProperty(this.getConfig().FALLEN_LOG_DENSITY_MULTIPLIER);
   }

   public void initDecos() {
      super.initDecos();
      this.fallenTrees(new IBlockState[]{BlockUtil.getStateLog(EnumType.OAK), BlockUtil.getStateLog(EnumType.SPRUCE)}, new int[]{1, 2});
   }

   protected IBlockState getMixBlock1() {
      return BlockUtil.getStateDirt(DirtType.PODZOL);
   }

   protected IBlockState getMixBlock2() {
      return BlockUtil.getStateDirt(DirtType.COARSE_DIRT);
   }

   protected IBlockState getMixBlock3() {
      return BlockUtil.getStateDirt(DirtType.PODZOL);
   }
}
