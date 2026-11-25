package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public class AffixKnockback extends AffixBase {
   public AffixKnockback() {
      super("knockback", AffixCategory.CC);
   }

   public void onKnockback(EntityLiving entity, IChampionship cap, LivingKnockBackEvent evt) {
      evt.setStrength(evt.getStrength() * (float)ConfigHandler.affix.knockback.multiplier);
      evt.getEntityLiving().addPotionEffect(new PotionEffect(MobEffects.SLOWNESS, 100, 2));
   }
}
