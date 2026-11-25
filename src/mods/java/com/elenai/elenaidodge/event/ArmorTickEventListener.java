package com.elenai.elenaidodge.event;

import com.elenai.elenaidodge.integration.ConstructsArmory;
import com.elenai.elenaidodge.network.PacketHandler;
import com.elenai.elenaidodge.network.message.SWeightMessage;
import com.elenai.elenaidodge.util.ClientStorage;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.function.Consumer;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

public class ArmorTickEventListener {
   public static double weight = 0.0D;
   public static int intWeight = 0;
   public static List<ItemStack> previousArmor = new ArrayList();
   public static List<ItemStack> currentArmor = new ArrayList();
   public static boolean head = false;
   public static boolean chest = false;
   public static boolean legs = false;
   public static boolean feet = false;

   public ArmorTickEventListener() {
   }

   @SubscribeEvent
   public void onArmorUpdate(ClientTickEvent event) {
      if (event.phase == Phase.END && event.side.isClient() && ClientStorage.weightValues != null) {
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (player != null) {
            if (!currentArmor.isEmpty()) {
               currentArmor.clear();
            }

            currentArmor.add(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD));
            currentArmor.add(player.getItemStackFromSlot(EntityEquipmentSlot.CHEST));
            currentArmor.add(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS));
            currentArmor.add(player.getItemStackFromSlot(EntityEquipmentSlot.FEET));
            if (!currentArmor.equals(previousArmor)) {
               weight = 0.0D;
               List<String> weights = new ArrayList();
               Collections.addAll(weights, ClientStorage.weightValues.split(","));
               weights.forEach((w) -> {
                  String[] itemValue = w.split("=");
                  if (player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() == Item.getByNameOrId(itemValue[0])) {
                     weight += Double.valueOf(itemValue[1]).doubleValue();
                     head = true;
                  }

                  if (player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() == Item.getByNameOrId(itemValue[0])) {
                     weight += Double.valueOf(itemValue[1]).doubleValue();
                     chest = true;
                  }

                  if (player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() == Item.getByNameOrId(itemValue[0])) {
                     weight += Double.valueOf(itemValue[1]).doubleValue();
                     legs = true;
                  }

                  if (player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() == Item.getByNameOrId(itemValue[0])) {
                     weight += Double.valueOf(itemValue[1]).doubleValue();
                     feet = true;
                  }

               });
               if (!head && player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem() instanceof ItemArmor) {
                  ItemArmor item = (ItemArmor)player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem();
                  weight += (double)(item.damageReduceAmount / 2) * 1.8D.doubleValue();
               }

               if (!chest && player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem() instanceof ItemArmor) {
                  ItemArmor item = (ItemArmor)player.getItemStackFromSlot(EntityEquipmentSlot.CHEST).getItem();
                  weight += (double)(item.damageReduceAmount / 2) * 1.8D.doubleValue();
               }

               if (!legs && player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem() instanceof ItemArmor) {
                  ItemArmor item = (ItemArmor)player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem();
                  weight += (double)(item.damageReduceAmount / 2) * 1.8D.doubleValue();
               }

               if (!feet && player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem() instanceof ItemArmor) {
                  ItemArmor item = (ItemArmor)player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem();
                  weight += (double)(item.damageReduceAmount / 2) * 1.8D.doubleValue();
               }

               if (Loader.isModLoaded("conarm")) {
                  if (!head) {
                     weight += ConstructsArmory.getWeight(player, EntityEquipmentSlot.HEAD);
                  }

                  if (!chest) {
                     weight += ConstructsArmory.getWeight(player, EntityEquipmentSlot.CHEST);
                  }

                  if (!legs) {
                     weight += ConstructsArmory.getWeight(player, EntityEquipmentSlot.LEGS);
                  }

                  if (!feet) {
                     weight += ConstructsArmory.getWeight(player, EntityEquipmentSlot.FEET);
                  }
               }

               head = false;
               chest = false;
               legs = false;
               feet = false;
               ClientStorage.weight = weight;
               PacketHandler.instance.sendToServer(new SWeightMessage(weight));
               previousArmor.clear();
               previousArmor.addAll(currentArmor);
            }
         }
      }

   }
}
