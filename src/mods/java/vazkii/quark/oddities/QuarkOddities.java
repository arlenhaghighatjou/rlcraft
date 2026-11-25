package vazkii.quark.oddities;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.oddities.feature.Backpacks;
import vazkii.quark.oddities.feature.MatrixEnchanting;
import vazkii.quark.oddities.feature.Pipes;
import vazkii.quark.oddities.feature.TinyPotato;
import vazkii.quark.oddities.feature.TotemOfHolding;

public class QuarkOddities extends Module {
   public QuarkOddities() {
   }

   public void addFeatures() {
      this.registerFeature(new Pipes());
      this.registerFeature(new Backpacks());
      this.registerFeature(new TotemOfHolding());
      this.registerFeature(new MatrixEnchanting());
      this.registerFeature(new TinyPotato());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Items.CHORUS_FRUIT);
   }
}
