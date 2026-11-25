package invtweaks;

import invtweaks.api.container.ContainerSection;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiButton;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiEditSign;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.client.gui.inventory.GuiContainerCreative.CreativeSlot;
import net.minecraft.client.multiplayer.PlayerControllerMP;
import net.minecraft.client.settings.GameSettings;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.InventoryPlayer;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.item.ItemArmor;
import net.minecraft.item.ItemStack;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.TextComponentString;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.apache.logging.log4j.Logger;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.lwjgl.input.Mouse;

public class InvTweaksObfuscation {
   private static final Logger log = InvTweaks.log;
   public Minecraft mc;

   public InvTweaksObfuscation(Minecraft mc_) {
      this.mc = mc_;
   }

   @Nullable
   public static String getNamespacedID(@Nullable String id) {
      if (id == null) {
         return null;
      } else {
         return id.indexOf(58) == -1 ? "minecraft:" + id : id;
      }
   }

   public static int getDisplayWidth() {
      return FMLClientHandler.instance().getClient().displayWidth;
   }

   public static int getDisplayHeight() {
      return FMLClientHandler.instance().getClient().displayHeight;
   }

   public static boolean areItemStacksEqual(@NotNull ItemStack itemStack1, @NotNull ItemStack itemStack2) {
      return itemStack1.isItemEqual(itemStack2) && itemStack1.getCount() == itemStack2.getCount();
   }

   @NotNull
   public static ItemStack getSlotStack(@NotNull Container container, int i) {
      Slot slot = (Slot)container.inventorySlots.get(i);
      return slot == null ? ItemStack.EMPTY : slot.getStack();
   }

   public static int getSlotNumber(Slot slot) {
      try {
         if (slot instanceof CreativeSlot) {
            Slot underlyingSlot = ((CreativeSlot)slot).slot;
            if (underlyingSlot != null) {
               return underlyingSlot.slotNumber;
            }

            log.warn("Creative inventory: Failed to get real slot");
         }
      } catch (Exception var2) {
         log.warn("Failed to access creative slot number");
      }

      return slot.slotNumber;
   }

   @SideOnly(Side.CLIENT)
   @Nullable
   public static Slot getSlotAtMousePosition(@Nullable GuiContainer guiContainer) {
      if (guiContainer != null) {
         Container container = guiContainer.inventorySlots;
         int x = getMouseX(guiContainer);
         int y = getMouseY(guiContainer);

         for(int k = 0; k < container.inventorySlots.size(); ++k) {
            Slot slot = (Slot)container.inventorySlots.get(k);
            if (getIsMouseOverSlot(guiContainer, slot, x, y)) {
               return slot;
            }
         }

         return null;
      } else {
         return null;
      }
   }

   @SideOnly(Side.CLIENT)
   private static boolean getIsMouseOverSlot(@Nullable GuiContainer guiContainer, @NotNull Slot slot, int x, int y) {
      if (guiContainer == null) {
         return false;
      } else {
         x = x - guiContainer.guiLeft;
         y = y - guiContainer.guiTop;
         return x >= slot.xPos - 1 && x < slot.xPos + 16 + 1 && y >= slot.yPos - 1 && y < slot.yPos + 16 + 1;
      }
   }

   @SideOnly(Side.CLIENT)
   private static int getMouseX(@NotNull GuiContainer guiContainer) {
      return Mouse.getEventX() * guiContainer.width / getDisplayWidth();
   }

   @SideOnly(Side.CLIENT)
   private static int getMouseY(@NotNull GuiContainer guiContainer) {
      return guiContainer.height - Mouse.getEventY() * guiContainer.height / getDisplayHeight() - 1;
   }

   @Contract("!null->_")
   public static int getSpecialChestRowSize(Container container) {
      return 0;
   }

   @Contract("!null->_")
   public static boolean isValidChest(Container container) {
      return false;
   }

   @Contract("!null->_")
   public static boolean isLargeChest(Container container) {
      return false;
   }

   @Contract("!null->_")
   public static boolean isValidInventory(Container container) {
      return false;
   }

   @Contract("!null->_")
   public static boolean showButtons(Container container) {
      return false;
   }

   @Contract("!null->_")
   public static Map<ContainerSection, List<Slot>> getContainerSlotMap(Container container) {
      return null;
   }

   public static boolean isGuiContainer(@Nullable Object o) {
      return o != null && o instanceof GuiContainer;
   }

   public static boolean isGuiInventoryCreative(@Nullable Object o) {
      return o != null && o.getClass().equals(GuiContainerCreative.class);
   }

   public static boolean isGuiInventory(@Nullable Object o) {
      return o != null && o.getClass().equals(GuiInventory.class);
   }

   public static boolean isGuiButton(@Nullable Object o) {
      return o != null && o instanceof GuiButton;
   }

   public static boolean isGuiEditSign(@Nullable Object o) {
      return o != null && o.getClass().equals(GuiEditSign.class);
   }

   public static boolean isItemArmor(@Nullable Object o) {
      return o != null && o instanceof ItemArmor;
   }

   public static boolean isBasicSlot(@Nullable Object o) {
      return o != null && (o.getClass().equals(Slot.class) || o.getClass().equals(CreativeSlot.class));
   }

   public static Container getCurrentContainer() {
      Minecraft mc = FMLClientHandler.instance().getClient();
      Container currentContainer = mc.player.inventoryContainer;
      if (isGuiContainer(mc.currentScreen)) {
         currentContainer = ((GuiContainer)mc.currentScreen).inventorySlots;
      }

      return currentContainer;
   }

   public static boolean areSameItemType(@NotNull ItemStack itemStack1, @NotNull ItemStack itemStack2) {
      return !itemStack1.isEmpty() && !itemStack2.isEmpty() && (itemStack1.isItemEqual(itemStack2) || itemStack1.isItemStackDamageable() && itemStack1.getItem() == itemStack2.getItem());
   }

   public static boolean areItemsStackable(@NotNull ItemStack itemStack1, @NotNull ItemStack itemStack2) {
      return !itemStack1.isEmpty() && !itemStack2.isEmpty() && itemStack1.isItemEqual(itemStack2) && itemStack1.isStackable() && (!itemStack1.getHasSubtypes() || itemStack1.getItemDamage() == itemStack2.getItemDamage()) && ItemStack.areItemStackTagsEqual(itemStack1, itemStack2);
   }

   public void addChatMessage(@NotNull String message) {
      if (this.mc.ingameGUI != null) {
         this.mc.ingameGUI.getChatGUI().printChatMessage(new TextComponentString(message));
      }

   }

   public EntityPlayer getThePlayer() {
      return this.mc.player;
   }

   public PlayerControllerMP getPlayerController() {
      return this.mc.playerController;
   }

   @Nullable
   public GuiScreen getCurrentScreen() {
      return this.mc.currentScreen;
   }

   public FontRenderer getFontRenderer() {
      return this.mc.fontRenderer;
   }

   public void displayGuiScreen(GuiScreen parentScreen) {
      this.mc.displayGuiScreen(parentScreen);
   }

   public GameSettings getGameSettings() {
      return this.mc.gameSettings;
   }

   public int getKeyBindingForwardKeyCode() {
      return this.getGameSettings().keyBindForward.keyCode;
   }

   public int getKeyBindingBackKeyCode() {
      return this.getGameSettings().keyBindBack.keyCode;
   }

   public InventoryPlayer getInventoryPlayer() {
      return this.getThePlayer().inventory;
   }

   public NonNullList<ItemStack> getMainInventory() {
      return this.getInventoryPlayer().mainInventory;
   }

   @NotNull
   public ItemStack getHeldStack() {
      return this.getInventoryPlayer().getItemStack();
   }

   @NotNull
   public ItemStack getFocusedStack() {
      return this.getInventoryPlayer().getCurrentItem();
   }

   public int getFocusedSlot() {
      return this.getInventoryPlayer().currentItem;
   }

   public boolean hasTexture(@NotNull ResourceLocation texture) {
      try {
         this.mc.getResourceManager().getResource(texture);
         return true;
      } catch (IOException var3) {
         return false;
      }
   }

   @NotNull
   public ItemStack getOffhandStack() {
      return (ItemStack)this.getInventoryPlayer().offHandInventory.get(0);
   }
}
