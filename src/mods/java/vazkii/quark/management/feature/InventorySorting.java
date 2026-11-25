package vazkii.quark.management.feature;

import com.google.common.collect.Lists;
import java.util.List;
import java.util.function.Predicate;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiContainerCreative;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.Slot;
import net.minecraft.inventory.SlotCrafting;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.handler.SortingHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.network.message.MessageSortInventory;
import vazkii.quark.management.client.gui.GuiButtonChest;

public class InventorySorting extends Feature {
   public static int xPos;
   public static int yPos;
   public static int xPosC;
   public static int yPosC;
   public static boolean enablePlayerButton;
   public static List<String> classnames;

   public InventorySorting() {
   }

   public void setupConfig() {
      xPos = this.loadPropInt("Position X", "", -20);
      yPos = this.loadPropInt("Position Y ", "", 30);
      xPosC = this.loadPropInt("Position X (Creative)", "", 8);
      yPosC = this.loadPropInt("Position Y (Creative)", "", -20);
      enablePlayerButton = this.loadPropBool("Enable Button in Player Inventory", "", true);
      String[] classnamesArr = this.loadPropStringList("Forced GUIs", "GUIs in which the sort button should be forced to show up. Use the \"Debug Classnames\" option in chest buttons to find the names.", new String[0]);
      classnames = Lists.newArrayList(classnamesArr);
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ModKeybinds.initPlayerSortingKey();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void initGui(Post event) {
      if (enablePlayerButton && event.getGui() instanceof GuiInventory || event.getGui() instanceof GuiContainerCreative || classnames.contains(event.getGui().getClass().getName())) {
         GuiContainer guiInv = (GuiContainer)event.getGui();
         GuiContainerCreative creativeInv = null;
         if (guiInv instanceof GuiContainerCreative) {
            creativeInv = (GuiContainerCreative)guiInv;
         }

         if (creativeInv == null && Minecraft.getMinecraft().player.capabilities.isCreativeMode) {
            return;
         }

         Container container = guiInv.inventorySlots;

         for(Slot s : container.inventorySlots) {
            if (creativeInv != null || s instanceof SlotCrafting) {
               if (creativeInv == null) {
                  ChestButtons.addButtonAndKeybind(event, GuiButtonChest.Action.SORT, guiInv, 13212, s.xPos + xPos, s.yPos + yPos, ModKeybinds.playerSortKey);
                  break;
               }

               if (s.getSlotIndex() == 15) {
                  ChestButtons.addButtonAndKeybind(event, GuiButtonChest.Action.SORT, guiInv, 132112, s.xPos + xPosC, s.yPos + yPosC, ModKeybinds.playerSortKey, (gui) -> {
                     return ((GuiContainerCreative)gui).getSelectedTabIndex() == CreativeTabs.INVENTORY.getTabIndex();
                  });
                  break;
               }
            }
         }
      }

   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void performAction(Pre event) {
      if (event.getButton() instanceof GuiButtonChest) {
         GuiButtonChest.Action a = ((GuiButtonChest)event.getButton()).action;
         if (a.isSortAction()) {
            boolean forcePlayer = a == GuiButtonChest.Action.SORT_PLAYER;
            NetworkHandler.INSTANCE.sendToServer(new MessageSortInventory(forcePlayer));
            SortingHandler.sortInventory(Minecraft.getMinecraft().player, forcePlayer);
            event.setCanceled(true);
         }
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public String[] getIncompatibleMods() {
      return new String[]{"inventorytweaks", "inventorysorter"};
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
