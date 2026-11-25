package svenhjol.charm.smithing.feature;

import java.util.HashMap;
import java.util.Map;
import net.minecraft.init.Items;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemAxe;
import net.minecraft.item.ItemHoe;
import net.minecraft.item.ItemPickaxe;
import net.minecraft.item.ItemSpade;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraftforge.event.AnvilUpdateEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import svenhjol.meson.Feature;

public class DecreaseRepairCost extends Feature {
   public static Map<Item, Class<? extends Item>> tools = new HashMap();
   public static Map<Item, EntityEquipmentSlot> armor = new HashMap();
   public static int decreaseCost;
   public static int xpCost;

   public DecreaseRepairCost() {
   }

   public String getDescription() {
      return "Combine an item with its golden version on an anvil to reduce the original item's repair cost.";
   }

   public void configure() {
      super.configure();
      decreaseCost = this.propInt("Decrease repair amount", "Amount that a golden item will decrease another item's repair cost.", 2);
      xpCost = this.propInt("XP cost", "Amount of XP (levels) to use a golden item to decrease another item's repair cost.", 0);
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      tools.put(Items.GOLDEN_PICKAXE, ItemPickaxe.class);
      tools.put(Items.GOLDEN_SHOVEL, ItemSpade.class);
      tools.put(Items.GOLDEN_AXE, ItemAxe.class);
      tools.put(Items.GOLDEN_HOE, ItemHoe.class);
      tools.put(Items.GOLDEN_SWORD, ItemSword.class);
      armor.put(Items.GOLDEN_HELMET, EntityEquipmentSlot.HEAD);
      armor.put(Items.GOLDEN_CHESTPLATE, EntityEquipmentSlot.CHEST);
      armor.put(Items.GOLDEN_LEGGINGS, EntityEquipmentSlot.LEGS);
      armor.put(Items.GOLDEN_BOOTS, EntityEquipmentSlot.FEET);
   }

   @SubscribeEvent
   public void onAnvilUpdate(AnvilUpdateEvent event) {
      ItemStack in = event.getLeft();
      ItemStack combine = event.getRight();
      if (!in.isEmpty() && !combine.isEmpty()) {
         Item i = in.getItem();
         Item c = combine.getItem();
         if (tools.get(c) != null || armor.get(c) != null) {
            boolean canDecrease;
            if (i instanceof ItemArmor) {
               canDecrease = ((ItemArmor)i).armorType == armor.get(c);
            } else {
               canDecrease = i.getClass() == tools.get(c);
            }

            if (canDecrease) {
               ItemStack out = in.copy();
               if (in.getTagCompound() != null && !in.getTagCompound().hasNoTags()) {
                  int cost = in.getTagCompound().getInteger("RepairCost");
                  out.setRepairCost(Math.max(0, cost - decreaseCost));
               }

               event.setCost(xpCost);
               event.setOutput(out);
            }
         }
      }
   }

   public boolean hasSubscriptions() {
      return true;
   }
}
