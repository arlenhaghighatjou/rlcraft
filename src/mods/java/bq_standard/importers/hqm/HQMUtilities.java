package bq_standard.importers.hqm;

import betterquesting.api.placeholders.PlaceholderConverter;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.JsonHelper;
import bq_standard.core.BQ_Standard;
import bq_standard.importers.hqm.converters.items.HQMItem;
import bq_standard.importers.hqm.converters.items.HQMItemBag;
import bq_standard.importers.hqm.converters.items.HQMItemHeart;
import com.google.gson.JsonObject;
import java.util.HashMap;
import net.minecraft.item.Item;
import net.minecraft.nbt.JsonToNBT;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fluids.Fluid;
import net.minecraftforge.fluids.FluidRegistry;
import net.minecraftforge.fluids.FluidStack;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

public class HQMUtilities {
   private static HashMap<String, HQMItem> itemConverters = new HashMap();

   public HQMUtilities() {
   }

   public static BigItemStack HQMStackT1(JsonObject json) {
      String iID = JsonHelper.GetString(json, "id", "minecraft:stone");
      Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(iID));
      int amount = JsonHelper.GetNumber(json, "amount", Integer.valueOf(1)).intValue();
      int damage = JsonHelper.GetNumber(json, "damage", Integer.valueOf(0)).intValue();
      NBTTagCompound tags = null;
      if (json.has("nbt")) {
         try {
            String rawNbt = json.get("nbt").toString();
            rawNbt = rawNbt.replaceFirst("\"", "");
            rawNbt = rawNbt.substring(0, rawNbt.length() - 1);
            rawNbt = rawNbt.replace(":\\\"", ":\"");
            rawNbt = rawNbt.replace("\\\",", "\",");
            rawNbt = rawNbt.replace("\\\"}", "\"}");
            rawNbt = rawNbt.replace("\\\"]", "\"]");
            rawNbt = rawNbt.replace("[\\\"", "[\"");
            rawNbt = rawNbt.replace("\\n", "\n");
            tags = JsonToNBT.getTagFromJson(rawNbt);
         } catch (Exception var7) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to convert HQM NBT data. This is likely a HQM Gson/Json formatting issue", var7);
         }
      }

      HQMItem hqm = (HQMItem)itemConverters.get(iID.toLowerCase());
      return hqm != null ? hqm.convertItem(damage, amount, tags) : PlaceholderConverter.convertItem(item, iID, amount, damage, "", tags);
   }

   public static BigItemStack HQMStackT2(JsonObject rJson) {
      JsonObject json = JsonHelper.GetObject(rJson, "item");
      String iID = JsonHelper.GetString(json, "id", "minecraft:stone");
      Item item = (Item)Item.REGISTRY.getObject(new ResourceLocation(iID));
      int amount = JsonHelper.GetNumber(rJson, "required", Integer.valueOf(1)).intValue();
      int damage = JsonHelper.GetNumber(json, "damage", Integer.valueOf(0)).intValue();
      boolean oreDict = JsonHelper.GetString(rJson, "precision", "").equalsIgnoreCase("ORE_DICTIONARY");
      NBTTagCompound tags = null;
      if (json.has("nbt")) {
         try {
            String rawNbt = json.get("nbt").toString();
            rawNbt = rawNbt.replaceFirst("\"", "");
            rawNbt = rawNbt.substring(0, rawNbt.length() - 1);
            rawNbt = rawNbt.replace(":\\\"", ":\"");
            rawNbt = rawNbt.replace("\\\",", "\",");
            rawNbt = rawNbt.replace("\\\"}", "\"}");
            rawNbt = rawNbt.replace("\\\"]", "\"]");
            rawNbt = rawNbt.replace("[\\\"", "[\"");
            rawNbt = rawNbt.replace("\\n", "\n");
            tags = JsonToNBT.getTagFromJson(rawNbt);
         } catch (Exception var11) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to convert HQM NBT data. This is likely a HQM Gson/Json formatting issue", var11);
         }
      }

      HQMItem hqm = (HQMItem)itemConverters.get(iID.toLowerCase());
      if (hqm != null) {
         return hqm.convertItem(damage, amount, tags);
      } else {
         BigItemStack stack = PlaceholderConverter.convertItem(item, iID, amount, damage, "", tags);
         if (oreDict && item != null) {
            int[] oreId = OreDictionary.getOreIDs(stack.getBaseStack());
            if (oreId.length > 0) {
               stack.setOreDict(OreDictionary.getOreName(oreId[0]));
            }
         }

         return stack;
      }
   }

   public static FluidStack HQMStackT3(JsonObject json) {
      String name = JsonHelper.GetString(json, "fluid", "water");
      Fluid fluid = FluidRegistry.getFluid(name);
      int amount = JsonHelper.GetNumber(json, "required", Integer.valueOf(1000)).intValue();
      return PlaceholderConverter.convertFluid(fluid, name, amount, (NBTTagCompound)null);
   }

   static {
      itemConverters.put("hardcorequesting:hearts", new HQMItemHeart());
      itemConverters.put("hardcorequesting:bags", new HQMItemBag());
   }
}
