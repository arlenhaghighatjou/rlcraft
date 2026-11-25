package bq_msi.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_msi.client.gui.PanelTaskRCLocate;
import bq_msi.tasks.factory.FactoryTaskRCLocate;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.UUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class TaskRCLocate implements ITaskTickable {
   private final Set<UUID> completeUsers = new TreeSet();
   public String structure = "";
   public String name = "New Structure";
   public int dim = 0;
   public boolean hideInfo = false;

   public TaskRCLocate() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskRCLocate.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_msi.task.rc_locate";
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
      } else {
         this.completeUsers.remove(uuid);
      }

   }

   public void tickTask(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (pInfo.PLAYER.ticksExisted % 100 == 0) {
         this.internalDetect(pInfo, quest);
      }

   }

   public void detect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      this.internalDetect(pInfo, quest);
   }

   public boolean DataReadCompare(String regionName, String structure, int dim, int xPos, int zPos) {
      File fileName = new File(DimensionManager.getCurrentSaveRootDirectory() + File.separator + "data" + File.separator + "QuestStructure" + File.separator + dim + File.separator + regionName + ".txt");

      try {
         FileReader fileReader = new FileReader(fileName);
         BufferedReader bufferedReader = new BufferedReader(fileReader);

         String line;
         while((line = bufferedReader.readLine()) != null) {
            if (line.contains(structure)) {
               String[] tokens = line.split(",");
               int minX = Integer.parseInt(tokens[1]);
               int maxX = Integer.parseInt(tokens[2]);
               int minZ = Integer.parseInt(tokens[3]);
               int maxZ = Integer.parseInt(tokens[4]);
               if (minX < xPos && xPos < maxX && minZ < zPos && zPos < maxZ) {
                  bufferedReader.close();
                  return true;
               }
            }
         }

         bufferedReader.close();
         return false;
      } catch (IOException var15) {
         if (var15 instanceof FileNotFoundException) {
            return false;
         } else {
            System.out.println(var15);
            return false;
         }
      }
   }

   public String convertPosToRegion(int xPos, int zPos) {
      int regionX = Math.round((float)(xPos / 512));
      int regionZ = Math.round((float)(zPos / 512));
      String regionName = "r." + Integer.toString(regionX) + "." + Integer.toString(regionZ);
      return regionName;
   }

   private void internalDetect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (pInfo.PLAYER.isEntityAlive() && pInfo.PLAYER instanceof EntityPlayerMP) {
         EntityPlayerMP playerMP = (EntityPlayerMP)pInfo.PLAYER;
         BlockPos pos = playerMP.getPosition();
         int xPos = pos.getX();
         int zPos = pos.getZ();
         String regionName = this.convertPosToRegion(xPos, zPos);
         if (playerMP.dimension == this.dim && !StringUtils.isNullOrEmpty(this.structure) && this.DataReadCompare(regionName, this.structure, this.dim, xPos, zPos)) {
            pInfo.ALL_UUIDS.forEach((uuid) -> {
               if (!this.isComplete(uuid)) {
                  this.setComplete(uuid);
               }

            });
            pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }

      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setInteger("dimension", this.dim);
      nbt.setString("structure", this.structure);
      nbt.setString("name", this.name);
      nbt.setBoolean("hideInfo", this.hideInfo);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.dim = nbt.getInteger("dimension");
      this.structure = nbt.getString("structure");
      this.name = nbt.getString("name");
      this.hideInfo = nbt.getBoolean("hideInfo");
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<UUID> users) {
      NBTTagList jArray = new NBTTagList();
      this.completeUsers.forEach((uuid) -> {
         if (users == null || users.contains(uuid)) {
            jArray.appendTag(new NBTTagString(uuid.toString()));
         }

      });
      nbt.setTag("completeUsers", jArray);
      return nbt;
   }

   public void readProgressFromNBT(NBTTagCompound nbt, boolean merge) {
      if (!merge) {
         this.completeUsers.clear();
      }

      NBTTagList cList = nbt.getTagList("completeUsers", 8);

      for(int i = 0; i < cList.tagCount(); ++i) {
         try {
            this.completeUsers.add(UUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            ;
         }
      }

   }

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskRCLocate(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }
}
