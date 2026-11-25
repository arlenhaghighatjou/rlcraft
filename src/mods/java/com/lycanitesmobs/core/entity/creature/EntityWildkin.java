package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.pathfinding.PathNavigateGround;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;

public class EntityWildkin extends TameableCreatureEntity implements IMob {
   public EntityWildkin(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.canGrow = true;
      this.babySpawnChance = 0.01D;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, new BreakDoorGoal(this));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
      if (this.getNavigator() instanceof PathNavigateGround) {
         PathNavigateGround pathNavigateGround = (PathNavigateGround)this.getNavigator();
         pathNavigateGround.setBreakDoors(true);
      }

   }

   public double getFallingMod() {
      return 0.8D;
   }

   public float getFallResistance() {
      return 100.0F;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean petControlsEnabled() {
      return true;
   }

   public ResourceLocation getTexture() {
      if (!"Gooderness".equals(this.getCustomNameTag())) {
         return super.getTexture();
      } else {
         String textureName = this.getTextureName() + "_gooderness";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      }
   }
}
