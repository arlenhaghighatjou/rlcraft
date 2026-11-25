package mezz.jei.transfer;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.config.ServerInfo;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class BasicRecipeTransferHandler<C extends Container> implements IRecipeTransferHandler<C> {
   private final StackHelper stackHelper;
   private final IRecipeTransferHandlerHelper handlerHelper;
   private final IRecipeTransferInfo<C> transferHelper;

   public BasicRecipeTransferHandler(StackHelper stackHelper, IRecipeTransferHandlerHelper handlerHelper, IRecipeTransferInfo<C> transferHelper) {
      this.stackHelper = stackHelper;
      this.handlerHelper = handlerHelper;
      this.transferHelper = transferHelper;
   }

   public Class<C> getContainerClass() {
      return this.transferHelper.getContainerClass();
   }

   @Nullable
   public IRecipeTransferError transferRecipe(C container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
      if (!ServerInfo.isJeiOnServer()) {
         String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.no.server");
         return this.handlerHelper.createUserErrorWithTooltip(tooltipMessage);
      } else if (!this.transferHelper.canHandle(container)) {
         return this.handlerHelper.createInternalError();
      } else {
         Map<Integer, Slot> inventorySlots = new HashMap();

         for(Slot slot : this.transferHelper.getInventorySlots(container)) {
            inventorySlots.put(Integer.valueOf(slot.slotNumber), slot);
         }

         Map<Integer, Slot> craftingSlots = new HashMap();

         for(Slot slot : this.transferHelper.getRecipeSlots(container)) {
            craftingSlots.put(Integer.valueOf(slot.slotNumber), slot);
         }

         int inputCount = 0;
         IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();

         for(IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
            if (ingredient.isInput() && !ingredient.getAllIngredients().isEmpty()) {
               ++inputCount;
            }
         }

         if (inputCount > craftingSlots.size()) {
            Log.get().error("Recipe Transfer helper {} does not work for container {}", this.transferHelper.getClass(), container.getClass());
            return this.handlerHelper.createInternalError();
         } else {
            Map<Integer, ItemStack> availableItemStacks = new HashMap();
            int filledCraftSlotCount = 0;
            int emptySlotCount = 0;

            for(Slot slot : craftingSlots.values()) {
               ItemStack stack = slot.getStack();
               if (!stack.isEmpty()) {
                  if (!slot.canTakeStack(player)) {
                     Log.get().error("Recipe Transfer helper {} does not work for container {}. Player can't move item out of Crafting Slot number {}", this.transferHelper.getClass(), container.getClass(), Integer.valueOf(slot.slotNumber));
                     return this.handlerHelper.createInternalError();
                  }

                  ++filledCraftSlotCount;
                  availableItemStacks.put(Integer.valueOf(slot.slotNumber), stack.copy());
               }
            }

            for(Slot slot : inventorySlots.values()) {
               ItemStack stack = slot.getStack();
               if (!stack.isEmpty()) {
                  availableItemStacks.put(Integer.valueOf(slot.slotNumber), stack.copy());
               } else {
                  ++emptySlotCount;
               }
            }

            if (filledCraftSlotCount - inputCount > emptySlotCount) {
               String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.inventory.full");
               return this.handlerHelper.createUserErrorWithTooltip(message);
            } else {
               StackHelper.MatchingItemsResult matchingItemsResult = this.stackHelper.getMatchingItems(availableItemStacks, itemStackGroup.getGuiIngredients());
               if (matchingItemsResult.missingItems.size() > 0) {
                  String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
                  return this.handlerHelper.createUserErrorForSlots(message, matchingItemsResult.missingItems);
               } else {
                  List<Integer> craftingSlotIndexes = new ArrayList(craftingSlots.keySet());
                  Collections.sort(craftingSlotIndexes);
                  List<Integer> inventorySlotIndexes = new ArrayList(inventorySlots.keySet());
                  Collections.sort(inventorySlotIndexes);

                  for(Entry<Integer, Integer> entry : matchingItemsResult.matchingItems.entrySet()) {
                     int craftNumber = ((Integer)entry.getKey()).intValue();
                     int slotNumber = ((Integer)craftingSlotIndexes.get(craftNumber)).intValue();
                     if (slotNumber < 0 || slotNumber >= container.inventorySlots.size()) {
                        Log.get().error("Recipes Transfer Helper {} references slot {} outside of the inventory's size {}", this.transferHelper.getClass(), Integer.valueOf(slotNumber), Integer.valueOf(container.inventorySlots.size()));
                        return this.handlerHelper.createInternalError();
                     }
                  }

                  if (doTransfer) {
                     PacketRecipeTransfer packet = new PacketRecipeTransfer(matchingItemsResult.matchingItems, craftingSlotIndexes, inventorySlotIndexes, maxTransfer, this.transferHelper.requireCompleteSets());
                     JustEnoughItems.getProxy().sendPacketToServer(packet);
                  }

                  return null;
               }
            }
         }
      }
   }
}
