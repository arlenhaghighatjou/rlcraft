package xzeroair.trinkets.util.config.gui;

import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.SlidingOption;
import xzeroair.trinkets.util.Reference;

public class ClientConfigTrinketsContainer {
   @Name("01. Gui Texture")
   public String GuiTex = Reference.RESOURCE_PREFIX + "textures/gui/gui_inventory.png";
   @Comment({"Texture Size in Pixels. 2, 4, 8, 16, 32, 64, etc. Default: 256"})
   @Name("02. Gui Size")
   public int guiTexSize = 256;
   @Comment({"Trinkets API Gui X Potion. Default 0"})
   @Name("03. Gui Location X")
   public int X = -14;
   @Comment({"Trinkets API Gui Y Potion. Default 0"})
   @Name("04. Gui Location Y")
   public int Y = 7;
   @Comment({"Show Active Potion Effects while in the Trinkets Gui. Default: True"})
   @Name("05. Potion Icons")
   public boolean PotionIcons = true;
   @Name("06. Gui Z Level")
   @RangeInt(
      min = 0,
      max = 1
   )
   @SlidingOption
   public int Z = 1;
   @Comment({"Hex Color Followed by Alpha float value. Default: #FFFFFF:1"})
   @Name("07. Texture Color Overlay")
   public String GuiColor = "#FFFFFF:1";
   @Comment({"Gui Button Configs"})
   @Name("Gui Button")
   public ClientConfigTrinketsContainer.TrinketButton button = new ClientConfigTrinketsContainer.TrinketButton();

   public ClientConfigTrinketsContainer() {
   }

   public class TrinketButton {
      @Comment({"The Button ID for the Trinkets API. Default: 67"})
      @Name("01. Button ID")
      public int ID = 67;
      @Comment({"Trinkets API Button X Potion. Default 2"})
      @Name("02. Button Location X")
      public int X = -24;
      @Comment({"Trinkets API Button Y Potion. Default 37"})
      @Name("03. Button Location Y")
      public int Y = -24;
      @Comment({"Button X Offset. Default: -39"})
      @Name("04. X Offset")
      public int Xoffset = 0;
      @Comment({"Button Y Offset. Default: 0"})
      @Name("05. Y Offset")
      public int Yoffset = 0;
      @Comment({"The Clickable Height of the Button. Default: 11"})
      @Name("06. Button Height")
      public int bHeight = 10;
      @Comment({"The Clickable Width of the Button. Default: 4"})
      @Name("07. Button Width")
      public int bWidth = 10;
      private String image = Reference.RESOURCE_PREFIX + "textures/gui/gui_inventory.png";
      private int x = 208;
      private int y = 0;
      private int width = 10;
      private int height = 10;
      private int texWidth = 16;
      private int texHeight = 16;
      private int texSizeWidth = 256;
      private int texSizeHeight = 256;
      private int color = 16777215;
      public ConfigGuiButtonShared open;
      public ConfigGuiButtonShared close;

      public TrinketButton() {
         this.open = new ConfigGuiButtonShared(this.image, this.x + 16, this.y, this.width, this.height, this.texWidth, this.texHeight, this.texSizeWidth, this.texSizeHeight, this.color);
         this.close = new ConfigGuiButtonShared(this.image, this.x, this.y, this.width, this.height, this.texWidth, this.texHeight, this.texSizeWidth, this.texSizeHeight, this.color);
      }
   }
}
