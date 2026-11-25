package vazkii.quark.management.feature;

import javax.annotation.Nullable;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Keyboard;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDeleteItem;
import vazkii.quark.management.client.gui.GuiButtonTrash;

public class DeleteItems extends Feature {
   public static boolean keyboardDown = false;
   public static boolean mouseDown = false;
   @SideOnly(Side.CLIENT)
   public static GuiButtonTrash trash;
   public static boolean trashButton;
   public static boolean playerInvOnly;
   public static boolean needsShift;
   public static int trashButtonX;
   public static int trashButtonY;

   public DeleteItems() {
   }

   public void setupConfig() {
      trashButton = this.loadPropBool("Enable Trash Button", "", true);
      playerInvOnly = this.loadPropBool("Trash Button only on Player Inventory", "", false);
      trashButtonX = this.loadPropInt("Trash Button X", "", 3);
      trashButtonY = this.loadPropInt("Trash Button Y", "", -25);
      needsShift = this.loadPropBool("Trash Button Needs Shift", "", true);
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void initGui(Post event) {
      trash = null;
      if (event.getGui() instanceof GuiContainer && trashButton) {
         GuiContainer guiInv = (GuiContainer)event.getGui();
         EntityPlayer player = Minecraft.getMinecraft().player;
         boolean isPlayerInv = guiInv instanceof GuiInventory;
         boolean creative = player.isCreative();
         if (creative || !isPlayerInv && playerInvOnly) {
            return;
         }

         int guiWidth = guiInv.getXSize();
         int guiHeight = guiInv.getYSize();
         trash = new GuiButtonTrash(guiInv, 82424, guiWidth + trashButtonX, guiHeight + trashButtonY, needsShift);
         event.getButtonList().add(trash);
         this.updateTrashPos(guiInv);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void mouseEvent(Pre event) {
      boolean oldMouseDown = mouseDown;
      mouseDown = Mouse.isButtonDown(0);
      Minecraft mc = Minecraft.getMinecraft();
      GuiScreen current = Minecraft.getMinecraft().currentScreen;
      if (mouseDown != oldMouseDown && current instanceof GuiContainer && trash != null && trash.ready) {
         NetworkHandler.INSTANCE.sendToServer(new MessageDeleteItem(-1));
         event.setCanceled(true);
         mc.player.inventory.setItemStack(ItemStack.EMPTY);
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void keyboardEvent(net.minecraftforge.client.event.GuiScreenEvent.KeyboardInputEvent.Post event) {
      boolean down = Keyboard.isKeyDown(211);
      if (GuiScreen.isCtrlKeyDown() && down && !keyboardDown && event.getGui() instanceof GuiContainer) {
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
                  NetworkHandler.INSTANCE.sendToServer(new MessageDeleteItem(index));
               }
            }
         }
      }

      keyboardDown = down;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void update(ClientTickEvent event) {
      GuiScreen gui = Minecraft.getMinecraft().currentScreen;
      if (gui instanceof GuiContainer && trash != null) {
         GuiContainer inv = (GuiContainer)gui;
         this.updateTrashPos(inv);
      }

   }

   @SideOnly(Side.CLIENT)
   private void updateTrashPos(GuiContainer inv) {
      if (trash != null) {
         trash.x = inv.getGuiLeft() + trash.shiftX;
         trash.y = inv.getGuiTop() + trash.shiftY;
      }

   }

   public static void deleteItem(EntityPlayer player, int slot) {
      if (slot <= player.inventory.mainInventory.size()) {
         ItemStack stack = slot == -1 ? player.inventory.getItemStack() : player.inventory.getStackInSlot(slot);
         if (canItemBeDeleted(stack)) {
            if (slot == -1) {
               player.inventory.setItemStack(ItemStack.EMPTY);
            } else {
               player.inventory.setInventorySlotContents(slot, ItemStack.EMPTY);
            }

         }
      }
   }

   public static boolean canItemBeDeleted(ItemStack stack) {
      return ModuleLoader.isFeatureEnabled(DeleteItems.class) && !stack.isEmpty() && !FavoriteItems.isItemFavorited(stack);
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public String getFeatureIngameConfigName() {
      return "Delete Items";
   }

   @Nullable
   public static GuiButtonTrash getTrash() {
      return trash;
   }
}
