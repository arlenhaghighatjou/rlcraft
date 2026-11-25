package vazkii.quark.management.feature;

import com.google.common.collect.Lists;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.inventory.GuiChest;
import net.minecraft.client.gui.inventory.GuiContainer;
import net.minecraft.client.gui.inventory.GuiShulkerBox;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.inventory.Container;
import net.minecraft.inventory.ContainerChest;
import net.minecraft.inventory.IInventory;
import net.minecraft.inventory.Slot;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.items.SlotItemHandler;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.api.IChestButtonCallback;
import vazkii.quark.base.Quark;
import vazkii.quark.base.client.ModKeybinds;
import vazkii.quark.base.handler.DropoffHandler;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;
import vazkii.quark.base.network.message.MessageDropoff;
import vazkii.quark.base.network.message.MessageRestock;
import vazkii.quark.management.client.gui.GuiButtonChest;
import vazkii.quark.management.client.gui.GuiButtonShulker;

public class ChestButtons extends Feature {
   public static ChestButtons.ButtonInfo deposit;
   public static ChestButtons.ButtonInfo smartDeposit;
   public static ChestButtons.ButtonInfo restock;
   public static ChestButtons.ButtonInfo extract;
   public static ChestButtons.ButtonInfo sort;
   public static ChestButtons.ButtonInfo sortPlayer;
   public static boolean debugClassnames;
   public static List<String> classnames;
   public static List<ResourceLocation> dropoffTiles;
   public static boolean dropoffAnyChestTile;
   public static boolean dropoffAnyChestBlock;
   public static boolean dropoffAnyChestMod;
   @SideOnly(Side.CLIENT)
   public static List<GuiButtonChest> chestButtons;

   public ChestButtons() {
   }

   public void setupConfig() {
      deposit = this.loadButtonInfo("deposit", "", -18, -50);
      smartDeposit = this.loadButtonInfo("smart_deposit", "", -18, -30);
      restock = this.loadButtonInfo("restock", "", -18, 45);
      extract = this.loadButtonInfo("extract", "", -18, 25);
      sort = this.loadButtonInfo("sort", "The Sort button is only available if the Inventory Sorting feature is enable", -18, -70);
      sortPlayer = this.loadButtonInfo("sort_player", "The Sort button is only available if the Inventory Sorting feature is enable", -18, 5);
      debugClassnames = this.loadPropBool("Debug Classnames", "Set this to true to print out the names of all GUIs you open to the log. This is used to fill in the \"Forced GUIs\" list.", false);
      String[] classnamesArr = this.loadPropStringList("Forced GUIs", "GUIs in which the chest buttons should be forced to show up. Use the \"Debug Classnames\" option to find the names.", new String[0]);
      classnames = Lists.newArrayList(classnamesArr);
      String[] dropoffArr = this.loadPropStringList("Dropoff Enabled Blocks", "Blocks with inventories which do not explicitly accept dropoffs, but should be treated as though they do.", new String[]{"minecraft:chest", "minecraft:trapped_chest", "minecraft:shulker_box"});
      dropoffTiles = (List)Arrays.stream(dropoffArr).map(ResourceLocation::new).collect(Collectors.toList());
      dropoffAnyChestTile = this.loadPropBool("Dropoff to Any Chest Tile", "Allow anything with 'chest' in its TileEntity identifier to be used as a dropoff inventory?", true);
      dropoffAnyChestBlock = this.loadPropBool("Dropoff to Any Chest Block", "Allow anything with 'chest' in its block identifier to be used as a dropoff inventory?", true);
      dropoffAnyChestMod = this.loadPropBool("Dropoff to Any Chest Mod", "Allow any block with 'chest' in its modid identifier to be used as a dropoff inventory?", true);
   }

   public static boolean overriddenDropoff(TileEntity tile) {
      Block block = tile.getBlockType();
      ResourceLocation blockType = block == null ? null : block.getRegistryName();
      ResourceLocation tileType = TileEntity.getKey(tile.getClass());
      if (blockType != null) {
         if (dropoffTiles.contains(blockType)) {
            return true;
         }

         if (dropoffAnyChestBlock && blockType.getResourcePath().contains("chest")) {
            return true;
         }

         if (dropoffAnyChestMod && blockType.getResourceDomain().contains("chest")) {
            return true;
         }
      }

      return tileType != null && dropoffAnyChestTile && tileType.getResourcePath().contains("chest");
   }

   private ChestButtons.ButtonInfo loadButtonInfo(String name, String comment, int xShift, int yShift) {
      ChestButtons.ButtonInfo info = new ChestButtons.ButtonInfo();
      String category = this.configCategory + "." + name;
      info.enabled = ModuleLoader.config.getBoolean("Enabled", category, true, comment);
      info.xShift = ModuleLoader.config.getInt("X Position", category, xShift, Integer.MIN_VALUE, Integer.MAX_VALUE, "");
      info.yShift = ModuleLoader.config.getInt("Y Position", category, yShift, Integer.MIN_VALUE, Integer.MAX_VALUE, "");
      return info;
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      if (chestButtons == null) {
         chestButtons = new ArrayList();
      }

      ModKeybinds.initChestKeys();
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void initGui(Post event) {
      if (event.getGui() instanceof GuiContainer) {
         GuiContainer guiInv = (GuiContainer)event.getGui();
         Container container = guiInv.inventorySlots;
         EntityPlayer player = Minecraft.getMinecraft().player;
         if (debugClassnames) {
            Quark.LOG.info("Opening GUI " + guiInv.getClass().getName());
         }

         boolean accept = guiInv instanceof IChestButtonCallback || guiInv instanceof GuiChest || guiInv instanceof GuiShulkerBox || classnames.contains(guiInv.getClass().getName());
         if (!accept) {
            for(Slot s : container.inventorySlots) {
               if (s instanceof SlotItemHandler && DropoffHandler.isValidChest(player, ((SlotItemHandler)s).getItemHandler())) {
                  accept = true;
                  break;
               }

               IInventory inv = s.inventory;
               if (inv != null && DropoffHandler.isValidChest(player, inv)) {
                  accept = true;
                  break;
               }
            }
         }

         if (!accept) {
            return;
         }

         if (chestButtons == null) {
            chestButtons = new ArrayList();
         }

         chestButtons.clear();

         for(Slot s : container.inventorySlots) {
            if (s.inventory == player.inventory && s.getSlotIndex() == 9) {
               addButtonAndKeybind(event, extract, GuiButtonChest.Action.EXTRACT, guiInv, 13210, s, ModKeybinds.chestExtractKey);
               addButtonAndKeybind(event, restock, GuiButtonChest.Action.RESTOCK, guiInv, 13211, s, ModKeybinds.chestRestockKey);
               addButtonAndKeybind(event, deposit, GuiButtonChest.Action.DEPOSIT, guiInv, 13212, s, ModKeybinds.chestDropoffKey);
               addButtonAndKeybind(event, smartDeposit, GuiButtonChest.Action.SMART_DEPOSIT, guiInv, 13213, s, ModKeybinds.chestMergeKey);
               if (ModuleLoader.isFeatureEnabled(InventorySorting.class)) {
                  addButtonAndKeybind(event, sort, GuiButtonChest.Action.SORT, guiInv, 13214, s, ModKeybinds.chestSortKey);
                  addButtonAndKeybind(event, sortPlayer, GuiButtonChest.Action.SORT_PLAYER, guiInv, 13215, s, ModKeybinds.playerSortKey);
               }
               break;
            }
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public static void addButtonAndKeybind(Post event, ChestButtons.ButtonInfo info, GuiButtonChest.Action action, GuiContainer guiInv, int index, Slot s, KeyBinding kb) {
      if (info.enabled) {
         addButtonAndKeybind(event, action, guiInv, index, info.xShift, s.yPos + info.yShift, kb);
      }

   }

   @SideOnly(Side.CLIENT)
   public static void addButtonAndKeybind(Post event, GuiButtonChest.Action action, GuiContainer guiInv, int index, int x, int y, KeyBinding kb) {
      addButtonAndKeybind(event, action, guiInv, index, x, y, kb, (Predicate)null);
   }

   @SideOnly(Side.CLIENT)
   public static void addButtonAndKeybind(Post event, GuiButtonChest.Action action, GuiContainer guiInv, int index, int x, int y, KeyBinding kb, Predicate<GuiScreen> predicate) {
      int left = guiInv.getGuiLeft();
      int top = guiInv.getGuiTop();
      GuiButtonChest button;
      if (guiInv instanceof GuiShulkerBox) {
         button = new GuiButtonShulker((GuiShulkerBox)guiInv, action, index, x, y, left, top);
      } else {
         button = new GuiButtonChest(guiInv, action, index, x, y, left, top, predicate);
      }

      if (!(guiInv instanceof IChestButtonCallback) || ((IChestButtonCallback)guiInv).onAddChestButton(button, action.ordinal())) {
         if (guiInv.inventorySlots instanceof ContainerChest) {
            ContainerChest chest = (ContainerChest)guiInv.inventorySlots;
            IInventory chestInv = chest.getLowerChestInventory();
            if (chestInv.getName().equals(Blocks.ENDER_CHEST.getLocalizedName())) {
               button.setEnder(true);
            }
         }

         if (chestButtons == null) {
            chestButtons = new ArrayList();
         }

         chestButtons.add(button);
         event.getButtonList().add(button);
         if (kb != null) {
            ModKeybinds.keybindButton(kb, button);
         }

      }
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void performAction(Pre event) {
      if (event.getButton() instanceof GuiButtonChest) {
         GuiButtonChest buttonChest = (GuiButtonChest)event.getButton();
         GuiButtonChest.Action action = buttonChest.action;
         switch(action) {
         case SMART_DEPOSIT:
            NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(true, true));
            event.setCanceled(true);
            break;
         case DEPOSIT:
            NetworkHandler.INSTANCE.sendToServer(new MessageDropoff(false, true));
            event.setCanceled(true);
            break;
         case EXTRACT:
            NetworkHandler.INSTANCE.sendToServer(new MessageRestock(false));
            event.setCanceled(true);
            break;
         case RESTOCK:
            NetworkHandler.INSTANCE.sendToServer(new MessageRestock(true));
            event.setCanceled(true);
         }
      }

   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   private static class ButtonInfo {
      private boolean enabled;
      private int xShift;
      private int yShift;

      private ButtonInfo() {
      }
   }
}
