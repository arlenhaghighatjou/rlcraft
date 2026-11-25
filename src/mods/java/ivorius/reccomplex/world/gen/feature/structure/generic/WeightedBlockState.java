package ivorius.reccomplex.world.gen.feature.structure.generic;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import ivorius.ivtoolkit.blocks.BlockStates;
import ivorius.ivtoolkit.random.WeightedSelector.Item;
import ivorius.ivtoolkit.tools.MCRegistry;
import ivorius.reccomplex.RecurrentComplex;
import ivorius.reccomplex.json.JsonUtils;
import ivorius.reccomplex.json.NBTToJson;
import java.lang.reflect.Type;
import javax.annotation.Nullable;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTException;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;

public class WeightedBlockState implements Item {
   private static Gson gson = createGson();
   @Nullable
   public Double weight;
   @Nullable
   public IBlockState state;
   @Nullable
   public NBTTagCompound tileEntityInfo;

   public WeightedBlockState(@Nullable Double weight, @Nullable IBlockState state, @Nullable NBTTagCompound tileEntityInfo) {
      this.weight = weight;
      this.state = state;
      this.tileEntityInfo = tileEntityInfo;
   }

   public WeightedBlockState(MCRegistry registry, NBTTagCompound compound) {
      this.weight = compound.hasKey("weight") ? compound.getDouble("weight") : null;
      Block block = compound.hasKey("block") ? registry.blockFromID(new ResourceLocation(compound.getString("block"))) : null;
      this.state = block != null ? BlockStates.fromMetadata(block, compound.getInteger("meta")) : null;
      this.tileEntityInfo = compound.hasKey("tileEntityInfo") ? tryParse(compound.getString("tileEntityInfo")) : (compound.hasKey("tileEntity") ? (NBTTagCompound)compound.getTag("tileEntity") : null);
   }

   public static Gson createGson() {
      GsonBuilder builder = new GsonBuilder();
      builder.registerTypeAdapter(WeightedBlockState.class, new WeightedBlockState.Serializer(RecurrentComplex.specialRegistry));
      builder.registerTypeAdapter(WeightedBlockState.class, new WeightedBlockState.Serializer(RecurrentComplex.specialRegistry));
      NBTToJson.registerSafeNBTSerializer(builder);
      return builder.create();
   }

   public static Gson getGson() {
      return gson;
   }

   public static NBTTagCompound tryParse(String json) {
      if (json.trim().length() == 0) {
         return null;
      } else {
         NBTTagCompound nbt = null;

         try {
            nbt = JsonToNBT.getTagFromJson(json);
         } catch (NBTException var3) {
            ;
         }

         return nbt;
      }
   }

   public double getWeight() {
      return this.weight != null ? this.weight.doubleValue() : 1.0D;
   }

   public NBTTagCompound writeToNBT(MCRegistry registry) {
      NBTTagCompound compound = new NBTTagCompound();
      if (this.weight != null) {
         compound.setDouble("weight", this.weight.doubleValue());
      }

      if (this.state != null) {
         compound.setString("block", registry.idFromBlock(this.state.getBlock()).toString());
         compound.setInteger("meta", BlockStates.toMetadata(this.state));
      }

      if (this.tileEntityInfo != null) {
         compound.setTag("tileEntity", this.tileEntityInfo);
      }

      return compound;
   }

   public WeightedBlockState copy() {
      return new WeightedBlockState(this.weight, this.state, this.tileEntityInfo != null ? this.tileEntityInfo.copy() : null);
   }

   public static class Serializer implements JsonDeserializer<WeightedBlockState>, JsonSerializer<WeightedBlockState> {
      private MCRegistry registry;

      public Serializer(MCRegistry registry) {
         this.registry = registry;
      }

      public WeightedBlockState deserialize(JsonElement jsonElement, Type par2Type, JsonDeserializationContext context) {
         JsonObject jsonObject = JsonUtils.asJsonObject(jsonElement, "blockState");
         Double weight = jsonObject.has("weight") ? JsonUtils.getDouble(jsonObject, "weight") : null;
         IBlockState state = BlockStates.fromMetadata(this.registry.blockFromID(new ResourceLocation(JsonUtils.getString(jsonObject, "block", "air"))), JsonUtils.getInt(jsonObject, "metadata", 0));
         NBTTagCompound tileEntityInfo = JsonUtils.hasString(jsonObject, "tileEntityInfo") ? WeightedBlockState.tryParse(JsonUtils.getString(jsonObject, "tileEntityInfo")) : (jsonObject.has("tileEntity") ? (NBTTagCompound)WeightedBlockState.gson.fromJson(jsonObject.get("tileEntity"), NBTTagCompound.class) : null);
         return new WeightedBlockState(weight, state, tileEntityInfo);
      }

      public JsonElement serialize(WeightedBlockState source, Type par2Type, JsonSerializationContext context) {
         JsonObject jsonObject = new JsonObject();
         if (source.weight != null) {
            jsonObject.addProperty("weight", source.weight);
         }

         jsonObject.addProperty("block", this.registry.idFromBlock(source.state.getBlock()).toString());
         jsonObject.addProperty("metadata", Integer.valueOf(BlockStates.toMetadata(source.state)));
         jsonObject.addProperty("tileEntity", WeightedBlockState.gson.toJson(source.tileEntityInfo));
         return jsonObject;
      }
   }
}
