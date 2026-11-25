package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityJabberwock extends TameableCreatureEntity implements IMob {
   public EntityJabberwock(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.canGrow = false;
      this.babySpawnChance = 0.01D;
      this.setupMob();
   }

   protected void initEntityAI() {
      this.tasks.addTask(this.nextTravelGoalIndex++, new MoveVillageGoal(this));
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setTargetClass(EntityPlayer.class).setLongMemory(false));
      this.tasks.addTask(this.nextCombatGoalIndex++, new AttackMeleeGoal(this));
      if (this.getNavigator() instanceof PathNavigateGround) {
         PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
         pathNavigateGround.setBreakDoors(true);
      }

   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.onGround && !this.getEntityWorld().isRemote && this.hasAttackTarget() && this.rand.nextInt(10) == 0) {
         this.leap(6.0F, 0.1D, this.getAttackTarget());
      }

   }

   public float getFallResistance() {
      return 50.0F;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public ResourceLocation getTexture() {
      if (!"Rudolph".equals(this.getCustomNameTag())) {
         return super.getTexture();
      } else {
         String textureName = this.getTextureName() + "_rudolph";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      }
   }
}
