package vazkii.quark.world;

import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.world.feature.Archaeologist;
import vazkii.quark.world.feature.Basalt;
import vazkii.quark.world.feature.Biotite;
import vazkii.quark.world.feature.BrokenNetherPortals;
import vazkii.quark.world.feature.BuriedTreasure;
import vazkii.quark.world.feature.CaveRoots;
import vazkii.quark.world.feature.ClayGeneration;
import vazkii.quark.world.feature.Crabs;
import vazkii.quark.world.feature.CrystalCaves;
import vazkii.quark.world.feature.DefaultWorldOptions;
import vazkii.quark.world.feature.DepthMobs;
import vazkii.quark.world.feature.FairyRings;
import vazkii.quark.world.feature.Foxhounds;
import vazkii.quark.world.feature.Frogs;
import vazkii.quark.world.feature.MonsterBoxes;
import vazkii.quark.world.feature.MushroomsInSwamps;
import vazkii.quark.world.feature.NaturalBlazesInNether;
import vazkii.quark.world.feature.NetherFossils;
import vazkii.quark.world.feature.NetherObsidianSpikes;
import vazkii.quark.world.feature.NetherSmoker;
import vazkii.quark.world.feature.OceanGuardians;
import vazkii.quark.world.feature.PathfinderMaps;
import vazkii.quark.world.feature.PirateShips;
import vazkii.quark.world.feature.RealisticWorldType;
import vazkii.quark.world.feature.RevampStoneGen;
import vazkii.quark.world.feature.Speleothems;
import vazkii.quark.world.feature.Stonelings;
import vazkii.quark.world.feature.TreeVariants;
import vazkii.quark.world.feature.UndergroundBiomes;
import vazkii.quark.world.feature.VariedDungeons;
import vazkii.quark.world.feature.Wraiths;

public class QuarkWorld extends Module {
   public QuarkWorld() {
   }

   public void addFeatures() {
      this.registerFeature(new Basalt());
      this.registerFeature(new ClayGeneration(), "Generate clay underground like dirt");
      this.registerFeature(new OceanGuardians(), "Guardians spawn in oceans");
      this.registerFeature(new NaturalBlazesInNether(), "Blazes spawn naturally in the nether");
      this.registerFeature(new MushroomsInSwamps(), "Big mushrooms generate in swamps");
      this.registerFeature(new Biotite());
      this.registerFeature(new BuriedTreasure());
      this.registerFeature(new DepthMobs());
      this.registerFeature(new PirateShips());
      this.registerFeature(new Wraiths());
      this.registerFeature(new RevampStoneGen());
      this.registerFeature(new CrystalCaves());
      this.registerFeature(new VariedDungeons());
      this.registerFeature(new UndergroundBiomes());
      this.registerFeature(new PathfinderMaps());
      this.registerFeature(new NetherSmoker());
      this.registerFeature(new NetherFossils());
      this.registerFeature(new Speleothems());
      this.registerFeature(new FairyRings());
      this.registerFeature(new CaveRoots());
      this.registerFeature(new Archaeologist());
      this.registerFeature(new MonsterBoxes());
      this.registerFeature(new Stonelings());
      this.registerFeature(new Frogs());
      this.registerFeature(new Foxhounds());
      this.registerFeature(new Crabs());
      this.registerFeature(new TreeVariants());
      this.registerFeature(new BrokenNetherPortals());
      this.registerFeature(new NetherObsidianSpikes());
      this.registerFeature(new RealisticWorldType());
      this.registerFeature(new DefaultWorldOptions());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Blocks.GRASS);
   }
}
