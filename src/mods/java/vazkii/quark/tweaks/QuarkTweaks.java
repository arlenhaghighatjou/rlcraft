package vazkii.quark.tweaks;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.tweaks.feature.ArmedArmorStands;
import vazkii.quark.tweaks.feature.AutomaticRecipeUnlock;
import vazkii.quark.tweaks.feature.AxesBreakLeaves;
import vazkii.quark.tweaks.feature.BabyZombiesBurn;
import vazkii.quark.tweaks.feature.BlastproofShulkerBoxes;
import vazkii.quark.tweaks.feature.ChickensShedFeathers;
import vazkii.quark.tweaks.feature.CompassesWorkEverywhere;
import vazkii.quark.tweaks.feature.ConvertClay;
import vazkii.quark.tweaks.feature.DeployLaddersDown;
import vazkii.quark.tweaks.feature.DirtToPath;
import vazkii.quark.tweaks.feature.DoubleDoors;
import vazkii.quark.tweaks.feature.DragonsBreathBottleless;
import vazkii.quark.tweaks.feature.DyeAnyWool;
import vazkii.quark.tweaks.feature.EndermenAntiCheese;
import vazkii.quark.tweaks.feature.GlassShards;
import vazkii.quark.tweaks.feature.HoeSickle;
import vazkii.quark.tweaks.feature.ImprovedSleeping;
import vazkii.quark.tweaks.feature.ImprovedStoneToolCrafting;
import vazkii.quark.tweaks.feature.JumpBoostStepAssist;
import vazkii.quark.tweaks.feature.KnockOnDoors;
import vazkii.quark.tweaks.feature.LookDownLadders;
import vazkii.quark.tweaks.feature.MinecartInteraction;
import vazkii.quark.tweaks.feature.PatTheDogs;
import vazkii.quark.tweaks.feature.QuickArmorSwapping;
import vazkii.quark.tweaks.feature.RemoveSnowLayers;
import vazkii.quark.tweaks.feature.RightClickHarvest;
import vazkii.quark.tweaks.feature.RightClickSignEdit;
import vazkii.quark.tweaks.feature.ShearableChickens;
import vazkii.quark.tweaks.feature.SheepArmor;
import vazkii.quark.tweaks.feature.SlabsToBlocks;
import vazkii.quark.tweaks.feature.SpongeDriesInNether;
import vazkii.quark.tweaks.feature.SpringySlime;
import vazkii.quark.tweaks.feature.SquidsInkYou;
import vazkii.quark.tweaks.feature.StackableItems;
import vazkii.quark.tweaks.feature.StairsMakeMore;
import vazkii.quark.tweaks.feature.TorchesBurnInFurnaces;
import vazkii.quark.tweaks.feature.VillagerPursueEmeralds;

public class QuarkTweaks extends Module {
   public QuarkTweaks() {
   }

   public void addFeatures() {
      this.registerFeature(new StackableItems());
      this.registerFeature(new LookDownLadders(), "Look down on ladders to descend fast");
      this.registerFeature(new RightClickSignEdit());
      this.registerFeature(new ChickensShedFeathers());
      this.registerFeature(new GlassShards());
      this.registerFeature(new StairsMakeMore(), "Stair crafting makes more");
      this.registerFeature(new SlabsToBlocks(), "Slabs to blocks recipe");
      this.registerFeature(new JumpBoostStepAssist(), "Jump boost allows to step up 1 block");
      this.registerFeature(new DragonsBreathBottleless(), "Dragon's Breath doesn't leave a bottle behind");
      this.registerFeature(new KnockOnDoors());
      this.registerFeature(new ArmedArmorStands());
      this.registerFeature(new BabyZombiesBurn());
      this.registerFeature(new ShearableChickens());
      this.registerFeature(new MinecartInteraction(), "Right click minecarts to add blocks to them");
      this.registerFeature(new EndermenAntiCheese());
      this.registerFeature(new SheepArmor(), "Sheep have armor while wearing wool");
      this.registerFeature(new ImprovedStoneToolCrafting(), "Allow crafting stone tools using flint");
      this.registerFeature(new ConvertClay(), "Convert Clay Blocks to Clay");
      this.registerFeature(new RemoveSnowLayers(), "Remove layers of snow by right clicking with a shovel");
      this.registerFeature(new HoeSickle(), "Hoes break a 5x5 of plants");
      this.registerFeature(new DeployLaddersDown());
      this.registerFeature(new DoubleDoors(), "Double doors open together");
      this.registerFeature(new TorchesBurnInFurnaces());
      this.registerFeature(new VillagerPursueEmeralds(), "Villagers chase players holding Emerald Blocks");
      this.registerFeature(new SpongeDriesInNether(), "Wet sponge will dry in the nether");
      this.registerFeature(new QuickArmorSwapping());
      this.registerFeature(new AutomaticRecipeUnlock());
      this.registerFeature(new ImprovedSleeping());
      this.registerFeature(new AxesBreakLeaves());
      this.registerFeature(new CompassesWorkEverywhere());
      this.registerFeature(new BlastproofShulkerBoxes());
      this.registerFeature(new DyeAnyWool());
      this.registerFeature(new SquidsInkYou());
      this.registerFeature(new SpringySlime(), "Slime blocks bounce off sides");
      this.registerFeature(new PatTheDogs());
      this.registerFeature(new RightClickHarvest());
      this.registerFeature(new DirtToPath());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Items.IRON_PICKAXE);
   }
}
