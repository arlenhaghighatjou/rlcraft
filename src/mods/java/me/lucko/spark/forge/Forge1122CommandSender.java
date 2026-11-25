package me.lucko.spark.forge;

import java.util.UUID;
import me.lucko.spark.common.command.sender.AbstractCommandSender;
import me.lucko.spark.forge.plugin.Forge1122SparkPlugin;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.serializer.gson.GsonComponentSerializer;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.network.rcon.RConConsoleSource;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.ITextComponent.Serializer;

public class Forge1122CommandSender extends AbstractCommandSender<ICommandSender> {
   private final Forge1122SparkPlugin plugin;

   public Forge1122CommandSender(ICommandSender source, Forge1122SparkPlugin plugin) {
      super(source);
      this.plugin = plugin;
   }

   public String getName() {
      if (super.delegate instanceof EntityPlayer) {
         return ((EntityPlayer)super.delegate).getGameProfile().getName();
      } else if (super.delegate instanceof MinecraftServer) {
         return "Console";
      } else {
         return super.delegate instanceof RConConsoleSource ? "RCON Console" : "unknown:" + ((ICommandSender)super.delegate).getClass().getSimpleName();
      }
   }

   public UUID getUniqueId() {
      return super.delegate instanceof EntityPlayer ? ((EntityPlayer)super.delegate).getUniqueID() : null;
   }

   public void sendMessage(Component message) {
      ITextComponent component = Serializer.jsonToComponent((String)GsonComponentSerializer.gson().serialize(message));
      ((ICommandSender)super.delegate).sendMessage(component);
   }

   public boolean hasPermission(String permission) {
      return this.plugin.hasPermission((ICommandSender)super.delegate, permission);
   }
}
