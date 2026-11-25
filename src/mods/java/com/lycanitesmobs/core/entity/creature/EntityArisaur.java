package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IGroupHeavy;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityArisaur extends AgeableCreatureEntity implements IGroupHeavy {
   public EntityArisaur(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.canGrow = true;
      this.babySpawnChance = 0.1D;
      this.fleeHealthPercent = 1.0F;
      this.isAggressiveByDefault = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public float getBlockPathWeight(int x, int y, int z) {
      if (this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z)).getBlock() != Blocks.AIR) {
         IBlockState blocState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
         if (blocState.getMaterial() == Material.GRASS) {
            return 10.0F;
         }

         if (blocState.getMaterial() == Material.GROUND) {
            return 7.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return true;
   }

   public ResourceLocation getTexture() {
      if (this.hasCustomName() && "Flowersaur".equals(this.getCustomNameTag())) {
         String textureName = this.getTextureName() + "_flowersaur";
         if (AssetManager.getTexture(textureName) == null) {
            AssetManager.addTexture(textureName, this.creatureInfo.modInfo, "textures/entity/" + textureName.toLowerCase() + ".png");
         }

         return AssetManager.getTexture(textureName);
      } else {
         return super.getTexture();
      }
   }
}
