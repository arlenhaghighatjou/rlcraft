package greymerk.roguelike.command;

import com.github.fnar.minecraft.item.RldItemStack;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;

public class CommandContext1_12 {
   private final CommandSender commandSender;

   public CommandContext1_12(CommandSender commandSender) {
      this.commandSender = commandSender;
   }

   public void sendFailure(Exception e) {
      this.sendFailure(e.getMessage());
      e.printStackTrace();
   }

   public void sendFailure(String message) {
      this.sendMessage("Failure: " + message, MessageType.ERROR);
   }

   public void sendInfo(String message) {
      this.sendMessage(message, MessageType.INFO);
   }

   public void sendSpecial(String message) {
      this.sendMessage(message, MessageType.SPECIAL);
   }

   public void sendSuccess(String message) {
      this.sendMessage("Success: " + message, MessageType.SUCCESS);
   }

   public void sendMessage(String message, MessageType type) {
      this.commandSender.sendMessage(message, type);
   }

   public WorldEditor createEditor() {
      return this.commandSender.createWorldEditor();
   }

   public Coord getPos() {
      return this.commandSender.getPos();
   }

   public void give(RldItemStack item) {
      this.commandSender.give(item);
   }
}
