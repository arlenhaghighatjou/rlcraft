package vazkii.quark.automation;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.automation.feature.AnimalsEatFloorFood;
import vazkii.quark.automation.feature.ChainLinkage;
import vazkii.quark.automation.feature.Chute;
import vazkii.quark.automation.feature.ColorSlime;
import vazkii.quark.automation.feature.DispenserRecords;
import vazkii.quark.automation.feature.DispensersPlaceBlocks;
import vazkii.quark.automation.feature.DispensersPlaceSeeds;
import vazkii.quark.automation.feature.EnderWatcher;
import vazkii.quark.automation.feature.Gravisand;
import vazkii.quark.automation.feature.MetalButtons;
import vazkii.quark.automation.feature.ObsidianPressurePlate;
import vazkii.quark.automation.feature.PistonSpikes;
import vazkii.quark.automation.feature.PistonsMoveTEs;
import vazkii.quark.automation.feature.PistonsPushPullItems;
import vazkii.quark.automation.feature.RainDetector;
import vazkii.quark.automation.feature.RedstoneInductor;
import vazkii.quark.automation.feature.RedstoneRandomizer;
import vazkii.quark.automation.feature.SugarBlock;
import vazkii.quark.base.module.Module;

public class QuarkAutomation extends Module {
   public QuarkAutomation() {
   }

   public void addFeatures() {
      this.registerFeature(new ObsidianPressurePlate());
      this.registerFeature(new DispensersPlaceSeeds());
      this.registerFeature(new RainDetector());
      this.registerFeature(new EnderWatcher());
      this.registerFeature(new PistonSpikes(), "Piston Block Breakers");
      this.registerFeature(new AnimalsEatFloorFood());
      this.registerFeature(new PistonsMoveTEs());
      this.registerFeature(new DispensersPlaceBlocks());
      this.registerFeature(new DispenserRecords());
      this.registerFeature(new PistonsPushPullItems(), "Pistons Push and Pull Items");
      this.registerFeature(new Chute());
      this.registerFeature(new MetalButtons());
      this.registerFeature(new SugarBlock());
      this.registerFeature(new RedstoneRandomizer());
      this.registerFeature(new Gravisand());
      this.registerFeature(new RedstoneInductor());
      this.registerFeature(new ChainLinkage());
      this.registerFeature(new ColorSlime());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Items.REDSTONE);
   }
}
