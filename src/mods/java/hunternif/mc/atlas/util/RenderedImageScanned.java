package hunternif.mc.atlas.util;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.RenderedImage;
import java.awt.image.SampleModel;
import java.awt.image.WritableRaster;
import java.util.Vector;
import java.util.function.Consumer;
import java.util.function.IntConsumer;

class RenderedImageScanned implements RenderedImage {
   private final int width;
   private final int height;
   private int bufY;
   private final BufferedImage scanBuffer;
   private final Consumer<Graphics2D> generator;
   private final IntConsumer rowListener;

   public RenderedImageScanned(int width, int height, BufferedImage scanBuffer, Consumer<Graphics2D> generator, IntConsumer rowListener) {
      this.width = width;
      this.height = height;
      this.scanBuffer = scanBuffer;
      this.generator = generator;
      this.rowListener = rowListener;
      this.bufY = 0;
      this.redrawToBuffer();
   }

   private void redrawToBuffer() {
      Log.info("Redrawing at %d", this.bufY);
      Graphics2D graphics = this.scanBuffer.createGraphics();
      graphics.setBackground(new Color(0, 0, 0, 0));
      graphics.clearRect(0, 0, this.scanBuffer.getWidth(), this.scanBuffer.getHeight());
      graphics.translate(0, -this.bufY);
      this.generator.accept(graphics);
   }

   public Raster getData(Rectangle rect) {
      if (rect.height > this.scanBuffer.getHeight()) {
         return null;
      } else {
         if (rect.y >= this.bufY + this.scanBuffer.getHeight() || rect.y + rect.height <= this.bufY) {
            this.bufY = rect.y;
            this.redrawToBuffer();
         }

         this.rowListener.accept(rect.y - this.bufY);
         Raster r = this.scanBuffer.getData(new Rectangle(rect.x, rect.y - this.bufY, rect.width, rect.height));
         r = r.createTranslatedChild(r.getMinX(), r.getMinY() + this.bufY);
         return r;
      }
   }

   public Vector<RenderedImage> getSources() {
      return null;
   }

   public Object getProperty(String name) {
      return this.scanBuffer.getProperty(name);
   }

   public String[] getPropertyNames() {
      return this.scanBuffer.getPropertyNames();
   }

   public ColorModel getColorModel() {
      return this.scanBuffer.getColorModel();
   }

   public SampleModel getSampleModel() {
      return this.scanBuffer.getSampleModel();
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   public int getMinX() {
      return 0;
   }

   public int getMinY() {
      return 0;
   }

   public int getNumXTiles() {
      return 1;
   }

   public int getNumYTiles() {
      return (int)Math.ceil((double)((float)this.height / (float)this.scanBuffer.getHeight()));
   }

   public int getMinTileX() {
      return 0;
   }

   public int getMinTileY() {
      return 0;
   }

   public int getTileWidth() {
      return this.width;
   }

   public int getTileHeight() {
      return this.height;
   }

   public int getTileGridXOffset() {
      return 0;
   }

   public int getTileGridYOffset() {
      return 0;
   }

   public Raster getTile(int tileX, int tileY) {
      return null;
   }

   public Raster getData() {
      return null;
   }

   public WritableRaster copyData(WritableRaster raster) {
      return null;
   }
}
