package bq_standard.rewards;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.questing.IQuest;
import betterquesting.api.questing.rewards.IReward;
import betterquesting.api2.client.gui.misc.IGuiRect;
import betterquesting.api2.client.gui.panels.IGuiPanel;
import betterquesting.api2.storage.DBEntry;
import bq_standard.AdminExecute;
import bq_standard.client.gui.rewards.PanelRewardCommand;
import bq_standard.rewards.factory.FactoryRewardCommand;
import io.netty.buffer.ByteBuf;
import java.util.Arrays;
import java.util.UUID;
import javax.annotation.Nonnull;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.command.FunctionObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.CommandBlockBaseLogic;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class RewardCommand implements IReward {
   public String command = "#Script Comment\nsay Running reward script...\nsay @s Claimed a reward";
   public String title = "bq_standard.reward.command";
   public String desc = "Run a command script";
   public boolean viaPlayer = false;
   public boolean hideIcon = true;
   public boolean asScript = true;

   public RewardCommand() {
   }

   public ResourceLocation getFactoryID() {
      return FactoryRewardCommand.INSTANCE.getRegistryName();
   }

   public String getUnlocalisedName() {
      return this.title;
   }

   public boolean canClaim(EntityPlayer player, DBEntry<IQuest> quest) {
      return true;
   }

   public void claimReward(EntityPlayer player, DBEntry<IQuest> quest) {
      if (!player.world.isRemote) {
         UUID playerID = QuestingAPI.getQuestingUUID(player);
         String tmp = this.command.replaceAll("VAR_NAME", player.getName());
         String finCom = tmp.replaceAll("VAR_UUID", playerID.toString());
         String[] comAry = finCom.split("\n");
         MinecraftServer server = player.world.getMinecraftServer();
         ICommandSender sender = (ICommandSender)(this.viaPlayer ? new AdminExecute(player) : new RewardCommand.RewardCommandSender(player));
         if (this.asScript) {
            for(int i = 0; i < comAry.length; ++i) {
               if (comAry[i].startsWith("/")) {
                  comAry[i] = comAry[i].replaceFirst("/", "");
               }
            }

            FunctionObject func = FunctionObject.create(server.getFunctionManager(), Arrays.asList(comAry));
            server.getFunctionManager().execute(func, sender);
         } else {
            server.getCommandManager().executeCommand(sender, finCom);
         }

      }
   }

   public void readFromNBT(NBTTagCompound nbt) {
      this.command = nbt.getString("command");
      this.title = nbt.hasKey("title", 8) ? nbt.getString("title") : "bq_standard.reward.command";
      this.desc = nbt.hasKey("description", 8) ? nbt.getString("description") : "Run a command script";
      this.viaPlayer = nbt.getBoolean("viaPlayer");
      this.hideIcon = !nbt.hasKey("hideBlockIcon", 1) || nbt.getBoolean("hideBlockIcon");
      this.asScript = !nbt.hasKey("asScript", 1) || nbt.getBoolean("asScript");
   }

   public NBTTagCompound writeToNBT(NBTTagCompound nbt) {
      nbt.setString("command", this.command);
      nbt.setString("title", this.title);
      nbt.setString("description", this.desc);
      nbt.setBoolean("viaPlayer", this.viaPlayer);
      nbt.setBoolean("hideBlockIcon", this.hideIcon);
      nbt.setBoolean("asScript", this.asScript);
      return nbt;
   }

   public IGuiPanel getRewardGui(IGuiRect rect, DBEntry<IQuest> quest) {
      return new PanelRewardCommand(rect, this);
   }

   public GuiScreen getRewardEditor(GuiScreen screen, DBEntry<IQuest> quest) {
      return null;
   }

   public static class RewardCommandSender extends CommandBlockBaseLogic {
      private final Entity entity;

      private RewardCommandSender(@Nonnull Entity entity) {
         this.entity = entity;
      }

      @Nonnull
      public BlockPos getPosition() {
         return this.entity.getPosition();
      }

      @Nonnull
      public Vec3d getPositionVector() {
         return this.entity.getPositionVector();
      }

      @Nonnull
      public World getEntityWorld() {
         return this.entity.getEntityWorld();
      }

      public Entity getCommandSenderEntity() {
         return this.entity;
      }

      public void updateCommand() {
      }

      public int getCommandBlockType() {
         return 0;
      }

      public void fillInInfo(@Nonnull ByteBuf p_145757_1_) {
      }

      @Nonnull
      public String getName() {
         return "BetterQuesting";
      }

      public MinecraftServer getServer() {
         return this.entity.getServer();
      }
   }
}
