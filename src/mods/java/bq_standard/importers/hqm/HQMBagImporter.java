package bq_standard.importers.hqm;

import betterquesting.api.client.importers.IImporter;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api.utils.FileExtensionFilter;
import betterquesting.api.utils.JsonHelper;
import bq_standard.network.handlers.NetLootImport;
import bq_standard.rewards.loot.LootGroup;
import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;

public class HQMBagImporter implements IImporter {
   public static final HQMBagImporter INSTANCE = new HQMBagImporter();
   private List<LootGroup> hqmLoot = new ArrayList();

   public HQMBagImporter() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.importer.hqm_bag.name";
   }

   public String getUnlocalisedDescription() {
      return "bq_standard.importer.hqm_bag.desc";
   }

   public FileFilter getFileFilter() {
      return new FileExtensionFilter(".json");
   }

   private void ImportJsonBags(JsonArray json) {
      for(JsonElement e : json) {
         if (e != null && e.isJsonObject()) {
            JsonObject jGrp = e.getAsJsonObject();
            LootGroup group = new LootGroup();
            group.name = JsonHelper.GetString(jGrp, "name", "HQM Loot");

            try {
               int tmp = 0;
               JsonArray jWht = JsonHelper.GetArray(jGrp, "weights");

               for(int i = 0; i < jWht.size(); ++i) {
                  JsonElement w = jWht.get(i);
                  if (w != null && w.isJsonPrimitive() && w.getAsJsonPrimitive().isNumber()) {
                     tmp += w.getAsInt() * (jWht.size() - i);
                  }
               }

               group.weight = Math.max(1, tmp / 4);
            } catch (Exception var12) {
               group.weight = 1;
            }

            for(JsonElement e2 : JsonHelper.GetArray(jGrp, "groups")) {
               if (e2 != null && e2.isJsonObject()) {
                  JsonObject je = e2.getAsJsonObject();
                  LootGroup.LootEntry lEntry = new LootGroup.LootEntry();
                  lEntry.weight = JsonHelper.GetNumber(je, "limit", Integer.valueOf(1)).intValue();

                  for(JsonElement ji : JsonHelper.GetArray(je, "items")) {
                     if (ji != null && ji.isJsonObject()) {
                        lEntry.items.add(HQMUtilities.HQMStackT1(ji.getAsJsonObject()));
                     }
                  }

                  group.add(group.nextID(), lEntry);
               }
            }

            this.hqmLoot.add(group);
         }
      }

   }

   public void loadFiles(IQuestDatabase questDB, IQuestLineDatabase lineDB, File[] files) {
      this.hqmLoot.clear();

      for(File selected : files) {
         if (selected != null && selected.exists()) {
            JsonArray json = this.ReadFromFile(selected);
            if (json != null && json.size() > 0) {
               this.ImportJsonBags(json);
            }
         }
      }

      NBTTagCompound tags = new NBTTagCompound();
      NBTTagCompound base = new NBTTagCompound();
      NBTTagList jAry = new NBTTagList();

      for(LootGroup group : this.hqmLoot) {
         NBTTagCompound jGrp = new NBTTagCompound();
         group.writeToNBT(jGrp);
         jAry.appendTag(jGrp);
      }

      base.setTag("groups", jAry);
      tags.setTag("data", base);
      NetLootImport.importLoot(tags);
   }

   private JsonArray ReadFromFile(File file) {
      if (file != null && file.exists()) {
         try {
            InputStreamReader fr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
            JsonArray json = (JsonArray)(new Gson()).fromJson(fr, JsonArray.class);
            fr.close();
            return json;
         } catch (Exception var4) {
            return new JsonArray();
         }
      } else {
         return new JsonArray();
      }
   }
}
