package slimeknights.mantle.client.book.data.element;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ImageData extends DataLocation {
   public static final ImageData MISSING = new ImageData();
   public ItemStackData item;
   public int u;
   public int v;
   public int uw;
   public int vh;
   public int texWidth;
   public int texHeight;
   public int x;
   public int y;
   public int width;
   public int height;
   public int colorMultiplier;

   public ImageData() {
      this.item = null;
      this.u = 0;
      this.v = 0;
      this.uw = 256;
      this.vh = 256;
      this.texWidth = 256;
      this.texHeight = 256;
      this.x = -1;
      this.y = -1;
      this.width = -1;
      this.height = -1;
      this.colorMultiplier = 16777215;
   }

   public ImageData(ResourceLocation location, int u, int v, int uw, int vh, int texWidth, int texHeight) {
      this(location, u, v, uw, vh, texWidth, texHeight, uw, vh, 16777215);
   }

   public ImageData(ResourceLocation location, int u, int v, int uw, int vh, int texWidth, int texHeight, int colorMultiplier) {
      this(location, u, v, uw, vh, texWidth, texHeight, uw, vh);
   }

   public ImageData(ResourceLocation location, int u, int v, int uw, int vh, int texWidth, int texHeight, int width, int height) {
      this(location, u, v, uw, vh, texWidth, texHeight, width, height, 16777215);
   }

   public ImageData(ResourceLocation location, int u, int v, int uw, int vh, int texWidth, int texHeight, int width, int height, int colorMultiplier) {
      this.item = null;
      this.u = 0;
      this.v = 0;
      this.uw = 256;
      this.vh = 256;
      this.texWidth = 256;
      this.texHeight = 256;
      this.x = -1;
      this.y = -1;
      this.width = -1;
      this.height = -1;
      this.colorMultiplier = 16777215;
      this.location = location;
      this.u = u;
      this.v = v;
      this.uw = uw;
      this.vh = vh;
      this.texWidth = texWidth;
      this.texHeight = texHeight;
      this.width = width;
      this.height = height;
      this.colorMultiplier = colorMultiplier;
   }

   static {
      MISSING.location = new ResourceLocation("mantle:textures/gui/missingno.png");
      MISSING.texWidth = 32;
      MISSING.texHeight = 32;
      MISSING.uw = 32;
      MISSING.vh = 32;
   }
}
