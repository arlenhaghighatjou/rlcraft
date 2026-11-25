package svenhjol.charm.world.feature;

import net.minecraft.block.Block;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.GameRegistry;
import svenhjol.charm.world.block.BlockNetherGoldDeposit;
import svenhjol.charm.world.generator.NetherGoldDepositGenerator;
import svenhjol.meson.Feature;
import svenhjol.meson.helper.ForgeHelper;

public class NetherGoldDeposits extends Feature {
   public static Block ore;
   public static int clusterSize;
   public static int clusterCount;
   public static int minNuggets;
   public static int maxNuggets;
   public static float hardness;
   public static float resistance;

   public NetherGoldDeposits() {
   }

   public String getDescription() {
      return "Gold deposits spawn in the Nether that can be broken to receive gold nuggets.";
   }

   public void configure() {
      super.configure();
      clusterSize = 12;
      clusterCount = 12;
      hardness = 3.0F;
      resistance = 10.0F;
      minNuggets = 2;
      maxNuggets = 5;
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      ore = new BlockNetherGoldDeposit();
      GameRegistry.registerWorldGenerator(new NetherGoldDepositGenerator(), 0);
   }

   public boolean isEnabled() {
      return this.enabled && !ForgeHelper.areModsLoaded("nethergoldore");
   }
}
