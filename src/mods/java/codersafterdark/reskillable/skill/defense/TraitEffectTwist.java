package codersafterdark.reskillable.skill.defense;

import codersafterdark.reskillable.api.unlockable.Trait;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.IMob;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingHurtEvent;

public class TraitEffectTwist extends Trait {
   private Map<Potion, Potion> badPotions = new HashMap();

   public TraitEffectTwist() {
      super(new ResourceLocation("reskillable", "effect_twist"), 3, 1, new ResourceLocation("reskillable", "defense"), 8, "reskillable:defense|20", "reskillable:attack|16", "reskillable:magic|16");
      this.badPotions.put(MobEffects.SPEED, MobEffects.SLOWNESS);
      this.badPotions.put(MobEffects.HASTE, MobEffects.MINING_FATIGUE);
      this.badPotions.put(MobEffects.STRENGTH, MobEffects.WEAKNESS);
      this.badPotions.put(MobEffects.REGENERATION, MobEffects.POISON);
      this.badPotions.put(MobEffects.NIGHT_VISION, MobEffects.BLINDNESS);
      this.badPotions.put(MobEffects.LUCK, MobEffects.UNLUCK);
   }

   public void onHurt(LivingHurtEvent event) {
      if (!event.isCanceled()) {
         Entity src = event.getSource().getTrueSource();
         if (src instanceof EntityLivingBase && src instanceof IMob && src.world.rand.nextBoolean()) {
            List<PotionEffect> effects = (List)event.getEntityLiving().getActivePotionEffects().stream().filter((p) -> {
               return this.badPotions.containsKey(p.getPotion());
            }).collect(Collectors.toList());
            if (effects.size() > 0) {
               PotionEffect target = (PotionEffect)effects.get(src.world.rand.nextInt(effects.size()));
               PotionEffect newEff = new PotionEffect((Potion)this.badPotions.get(target.getPotion()), 80 + src.world.rand.nextInt(60), 0);
               ((EntityLivingBase)src).addPotionEffect(newEff);
            }
         }

      }
   }
}
