package com.bloodnbonesgaming.lib.util;

import com.bloodnbonesgaming.lib.BNBGamingLib;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.lang.reflect.Type;

public class JsonHelper {
   private static final JsonParser parser = new JsonParser();
   private static final Gson gson = (new GsonBuilder()).setPrettyPrinting().disableHtmlEscaping().create();

   public JsonHelper() {
   }

   public static JsonElement readJson(File file) {
      synchronized(parser) {
         JsonElement var4;
         try {
            BufferedReader reader = FileHelper.openReader(file);
            Throwable var3 = null;

            try {
               var4 = parser.parse(reader);
            } catch (Throwable var16) {
               var3 = var16;
               throw var16;
            } finally {
               if (reader != null) {
                  if (var3 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var15) {
                        var3.addSuppressed(var15);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (Exception var18) {
            BNBGamingLib.libInstance.getLog().error("Failed to read Json file " + file.getName());
            var18.printStackTrace();
            return null;
         }

         return var4;
      }
   }

   public static boolean writeJson(JsonElement e, File file) {
      return writeJson(e, file, gson);
   }

   public static boolean writeJson(JsonElement e, File file, Gson gson) {
      synchronized(gson) {
         boolean var6;
         try {
            BufferedWriter writer = FileHelper.openWriter(file);
            Throwable var5 = null;

            try {
               writer.write(gson.toJson(e));
               var6 = true;
            } catch (Throwable var18) {
               var5 = var18;
               throw var18;
            } finally {
               if (writer != null) {
                  if (var5 != null) {
                     try {
                        writer.close();
                     } catch (Throwable var17) {
                        var5.addSuppressed(var17);
                     }
                  } else {
                     writer.close();
                  }
               }

            }
         } catch (Exception var20) {
            BNBGamingLib.libInstance.getLog().error("Failed to write Json file " + file.getName());
            var20.printStackTrace();
            return false;
         }

         return var6;
      }
   }

   public static JsonElement toJsonTree(Object obj) {
      return toJsonTree(obj, gson);
   }

   public static JsonElement toJsonTree(Object obj, Gson gson) {
      return gson.toJsonTree(obj);
   }

   public static boolean writeGson(Object obj, File file) {
      return writeGson(obj, file, gson);
   }

   public static boolean writeGson(Object obj, File file, Gson gson) {
      synchronized(gson) {
         boolean var6;
         try {
            BufferedWriter writer = FileHelper.openWriter(file);
            Throwable var5 = null;

            try {
               writer.write(gson.toJson(obj));
               var6 = true;
            } catch (Throwable var18) {
               var5 = var18;
               throw var18;
            } finally {
               if (writer != null) {
                  if (var5 != null) {
                     try {
                        writer.close();
                     } catch (Throwable var17) {
                        var5.addSuppressed(var17);
                     }
                  } else {
                     writer.close();
                  }
               }

            }
         } catch (Exception var20) {
            BNBGamingLib.libInstance.getLog().error("Failed to write Json file " + file.getName());
            var20.printStackTrace();
            return false;
         }

         return var6;
      }
   }

   public static <T> T readGson(Class<T> clazz, File file) {
      return (T)readGson(clazz, file, gson, false);
   }

   public static <T> T readGson(Class<T> clazz, File file, Gson gson) {
      return (T)readGson(clazz, file, gson, false);
   }

   public static <T> T readGson(Class<T> clazz, File file, boolean printStackTrace) {
      return (T)readGson(clazz, file, gson, printStackTrace);
   }

   public static <T> T readGson(Class<T> clazz, File file, Gson gson, boolean printStackTrace) {
      synchronized(gson) {
         Object var7;
         try {
            BufferedReader reader = FileHelper.openReader(file);
            Throwable var6 = null;

            try {
               var7 = gson.fromJson(reader, clazz);
            } catch (Throwable var19) {
               var6 = var19;
               throw var19;
            } finally {
               if (reader != null) {
                  if (var6 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var18) {
                        var6.addSuppressed(var18);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (Exception var21) {
            BNBGamingLib.libInstance.getLog().error("Failed to read Json from file " + file.getName());
            if (printStackTrace) {
               var21.printStackTrace();
            }

            return (T)null;
         }

         return (T)var7;
      }
   }

   public static <T> T readGson(Type type, File file) {
      return (T)readGson(type, file, gson, false);
   }

   public static <T> T readGson(Type type, File file, boolean printStackTrace) {
      return (T)readGson(type, file, gson, printStackTrace);
   }

   public static <T> T readGson(Type type, File file, Gson gson) {
      return (T)readGson(type, file, gson, false);
   }

   public static <T> T readGson(Type type, File file, Gson gson, boolean printStackTrace) {
      synchronized(gson) {
         Object var7;
         try {
            BufferedReader reader = FileHelper.openReader(file);
            Throwable var6 = null;

            try {
               var7 = gson.fromJson(reader, type);
            } catch (Throwable var19) {
               var6 = var19;
               throw var19;
            } finally {
               if (reader != null) {
                  if (var6 != null) {
                     try {
                        reader.close();
                     } catch (Throwable var18) {
                        var6.addSuppressed(var18);
                     }
                  } else {
                     reader.close();
                  }
               }

            }
         } catch (Exception var21) {
            BNBGamingLib.libInstance.getLog().error("Failed to read Json from file " + file.getName());
            if (printStackTrace) {
               var21.printStackTrace();
            }

            return (T)null;
         }

         return (T)var7;
      }
   }
}
