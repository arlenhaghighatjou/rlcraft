package bq_msi.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_msi.MultiblockInventory;
import bq_msi.NbtBlockType;
import bq_msi.client.gui.PanelTaskMultiblock;
import bq_msi.tasks.factory.FactoryTaskMultiblock;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.darkhax.gamestages.GameStageHelper;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TaskMultiblock implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   private final HashMap<UUID, Integer> userProgress = new HashMap();
   public static HashMap<String, MultiblockInventory> multiblockHash = new HashMap();
   public BigItemStack targetItem = new BigItemStack(Items.AIR);
   public final NbtBlockType targetBlock = new NbtBlockType(Blocks.AIR);
   public boolean render = false;
   public String fileNames = "";
   public int length = 0;
   public int width = 0;
   public int height = 0;
   public boolean wildcardOptimization = false;
   public String name = "New Multiblock Name";
   public boolean gameStageCompat = false;
   public String gameStageName = "";
   public boolean useImage = false;
   public String imageLocation = "";

   public TaskMultiblock() {
   }

   public static String clearHashmap() {
      if (multiblockHash.size() == 0) {
         return "No cached files.";
      } else {
         String returnable = "Cleared cached files:";

         for(String key : multiblockHash.keySet()) {
            returnable = returnable + " " + key;
         }

         multiblockHash.clear();
         return returnable;
      }
   }

   public static String getHashmapContents() {
      if (multiblockHash.size() == 0) {
         return "No cached files.";
      } else {
         String returnable = "Cached files:";

         for(String key : multiblockHash.keySet()) {
            returnable = returnable + " " + key;
         }

         return returnable;
      }
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskMultiblock.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_msi.task.multiblock";
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void resetUser(@Nullable UUID uuid) {
      if (uuid == null) {
         this.completeUsers.clear();
         this.userProgress.clear();
      } else {
         this.completeUsers.remove(uuid);
         this.userProgress.remove(uuid);
      }

   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      List<Tuple<UUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
      progress.forEach((value) -> {
         if (((Integer)value.getSecond()).intValue() >= 1) {
            this.setComplete((UUID)value.getFirst());
         }

      });
      pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
   }

   @Nullable
   public MultiblockInventory fileData(String fileName, int length, int width, int height) {
      File file = new File(Loader.instance().getConfigDir() + File.separator + "betterquesting" + File.separator + "resources" + File.separator + fileName);

      try {
         FileReader fileReader = new FileReader(file);
         BufferedReader bufferedReader = new BufferedReader(fileReader);
         String[][][] fileArray = new String[length][width][height];
         int[] keyCoords = new int[3];
         String separator = ".";
         String keyBlock = "keyBlock";
         int heightCount = 0;
         int lengthCount = 0;

         String line;
         while((line = bufferedReader.readLine()) != null) {
            if (line.trim().contentEquals(separator.trim())) {
               ++heightCount;
               lengthCount = 0;
            } else if (!line.trim().contentEquals("")) {
               String[] blocks = line.split(",");

               for(int i = 0; i < blocks.length; ++i) {
                  if (!blocks[i].trim().contentEquals("")) {
                     fileArray[lengthCount][i][heightCount] = blocks[i].trim();
                     if (blocks[i].trim().contentEquals(keyBlock)) {
                        fileArray[lengthCount][i][heightCount] = "wildcard";
                        keyCoords[0] = lengthCount;
                        keyCoords[1] = i;
                        keyCoords[2] = heightCount;
                     }
                  }
               }

               ++lengthCount;
            }
         }

         bufferedReader.close();
         return new MultiblockInventory(fileArray, keyCoords);
      } catch (IOException var17) {
         if (var17 instanceof FileNotFoundException) {
            System.out.println("TaskMultiblock tried to access non-existant file" + var17);
            return null;
         } else {
            System.out.println(var17);
            return null;
         }
      }
   }

   public boolean matchData(World world, IBlockState state, BlockPos pos, int length, int width, int height, boolean wildcardOptimization, MultiblockInventory inventory) {
      String[][][] fileArray = inventory.getFileArray();
      int[] keyCoords = inventory.getKeyCoords();
      int keyLength = keyCoords[0];
      int keyWidth = keyCoords[1];
      int keyHeight = keyCoords[2];
      boolean rot0 = true;
      boolean rot1 = true;
      boolean rot2 = true;
      boolean rot3 = true;
      if (wildcardOptimization) {
         for(int h = 0; h < height; ++h) {
            for(int w = 0; w < width; ++w) {
               for(int l = 0; l < length; ++l) {
                  if (!fileArray[l][w][h].contentEquals("wildcard")) {
                     for(int rot = 0; rot < 4; ++rot) {
                        if (rot == 0 && rot0) {
                           int x = -keyLength + l + pos.getX();
                           int y = -keyHeight + h + pos.getY();
                           int z = keyWidth + -w + pos.getZ();
                           IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                           String targetBlock = targetState.getBlock().getRegistryName().toString();
                           String fileBlock = fileArray[l][w][h];
                           if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                              targetBlock = targetBlock + ":*";
                           } else {
                              targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                           }

                           if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                              rot0 = false;
                           }
                        }

                        if (rot == 1 && rot1) {
                           int x = keyLength + -l + pos.getX();
                           int y = -keyHeight + h + pos.getY();
                           int z = -keyWidth + w + pos.getZ();
                           IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                           String targetBlock = targetState.getBlock().getRegistryName().toString();
                           String fileBlock = fileArray[l][w][h];
                           if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                              targetBlock = targetBlock + ":*";
                           } else {
                              targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                           }

                           if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                              rot1 = false;
                           }
                        }

                        if (rot == 2 && rot2) {
                           int x = -keyWidth + w + pos.getX();
                           int y = -keyHeight + h + pos.getY();
                           int z = -keyLength + l + pos.getZ();
                           IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                           String targetBlock = targetState.getBlock().getRegistryName().toString();
                           String fileBlock = fileArray[l][w][h];
                           if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                              targetBlock = targetBlock + ":*";
                           } else {
                              targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                           }

                           if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                              rot2 = false;
                           }
                        }

                        if (rot == 3 && rot3) {
                           int x = keyWidth + -w + pos.getX();
                           int y = -keyHeight + h + pos.getY();
                           int z = keyLength + -l + pos.getZ();
                           IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                           String targetBlock = targetState.getBlock().getRegistryName().toString();
                           String fileBlock = fileArray[l][w][h];
                           if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                              targetBlock = targetBlock + ":*";
                           } else {
                              targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                           }

                           if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                              rot3 = false;
                           }
                        }

                        if (!rot0 && !rot1 && !rot2 && !rot3) {
                           return false;
                        }
                     }
                  }
               }
            }
         }

         return true;
      } else {
         for(int h = 0; h < height; ++h) {
            for(int w = 0; w < width; ++w) {
               for(int l = 0; l < length; ++l) {
                  for(int rot = 0; rot < 4; ++rot) {
                     if (rot == 0 && rot0) {
                        int x = -keyLength + l + pos.getX();
                        int y = -keyHeight + h + pos.getY();
                        int z = keyWidth + -w + pos.getZ();
                        IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                        String targetBlock = targetState.getBlock().getRegistryName().toString();
                        String fileBlock = fileArray[l][w][h];
                        if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                           targetBlock = targetBlock + ":*";
                        } else {
                           targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                        }

                        if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                           rot0 = false;
                        }
                     }

                     if (rot == 1 && rot1) {
                        int x = keyLength + -l + pos.getX();
                        int y = -keyHeight + h + pos.getY();
                        int z = -keyWidth + w + pos.getZ();
                        IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                        String targetBlock = targetState.getBlock().getRegistryName().toString();
                        String fileBlock = fileArray[l][w][h];
                        if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                           targetBlock = targetBlock + ":*";
                        } else {
                           targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                        }

                        if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                           rot1 = false;
                        }
                     }

                     if (rot == 2 && rot2) {
                        int x = -keyWidth + w + pos.getX();
                        int y = -keyHeight + h + pos.getY();
                        int z = -keyLength + l + pos.getZ();
                        IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                        String targetBlock = targetState.getBlock().getRegistryName().toString();
                        String fileBlock = fileArray[l][w][h];
                        if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                           targetBlock = targetBlock + ":*";
                        } else {
                           targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                        }

                        if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                           rot2 = false;
                        }
                     }

                     if (rot == 3 && rot3) {
                        int x = keyWidth + -w + pos.getX();
                        int y = -keyHeight + h + pos.getY();
                        int z = keyLength + -l + pos.getZ();
                        IBlockState targetState = world.getBlockState(new BlockPos(x, y, z));
                        String targetBlock = targetState.getBlock().getRegistryName().toString();
                        String fileBlock = fileArray[l][w][h];
                        if (fileBlock.substring(fileBlock.length() - 1).equalsIgnoreCase("*")) {
                           targetBlock = targetBlock + ":*";
                        } else {
                           targetBlock = targetBlock + ":" + targetState.getBlock().getMetaFromState(targetState);
                        }

                        if (!fileBlock.contentEquals(targetBlock) && !fileBlock.contentEquals("wildcard")) {
                           rot3 = false;
                        }
                     }

                     if (!rot0 && !rot1 && !rot2 && !rot3) {
                        return false;
                     }
                  }
               }
            }
         }

         return true;
      }
   }

   public void onInteract(ParticipantInfo pInfo, DBEntry<IQuest> quest, EnumHand hand, ItemStack item, IBlockState state, BlockPos pos) {
      if (pInfo.PLAYER.isEntityAlive() && pInfo.PLAYER instanceof EntityPlayerMP) {
         World world = pInfo.PLAYER.world;
         if (this.targetBlock.b != Blocks.AIR && this.targetBlock.b == state.getBlock() && ItemComparison.StackMatch(this.targetItem.getBaseStack(), item, false, false)) {
            if (state.getBlock() == Blocks.AIR) {
               return;
            }

            if (hand != EnumHand.MAIN_HAND) {
               return;
            }

            String[] nameList = this.fileNames.split(",");
            if (this.gameStageCompat && Loader.isModLoaded("gamestages") && !GameStageHelper.hasStage(pInfo.PLAYER, this.gameStageName)) {
               return;
            }

            for(String name : nameList) {
               if (multiblockHash.get(name.trim()) == null) {
                  MultiblockInventory invenToCache = this.fileData(name.trim(), this.length, this.width, this.height);
                  if (invenToCache == null) {
                     System.out.println("Multiblock task attempted to cache broken file " + name);
                     return;
                  }

                  System.out.println("Multiblock " + name + " has been cached!");
                  multiblockHash.put(name, invenToCache);
               }

               if (this.matchData(world, state, pos, this.length, this.width, this.height, this.wildcardOptimization, (MultiblockInventory)multiblockHash.get(name))) {
                  List<Tuple<UUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
                  progress.forEach((value) -> {
                     if (!this.isComplete((UUID)value.getFirst())) {
                        int np = Math.min(1, ((Integer)value.getSecond()).intValue() + 1);
                        this.setUserProgress((UUID)value.getFirst(), Integer.valueOf(np));
                        if (np >= 1) {
                           this.setComplete((UUID)value.getFirst());
                        }

                     }
                  });
                  pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
                  return;
               }
            }
         }

      }
   }

   public synchronized NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("item", this.targetItem.writeToNBT(new NBTTagCompound()));
      nbt.setTag("block", this.targetBlock.writeToNBT(new NBTTagCompound()));
      nbt.setString("fileNames", this.fileNames);
      nbt.setInteger("length", this.length);
      nbt.setInteger("width", this.width);
      nbt.setInteger("height", this.height);
      nbt.setBoolean("wildcardOptimization", this.wildcardOptimization);
      nbt.setString("name", this.name);
      nbt.setBoolean("gameStageCompat", this.gameStageCompat);
      nbt.setString("gameStageName", this.gameStageName);
      nbt.setBoolean("useImage", this.useImage);
      nbt.setString("imageLocation", this.imageLocation);
      return nbt;
   }

   public synchronized void readFromNBT(NBTTagCompound nbt) {
      this.targetItem = new BigItemStack(nbt.getCompoundTag("item"));
      this.targetBlock.readFromNBT(nbt.getCompoundTag("block"));
      this.fileNames = nbt.getString("fileNames");
      this.length = nbt.getInteger("length");
      this.width = nbt.getInteger("width");
      this.height = nbt.getInteger("height");
      this.wildcardOptimization = nbt.getBoolean("wildcardOptimization");
      this.name = nbt.getString("name");
      this.gameStageCompat = nbt.getBoolean("gameStageCompat");
      this.gameStageName = nbt.getString("gameStageName");
      this.useImage = nbt.getBoolean("useImage");
      this.imageLocation = nbt.getString("imageLocation");
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
      NBTTagList jArray = new NBTTagList();
      NBTTagList progArray = new NBTTagList();
      if (users != null) {
         users.forEach((uuid) -> {
            if (this.completeUsers.contains(uuid)) {
               jArray.appendTag(new NBTTagString(uuid.toString()));
            }

            Integer data = (Integer)this.userProgress.get(uuid);
            if (data != null) {
               NBTTagCompound pJson = new NBTTagCompound();
               pJson.setString("uuid", uuid.toString());
               pJson.setInteger("value", data.intValue());
               progArray.appendTag(pJson);
            }

         });
      } else {
         this.completeUsers.forEach((uuid) -> {
            jArray.appendTag(new NBTTagString(uuid.toString()));
         });
         this.userProgress.forEach((uuid, data) -> {
            NBTTagCompound pJson = new NBTTagCompound();
            pJson.setString("uuid", uuid.toString());
            pJson.setInteger("value", data.intValue());
            progArray.appendTag(pJson);
         });
      }

      nbt.setTag("completeUsers", jArray);
      nbt.setTag("userProgress", progArray);
      return nbt;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
         this.userProgress.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var9) {
            ;
         }
      }

      NBTTagList pList = nbt.getTagList("userProgress", 10);

      for(int n = 0; n < pList.tagCount(); ++n) {
         try {
            NBTTagCompound pTag = pList.getCompoundTagAt(n);
            UUID uuid = UUID.fromString(pTag.getString("uuid"));
            this.userProgress.put(uuid, Integer.valueOf(pTag.getInteger("value")));
         } catch (Exception var8) {
            ;
         }
      }

   }

   private void setUserProgress(UUID uuid, Integer progress) {
      this.userProgress.put(uuid, progress);
   }

   public int getUsersProgress(UUID uuid) {
      Integer n = (Integer)this.userProgress.get(uuid);
      return n == null ? 0 : n.intValue();
   }

   private List<Tuple<UUID, Integer>> getBulkProgress(@Nonnull List<UUID> uuids) {
      if (uuids.size() <= 0) {
         return Collections.emptyList();
      } else {
         List<Tuple<UUID, Integer>> list = new ArrayList();
         uuids.forEach((key) -> {
            list.add(new Tuple(key, this.getUsersProgress(key)));
         });
         return list;
      }
   }

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskMultiblock(rect, this);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }
}
