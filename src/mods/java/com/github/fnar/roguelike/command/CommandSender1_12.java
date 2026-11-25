package com.github.fnar.roguelike.command;

import com.github.fnar.minecraft.WorldEditor1_12;
import com.github.fnar.minecraft.item.RldItemStack;
import com.github.fnar.minecraft.item.mapper.ItemMapper1_12;
import greymerk.roguelike.command.CommandSender;
import greymerk.roguelike.command.MessageType;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.text.TextComponentString;

public class CommandSender1_12 implements CommandSender {
   private final ICommandSender commandSender;

   public CommandSender1_12(ICommandSender iCommandSender) {
      this.commandSender = iCommandSender;
   }

   public void sendMessage(String message, MessageType type) {
      String formattedMessage = type.apply(message);
      TextComponentString text = new TextComponentString(formattedMessage);
      this.commandSender.sendMessage(text);
   }

   public void give(RldItemStack item) {
      Entity player = this.commandSender.getCommandSenderEntity();
      ItemStack mappedItem = (new ItemMapper1_12()).map(item);
      EntityItem drop = player.entityDropItem(mappedItem, 0.0F);
      drop.setNoPickupDelay();
   }

   public WorldEditor createWorldEditor() {
      return new WorldEditor1_12(this.commandSender.getEntityWorld());
   }

   public Coord getPos() {
      BlockPos bp = this.commandSender.getPosition();
      return new Coord(bp.getX(), bp.getY(), bp.getZ());
   }
}
