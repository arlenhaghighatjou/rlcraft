package com.jamieswhiteshirt.trumpetskeleton.common.entity;

import com.jamieswhiteshirt.trumpetskeleton.TrumpetSkeleton;
import com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonItems;
import com.jamieswhiteshirt.trumpetskeleton.common.TrumpetSkeletonSoundEvents;
import javax.annotation.Nullable;
import net.minecraft.entity.monster.AbstractSkeleton;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundEvent;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.World;

public class EntityTrumpetSkeleton extends AbstractSkeleton {
   public EntityTrumpetSkeleton(World worldIn) {
      super(worldIn);
   }

   @Nullable
   protected ResourceLocation getLootTable() {
      return TrumpetSkeleton.ENTITIES_TRUMPET_SKELETON_LOOT_TABLE;
   }

   protected SoundEvent getAmbientSound() {
      return TrumpetSkeletonSoundEvents.ENTITY_TRUMPET_SKELETON_AMBIENT;
   }

   protected SoundEvent getHurtSound(DamageSource damageSourceIn) {
      return SoundEvents.ENTITY_SKELETON_HURT;
   }

   protected SoundEvent getDeathSound() {
      return SoundEvents.ENTITY_SKELETON_DEATH;
   }

   protected SoundEvent getStepSound() {
      return SoundEvents.ENTITY_SKELETON_STEP;
   }

   protected void setEquipmentBasedOnDifficulty(DifficultyInstance difficulty) {
      super.setEquipmentBasedOnDifficulty(difficulty);
      this.setItemStackToSlot(EntityEquipmentSlot.MAINHAND, new ItemStack(TrumpetSkeletonItems.TRUMPET));
   }

   public void playLivingSound() {
      super.playLivingSound();
      if (this.isSwingingArms()) {
         TrumpetSkeleton.scare(this.world, this);
      }

   }
}
