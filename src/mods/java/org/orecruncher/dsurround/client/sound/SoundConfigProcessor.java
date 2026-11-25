package org.orecruncher.dsurround.client.sound;

import com.google.gson.Gson;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Map;
import java.util.Map.Entry;
import javax.annotation.Nonnull;
import net.minecraft.util.ResourceLocation;
import org.orecruncher.dsurround.registry.config.SoundMetadataConfig;

public final class SoundConfigProcessor implements AutoCloseable {
   private static final ParameterizedType TYPE = new ParameterizedType() {
      public Type[] getActualTypeArguments() {
         return new Type[]{String.class, SoundMetadataConfig.class};
      }

      public Type getRawType() {
         return Map.class;
      }

      public Type getOwnerType() {
         return null;
      }
   };
   private final InputStream stream;

   public SoundConfigProcessor(@Nonnull ResourceLocation soundConfig) {
      String asset = String.format("/assets/%s/%s", soundConfig.getResourceDomain(), soundConfig.getResourcePath());
      this.stream = SoundConfigProcessor.class.getResourceAsStream(asset);
   }

   public void forEach(@Nonnull SoundConfigProcessor.ICallback callback) {
      Map<String, SoundMetadataConfig> sounds = (Map)(new Gson()).fromJson(new InputStreamReader(this.stream), TYPE);

      for(Entry<String, SoundMetadataConfig> e : sounds.entrySet()) {
         callback.onEntry((String)e.getKey(), (SoundMetadataConfig)e.getValue());
      }

   }

   public void close() throws Exception {
      if (this.stream != null) {
         this.stream.close();
      }

   }

   public interface ICallback {
      void onEntry(@Nonnull String var1, @Nonnull SoundMetadataConfig var2);
   }
}
