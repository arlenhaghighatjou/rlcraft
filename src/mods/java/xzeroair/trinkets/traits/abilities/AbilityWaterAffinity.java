package xzeroair.trinkets.traits.abilities;

import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.SharedMonsterAttributes;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.init.Blocks;
import net.minecraft.init.Enchantments;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.traits.abilities.interfaces.IMiningAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ClientConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigSeaStone;

public class AbilityWaterAffinity extends Ability implements ITickableAbility, IMiningAbility {
   public static final ConfigSeaStone serverConfig;
   public static final ClientConfig.ClientConfigItems.ClientConfigSeaStone clientConfig;

   public AbilityWaterAffinity() {
      super("water_affinity");
   }

   public void tickAbility(EntityLivingBase entity) {
      if (serverConfig.underwater_breathing) {
         if (!serverConfig.always_full) {
            if (entity.getAir() < 20) {
               entity.setAir(20);
            }
         } else {
            entity.setAir(300);
         }

         if (Trinkets.BetterDiving) {
            entity.addPotionEffect(new PotionEffect(MobEffects.WATER_BREATHING, 150, 0, false, false));
         }
      }

      if (!Trinkets.BetterDiving && serverConfig.Swim_Tweaks && !this.isSpectator(entity) && !this.isCreativeFlying(entity)) {
         BlockPos head = entity.getPosition();
         IBlockState headBlock = entity.world.getBlockState(head);
         Block block = headBlock.getBlock();
         if ((entity.isInWater() || entity.isInLava()) && block != Blocks.AIR) {
            if (serverConfig.old_tweaks) {
               this.handleMovementOld(entity);
            } else {
               this.handleMovement(entity);
            }
         }
      }

   }

   public float breakingBlock(EntityLivingBase entity, IBlockState state, BlockPos pos, float originalSpeed, float newSpeed) {
      if (TrinketsConfig.SERVER.Items.SEA_STONE.Swim_Tweaks && entity.isInsideOfMaterial(Material.WATER) && !EnchantmentHelper.getAquaAffinityModifier(entity)) {
         float speed = originalSpeed * 5.0F;
         if (!entity.onGround) {
            speed *= 5.0F;
         }

         if (newSpeed < speed) {
            return speed;
         }
      }

      return newSpeed;
   }

   protected void handleMovementOld(EntityLivingBase entity) {
      double motion = 0.1D;
      double bouyance = 0.25D;
      if (entity.isInLava()) {
         motion = 0.09D;
      }

      if (!entity.isSneaking()) {
         entity.motionY = 0.0D;
         if (this.movingForward(entity, entity.getHorizontalFacing()) && (entity.motionX > motion || entity.motionX < -motion || entity.motionZ > motion || entity.motionZ < -motion)) {
            entity.motionY += MathHelper.clamp(entity.getLookVec().y / 1.0D, -0.25D, 0.25D);
         }
      } else if (!this.movingForward(entity, entity.getHorizontalFacing()) && entity.motionY <= 0.0D) {
         if (entity.isInLava()) {
            entity.motionY *= 1.75D;
         } else {
            entity.motionY *= 1.25D;
         }
      }

   }

   protected double getSwimSpeedFromPlayer(EntityLivingBase entity) {
      IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      IAttributeInstance swimMovement = entity.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
      double swimMulti = swimMovement != null ? swimMovement.getAttributeValue() * 0.2D : 1.0D;
      double movementSpeed = movement != null ? movement.getBaseValue() : 0.1D;
      double swimSpeedBase = movementSpeed * Math.max(swimMulti, 0.0D);
      ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
      double min = swimSpeedBase * 0.01D;
      double max = swimSpeedBase * 1.0D;
      double speed = MathHelper.clamp(swimSpeedBase, min, max);
      double depthStriderLevel = (double)EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, feet);
      if (depthStriderLevel > 0.0D) {
         speed += swimSpeedBase * 0.667D * depthStriderLevel;
      }

      return speed;
   }

   protected double calculateSlow(EntityLivingBase entity) {
      IAttributeInstance movement = entity.getEntityAttribute(SharedMonsterAttributes.MOVEMENT_SPEED);
      IAttributeInstance swimMovement = entity.getEntityAttribute(EntityLivingBase.SWIM_SPEED);
      double swimMulti = swimMovement != null ? swimMovement.getAttributeValue() : 1.0D;
      double movementSpeed = movement != null ? movement.getBaseValue() : 0.1D;
      double swimSpeedBase = movementSpeed * Math.max(swimMulti, 0.0D);
      double slow = swimSpeedBase * 0.2D;
      ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
      double depthStrider = (double)EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, feet);
      if (depthStrider > 0.0D) {
         if (depthStrider > 3.0D) {
            depthStrider = 3.0D;
         }

         if (!entity.onGround) {
            depthStrider *= 0.5D;
         } else if (entity.isActiveItemStackBlocking()) {
            depthStrider *= 0.02D;
         }

         double striderSlow = (swimSpeedBase - slow) * depthStrider / 3.0D;
         slow += striderSlow;
      }

      return slow;
   }

   protected void handleMovement(EntityLivingBase entity) {
      if (!entity.world.isRemote) {
         entity.motionY += 0.02D;
      } else {
         GameSettings settings = Minecraft.getMinecraft().gameSettings;
         boolean inputForward = settings.keyBindForward.isKeyDown();
         boolean inputBack = settings.keyBindBack.isKeyDown();
         boolean inputRight = settings.keyBindRight.isKeyDown();
         boolean inputLeft = settings.keyBindLeft.isKeyDown();
         boolean inputUp = settings.keyBindJump.isKeyDown();
         boolean inputDown = settings.keyBindSneak.isKeyDown();
         float rotationPitch = entity.rotationPitch;
         float rotationYaw = entity.rotationYaw;
         double slow = this.calculateSlow(entity);
         double speed = this.getSwimSpeedFromPlayer(entity);
         double strafe = 0.0D;
         double forward = 0.0D;
         double up = 0.0D;
         if (inputForward) {
            ++forward;
         }

         if (inputBack) {
            --forward;
         }

         if (inputRight) {
            --strafe;
         }

         if (inputLeft) {
            ++strafe;
         }

         if (inputUp) {
            ++up;
         }

         if (inputDown) {
            --up;
         }

         if (inputDown) {
            slow *= 0.3D;
         }

         entity.motionY += 0.02D;
         if (inputForward != inputBack || inputRight != inputLeft) {
            this.move2D(entity, strafe, forward, -slow, (double)rotationYaw);
         }

         if (inputForward != inputBack || inputRight != inputLeft || inputUp != inputDown) {
            if (inputForward && !inputBack) {
               if (inputUp && !inputDown) {
                  rotationPitch = (rotationPitch - 90.0F) / 2.0F;
                  up = 0.0D;
               } else if (inputDown && !inputUp) {
                  rotationPitch = (rotationPitch + 90.0F) / 2.0F;
                  up = 0.0D;
               }
            } else if (inputBack && !inputForward) {
               if (inputUp && !inputDown) {
                  rotationPitch = (rotationPitch + 90.0F) / 2.0F;
                  up = 0.0D;
               } else if (inputDown && !inputUp) {
                  rotationPitch = (rotationPitch - 90.0F) / 2.0F;
                  up = 0.0D;
               }
            }

            this.move3D(entity, strafe, up, forward, speed, (double)rotationYaw, (double)rotationPitch);
            ItemStack feet = entity.getItemStackFromSlot(EntityEquipmentSlot.FEET);
            int depthStriderLevel = EnchantmentHelper.getEnchantmentLevel(Enchantments.DEPTH_STRIDER, feet);
            if (inputUp) {
               if (depthStriderLevel == 0) {
                  entity.motionY *= 0.6D;
               } else {
                  entity.motionY *= 0.772D;
               }
            }

            if (inputDown && depthStriderLevel != 0) {
               entity.motionY *= 0.936D;
            }
         }
      }

   }

   protected void move2D(Entity entity, double strafe, double forward, double speed, double yaw) {
      double d = strafe * strafe + forward * forward;
      if (d >= 1.0E-4D) {
         d = Math.sqrt(d);
         if (d < 1.0D) {
            d = 1.0D;
         }

         d = speed / d;
         strafe = strafe * d;
         forward = forward * d;
         double d1 = Math.sin(yaw * 0.017453292D);
         double d2 = Math.cos(yaw * 0.017453292D);
         entity.motionX += strafe * d2 - forward * d1;
         entity.motionZ += forward * d2 + strafe * d1;
      }

   }

   protected void move3D(EntityLivingBase entity, double strafe, double up, double forward, double speed, double yaw, double pitch) {
      double d = strafe * strafe + up * up + forward * forward;
      if (d >= 1.0E-4D) {
         d = Math.sqrt(d);
         if (d < 1.0D) {
            d = 1.0D;
         }

         d = speed / d;
         strafe = strafe * d;
         up = up * d;
         forward = forward * d;
         double d1 = Math.sin(yaw * 0.017453292D);
         double d2 = Math.cos(yaw * 0.017453292D);
         double d3 = Math.sin(pitch * 0.017453292D);
         double d4 = Math.cos(pitch * 0.017453292D);
         double upMotion = up - forward * d3;
         entity.motionX += strafe * d2 - forward * d1 * d4;
         entity.motionY += upMotion;
         entity.motionZ += forward * d2 * d4 + strafe * d1;
      }

   }

   protected boolean movingForward(EntityLivingBase entity, EnumFacing facing) {
      return (double)entity.getHorizontalFacing().getDirectionVec().getX() * entity.motionX > 0.0D || (double)entity.getHorizontalFacing().getDirectionVec().getZ() * entity.motionZ > 0.0D;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.SEA_STONE;
      clientConfig = TrinketsConfig.CLIENT.items.SEA_STONE;
   }
}
