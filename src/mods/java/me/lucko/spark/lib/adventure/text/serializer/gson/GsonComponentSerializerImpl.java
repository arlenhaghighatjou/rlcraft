package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.function.UnaryOperator;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.BlockNBTComponent;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import me.lucko.spark.lib.adventure.util.Services;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

final class GsonComponentSerializerImpl implements GsonComponentSerializer {
   private static final Optional<GsonComponentSerializer.Provider> SERVICE = Services.<GsonComponentSerializer.Provider>service(GsonComponentSerializer.Provider.class);
   static final Consumer<GsonComponentSerializer.Builder> BUILDER = (Consumer)SERVICE.map(GsonComponentSerializer.Provider::builder).orElseGet(() -> {
      return (builder) -> {
      };
   });
   private final Gson serializer;
   private final UnaryOperator<GsonBuilder> populator;
   private final boolean downsampleColor;
   @Nullable
   private final LegacyHoverEventSerializer legacyHoverSerializer;
   private final boolean emitLegacyHover;

   GsonComponentSerializerImpl(boolean downsampleColor, @Nullable LegacyHoverEventSerializer legacyHoverSerializer, boolean emitLegacyHover) {
      this.downsampleColor = downsampleColor;
      this.legacyHoverSerializer = legacyHoverSerializer;
      this.emitLegacyHover = emitLegacyHover;
      this.populator = (builder) -> {
         builder.registerTypeHierarchyAdapter(Key.class, KeySerializer.INSTANCE);
         builder.registerTypeHierarchyAdapter(Component.class, new ComponentSerializerImpl());
         builder.registerTypeHierarchyAdapter(Style.class, new StyleSerializer(legacyHoverSerializer, emitLegacyHover));
         builder.registerTypeAdapter(ClickEvent.Action.class, IndexedSerializer.of("click action", ClickEvent.Action.NAMES));
         builder.registerTypeAdapter(HoverEvent.Action.class, IndexedSerializer.of("hover action", HoverEvent.Action.NAMES));
         builder.registerTypeAdapter(HoverEvent.ShowItem.class, new ShowItemSerializer());
         builder.registerTypeAdapter(HoverEvent.ShowEntity.class, new ShowEntitySerializer());
         builder.registerTypeAdapter(TextColorWrapper.class, new TextColorWrapper.Serializer());
         builder.registerTypeHierarchyAdapter(TextColor.class, downsampleColor ? TextColorSerializer.DOWNSAMPLE_COLOR : TextColorSerializer.INSTANCE);
         builder.registerTypeAdapter(TextDecoration.class, IndexedSerializer.of("text decoration", TextDecoration.NAMES));
         builder.registerTypeHierarchyAdapter(BlockNBTComponent.Pos.class, BlockNBTComponentPosSerializer.INSTANCE);
         return builder;
      };
      this.serializer = ((GsonBuilder)this.populator.apply(new GsonBuilder())).create();
   }

   @NotNull
   public Gson serializer() {
      return this.serializer;
   }

   @NotNull
   public UnaryOperator<GsonBuilder> populator() {
      return this.populator;
   }

   @NotNull
   public Component deserialize(@NotNull String string) {
      Component component = (Component)this.serializer().fromJson(string, Component.class);
      return component;
   }

   @NotNull
   public String serialize(@NotNull Component component) {
      return this.serializer().toJson(component);
   }

   @NotNull
   public Component deserializeFromTree(@NotNull JsonElement input) {
      Component component = (Component)this.serializer().fromJson(input, Component.class);
      return component;
   }

   @NotNull
   public JsonElement serializeToTree(@NotNull Component component) {
      return this.serializer().toJsonTree(component);
   }

   @NotNull
   public GsonComponentSerializer.Builder toBuilder() {
      return new GsonComponentSerializerImpl.BuilderImpl(this);
   }

   static final class BuilderImpl implements GsonComponentSerializer.Builder {
      private boolean downsampleColor;
      @Nullable
      private LegacyHoverEventSerializer legacyHoverSerializer;
      private boolean emitLegacyHover;

      BuilderImpl() {
         this.downsampleColor = false;
         this.emitLegacyHover = false;
         GsonComponentSerializerImpl.BUILDER.accept(this);
      }

      BuilderImpl(GsonComponentSerializerImpl serializer) {
         this();
         this.downsampleColor = serializer.downsampleColor;
         this.emitLegacyHover = serializer.emitLegacyHover;
         this.legacyHoverSerializer = serializer.legacyHoverSerializer;
      }

      @NotNull
      public GsonComponentSerializer.Builder downsampleColors() {
         this.downsampleColor = true;
         return this;
      }

      @NotNull
      public GsonComponentSerializer.Builder legacyHoverEventSerializer(@Nullable LegacyHoverEventSerializer serializer) {
         this.legacyHoverSerializer = serializer;
         return this;
      }

      @NotNull
      public GsonComponentSerializer.Builder emitLegacyHoverEvent() {
         this.emitLegacyHover = true;
         return this;
      }

      @NotNull
      public GsonComponentSerializer build() {
         if (this.legacyHoverSerializer == null) {
            return this.downsampleColor ? GsonComponentSerializerImpl.Instances.LEGACY_INSTANCE : GsonComponentSerializerImpl.Instances.INSTANCE;
         } else {
            return new GsonComponentSerializerImpl(this.downsampleColor, this.legacyHoverSerializer, this.emitLegacyHover);
         }
      }
   }

   static final class Instances {
      static final GsonComponentSerializer INSTANCE = (GsonComponentSerializer)GsonComponentSerializerImpl.SERVICE.map(GsonComponentSerializer.Provider::gson).orElseGet(() -> {
         return new GsonComponentSerializerImpl(false, (LegacyHoverEventSerializer)null, false);
      });
      static final GsonComponentSerializer LEGACY_INSTANCE = (GsonComponentSerializer)GsonComponentSerializerImpl.SERVICE.map(GsonComponentSerializer.Provider::gsonLegacy).orElseGet(() -> {
         return new GsonComponentSerializerImpl(true, (LegacyHoverEventSerializer)null, true);
      });

      Instances() {
      }
   }
}
