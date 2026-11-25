package vazkii.quark.management.feature;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.Gui;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.client.event.GuiScreenEvent.DrawScreenEvent.Post;
import net.minecraftforge.client.event.GuiScreenEvent.MouseInputEvent.Pre;
import net.minecraftforge.client.event.RenderTooltipEvent.PostText;
import net.minecraftforge.event.entity.player.EntityItemPickupEvent;
import net.minecraftforge.event.entity.player.ItemTooltipEvent;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.Mouse;
import vazkii.arl.network.NetworkHandler;
import vazkii.arl.util.ItemNBTHelper;
import vazkii.quark.base.lib.LibMisc;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageFavoriteItem;

public class FavoriteItems extends Feature {
   public static final String TAG_FAVORITE_ITEM = "Quark:FavoriteItem";
   public static boolean hovering;
   private static boolean mouseDown = false;

   public FavoriteItems() {
   }

   @SubscribeEvent
   public void itemPickedUp(EntityItemPickupEvent event) {
      ItemStack stack = event.getItem().getItem();
      ItemStack copy = stack.copy();
      ItemNBTHelper.setBoolean(copy, "Quark:FavoriteItem", true);
      if (stack.isStackable()) {
         for(ItemStack other : event.getEntityPlayer().inventory.mainInventory) {
            if (ItemStack.areItemsEqual(copy, other) && ItemStack.areItemStackTagsEqual(copy, other)) {
               if (!ItemStack.areItemStackTagsEqual(stack, copy)) {
                  event.getItem().setItem(copy);
                  event.setCanceled(true);
               }

               return;
            }
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   @SideOnly(Side.CLIENT)
   public void mouseEvent(Pre event) {
      boolean wasMouseDown = mouseDown;
      mouseDown = Mouse.isButtonDown(1);
      boolean click = mouseDown && !wasMouseDown;
      if (GuiScreen.isAltKeyDown() && click && event.getGui() instanceof GuiContainer) {
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
                  NetworkHandler.INSTANCE.sendToServer(new MessageFavoriteItem(index));
                  event.setCanceled(true);
               }
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void drawEvent(Post event) {
      if (hovering && event.getGui() instanceof GuiContainer) {
         GuiContainer guiInv = (GuiContainer)event.getGui();
         Container container = guiInv.inventorySlots;
         int guiLeft = guiInv.getGuiLeft();
         int guiTop = guiInv.getGuiTop();
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         GlStateManager.pushMatrix();
         GlStateManager.disableDepth();
         GlStateManager.disableLighting();

         for(Slot s : container.inventorySlots) {
            ItemStack stack = s.getStack();
            if (isItemFavorited(stack)) {
               Minecraft.getMinecraft().renderEngine.bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
               guiInv.drawTexturedModalRect(guiLeft + s.xPos, guiTop + s.yPos, 96, 0, 16, 16);
            }
         }

         GlStateManager.popMatrix();
      }

      hovering = false;
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void makeTooltip(ItemTooltipEvent event) {
      if (isItemFavorited(event.getItemStack())) {
         event.getToolTip().set(0, "   " + (String)event.getToolTip().get(0));
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void renderTooltip(PostText event) {
      if (isItemFavorited(event.getStack())) {
         GlStateManager.pushMatrix();
         GlStateManager.color(1.0F, 1.0F, 1.0F);
         Minecraft mc = Minecraft.getMinecraft();
         int x = event.getX();
         int y = event.getY() - 1;
         mc.getTextureManager().bindTexture(LibMisc.GENERAL_ICONS_RESOURCE);
         Gui.drawModalRectWithCustomSizedTexture(x, y, 166.0F, 0.0F, 9, 9, 256.0F, 256.0F);
         GlStateManager.popMatrix();
      }

   }

   public static void favoriteItem(EntityPlayer player, int slot) {
      if (ModuleLoader.isFeatureEnabled(FavoriteItems.class) && slot < player.inventory.getSizeInventory()) {
         ItemStack stack = player.inventory.getStackInSlot(slot);
         if (!stack.isEmpty()) {
            if (isItemFavorited(stack)) {
               NBTTagCompound cmp = stack.getTagCompound();
               if (cmp != null) {
                  cmp.removeTag("Quark:FavoriteItem");
                  if (cmp.hasNoTags()) {
                     stack.setTagCompound((NBTTagCompound)null);
                  }
               }
            } else {
               ItemNBTHelper.setBoolean(stack, "Quark:FavoriteItem", true);
            }
         }

      }
   }

   public static boolean isItemFavorited(ItemStack stack) {
      return stack != null && !stack.isEmpty() && stack.hasTagCompound() && ModuleLoader.isFeatureEnabled(FavoriteItems.class) ? ItemNBTHelper.getBoolean(stack, "Quark:FavoriteItem", false) : false;
   }

   public boolean hasSubscriptions() {
      return true;
   }

   public String getFeatureIngameConfigName() {
      return "Favorite Items";
   }
}
