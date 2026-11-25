package bq_standard.importers.ftbq;

import betterquesting.api.client.importers.IImporter;
import betterquesting.api.enums.EnumQuestVisibility;
import betterquesting.api.properties.NativeProps;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.IQuestDatabase;
import betterquesting.api.questing.IQuestLine;
import betterquesting.api.questing.IQuestLineDatabase;
import betterquesting.api.questing.IQuestLineEntry;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.storage.IDatabaseNBT;
import bq_standard.core.BQ_Standard;
import bq_standard.importers.ftbq.converters.rewards.FtbqRewardCommand;
import bq_standard.importers.ftbq.converters.rewards.FtbqRewardItem;
import bq_standard.importers.ftbq.converters.rewards.FtbqRewardXP;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskAdvancement;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskDimension;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskEnergy;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskFluid;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskItem;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskKill;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskLocation;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskStat;
import bq_standard.importers.ftbq.converters.tasks.FtbqTaskXP;
import bq_standard.tasks.TaskCheckbox;
import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.CompressedStreamTools;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.util.math.MathHelper;

public class FTBQQuestImporter implements IImporter {
   public static final FTBQQuestImporter INSTANCE = new FTBQQuestImporter();
   private static final FileFilter FILTER = new FTBQFileFIlter();
   private static final HashMap<String, Function<NBTTagCompound, ITask[]>> taskConverters = new HashMap();
   private static final HashMap<String, Function<NBTTagCompound, IReward[]>> rewardConverters = new HashMap();
   private final HashMap<String, FTBEntry> ID_MAP = new HashMap();
   private static IQuest iconQuest;
   private static IQuestLine iconChapter;

   public FTBQQuestImporter() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.importer.ftbq_quest.name";
   }

   public String getUnlocalisedDescription() {
      return "bq_standard.importer.ftbq_quest.desc";
   }

   public FileFilter getFileFilter() {
      return FILTER;
   }

   public void loadFiles(IQuestDatabase questDB, IQuestLineDatabase lineDB, File[] files) {
      for(File f : files) {
         if (f != null && f.getParent() != null) {
            try {
               FileInputStream fis = new FileInputStream(f);
               Throwable var9 = null;

               try {
                  this.startImport(questDB, lineDB, CompressedStreamTools.readCompressed(fis), f.getParentFile());
               } catch (Throwable var18) {
                  var9 = var18;
                  throw var18;
               } finally {
                  if (fis != null) {
                     if (var9 != null) {
                        try {
                           fis.close();
                        } catch (Throwable var19) {
                           var9.addSuppressed(var19);
                        }
                     } else {
                        fis.close();
                     }
                  }

               }
            } catch (Exception var21) {
               BQ_Standard.logger.error("Failed to import FTB Quests NBT file:\n" + f.getAbsolutePath() + "\nReason:", var21);
            }
         }
      }

   }

   private void startImport(IQuestDatabase questDB, IQuestLineDatabase lineDB, NBTTagCompound tagIndex, File folder) {
      int[] indexIDs = tagIndex.getIntArray("index");
      this.ID_MAP.clear();
      requestQuestIcon((IQuest)null);
      requestChapterIcon((IQuestLine)null);
      HashMap<IQuest, String[]> parentMap = new HashMap();
      BQ_Standard.logger.info("Found " + indexIDs.length + " quest chapter(s) to import");

      for(int id : indexIDs) {
         String hexName = String.format("%1$08x", id);
         File qlFolder = new File(folder, hexName);
         if (qlFolder.exists() && qlFolder.isDirectory()) {
            File[] contents = qlFolder.listFiles();
            if (contents != null) {
               int lineID = lineDB.nextID();
               IQuestLine questLine = lineDB.createNew(lineID);
               this.ID_MAP.put(hexName, new FTBEntry(lineID, questLine, FTBEntry.FTBEntryType.LINE));
               requestChapterIcon(questLine);

               for(File questFile : contents) {
                  if (questFile.getName().toLowerCase().endsWith(".nbt")) {
                     NBTTagCompound qTag;
                     try {
                        FileInputStream chFis = new FileInputStream(questFile);
                        Throwable questID = null;

                        try {
                           qTag = CompressedStreamTools.readCompressed(chFis);
                        } catch (Throwable var47) {
                           questID = var47;
                           throw var47;
                        } finally {
                           if (chFis != null) {
                              if (questID != null) {
                                 try {
                                    chFis.close();
                                 } catch (Throwable var46) {
                                    questID.addSuppressed(var46);
                                 }
                              } else {
                                 chFis.close();
                              }
                           }

                        }
                     } catch (Exception var49) {
                        BQ_Standard.logger.error("Failed to import chapter file entry: " + questFile, var49);
                        continue;
                     }

                     if (questFile.getName().equalsIgnoreCase("chapter.nbt")) {
                        questLine.setProperty(NativeProps.NAME, qTag.getString("title"));
                        NBTTagList desc = qTag.getTagList("description", 8);
                        StringBuilder sb = new StringBuilder();

                        for(int i = 0; i < desc.tagCount(); ++i) {
                           sb.append(desc.getStringTagAt(i));
                           if (i + 1 < desc.tagCount()) {
                              sb.append("\n");
                           }
                        }

                        questLine.setProperty(NativeProps.DESC, sb.toString());
                        if (qTag.hasKey("icon")) {
                           BigItemStack icoStack = FTBQUtils.convertItem(qTag.getTag("icon"));
                           if (!icoStack.getBaseStack().isEmpty()) {
                              questLine.setProperty(NativeProps.ICON, icoStack);
                           }

                           requestChapterIcon((IQuestLine)null);
                        }
                     } else {
                        requestQuestIcon((IQuest)null);
                        String hexID = questFile.getName().substring(0, questFile.getName().length() - 4);
                        int questID = questDB.nextID();
                        IQuest quest = questDB.createNew(questID);
                        IQuestLineEntry qle = questLine.createNew(questID);
                        this.ID_MAP.put(hexID, new FTBEntry(questID, quest, FTBEntry.FTBEntryType.QUEST));
                        quest.setProperty(NativeProps.NAME, qTag.hasKey("title", 8) ? qTag.getString("title") : hexID);
                        NBTTagList desc = qTag.getTagList("text", 8);
                        StringBuilder sb = new StringBuilder();

                        for(int i = 0; i < desc.tagCount(); ++i) {
                           sb.append(desc.getStringTagAt(i));
                           if (i + 1 < desc.tagCount()) {
                              sb.append("\n");
                           }
                        }

                        quest.setProperty(NativeProps.DESC, sb.toString());
                        quest.setProperty(NativeProps.VISIBILITY, EnumQuestVisibility.ALWAYS);
                        if (qTag.hasKey("icon")) {
                           BigItemStack icoStack = FTBQUtils.convertItem(qTag.getTag("icon"));
                           if (!icoStack.getBaseStack().isEmpty()) {
                              quest.setProperty(NativeProps.ICON, icoStack);
                           }

                           requestQuestIcon((IQuest)null);
                        } else {
                           requestQuestIcon(quest);
                        }

                        double size = qTag.hasKey("size") ? qTag.getDouble("size") : 1.0D;
                        size = size * 24.0D;
                        qle.setSize(MathHelper.ceil(size), MathHelper.ceil(size));
                        qle.setPosition(MathHelper.ceil(qTag.getDouble("x") * 24.0D - size / 2.0D), MathHelper.ceil(qTag.getDouble("y") * 24.0D - size / 2.0D));
                        int[] depend = null;
                        if (qTag.hasKey("dependencies", 11)) {
                           depend = qTag.getIntArray("dependencies");
                        }

                        if (qTag.hasKey("dependency", 3)) {
                           depend = new int[]{qTag.getInteger("dependency")};
                        }

                        if (depend != null && depend.length > 0) {
                           String[] depKeys = new String[depend.length];

                           for(int d = 0; d < depend.length; ++d) {
                              depKeys[d] = String.format("%1$08x", depend[d]);
                           }

                           parentMap.put(quest, depKeys);
                        }

                        NBTTagList taskList = qTag.getTagList("tasks", 10);

                        for(int i = 0; i < taskList.tagCount(); ++i) {
                           NBTTagCompound taskTag = taskList.getCompoundTagAt(i);
                           String tType = taskTag.getString("type");
                           if (!taskConverters.containsKey(tType)) {
                              BQ_Standard.logger.warn("Unsupported FTBQ task \"" + tType + "\"! Skipping...");
                           } else {
                              ITask[] tsks = (ITask[])((Function)taskConverters.get(tType)).apply(taskTag);
                              if (tsks != null && tsks.length > 0) {
                                 IDatabaseNBT<ITask, NBTTagList, NBTTagList> taskReg = quest.getTasks();

                                 for(ITask t : tsks) {
                                    taskReg.add(taskReg.nextID(), t);
                                 }
                              }
                           }
                        }

                        NBTTagList rewardList = qTag.getTagList("rewards", 10);

                        for(int i = 0; i < rewardList.tagCount(); ++i) {
                           NBTTagCompound rewTag = rewardList.getCompoundTagAt(i);
                           String rType = rewTag.getString("type");
                           if (!rewardConverters.containsKey(rType)) {
                              BQ_Standard.logger.warn("Unsupported FTBQ reward \"" + rType + "\"! Skipping...");
                           } else {
                              IReward[] tsks = (IReward[])((Function)rewardConverters.get(rType)).apply(rewTag);
                              if (tsks != null && tsks.length > 0) {
                                 IDatabaseNBT<IReward, NBTTagList, NBTTagList> rewardReg = quest.getRewards();

                                 for(IReward t : tsks) {
                                    rewardReg.add(rewardReg.nextID(), t);
                                 }
                              }
                           }
                        }

                        if (iconQuest != null) {
                           iconQuest.setProperty(NativeProps.ICON, new BigItemStack(ItemStack.EMPTY));
                           iconQuest = null;
                        }
                     }
                  }
               }

               if (iconChapter != null) {
                  iconChapter.setProperty(NativeProps.ICON, new BigItemStack(ItemStack.EMPTY));
                  iconChapter = null;
               }
            }
         }
      }

      for(Entry<IQuest, String[]> entry : parentMap.entrySet()) {
         List<Integer> qIDs = new ArrayList();

         for(String key : (String[])entry.getValue()) {
            FTBEntry type = (FTBEntry)this.ID_MAP.get(key);
            if (type == null) {
               BQ_Standard.logger.warn("Unable to find quest dependency '" + key + "'");
            } else if (type.type != FTBEntry.FTBEntryType.VAR) {
               if (type.type == FTBEntry.FTBEntryType.QUEST) {
                  qIDs.add(Integer.valueOf(questDB.getID((IQuest)type.obj)));
               } else if (type.type == FTBEntry.FTBEntryType.LINE) {
                  for(DBEntry<IQuestLineEntry> qle : ((IQuestLine)type.obj).getEntries()) {
                     qIDs.add(Integer.valueOf(qle.getID()));
                  }
               }
            }
         }

         int[] preReq = new int[qIDs.size()];

         for(int i = 0; i < qIDs.size(); ++i) {
            preReq[i] = ((Integer)qIDs.get(i)).intValue();
         }

         ((IQuest)entry.getKey()).setRequirements(preReq);
      }

   }

   private static void requestQuestIcon(IQuest quest) {
      iconQuest = quest;
   }

   private static void requestChapterIcon(IQuestLine chapter) {
      iconChapter = chapter;
   }

   public static void provideQuestIcon(BigItemStack stack) {
      if (stack != null) {
         if (iconQuest != null) {
            iconQuest.setProperty(NativeProps.ICON, stack);
            iconQuest = null;
         }

         if (iconChapter != null) {
            iconChapter.setProperty(NativeProps.ICON, stack);
            iconChapter = null;
         }

      }
   }

   public static void provideChapterIcon(BigItemStack stack) {
      if (iconChapter != null && stack != null) {
         iconChapter.setProperty(NativeProps.ICON, stack);
         iconChapter = null;
      }

   }

   static {
      HashMap var10000 = taskConverters;
      FtbqTaskItem var10002 = new FtbqTaskItem();
      var10000.put("item", var10002::convertTask);
      var10000 = taskConverters;
      FtbqTaskFluid var12 = new FtbqTaskFluid();
      var10000.put("fluid", var12::convertTask);
      var10000 = taskConverters;
      FtbqTaskEnergy var13 = new FtbqTaskEnergy();
      var10000.put("forge_energy", var13::converTask);
      var10000 = taskConverters;
      FtbqTaskXP var14 = new FtbqTaskXP();
      var10000.put("xp", var14::convertTask);
      var10000 = taskConverters;
      FtbqTaskDimension var15 = new FtbqTaskDimension();
      var10000.put("dimension", var15::converTask);
      var10000 = taskConverters;
      FtbqTaskStat var16 = new FtbqTaskStat();
      var10000.put("stat", var16::convertTask);
      var10000 = taskConverters;
      FtbqTaskKill var17 = new FtbqTaskKill();
      var10000.put("kill", var17::convertTask);
      var10000 = taskConverters;
      FtbqTaskLocation var18 = new FtbqTaskLocation();
      var10000.put("location", var18::convertTask);
      taskConverters.put("checkmark", (tag) -> {
         return new ITask[]{new TaskCheckbox()};
      });
      var10000 = taskConverters;
      FtbqTaskAdvancement var19 = new FtbqTaskAdvancement();
      var10000.put("advancement", var19::converTask);
      var10000 = rewardConverters;
      FtbqRewardItem var20 = new FtbqRewardItem();
      var10000.put("item", var20::convertTask);
      var10000 = rewardConverters;
      FtbqRewardXP var21 = new FtbqRewardXP(false);
      var10000.put("xp", var21::convertTask);
      var10000 = rewardConverters;
      var21 = new FtbqRewardXP(true);
      var10000.put("xp_levels", var21::convertTask);
      var10000 = rewardConverters;
      FtbqRewardCommand var23 = new FtbqRewardCommand();
      var10000.put("command", var23::convertReward);
   }
}
