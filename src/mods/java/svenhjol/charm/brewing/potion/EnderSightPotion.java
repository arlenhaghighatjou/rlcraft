package svenhjol.charm.brewing.potion;

import net.minecraft.init.Items;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmPotion;
import svenhjol.charm.brewing.feature.EnderSight;
import svenhjol.meson.registry.ProxyRegistry;

public class EnderSightPotion extends CharmPotion {
   public static PotionEffect effect;
   public static PotionType type;

   public EnderSightPotion() {
      super("ender_sight", false, 7708951, 1);
      effect = new PotionEffect(this, EnderSight.duration * 20);
      type = (PotionType)(new PotionType(this.name, new PotionEffect[]{effect})).setRegistryName(new ResourceLocation(this.getModId(), this.name));
      ProxyRegistry.register(type);
      ItemStack eye = new ItemStack(Items.ENDER_EYE);
      PotionHelper.addMix(PotionTypes.NIGHT_VISION, Ingredient.fromStacks(new ItemStack[]{eye}), type);
   }

   public String getModId() {
      return "charm";
   }
}
