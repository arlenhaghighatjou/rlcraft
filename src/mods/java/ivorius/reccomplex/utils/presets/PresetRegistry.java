package ivorius.reccomplex.utils.presets;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import ivorius.reccomplex.files.SimpleLeveledRegistry;
import ivorius.reccomplex.files.loading.FileLoaderRegistryString;
import ivorius.reccomplex.files.saving.FileSaverString;
import java.lang.reflect.Type;
import java.text.ParseException;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.function.Function;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;

public abstract class PresetRegistry<T> {
   protected final SimpleLeveledRegistry<PresetRegistry.Preset<T>> registry;
   protected Gson gson;
   @Nullable
   protected String defaultID;
   protected String fileSuffix;

   public PresetRegistry(String fileSuffix, String description) {
      this.fileSuffix = fileSuffix;
      GsonBuilder builder = (new GsonBuilder()).setPrettyPrinting();
      this.registerGson(builder);
      this.gson = builder.create();
      this.registry = new SimpleLeveledRegistry<PresetRegistry.Preset<T>>(description);
   }

   @Nonnull
   public static <T> PresetRegistry.Preset<T> fullPreset(@Nonnull String id, @Nonnull T t, @Nullable PresetRegistry.Metadata metadata) {
      return new PresetRegistry.Preset<T>(t, metadata != null ? metadata : new PresetRegistry.Metadata(id, new String[0]));
   }

   public String getFileSuffix() {
      return this.fileSuffix;
   }

   public SimpleLeveledRegistry<PresetRegistry.Preset<T>> getRegistry() {
      return this.registry;
   }

   public void setDefault(@Nonnull String type) {
      this.defaultID = type;
   }

   @Nullable
   public String defaultID() {
      return this.defaultID;
   }

   @Nonnull
   public Optional<T> preset(String id) {
      return this.originalPreset(id).map(this::copy);
   }

   @Nonnull
   public Optional<T> originalPreset(String id) {
      return Optional.ofNullable(this.registry.get(id)).map((p) -> {
         return p.t;
      });
   }

   public PresetRegistry.Metadata copy(PresetRegistry.Metadata meta) {
      return (PresetRegistry.Metadata)this.gson.fromJson(this.gson.toJsonTree(meta), PresetRegistry.Metadata.class);
   }

   public T copy(T t) {
      return (T)this.gson.fromJson(this.gson.toJsonTree(t), this.getType());
   }

   @Nonnull
   public Optional<PresetRegistry.Preset<T>> fullPreset(String id) {
      return this.metadata(id).flatMap((m) -> {
         return this.preset(id).map((p) -> {
            return new PresetRegistry.Preset<PresetRegistry.Preset<T>>(p, m);
         });
      });
   }

   @Nonnull
   public Optional<String> title(String id) {
      return this.metadata(id).map((m) -> {
         return m.title;
      });
   }

   @Nonnull
   public Optional<List<String>> description(String id) {
      return this.metadata(id).map((d) -> {
         return d.description;
      }).map(Lists::newArrayList);
   }

   @Nonnull
   public Optional<PresetRegistry.Metadata> metadata(String id) {
      return Optional.ofNullable(this.registry.get(id)).map((p) -> {
         return this.copy(p.metadata);
      });
   }

   public Collection<String> allIDs() {
      return this.registry.ids();
   }

   public boolean has(String id) {
      return this.registry.has(id);
   }

   protected abstract void registerGson(GsonBuilder var1);

   protected abstract Type getType();

   public FileLoaderRegistryString loader() {
      return new FileLoaderRegistryString(this.fileSuffix, this.registry, this::read);
   }

   public FileSaverString<PresetRegistry.Preset<T>> saver(String id) {
      return new FileSaverString<PresetRegistry.Preset<T>>(id, this.fileSuffix, this.registry, this::write);
   }

   public PresetRegistry.Preset<T> read(String file) throws Exception {
      JsonParser parser = new JsonParser();
      JsonObject jsonObject = parser.parse(file).getAsJsonObject();
      T t = (T)this.gson.fromJson(jsonObject.get("data"), this.getType());
      PresetRegistry.Metadata metadata = (PresetRegistry.Metadata)this.gson.fromJson(jsonObject.get("metadata"), PresetRegistry.Metadata.class);
      if (t != null && metadata != null) {
         return new PresetRegistry.Preset<T>(t, metadata);
      } else {
         throw new ParseException("Error parsing preset", 0);
      }
   }

   public String write(PresetRegistry.Preset p) throws Exception {
      JsonObject object = new JsonObject();
      object.add("data", this.gson.toJsonTree(p.t));
      object.add("metadata", this.gson.toJsonTree(p.metadata));
      return this.gson.toJson(object);
   }

   public static class Metadata {
      public String title = "";
      public String[] description;

      public Metadata(String title, String[] description) {
         this.title = title;
         this.description = description;
      }

      public Metadata(String title, String description) {
         this.title = title;
         this.description = new String[]{description};
      }
   }

   protected static class Preset<T> {
      public T t;
      public PresetRegistry.Metadata metadata;

      public Preset(T t, PresetRegistry.Metadata metadata) {
         this.t = t;
         this.metadata = metadata;
      }
   }
}
