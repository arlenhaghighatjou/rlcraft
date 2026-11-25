package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.tasks.ITask;
import betterquesting.api.utils.BigItemStack;
import betterquesting.api.utils.ItemComparison;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.NbtBlockType;
import bq_standard.client.gui.tasks.PanelTaskInteractItem;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskInteractItem;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.EnumHand;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class TaskInteractItem implements ITask {
   private final Set<EaglercraftUUID> completeUsers = new TreeSet();
   private final HashMap<EaglercraftUUID, Integer> userProgress = new HashMap();
   public BigItemStack targetItem = new BigItemStack(Items.AIR);
   public final NbtBlockType targetBlock = new NbtBlockType(Blocks.AIR);
   public boolean partialMatch = true;
   public boolean ignoreNBT = false;
   public boolean useMainHand = true;
   public boolean useOffHand = true;
   public boolean onInteract = true;
   public boolean onHit = false;
   public int required = 1;

   public TaskInteractItem() {
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.interact_item";
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskInteractItem.INSTANCE.getRegistryName();
   }

   public void onInteract(ParticipantInfo pInfo, DBEntry<IQuest> quest, EnumHand hand, ItemStack item, IBlockState state, BlockPos pos, boolean isHit) {
      if ((this.onHit || !isHit) && (this.onInteract || isHit)) {
         if ((this.useMainHand || hand != EnumHand.MAIN_HAND) && (this.useOffHand || hand != EnumHand.OFF_HAND)) {
            if (this.targetBlock.b != Blocks.AIR) {
               if (state.getBlock() == Blocks.AIR) {
                  return;
               }

               TileEntity tile = state.getBlock().hasTileEntity(state) ? pInfo.PLAYER.world.getTileEntity(pos) : null;
               NBTTagCompound tags = tile == null ? null : tile.writeToNBT(new NBTTagCompound());
               int tmpMeta = this.targetBlock.m >= 0 && this.targetBlock.m != 32767 ? state.getBlock().getMetaFromState(state) : 32767;
               boolean oreMatch = this.targetBlock.oreDict.length() > 0 && OreDictionary.getOres(this.targetBlock.oreDict).contains(new ItemStack(state.getBlock(), 1, tmpMeta));
               if (!oreMatch && (state.getBlock() != this.targetBlock.b || this.targetBlock.m >= 0 && state.getBlock().getMetaFromState(state) != this.targetBlock.m) || !ItemComparison.CompareNBTTag(this.targetBlock.tags, tags, true)) {
                  return;
               }
            }

            if (this.targetItem.getBaseStack().getItem() != Items.AIR) {
               if (this.targetItem.hasOreDict() && !ItemComparison.OreDictionaryMatch(this.targetItem.getOreIngredient(), this.targetItem.GetTagCompound(), item, !this.ignoreNBT, this.partialMatch)) {
                  return;
               }

               if (!ItemComparison.StackMatch(this.targetItem.getBaseStack(), item, !this.ignoreNBT, this.partialMatch)) {
                  return;
               }
            }

            List<Tuple<EaglercraftUUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
            progress.forEach((value) -> {
               if (!this.isComplete((EaglercraftUUID)value.getFirst())) {
                  int np = Math.min(this.required, ((Integer)value.getSecond()).intValue() + 1);
                  this.setUserProgress((EaglercraftUUID)value.getFirst(), Integer.valueOf(np));
                  if (np >= this.required) {
                     this.setComplete((EaglercraftUUID)value.getFirst());
                  }

               }
            });
            pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }
      }
   }

   public void detect(ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      List<Tuple<EaglercraftUUID, Integer>> progress = this.getBulkProgress(pInfo.ALL_UUIDS);
      progress.forEach((value) -> {
         if (((Integer)value.getSecond()).intValue() >= this.required) {
            this.setComplete((EaglercraftUUID)value.getFirst());
         }

      });
      pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
   }

   public boolean isComplete(EaglercraftUUID uuid) {
      return this.completeUsers.contains(uuid);
   }

   public void setComplete(EaglercraftUUID uuid) {
      this.completeUsers.add(uuid);
   }

   public void resetUser(@Nullable EaglercraftUUID uuid) {
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
      return new PanelTaskInteractItem(rect, this);
   }

   @Nullable
   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
         this.userProgress.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(EaglercraftUUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var9) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load EaglercraftUUID for task", var9);
         }
      }

      NBTTagList pList = nbt.getTagList("userProgress", 10);

      for(int n = 0; n < pList.tagCount(); ++n) {
         try {
            NBTTagCompound pTag = pList.getCompoundTagAt(n);
            EaglercraftUUID uuid = EaglercraftUUID.fromString(pTag.getString("uuid"));
            this.userProgress.put(uuid, Integer.valueOf(pTag.getInteger("value")));
         } catch (Exception var8) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load user progress for task", var8);
         }
      }

   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<EaglercraftUUID> users) {
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

   public synchronized NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setTag("item", this.targetItem.writeToNBT(new NBTTagCompound()));
      nbt.setTag("block", this.targetBlock.writeToNBT(new NBTTagCompound()));
      nbt.setBoolean("ignoreNbt", this.ignoreNBT);
      nbt.setBoolean("partialMatch", this.partialMatch);
      nbt.setBoolean("allowMainHand", this.useMainHand);
      nbt.setBoolean("allowOffHand", this.useOffHand);
      nbt.setInteger("requiredUses", this.required);
      nbt.setBoolean("onInteract", this.onInteract);
      nbt.setBoolean("onHit", this.onHit);
      return nbt;
   }

   public synchronized void readFromNBT(NBTTagCompound nbt) {
      this.targetItem = new BigItemStack(nbt.getCompoundTag("item"));
      this.targetBlock.readFromNBT(nbt.getCompoundTag("block"));
      this.ignoreNBT = nbt.getBoolean("ignoreNbt");
      this.partialMatch = nbt.getBoolean("partialMatch");
      this.useMainHand = nbt.getBoolean("allowMainHand");
      this.useOffHand = nbt.getBoolean("allowOffHand");
      this.required = nbt.getInteger("requiredUses");
      this.onInteract = nbt.getBoolean("onInteract");
      this.onHit = nbt.getBoolean("onHit");
   }

   private void setUserProgress(EaglercraftUUID uuid, Integer progress) {
      this.userProgress.put(uuid, progress);
   }

   public int getUsersProgress(EaglercraftUUID uuid) {
      Integer n = (Integer)this.userProgress.get(uuid);
      return n == null ? 0 : n.intValue();
   }

   private List<Tuple<EaglercraftUUID, Integer>> getBulkProgress(@Nonnull List<EaglercraftUUID> uuids) {
      if (uuids.size() <= 0) {
         return Collections.emptyList();
      } else {
         List<Tuple<EaglercraftUUID, Integer>> list = new ArrayList();
         uuids.forEach((key) -> {
            list.add(new Tuple(key, this.getUsersProgress(key)));
         });
         return list;
      }
   }
}
