package com.lycanitesmobs.core.entity.creature;

import com.lycanitesmobs.core.entity.BaseCreatureEntity;
import com.lycanitesmobs.core.entity.TameableCreatureEntity;
import com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal;
import java.util.HashMap;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EnumCreatureAttribute;
import net.minecraft.entity.monster.IMob;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EnumHand;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class EntityXaphan extends TameableCreatureEntity implements IMob {
   private int nextSplash = 20;

   public EntityXaphan(World world) {
      super(world);
      this.attribute = EnumCreatureAttribute.UNDEFINED;
      this.spawnsInWater = true;
      this.hasAttackSound = false;
      this.setupMob();
      this.stepHeight = 1.0F;
   }

   protected void initEntityAI() {
      super.initEntityAI();
      this.tasks.addTask(this.nextCombatGoalIndex++, (new AttackRangedGoal(this)).setSpeed(0.75D).setRange(14.0F).setMinChaseDistance(10.0F));
   }

   public void onLivingUpdate() {
      super.onLivingUpdate();
      if (!this.getEntityWorld().isRemote && this.updateTick % (long)this.nextSplash == 0L) {
         this.fireProjectile("acidsplash", (Entity)null, 0.0F, 0.0F, new Vec3d(0.5D - this.getRNG().nextDouble(), 0.0D, 0.5D - this.getRNG().nextDouble()), 0.0F, (float)this.nextSplash / 20.0F, 1.0F);
         this.nextSplash = 20 + this.getRNG().nextInt(20);
      }

   }

   public boolean isDamageEntityApplicable(Entity entity) {
      return entity instanceof EntityXaphan && this.getPlayerOwner() == ((EntityXaphan)entity).getPlayerOwner() ? false : super.isDamageEntityApplicable(entity);
   }

   public void attackRanged(Entity target, float range) {
      for(int row = -1; row <= 1; ++row) {
         int projectileCount = 10;

         for(int i = 0; i < projectileCount; ++i) {
            this.fireProjectile("acidsplash", target, range, (float)(90 / projectileCount * i), new Vec3d(0.0D, (double)(3 * row), 0.0D), 0.6F, 2.0F, 1.0F);
         }
      }

      super.attackRanged(target, range);
   }

   public boolean isFlying() {
      return true;
   }

   public boolean isStrongSwimmer() {
      return true;
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

   public boolean isDamageTypeApplicable(String type, DamageSource source, float damage) {
      return type.equals("acid") ? false : super.isDamageTypeApplicable(type, source, damage);
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

   public float getBrightness() {
      return 1.0F;
   }

   @SideOnly(Side.CLIENT)
   public int getBrightnessForRender() {
      return 15728880;
   }
}
