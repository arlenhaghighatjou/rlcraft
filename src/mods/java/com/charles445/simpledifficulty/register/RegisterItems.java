package com.charles445.simpledifficulty.register;

import com.charles445.simpledifficulty.SimpleDifficulty;
import com.charles445.simpledifficulty.api.SDItems;
import com.charles445.simpledifficulty.config.JsonConfigInternal;
import com.charles445.simpledifficulty.config.json.ExtraItem;
import com.charles445.simpledifficulty.item.ItemArmorTemperature;
import com.charles445.simpledifficulty.item.ItemCanteen;
import com.charles445.simpledifficulty.item.ItemDragonCanteen;
import com.charles445.simpledifficulty.item.ItemIronCanteen;
import com.charles445.simpledifficulty.item.ItemJuice;
import com.charles445.simpledifficulty.item.ItemPurifiedWaterBottle;
import com.charles445.simpledifficulty.item.ItemThermometer;
import java.util.Map.Entry;
import net.minecraft.init.Blocks;
import net.minecraft.init.SoundEvents;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class RegisterItems {
   public RegisterItems() {
   }

   @EventBusSubscriber(
      modid = "simpledifficulty"
   )
   public static class Registrar {
      public Registrar() {
      }

      @SubscribeEvent
      public static void registerItems(Register<Item> event) {
         SimpleDifficulty.logger.info("Registering Items");
         SDItems.woolArmorMaterial = EnumHelper.addArmorMaterial("SDWOOL", "simpledifficulty:wool", 2, new int[]{1, 1, 1, 1}, 5, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
         SDItems.woolArmorMaterial.repairMaterial = new ItemStack(Blocks.WOOL);
         SDItems.armorMaterials.put("woolArmorMaterial", SDItems.woolArmorMaterial);
         SDItems.iceArmorMaterial = EnumHelper.addArmorMaterial("SDICE", "simpleDifficulty:ice", 2, new int[]{1, 1, 1, 1}, 5, SoundEvents.ITEM_ARMOR_EQUIP_GENERIC, 0.0F);
         SDItems.iceArmorMaterial.repairMaterial = new ItemStack(SDItems.ice_chunk);
         SDItems.armorMaterials.put("iceArmorMaterial", SDItems.iceArmorMaterial);
         IForgeRegistry<Item> registry = event.getRegistry();
         SDItems.purifiedWaterBottle = registerAs("purified_water_bottle", new ItemPurifiedWaterBottle(), registry);
         SDItems.juice = registerAs("juice", new ItemJuice(), registry);
         SDItems.canteen = registerAs("canteen", new ItemCanteen(), registry);
         SDItems.ironCanteen = registerAs("iron_canteen", new ItemIronCanteen(), registry);
         SDItems.charcoalFilter = registerAs("charcoal_filter", new Item(), registry);
         SDItems.ice_chunk = registerAs("ice_chunk", new Item(), registry);
         SDItems.magma_chunk = registerAs("magma_chunk", new Item(), registry);
         SDItems.thermometer = registerAs("thermometer", new ItemThermometer(), registry);
         SDItems.wool_helmet = registerAs("wool_helmet", new ItemArmorTemperature(SDItems.woolArmorMaterial, EntityEquipmentSlot.HEAD), registry);
         SDItems.wool_chestplate = registerAs("wool_chestplate", new ItemArmorTemperature(SDItems.woolArmorMaterial, EntityEquipmentSlot.CHEST), registry);
         SDItems.wool_leggings = registerAs("wool_leggings", new ItemArmorTemperature(SDItems.woolArmorMaterial, EntityEquipmentSlot.LEGS), registry);
         SDItems.wool_boots = registerAs("wool_boots", new ItemArmorTemperature(SDItems.woolArmorMaterial, EntityEquipmentSlot.FEET), registry);
         SDItems.ice_helmet = registerAs("ice_helmet", new ItemArmorTemperature(SDItems.iceArmorMaterial, EntityEquipmentSlot.HEAD), registry);
         SDItems.ice_chestplate = registerAs("ice_chestplate", new ItemArmorTemperature(SDItems.iceArmorMaterial, EntityEquipmentSlot.CHEST), registry);
         SDItems.ice_leggings = registerAs("ice_leggings", new ItemArmorTemperature(SDItems.iceArmorMaterial, EntityEquipmentSlot.LEGS), registry);
         SDItems.ice_boots = registerAs("ice_boots", new ItemArmorTemperature(SDItems.iceArmorMaterial, EntityEquipmentSlot.FEET), registry);

         for(Entry<String, ExtraItem> entry : JsonConfigInternal.extraItems.entrySet()) {
            String name = (String)entry.getKey();
            ExtraItem extraItem = (ExtraItem)entry.getValue();
            if (extraItem.enabled) {
               byte var7 = -1;
               switch(name.hashCode()) {
               case -1846412162:
                  if (name.equals("dragon_canteen")) {
                     var7 = 0;
                  }
                  break;
               case 271238604:
                  if (name.equals("frost_rod")) {
                     var7 = 1;
                  }
                  break;
               case 1579175060:
                  if (name.equals("frost_powder")) {
                     var7 = 2;
                  }
               }

               switch(var7) {
               case 0:
                  registerAs(name, new ItemDragonCanteen(extraItem), registry);
                  break;
               case 1:
               case 2:
                  registerAs(name, new Item(), registry);
                  break;
               default:
                  SimpleDifficulty.logger.error("Tried to register invalid extra item: " + name);
               }
            }
         }

      }

      private static Item registerAs(String name, Item newItem, IForgeRegistry<Item> registry) {
         newItem.setRegistryName("simpledifficulty", name);
         newItem.setUnlocalizedName(newItem.getRegistryName().toString());
         newItem.setCreativeTab(ModCreativeTab.instance);
         registry.register(newItem);
         SDItems.items.put(name, newItem);
         return newItem;
      }
   }
}
