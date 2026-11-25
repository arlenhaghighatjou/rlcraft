package com.charles445.simpledifficulty.util.internal;

import com.charles445.simpledifficulty.api.temperature.ITemperatureDynamicModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureModifier;
import com.charles445.simpledifficulty.api.temperature.ITemperatureUtil;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.api.temperature.TemperatureRegistry;
import com.charles445.simpledifficulty.util.WorldUtil;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagFloat;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.World;

public class TemperatureUtilInternal implements ITemperatureUtil {
   private final String ARMOR_TEMPERATURE_TAG = "SDArmorTemp";

   public TemperatureUtilInternal() {
   }

   public int getPlayerTargetTemperature(EntityPlayer player) {
      float sum = 0.0F;
      World world = player.getEntityWorld();
      BlockPos pos = WorldUtil.getSidedBlockPos(world, player);

      for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
         sum = sum + modifier.getWorldInfluence(world, pos);
         sum = sum + modifier.getPlayerInfluence(player);
      }

      for(ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values()) {
         sum = dynmodifier.applyDynamicWorldInfluence(world, pos, sum);
         sum = dynmodifier.applyDynamicPlayerInfluence(player, sum);
      }

      return (int)sum;
   }

   public int getWorldTemperature(World world, BlockPos pos) {
      float sum = 0.0F;

      for(ITemperatureModifier modifier : TemperatureRegistry.modifiers.values()) {
         sum += modifier.getWorldInfluence(world, pos);
      }

      for(ITemperatureDynamicModifier dynmodifier : TemperatureRegistry.dynamicModifiers.values()) {
         sum = dynmodifier.applyDynamicWorldInfluence(world, pos, sum);
      }

      return (int)sum;
   }

   public int clampTemperature(int temperature) {
      return MathHelper.clamp(temperature, TemperatureEnum.FREEZING.getLowerBound(), TemperatureEnum.BURNING.getUpperBound());
   }

   public TemperatureEnum getTemperatureEnum(int temp) {
      for(TemperatureEnum temp_enum : TemperatureEnum.values()) {
         if (temp_enum.matches(temp)) {
            return temp_enum;
         }
      }

      if (temp < 0) {
         return TemperatureEnum.FREEZING;
      } else {
         return TemperatureEnum.BURNING;
      }
   }

   public void setArmorTemperatureTag(ItemStack stack, float temperature) {
      if (!stack.hasTagCompound()) {
         stack.setTagCompound(new NBTTagCompound());
      }

      NBTTagCompound compound = stack.getTagCompound();
      compound.setFloat("SDArmorTemp", temperature);
   }

   public float getArmorTemperatureTag(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         if (compound.hasKey("SDArmorTemp")) {
            NBTBase tempTag = compound.getTag("SDArmorTemp");
            if (tempTag instanceof NBTTagFloat) {
               return ((NBTTagFloat)tempTag).getFloat();
            }
         }
      }

      return 0.0F;
   }

   public void removeArmorTemperatureTag(ItemStack stack) {
      if (stack.hasTagCompound()) {
         NBTTagCompound compound = stack.getTagCompound();
         if (compound.hasKey("SDArmorTemp")) {
            compound.removeTag("SDArmorTemp");
         }
      }

   }
}
