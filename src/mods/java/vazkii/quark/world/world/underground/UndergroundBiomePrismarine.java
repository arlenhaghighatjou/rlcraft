package vazkii.quark.world.world.underground;

import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import vazkii.quark.base.module.ConfigHelper;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.world.feature.UndergroundBiomes;

public class UndergroundBiomePrismarine extends BasicUndergroundBiome {
   public static double seaLanternChance;
   public static double waterChance;
   private IBlockState lanternState = Blocks.SEA_LANTERN.getDefaultState();
   public static boolean spawnElderPrismarine;

   public UndergroundBiomePrismarine() {
      super(Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState(), Blocks.PRISMARINE.getDefaultState());
   }

   public void update() {
      boolean elder = UndergroundBiomes.elderPrismarineEnabled && spawnElderPrismarine;
      IBlockState prismarineState = ((Block)(elder ? UndergroundBiomes.elder_prismarine : Blocks.PRISMARINE)).getDefaultState();
      this.ceilingState = this.floorState = this.wallState = prismarineState;
      this.lanternState = (elder ? UndergroundBiomes.elder_sea_lantern : Blocks.SEA_LANTERN).getDefaultState();
   }

   public void fillWall(World world, BlockPos pos, IBlockState state) {
      super.fillWall(world, pos, state);
      if (world.rand.nextDouble() < seaLanternChance) {
         world.setBlockState(pos, this.lanternState, 2);
      }

   }

   public void fillFloor(World world, BlockPos pos, IBlockState state) {
      if (world.rand.nextDouble() < waterChance && !this.isBorder(world, pos)) {
         world.setBlockState(pos, Blocks.WATER.getDefaultState());
      } else {
         super.fillFloor(world, pos, state);
      }

   }

   public void setupConfig(String category) {
      seaLanternChance = ConfigHelper.loadLegacyPropChance("Sea Lantern Percentage Chance", category, "Sea Lantern Chance", "The chance sea lanterns will spawn", 0.0085D);
      waterChance = ConfigHelper.loadLegacyPropChance("Water Percentage Chance", category, "Water Chance", "The chance water will spawn", 0.25D);
      spawnElderPrismarine = ModuleLoader.config.getBoolean("Spawn Elder Prismarine", category, true, "Set to false to spawn regular prismarine instead of elder prismarine (even if the block is enabled)");
   }
}
