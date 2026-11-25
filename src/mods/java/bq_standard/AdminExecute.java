package bq_standard;

import javax.annotation.Nonnull;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class AdminExecute implements ICommandSender {
   private final EntityPlayer player;

   public AdminExecute(EntityPlayer player) {
      this.player = player;
   }

   @Nonnull
   public String getName() {
      return this.player.getName();
   }

   @Nonnull
   public ITextComponent getDisplayName() {
      return this.player.getDisplayName();
   }

   public void sendMessage(ITextComponent p_145747_1_) {
      this.player.sendMessage(p_145747_1_);
   }

   public boolean canUseCommand(int p_70003_1_, @Nonnull String p_70003_2_) {
      return true;
   }

   @Nonnull
   public BlockPos getPosition() {
      return this.player.getPosition();
   }

   @Nonnull
   public World getEntityWorld() {
      return this.player.getEntityWorld();
   }

   @Nonnull
   public Vec3d getPositionVector() {
      return this.player.getPositionVector();
   }

   public Entity getCommandSenderEntity() {
      return this.player.getCommandSenderEntity();
   }

   public boolean sendCommandFeedback() {
      return this.player.sendCommandFeedback();
   }

   public void setCommandStat(Type type, int amount) {
      this.player.setCommandStat(type, amount);
   }

   public MinecraftServer getServer() {
      return this.player.getServer();
   }
}
