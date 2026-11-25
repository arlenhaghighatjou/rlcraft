package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import c4.champions.common.init.ChampionsRegistry;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;

public class AffixScrapper extends AffixBase {
   public AffixScrapper() {
      super("scrapper", AffixCategory.OFFENSE);
   }

   public void onAttack(EntityLiving entity, IChampionship cap, EntityLivingBase target, DamageSource source, float amount, LivingAttackEvent evt) {
      if (!entity.world.isRemote && (double)entity.getRNG().nextFloat() < ConfigHandler.affix.scrapper.chance) {
         target.addPotionEffect(new PotionEffect(ChampionsRegistry.injured, 200, 0));
      }

   }

   public boolean canApply(EntityLiving entity) {
      return !(entity instanceof EntityCreeper);
   }
}
