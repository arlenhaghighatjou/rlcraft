package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.NbtBlockType;
import bq_standard.client.gui.tasks.PanelTaskBlockBreak;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskBlockBreak;
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
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagInt;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import org.apache.logging.log4j.Level;

public class TaskBlockBreak implements ITask {
   private final Set<UUID> completeUsers = new TreeSet();
   private final HashMap<UUID, int[]> userProgress = new HashMap();
   public final List<NbtBlockType> blockTypes = new ArrayList();

   public TaskBlockBreak() {
      this.blockTypes.add(new NbtBlockType());
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskBlockBreak.INSTANCE.getRegistryName();
   }

   public boolean isComplete(UUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(UUID uuid) {
      this.completeUsers.add(uuid);
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.block_break";
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      pInfo.ALL_UUIDS.forEach((uuid) -> {
         if (!this.isComplete(uuid)) {
            int[] tmp = this.getUsersProgress(uuid);

            for(int i = 0; i < this.blockTypes.size(); ++i) {
               NbtBlockType block = (NbtBlockType)this.blockTypes.get(i);
               if (block != null && tmp[i] < block.n) {
                  return;
               }
            }

            this.setComplete(uuid);
         }
      });
      pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
   }

   public void onBlockBreak(ParticipantInfo pInfo, DBEntry<IQuest> quest, IBlockState state, BlockPos pos) {
      TileEntity tile = state.getBlock().hasTileEntity(state) ? pInfo.PLAYER.world.getTileEntity(pos) : null;
      NBTTagCompound tags = tile == null ? null : tile.writeToNBT(new NBTTagCompound());
      List<Tuple<UUID, int[]>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
      boolean changed = false;

      for(int i = 0; i < this.blockTypes.size(); ++i) {
         NbtBlockType targetBlock = (NbtBlockType)this.blockTypes.get(i);
         int tmpMeta = targetBlock.m >= 0 && targetBlock.m != 32767 ? state.getBlock().getMetaFromState(state) : 32767;
         boolean oreMatch = targetBlock.oreDict.length() > 0 && OreDictionary.getOres(targetBlock.oreDict).contains(new ItemStack(state.getBlock(), 1, tmpMeta));
         if ((oreMatch || state.getBlock() == targetBlock.b && (targetBlock.m < 0 || state.getBlock().getMetaFromState(state) == targetBlock.m)) && ItemComparison.CompareNBTTag(targetBlock.tags, tags, true)) {
            progress.forEach((entry) -> {
               if (((int[])entry.getSecond())[index] < targetBlock.n) {
                  ++((int[])entry.getSecond())[index];
               }
            });
            changed = true;
            break;
         }
      }

      if (changed) {
         this.setBulkProgress(progress);
         this.detect(pInfo, quest);
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      NBTTagList bAry = new NBTTagList();

      for(NbtBlockType block : this.blockTypes) {
         bAry.appendTag(block.writeToNBT(new NBTTagCompound()));
      }

      nbt.setTag("blocks", bAry);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.blockTypes.clear();
      NBTTagList bList = nbt.getTagList("blocks", 10);

      for(int i = 0; i < bList.tagCount(); ++i) {
         NbtBlockType block = new NbtBlockType();
         block.readFromNBT(bList.getCompoundTagAt(i));
         this.blockTypes.add(block);
      }

      if (nbt.hasKey("blockID", 8)) {
         Block targetBlock = (Block)Block.REGISTRY.getObject(new ResourceLocation(nbt.getString("blockID")));
         targetBlock = targetBlock != Blocks.AIR ? targetBlock : Blocks.LOG;
         int targetMeta = nbt.getInteger("blockMeta");
         NBTTagCompound targetNbt = nbt.getCompoundTag("blockNBT");
         int targetNum = nbt.getInteger("amount");
         NbtBlockType leg = new NbtBlockType();
         leg.b = targetBlock;
         leg.m = targetMeta;
         leg.tags = targetNbt;
         leg.n = targetNum;
         this.blockTypes.add(leg);
      }

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
         } catch (Exception var11) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load UUID for task", var11);
         }
      }

      NBTTagList pList = nbt.getTagList("userProgress", 10);

      for(int n = 0; n < pList.tagCount(); ++n) {
         try {
            NBTTagCompound pTag = pList.getCompoundTagAt(n);
            UUID uuid = UUID.fromString(pTag.getString("uuid"));
            int[] data = new int[this.blockTypes.size()];
            NBTTagList dNbt = pTag.getTagList("data", 3);

            for(int i = 0; i < data.length && i < dNbt.tagCount(); ++i) {
               data[i] = dNbt.getIntAt(i);
            }

            this.userProgress.put(uuid, data);
         } catch (Exception var12) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load user progress for task", var12);
         }
      }

   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
      NBTTagList jArray = new NBTTagList();
      NBTTagList progArray = new NBTTagList();
      if (users != null) {
         users.forEach((uuid) -> {
            if (this.completeUsers.contains(uuid)) {
               jArray.appendTag(new NBTTagString(uuid.toString()));
            }

            int[] data = (int[])this.userProgress.get(uuid);
            if (data != null) {
               NBTTagCompound pJson = new NBTTagCompound();
               pJson.setString("uuid", uuid.toString());
               NBTTagList pArray = new NBTTagList();

               for(int i : data) {
                  pArray.appendTag(new NBTTagInt(i));
               }

               pJson.setTag("data", pArray);
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
            NBTTagList pArray = new NBTTagList();

            for(int i : data) {
               pArray.appendTag(new NBTTagInt(i));
            }

            pJson.setTag("data", pArray);
            progArray.appendTag(pJson);
         });
      }

      nbt.setTag("completeUsers", jArray);
      nbt.setTag("userProgress", progArray);
      return nbt;
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

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskBlockBreak(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen screen, DBEntry<IQuest> context) {
      return null;
   }

   private void setUserProgress(UUID uuid, int[] progress) {
      this.userProgress.put(uuid, progress);
   }

   public int[] getUsersProgress(UUID uuid) {
      int[] progress = (int[])this.userProgress.get(uuid);
      return progress != null && progress.length == this.blockTypes.size() ? progress : new int[this.blockTypes.size()];
   }

   private List<Tuple<UUID, int[]>> getBulkProgress(@Nonnull List<UUID> uuids) {
      if (uuids.size() <= 0) {
         return Collections.emptyList();
      } else {
         List<Tuple<UUID, int[]>> list = new ArrayList();
         uuids.forEach((key) -> {
            list.add(new Tuple(key, this.getUsersProgress(key)));
         });
         return list;
      }
   }

   private void setBulkProgress(@Nonnull List<Tuple<UUID, int[]>> list) {
      list.forEach((entry) -> {
         this.setUserProgress((UUID)entry.getFirst(), (int[])entry.getSecond());
      });
   }
}
