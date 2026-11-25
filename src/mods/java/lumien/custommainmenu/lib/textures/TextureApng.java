package lumien.custommainmenu.lib.textures;

import com.google.common.io.ByteStreams;
import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.zip.CRC32;
import lumien.custommainmenu.CustomMainMenu;
import net.lax1dude.eaglercraft.v1_8.EagRuntime;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.lax1dude.eaglercraft.v1_8.opengl.ImageData;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.texture.TextureUtil;
import net.minecraft.client.resources.IResource;
import net.minecraft.util.ResourceLocation;

public class TextureApng implements ITexture {
   private static final byte[] PNG = {(byte)137, 80, 78, 71, 13, 10, 26, 10};
   private static final int IHDR = 0x49484452;
   private static final int PLTE = 0x504C5445;
   private static final int TRNS = 0x74524E53;
   private static final int ACTL = 0x6163544C;
   private static final int FCTL = 0x6663544C;
   private static final int FDAT = 0x66644154;
   private static final int IDAT = 0x49444154;
   private static final int IEND = 0x49454E44;
   private final ResourceLocation location;
   private final List<Frame> frames = new ArrayList<>();
   private boolean loaded;
   private long started;
   private long cycleDuration;
   private long playCount;
   private int texture = -1;
   private int uploadedFrame = -1;
   private int width = 16;
   private int height = 16;

   public TextureApng(String path) {
      this.location = new ResourceLocation(path);
   }

   @Override
   public void bind() {
      if (!this.loaded) {
         this.loaded = true;
         try (IResource resource = Minecraft.getMinecraft().getResourceManager().getResource(this.location)) {
            this.load(ByteStreams.toByteArray(resource.getInputStream()));
            this.started = EagRuntime.steadyTimeMillis();
         } catch (IOException | RuntimeException ex) {
            this.frames.clear();
            CustomMainMenu.INSTANCE.logger.error("Could not load animated texture {}", this.location, ex);
         }
      }
      if (this.frames.isEmpty()) {
         GlStateManager.bindTexture(TextureUtil.missingTexture.getGlTextureId());
         return;
      }

      long elapsed = EagRuntime.steadyTimeMillis() - this.started;
      long age = this.playCount != 0 && elapsed / this.cycleDuration >= this.playCount
         ? this.cycleDuration - 1 : elapsed % this.cycleDuration;
      int index = this.findFrame(age);
      if (this.texture == -1) {
         this.texture = TextureUtil.uploadTextureImageAllocate(TextureUtil.glGenTextures(),
            new ImageData(this.width, this.height, this.frames.get(index).pixels, true), false, false);
         this.uploadedFrame = index;
      } else if (this.uploadedFrame != index) {
         TextureUtil.uploadTexture(this.texture, this.frames.get(index).pixels, this.width, this.height);
         this.uploadedFrame = index;
      }
      GlStateManager.bindTexture(this.texture);
   }

   private int findFrame(long age) {
      int low = 0;
      int high = this.frames.size() - 1;
      while (low < high) {
         int mid = (low + high) >>> 1;
         if (age < this.frames.get(mid).endTime) {
            high = mid;
         } else {
            low = mid + 1;
         }
      }
      return low;
   }

   private void load(byte[] bytes) throws IOException {
      if (bytes.length < PNG.length || !Arrays.equals(PNG, Arrays.copyOf(bytes, PNG.length))) {
         throw new IOException("Invalid PNG signature");
      }
      byte[] header = null;
      int[] canvas = null;
      List<byte[]> shared = new ArrayList<>();
      FrameControl frame = null;
      ByteArrayOutputStream frameData = new ByteArrayOutputStream();
      int frameCount = 0;
      boolean ended = false;
      for (int offset = PNG.length; offset <= bytes.length - 12;) {
         int length = readInt(bytes, offset);
         int type = readInt(bytes, offset + 4);
         int data = offset + 8;
         if (length < 0 || length > bytes.length - data - 4) {
            throw new IOException("Invalid PNG chunk length");
         }
         if (type == IHDR) {
            if (length != 13 || header != null) {
               throw new IOException("Invalid PNG header");
            }
            header = Arrays.copyOfRange(bytes, data, data + length);
            this.width = readInt(header, 0);
            this.height = readInt(header, 4);
            if (this.width <= 0 || this.height <= 0 || (long)this.width * this.height > 16_777_216L) {
               throw new IOException("Unsupported animated texture size");
            }
            canvas = new int[this.width * this.height];
         } else if (type == PLTE || type == TRNS) {
            shared.add(Arrays.copyOfRange(bytes, offset, data + length + 4));
         } else if (type == ACTL && length == 8) {
            frameCount = readInt(bytes, data);
            this.playCount = Integer.toUnsignedLong(readInt(bytes, data + 4));
            if (canvas == null || frameCount <= 0 || (long)canvas.length * frameCount > 33_554_432L) {
               throw new IOException("Unsupported animated texture frame count");
            }
         } else if (type == FCTL || type == IEND) {
            if (frame != null) {
               this.appendFrame(header, shared, canvas, frame, frameData.toByteArray());
               frameData.reset();
            }
            if (type == IEND) {
               ended = true;
               break;
            }
            if (length != 26 || frameCount == 0 || this.frames.size() >= frameCount) {
               throw new IOException("Invalid animated frame control");
            }
            frame = new FrameControl(bytes, data);
         } else if (type == IDAT && frame != null) {
            frameData.write(bytes, data, length);
         } else if (type == FDAT && frame != null && length > 4) {
            frameData.write(bytes, data + 4, length - 4);
         }
         offset = data + length + 4;
      }
      if (!ended || this.frames.size() != frameCount) {
         throw new IOException("Incomplete animated PNG");
      }
      if (this.frames.isEmpty()) {
         ImageData image = ImageData.loadImageFile(bytes, "image/png");
         if (image == null) {
            throw new IOException("Could not decode PNG");
         }
         this.width = image.width;
         this.height = image.height;
         this.cycleDuration = 1;
         this.frames.add(new Frame(image.pixels, 1));
      }
   }

   private void appendFrame(byte[] header, List<byte[]> shared, int[] canvas, FrameControl frame, byte[] data) throws IOException {
      if (frame.width <= 0 || frame.height <= 0 || frame.x < 0 || frame.y < 0
          || frame.width > this.width - frame.x || frame.height > this.height - frame.y
          || frame.disposal > 2 || frame.blend > 1 || data.length == 0) {
         throw new IOException("Invalid animated frame bounds or operation");
      }

      ByteArrayOutputStream png = new ByteArrayOutputStream();
      DataOutputStream out = new DataOutputStream(png);
      out.write(PNG);
      byte[] resized = header.clone();
      writeInt(resized, 0, frame.width);
      writeInt(resized, 4, frame.height);
      writeChunk(out, IHDR, resized);
      for (byte[] chunk : shared) {
         out.write(chunk);
      }
      writeChunk(out, IDAT, data);
      writeChunk(out, IEND, new byte[0]);
      ImageData image = ImageData.loadImageFile(png.toByteArray(), "image/png");
      if (image == null || image.width != frame.width || image.height != frame.height) {
         throw new IOException("Could not decode animated PNG frame");
      }

      int[] output = frame.disposal == 2 ? canvas.clone() : canvas;
      for (int y = 0; y < frame.height; ++y) {
         int from = y * frame.width;
         int to = (frame.y + y) * this.width + frame.x;
         if (frame.blend == 0) {
            System.arraycopy(image.pixels, from, output, to, frame.width);
         } else {
            for (int x = 0; x < frame.width; ++x) {
               output[to + x] = blend(image.pixels[from + x], output[to + x]);
            }
         }
      }
      this.cycleDuration += frame.delay;
      this.frames.add(new Frame(output == canvas ? output.clone() : output, this.cycleDuration));
      if (frame.disposal == 1) {
         for (int y = 0; y < frame.height; ++y) {
            int start = (frame.y + y) * this.width + frame.x;
            Arrays.fill(canvas, start, start + frame.width, 0);
         }
      }
   }

   private static int blend(int src, int dst) {
      int sa = src >>> 24;
      int da = dst >>> 24;
      if (sa == 255 || da == 0) {
         return src;
      }
      if (sa == 0) {
         return dst;
      }
      int srcWeight = sa * 255;
      int dstWeight = da * (255 - sa);
      int alpha = srcWeight + dstWeight;
      int result = ((alpha + 127) / 255) << 24;
      for (int bit = 0; bit <= 16; bit += 8) {
         int channel = (((src >>> bit) & 255) * srcWeight + ((dst >>> bit) & 255) * dstWeight + alpha / 2) / alpha;
         result |= channel << bit;
      }
      return result;
   }

   private static int readInt(byte[] bytes, int offset) {
      return (bytes[offset] & 255) << 24 | (bytes[offset + 1] & 255) << 16
         | (bytes[offset + 2] & 255) << 8 | bytes[offset + 3] & 255;
   }

   private static void writeInt(byte[] bytes, int offset, int value) {
      bytes[offset] = (byte)(value >>> 24);
      bytes[offset + 1] = (byte)(value >>> 16);
      bytes[offset + 2] = (byte)(value >>> 8);
      bytes[offset + 3] = (byte)value;
   }

   private static void writeChunk(DataOutputStream out, int type, byte[] data) throws IOException {
      CRC32 crc = new CRC32();
      crc.update(type >>> 24);
      crc.update(type >>> 16);
      crc.update(type >>> 8);
      crc.update(type);
      crc.update(data, 0, data.length);
      out.writeInt(data.length);
      out.writeInt(type);
      out.write(data);
      out.writeInt((int)crc.getValue());
   }

   public int getWidth() {
      return this.width;
   }

   public int getHeight() {
      return this.height;
   }

   private static final class Frame {
      private final int[] pixels;
      private final long endTime;

      private Frame(int[] pixels, long endTime) {
         this.pixels = pixels;
         this.endTime = endTime;
      }
   }

   private static final class FrameControl {
      private final int width;
      private final int height;
      private final int x;
      private final int y;
      private final int delay;
      private final int disposal;
      private final int blend;

      private FrameControl(byte[] data, int offset) {
         this.width = readInt(data, offset + 4);
         this.height = readInt(data, offset + 8);
         this.x = readInt(data, offset + 12);
         this.y = readInt(data, offset + 16);
         int numerator = (data[offset + 20] & 255) << 8 | data[offset + 21] & 255;
         int denominator = (data[offset + 22] & 255) << 8 | data[offset + 23] & 255;
         this.delay = Math.max(1, numerator * 1000 / (denominator == 0 ? 100 : denominator));
         this.disposal = data[offset + 24] & 255;
         this.blend = data[offset + 25] & 255;
      }
   }
}
