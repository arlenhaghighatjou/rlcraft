package ivorius.reccomplex.network;

import io.netty.buffer.ByteBuf;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import ivorius.reccomplex.world.storage.loot.ItemCollectionSaveHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketEditLootTable implements IMessage {
   private String key;
   private GenericLootTable.Component component;
   private SaveDirectoryData saveDirectoryData;

   public PacketEditLootTable() {
   }

   public PacketEditLootTable(String key, GenericLootTable.Component component, SaveDirectoryData saveDirectoryData) {
      this.key = key;
      this.component = component;
      this.saveDirectoryData = saveDirectoryData;
   }

   public String getKey() {
      return this.key;
   }

   public void setKey(String key) {
      this.key = key;
   }

   public GenericLootTable.Component getComponent() {
      return this.component;
   }

   public void setComponent(GenericLootTable.Component component) {
      this.component = component;
   }

   public SaveDirectoryData getSaveDirectoryData() {
      return this.saveDirectoryData;
   }

   public void setSaveDirectoryData(SaveDirectoryData saveDirectoryData) {
      this.saveDirectoryData = saveDirectoryData;
   }

   public void fromBytes(ByteBuf buf) {
      this.key = ByteBufUtils.readUTF8String(buf);
      this.component = ItemCollectionSaveHandler.INSTANCE.read(buf);
      this.saveDirectoryData = SaveDirectoryData.readFrom(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.key);
      ItemCollectionSaveHandler.INSTANCE.write(buf, this.component);
      this.saveDirectoryData.writeTo(buf);
   }
}
