package org.orecruncher.dsurround.registry.config;

import com.google.gson.Gson;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.MalformedJsonException;
import it.unimi.dsi.fastutil.io.FastByteArrayInputStream;
import it.unimi.dsi.fastutil.io.FastByteArrayOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;
import java.util.zip.GZIPInputStream;
import java.util.zip.GZIPOutputStream;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.ModContainer;
import org.apache.commons.lang3.StringUtils;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.dsurround.registry.config.packs.IMyResourcePack;
import org.orecruncher.dsurround.registry.config.packs.ResourcePacks;

public final class ConfigData implements Iterable<ModConfiguration> {
   private final byte[] crunchyBits;

   private ConfigData(@Nonnull byte[] theBits) {
      this.crunchyBits = theBits;
   }

   public Stream<ModConfiguration> stream() {
      return StreamSupport.stream(this.spliterator(), false);
   }

   private static void injectString(@Nonnull String txt, @Nonnull OutputStreamWriter out) {
      try {
         out.write(34);
         out.write(txt);
         out.write("\",");
      } catch (Throwable var3) {
         ModBase.log().error("Really??", var3);
      }

   }

   protected static void appendComma(@Nonnull OutputStreamWriter out) {
      try {
         out.write(44);
      } catch (Throwable var2) {
         ModBase.log().error("Really??", var2);
      }

   }

   protected static void copy(@Nonnull BufferedReader reader, @Nonnull OutputStreamWriter out) {
      while(true) {
         try {
            String line;
            if ((line = reader.readLine()) != null) {
               String s = line.trim();
               if (!StringUtils.isEmpty(s)) {
                  out.write(s);
               }
               continue;
            }
         } catch (IOException var4) {
            ModBase.log().error("Huh?", var4);
         }

         return;
      }
   }

   protected static boolean copy(@Nonnull InputStreamReader in, @Nonnull OutputStreamWriter out, @Nonnull String text, boolean comma) {
      try {
         BufferedReader reader = new BufferedReader(in);
         Throwable var5 = null;

         boolean r;
         try {
            try {
               reader.mark(131072);
               new JsonReader(reader);
               ModConfiguration var10000 = (ModConfiguration)(new Gson()).fromJson(reader, ModConfiguration.class);
            } finally {
               reader.reset();
            }

            if (comma) {
               appendComma(out);
            }

            injectString(text, out);
            copy(reader, out);
            ModBase.log().debug("Loaded %s", new Object[]{text});
            r = true;
         } catch (Throwable var26) {
            var5 = var26;
            throw var26;
         } finally {
            if (reader != null) {
               if (var5 != null) {
                  try {
                     reader.close();
                  } catch (Throwable var24) {
                     var5.addSuppressed(var24);
                  }
               } else {
                  reader.close();
               }
            }

         }

         return r;
      } catch (MalformedJsonException | JsonSyntaxException var28) {
         ModBase.log().warn("Json validation failed for %s: %s", new Object[]{text, var28.getMessage()});
      } catch (Throwable var29) {
         ModBase.log().error(text, var29);
      }

      return comma;
   }

   protected static boolean copy(@Nonnull IMyResourcePack p, @Nonnull ResourceLocation rl, @Nonnull OutputStreamWriter output, @Nullable String text, boolean comma) {
      try {
         InputStream is = p.getInputStream(rl);
         Throwable var6 = null;

         Throwable var9;
         try {
            if (is == null) {
               return comma;
            }

            try {
               InputStreamReader stream = new InputStreamReader(is);
               Throwable var8 = null;

               try {
                  var9 = copy(stream, output, text, comma);
               } catch (Throwable var37) {
                  var9 = var37;
                  var8 = var37;
                  throw var37;
               } finally {
                  if (stream != null) {
                     if (var8 != null) {
                        try {
                           stream.close();
                        } catch (Throwable var36) {
                           var8.addSuppressed(var36);
                        }
                     } else {
                        stream.close();
                     }
                  }

               }
            } catch (Throwable var39) {
               ModBase.log().error(rl.toString(), var39);
               return comma;
            }
         } catch (Throwable var40) {
            var6 = var40;
            throw var40;
         } finally {
            if (is != null) {
               if (var6 != null) {
                  try {
                     is.close();
                  } catch (Throwable var35) {
                     var6.addSuppressed(var35);
                  }
               } else {
                  is.close();
               }
            }

         }

         return (boolean)var9;
      } catch (Throwable var42) {
         ModBase.log().error(rl.toString(), var42);
         return comma;
      }
   }

   @Nonnull
   private static File getFileReference(@Nonnull String dataFile) {
      String workingFile = StringUtils.appendIfMissing(Paths.get(dataFile).getFileName().toString(), ".json", new CharSequence[0]);
      return new File(ModBase.dataDirectory(), workingFile);
   }

   public static ConfigData load() {
      FastByteArrayOutputStream bits = new FastByteArrayOutputStream(16384);

      try {
         OutputStreamWriter output = new OutputStreamWriter(new GZIPOutputStream(bits));
         Throwable var2 = null;

         try {
            output.write("[");
            boolean prependComma = false;
            List<IMyResourcePack> packs = ResourcePacks.findResourcePacks();

            for(ModContainer mod : Loader.instance().getActiveModList()) {
               ResourceLocation rl = new ResourceLocation("dsurround", "data/" + mod.getModId().toLowerCase() + ".json");

               for(IMyResourcePack p : packs) {
                  if (p.resourceExists(rl)) {
                     prependComma = copy(p, rl, output, "[" + rl.toString() + "] from [" + p.getModName() + "]", prependComma);
                  }
               }
            }

            ResourceLocation rl = ResourcePacks.CONFIGURE_RESOURCE;

            for(IMyResourcePack p : packs) {
               if (p.resourceExists(rl)) {
                  prependComma = copy(p, rl, output, "[" + rl.toString() + "] from [" + p.getModName() + "]", prependComma);
               }
            }

            for(Profiles.ProfileScript script : Profiles.getProfileStreams()) {
               try {
                  InputStreamReader reader = new InputStreamReader(script.stream);
                  Throwable cfg = null;

                  try {
                     prependComma = copy(reader, output, script.packName, prependComma);
                  } catch (Throwable var103) {
                     cfg = var103;
                     throw var103;
                  } finally {
                     if (reader != null) {
                        if (cfg != null) {
                           try {
                              reader.close();
                           } catch (Throwable var102) {
                              cfg.addSuppressed(var102);
                           }
                        } else {
                           reader.close();
                        }
                     }

                  }
               } catch (Throwable var110) {
                  ModBase.log().error("Error reading profile script", var110);
               }
            }

            for(String cfg : ModOptions.general.externalScriptFiles) {
               File file = getFileReference(cfg);
               if (file.exists()) {
                  ModBase.log().info("Loading external configuration script '%s'", new Object[]{cfg});

                  try {
                     InputStream stream = new FileInputStream(file);
                     Throwable var14 = null;

                     try {
                        try {
                           InputStreamReader input = new InputStreamReader(stream);
                           Throwable var16 = null;

                           try {
                              prependComma = copy(input, output, cfg, prependComma);
                           } catch (Throwable var101) {
                              var16 = var101;
                              throw var101;
                           } finally {
                              if (input != null) {
                                 if (var16 != null) {
                                    try {
                                       input.close();
                                    } catch (Throwable var100) {
                                       var16.addSuppressed(var100);
                                    }
                                 } else {
                                    input.close();
                                 }
                              }

                           }
                        } catch (Throwable var105) {
                           ModBase.log().error(String.format("Unable to load configuration script '%s'", cfg), var105);
                        }
                     } catch (Throwable var106) {
                        var14 = var106;
                        throw var106;
                     } finally {
                        if (stream != null) {
                           if (var14 != null) {
                              try {
                                 stream.close();
                              } catch (Throwable var99) {
                                 var14.addSuppressed(var99);
                              }
                           } else {
                              stream.close();
                           }
                        }

                     }
                  } catch (Throwable var108) {
                     ModBase.log().error(String.format("Unable to load configuration script '%s'", cfg), var108);
                  }
               }
            }

            output.write("]");
            output.flush();
         } catch (Throwable var111) {
            var2 = var111;
            throw var111;
         } finally {
            if (output != null) {
               if (var2 != null) {
                  try {
                     output.close();
                  } catch (Throwable var98) {
                     var2.addSuppressed(var98);
                  }
               } else {
                  output.close();
               }
            }

         }
      } catch (Throwable var113) {
         ModBase.log().error("Something went horribly wrong", var113);
      }

      bits.trim();
      return new ConfigData(bits.array);
   }

   @Nonnull
   public Iterator<ModConfiguration> iterator() {
      try {
         return new MCFIterator(this.crunchyBits);
      } catch (Throwable var2) {
         ModBase.log().error("Unable to create ModConfiguration iterator", var2);
         return new Iterator<ModConfiguration>() {
            public boolean hasNext() {
               return false;
            }

            public ModConfiguration next() {
               throw new IllegalStateException("Huh?");
            }
         };
      }
   }

   protected static void dump(@Nonnull byte[] demBytes) {
      try {
         BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(new GZIPInputStream(new FastByteArrayInputStream(demBytes)), StandardCharsets.UTF_8));
         Throwable var3 = null;

         try {
            String line;
            try {
               while((line = bufferedReader.readLine()) != null) {
                  ModBase.log().info(line, new Object[0]);
               }
            } catch (Throwable var13) {
               var3 = var13;
               throw var13;
            }
         } finally {
            if (bufferedReader != null) {
               if (var3 != null) {
                  try {
                     bufferedReader.close();
                  } catch (Throwable var12) {
                     var3.addSuppressed(var12);
                  }
               } else {
                  bufferedReader.close();
               }
            }

         }
      } catch (Throwable var15) {
         ModBase.log().error("Error dumping bytes!", var15);
      }

   }
}
