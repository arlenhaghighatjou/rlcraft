package bettercombat.mod.event;

import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraftforge.event.entity.player.PlayerEvent;

public class RLCombatSweepEvent extends PlayerEvent {
   private final Entity targetEntity;
   private final float baseDamage;
   private final boolean offhand;
   private final ItemStack stack;
   private final boolean doSweepIgnoreItemSword;
   private boolean doSweep;
   private float sweepModifier;
   private AxisAlignedBB sweepingAABB;
   private DamageSource sweepingDamageSource;

   public RLCombatSweepEvent(EntityPlayer player, Entity targetEntity, float baseDamage, boolean offhand, ItemStack stack, boolean doSweepIgnoreItemSword, boolean doSweep, float sweepModifier, AxisAlignedBB sweepingAABB, DamageSource sweepingDamageSource) {
      super(player);
      this.targetEntity = targetEntity;
      this.baseDamage = baseDamage;
      this.offhand = offhand;
      this.stack = stack;
      this.doSweepIgnoreItemSword = doSweepIgnoreItemSword;
      this.doSweep = doSweep;
      this.sweepModifier = sweepModifier;
      this.sweepingAABB = sweepingAABB;
      this.sweepingDamageSource = sweepingDamageSource;
   }

   public Entity getTargetEntity() {
      return this.targetEntity;
   }

   public float getBaseDamage() {
      return this.baseDamage;
   }

   public boolean getOffhand() {
      return this.offhand;
   }

   public ItemStack getItemStack() {
      return this.stack;
   }

   public boolean getDoSweepIgnoreItemSword() {
      return this.doSweepIgnoreItemSword;
   }

   public boolean getDoSweep() {
      return this.doSweep;
   }

   public void setDoSweep(boolean doSweep) {
      this.doSweep = doSweep;
   }

   public float getSweepModifier() {
      return this.sweepModifier;
   }

   public void setSweepModifier(float sweepModifier) {
      this.sweepModifier = sweepModifier;
   }

   public AxisAlignedBB getSweepingAABB() {
      return this.sweepingAABB;
   }

   public void setSweepingAABB(AxisAlignedBB sweepingAABB) {
      this.sweepingAABB = sweepingAABB;
   }

   public DamageSource getSweepingDamageSource() {
      return this.sweepingDamageSource;
   }

   public void setSweepingDamageSource(DamageSource sweepingDamageSource) {
      this.sweepingDamageSource = sweepingDamageSource;
   }
}
