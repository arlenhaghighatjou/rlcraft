package svenhjol.charm.brewing.potion;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.init.PotionTypes;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionHelper;
import net.minecraft.potion.PotionType;
import net.minecraft.util.ResourceLocation;
import svenhjol.charm.base.CharmPotion;
import svenhjol.charm.brewing.feature.Coffee;
import svenhjol.meson.registry.ProxyRegistry;

public class CoffeePotion extends CharmPotion {
   public static PotionType type;

   public CoffeePotion() {
      super("coffee", false, 1442306, 3);
      List<PotionEffect> effects = new ArrayList();
      effects.add(new PotionEffect(MobEffects.SPEED, Coffee.duration * 20));
      effects.add(new PotionEffect(MobEffects.HASTE, Coffee.duration * 20));
      effects.add(new PotionEffect(MobEffects.STRENGTH, Coffee.duration * 20));
      PotionEffect[] potionEffects = (PotionEffect[])effects.toArray(new PotionEffect[0]);
      type = (PotionType)(new PotionType(this.name, potionEffects)).setRegistryName(new ResourceLocation(this.getModId(), this.name));
      ProxyRegistry.register(type);
      ItemStack cocoa = new ItemStack(Items.DYE, 1, 3);
      PotionHelper.addMix(PotionTypes.WATER, Ingredient.fromStacks(new ItemStack[]{cocoa}), type);
   }

   public String getModId() {
      return "charm";
   }
}
