package vazkii.quark.vanity;

import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import vazkii.quark.base.module.Module;
import vazkii.quark.vanity.feature.BoatSails;
import vazkii.quark.vanity.feature.DyableElytra;
import vazkii.quark.vanity.feature.DyeItemNames;
import vazkii.quark.vanity.feature.EmoteSystem;
import vazkii.quark.vanity.feature.FireworkCloning;
import vazkii.quark.vanity.feature.SitInStairs;
import vazkii.quark.vanity.feature.WitchHat;

public class QuarkVanity extends Module {
   public QuarkVanity() {
   }

   public void addFeatures() {
      this.registerFeature(new DyableElytra());
      this.registerFeature(new FireworkCloning());
      this.registerFeature(new EmoteSystem());
      this.registerFeature(new SitInStairs());
      this.registerFeature(new WitchHat());
      this.registerFeature(new BoatSails());
      this.registerFeature(new DyeItemNames());
   }

   public ItemStack getIconStack() {
      return new ItemStack(Items.LEATHER_HELMET);
   }
}
