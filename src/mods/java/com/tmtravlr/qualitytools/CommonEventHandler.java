package com.tmtravlr.qualitytools;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.tmtravlr.qualitytools.baubles.BaublesHandler;
import com.tmtravlr.qualitytools.config.ConfigLoader;
import com.tmtravlr.qualitytools.items.ItemEmeraldBauble;
import com.tmtravlr.qualitytools.reforging.BlockReforgingStation;
import com.tmtravlr.qualitytools.reforging.TileEntityReforgingStation;
import java.util.ArrayList;
import java.util.function.Consumer;
import net.minecraft.block.Block;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.ai.attributes.IAttributeInstance;
import net.minecraft.entity.passive.EntityHorse;
import net.minecraft.entity.passive.HorseArmorType;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.tileentity.TileEntity;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.living.LivingEvent.LivingUpdateEvent;
import net.minecraftforge.fml.common.FMLLog;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.PlayerLoggedInEvent;
import net.minecraftforge.registries.IForgeRegistry;

public class CommonEventHandler {
   private static boolean notified;

   public CommonEventHandler() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, Block.class, CommonEventHandler::onRegisterBlocks);
      bus.registerGeneric(Register.class, Item.class, CommonEventHandler::onRegisterItems);
      bus.register(LivingUpdateEvent.class, CommonEventHandler::onLivingUpdate);
      bus.register(EntityJoinWorldEvent.class, CommonEventHandler::onEntityJoinWorld);
      bus.register(PlayerLoggedInEvent.class, CommonEventHandler::onPlayerLogin);
   }

   public static void onRegisterBlocks(Register<Block> event) {
      IForgeRegistry<Block> registry = event.getRegistry();
      registry.register(BlockReforgingStation.INSTANCE);
      TileEntity.register("qualitytools.reforging_station", TileEntityReforgingStation.class);
   }

   public static void onRegisterItems(Register<Item> event) {
      IForgeRegistry<Item> registry = event.getRegistry();
      registry.register(ItemEmeraldBauble.EMERALD_RING);
      registry.register(ItemEmeraldBauble.EMERALD_AMULET);
      registry.register(BlockReforgingStation.ITEM_INSTANCE);
   }

   public static void onLivingUpdate(LivingUpdateEvent event) {
      if (!event.getEntityLiving().world.isRemote) {
         if (event.getEntityLiving().ticksExisted % 13 == 0 && event.getEntityLiving() instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)event.getEntityLiving();
            if (!notified) {
               if (!ConfigLoader.problems.isEmpty()) {
                  ConfigLoader.notifyProblems(player);
               }

               notified = true;
            }

            ArrayList<ItemStack> itemsToCheck = new ArrayList();
            if (player.inventory != null) {
               itemsToCheck.addAll(player.inventory.mainInventory);
               itemsToCheck.addAll(player.inventory.armorInventory);
               itemsToCheck.addAll(player.inventory.offHandInventory);
            }

            BaublesHandler.INSTANCE.addEquippedBaubles(player, itemsToCheck);
            itemsToCheck.forEach((stack) -> {
               if (!QualityToolsHelper.hasQualityTag(stack)) {
                  QualityToolsHelper.generateQualityTag(stack, false);
               }

            });
         }

         if (event.getEntityLiving().ticksExisted % 7 == 0) {
            Multimap<String, AttributeModifier> modifiersToRemove = HashMultimap.create();

            for(IAttributeInstance attribute : event.getEntityLiving().getAttributeMap().getAllAttributes()) {
               for(AttributeModifier modifier : attribute.getModifiers()) {
                  if (modifier.getName().equals("qualitytools")) {
                     modifiersToRemove.put(attribute.getAttribute().getName(), modifier);
                  }
               }
            }

            ItemStack item = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.MAINHAND);
            QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), item, EntityEquipmentSlot.MAINHAND.getName(), modifiersToRemove);
            item = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.OFFHAND);
            QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), item, EntityEquipmentSlot.OFFHAND.getName(), modifiersToRemove);
            item = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.HEAD);
            QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), item, EntityEquipmentSlot.HEAD.getName(), modifiersToRemove);
            item = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.CHEST);
            QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), item, EntityEquipmentSlot.CHEST.getName(), modifiersToRemove);
            item = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.LEGS);
            QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), item, EntityEquipmentSlot.LEGS.getName(), modifiersToRemove);
            item = event.getEntityLiving().getItemStackFromSlot(EntityEquipmentSlot.FEET);
            QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), item, EntityEquipmentSlot.FEET.getName(), modifiersToRemove);
            if (event.getEntityLiving() instanceof EntityPlayer) {
               EntityPlayer player = (EntityPlayer)event.getEntityLiving();
               BaublesHandler.INSTANCE.applyAttributesFromBaubles(player, modifiersToRemove);

               for(ItemStack inventoryItem : player.inventory.mainInventory) {
                  QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), inventoryItem, "inventory", modifiersToRemove);
               }
            }

            if (event.getEntityLiving() instanceof EntityHorse) {
               EntityHorse horse = (EntityHorse)event.getEntityLiving();
               if (horse.getHorseArmorType() != HorseArmorType.NONE) {
                  try {
                     NBTTagCompound horseTag = horse.writeToNBT(new NBTTagCompound());
                     if (horseTag.hasKey("ArmorItem", 10)) {
                        NBTTagCompound armorTag = horseTag.getCompoundTag("ArmorItem");
                        ItemStack armorItem = new ItemStack(armorTag);
                        QualityToolsHelper.applyAttributesForSlot(event.getEntityLiving(), armorItem, "horse", modifiersToRemove);
                     }
                  } catch (Exception var7) {
                     FMLLog.warning("[Quality Tools] Caught exception while trying to find horse armor", new Object[0]);
                     var7.printStackTrace();
                  }
               }
            }

            event.getEntityLiving().getAttributeMap().removeAttributeModifiers(modifiersToRemove);
         }
      }

   }

   public static void onEntityJoinWorld(EntityJoinWorldEvent event) {
      if (!event.getWorld().isRemote && event.getEntity() instanceof EntityLivingBase) {
         EntityLivingBase entity = (EntityLivingBase)event.getEntity();

         for(IAttributeInstance attribute : entity.getAttributeMap().getAllAttributes()) {
            for(AttributeModifier modifier : attribute.getModifiers()) {
               if (modifier.getName().equals("qualitytools")) {
                  attribute.removeModifier(modifier);
                  attribute.applyModifier(modifier);
               }
            }
         }
      }

   }

   public static void onPlayerLogin(PlayerLoggedInEvent event) {
      if (event.player instanceof EntityPlayerMP) {
         ConfigLoader.sendReforgingMaterialsToClient((EntityPlayerMP)event.player);
      }

   }
}
