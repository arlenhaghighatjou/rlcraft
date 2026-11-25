package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageSwapItems;

public class FToSwitchItems extends Feature {
   public FToSwitchItems() {
   }

   @SubscribeEvent
   public void keyboardEvent(Post event) {
      if (GameSettings.isKeyDown(Minecraft.getMinecraft().gameSettings.keyBindSwapHands) && event.getGui() instanceof GuiContainer) {
         GuiContainer gui = (GuiContainer)event.getGui();
         Slot slot = gui.getSlotUnderMouse();
         if (slot != null) {
            IInventory inv = slot.inventory;
            if (inv instanceof InventoryPlayer) {
               int index = slot.getSlotIndex();
               if (Minecraft.getMinecraft().player.capabilities.isCreativeMode && index >= 36) {
                  index -= 36;
               }

               if (index < ((InventoryPlayer)inv).mainInventory.size()) {
                  NetworkHandler.INSTANCE.sendToServer(new MessageSwapItems(index));
               }
            }
         }
      }

   }

   public static void switchItems(EntityPlayer player, int slot) {
      if (ModuleLoader.isFeatureEnabled(FToSwitchItems.class) && slot < player.inventory.mainInventory.size()) {
         int offHandSlot = player.inventory.getSizeInventory() - 1;
         ItemStack stackAtSlot = player.inventory.getStackInSlot(slot);
         ItemStack stackAtOffhand = player.inventory.getStackInSlot(offHandSlot);
         player.inventory.setInventorySlotContents(slot, stackAtOffhand);
         player.inventory.setInventorySlotContents(offHandSlot, stackAtSlot);
      }
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public String[] getIncompatibleMods() {
      return new String[]{"visiblearmorslots"};
   }

   public String getFeatureIngameConfigName() {
      return "F to Switch Items";
   }
}
