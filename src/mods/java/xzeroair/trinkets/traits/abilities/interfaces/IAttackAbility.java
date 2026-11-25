package xzeroair.trinkets.traits.abilities.interfaces;

import java.util.List;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;

public interface IAttackAbility extends IAbilityInterface {
   default void targetedByEnemy(EntityLivingBase enemy) {
   }

   default boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      return cancel;
   }

   default float hurt(EntityLivingBase attacked, DamageSource source, float dmg) {
      return dmg;
   }

   default float damaged(EntityLivingBase attacked, DamageSource source, float dmg) {
      return dmg;
   }

   default boolean died(EntityLivingBase attacked, DamageSource source, boolean cancel) {
      return cancel;
   }

   default boolean attackEntity(EntityLivingBase target, DamageSource source, float dmg, boolean cancel) {
      return cancel;
   }

   default float hurtEntity(EntityLivingBase target, DamageSource source, float dmg) {
      return dmg;
   }

   default float damageEntity(EntityLivingBase target, DamageSource source, float dmg) {
      return dmg;
   }

   default void killedEntity(EntityLivingBase target, DamageSource source) {
   }

   default int killedEntityExpDrop(EntityLivingBase target, int originalExp, int droppedExp) {
      return droppedExp;
   }

   default void killedEntityItemDrops(EntityLivingBase target, DamageSource source, int lootingLevel, List<EntityItem> drops) {
   }

   default boolean isIndirectDamage(DamageSource source) {
      return source instanceof EntityDamageSourceIndirect || source.isExplosion() || source.isMagicDamage() || source.isProjectile();
   }
}
