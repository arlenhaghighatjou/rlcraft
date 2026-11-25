package com.fantasticsource.fantasticlib.config;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.LangKey;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeDouble;
import net.minecraftforge.common.config.Config.RangeInt;

public class GUIConfig {
   @Name("Focus Zoom on Mouse by Default")
   @LangKey("fantasticlib.config.zoomFocusMouse")
   @Comment({"Whether zooming in/out focuses on the mouse position or not in a zoomable GUI view (if not, center of view is used instead)", "Whichever mode is *NOT* the default can be accessed by holding the ctrl key while using zoom controls"})
   public boolean zoomFocusMouse = true;
   @Name("Reset Zoom (Mouse Button)")
   @LangKey("fantasticlib.config.zoomResetButton")
   @Comment({"Which mouse button resets the zoom level in a zoomable GUI view (click)"})
   @RangeInt(
      min = 0
   )
   public int zoomResetButton = 2;
   @Name("Pan (Mouse Button)")
   @LangKey("fantasticlib.config.panButton")
   @Comment({"Which mouse button pans the view in a pannable GUI view (hold and drag)"})
   @RangeInt(
      min = 0
   )
   public int panButton = 2;
   @Name("Zoom Rate")
   @LangKey("fantasticlib.config.zoomRate")
   @Comment({"How fast zoomable views zoom"})
   @RangeDouble(
      min = 1.1D,
      max = 4.0D
   )
   public double zoomRate = 1.2D;
   @Name("Per-GUI Scaling")
   @LangKey("fantasticlib.config.perGUIScaling")
   @Comment({"The scaling for each applicable GUI.", "Applicable GUIs should show up here automatically if they've been accessed at least once."})
   public String[] perGUIScaling = new String[0];

   public GUIConfig() {
   }
}
