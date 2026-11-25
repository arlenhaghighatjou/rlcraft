package com.charles445.simpledifficulty.util.internal;

import com.charles445.simpledifficulty.api.SDCapabilities;
import com.charles445.simpledifficulty.api.SDFluids;
import com.charles445.simpledifficulty.api.SDPotions;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.config.ServerConfig;
import com.charles445.simpledifficulty.api.config.ServerOptions;
import com.charles445.simpledifficulty.api.thirst.IThirstCapability;
import com.charles445.simpledifficulty.api.thirst.IThirstUtil;
import com.charles445.simpledifficulty.api.thirst.ThirstEnum;
import com.charles445.simpledifficulty.api.thirst.ThirstEnumBlockPos;
import com.charles445.simpledifficulty.api.thirst.ThirstUtil;
import com.charles445.simpledifficulty.config.ModConfig;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.fluids.FluidUtil;

public class ThirstUtilInternal implements IThirstUtil {
   public ThirstUtilInternal() {
   }

   @Nullable
   public static ThirstEnumBlockPos traceWaterToDrink(EntityPlayer player) {
      if (player.getHeldItemMainhand().isEmpty()) {
         IThirstCapability capability = SDCapabilities.getThirstData(player);
         if (capability.isThirsty()) {
            ThirstEnumBlockPos traceResult = ThirstUtil.traceWater(player);
            if (traceResult == null) {
               return null;
            }

            if (traceResult.thirstEnum == ThirstEnum.PURIFIED) {
               if (!ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS)) {
                  return null;
               }

               if (!ServerConfig.instance.getBoolean(ServerOptions.INFINITE_PURIFIED_WATER)) {
                  player.world.setBlockToAir(traceResult.pos);
               }
            } else {
               if (traceResult.thirstEnum == ThirstEnum.RAIN && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN)) {
                  return null;
               }

               if (traceResult.thirstEnum == ThirstEnum.NORMAL && !ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_BLOCKS)) {
                  return null;
               }
            }

            return traceResult;
         }
      }

      return null;
   }

   @Nullable
   public ThirstEnumBlockPos traceWater(EntityPlayer player) {
      if (player.rotationPitch < -75.0F && player.world.isRainingAt(player.getPosition()) && player.world.canSeeSky(player.getPosition()) && ServerConfig.instance.getBoolean(ServerOptions.THIRST_DRINK_RAIN)) {
         return new ThirstEnumBlockPos(ThirstEnum.RAIN, player.getPosition());
      } else {
         double reach = player.getEntityAttribute(EntityPlayer.REACH_DISTANCE).getAttributeValue() * 0.5D;
         Vec3d eyevec = player.getPositionEyes(1.0F);
         Vec3d lookvec = player.getLook(1.0F);
         Vec3d targetvec = eyevec.addVector(lookvec.x * reach, lookvec.y * reach, lookvec.z * reach);
         RayTraceResult trace = player.getEntityWorld().rayTraceBlocks(eyevec, targetvec, true);
         if (trace != null && trace.typeOfHit == Type.BLOCK) {
            Block traceBlock = player.getEntityWorld().getBlockState(trace.getBlockPos()).getBlock();
            if (traceBlock == Blocks.WATER) {
               return new ThirstEnumBlockPos(ThirstEnum.NORMAL, trace.getBlockPos());
            } else {
               return traceBlock == SDFluids.blockPurifiedWater ? new ThirstEnumBlockPos(ThirstEnum.PURIFIED, trace.getBlockPos()) : null;
            }
         } else {
            return null;
         }
      }
   }

   public void takeDrink(EntityPlayer player, int thirst, float saturation, float dirtyChance) {
      if (QuickConfig.isThirstEnabled()) {
         IThirstCapability capability = SDCapabilities.getThirstData(player);
         if (capability.isThirsty()) {
            capability.addThirstLevel(thirst);
            capability.addThirstSaturation(saturation);
            if (dirtyChance != 0.0F && player.world.rand.nextFloat() < dirtyChance) {
               player.addPotionEffect(new PotionEffect(SDPotions.thirsty, 600));
               if (ModConfig.server.thirst.thirstParasites && player.world.rand.nextDouble() < ModConfig.server.thirst.thirstParasitesChance) {
                  player.addPotionEffect(new PotionEffect(SDPotions.parasites, ModConfig.server.thirst.thirstParasitesDuration));
               }
            }
         } else if (capability.getThirstSaturation() < saturation) {
            capability.setThirstSaturation(saturation);
         }

      }
   }

   public void takeDrink(EntityPlayer player, int thirst, float saturation) {
      this.takeDrink(player, thirst, saturation, 0.0F);
   }

   public void takeDrink(EntityPlayer player, ThirstEnum type) {
      this.takeDrink(player, type.getThirst(), type.getSaturation(), type.getThirstyChance());
   }

   public ItemStack createPurifiedWaterBucket() {
      return FluidUtil.getFilledBucket(new FluidStack(SDFluids.purifiedWater, 1000));
   }
}
