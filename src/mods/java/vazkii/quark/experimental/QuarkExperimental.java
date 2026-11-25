package vazkii.quark.experimental;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.experimental.client.TextureDump;
import vazkii.quark.experimental.features.BetterNausea;
import vazkii.quark.experimental.features.BiggerCaves;
import vazkii.quark.experimental.features.CollateralPistonMovement;
import vazkii.quark.experimental.features.ColoredLights;
import vazkii.quark.experimental.features.CustomSplashes;
import vazkii.quark.experimental.features.FramedBlocks;
import vazkii.quark.experimental.features.ParrotsSayDespacito;
import vazkii.quark.experimental.features.PrintSuppressor;
import vazkii.quark.experimental.features.ReactiveCursor;
import vazkii.quark.experimental.features.RejectMods;

public class QuarkExperimental extends Module {
   public QuarkExperimental() {
   }

   public void addFeatures() {
      this.registerFeature(new BiggerCaves(), false);
      this.registerFeature(new PrintSuppressor(), false);
      this.registerFeature(new ColoredLights(), false);
      this.registerFeature(new ReactiveCursor(), false);
      this.registerFeature(new ParrotsSayDespacito(), false);
      this.registerFeature(new CollateralPistonMovement(), false);
      this.registerFeature(new FramedBlocks(), false);
      this.registerFeature(new TextureDump(), "Dump texture atlases", false);
      this.registerFeature(new BetterNausea(), false);
      this.registerFeature(new CustomSplashes(), false);
      this.registerFeature(new RejectMods(), false);
   }

   public String getModuleDescription() {
      return "Experimental Features. All features in this module are disabled by default. Use at your own risk.";
   }

   public ItemStack getIconStack() {
      return new ItemStack(Blocks.TNT);
   }
}
