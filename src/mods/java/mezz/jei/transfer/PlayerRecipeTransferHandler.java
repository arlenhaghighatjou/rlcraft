package mezz.jei.transfer;

import com.google.common.collect.ImmutableSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import javax.annotation.Nullable;
import mezz.jei.Internal;
import mezz.jei.JustEnoughItems;
import mezz.jei.api.gui.IGuiIngredient;
import mezz.jei.api.gui.IGuiItemStackGroup;
import mezz.jei.api.gui.IRecipeLayout;
import mezz.jei.api.recipe.IFocus;
import mezz.jei.api.recipe.transfer.IRecipeTransferError;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandler;
import mezz.jei.api.recipe.transfer.IRecipeTransferHandlerHelper;
import mezz.jei.api.recipe.transfer.IRecipeTransferInfo;
import mezz.jei.config.ServerInfo;
import mezz.jei.gui.ingredients.GuiItemStackGroup;
import mezz.jei.network.packets.PacketRecipeTransfer;
import mezz.jei.startup.StackHelper;
import mezz.jei.util.Log;
import mezz.jei.util.Translator;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.inventory.ContainerPlayer;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;

public class PlayerRecipeTransferHandler implements IRecipeTransferHandler<ContainerPlayer> {
   private final StackHelper stackHelper = Internal.getStackHelper();
   private final IRecipeTransferHandlerHelper handlerHelper;
   private final IRecipeTransferInfo<ContainerPlayer> transferHelper;

   public PlayerRecipeTransferHandler(IRecipeTransferHandlerHelper handlerHelper) {
      this.handlerHelper = handlerHelper;
      this.transferHelper = new BasicRecipeTransferInfo<ContainerPlayer>(ContainerPlayer.class, "minecraft.crafting", 1, 4, 9, 36);
   }

   public Class<ContainerPlayer> getContainerClass() {
      return this.transferHelper.getContainerClass();
   }

   @Nullable
   public IRecipeTransferError transferRecipe(ContainerPlayer container, IRecipeLayout recipeLayout, EntityPlayer player, boolean maxTransfer, boolean doTransfer) {
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

         IGuiItemStackGroup itemStackGroup = recipeLayout.getItemStacks();
         int inputCount = 0;
         Set<Integer> badIndexes = ImmutableSet.of(Integer.valueOf(2), Integer.valueOf(5), Integer.valueOf(6), Integer.valueOf(7), Integer.valueOf(8));
         int inputIndex = 0;

         for(IGuiIngredient<ItemStack> ingredient : itemStackGroup.getGuiIngredients().values()) {
            if (ingredient.isInput()) {
               if (!ingredient.getAllIngredients().isEmpty()) {
                  ++inputCount;
                  if (badIndexes.contains(Integer.valueOf(inputIndex))) {
                     String tooltipMessage = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.too.large.player.inventory");
                     return this.handlerHelper.createUserErrorWithTooltip(tooltipMessage);
                  }
               }

               ++inputIndex;
            }
         }

         badIndexes = new ArrayList();

         for(IGuiIngredient<ItemStack> guiIngredient : itemStackGroup.getGuiIngredients().values()) {
            if (guiIngredient.isInput()) {
               badIndexes.add(guiIngredient);
            }
         }

         IGuiItemStackGroup playerInvItemStackGroup = new GuiItemStackGroup((IFocus)null, 0);
         int[] playerGridIndexes = new int[]{0, 1, 3, 4};

         for(int i = 0; i < 4; ++i) {
            int index = playerGridIndexes[i];
            if (index < badIndexes.size()) {
               IGuiIngredient<ItemStack> ingredient = (IGuiIngredient)badIndexes.get(index);
               playerInvItemStackGroup.init(i, true, 0, 0);
               playerInvItemStackGroup.set(i, ingredient.getAllIngredients());
            }
         }

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
            StackHelper.MatchingItemsResult matchingItemsResult = this.stackHelper.getMatchingItems(availableItemStacks, playerInvItemStackGroup.getGuiIngredients());
            if (matchingItemsResult.missingItems.size() > 0) {
               String message = Translator.translateToLocal("jei.tooltip.error.recipe.transfer.missing");
               matchingItemsResult = this.stackHelper.getMatchingItems(availableItemStacks, itemStackGroup.getGuiIngredients());
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
                  PacketRecipeTransfer packet = new PacketRecipeTransfer(matchingItemsResult.matchingItems, craftingSlotIndexes, inventorySlotIndexes, maxTransfer, false);
                  JustEnoughItems.getProxy().sendPacketToServer(packet);
               }

               return null;
            }
         }
      }
   }
}
