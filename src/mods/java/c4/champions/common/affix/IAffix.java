package c4.champions.common.affix;

import c4.champions.common.affix.core.AffixCategory;
import c4.champions.common.capability.IChampionship;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.DamageSource;
import net.minecraftforge.event.entity.living.LivingAttackEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingKnockBackEvent;

public interface IAffix {
   String getIdentifier();

   AffixCategory getCategory();

   void onInitialSpawn(EntityLiving var1, IChampionship var2);

   void onSpawn(EntityLiving var1, IChampionship var2);

   void onUpdate(EntityLiving var1, IChampionship var2);

   void onAttack(EntityLiving var1, IChampionship var2, EntityLivingBase var3, DamageSource var4, float var5, LivingAttackEvent var6);

   void onAttacked(EntityLiving var1, IChampionship var2, DamageSource var3, float var4, LivingAttackEvent var5);

   float onHurt(EntityLiving var1, IChampionship var2, DamageSource var3, float var4, float var5);

   float onHealed(EntityLiving var1, IChampionship var2, float var3, float var4);

   float onDamaged(EntityLiving var1, IChampionship var2, DamageSource var3, float var4, float var5);

   void onDeath(EntityLiving var1, IChampionship var2, DamageSource var3, LivingDeathEvent var4);

   void onKnockback(EntityLiving var1, IChampionship var2, LivingKnockBackEvent var3);

   boolean canApply(EntityLiving var1);

   boolean isCompatibleWith(IAffix var1);

   int getTier();
}
