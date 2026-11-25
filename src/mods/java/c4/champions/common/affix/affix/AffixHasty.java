package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;

public class AffixHasty extends AffixBase {
   public AffixHasty() {
      super("hasty", AffixCategory.OFFENSE);
   }

   public void onInitialSpawn(EntityLiving entity, IChampionship cap) {
      IAttributeInstance speed = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      speed.setBaseValue(speed.getBaseValue() + ConfigHandler.affix.hasty.movementBonus);
   }

   public boolean canApply(EntityLiving entity) {
      return entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED) != null;
   }
}
