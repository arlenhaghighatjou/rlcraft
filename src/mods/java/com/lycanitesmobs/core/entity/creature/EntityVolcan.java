package com.lycanitesmobs.core.entity.creature;

import com.google.common.base.Predicate;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal;
import java.util.HashMap;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.EntitySilverfish;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.world.World;

public class EntityVolcan extends TameableCreatureEntity implements IMob {
   public int blockMeltingRadius = 2;

   public EntityVolcan(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.hasAttackSound = true;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(2, (new AttackMeleeGoal(this)).setLongMemory(true));
   }

   public void loadCreatureFlags() {
      this.blockMeltingRadius = this.creatureInfo.getFlag("blockMeltingRadius", this.blockMeltingRadius);
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.updateTick % 40L == 0L) {
         for(Object entityObj : this.getNearbyEntities(EntityLivingBase.class, (Predicate)null, 4.0D)) {
            EntityLivingBase target = (EntityLivingBase)entityObj;
            if (target != this && this.canAttackClass(target.getClass()) && this.canAttackEntity(target) && this.getEntitySenses().canSee(target)) {
               target.setFire(2);
            }
         }
      }

      if (!this.getEntityWorld().isRemote && this.updateTick % 40L == 0L && this.blockMeltingRadius > 0 && !this.isTamed() && this.getEntityWorld().getGameRules().getBoolean("mobGriefing")) {
         int range = this.blockMeltingRadius;

         for(int w = -((int)Math.ceil((double)this.width) + range); (double)w <= Math.ceil((double)this.width) + (double)range; ++w) {
            for(int d = -((int)Math.ceil((double)this.width) + range); (double)d <= Math.ceil((double)this.width) + (double)range; ++d) {
               for(int h = -((int)Math.ceil((double)this.height) + range); (double)h <= Math.ceil((double)this.height); ++h) {
                  Block block = this.getEntityWorld().getBlockState(this.getPosition().add(w, h, d)).getBlock();
                  if (block == Blocks.COBBLESTONE || block == Blocks.GRAVEL) {
                     IBlockState blockState = Blocks.FLOWING_LAVA.getStateFromMeta(11);
                     this.getEntityWorld().setBlockState(this.getPosition().add(w, h, d), blockState);
                  }
               }
            }
         }
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.DRIP_LAVA, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }

         if (this.ticksExisted % 10 == 0) {
            for(int i = 0; i < 2; ++i) {
               this.getEntityWorld().spawnParticle(EnumParticleTypes.FLAME, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            }
         }
      }

   }

   public boolean attackMelee(Entity target, double damageScale) {
      if (!super.attackMelee(target, damageScale)) {
         return false;
      } else {
         if (target instanceof EntitySilverfish) {
            target.setDead();
         }

         return true;
      }
   }

   public boolean isFlying() {
      return true;
   }

   public HashMap<Integer, String> getInteractCommands(EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      HashMap<Integer, String> commands = new HashMap();
      commands.putAll(super.getInteractCommands(player, hand, itemStack));
      if (itemStack != null && itemStack.getItem() == Items.BUCKET && this.isTamed()) {
         commands.put(Integer.valueOf(BaseCreatureEntity.COMMAND_PIORITIES.ITEM_USE.id), "Water");
      }

      return commands;
   }

   public boolean performCommand(String command, EntityPlayer player, EnumHand hand, ItemStack itemStack) {
      if (command.equals("Water")) {
         this.replacePlayersItem(player, hand, itemStack, new ItemStack(Items.LAVA_BUCKET));
         return true;
      } else {
         return super.performCommand(command, player, hand, itemStack);
      }
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

   public float getDamageModifier(DamageSource damageSrc) {
      return damageSrc.isFireDamage() ? 0.0F : super.getDamageModifier(damageSrc);
   }

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return !type.equals("cactus") && !type.equals("inWall") ? super.isDamageTypeApplicable(type, source, damage) : false;
   }

   public boolean canBurn() {
      return false;
   }

   public boolean waterDamage() {
      return true;
   }
}
