package betterquesting.api2.supporter;

import betterquesting.api.api.QuestingAPI;
import betterquesting.api.utils.JsonHelper;
import betterquesting.client.themes.ResourceTheme;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import java.awt.image.BufferedImage;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Base64;
import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.UUID;
import java.util.concurrent.Callable;
import java.util.function.Predicate;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.Tuple;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class SupporterAPI {
   private static Gson GSON = (new GsonBuilder()).create();
   private static final String charSet = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuv0123456789";
   private static final Random rand = new Random();

   public SupporterAPI() {
   }

   @Nullable
   public static JsonObject readManifest(File loc) {
      try {
         DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(loc)));
         Throwable var2 = null;

         JsonObject var3;
         try {
            var3 = (JsonObject)GSON.fromJson(new String(Base64.getDecoder().decode(dis.readUTF())), JsonObject.class);
         } catch (Throwable var13) {
            var2 = var13;
            throw var13;
         } finally {
            if (dis != null) {
               if (var2 != null) {
                  try {
                     dis.close();
                  } catch (Throwable var12) {
                     var2.addSuppressed(var12);
                  }
               } else {
                  dis.close();
               }
            }

         }

         return var3;
      } catch (Exception var15) {
         return null;
      }
   }

   public static ResourceTheme readCompressedFile(File loc) {
      try {
         DataInputStream dis = new DataInputStream(new GZIPInputStream(new FileInputStream(loc)));
         Throwable var2 = null;

         ResourceTheme var30;
         try {
            JsonObject manifest = (JsonObject)GSON.fromJson(new String(Base64.getDecoder().decode(dis.readUTF()), StandardCharsets.UTF_8), JsonObject.class);
            int format = JsonHelper.GetNumber(manifest, "format", Integer.valueOf(0)).intValue();
            ResourceLocation parID = manifest.has("parentID") ? new ResourceLocation(JsonHelper.GetString(manifest, "parentID", "minecraft:null")) : null;
            ResourceLocation thmID = new ResourceLocation(JsonHelper.GetString(manifest, "themeID", "betterquesting:untitled"));
            String thmName = JsonHelper.GetString(manifest, "themeName", "Untitled Theme");
            ResourceTheme theme = new ResourceTheme(parID, thmID, thmName);
            byte[] fileKey = readFileKey(dis, format);
            JsonObject themeJson = (JsonObject)GSON.fromJson(decode(dis.readUTF(), fileKey), JsonObject.class);
            int numTex = dis.readInt();

            for(int n = 0; n < numTex; ++n) {
               ResourceLocation resID = new ResourceLocation(decode(dis.readUTF(), fileKey));
               int w = dis.readInt();
               int h = dis.readInt();
               int[] rgb = new int[w * h];

               for(int i = 0; i < rgb.length; ++i) {
                  int flip = fileKey[i * 4 % fileKey.length];
                  flip = flip | fileKey[(i * 4 + 1) % fileKey.length] << 8;
                  flip = flip | fileKey[(i * 4 + 2) % fileKey.length] << 16;
                  flip = flip | fileKey[(i * 4 + 3) % fileKey.length] << 24;
                  rgb[i] = dis.readInt() ^ flip;
               }

               RgbTexture texture = new RgbTexture(w, h, rgb);
               Minecraft.getMinecraft().addScheduledTask(() -> {
                  return Minecraft.getMinecraft().getTextureManager().loadTexture(resID, texture);
               });
            }

            theme.loadFromJson(themeJson);
            var30 = theme;
         } catch (Throwable var27) {
            var2 = var27;
            throw var27;
         } finally {
            if (dis != null) {
               if (var2 != null) {
                  try {
                     dis.close();
                  } catch (Throwable var26) {
                     var2.addSuppressed(var26);
                  }
               } else {
                  dis.close();
               }
            }

         }

         return var30;
      } catch (Exception var29) {
         return null;
      }
   }

   public static void buildCompressedFile(File fileOut, JsonObject jsonDetails, JsonObject jsonTheme, Collection<Tuple<ResourceLocation, File>> textures, String token, String service, int tier) {
      if (fileOut.exists()) {
         try {
            fileOut.delete();
         } catch (Exception var26) {
            ;
         }
      }

      try {
         DataOutputStream dos = new DataOutputStream(new GZIPOutputStream(new FileOutputStream(fileOut)));
         Throwable var8 = null;

         try {
            int format = JsonHelper.GetNumber(jsonDetails, "format", Integer.valueOf(0)).intValue();
            dos.writeUTF(Base64.getEncoder().encodeToString(GSON.toJson(jsonDetails).getBytes(StandardCharsets.UTF_8)));
            byte[] key = format == 0 ? makeFormat_0(dos, rand.nextLong()) : makeFormat_1(dos, token, 3 + rand.nextInt(6), service, tier);
            dos.writeUTF(encode(GSON.toJson(jsonTheme), key));
            dos.writeInt(textures.size());

            for(Tuple<ResourceLocation, File> t : textures) {
               dos.writeUTF(encode(((ResourceLocation)t.getFirst()).toString(), key));
               BufferedImage bufImg = ImageIO.read((File)t.getSecond());
               int[] rgb = bufImg.getRGB(0, 0, bufImg.getWidth(), bufImg.getHeight(), (int[])null, 0, bufImg.getWidth());
               dos.writeInt(bufImg.getWidth());
               dos.writeInt(bufImg.getHeight());

               for(int i = 0; i < rgb.length; ++i) {
                  int flip = key[i * 4 % key.length];
                  flip = flip | key[(i * 4 + 1) % key.length] << 8;
                  flip = flip | key[(i * 4 + 2) % key.length] << 16;
                  flip = flip | key[(i * 4 + 3) % key.length] << 24;
                  dos.writeInt(rgb[i] ^ flip);
               }
            }
         } catch (Throwable var27) {
            var8 = var27;
            throw var27;
         } finally {
            if (dos != null) {
               if (var8 != null) {
                  try {
                     dos.close();
                  } catch (Throwable var25) {
                     var8.addSuppressed(var25);
                  }
               } else {
                  dos.close();
               }
            }

         }
      } catch (Exception var29) {
         var29.printStackTrace();
      }

   }

   private static String encode(String s, byte[] key) {
      return Base64.getEncoder().encodeToString(flipBytes(s.getBytes(StandardCharsets.UTF_8), key));
   }

   private static String decode(String s, byte[] key) {
      return new String(flipBytes(Base64.getDecoder().decode(s), key), StandardCharsets.UTF_8);
   }

   private static byte[] flipBytes(@Nonnull byte[] input, @Nonnull byte[] key) {
      byte[] output = new byte[input.length];

      for(int i = 0; i < input.length; ++i) {
         output[i] = (byte)(input[i] ^ key[i % key.length]);
      }

      return output;
   }

   private static byte[] makeFormat_0(@Nonnull DataOutputStream dos, long seed) throws IOException {
      byte[] b = new byte[16];
      (new Random(seed)).nextBytes(b);
      dos.writeLong(seed);
      return b;
   }

   private static byte[] makeFormat_1(@Nonnull DataOutputStream dos, @Nonnull String token, int salts, String service, int threshold) throws IOException {
      List<Tuple<Boolean, String>> list = new ArrayList();
      list.add(new Tuple(true, token));

      for(int i = 0; i < salts; ++i) {
         StringBuilder sb = new StringBuilder();

         for(int j = rand.nextInt(9) + 16; j >= 0; --j) {
            sb.append("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuv0123456789".charAt(rand.nextInt("ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuv0123456789".length())));
         }

         list.add(new Tuple(false, sb.toString()));
      }

      Collections.shuffle(list, rand);
      return makeFormat_1(dos, list, service, threshold);
   }

   private static byte[] makeFormat_1(@Nonnull DataOutputStream dos, @Nonnull Collection<Tuple<Boolean, String>> tokens, String service, int threshold) throws IOException {
      dos.writeInt(tokens.size());
      int s = 0;
      Set<byte[]> l = new HashSet();

      for(Tuple<Boolean, String> t : tokens) {
         byte[] b = ((Boolean)t.getFirst()).booleanValue() ? ((String)t.getSecond()).getBytes(StandardCharsets.UTF_8) : new byte[16];
         if (!((Boolean)t.getFirst()).booleanValue()) {
            (new Random((long)((String)t.getSecond()).hashCode())).nextBytes(b);
         }

         l.add(b);
         if (s < b.length) {
            s = b.length;
         }

         dos.writeUTF(Base64.getEncoder().encodeToString(((String)t.getSecond()).getBytes(StandardCharsets.UTF_8)));
      }

      dos.writeUTF(Base64.getEncoder().encodeToString(service.getBytes(StandardCharsets.UTF_8)));
      dos.writeInt(threshold);
      byte[] k = new byte[s];

      for(int i = 0; i < s; ++i) {
         for(byte[] e : l) {
            k[i] ^= e[i % e.length];
         }
      }

      return k;
   }

   @SideOnly(Side.CLIENT)
   private static byte[] readFileKey(@Nonnull DataInputStream dis, int format) {
      switch(format) {
      case -1:
         return new byte[]{127};
      case 0:
         return readFormat_0(dis);
      case 1:
         return readFormat_1(dis);
      default:
         return new byte[]{127};
      }
   }

   private static byte[] readFormat_0(@Nonnull DataInputStream dis) {
      try {
         byte[] b = new byte[16];
         long seed = dis.readLong();
         (new Random(seed)).nextBytes(b);
         return b;
      } catch (Exception var4) {
         return new byte[]{127};
      }
   }

   @SideOnly(Side.CLIENT)
   private static byte[] readFormat_1(@Nonnull DataInputStream dis) {
      try {
         String[] tokens = new String[dis.readInt()];

         for(int n = 0; n < tokens.length; ++n) {
            tokens[n] = new String(Base64.getDecoder().decode(dis.readUTF()));
         }

         String service = new String(Base64.getDecoder().decode(dis.readUTF()));
         int threshold = dis.readInt();
         UUID playerID = QuestingAPI.getQuestingUUID(Minecraft.getMinecraft().player);
         SupporterEntry entry = SupporterDB.INSTANCE.getValue(playerID);
         int m = 0;
         Set<byte[]> encoded = new HashSet();

         for(String k : tokens) {
            boolean c = entry != null && entry.getServices(k).entrySet().stream().anyMatch((v) -> {
               return ((String)v.getKey()).equals(service) && ((Integer)v.getValue()).intValue() >= threshold;
            });
            byte[] b = c ? k.getBytes(StandardCharsets.UTF_8) : new byte[16];
            if (c) {
               (new Random((long)k.hashCode())).nextBytes(b);
            }

            encoded.add(b);
            if (b.length > m) {
               m = b.length;
            }
         }

         byte[] merged = new byte[m];

         for(int i = 0; i < m; ++i) {
            for(byte[] e : encoded) {
               merged[i] ^= e[i % e.length];
            }
         }

         return merged;
      } catch (Exception var14) {
         return new byte[]{127};
      }
   }
}
