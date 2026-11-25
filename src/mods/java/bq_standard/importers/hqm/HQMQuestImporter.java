package bq_standard.importers.hqm;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.client.importers.IImporter;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.FileExtensionFilter;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.storage.IDatabaseNBT;
import betterquesting.api2.utils.BQThreadedIO;
import bq_standard.core.BQ_Standard;
import bq_standard.importers.hqm.converters.HQMRep;
import bq_standard.importers.hqm.converters.rewards.HQMRewardChoice;
import bq_standard.importers.hqm.converters.rewards.HQMRewardCommand;
import bq_standard.importers.hqm.converters.rewards.HQMRewardReputation;
import bq_standard.importers.hqm.converters.rewards.HQMRewardStandard;
import bq_standard.importers.hqm.converters.tasks.HQMTaskAdvancement;
import bq_standard.importers.hqm.converters.tasks.HQMTaskBlockBreak;
import bq_standard.importers.hqm.converters.tasks.HQMTaskBlockPlace;
import bq_standard.importers.hqm.converters.tasks.HQMTaskCraft;
import bq_standard.importers.hqm.converters.tasks.HQMTaskDetect;
import bq_standard.importers.hqm.converters.tasks.HQMTaskKill;
import bq_standard.importers.hqm.converters.tasks.HQMTaskLocation;
import bq_standard.importers.hqm.converters.tasks.HQMTaskReputaion;
import bq_standard.importers.hqm.converters.tasks.HQMTaskTame;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.concurrent.Callable;
import java.util.concurrent.Future;
import java.util.function.Function;
import net.minecraft.init.Items;
import net.minecraft.nbt.NBTTagList;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class HQMQuestImporter implements IImporter {
   public static final HQMQuestImporter INSTANCE = new HQMQuestImporter();
   private static final FileFilter FILTER = new FileExtensionFilter(".json");
   private static HashMap<String, Function<JsonObject, ITask[]>> taskConverters = new HashMap();
   private static HashMap<String, Function<JsonElement, IReward[]>> rewardConverters = new HashMap();
   public HashMap<String, HQMRep> reputations = new HashMap();
   private HashMap<String, IQuest> idMap = new HashMap();
   private static final Gson GSON = (new GsonBuilder()).setPrettyPrinting().create();

   public HQMQuestImporter() {
   }

   public FileFilter getFileFilter() {
      return FILTER;
   }

   public String getUnlocalisedName() {
      return "bq_standard.importer.hqm_quest.name";
   }

   public String getUnlocalisedDescription() {
      return "bq_standard.importer.hqm_quest.desc";
   }

   public void loadFiles(IQuestDatabase questDB, IQuestLineDatabase lineDB, File[] files) {
      this.reputations.clear();
      this.idMap.clear();

      for(File selected : files) {
         if (selected != null && selected.exists() && selected.getName().equalsIgnoreCase("reputations.json")) {
            JsonArray json = ReadArrayFromFile(selected);
            this.LoadReputations(json);
         }
      }

      for(File selected : files) {
         if (selected != null && selected.exists() && !selected.getName().equalsIgnoreCase("reputations.json")) {
            JsonObject json = JsonHelper.ReadFromFile(selected);
            this.ImportQuestLine(questDB, lineDB, json);
         }
      }

   }

   private static JsonArray ReadArrayFromFile(File file) {
      Future<JsonArray> task = BQThreadedIO.INSTANCE.enqueue(() -> {
         if (file != null && file.exists()) {
            try {
               InputStreamReader fr = new InputStreamReader(new FileInputStream(file), StandardCharsets.UTF_8);
               Throwable var16 = null;

               JsonArray var17;
               try {
                  var17 = (JsonArray)GSON.fromJson(fr, JsonArray.class);
               } catch (Throwable var13) {
                  var16 = var13;
                  throw var13;
               } finally {
                  if (fr != null) {
                     if (var16 != null) {
                        try {
                           fr.close();
                        } catch (Throwable var12) {
                           var16.addSuppressed(var12);
                        }
                     } else {
                        fr.close();
                     }
                  }

               }

               return var17;
            } catch (Exception var15) {
               QuestingAPI.getLogger().log(Level.ERROR, "An error occured while loading JSON from file:", var15);
               int i = 0;

               File bkup;
               for(bkup = new File(file.getParent(), "malformed_" + file.getName() + i + ".json"); bkup.exists(); bkup = new File(file.getParent(), "malformed_" + file.getName() + i + ".json")) {
                  ++i;
               }

               QuestingAPI.getLogger().log(Level.ERROR, "Creating backup at: " + bkup.getAbsolutePath());
               JsonHelper.CopyPaste(file, bkup);
               return new JsonArray();
            }
         } else {
            return new JsonArray();
         }
      });

      try {
         return (JsonArray)task.get();
      } catch (Exception var3) {
         QuestingAPI.getLogger().error("Unable to read from file " + file, var3);
         return new JsonArray();
      }
   }

   private void LoadReputations(JsonArray jsonRoot) {
      if (jsonRoot != null && jsonRoot.size() > 0) {
         int i = -1;

         for(JsonElement e : jsonRoot) {
            if (e instanceof JsonObject) {
               JsonObject jRep = e.getAsJsonObject();
               String repName = "Reputation(" + i + ")";
               if (jRep.has("Name")) {
                  repName = JsonHelper.GetString(jRep, "Name", repName);
               }

               if (jRep.has("name")) {
                  repName = JsonHelper.GetString(jRep, "name", repName);
               }

               StringBuilder var10000 = (new StringBuilder()).append("");
               ++i;
               String repId = var10000.append(i).toString();
               if (jRep.has("Id")) {
                  repId = JsonHelper.GetNumber(jRep, "Id", Integer.valueOf(i)).toString();
               }

               if (jRep.has("id")) {
                  repId = JsonHelper.GetString(jRep, "id", repId);
               }

               HQMRep repObj = new HQMRep(repName);
               JsonArray mrkAry = null;
               if (jRep.has("Markers")) {
                  mrkAry = JsonHelper.GetArray(jRep, "Markers");
               }

               if (mrkAry == null) {
                  mrkAry = JsonHelper.GetArray(jRep, "markers");
               }

               for(int m = 0; m < mrkAry.size(); ++m) {
                  JsonElement e2 = mrkAry.get(m);
                  if (e2 instanceof JsonObject) {
                     JsonObject jMark = e2.getAsJsonObject();
                     int mId = m;
                     if (jMark.has("Id")) {
                        mId = JsonHelper.GetNumber(jMark, "Id", Integer.valueOf(m)).intValue();
                     }

                     int mVal = 0;
                     if (jMark.has("Value")) {
                        mVal = JsonHelper.GetNumber(jMark, "Value", Integer.valueOf(mVal)).intValue();
                     }

                     if (jMark.has("value")) {
                        mVal = JsonHelper.GetNumber(jMark, "value", Integer.valueOf(mVal)).intValue();
                     }

                     repObj.addMarker(mId, mVal);
                  }
               }

               this.reputations.put(repId, repObj);
            }
         }

      }
   }

   private IQuest GetNewQuest(String oldID, IQuestDatabase qdb) {
      if (this.idMap.containsKey(oldID)) {
         return (IQuest)this.idMap.get(oldID);
      } else {
         IQuest quest = qdb.createNew(qdb.nextID());
         this.idMap.put(oldID, quest);
         return quest;
      }
   }

   private void ImportQuestLine(IQuestDatabase questDB, IQuestLineDatabase lineDB, JsonObject json) {
      IQuestLine questLine = lineDB.createNew(lineDB.nextID());
      questLine.setProperty(NativeProps.NAME, JsonHelper.GetString(json, "name", "HQM Quest Line"));
      questLine.setProperty(NativeProps.DESC, JsonHelper.GetString(json, "description", "No description"));
      this.LoadReputations(JsonHelper.GetArray(json, "reputations"));
      JsonArray qlJson = JsonHelper.GetArray(json, "quests");
      List<String> loadedQuests = new ArrayList();

      for(int i = 0; i < qlJson.size(); ++i) {
         JsonElement element = qlJson.get(i);
         if (element != null && element.isJsonObject()) {
            JsonObject jQuest = element.getAsJsonObject();
            String name = JsonHelper.GetString(jQuest, "name", "HQM Quest");
            String idName = jQuest.has("uuid") ? JsonHelper.GetString(jQuest, "uuid", name) : name;
            if (loadedQuests.contains(idName)) {
               int n;
               for(n = 1; loadedQuests.contains(idName + " (" + n + ")"); ++n) {
                  ;
               }

               BQ_Standard.logger.log(Level.WARN, "Found duplicate quest " + name + ". Any quests with this pre-requisite will need repair!");
               idName = name + " (" + n + ")";
            }

            loadedQuests.add(idName);
            IQuest quest = this.GetNewQuest(idName, questDB);
            quest.setProperty(NativeProps.NAME, name);
            quest.setProperty(NativeProps.DESC, JsonHelper.GetString(jQuest, "description", "No Description"));
            BigItemStack tmp = HQMUtilities.HQMStackT1(JsonHelper.GetObject(jQuest, "icon"));
            if (tmp != null) {
               quest.setProperty(NativeProps.ICON, tmp);
            } else {
               quest.setProperty(NativeProps.ICON, new BigItemStack(Items.NETHER_STAR));
            }

            if (json.has("repeat")) {
               JsonObject jRpt = JsonHelper.GetObject(jQuest, "repeat");
               int rTime = 0;
               rTime = rTime + JsonHelper.GetNumber(jRpt, "days", Integer.valueOf(0)).intValue() * 24000;
               rTime = rTime + JsonHelper.GetNumber(jRpt, "hours", Integer.valueOf(0)).intValue() * 1000;
               quest.setProperty(NativeProps.REPEAT_TIME, Integer.valueOf(rTime));
            }

            for(JsonElement er : JsonHelper.GetArray(jQuest, "prerequisites")) {
               if (er != null && er.isJsonPrimitive() && er.getAsJsonPrimitive().isString()) {
                  String id = er.getAsJsonPrimitive().getAsString();
                  if (id.startsWith("{") && id.contains("[")) {
                     String[] nParts = id.split("\\[");
                     if (nParts.length > 1) {
                        id = nParts[1].replaceFirst("]", "");
                     }
                  }

                  IQuest preReq = this.GetNewQuest(id, questDB);
                  this.addReq(quest, questDB.getID(preReq));
               }
            }

            for(JsonElement er : JsonHelper.GetArray(jQuest, "optionlinks")) {
               if (er != null && er.isJsonPrimitive() && er.getAsJsonPrimitive().isString()) {
                  String id = er.getAsJsonPrimitive().getAsString();
                  if (id.startsWith("{") && id.contains("[")) {
                     String[] nParts = id.split("\\[");
                     if (nParts.length > 1) {
                        id = nParts[1].replaceFirst("]", "");
                     }
                  }

                  IQuest preReq = this.GetNewQuest(id, questDB);
                  this.addReq(quest, questDB.getID(preReq));
               }
            }

            for(JsonElement jt : JsonHelper.GetArray(jQuest, "tasks")) {
               if (jt != null && jt.isJsonObject()) {
                  JsonObject jTask = jt.getAsJsonObject();
                  String tType = JsonHelper.GetString(jTask, "type", "");
                  if (tType != null && tType.length() > 0) {
                     if (!taskConverters.containsKey(tType)) {
                        BQ_Standard.logger.warn("Unsupported HQM task \"" + tType + "\"! Skipping...");
                     } else {
                        ITask[] tsks = (ITask[])((Function)taskConverters.get(tType)).apply(jTask);
                        if (tsks != null && tsks.length > 0) {
                           IDatabaseNBT<ITask, NBTTagList, NBTTagList> taskReg = quest.getTasks();

                           for(ITask t : tsks) {
                              taskReg.add(taskReg.nextID(), t);
                           }
                        }
                     }
                  }
               }
            }

            for(Entry<String, Function<JsonElement, IReward[]>> entry : rewardConverters.entrySet()) {
               if (jQuest.has((String)entry.getKey())) {
                  IReward[] rews = (IReward[])((Function)entry.getValue()).apply(jQuest.get((String)entry.getKey()));
                  if (rews != null && rews.length > 0) {
                     IDatabaseNBT<IReward, NBTTagList, NBTTagList> rewardReg = quest.getRewards();

                     for(IReward r : rews) {
                        rewardReg.add(rewardReg.nextID(), r);
                     }
                  }
               }
            }

            if (questLine.getValue(questDB.getID(quest)) != null) {
               BQ_Standard.logger.log(Level.WARN, "Tried to add duplicate quest " + quest + " to quest line " + questLine.getUnlocalisedName());
            } else {
               int qleX = JsonHelper.GetNumber(jQuest, "x", Integer.valueOf(0)).intValue();
               int qleY = JsonHelper.GetNumber(jQuest, "y", Integer.valueOf(0)).intValue();
               boolean bigIcon = JsonHelper.GetBoolean(jQuest, "bigicon", false);
               IQuestLineEntry qle = questLine.createNew(questDB.getID(quest));
               int size = bigIcon ? 32 : 24;
               qle.setSize(size, size);
               qle.setPosition(qleX, qleY);
            }
         }
      }

   }

   private boolean containsReq(IQuest quest, int id) {
      for(int reqID : quest.getRequirements()) {
         if (id == reqID) {
            return true;
         }
      }

      return false;
   }

   private void addReq(IQuest quest, int id) {
      if (!this.containsReq(quest, id)) {
         int[] orig = quest.getRequirements();
         int[] added = Arrays.copyOf(orig, orig.length + 1);
         added[orig.length] = id;
         quest.setRequirements(added);
      }
   }

   static {
      HashMap var10000 = taskConverters;
      HQMTaskDetect var10002 = new HQMTaskDetect(false);
      var10000.put("DETECT", var10002::convertTask);
      var10000 = taskConverters;
      var10002 = new HQMTaskDetect(true);
      var10000.put("CONSUME", var10002::convertTask);
      var10000 = taskConverters;
      var10002 = new HQMTaskDetect(true);
      var10000.put("CONSUME_QDS", var10002::convertTask);
      var10000 = taskConverters;
      HQMTaskKill var16 = new HQMTaskKill();
      var10000.put("KILL", var16::convertTask);
      var10000 = taskConverters;
      HQMTaskLocation var17 = new HQMTaskLocation();
      var10000.put("LOCATION", var17::convertTask);
      var10000 = taskConverters;
      HQMTaskCraft var18 = new HQMTaskCraft();
      var10000.put("CRAFT", var18::convertTask);
      var10000 = taskConverters;
      HQMTaskTame var19 = new HQMTaskTame();
      var10000.put("TAME", var19::convertTask);
      var10000 = taskConverters;
      HQMTaskAdvancement var20 = new HQMTaskAdvancement();
      var10000.put("ADVANCEMENT", var20::convertTask);
      var10000 = taskConverters;
      HQMTaskBlockBreak var21 = new HQMTaskBlockBreak();
      var10000.put("BLOCK_BREAK", var21::convertTask);
      var10000 = taskConverters;
      HQMTaskBlockPlace var22 = new HQMTaskBlockPlace();
      var10000.put("BLOCK_PLACE", var22::convertTask);
      var10000 = taskConverters;
      HQMTaskReputaion var23 = new HQMTaskReputaion();
      var10000.put("REPUTATION", var23::convertTask);
      var10000 = rewardConverters;
      HQMRewardStandard var24 = new HQMRewardStandard();
      var10000.put("reward", var24::convertReward);
      var10000 = rewardConverters;
      HQMRewardChoice var25 = new HQMRewardChoice();
      var10000.put("rewardchoice", var25::convertReward);
      var10000 = rewardConverters;
      HQMRewardReputation var26 = new HQMRewardReputation();
      var10000.put("reputationrewards", var26::convertReward);
      var10000 = rewardConverters;
      HQMRewardCommand var27 = new HQMRewardCommand();
      var10000.put("commandrewards", var27::convertReward);
   }
}
