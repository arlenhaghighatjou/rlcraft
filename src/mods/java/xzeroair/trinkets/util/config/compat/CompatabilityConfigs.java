package xzeroair.trinkets.util.config.compat;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RequiresMcRestart;
import net.minecraftforge.common.config.Config.RequiresWorldRestart;

public class CompatabilityConfigs {
   @RequiresMcRestart
   @Comment({"Should Items that implement IBauble be allowed in the Trinkets Gui", "WARNING.", "This is Very Experimental, Baubles equipped might only partially work, not at all, or Potentially cause a crash."})
   @Name("Baubles")
   public boolean baubles = false;
   @Comment({"Should Trinkets & Baubles Items only work in the T&B Container"})
   @Name("Accessories Use T&B Container Only")
   public boolean xatItemsInTrinketGuiOnly = false;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles use ArtemisLib to handle Height Adjustments to the player"})
   @Name("ArtemisLib")
   public boolean artemislib = false;
   @RequiresMcRestart
   @Name("ElenaiDodge")
   public boolean elenaiDodge = true;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles Interact with Tough as Nails"})
   @Name("Tough as Nails")
   public boolean toughasnails = true;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles Interact with Simple Difficulty"})
   @Name("Simple Difficulty")
   public boolean simpledifficulty = true;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles Interact with Lycanites Mobs"})
   @Name("Lycanites Mobs")
   public boolean lycanites = true;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles Interact with Defiled Lands"})
   @Name("Defiled Lands")
   public boolean defiledlands = true;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles prevent some of the Visuals in Enhanced Visuals"})
   @Name("Enhanced Visuals")
   public boolean enhancedvisuals = true;
   @RequiresWorldRestart
   @Comment({"Should Trinkets and Baubles Stone of the Sea work Infinite Oxygen work with Better Diving"})
   @Name("Better Diving")
   public boolean betterdiving = true;

   public CompatabilityConfigs() {
   }
}
