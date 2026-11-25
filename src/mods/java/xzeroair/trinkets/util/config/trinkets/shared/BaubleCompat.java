package xzeroair.trinkets.util.config.trinkets.shared;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class BaubleCompat {
   private static final String PREFIX = "xat.config.baubles";
   @Comment({"If the mod Baubles is installed what bauble slot should it use", "Available Types:", "Trinket, Any, All", "Amulet, Necklace, Pendant", "Ring, Rings", "Belt", "Head, Hat", "Body, Chest", "Charm"})
   @Name("Bauble Type")
   @RequiresWorldRestart
   @LangKey("xat.config.baubles.type")
   public String bauble_type = "trinket";
   @Name("Equip Multiple")
   public boolean equip_multiple = false;

   public BaubleCompat(String string) {
      this.bauble_type = string;
   }
}
