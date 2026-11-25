package com.charles445.simpledifficulty.item;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.config.QuickConfig;
import com.charles445.simpledifficulty.api.temperature.TemperatureEnum;
import com.charles445.simpledifficulty.config.ModConfig;
import com.charles445.simpledifficulty.util.WorldUtil;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.item.IItemPropertyGetter;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class ItemThermometer extends Item {
   protected static Map<Integer, Long> hashAge = new HashMap();
   protected static Map<Integer, Float> hashTemp = new HashMap();
   protected static long lastAudit = 0L;

   @SideOnly(Side.CLIENT)
   protected static void audit(long worldTime) {
      if (worldTime - lastAudit >= 200L || worldTime < lastAudit) {
         lastAudit = worldTime;
         boolean mismatched = false;

         for(Integer key : hashTemp.keySet()) {
            if (!hashAge.containsKey(key)) {
               mismatched = true;
            }
         }

         for(Integer key : hashAge.keySet()) {
            if (!hashTemp.containsKey(key)) {
               mismatched = true;
            }
         }

         if (mismatched) {
            SimpleDifficulty.logger.warn("Thermometer audit had mismatched maps!");
            hashAge.clear();
            hashTemp.clear();
            return;
         }

         int removalCount = 0;
         Set<Integer> removalSet = new HashSet();

         for(Entry<Integer, Long> entry : hashAge.entrySet()) {
            if (worldTime - ((Long)entry.getValue()).longValue() >= 100L || worldTime < ((Long)entry.getValue()).longValue()) {
               removalSet.add(entry.getKey());
            }
         }

         for(Integer value : removalSet) {
            hashAge.remove(value);
            hashTemp.remove(value);
            ++removalCount;
         }

         removalSet.clear();
      }

   }

   public ItemThermometer() {
      this.addPropertyOverride(new ResourceLocation("temperature"), new IItemPropertyGetter() {
         @SideOnly(Side.CLIENT)
         public float apply(ItemStack stack, @Nullable World world, @Nullable EntityLivingBase pendingEntity) {
            boolean hasEntity = pendingEntity != null;
            Entity entity = (Entity)(hasEntity ? pendingEntity : stack.getItemFrame());
            if (world == null && entity != null) {
               world = entity.world;
            }

            if (world != null && entity != null) {
               return QuickConfig.isTemperatureEnabled() && ModConfig.client.thermometer.enableThermometer ? this.wobble(world, entity, stack.hashCode()) : 0.0F;
            } else {
               return 0.0F;
            }
         }

         @SideOnly(Side.CLIENT)
         private float wobble(World world, Entity entity, int hash) {
            Long age = (Long)ItemThermometer.hashAge.get(Integer.valueOf(hash));
            Float temp = (Float)ItemThermometer.hashTemp.get(Integer.valueOf(hash));
            long totalWorldTime = world.getTotalWorldTime();
            ItemThermometer.audit(totalWorldTime);
            boolean cleanStart = false;
            if (age == null && temp == null) {
               cleanStart = true;
            } else if (age == null || temp == null) {
               ItemThermometer.hashAge.remove(Integer.valueOf(hash));
               ItemThermometer.hashTemp.remove(Integer.valueOf(hash));
               cleanStart = true;
            }

            if (cleanStart) {
               ItemThermometer.hashAge.put(Integer.valueOf(hash), Long.valueOf(totalWorldTime));
               float newTemp = this.calculateTemperature(world, entity);
               ItemThermometer.hashTemp.put(Integer.valueOf(hash), Float.valueOf(newTemp));
               return newTemp;
            } else if (totalWorldTime - age.longValue() >= 10L + (long)(hash & 7)) {
               float newTemp = this.calculateTemperature(world, entity);
               ItemThermometer.hashTemp.put(Integer.valueOf(hash), Float.valueOf(newTemp));
               ItemThermometer.hashAge.put(Integer.valueOf(hash), Long.valueOf(totalWorldTime));
               return newTemp;
            } else {
               return temp.floatValue();
            }
         }

         @SideOnly(Side.CLIENT)
         private float calculateTemperature(World world, Entity entity) {
            int tempRange = TemperatureEnum.BURNING.getUpperBound() - TemperatureEnum.FREEZING.getLowerBound() + 1;
            return (float)WorldUtil.calculateClientWorldEntityTemperature(world, entity) / (float)tempRange;
         }
      });
   }
}
