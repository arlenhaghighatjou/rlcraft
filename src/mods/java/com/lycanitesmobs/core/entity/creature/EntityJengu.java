package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.api.IFusable;
import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import com.lycanitesmobs.core.info.CreatureManager;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.EnumHand;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class EntityJengu extends TameableCreatureEntity implements IMob, IFusable {
   protected IFusable fusionTarget;

   public EntityJengu(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(5.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 2; ++i) {
            this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_DROP, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.WATER_BUBBLE, this.posX + (this.rand.nextDouble() - 0.5D) * (double)this.width, this.posY + this.rand.nextDouble() * (double)this.height, this.posZ + (this.rand.nextDouble() - 0.5D) * (double)this.width, 0.0D, 0.0D, 0.0D, new int[0]);
         }
      }

   }

   public void attackRanged(Entity target, float range) {
      this.fireProjectile("aquapulse", target, range, 0.0F, new Vec3d(0.0D, 0.0D, 0.0D), 0.6F, 2.0F, 1.0F);
      super.attackRanged(target, range);
   }

   public float getAISpeedModifier() {
      if (this.isInWater()) {
         return 1.0F;
      } else {
         return this.waterContact() ? 0.75F : 1.0F;
      }
   }

   public boolean isFlying() {
      return true;
   }

   public boolean isStrongSwimmer() {
      return true;
   }

   public boolean isPushedByWater() {
      return false;
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

   public boolean canBreatheUnderwater() {
      return true;
   }

   public boolean canBurn() {
      return false;
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
         this.replacePlayersItem(player, hand, itemStack, new ItemStack(Items.WATER_BUCKET));
         return true;
      } else {
         return super.performCommand(command, player, hand, itemStack);
      }
   }

   public IFusable getFusionTarget() {
      return this.fusionTarget;
   }

   public void setFusionTarget(IFusable fusionTarget) {
      this.fusionTarget = fusionTarget;
   }

   public Class getFusionClass(IFusable fusable) {
      if (fusable instanceof EntityCinder) {
         return CreatureManager.getInstance().getEntityClass("xaphan");
      } else if (fusable instanceof EntityGeonach) {
         return CreatureManager.getInstance().getEntityClass("spriggan");
      } else if (fusable instanceof EntityDjinn) {
         return CreatureManager.getInstance().getEntityClass("reiver");
      } else if (fusable instanceof EntityAegis) {
         return CreatureManager.getInstance().getEntityClass("nymph");
      } else {
         return fusable instanceof EntityArgus ? CreatureManager.getInstance().getEntityClass("eechetik") : null;
      }
   }
}
