package ivorius.reccomplex.block;

import io.netty.buffer.ByteBuf;
import ivorius.reccomplex.RCConfig;
import ivorius.reccomplex.utils.accessor.RCAccessorCommandBase;
import java.text.SimpleDateFormat;
import net.minecraft.command.CommandBase;
import net.minecraft.command.CommandResultStats;
import net.minecraft.command.ICommandListener;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.crash.CrashReport;
import net.minecraft.crash.CrashReportCategory;
import net.minecraft.crash.ICrashReportDetail;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.ReportedException;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.world.World;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public abstract class SpawnCommandLogic implements ICommandSender {
   private static final SimpleDateFormat TIMESTAMP_FORMAT = new SimpleDateFormat("HH:mm:ss");
   private String commandStored = "";
   private String customName = "@";
   private final CommandResultStats resultStats = new CommandResultStats();

   public SpawnCommandLogic() {
   }

   public NBTTagCompound writeToNBT(NBTTagCompound p_189510_1_) {
      p_189510_1_.setString("Command", this.commandStored);
      p_189510_1_.setString("CustomName", this.customName);
      this.resultStats.writeStatsToNBT(p_189510_1_);
      return p_189510_1_;
   }

   public void readDataFromNBT(NBTTagCompound nbt) {
      this.commandStored = nbt.getString("Command");
      if (nbt.hasKey("CustomName", 8)) {
         this.customName = nbt.getString("CustomName");
      }

      this.resultStats.readStatsFromNBT(nbt);
   }

   public boolean canUseCommand(int permLevel, String commandName) {
      return permLevel <= 2;
   }

   public void setCommand(String command) {
      this.commandStored = command;
   }

   public String getCommand() {
      return this.commandStored;
   }

   public void trigger(World worldIn) {
      if (!worldIn.isRemote) {
         MinecraftServer minecraftserver = this.getServer();
         if (minecraftserver != null && minecraftserver.isAnvilFileSet() && minecraftserver.isCommandBlockEnabled()) {
            ICommandManager icommandmanager = minecraftserver.getCommandManager();
            ICommandListener cachedAdmin = null;
            if (!RCConfig.notifyAdminOnBlockCommands) {
               cachedAdmin = RCAccessorCommandBase.getCommandAdmin();
               CommandBase.setCommandListener((ICommandListener)null);
            }

            try {
               icommandmanager.executeCommand(this, this.commandStored);
            } catch (Exception var8) {
               CrashReport crashreport = CrashReport.makeCrashReport(var8, "Executing command block");
               CrashReportCategory crashreportcategory = crashreport.makeCategory("Command to be executed");
               crashreportcategory.addDetail("Command", this::getCommand);
               crashreportcategory.addDetail("Name", this::getName);
               throw new ReportedException(crashreport);
            }

            if (!RCConfig.notifyAdminOnBlockCommands) {
               CommandBase.setCommandListener(cachedAdmin);
            }
         }
      }

   }

   public String getName() {
      return this.customName;
   }

   public ITextComponent getDisplayName() {
      return new TextComponentString(this.getName());
   }

   public void setName(String name) {
      this.customName = name;
   }

   public void sendMessage(ITextComponent component) {
   }

   public boolean sendCommandFeedback() {
      MinecraftServer minecraftserver = this.getServer();
      return minecraftserver == null || !minecraftserver.isAnvilFileSet() || minecraftserver.getWorld(0).getGameRules().getBoolean("commandBlockOutput");
   }

   public void setCommandStat(Type type, int amount) {
      this.resultStats.setCommandStatForSender(this.getServer(), this, type, amount);
   }

   public abstract void updateCommand();

   @SideOnly(Side.CLIENT)
   public abstract int getCommandBlockType();

   @SideOnly(Side.CLIENT)
   public abstract void fillInInfo(ByteBuf var1);

   public CommandResultStats getCommandResultStats() {
      return this.resultStats;
   }
}
