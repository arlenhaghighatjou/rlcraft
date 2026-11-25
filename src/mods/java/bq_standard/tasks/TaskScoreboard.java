package bq_standard.tasks;

import betterquesting.api.questing.IQuest;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import betterquesting.api2.utils.ParticipantInfo;
import bq_standard.ScoreboardBQ;
import bq_standard.client.gui.editors.tasks.GuiEditTaskScoreboard;
import bq_standard.client.gui.tasks.PanelTaskScoreboard;
import bq_standard.core.BQ_Standard;
import bq_standard.network.handlers.NetScoreSync;
import bq_standard.tasks.factory.FactoryTaskScoreboard;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import net.lax1dude.eaglercraft.v1_8.EaglercraftUUID;
import java.util.function.Consumer;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.nbt.NBTTagString;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class TaskScoreboard implements ITaskTickable {
   private final Set<EaglercraftUUID> completeUsers = new TreeSet();
   public String scoreName = "Score";
   public String scoreDisp = "Score";
   public String type = "dummy";
   public int target = 1;
   public float conversion = 1.0F;
   public String suffix = "";
   public TaskScoreboard.ScoreOperation operation = TaskScoreboard.ScoreOperation.MORE_OR_EQUAL;

   public TaskScoreboard() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryTaskScoreboard.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.task.scoreboard";
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
      if (pInfo.PLAYER.ticksExisted % 20 == 0) {
         this.detect(pInfo, quest);
      }

   }

   public void detect(@Nonnull ParticipantInfo pInfo, DBEntry<IQuest> quest) {
      Scoreboard board = pInfo.PLAYER.getWorldScoreboard();
      ScoreObjective scoreObj = board.getObjective(this.scoreName);
      if (scoreObj == null) {
         try {
            IScoreCriteria criteria = (IScoreCriteria)IScoreCriteria.INSTANCES.computeIfAbsent(this.type, (t) -> {
               return (void)(new ScoreCriteria(this.scoreName));
            });
            scoreObj = board.addScoreObjective(this.scoreName, criteria);
            scoreObj.setDisplayName(this.scoreDisp);
         } catch (Exception var7) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to create score '" + this.scoreName + "' for task!", var7);
            return;
         }
      }

      int points = board.getOrCreateScore(pInfo.PLAYER.getName(), scoreObj).getScorePoints();
      int lastValue = ScoreboardBQ.INSTANCE.getScore(pInfo.EaglercraftUUID, this.scoreName);
      if (points != lastValue) {
         ScoreboardBQ.INSTANCE.setScore(pInfo.EaglercraftUUID, this.scoreName, points);
         if (pInfo.PLAYER instanceof EntityPlayerMP) {
            NetScoreSync.sendScore((EntityPlayerMP)pInfo.PLAYER);
         }
      }

      if (this.operation.checkValues(points, this.target)) {
         this.setComplete(pInfo.EaglercraftUUID);
         pInfo.markDirty(Collections.singletonList(Integer.valueOf(quest.getID())));
      }

   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setString("scoreName", this.scoreName);
      json.setString("scoreDisp", this.scoreDisp);
      json.setString("type", this.type);
      json.setInteger("target", this.target);
      json.setFloat("unitConversion", this.conversion);
      json.setString("unitSuffix", this.suffix);
      json.setString("operation", this.operation.name());
      return json;
   }

   public void readFromNBT(NBTTagCompound json) {
      this.scoreName = json.getString("scoreName");
      this.scoreName = this.scoreName.replaceAll(" ", "_");
      this.scoreDisp = json.getString("scoreDisp");
      this.type = json.hasKey("type", 8) ? json.getString("type") : "dummy";
      this.target = json.getInteger("target");
      this.conversion = json.getFloat("unitConversion");
      this.suffix = json.getString("unitSuffix");

      try {
         this.operation = TaskScoreboard.ScoreOperation.valueOf(json.hasKey("operation", 8) ? json.getString("operation") : "MORE_OR_EQUAL");
      } catch (Exception var3) {
         this.operation = TaskScoreboard.ScoreOperation.MORE_OR_EQUAL;
      }

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

   @SideOnly(Side.CLIENT)
   public IGuiPanel getTaskGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelTaskScoreboard(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getTaskEditor(GuiScreen parent, DBEntry<IQuest> quest) {
      return new GuiEditTaskScoreboard(parent, quest, this);
   }

   public static enum ScoreOperation {
      EQUAL("="),
      LESS_THAN("<"),
      MORE_THAN(">"),
      LESS_OR_EQUAL("<="),
      MORE_OR_EQUAL(">="),
      NOT("=/=");

      private final String text;

      private ScoreOperation(String text) {
         this.text = text;
      }

      public String GetText() {
         return this.text;
      }

      public boolean checkValues(int n1, int n2) {
         switch(this) {
         case EQUAL:
            return n1 == n2;
         case LESS_THAN:
            return n1 < n2;
         case MORE_THAN:
            return n1 > n2;
         case LESS_OR_EQUAL:
            return n1 <= n2;
         case MORE_OR_EQUAL:
            return n1 >= n2;
         case NOT:
            return n1 != n2;
         default:
            return false;
         }
      }
   }
}
