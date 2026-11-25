package com.teamacronymcoders.contenttweaker.modules.vanilla.utils.commands;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.CommandResultStats.Type;
import net.minecraft.entity.Entity;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.world.World;

public class CommandSenderWrapper implements ICommandSender {
   private final ICommandSender parent;
   private final boolean bypassPermissions;
   private final boolean sendMessages;

   public CommandSenderWrapper(ICommandSender parent, boolean bypassPermissions, boolean sendMessages) {
      this.parent = parent;
      this.bypassPermissions = bypassPermissions;
      this.sendMessages = sendMessages;
   }

   @Nonnull
   public String getName() {
      return this.parent.getName();
   }

   @Nonnull
   public ITextComponent getDisplayName() {
      return this.parent.getDisplayName();
   }

   public void sendMessage(@Nonnull ITextComponent component) {
      if (this.sendMessages) {
         this.parent.sendMessage(component);
      }

   }

   public boolean canUseCommand(int permLevel, @Nonnull String commandName) {
      return this.bypassPermissions || this.parent.canUseCommand(permLevel, commandName);
   }

   @Nonnull
   public BlockPos getPosition() {
      return this.parent.getPosition();
   }

   @Nonnull
   public Vec3d getPositionVector() {
      return this.parent.getPositionVector();
   }

   @Nonnull
   public World getEntityWorld() {
      return this.parent.getEntityWorld();
   }

   @Nullable
   public Entity getCommandSenderEntity() {
      return this.parent.getCommandSenderEntity();
   }

   public boolean sendCommandFeedback() {
      return this.sendMessages && this.parent.sendCommandFeedback();
   }

   public void setCommandStat(@Nonnull Type type, int amount) {
      this.parent.setCommandStat(type, amount);
   }

   @Nullable
   public MinecraftServer getServer() {
      return this.parent.getServer();
   }
}
