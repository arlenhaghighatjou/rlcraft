package vazkii.quark.management.feature;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.Stack;
import java.util.WeakHashMap;
import java.util.function.Predicate;
import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Enchantments;
import net.minecraft.item.Item;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemFishingRod;
import net.minecraft.item.ItemStack;
import net.minecraft.item.ItemSword;
import net.minecraft.item.ItemTool;
import net.minecraft.util.EnumHand;
import net.minecraftforge.event.entity.player.PlayerDestroyItemEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import org.apache.commons.lang3.tuple.Pair;
import vazkii.quark.base.module.Feature;

public class AutomaticToolRestock extends Feature {
   private static final WeakHashMap<EntityPlayer, Stack<Pair<Integer, Integer>>> replacements = new WeakHashMap();
   public final List<Enchantment> importantEnchants = new ArrayList();
   private String[] enchantNames;
   private boolean enableLooseMatching;
   private boolean enableEnchantMatching;
   private boolean unstackablesOnly;

   public AutomaticToolRestock() {
   }

   public void setupConfig() {
      this.enchantNames = this.loadPropStringList("Important Enchantments", "Enchantments deemed important enough to have special priority when finding a replacement", this.generateDefaultEnchantmentList());
      this.enableLooseMatching = this.loadPropBool("Enable Loose Matching", "Enable replacing your tools with tools of the same type but not the same item", true);
      this.enableEnchantMatching = this.loadPropBool("Enable Enchantment Matching", "Enable comparing enchantments to find a replacement", true);
      this.unstackablesOnly = this.loadPropBool("Unstackable Items Only", "", false);
   }

   public void postInit() {
      this.initializeEnchantmentList(this.enchantNames, this.importantEnchants);
   }

   @SubscribeEvent
   public void onToolBreak(PlayerDestroyItemEvent event) {
      EntityPlayer player = event.getEntityPlayer();
      ItemStack stack = event.getOriginal();
      Item item = stack.getItem();
      if (player != null && player.world != null && !player.world.isRemote && !stack.isEmpty() && !(item instanceof ItemArmor) && (!this.unstackablesOnly || !stack.isStackable())) {
         int currSlot = player.inventory.currentItem;
         if (event.getHand() == EnumHand.OFF_HAND) {
            currSlot = player.inventory.getSizeInventory() - 1;
         }

         List<Enchantment> enchantmentsOnStack = this.getImportantEnchantments(stack);
         Predicate<ItemStack> itemPredicate = (other) -> {
            return other.getItem() == item;
         };
         if (!stack.isItemStackDamageable()) {
            itemPredicate = itemPredicate.and((other) -> {
               return other.getItemDamage() == stack.getItemDamage();
            });
         }

         Predicate<ItemStack> enchantmentPredicate = (other) -> {
            return !(new ArrayList(enchantmentsOnStack)).retainAll(this.getImportantEnchantments(other));
         };
         if (this.enableEnchantMatching && this.findReplacement(player, currSlot, itemPredicate.and(enchantmentPredicate))) {
            return;
         }

         if (this.findReplacement(player, currSlot, itemPredicate)) {
            return;
         }

         if (this.enableLooseMatching) {
            Set<String> classes = this.getItemClasses(stack);
            if (!classes.isEmpty()) {
               Predicate<ItemStack> toolPredicate = (other) -> {
                  Set<String> otherClasses = this.getItemClasses(other);
                  return !otherClasses.isEmpty() && !otherClasses.retainAll(classes);
               };
               if (this.enableEnchantMatching && !enchantmentsOnStack.isEmpty() && this.findReplacement(player, currSlot, toolPredicate.and(enchantmentPredicate))) {
                  return;
               }

               this.findReplacement(player, currSlot, toolPredicate);
            }
         }
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.phase == Phase.END && replacements.containsKey(event.player)) {
         Stack<Pair<Integer, Integer>> replacementStack = (Stack)replacements.get(event.player);
         synchronized(this) {
            while(!replacementStack.isEmpty()) {
               Pair<Integer, Integer> pair = (Pair)replacementStack.pop();
               this.switchItems(event.player, ((Integer)pair.getLeft()).intValue(), ((Integer)pair.getRight()).intValue());
            }
         }
      }

   }

   private HashSet<String> getItemClasses(ItemStack stack) {
      Item item = stack.getItem();
      if (item instanceof ItemTool) {
         return new HashSet(item.getToolClasses(stack));
      } else if (item instanceof ItemSword) {
         return new HashSet(Collections.singletonList("sword"));
      } else if (item instanceof ItemBow) {
         return new HashSet(Collections.singletonList("bow"));
      } else {
         return item instanceof ItemFishingRod ? new HashSet(Collections.singletonList("fishing_rod")) : new HashSet();
      }
   }

   private boolean findReplacement(EntityPlayer player, int currSlot, Predicate<ItemStack> match) {
      for(int i = 0; i < player.inventory.mainInventory.size(); ++i) {
         if (i != currSlot) {
            ItemStack stackAt = player.inventory.getStackInSlot(i);
            if (!stackAt.isEmpty() && match.test(stackAt)) {
               this.pushReplace(player, i, currSlot);
               return true;
            }
         }
      }

      return false;
   }

   private void pushReplace(EntityPlayer player, int slot1, int slot2) {
      synchronized(this) {
         if (!replacements.containsKey(player)) {
            replacements.put(player, new Stack());
         }

         ((Stack)replacements.get(player)).push(Pair.of(Integer.valueOf(slot1), Integer.valueOf(slot2)));
      }
   }

   private void switchItems(EntityPlayer player, int slot1, int slot2) {
      int size = player.inventory.mainInventory.size();
      if (slot1 < size && slot2 < size) {
         ItemStack stackAtSlot1 = player.inventory.getStackInSlot(slot1);
         ItemStack stackAtSlot2 = player.inventory.getStackInSlot(slot2);
         player.inventory.setInventorySlotContents(slot2, stackAtSlot1);
         player.inventory.setInventorySlotContents(slot1, stackAtSlot2);
      }
   }

   private List<Enchantment> getImportantEnchantments(ItemStack stack) {
      List<Enchantment> enchantsOnStack = new ArrayList();

      for(Enchantment ench : this.importantEnchants) {
         if (EnchantmentHelper.getEnchantmentLevel(ench, stack) > 0) {
            enchantsOnStack.add(ench);
         }
      }

      return enchantsOnStack;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String[] getIncompatibleMods() {
      return new String[]{"inventorytweaks"};
   }

   private String[] generateDefaultEnchantmentList() {
      Enchantment[] enchants = new Enchantment[]{Enchantments.SILK_TOUCH, Enchantments.FORTUNE, Enchantments.INFINITY, Enchantments.LUCK_OF_THE_SEA, Enchantments.LOOTING};
      List<String> strings = new ArrayList();

      for(Enchantment e : enchants) {
         if (e != null && e.getRegistryName() != null) {
            strings.add(e.getRegistryName().toString());
         }
      }

      return (String[])strings.toArray(new String[0]);
   }
}
