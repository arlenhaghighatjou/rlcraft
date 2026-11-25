package ivorius.reccomplex.network;

import net.lax1dude.eaglercraft.v1_8.netty.ByteBuf;
import ivorius.reccomplex.utils.SaveDirectoryData;
import ivorius.reccomplex.world.storage.loot.GenericLootTable;
import ivorius.reccomplex.world.storage.loot.ItemCollectionSaveHandler;
import net.minecraftforge.fml.common.network.ByteBufUtils;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;

public class PacketSaveLootTable implements IMessage {
   private String key;
   private GenericLootTable.Component component;
   private SaveDirectoryData.Result saveDirectoryDataResult;

   public PacketSaveLootTable() {
   }

   public PacketSaveLootTable(String key, GenericLootTable.Component component, SaveDirectoryData.Result saveDirectoryDataResult) {
      this.key = key;
      this.component = component;
      this.saveDirectoryDataResult = saveDirectoryDataResult;
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

   public SaveDirectoryData.Result getSaveDirectoryDataResult() {
      return this.saveDirectoryDataResult;
   }

   public void setSaveDirectoryDataResult(SaveDirectoryData.Result saveDirectoryDataResult) {
      this.saveDirectoryDataResult = saveDirectoryDataResult;
   }

   public void fromBytes(ByteBuf buf) {
      this.key = ByteBufUtils.readUTF8String(buf);
      this.component = ItemCollectionSaveHandler.INSTANCE.read(buf);
      this.saveDirectoryDataResult = SaveDirectoryData.Result.readFrom(buf);
   }

   public void toBytes(ByteBuf buf) {
      ByteBufUtils.writeUTF8String(buf, this.key);
      ItemCollectionSaveHandler.INSTANCE.write(buf, this.component);
      this.saveDirectoryDataResult.writeTo(buf);
   }
}
