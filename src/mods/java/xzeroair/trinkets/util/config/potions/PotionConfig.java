package xzeroair.trinkets.util.config.potions;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;

public class PotionConfig {
   private final String potion = ".potion";
   private final String PREFIX = "xat.config.potion";
   @RequiresMcRestart
   @Comment({"Item Catalyst to brew this potion"})
   @Name("Potion Catalyst")
   @LangKey("xat.config.potion.catalyst")
   public String catalyst = "xat:glowing_ingot";
   @RequiresMcRestart
   @Comment({"How long this potion should last in ticks\\n20 ticks per second"})
   @Name("Potion Effect Duration")
   @LangKey("xat.config.potion.duration")
   public int Duration = 1200;

   public PotionConfig(String itemID, int duration) {
      this.catalyst = itemID;
      this.Duration = duration;
   }
}
