package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.AgeableCreatureEntity;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import com.lycanitesmobs.core.entity.goals.actions.TemptGoal;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class EntityBobeko extends AgeableCreatureEntity {
   public EntityBobeko(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = false;
      this.fleeHealthPercent = 1.0F;
      this.isAggressiveByDefault = false;
      this.setupMob();
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextDistractionGoalIndex++, (new TemptGoal(this)).setIncludeDiet(true));
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackMeleeGoal(this)).setLongMemory(false));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && (this.ticksExisted % 10 == 0 || this.isMoving() && this.ticksExisted % 5 == 0)) {
         int trailHeight = 2;
         if (this.isChild()) {
            trailHeight = 1;
         }

         for(int y = 0; y < trailHeight; ++y) {
            Block block = this.getEntityWorld().getBlockState(this.getPosition().add(0, y, 0)).getBlock();
            if (block == Blocks.AIR || block == Blocks.SNOW || block == ObjectManager.getBlock("frostcloud")) {
               this.getEntityWorld().setBlockState(this.getPosition().add(0, y, 0), ObjectManager.getBlock("frostcloud").getDefaultState());
            }
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.SNOWBALL, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public float getBlockPathWeight(int x, int y, int z) {
      IBlockState blockState = this.getEntityWorld().getBlockState(new BlockPos(x, y - 1, z));
      Block block = blockState.getBlock();
      if (block != Blocks.AIR) {
         if (blockState.getMaterial() == Material.GRASS || blockState.getMaterial() == Material.SNOW) {
            return 10.0F;
         }

         if (blockState.getMaterial() == Material.GROUND || blockState.getMaterial() == Material.ICE) {
            return 7.0F;
         }
      }

      return super.getBlockPathWeight(x, y, z);
   }

   public boolean canBeLeashedTo(EntityPlayer player) {
      return true;
   }

   public int getNoBagSize() {
      return 0;
   }

   public int getBagSize() {
      return this.creatureInfo.bagSize;
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("ooze") ? false : super.isDamageTypeApplicable(type, source, damage);
   }

   public boolean isPotionApplicable(PotionEffect potionEffect) {
      if (potionEffect.getPotion() == MobEffects.SLOWNESS) {
         return false;
      } else {
         return potionEffect.getPotion() == MobEffects.HUNGER ? false : super.isPotionApplicable(potionEffect);
      }
   }

   public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      HashMap<Integer, String> commands = new HashMap();
      commands.putAll(super.getInteractCommands(player, hand, itemStack));
      if (itemStack != null && itemStack.getItem() == Items.BUCKET) {
         commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Milk");
      }

      return commands;
   }

   public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if (command.equals("Milk")) {
         this.replacePlayersItem(player, hand, itemStack, new ItemStack(Items.MILK_BUCKET));
         return true;
      } else {
         return super.performCommand(command, player, hand, itemStack);
      }
   }
}
