package vazkii.quark.building;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.building.feature.BarkBlocks;
import vazkii.quark.building.feature.CarvedWood;
import vazkii.quark.building.feature.CharredNetherBricks;
import vazkii.quark.building.feature.DuskboundBlocks;
import vazkii.quark.building.feature.FramedGlass;
import vazkii.quark.building.feature.HardenedClayTiles;
import vazkii.quark.building.feature.IronPlates;
import vazkii.quark.building.feature.MagmaBricks;
import vazkii.quark.building.feature.MidoriBlocks;
import vazkii.quark.building.feature.MoreSandstone;
import vazkii.quark.building.feature.PolishedNetherrack;
import vazkii.quark.building.feature.PolishedStone;
import vazkii.quark.building.feature.QuiltedWool;
import vazkii.quark.building.feature.ReedBlock;
import vazkii.quark.building.feature.SandyBricks;
import vazkii.quark.building.feature.SnowBricks;
import vazkii.quark.building.feature.SoulSandstone;
import vazkii.quark.building.feature.StainedPlanks;
import vazkii.quark.building.feature.SturdyStone;
import vazkii.quark.building.feature.Thatch;
import vazkii.quark.building.feature.Trowel;
import vazkii.quark.building.feature.Turf;
import vazkii.quark.building.feature.VanillaStairsAndSlabs;
import vazkii.quark.building.feature.VanillaWalls;
import vazkii.quark.building.feature.VerticalWoodPlanks;
import vazkii.quark.building.feature.WorldStoneBricks;
import vazkii.quark.building.feature.WorldStonePavement;

public class QuarkBuilding extends Module {
   public QuarkBuilding() {
   }

   public void addFeatures() {
      this.registerFeature(new HardenedClayTiles());
      this.registerFeature(new VanillaStairsAndSlabs());
      this.registerFeature(new WorldStoneBricks());
      this.registerFeature(new Thatch());
      this.registerFeature(new SandyBricks());
      this.registerFeature(new ReedBlock(), "Sugar cane blocks");
      this.registerFeature(new BarkBlocks());
      this.registerFeature(new VanillaWalls());
      this.registerFeature(new PolishedStone());
      this.registerFeature(new CarvedWood());
      this.registerFeature(new SnowBricks());
      this.registerFeature(new CharredNetherBricks());
      this.registerFeature(new MoreSandstone());
      this.registerFeature(new MidoriBlocks());
      this.registerFeature(new IronPlates());
      this.registerFeature(new VerticalWoodPlanks());
      this.registerFeature(new SoulSandstone());
      this.registerFeature(new StainedPlanks());
      this.registerFeature(new PolishedNetherrack());
      this.registerFeature(new DuskboundBlocks());
      this.registerFeature(new SturdyStone());
      this.registerFeature(new QuiltedWool());
      this.registerFeature(new Trowel());
      this.registerFeature(new MagmaBricks());
      this.registerFeature(new WorldStonePavement());
      this.registerFeature(new FramedGlass());
      this.registerFeature(new Turf());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Blocks.BRICK_BLOCK);
   }
}
