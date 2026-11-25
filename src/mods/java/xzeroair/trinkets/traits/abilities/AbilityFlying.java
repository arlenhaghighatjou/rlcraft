package xzeroair.trinkets.traits.abilities;

import java.util.function.BiFunction;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.BlockPos;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.traits.abilities.interfaces.IMiningAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.handlers.Counter;

public class AbilityFlying extends Ability implements ITickableAbility, IPotionAbility, IMiningAbility {
   protected boolean flightEnabled = false;
   protected boolean speedModified = false;
   protected float speed = 0.0F;
   protected float cost = 0.0F;

   public AbilityFlying() {
      super("creative_flight");
   }

   public AbilityFlying setFlightEnabled(boolean enabled) {
      this.flightEnabled = enabled;
      return this;
   }

   public AbilityFlying setSpeedEnabled(boolean enabled) {
      this.speedModified = enabled;
      return this;
   }

   public AbilityFlying setFlightSpeed(float speed) {
      this.speed = speed;
      return this;
   }

   public AbilityFlying setFlightCost(float cost) {
      this.cost = cost;
      return this;
   }

   public void tickAbility(EntityLivingBase entity) {
      boolean flag = ((Boolean)Capabilities.getEntityProperties(entity, Boolean.valueOf(true), (prop, canFly) -> {
         return prop.getRaceHandler().canFly();
      })).booleanValue();
      if (this.flightEnabled && flag) {
         this.addFlyingAbility(entity);
      } else {
         this.removeFlyingAbility(entity);
      }

   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      return effect.getPotion().getRegistryName().toString().contentEquals("minecraft:levitation") ? true : cancel;
   }

   public float breakingBlock(EntityLivingBase entity, IBlockState state, BlockPos pos, float originalSpeed, float newSpeed) {
      if (!entity.isInsideOfMaterial(Material.WATER)) {
         float speed = originalSpeed;
         if (!entity.onGround) {
            speed = originalSpeed * 5.0F;
         }

         if (newSpeed < speed) {
            return speed;
         }
      }

      return newSpeed;
   }

   protected void setFlyingSpeed(EntityLivingBase entity, float flightSpeed) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (!this.isCreativePlayer(player)) {
            if (this.speedModified && player.world.isRemote && player.capabilities.getFlySpeed() != flightSpeed && flightSpeed > 0.0F && flightSpeed != Float.NaN) {
               player.capabilities.setFlySpeed(flightSpeed);
               player.sendPlayerAbilities();
            }
         } else if (player.world.isRemote && player.capabilities.getFlySpeed() == flightSpeed) {
            player.capabilities.setFlySpeed(0.05F);
            player.sendPlayerAbilities();
         }
      }

   }

   protected void addFlyingAbility(EntityLivingBase entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (!this.isCreativePlayer(player)) {
            MagicStats magic = Capabilities.getMagicStats(entity);
            if (this.cost > 0.0F && magic != null) {
               boolean flying = player.capabilities.isFlying;
               float mp = magic.getMana();
               if (mp >= this.cost) {
                  if (!player.capabilities.allowFlying) {
                     player.capabilities.allowFlying = true;
                     this.setFlyingSpeed(entity, this.speed);
                     if (player instanceof EntityPlayerMP) {
                        player.sendPlayerAbilities();
                     }
                  }
               } else if (flying) {
                  this.removeFlyingAbility(player);
               }

               if (player.capabilities.isFlying) {
                  this.setFlyingSpeed(entity, this.speed);
                  player.fallDistance = 0.0F;
                  Counter counter = this.tickHandler.getCounter("fly_timer", 20, true, true, true, true);
                  if (counter != null && !player.isRiding() && counter.Tick() && !magic.spendMana(this.cost)) {
                     this.removeFlyingAbility(player);
                  }
               }
            } else {
               if (!player.capabilities.allowFlying) {
                  player.capabilities.allowFlying = true;
                  if (player instanceof EntityPlayerMP) {
                     player.sendPlayerAbilities();
                  }
               }

               if (player.capabilities.isFlying) {
                  player.fallDistance = 0.0F;
               }
            }
         }
      }

   }

   protected void removeFlyingAbility(EntityLivingBase entity) {
      if (entity instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)entity;
         if (!this.isCreativePlayer(player) && player.capabilities.allowFlying) {
            player.capabilities.allowFlying = false;
            player.capabilities.isFlying = false;
            if (player instanceof EntityPlayerMP) {
               player.sendPlayerAbilities();
            }

            if (player.world.isRemote && player.capabilities.getFlySpeed() != 0.05F) {
               player.capabilities.setFlySpeed(0.05F);
               player.sendPlayerAbilities();
            }

            player.fallDistance = 0.0F;
         }
      }

   }

   public void onAbilityAdded(EntityLivingBase entity) {
      this.addFlyingAbility(entity);
   }

   public void onAbilityRemoved(EntityLivingBase entity) {
      this.removeFlyingAbility(entity);
      this.tickHandler.removeCounter("fly_timer");
   }
}
