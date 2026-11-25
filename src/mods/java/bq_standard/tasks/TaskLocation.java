package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.client.gui.tasks.PanelTaskLocation;
import bq_standard.core.BQ_Standard;
import bq_standard.tasks.factory.FactoryTaskLocation;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.StringUtils;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class TaskLocation implements ITaskTickable {
   private final Set<EaglercraftUUID> completeUsers = new TreeSet();
   public String name = "New Location";
   public String structure = "";
   public String biome = "";
   public int x = 0;
   public int y = 0;
   public int z = 0;
   public int dim = 0;
   public int range = -1;
   public boolean visible = false;
   public boolean hideInfo = false;
   public boolean invert = false;
   public boolean taxiCab = false;

   public TaskLocation() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskLocation.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.location";
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

   private void internalDetect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      if (pInfo.PLAYER.isEntityAlive() && pInfo.PLAYER instanceof EntityPlayerMP) {
         EntityPlayerMP playerMP = (EntityPlayerMP)pInfo.PLAYER;
         boolean flag = false;
         if (playerMP.dimension == this.dim && (this.range <= 0 || this.getDistance(playerMP) <= (double)this.range)) {
            if (!StringUtils.isNullOrEmpty(this.biome) && !(new ResourceLocation(this.biome)).equals(playerMP.getServerWorld().getBiome(playerMP.getPosition()).getRegistryName())) {
               if (!this.invert) {
                  return;
               }
            } else if (!StringUtils.isNullOrEmpty(this.structure) && !playerMP.getServerWorld().getChunkProvider().isInsideStructure(playerMP.world, this.structure, playerMP.getPosition())) {
               if (!this.invert) {
                  return;
               }
            } else if (this.visible && this.range > 0) {
               Vec3d pPos = new Vec3d(playerMP.posX, playerMP.posY + (double)playerMP.getEyeHeight(), playerMP.posZ);
               Vec3d tPos = new Vec3d((double)this.x, (double)this.y, (double)this.z);
               RayTraceResult mop = playerMP.world.rayTraceBlocks(pPos, tPos, false, true, false);
               if (mop == null || mop.typeOfHit != Type.BLOCK) {
                  flag = true;
               }
            } else {
               flag = true;
            }
         }

         if (flag != this.invert) {
            pInfo.ALL_UUIDS.forEach((uuid) -> {
               if (!this.isComplete(uuid)) {
                  this.setComplete(uuid);
               }

            });
            pInfo.markDirtyParty(Collections.singletonList(Integer.valueOf(quest.getID())));
         }

      }
   }

   private double getDistance(EntityPlayer player) {
      if (!this.taxiCab) {
         return player.getDistance((double)this.x, (double)this.y, (double)this.z);
      } else {
         BlockPos pPos = player.getPosition();
         return (double)(Math.abs(pPos.getX() - this.x) + Math.abs(pPos.getY() - this.y) + Math.abs(pPos.getZ() - this.z));
      }
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("name", this.name);
      nbt.setInteger("posX", this.x);
      nbt.setInteger("posY", this.y);
      nbt.setInteger("posZ", this.z);
      nbt.setInteger("dimension", this.dim);
      nbt.setString("biome", this.biome);
      nbt.setString("structure", this.structure);
      nbt.setInteger("range", this.range);
      nbt.setBoolean("visible", this.visible);
      nbt.setBoolean("hideInfo", this.hideInfo);
      nbt.setBoolean("invert", this.invert);
      nbt.setBoolean("taxiCabDist", this.taxiCab);
      return nbt;
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.name = nbt.getString("name");
      this.x = nbt.getInteger("posX");
      this.y = nbt.getInteger("posY");
      this.z = nbt.getInteger("posZ");
      this.dim = nbt.getInteger("dimension");
      this.biome = nbt.getString("biome");
      this.structure = nbt.getString("structure");
      this.range = nbt.getInteger("range");
      this.visible = nbt.getBoolean("visible");
      this.hideInfo = nbt.getBoolean("hideInfo");
      this.invert = nbt.getBoolean("invert") || nbt.getBoolean("invertDistance");
      this.taxiCab = nbt.getBoolean("taxiCabDist");
   }

   public NBTTagCompound writeProgressToNBT(NBTTagCompound nbt, @Nullable List<EaglercraftUUID> users) {
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
            this.completeUsers.add(EaglercraftUUID.fromString(cList.getStringTagAt(i)));
         } catch (Exception var6) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to load EaglercraftUUID for task", var6);
         }
      }

   }

   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskLocation(rect, this);
   }

   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return null;
   }
}
