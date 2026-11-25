package xzeroair.trinkets.traits.abilities;

import net.minecraft.entity.EntityLivingBase;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.traits.AbilityHandler;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.lycanitesmobs.LycanitesCompat;

public class AbilityFireImmunity extends Ability implements ITickableAbility, IPotionAbility, IAttackAbility {
   protected int amplifier = 0;

   public AbilityFireImmunity() {
      super("fire_immunity");
   }

   public int getAmplifier() {
      return this.amplifier;
   }

   public AbilityFireImmunity setAmplifier(int amp) {
      if (this.amplifier != amp) {
         this.amplifier = amp;
      }

      return this;
   }

   public void tickAbility(EntityLivingBase entity) {
      if (entity.isBurning()) {
         entity.extinguish();
      }

      int amp = this.getAmplifier();
      if (Trinkets.FireResistanceTiers) {
         AbilityHandler.AbilityHolder holder = this.getAbilityHolder();
         if (holder != null) {
            if (holder.getSourceID().equalsIgnoreCase("xat:dragons_eye")) {
               amp = TrinketsConfig.SERVER.Items.DRAGON_EYE.compat.FRTiers.amplifier;
            } else if (holder.getSourceID().equalsIgnoreCase("xat:dragon")) {
               amp = TrinketsConfig.SERVER.races.dragon.compat.FRTiers.amplifier;
            }
         }
      }

      PotionEffect fireResist = new PotionEffect(MobEffects.FIRE_RESISTANCE, 3600, amp, false, false);
      entity.addPotionEffect(fireResist);
      if (entity.isPotionActive(MobEffects.FIRE_RESISTANCE) && entity.world.isRemote) {
         entity.getActivePotionEffect(MobEffects.FIRE_RESISTANCE).setPotionDurationMax(true);
      }

   }

   public void onAbilityRemoved(EntityLivingBase entity) {
      if (entity.isPotionActive(MobEffects.FIRE_RESISTANCE)) {
         entity.removePotionEffect(MobEffects.FIRE_RESISTANCE);
      }

   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();
      Potion smouldering = LycanitesCompat.getPotionEffectByName("smouldering");
      return smouldering != null && e.contentEquals(smouldering.getRegistryName().toString()) ? true : cancel;
   }

   public boolean attacked(EntityLivingBase attacked, DamageSource damage, float dmg, boolean cancel) {
      return damage.isFireDamage() ? true : cancel;
   }

   public float damaged(EntityLivingBase attacked, DamageSource source, float dmg) {
      if (Trinkets.FireResistanceTiers) {
         return dmg;
      } else {
         return source.isFireDamage() ? 0.0F : dmg;
      }
   }
}
