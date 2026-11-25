package com.tmtravlr.qualitytools.baubles;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.IBauble;
import baubles.api.cap.IBaublesItemHandler;
import com.google.common.collect.Multimap;
import com.tmtravlr.qualitytools.QualityToolsHelper;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.entity.ai.attributes.AttributeModifier;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.common.Loader;

public class BaublesHandler {
   public static final BaublesHandler INSTANCE = new BaublesHandler();
   private static final String BAUBLES_PREFIX = "baubles_";
   private boolean baublesExists = false;

   public BaublesHandler() {
      this.baublesExists = Loader.isModLoaded("baubles");
   }

   public void addEquippedBaubles(EntityPlayer player, List<ItemStack> equipped) {
      if (this.baublesExists) {
         IBaublesItemHandler playerBaubles = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < playerBaubles.getSlots(); ++i) {
            equipped.add(playerBaubles.getStackInSlot(i));
         }
      }

   }

   public void applyAttributesFromBaubles(EntityPlayer player, Multimap<String, AttributeModifier> modifiersToRemove) {
      if (this.baublesExists) {
         IBaublesItemHandler baublesHandler = BaublesApi.getBaublesHandler(player);

         for(int i = 0; i < baublesHandler.getSlots(); ++i) {
            for(String slotName : this.getBaublesNamesForSlot(i)) {
               QualityToolsHelper.applyAttributesForSlot(player, baublesHandler.getStackInSlot(i), slotName, modifiersToRemove);
            }
         }
      }

   }

   public boolean canEquipBauble(ItemStack stack, String slotName) {
      if (this.baublesExists && slotName.startsWith("baubles_")) {
         try {
            BaubleType baubleType = BaubleType.valueOf(slotName.substring("baubles_".length()).toUpperCase());
            if (baubleType != null && stack.getItem() instanceof IBauble) {
               IBauble bauble = (IBauble)stack.getItem();
               if (baubleType == BaubleType.TRINKET || bauble.getBaubleType(stack) == BaubleType.TRINKET || bauble.getBaubleType(stack) == baubleType) {
                  return true;
               }
            }
         } catch (IllegalArgumentException var5) {
            ;
         }
      }

      return false;
   }

   private ArrayList<String> getBaublesNamesForSlot(int slot) {
      ArrayList<String> slots = new ArrayList();

      for(BaubleType type : BaubleType.values()) {
         if (type.hasSlot(slot)) {
            slots.add("baubles_" + type.name().toLowerCase());
         }
      }

      return slots;
   }
}
