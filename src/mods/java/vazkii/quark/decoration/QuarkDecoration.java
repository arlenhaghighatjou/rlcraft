package vazkii.quark.decoration;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.decoration.feature.BetterVanillaFlowerPot;
import vazkii.quark.decoration.feature.BlazeLantern;
import vazkii.quark.decoration.feature.CharcoalBlock;
import vazkii.quark.decoration.feature.ColoredFlowerPots;
import vazkii.quark.decoration.feature.ColoredItemFrames;
import vazkii.quark.decoration.feature.FlatItemFrames;
import vazkii.quark.decoration.feature.GlassItemFrame;
import vazkii.quark.decoration.feature.Grate;
import vazkii.quark.decoration.feature.IronLadders;
import vazkii.quark.decoration.feature.LeafCarpets;
import vazkii.quark.decoration.feature.LitLamp;
import vazkii.quark.decoration.feature.MoreBannerLayers;
import vazkii.quark.decoration.feature.MoreBanners;
import vazkii.quark.decoration.feature.NetherBrickFenceGate;
import vazkii.quark.decoration.feature.PaperLantern;
import vazkii.quark.decoration.feature.PaperWall;
import vazkii.quark.decoration.feature.PlaceBlazeRods;
import vazkii.quark.decoration.feature.Rope;
import vazkii.quark.decoration.feature.TallowAndCandles;
import vazkii.quark.decoration.feature.TieFences;
import vazkii.quark.decoration.feature.VariedBookshelves;
import vazkii.quark.decoration.feature.VariedButtonsAndPressurePlates;
import vazkii.quark.decoration.feature.VariedChests;
import vazkii.quark.decoration.feature.VariedTrapdoors;

public class QuarkDecoration extends Module {
   public QuarkDecoration() {
   }

   public void addFeatures() {
      this.registerFeature(new LitLamp());
      this.registerFeature(new BlazeLantern());
      this.registerFeature(new PaperWall());
      this.registerFeature(new VariedTrapdoors());
      this.registerFeature(new MoreBanners());
      this.registerFeature(new NetherBrickFenceGate());
      this.registerFeature(new ColoredItemFrames());
      this.registerFeature(new CharcoalBlock());
      this.registerFeature(new VariedChests());
      this.registerFeature(new LeafCarpets());
      this.registerFeature(new VariedBookshelves());
      this.registerFeature(new IronLadders());
      this.registerFeature(new FlatItemFrames());
      this.registerFeature(new GlassItemFrame());
      this.registerFeature(new ColoredFlowerPots());
      this.registerFeature(new BetterVanillaFlowerPot());
      this.registerFeature(new TallowAndCandles());
      this.registerFeature(new MoreBannerLayers());
      this.registerFeature(new VariedButtonsAndPressurePlates());
      this.registerFeature(new PaperLantern());
      this.registerFeature(new TieFences());
      this.registerFeature(new Rope());
      this.registerFeature(new PlaceBlazeRods());
      this.registerFeature(new Grate());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Blocks.RED_FLOWER);
   }
}
