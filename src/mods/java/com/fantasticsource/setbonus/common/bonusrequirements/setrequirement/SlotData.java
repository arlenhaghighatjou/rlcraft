package com.fantasticsource.setbonus.common.bonusrequirements.setrequirement;

import baubles.api.BaubleType;
import baubles.api.BaublesApi;
import baubles.api.cap.IBaublesItemHandler;
import com.fantasticsource.mctools.items.ItemFilter;
import com.fantasticsource.setbonus.client.ClientData;
import com.fantasticsource.setbonus.server.ServerData;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.fml.relauncher.Side;

public class SlotData {
   public ArrayList<Integer> slots = new ArrayList();
   public ArrayList<ItemFilter> involvedItems = new ArrayList();

   private SlotData() {
   }

   public static SlotData getInstance(String slotsAndEquipment, LinkedHashMap<String, ItemFilter> setdataEquipIDTracker, Side side) {
      SlotData result = new SlotData();
      String[] tokens = slotsAndEquipment.split("=");
      if (tokens.length != 2) {
         System.err.println(I18n.translateToLocalFormatted("setbonus.error.wrongSlotArgCount", new Object[]{slotsAndEquipment}));
         return null;
      } else {
         for(String slotString : tokens[0].split("[|]")) {
            slotString = slotString.trim().toLowerCase();
            if (slotString.equals("mainhand")) {
               result.slots.add(Integer.valueOf(-1));
            } else if (slotString.equals("hotbar")) {
               for(int i = 0; i < 9; ++i) {
                  result.slots.add(Integer.valueOf(i));
               }
            } else if (slotString.equals("inventory")) {
               for(int i = 9; i < 36; ++i) {
                  result.slots.add(Integer.valueOf(i));
               }
            } else if (slotString.equals("feet")) {
               result.slots.add(Integer.valueOf(36));
            } else if (slotString.equals("legs")) {
               result.slots.add(Integer.valueOf(37));
            } else if (slotString.equals("chest")) {
               result.slots.add(Integer.valueOf(38));
            } else if (slotString.equals("head")) {
               result.slots.add(Integer.valueOf(39));
            } else if (slotString.equals("offhand")) {
               result.slots.add(Integer.valueOf(40));
            } else if (slotString.equals("bauble_amulet")) {
               for(int i : BaubleType.AMULET.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else if (slotString.equals("bauble_ring")) {
               for(int i : BaubleType.RING.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else if (slotString.equals("bauble_belt")) {
               for(int i : BaubleType.BELT.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else if (slotString.equals("bauble_head")) {
               for(int i : BaubleType.HEAD.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else if (slotString.equals("bauble_body")) {
               for(int i : BaubleType.BODY.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else if (slotString.equals("bauble_charm")) {
               for(int i : BaubleType.CHARM.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else if (slotString.equals("bauble_trinket")) {
               for(int i : BaubleType.TRINKET.getValidSlots()) {
                  result.slots.add(Integer.valueOf(-2147483647 + i));
               }
            } else {
               try {
                  result.slots.add(Integer.valueOf(Integer.parseInt(slotString)));
               } catch (NumberFormatException var13) {
                  System.err.println(I18n.translateToLocalFormatted("setbonus.error.unknownSlot", new Object[]{slotString, slotsAndEquipment}));
                  return null;
               }
            }
         }

         LinkedHashMap<String, Equip> equipment = side == Side.SERVER ? ServerData.equipment : ClientData.equipment;

         for(String equipString : tokens[1].split("[|]")) {
            equipString = equipString.trim();
            Equip equip = (Equip)equipment.get(equipString);
            if (equip == null) {
               System.err.println(I18n.translateToLocalFormatted("setbonus.error.slotBadEquipID", new Object[]{equipString, slotsAndEquipment}));
               return null;
            }

            result.involvedItems.add(equip.filter);
            setdataEquipIDTracker.put(equipString, equip.filter);
         }

         return result;
      }
   }

   public int equipped(EntityPlayer player, ArrayList<Integer> blocked) {
      Iterator var3 = this.slots.iterator();

      label48:
      while(var3.hasNext()) {
         int slot = ((Integer)var3.next()).intValue();
         if (slot == -1) {
            slot = player.inventory.currentItem;
         }

         if (!blocked.contains(Integer.valueOf(slot))) {
            if (slot > -1) {
               IInventory inv = player.inventory;
               Iterator var9 = this.involvedItems.iterator();

               while(true) {
                  if (!var9.hasNext()) {
                     continue label48;
                  }

                  ItemFilter filter = (ItemFilter)var9.next();
                  if (filter.matches(inv.getStackInSlot(slot))) {
                     break;
                  }
               }

               return slot;
            } else {
               IBaublesItemHandler handler = BaublesApi.getBaublesHandler(player);
               Iterator var6 = this.involvedItems.iterator();

               while(true) {
                  if (!var6.hasNext()) {
                     continue label48;
                  }

                  ItemFilter filter = (ItemFilter)var6.next();
                  if (filter.matches(handler.getStackInSlot(slot - Integer.MIN_VALUE - 1))) {
                     break;
                  }
               }

               return slot;
            }
         }
      }

      return Integer.MIN_VALUE;
   }
}
