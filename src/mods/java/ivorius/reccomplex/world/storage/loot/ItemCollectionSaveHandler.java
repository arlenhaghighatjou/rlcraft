package ivorius.reccomplex.world.storage.loot;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import io.netty.buffer.ByteBuf;
import ivorius.reccomplex.json.NBTToJson;
import javax.annotation.Nullable;
import net.minecraftforge.fml.common.network.ByteBufUtils;

public class ItemCollectionSaveHandler {
   public static final ItemCollectionSaveHandler INSTANCE = new ItemCollectionSaveHandler();
   private Gson gson = this.createGson();

   public ItemCollectionSaveHandler() {
   }

   public Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.setPrettyPrinting();
      builder.registerTypeAdapter(GenericLootTable.Component.class, new GenericLootTable.Component.Serializer());
      NBTToJson.registerSafeNBTSerializer(builder);
      return builder.create();
   }

   public void write(ByteBuf data, GenericLootTable.Component component) {
      ByteBufUtils.writeUTF8String(data, this.toJSON(component));
   }

   @Nullable
   public GenericLootTable.Component read(ByteBuf data) {
      try {
         return this.fromJSON(ByteBufUtils.readUTF8String(data));
      } catch (InventoryLoadException var3) {
         var3.printStackTrace();
         return null;
      }
   }

   public String toJSON(GenericLootTable.Component component) {
      return this.gson.toJson(component, GenericLootTable.Component.class);
   }

   public GenericLootTable.Component fromJSON(String json) throws InventoryLoadException {
      try {
         return (GenericLootTable.Component)this.gson.fromJson(json, GenericLootTable.Component.class);
      } catch (JsonSyntaxException var3) {
         throw new InventoryLoadException(var3);
      }
   }
}
