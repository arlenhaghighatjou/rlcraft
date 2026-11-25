package codersafterdark.reskillable.skill.attack;

import codersafterdark.reskillable.api.unlockable.Trait;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.entity.living.LivingDeathEvent;

public class TraitBattleSpirit extends Trait {
   public TraitBattleSpirit() {
      super(new ResourceLocation("reskillable", "battle_spirit"), 3, 2, new ResourceLocation("reskillable", "attack"), 6, "reskillable:attack|16", "reskillable:defense|16", "reskillable:agility|12");
   }

   public void onKillMob(LivingDeathEvent event) {
      if (!event.isCanceled() && event.getEntity() instanceof IMob && event.getSource().getTrueSource() instanceof EntityPlayer) {
         ((EntityPlayer)event.getSource().getTrueSource()).addPotionEffect(new PotionEffect(MobEffects.STRENGTH, 120, 0));
      }

   }
}
