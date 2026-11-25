package vazkii.quark.world.entity;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.monster.EntityZombie;
import net.minecraft.init.MobEffects;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;
import vazkii.quark.world.feature.DepthMobs;

public class EntityDweller extends EntityZombie {
   public EntityDweller(World worldIn) {
      super(worldIn);
   }

   protected void applyEntityAttributes() {
      super.applyEntityAttributes();
      this.getEntityAttribute(SPAWN_REINFORCEMENTS_CHANCE).setBaseValue(0.0D);
      this.getEntityAttribute(SharedMonsterAttributes.ARMOR).setBaseValue(3.0D);
   }

   protected void setEquipmentBasedOnDifficulty(@Nonnull DifficultyInstance difficulty) {
   }

   public IEntityLivingData onInitialSpawn(DifficultyInstance difficulty, @Nullable IEntityLivingData data) {
      this.addPotionEffect(new PotionEffect(MobEffects.INVISIBILITY, Integer.MAX_VALUE));
      return super.onInitialSpawn(difficulty, data);
   }

   public boolean attackEntityFrom(@Nonnull DamageSource source, float amount) {
      if (amount > 0.0F) {
         this.removePotionEffect(MobEffects.INVISIBILITY);
      }

      return super.attackEntityFrom(source, amount);
   }

   public boolean attackEntityAsMob(Entity entityIn) {
      this.removePotionEffect(MobEffects.INVISIBILITY);
      return super.attackEntityAsMob(entityIn);
   }

   public boolean getCanSpawnHere() {
      return super.getCanSpawnHere() && this.posY < (double)DepthMobs.upperBound;
   }
}
