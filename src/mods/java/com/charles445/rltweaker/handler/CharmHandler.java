package com.charles445.rltweaker.handler;

import com.charles445.rltweaker.RLTweaker;
import com.charles445.rltweaker.config.ModConfig;
import com.charles445.rltweaker.util.CompatUtil;
import com.charles445.rltweaker.util.CriticalException;
import com.charles445.rltweaker.util.ErrorUtil;
import com.charles445.rltweaker.util.ReflectUtil;
import com.google.common.base.Predicate;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import javax.annotation.Nullable;
import net.minecraft.block.BlockDispenser;
import net.minecraft.dispenser.BehaviorDefaultDispenseItem;
import net.minecraft.dispenser.BehaviorProjectileDispense;
import net.minecraft.dispenser.IPosition;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnumEnchantmentType;
import net.minecraft.entity.IProjectile;
import net.minecraft.entity.passive.EntityVillager.ITradeList;
import net.minecraft.entity.projectile.EntitySnowball;
import net.minecraft.entity.projectile.EntityThrowable;
import net.minecraft.init.Items;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemTool;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.registry.RegistryNamespaced;
import net.minecraft.world.World;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.fml.common.registry.VillagerRegistry;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerCareer;
import net.minecraftforge.fml.common.registry.VillagerRegistry.VillagerProfession;

public class CharmHandler {
   public static Map<String, EnumEnchantmentType> enchantmentTypes = new HashMap();

   public CharmHandler() {
      try {
         if (ModConfig.server.charm.fixIncorrectItemEnchantments) {
            this.fixIncorrectItemEnchantments();
         }

         if (ModConfig.server.charm.fixSalvageTrade) {
            this.fixSalvageTrade();
         }

         if (ModConfig.server.charm.fixChargedEmeraldCrash) {
            this.fixChargedEmeraldCrash();
         }

         if (ModConfig.server.charm.disableMagneticEnchantment) {
            RLTweaker.logger.info("Disabling the Magnetic enchantment");
            CompatUtil.findAndRemoveHandlerFromEventBus("svenhjol.charm.enchanting.feature.Magnetic");
         }
      } catch (Exception var2) {
         RLTweaker.logger.error("Failed to setup CharmHandler!", var2);
         ErrorUtil.logSilent("Charm Critical Setup Failure");
         if (var2 instanceof CriticalException) {
            throw new RuntimeException(var2);
         }
      }

   }

   private void fixChargedEmeraldCrash() {
      boolean replace = false;
      Item itemRef = null;

      for(Item item : BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.getKeys()) {
         if (item != null && item.getClass().getName().equals("svenhjol.charm.world.item.ItemChargedEmerald")) {
            itemRef = item;
            replace = true;
            break;
         }
      }

      if (replace) {
         RLTweaker.logger.info("Fixing Charged Emerald Crash");

         try {
            Class c_EntityChargedEmerald = Class.forName("svenhjol.charm.world.entity.EntityChargedEmerald");
            final Constructor con_EntityChargedEmerald = c_EntityChargedEmerald.getDeclaredConstructor(World.class);
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(itemRef, new BehaviorProjectileDispense() {
               protected IProjectile getProjectileEntity(World world, IPosition position, ItemStack stack) {
                  try {
                     EntityThrowable throwable = (EntityThrowable)con_EntityChargedEmerald.newInstance(world);
                     throwable.setPosition(position.getX(), position.getY(), position.getZ());
                     return throwable;
                  } catch (IllegalAccessException | IllegalArgumentException | InvocationTargetException | InstantiationException var5) {
                     ErrorUtil.logSilent("Charm Charged Emerald Construction");
                     return new EntitySnowball(world, position.getX(), position.getY(), position.getZ());
                  }
               }
            });
         } catch (NoSuchMethodException | SecurityException | ClassNotFoundException var5) {
            ErrorUtil.logSilent("Charm Charged Emerald Invocation");
            BlockDispenser.DISPENSE_BEHAVIOR_REGISTRY.putObject(itemRef, new BehaviorDefaultDispenseItem());
         }
      }

   }

   private void fixIncorrectItemEnchantments() {
      this.fixEnchantmentWithPredicate("curse_break", (item) -> {
         return false;
      });
      this.fixEnchantmentWithPredicate("homing", (item) -> {
         return item == Items.IRON_HOE || item == Items.DIAMOND_HOE || item == Items.GOLDEN_HOE;
      });
      this.fixEnchantmentWithPredicate("magnetic", (item) -> {
         return item == Items.SHEARS || item instanceof ItemTool;
      });
   }

   private void fixEnchantmentWithType(String enchantName, EnumEnchantmentType type) {
      Enchantment enchant = this.getEnchantmentByName(enchantName);
      if (enchant == null) {
         ErrorUtil.logSilent("Charm Missing Enchantment " + enchantName);
         RLTweaker.logger.warn("Couldn't find Charm enchantment: " + enchantName);
      } else {
         enchant.type = type;
      }

   }

   private void fixEnchantmentWithPredicate(String enchantName, Predicate<Item> delegate) {
      Enchantment enchant = this.getEnchantmentByName(enchantName);
      if (enchant == null) {
         ErrorUtil.logSilent("Charm Missing Enchantment " + enchantName);
         RLTweaker.logger.warn("Couldn't find Charm enchantment: " + enchantName);
      } else {
         String typeName = "RLTweaker Charm " + enchantName;
         EnumEnchantmentType type = EnumHelper.addEnchantmentType(typeName, delegate);
         if (type == null) {
            ErrorUtil.logSilent("Charm addEnchantmentType " + typeName);
            RLTweaker.logger.error("Found Charm enchantment but addEnchantmentType failed: " + typeName);
         } else {
            enchantmentTypes.put(typeName, type);
            enchant.type = type;
            RLTweaker.logger.info("Replaced " + enchantName + " enchantment type with " + typeName);
         }
      }

   }

   @Nullable
   private Enchantment getEnchantmentByName(String name) {
      return (Enchantment)Enchantment.REGISTRY.getObject(new ResourceLocation("charm", name));
   }

   private void fixSalvageTrade() {
      Enchantment salvage = this.getEnchantmentByName("salvage");
      if (salvage == null) {
         try {
            Object o_VillagerRegistry_INSTANCE = ReflectUtil.findField(VillagerRegistry.class, "INSTANCE").get((Object)null);
            RegistryNamespaced<ResourceLocation, VillagerProfession> REGISTRY = (RegistryNamespaced)ReflectUtil.findField(VillagerRegistry.class, "REGISTRY").get(o_VillagerRegistry_INSTANCE);
            VillagerProfession smithProfession = (VillagerProfession)REGISTRY.getObject(new ResourceLocation("minecraft:smith"));
            VillagerCareer genericSmith = smithProfession.getCareer(0);
            VillagerCareer weaponSmith = smithProfession.getCareer(1);
            VillagerCareer toolSmith = smithProfession.getCareer(2);
            Field f_VillagerCareer_trades = ReflectUtil.findField(VillagerCareer.class, "trades");
            this.removeAllSalvages((List)f_VillagerCareer_trades.get(genericSmith));
            this.removeAllSalvages((List)f_VillagerCareer_trades.get(weaponSmith));
            this.removeAllSalvages((List)f_VillagerCareer_trades.get(toolSmith));
         } catch (Exception var9) {
            RLTweaker.logger.error("Failed to remove Salvage trades", var9);
            ErrorUtil.logSilent("Charm Salvage Trade Removal");
         }
      }

   }

   private void removeAllSalvages(List<List<ITradeList>> trades) {
      for(List<ITradeList> tradeLevel : trades) {
         Iterator<ITradeList> iterator = tradeLevel.iterator();

         while(iterator.hasNext()) {
            ITradeList tradeList = (ITradeList)iterator.next();
            if (tradeList.getClass().getName().equals("svenhjol.charm.world.feature.VillagerTrades$SalvageTrade")) {
               iterator.remove();
               RLTweaker.logger.info("Removed a charm salvage trade");
            }
         }
      }

   }
}
