package ivorius.reccomplex..shadow.mcopts.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.EnumFacing;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilityProvider;

public class DelegatingSender implements ICommandSender, ICapabilityProvider {
   private final ICommandSender sender;

   public DelegatingSender(ICommandSender sender) {
      this.sender = sender;
   }

   public String getName() {
      return this.sender.getName();
   }

   public ITextComponent getDisplayName() {
      return this.sender.getDisplayName();
   }

   public void sendMessage(ITextComponent component) {
      this.sender.sendMessage(component);
   }

   public boolean canUseCommand(int permLevel, String commandName) {
      return this.sender.canUseCommand(permLevel, commandName);
   }

   public BlockPos getPosition() {
      return this.sender.getPosition();
   }

   public Vec3d getPositionVector() {
      return this.sender.getPositionVector();
   }

   public World getEntityWorld() {
      return this.sender.getEntityWorld();
   }

   @Nullable
   public Entity getCommandSenderEntity() {
      return this.sender.getCommandSenderEntity();
   }

   public boolean sendCommandFeedback() {
      return this.sender.sendCommandFeedback();
   }

   public void setCommandStat(Type type, int amount) {
      this.sender.setCommandStat(type, amount);
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.sender.getServer();
   }

   public boolean hasCapability(@Nonnull Capability<?> capability, @Nullable EnumFacing facing) {
      return this.sender instanceof ICapabilityProvider && ((ICapabilityProvider)this.sender).hasCapability(capability, facing);
   }

   @Nullable
   public <T> T getCapability(@Nonnull Capability<T> capability, @Nullable EnumFacing facing) {
      return (T)(this.sender instanceof ICapabilityProvider ? ((ICapabilityProvider)this.sender).getCapability(capability, facing) : null);
   }
}
