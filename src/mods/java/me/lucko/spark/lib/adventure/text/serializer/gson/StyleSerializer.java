package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonNull;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.internal.Streams;
import com.google.gson.stream.JsonReader;
import java.io.IOException;
import java.io.StringReader;
import java.lang.reflect.Type;
import java.util.EnumSet;
import java.util.Set;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.event.ClickEvent;
import me.lucko.spark.lib.adventure.text.event.HoverEvent;
import me.lucko.spark.lib.adventure.text.format.Style;
import me.lucko.spark.lib.adventure.text.format.TextColor;
import me.lucko.spark.lib.adventure.text.format.TextDecoration;
import me.lucko.spark.lib.adventure.util.Codec;
import org.jetbrains.annotations.Nullable;

final class StyleSerializer implements JsonDeserializer<Style>, JsonSerializer<Style> {
   private static final TextDecoration[] DECORATIONS = new TextDecoration[]{TextDecoration.BOLD, TextDecoration.ITALIC, TextDecoration.UNDERLINED, TextDecoration.STRIKETHROUGH, TextDecoration.OBFUSCATED};
   static final String FONT = "font";
   static final String COLOR = "color";
   static final String INSERTION = "insertion";
   static final String CLICK_EVENT = "clickEvent";
   static final String CLICK_EVENT_ACTION = "action";
   static final String CLICK_EVENT_VALUE = "value";
   static final String HOVER_EVENT = "hoverEvent";
   static final String HOVER_EVENT_ACTION = "action";
   static final String HOVER_EVENT_CONTENTS = "contents";
   /** @deprecated */
   @Deprecated
   static final String HOVER_EVENT_VALUE = "value";
   private final LegacyHoverEventSerializer legacyHover;
   private final boolean emitLegacyHover;

   StyleSerializer(@Nullable LegacyHoverEventSerializer legacyHover, boolean emitLegacyHover) {
      this.legacyHover = legacyHover;
      this.emitLegacyHover = emitLegacyHover;
   }

   public Style deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      JsonObject object = json.getAsJsonObject();
      return this.deserialize(object, context);
   }

   private Style deserialize(JsonObject json, JsonDeserializationContext context) throws JsonParseException {
      Style.Builder style = Style.style();
      if (json.has("font")) {
         style.font((Key)context.deserialize(json.get("font"), Key.class));
      }

      if (json.has("color")) {
         TextColorWrapper color = (TextColorWrapper)context.deserialize(json.get("color"), TextColorWrapper.class);
         if (color.color != null) {
            style.color(color.color);
         } else if (color.decoration != null) {
            style.decoration(color.decoration, true);
         }
      }

      int i = 0;

      for(int length = DECORATIONS.length; i < length; ++i) {
         TextDecoration decoration = DECORATIONS[i];
         String name = TextDecoration.NAMES.key(decoration);
         if (json.has(name)) {
            style.decoration(decoration, json.get(name).getAsBoolean());
         }
      }

      if (json.has("insertion")) {
         style.insertion(json.get("insertion").getAsString());
      }

      if (json.has("clickEvent")) {
         JsonObject clickEvent = json.getAsJsonObject("clickEvent");
         if (clickEvent != null) {
            ClickEvent.Action action = (ClickEvent.Action)optionallyDeserialize(clickEvent.getAsJsonPrimitive("action"), context, ClickEvent.Action.class);
            if (action != null && action.readable()) {
               JsonPrimitive rawValue = clickEvent.getAsJsonPrimitive("value");
               String value = rawValue == null ? null : rawValue.getAsString();
               if (value != null) {
                  style.clickEvent(ClickEvent.clickEvent(action, value));
               }
            }
         }
      }

      if (json.has("hoverEvent")) {
         JsonObject hoverEvent = json.getAsJsonObject("hoverEvent");
         if (hoverEvent != null) {
            HoverEvent.Action action = (HoverEvent.Action)optionallyDeserialize(hoverEvent.getAsJsonPrimitive("action"), context, HoverEvent.Action.class);
            if (action != null && action.readable()) {
               Object value;
               if (hoverEvent.has("contents")) {
                  JsonElement rawValue = hoverEvent.get("contents");
                  value = context.deserialize(rawValue, action.type());
               } else if (hoverEvent.has("value")) {
                  Component rawValue = (Component)context.deserialize(hoverEvent.get("value"), Component.class);
                  value = this.legacyHoverEventContents(action, rawValue, context);
               } else {
                  value = null;
               }

               if (value != null) {
                  style.hoverEvent(HoverEvent.hoverEvent(action, value));
               }
            }
         }
      }

      if (json.has("font")) {
         style.font((Key)context.deserialize(json.get("font"), Key.class));
      }

      return style.build();
   }

   private static <T> T optionallyDeserialize(JsonElement json, JsonDeserializationContext context, Class<T> type) {
      return (T)(json == null ? null : context.deserialize(json, type));
   }

   private Object legacyHoverEventContents(HoverEvent.Action<?> action, Component rawValue, JsonDeserializationContext context) {
      if (action == HoverEvent.Action.SHOW_TEXT) {
         return rawValue;
      } else {
         if (this.legacyHover != null) {
            try {
               if (action == HoverEvent.Action.SHOW_ENTITY) {
                  return this.legacyHover.deserializeShowEntity(rawValue, this.decoder(context));
               }

               if (action == HoverEvent.Action.SHOW_ITEM) {
                  return this.legacyHover.deserializeShowItem(rawValue);
               }
            } catch (IOException var5) {
               throw new JsonParseException(var5);
            }
         }

         throw new UnsupportedOperationException();
      }
   }

   private Codec.Decoder<Component, String, JsonParseException> decoder(JsonDeserializationContext ctx) {
      return (string) -> {
         JsonReader reader = new JsonReader(new StringReader(string));
         return (Component)ctx.deserialize(Streams.parse(reader), Component.class);
      };
   }

   public JsonElement serialize(Style src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject json = new JsonObject();
      int i = 0;

      for(int length = DECORATIONS.length; i < length; ++i) {
         TextDecoration decoration = DECORATIONS[i];
         TextDecoration.State state = src.decoration(decoration);
         if (state != TextDecoration.State.NOT_SET) {
            String name = TextDecoration.NAMES.key(decoration);

            assert name != null;

            json.addProperty(name, Boolean.valueOf(state == TextDecoration.State.TRUE));
         }
      }

      TextColor color = src.color();
      if (color != null) {
         json.add("color", context.serialize(color));
      }

      String insertion = src.insertion();
      if (insertion != null) {
         json.addProperty("insertion", insertion);
      }

      ClickEvent clickEvent = src.clickEvent();
      if (clickEvent != null) {
         JsonObject eventJson = new JsonObject();
         eventJson.add("action", context.serialize(clickEvent.action()));
         eventJson.addProperty("value", clickEvent.value());
         json.add("clickEvent", eventJson);
      }

      HoverEvent<?> hoverEvent = src.hoverEvent();
      if (hoverEvent != null) {
         JsonObject eventJson = new JsonObject();
         eventJson.add("action", context.serialize(hoverEvent.action()));
         JsonElement modernContents = context.serialize(hoverEvent.value());
         eventJson.add("contents", modernContents);
         if (this.emitLegacyHover) {
            eventJson.add("value", this.serializeLegacyHoverEvent(hoverEvent, modernContents, context));
         }

         json.add("hoverEvent", eventJson);
      }

      Key font = src.font();
      if (font != null) {
         json.add("font", context.serialize(font));
      }

      return json;
   }

   private JsonElement serializeLegacyHoverEvent(HoverEvent<?> hoverEvent, JsonElement modernContents, JsonSerializationContext context) {
      if (hoverEvent.action() == HoverEvent.Action.SHOW_TEXT) {
         return modernContents;
      } else if (this.legacyHover != null) {
         Component serialized = null;

         try {
            if (hoverEvent.action() == HoverEvent.Action.SHOW_ENTITY) {
               serialized = this.legacyHover.serializeShowEntity((HoverEvent.ShowEntity)hoverEvent.value(), this.encoder(context));
            } else if (hoverEvent.action() == HoverEvent.Action.SHOW_ITEM) {
               serialized = this.legacyHover.serializeShowItem((HoverEvent.ShowItem)hoverEvent.value());
            }
         } catch (IOException var6) {
            throw new JsonSyntaxException(var6);
         }

         return (JsonElement)(serialized == null ? JsonNull.INSTANCE : context.serialize(serialized));
      } else {
         return JsonNull.INSTANCE;
      }
   }

   private Codec.Encoder<Component, String, RuntimeException> encoder(JsonSerializationContext ctx) {
      return (component) -> {
         return ctx.serialize(component).toString();
      };
   }

   static {
      Set<TextDecoration> knownDecorations = EnumSet.allOf(TextDecoration.class);

      for(TextDecoration decoration : DECORATIONS) {
         knownDecorations.remove(decoration);
      }

      if (!knownDecorations.isEmpty()) {
         throw new IllegalStateException("Gson serializer is missing some text decorations: " + knownDecorations);
      }
   }
}
