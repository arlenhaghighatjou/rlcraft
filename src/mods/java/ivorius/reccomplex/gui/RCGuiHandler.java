package ivorius.reccomplex.gui;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import net.lax1dude.eaglercraft.v1_8.netty.Unpooled;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.files.loading.ResourceDirectory;
import ivorius.reccomplex.gui.container.IvGuiHandler;
import ivorius.reccomplex.gui.container.IvGuiRegistry;
import ivorius.reccomplex.gui.loot.ContainerEditLootTableItems;
import ivorius.reccomplex.gui.loot.GuiEditLootTable;
import ivorius.reccomplex.gui.loot.GuiEditLootTableItems;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericItemCollectionRegistry;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import ivorius.reccomplex.world.storage.loot.ItemCollectionSaveHandler;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.inventory.Container;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class RCGuiHandler implements IvGuiHandler {
   public static final int editLootTable = 0;
   public static final int editLootTableItems = 1;

   public RCGuiHandler() {
   }

   protected static void openComponentGui(EntityPlayer player, String key, GenericLootTable.Component component, SaveDirectoryData saveDirectoryData, int guiID) {
      if (key == null) {
         key = "New Generator";
      }

      if (component == null) {
         component = GenericLootTable.Component.createDefaultComponent();
      }

      if (saveDirectoryData == null) {
         SimpleLeveledRegistry<GenericLootTable.Component>.Status status = GenericItemCollectionRegistry.INSTANCE.status(key);
         saveDirectoryData = SaveDirectoryData.defaultData(key, status != null ? ResourceDirectory.custom(status.isActive()) : null, RecurrentComplex.loader.tryFindIDs(ResourceDirectory.ACTIVE.toPath(), "rcig"), RecurrentComplex.loader.tryFindIDs(ResourceDirectory.INACTIVE.toPath(), "rcig"));
      }

      ByteBuf buf = Unpooled.buffer();
      ByteBufUtils.writeUTF8String(buf, key);
      ItemCollectionSaveHandler.INSTANCE.write(buf, component);
      saveDirectoryData.writeTo(buf);
      IvGuiRegistry.INSTANCE.openGui(player, "reccomplex", guiID, buf);
   }

   public static void editLootTableComponent(EntityPlayer player, String key, GenericLootTable.Component component, SaveDirectoryData saveDirectoryData) {
      openComponentGui(player, key, component, saveDirectoryData, 0);
   }

   public static void editLootTableComponentItems(EntityPlayer player, String key, GenericLootTable.Component component, SaveDirectoryData saveDirectoryData) {
      openComponentGui(player, key, component, saveDirectoryData, 1);
   }

   public Container getServerGuiElement(int id, EntityPlayerMP player, ByteBuf data) {
      if (id == 0) {
         return null;
      } else {
         if (id == 1) {
            if (!player.canUseCommand(2, "give")) {
               return null;
            }

            String key = ByteBufUtils.readUTF8String(data);
            GenericLootTable.Component component = ItemCollectionSaveHandler.INSTANCE.read(data);
            if (component != null) {
               return new ContainerEditLootTableItems(player, key, component);
            }
         }

         return null;
      }
   }

   public Object getClientGuiElement(int id, EntityPlayer player, ByteBuf data) {
      if (id == 0 || id == 1) {
         String key = ByteBufUtils.readUTF8String(data);
         GenericLootTable.Component component = ItemCollectionSaveHandler.INSTANCE.read(data);
         SaveDirectoryData saveDirectoryData = SaveDirectoryData.readFrom(data);
         if (component != null) {
            if (id == 0) {
               return new GuiEditLootTable(player, component, key, saveDirectoryData);
            }

            return new GuiEditLootTableItems(player, component, key, saveDirectoryData);
         }
      }

      return null;
   }
}
