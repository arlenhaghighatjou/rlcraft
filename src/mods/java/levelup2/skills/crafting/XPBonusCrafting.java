package levelup2.skills.crafting;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import levelup2.config.LevelUpConfig;
import levelup2.skills.BaseSkill;
import levelup2.skills.SkillRegistry;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.IInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.crafting.Ingredient;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.PlayerEvent.ItemCraftedEvent;

public class XPBonusCrafting extends BaseSkill {
   private Random rand = new Random();

   public XPBonusCrafting() {
   }

   public boolean hasSubscription() {
      return true;
   }

   public String getSkillName() {
      return "levelup:craft_bonus";
   }

   public int getLevelCost(int currentLevel) {
      return -1;
   }

   public byte getSkillType() {
      return 1;
   }

   public String[] getPrerequisites() {
      return new String[0];
   }

   @SubscribeEvent
   public void onCrafting(ItemCraftedEvent evt) {
      if (SkillRegistry.getSkillLevel(evt.player, this.getSkillName()) > 0 && !this.isBlacklistedOutput(evt.crafting) && this.isNotOneItemCrafting(evt.craftMatrix)) {
         int craftingChances = this.getCraftingItems(evt.craftMatrix);
         if (craftingChances > 0) {
            int experienceGain = 0;

            for(int i = 0; i < this.getCraftingItems(evt.craftMatrix); ++i) {
               if (this.rand.nextFloat() < 0.55F) {
                  ++experienceGain;
               }
            }

            if (experienceGain > 0) {
               SkillRegistry.addExperience(evt.player, experienceGain);
            }
         }
      }

   }

   private boolean isBlacklistedOutput(ItemStack stack) {
      if (!LevelUpConfig.blacklistOutputs.isEmpty()) {
         for(Ingredient ing : LevelUpConfig.blacklistOutputs) {
            if (ing.apply(stack)) {
               return true;
            }
         }
      }

      return false;
   }

   private boolean isNotOneItemCrafting(IInventory inv) {
      boolean notSame = false;
      ItemStack firstStack = ItemStack.EMPTY;

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty() && !inv.getStackInSlot(i).getItem().hasContainerItem(inv.getStackInSlot(i))) {
            if (firstStack.isEmpty()) {
               firstStack = inv.getStackInSlot(i).copy();
            } else if (!firstStack.isItemEqual(inv.getStackInSlot(i))) {
               notSame = true;
            }
         }
      }

      return notSame;
   }

   private int getCraftingItems(IInventory inv) {
      List<ItemStack> items = new ArrayList();

      for(int i = 0; i < inv.getSizeInventory(); ++i) {
         if (!inv.getStackInSlot(i).isEmpty() && !inv.getStackInSlot(i).getItem().hasContainerItem(inv.getStackInSlot(i))) {
            ItemStack stack = inv.getStackInSlot(i).copy();
            stack.setCount(1);
            if (items.isEmpty()) {
               items.add(stack);
            } else if (!SkillRegistry.listContains(stack, items)) {
               items.add(stack);
            }
         }
      }

      return items.size();
   }

   public int getSkillRow() {
      return 0;
   }

   public int getSkillColumn() {
      return 0;
   }

   public ItemStack getRepresentativeStack() {
      return new ItemStack(Blocks.CRAFTING_TABLE);
   }

   public boolean isMaxLevel(int level) {
      return true;
   }

   public int getMaxLevel() {
      return 1;
   }

   public boolean isEnabled() {
      return true;
   }

   public boolean hasExternalJson() {
      return false;
   }

   public boolean isActive() {
      return true;
   }
}
