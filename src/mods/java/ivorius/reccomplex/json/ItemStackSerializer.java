package ivorius.reccomplex.json;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.reccomplex.world.gen.feature.structure.registry.MCRegistrySpecial;
import java.lang.reflect.Type;
import javax.annotation.Nonnull;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.datafix.DataFixer;
import net.minecraft.util.datafix.DataFixesManager;
import net.minecraft.util.datafix.FixTypes;

public class ItemStackSerializer implements JsonSerializer<ItemStack>, JsonDeserializer<ItemStack> {
   private MCRegistrySpecial registry;
   private DataFixer fixer;

   public ItemStackSerializer(MCRegistrySpecial registry) {
      this.registry = registry;
      this.fixer = DataFixesManager.createFixer();
   }

   public JsonElement serialize(ItemStack src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject jsonObject = new JsonObject();
      jsonObject.addProperty("id", this.registry.itemHidingMode().containedItemID(src).toString());
      jsonObject.addProperty("damage", Integer.valueOf(src.getItemDamage()));
      jsonObject.addProperty("count", Integer.valueOf(src.getCount()));
      if (src.hasTagCompound()) {
         jsonObject.add("tag", context.serialize(src.getTagCompound()));
      }

      return jsonObject;
   }

   public ItemStack deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject jsonObject = JsonUtils.asJsonObject(json, "ItemStack");
      String id = JsonUtils.getString(jsonObject, "id");
      int damage = JsonUtils.getInt(jsonObject, "damage");
      int count = JsonUtils.getInt(jsonObject, "count");
      ItemStack stack = this.registry.itemHidingMode().constructItemStack(new ResourceLocation(id), count, damage);
      if (jsonObject.has("tag")) {
         NBTTagCompound compound = (NBTTagCompound)context.deserialize(jsonObject.get("tag"), NBTTagCompound.class);
         stack.setTagCompound(compound);
      } else if (jsonObject.has("tagBase64")) {
         NBTTagCompound compound = NBTToJson.getNBTFromBase64(JsonUtils.getString(jsonObject, "tagBase64"));
         stack.setTagCompound(compound);
      }

      NBTTagCompound fixed = this.fixItemStack(stack);
      ItemStack loadedFixed = new ItemStack(fixed);
      stack = this.registry.itemHidingMode().constructItemStack(new ResourceLocation(fixed.getString("id")), loadedFixed.getCount(), loadedFixed.getMetadata());
      if (loadedFixed.hasTagCompound()) {
         stack.setTagCompound(loadedFixed.getTagCompound());
      }

      return stack;
   }

   @Nonnull
   protected NBTTagCompound fixItemStack(ItemStack stack) {
      NBTTagCompound postCompound = new NBTTagCompound();
      stack.writeToNBT(postCompound);
      this.fixer.process(FixTypes.ITEM_INSTANCE, postCompound);
      return postCompound;
   }
}
