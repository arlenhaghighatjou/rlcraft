package greymerk.roguelike.command;

import com.github.fnar.minecraft.item.RldItemStack;
import greymerk.roguelike.worldgen.Coord;
import greymerk.roguelike.worldgen.WorldEditor;

public interface CommandSender {
   void sendMessage(String var1, MessageType var2);

   void give(RldItemStack var1);

   WorldEditor createWorldEditor();

   Coord getPos();
}
