package greymerk.roguelike.command;

import net.minecraft.command.NumberInvalidException;

public class CommandBase1_12 implements CommandBase {
   public CommandBase1_12() {
   }

   public boolean doesStringStartWith(String original, String region) {
      return net.minecraft.command.CommandBase.doesStringStartWith(original, region);
   }

   public int parseInt(String input) {
      try {
         return net.minecraft.command.CommandBase.parseInt(input);
      } catch (NumberInvalidException var3) {
         throw new RuntimeException(var3);
      }
   }
}
