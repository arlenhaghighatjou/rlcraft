package bq_standard.rewards;

import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import bq_standard.client.gui.rewards.PanelRewardScoreboard;
import bq_standard.core.BQ_Standard;
import bq_standard.rewards.factory.FactoryRewardScoreboard;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.scoreboard.IScoreCriteria;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreCriteria;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class RewardScoreboard implements IReward {
   public String score = "Reputation";
   public String type = "dummy";
   public boolean relative = true;
   public int value = 1;

   public RewardScoreboard() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardScoreboard.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return "bq_standard.reward.scoreboard";
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      return true;
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
      Scoreboard board = player.getWorldScoreboard();
      ScoreObjective scoreObj = board.getObjective(this.score);
      if (scoreObj == null) {
         try {
            IScoreCriteria criteria = (IScoreCriteria)IScoreCriteria.INSTANCES.get(this.type);
            criteria = (IScoreCriteria)(criteria != null ? criteria : new ScoreCriteria(this.score));
            scoreObj = board.addScoreObjective(this.score, criteria);
            scoreObj.setDisplayName(this.score);
         } catch (Exception var6) {
            BQ_Standard.logger.log(Level.ERROR, "Unable to create score '" + this.score + "' for reward!", var6);
         }
      }

      if (scoreObj != null && !scoreObj.getCriteria().isReadOnly()) {
         Score s = board.getOrCreateScore(player.getName(), scoreObj);
         if (this.relative) {
            s.increaseScore(this.value);
         } else {
            s.setScorePoints(this.value);
         }

      }
   }

   public void readFromNBT(NBTTagCompound json) {
      this.score = json.getString("score");
      this.type = json.getString("type");
      this.value = json.getInteger("value");
      this.relative = json.getBoolean("relative");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound json) {
      json.setString("score", this.score);
      json.setString("type", "dummy");
      json.setInteger("value", this.value);
      json.setBoolean("relative", this.relative);
      return json;
   }

   @SideOnly(Side.CLIENT)
   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelRewardScoreboard(rect, this);
   }

   @SideOnly(Side.CLIENT)
   public GuiScreen getRewardEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }
}
