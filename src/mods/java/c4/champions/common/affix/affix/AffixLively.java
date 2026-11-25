package c4.champions.common.affix.affix;

import c4.champions.common.affix.core.AffixBase;
import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.affix.core.AffixNBT;
import c4.champions.common.capability.IChampionship;
import c4.champions.common.config.ConfigHandler;
import net.minecraft.entity.EntityLiving;
import net.minecraft.util.DamageSource;

public class AffixLively extends AffixBase {
   public AffixLively() {
      super("lively", AffixCategory.DEFENSE);
   }

   public void onSpawn(EntityLiving entity, IChampionship cap) {
      super.onSpawn(entity, cap);
   }

   public void onUpdate(EntityLiving entity, IChampionship cap) {
      if (!entity.world.isRemote) {
         AffixNBT.Integer lastAttackTime = (AffixNBT.Integer)AffixNBT.getData(cap, this.getIdentifier(), AffixNBT.Integer.class);
         if ((long)(lastAttackTime.num + ConfigHandler.affix.lively.cooldown * 20) < entity.world.getTotalWorldTime() && entity.ticksExisted % 20 == 0) {
            double healAmount = ConfigHandler.affix.lively.healAmount;
            if (entity.getAttackTarget() == null) {
               healAmount *= ConfigHandler.affix.lively.passiveMultiplier;
            }

            entity.heal((float)healAmount);
         }
      }

   }

   public float onDamaged(EntityLiving entity, IChampionship cap, DamageSource source, float amount, float newAmount) {
      AffixNBT.Integer lastAttackTime = (AffixNBT.Integer)AffixNBT.getData(cap, this.getIdentifier(), AffixNBT.Integer.class);
      lastAttackTime.num = (int)entity.world.getTotalWorldTime();
      lastAttackTime.saveData(entity);
      return super.onDamaged(entity, cap, source, amount, newAmount);
   }
}
