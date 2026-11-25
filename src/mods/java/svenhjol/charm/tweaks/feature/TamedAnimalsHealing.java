package svenhjol.charm.tweaks.feature;

import net.minecraft.entity.passive.EntityTameable;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.world.World;
import svenhjol.meson.Feature;

public class TamedAnimalsHealing extends Feature {
   public TamedAnimalsHealing() {
   }

   public String getDescription() {
      return "Tame animals will heal within the range of a beacon with regeneration effect.";
   }

   public static void heal(World world, AxisAlignedBB aabb, Potion primaryEffect, Potion secondaryEffect, int duration, int amplifier) {
      if (primaryEffect == MobEffects.REGENERATION || secondaryEffect == MobEffects.REGENERATION) {
         for(EntityTameable animal : world.getEntitiesWithinAABB(EntityTameable.class, aabb)) {
            if (animal.isTamed()) {
               animal.addPotionEffect(new PotionEffect(MobEffects.REGENERATION, duration, amplifier, true, true));
            }
         }

      }
   }
}
