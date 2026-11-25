package com.ferreusveritas.dynamictrees.blocks;

import com.ferreusveritas.dynamictrees.api.treedata.ILeavesProperties;
import com.ferreusveritas.dynamictrees.util.JsonHelper;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;
import java.util.function.BiConsumer;
import java.util.function.Function;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class LeavesPaging {
   private static HashMap<String, HashMap<Integer, BlockDynamicLeaves>> modLeavesArray = new HashMap();
   private static HashMap<String, Integer> modLastSeq = new HashMap();

   public LeavesPaging() {
   }

   private static String autoModId(@Nullable String modid) {
      if (modid == null || "".equals(modid)) {
         ModContainer mc = Loader.instance().activeModContainer();
         modid = mc == null ? "dynamictrees" : mc.getModId();
      }

      return modid;
   }

   public static BlockDynamicLeaves getNextLeavesBlock(@Nullable String modid, @Nonnull ILeavesProperties leavesProperties) {
      return getLeavesBlockForSequence(modid, getNextSequenceNumber(modid), leavesProperties);
   }

   public static int getNextSequenceNumber(@Nullable String modid) {
      modid = autoModId(modid);
      int seq = ((Integer)modLastSeq.computeIfAbsent(modid, (i) -> {
         return Integer.valueOf(0);
      })).intValue();
      modLastSeq.put(modid, Integer.valueOf(seq + 1));
      return seq;
   }

   public static int getLastSequenceNumber(@Nullable String modid) {
      return ((Integer)modLastSeq.computeIfAbsent(autoModId(modid), (i) -> {
         return Integer.valueOf(0);
      })).intValue() - 1;
   }

   public static BlockDynamicLeaves getLeavesBlockForSequence(@Nullable String modid, int seq, @Nonnull ILeavesProperties leavesProperties) {
      BlockDynamicLeaves leaves = getLeavesBlockForSequence(modid, seq);
      int tree = seq & 3;
      leavesProperties.setDynamicLeavesState(leaves.getDefaultState().withProperty(BlockDynamicLeaves.TREE, Integer.valueOf(tree)));
      leaves.setProperties(tree, leavesProperties);
      return leaves;
   }

   private static BlockDynamicLeaves getLeavesBlockForSequence(@Nullable String modid, int seq) {
      int key = seq / 4;
      String regname = "leaves" + key;
      return (BlockDynamicLeaves)getLeavesMapForModId(modid).computeIfAbsent(Integer.valueOf(key), (k) -> {
         return (BlockDynamicLeaves)(new BlockDynamicLeaves()).setDefaultNaming(autoModId(modid), regname);
      });
   }

   public static Map<Integer, BlockDynamicLeaves> getLeavesMapForModId(@Nullable String modid) {
      return (Map)modLeavesArray.computeIfAbsent(autoModId(modid), (k) -> {
         return new HashMap();
      });
   }

   public static Map<String, ILeavesProperties> buildAll(Object... leavesProperties) {
      return buildAllForMod(autoModId(""), leavesProperties);
   }

   public static Map<String, ILeavesProperties> buildAllForMod(String modid, Object... leavesProperties) {
      Map<String, ILeavesProperties> leafMap = new HashMap();

      for(int i = 0; i < (leavesProperties.length & -2); i += 2) {
         String label = leavesProperties[i].toString();
         Object obj = leavesProperties[i + 1];
         ILeavesProperties newLp = LeavesProperties.NULLPROPERTIES;
         if (obj instanceof ILeavesProperties) {
            newLp = (ILeavesProperties)obj;
         } else if (obj instanceof String && !"".equals(obj)) {
            newLp = new LeavesPropertiesJson((String)obj);
         }

         getNextLeavesBlock(modid, newLp);
         leafMap.put(label, newLp);
      }

      return leafMap;
   }

   public static Map<String, ILeavesProperties> build(String jsonData) {
      return build(autoModId(""), jsonData);
   }

   public static Map<String, ILeavesProperties> build(String modid, String jsonData) {
      return build(modid, LeavesPropertiesJson.getJsonObject(jsonData));
   }

   public static Map<String, ILeavesProperties> build(JsonObject root) {
      return build(autoModId(""), root);
   }

   public static Map<String, ILeavesProperties> build(String modid, JsonObject root) {
      Map<String, ILeavesProperties> leafMap = new HashMap();
      if (root != null) {
         for(Entry<String, JsonElement> entry : root.entrySet()) {
            String label = (String)entry.getKey();
            ILeavesProperties newLp = LeavesProperties.NULLPROPERTIES;
            if (!label.startsWith("-")) {
               JsonObject jsonObj = ((JsonElement)entry.getValue()).getAsJsonObject();
               newLp = new LeavesPropertiesJson(jsonObj);
            }

            getNextLeavesBlock(modid, newLp);
            leafMap.put(label, newLp);
         }
      }

      return leafMap;
   }

   public static Map<String, ILeavesProperties> build(ResourceLocation jsonLocation) {
      return build(autoModId(""), jsonLocation);
   }

   public static Map<String, ILeavesProperties> build(String modid, ResourceLocation jsonLocation) {
      JsonElement element = JsonHelper.load(jsonLocation);
      if (element != null && element.isJsonObject()) {
         return build(element.getAsJsonObject());
      } else {
         Logger.getLogger("dynamictrees").log(Level.SEVERE, "Error building leaves paging for mod: " + modid + " at " + jsonLocation);
         return null;
      }
   }

   @SideOnly(Side.CLIENT)
   public static void setStateMappers() {
      LeavesStateMapper mapper = new LeavesStateMapper();

      for(String modId : modLeavesArray.keySet()) {
         getLeavesMapForModId(modId).forEach((key, leaves) -> {
            ModelLoader.setCustomStateMapper(leaves, mapper);
         });
      }

   }

   public static void cleanUp() {
      modLeavesArray = new HashMap();
      modLastSeq = new HashMap();
   }
}
