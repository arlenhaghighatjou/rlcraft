package bq_standard.importers;

import betterquesting.api.client.importers.IImporter;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.FileExtensionFilter;
import betterquesting.api.utils.JsonHelper;
import betterquesting.api2.storage.DBEntry;
import bq_standard.core.BQ_Standard;
import bq_standard.rewards.RewardCommand;
import bq_standard.rewards.RewardItem;
import bq_standard.rewards.RewardRecipe;
import bq_standard.rewards.RewardXP;
import bq_standard.tasks.TaskTrigger;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import java.io.File;
import java.io.FileFilter;
import java.nio.file.FileVisitOption;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Stack;
import java.util.TreeMap;
import java.util.Map.Entry;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.advancements.FrameType;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.JsonUtils;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;
import org.apache.commons.io.FilenameUtils;

public class AdvImporter implements IImporter {
   public static final AdvImporter INSTANCE = new AdvImporter();
   private static final FileFilter FILTER = new FileExtensionFilter(".json");
   private final TreeMap<ResourceLocation, DBEntry<IQuest>> ID_MAP = new TreeMap((o1, o2) -> {
      return o2.toString().compareToIgnoreCase(o1.toString());
   });
   private final HashMap<ResourceLocation, List<IQuest>> PENDING_CHILDREN = new HashMap();
   private final List<List<AdvImporter.AdvTreeNode>> NODES_BY_DEPTH = new ArrayList();

   public AdvImporter() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.importer.adv_json.name";
   }

   public String getUnlocalisedDescription() {
      return "bq_standard.importer.adv_json.desc";
   }

   public FileFilter getFileFilter() {
      return FILTER;
   }

   public void loadFiles(IQuestDatabase questDB, IQuestLineDatabase lineDB, File[] files) {
      this.ID_MAP.clear();
      this.PENDING_CHILDREN.clear();
      if (files.length == 1 && !files[0].isDirectory() && files[0].getName().equalsIgnoreCase("bq_adv_manifest.json")) {
         System.out.println("Importing manifest file...");
         JsonObject manifest = JsonHelper.ReadFromFile(files[0]);

         for(Entry<String, JsonElement> entry : manifest.entrySet()) {
            if (entry.getValue() instanceof JsonPrimitive && ((JsonPrimitive)entry.getValue()).isString()) {
               File folder = new File(files[0].getParentFile(), ((JsonElement)entry.getValue()).getAsString());
               if (folder.exists() && folder.isDirectory()) {
                  try {
                     Iterator<Path> iterator = Files.walk(folder.toPath(), FileVisitOption.FOLLOW_LINKS).iterator();

                     while(iterator.hasNext()) {
                        Path path = (Path)iterator.next();
                        if ("json".equals(FilenameUtils.getExtension(path.toString()))) {
                           String relPath = folder.toPath().relativize(path).toString();
                           ResourceLocation advID = new ResourceLocation((String)entry.getKey(), FilenameUtils.removeExtension(relPath).replaceAll("\\\\", "/"));
                           this.loadAdvancemenet(advID, JsonHelper.ReadFromFile(path.toFile()), questDB);
                        }
                     }
                  } catch (Exception var12) {
                     ;
                  }
               }
            }
         }
      } else {
         for(File selected : files) {
            if (selected != null && selected.exists()) {
               JsonObject json = JsonHelper.ReadFromFile(selected);
               if (json.size() > 0) {
                  this.loadAdvancemenet(this.guessID(selected), json, questDB);
               }
            }
         }
      }

      for(DBEntry<IQuest> entry : this.ID_MAP.values()) {
         if (((IQuest)entry.getValue()).getRequirements().length <= 0 && ((IQuest)entry.getValue()).getProperty(NativeProps.VISIBILITY) != EnumQuestVisibility.HIDDEN) {
            this.generateLayout(entry, lineDB);
         }
      }

   }

   private ResourceLocation guessID(File file) {
      File lastDir = file;

      for(File parent = file.getParentFile(); parent != null; parent = parent.getParentFile()) {
         if (parent.getName().equalsIgnoreCase("advancements") || parent.getName().equalsIgnoreCase("json") && parent.getParentFile() != null && parent.getParentFile().getName().equalsIgnoreCase("triumph")) {
            String relPath = lastDir.toPath().relativize(file.toPath()).toString();
            return new ResourceLocation(lastDir.getName(), FilenameUtils.removeExtension(relPath).replaceAll("\\\\", "/"));
         }

         lastDir = parent;
      }

      return new ResourceLocation("minecraft", FilenameUtils.removeExtension(file.getName()));
   }

   private void registerQuest(ResourceLocation id, DBEntry<IQuest> entry) {
      this.ID_MAP.put(id, entry);
      if (this.PENDING_CHILDREN.containsKey(id)) {
         for(IQuest q : (List)this.PENDING_CHILDREN.get(id)) {
            this.addReq(q, entry.getID());
         }
      }

   }

   private void loadAdvancemenet(ResourceLocation idName, JsonObject json, IQuestDatabase questDB) {
      int QID = questDB.nextID();
      IQuest quest = questDB.createNew(QID);
      this.registerQuest(idName, new DBEntry(QID, quest));
      if (json.has("display")) {
         this.readDisplayInfo(JsonHelper.GetObject(json, "display"), quest);
      } else {
         quest.setProperty(NativeProps.NAME, idName.toString());
         quest.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.HIDDEN);
      }

      quest.setProperty(NativeProps.AUTO_CLAIM, Boolean.valueOf(true));
      quest.setProperty(NativeProps.LOCKED_PROGRESS, Boolean.valueOf(true));
      if (json.has("parent")) {
         ResourceLocation parentID = new ResourceLocation(JsonHelper.GetString(json, "parent", ""));
         if (this.ID_MAP.containsKey(parentID)) {
            this.addReq(quest, ((DBEntry)this.ID_MAP.get(parentID)).getID());
         } else {
            List<IQuest> pending = (List)this.PENDING_CHILDREN.computeIfAbsent(parentID, (k) -> {
               return new ArrayList();
            });
            pending.add(quest);
         }
      }

      if (json.get("criteria") instanceof JsonObject) {
         JsonObject critObj = json.getAsJsonObject("criteria");

         for(Entry<String, JsonElement> entry : critObj.entrySet()) {
            JsonObject taskObj = JsonHelper.GetObject(critObj, (String)entry.getKey());
            TaskTrigger task = new TaskTrigger();
            task.setTriggerID(JsonHelper.GetString(taskObj, "trigger", "minecraft:impossible"));
            task.setCriteriaJson(JsonHelper.GetObject(taskObj, "conditions").toString());
            task.desc = (String)entry.getKey();
            quest.getTasks().add(quest.getTasks().nextID(), task);
         }
      }

      if (json.has("rewards")) {
         JsonObject rewObj = JsonHelper.GetObject(json, "rewards");
         if (rewObj.has("loot")) {
            RewardItem reward = new RewardItem();
            reward.items.clear();
            JsonArray ary = JsonHelper.GetArray(rewObj, "loot");

            for(int i = 0; i < ary.size(); ++i) {
               BigItemStack item = new BigItemStack(BQ_Standard.lootChest, 1, 103);
               NBTTagCompound tag = new NBTTagCompound();
               tag.setString("loottable", ary.get(i).getAsString());
               item.SetTagCompound(tag);
               reward.items.add(item);
            }

            quest.getRewards().add(quest.getRewards().nextID(), reward);
         }

         if (rewObj.has("recipes")) {
            RewardRecipe reward = new RewardRecipe();
            StringBuilder sb = new StringBuilder();
            JsonArray ary = JsonHelper.GetArray(rewObj, "recipes");

            for(int i = 0; i < ary.size(); ++i) {
               if (i != 0) {
                  sb.append("\n");
               }

               sb.append(ary.get(i).getAsString());
            }

            reward.recipeNames = sb.toString();
            quest.getRewards().add(quest.getRewards().nextID(), reward);
         }

         if (rewObj.has("experience")) {
            RewardXP reward = new RewardXP();
            reward.levels = false;
            reward.amount = JsonHelper.GetNumber(rewObj, "experience", Integer.valueOf(0)).intValue();
            quest.getRewards().add(quest.getRewards().nextID(), reward);
         }

         if (rewObj.has("function")) {
            RewardCommand reward = new RewardCommand();
            reward.viaPlayer = true;
            reward.command = "function " + JsonHelper.GetString(rewObj, "function", "minecraft:function");
            quest.getRewards().add(quest.getRewards().nextID(), reward);
         }
      }

   }

   private void readDisplayInfo(JsonObject json, IQuest quest) {
      if (json.has("title")) {
         ITextComponent title = Serializer.fromJsonLenient(json.get("title").toString());
         if (title != null) {
            quest.setProperty(NativeProps.NAME, title.getFormattedText());
         }
      }

      if (json.has("description")) {
         ITextComponent title = Serializer.fromJsonLenient(json.get("description").toString());
         if (title != null) {
            quest.setProperty(NativeProps.DESC, title.getFormattedText());
         }
      }

      quest.setProperty(NativeProps.ICON, new BigItemStack(this.readIcon(JsonHelper.GetObject(json, "icon"))));
      quest.setProperty(NativeProps.SILENT, Boolean.valueOf(!JsonHelper.GetBoolean(json, "show_toast", true)));
      if (JsonHelper.GetBoolean(json, "hidden", false)) {
         quest.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.COMPLETED);
      }

      FrameType frametype = json.has("frame") ? FrameType.byName(JsonUtils.getString(json, "frame")) : FrameType.TASK;
      quest.setProperty(NativeProps.MAIN, Boolean.valueOf(frametype == FrameType.GOAL));
   }

   private ItemStack readIcon(JsonObject json) {
      if (!json.has("item")) {
         return ItemStack.EMPTY;
      } else {
         try {
            Item item = JsonUtils.getItem(json, "item");
            int i = JsonUtils.getInt(json, "data", 0);
            ItemStack ret = new ItemStack(item, 1, i);
            ret.setTagCompound(net.minecraftforge.common.util.JsonUtils.readNBT(json, "nbt"));
            return ret;
         } catch (Exception var5) {
            return ItemStack.EMPTY;
         }
      }
   }

   private void addReq(IQuest quest, int id) {
      if (!this.containsReq(quest, id)) {
         int[] orig = quest.getRequirements();
         int[] added = Arrays.copyOf(orig, orig.length + 1);
         added[orig.length] = id;
         quest.setRequirements(added);
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

   private void generateLayout(DBEntry<IQuest> root, IQuestLineDatabase lineDB) {
      this.NODES_BY_DEPTH.clear();
      Stack<AdvImporter.AdvTreeNode> stack = new Stack();
      stack.push(new AdvImporter.AdvTreeNode(root.getID()));

      while(stack.size() > 0) {
         AdvImporter.AdvTreeNode node = (AdvImporter.AdvTreeNode)stack.pop();
         this.addNodeToDepthMap(node);
         this.findChildren(node);

         for(int i = node.getChildren().size() - 1; i >= 0; --i) {
            stack.push(node.getChildren().get(i));
         }
      }

      for(int d = this.NODES_BY_DEPTH.size() - 1; d >= 1; --d) {
         boolean changed = false;

         for(AdvImporter.AdvTreeNode node : (List)this.NODES_BY_DEPTH.get(d)) {
            Stack<AdvImporter.AdvTreeNode> childStack = new Stack();
            childStack.push(node);

            while(childStack.size() > 0) {
               AdvImporter.AdvTreeNode child = (AdvImporter.AdvTreeNode)childStack.pop();
               int prev = child.offY;
               int cPosYBase = Math.max(0, child.getParent().getChildren().size() - 1) * 16;
               int cPosY = child.getParent().getChildren().size() <= 1 ? 0 : child.getParent().getChildren().indexOf(child) * 32 - cPosYBase;
               int pOff = child.getParent().getPosY() - (child.getPosY() - child.offY);
               child.offY = pOff + cPosY;
               if (child.aboveNode != null) {
                  child.offY = Math.max(0, child.offY);
               }

               changed = changed || prev != child.offY;
               if (changed && child.getChildren().size() > 0) {
                  for(int i = child.getChildren().size() - 1; i >= 0; --i) {
                     childStack.push(child.getChildren().get(i));
                  }
               }
            }
         }
      }

      for(int d = 0; d < this.NODES_BY_DEPTH.size(); ++d) {
         boolean changed = false;

         for(AdvImporter.AdvTreeNode node : (List)this.NODES_BY_DEPTH.get(d)) {
            if (node.getChildren().size() > 0) {
               int prev = node.offY;
               int cPosY = ((AdvImporter.AdvTreeNode)node.getChildren().get(node.getChildren().size() - 1)).getPosY() + 24 - ((AdvImporter.AdvTreeNode)node.getChildren().get(0)).getPosY();
               cPosY = cPosY / 2 - 12;
               int pOff = ((AdvImporter.AdvTreeNode)node.getChildren().get(0)).getPosY() - (node.getPosY() - node.offY);
               int nPos = pOff + cPosY;
               node.offY = nPos;
               if (node.aboveNode != null) {
                  node.offY = Math.max(0, node.offY);
                  if (nPos < 0) {
                     int dShift = node.depth + 1;
                     int iShift = ((List)this.NODES_BY_DEPTH.get(dShift)).indexOf(node.getChildren().get(0));

                     for(int ds = dShift; ds < this.NODES_BY_DEPTH.size(); ++ds) {
                        for(AdvImporter.AdvTreeNode nShift : (List)this.NODES_BY_DEPTH.get(ds)) {
                           AdvImporter.AdvTreeNode tmp;
                           for(tmp = nShift; tmp.depth > dShift; tmp = tmp.getParent()) {
                              ;
                           }

                           if (((List)this.NODES_BY_DEPTH.get(dShift)).indexOf(tmp) >= iShift) {
                              nShift.offY = nShift.offY - nPos;
                              break;
                           }
                        }
                     }
                  }
               }

               changed = changed || prev != node.offY;
            }
         }

         if (changed && d > 0) {
            d -= 2;
         }
      }

      IQuestLine line = lineDB.createNew(lineDB.nextID());
      line.setProperty(NativeProps.NAME, ((IQuest)root.getValue()).getProperty(NativeProps.NAME));
      line.setProperty(NativeProps.DESC, ((IQuest)root.getValue()).getProperty(NativeProps.DESC));
      line.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.UNLOCKED);
      line.setProperty(NativeProps.ICON, ((IQuest)root.getValue()).getProperty(NativeProps.ICON));
      Iterator var24 = this.NODES_BY_DEPTH.iterator();

      while(var24.hasNext()) {
         for(AdvImporter.AdvTreeNode node : (List)var24.next()) {
            IQuestLineEntry qle = line.createNew(node.getQuestID());
            qle.setPosition(node.getPosX(), node.getPosY());
            qle.setSize(24, 24);
         }
      }

   }

   private void addNodeToDepthMap(AdvImporter.AdvTreeNode node) {
      while(node.depth >= this.NODES_BY_DEPTH.size()) {
         this.NODES_BY_DEPTH.add(new ArrayList());
      }

      List<AdvImporter.AdvTreeNode> list = (List)this.NODES_BY_DEPTH.get(node.depth);
      if (list.size() > 0) {
         node.setAboveNode((AdvImporter.AdvTreeNode)list.get(list.size() - 1));
      }

      list.add(node);
   }

   private void findChildren(AdvImporter.AdvTreeNode parent) {
      for(DBEntry<IQuest> entry : this.ID_MAP.values()) {
         if (((IQuest)entry.getValue()).getProperty(NativeProps.VISIBILITY) != EnumQuestVisibility.HIDDEN) {
            for(int req : ((IQuest)entry.getValue()).getRequirements()) {
               if (req == parent.getQuestID()) {
                  AdvImporter.AdvTreeNode child = new AdvImporter.AdvTreeNode(entry.getID());
                  parent.addChild(child);
               }
            }
         }
      }

   }

   private static class AdvTreeNode {
      private AdvImporter.AdvTreeNode aboveNode;
      private int depth;
      private int offY;
      private AdvImporter.AdvTreeNode parent;
      private final List<AdvImporter.AdvTreeNode> children;
      private final int questID;

      private AdvTreeNode(int questID) {
         this.depth = 0;
         this.offY = 0;
         this.children = new ArrayList();
         this.questID = questID;
      }

      private int getDepth() {
         return this.depth;
      }

      private int getPosX() {
         return this.getDepth() * 32;
      }

      private int getPosY() {
         return this.offY + (this.aboveNode == null ? 0 : this.aboveNode.getPosY() + 32);
      }

      private AdvImporter.AdvTreeNode getParent() {
         return this.parent;
      }

      private List<AdvImporter.AdvTreeNode> getChildren() {
         return Collections.unmodifiableList(this.children);
      }

      private int getQuestID() {
         return this.questID;
      }

      private void addChild(@Nonnull AdvImporter.AdvTreeNode node) {
         if (!this.children.contains(node)) {
            this.children.add(node);
            node.parent = this;
            node.depth = this.depth + 1;
         }
      }

      private void setAboveNode(@Nullable AdvImporter.AdvTreeNode node) {
         this.aboveNode = node;
      }
   }
}
