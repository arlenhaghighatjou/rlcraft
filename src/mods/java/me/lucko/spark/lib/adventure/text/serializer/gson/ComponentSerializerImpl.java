package me.lucko.spark.lib.adventure.text.serializer.gson;

import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import me.lucko.spark.lib.adventure.key.Key;
import me.lucko.spark.lib.adventure.text.BlockNBTComponent;
import me.lucko.spark.lib.adventure.text.BuildableComponent;
import me.lucko.spark.lib.adventure.text.Component;
import me.lucko.spark.lib.adventure.text.ComponentBuilder;
import me.lucko.spark.lib.adventure.text.EntityNBTComponent;
import me.lucko.spark.lib.adventure.text.KeybindComponent;
import me.lucko.spark.lib.adventure.text.NBTComponent;
import me.lucko.spark.lib.adventure.text.NBTComponentBuilder;
import me.lucko.spark.lib.adventure.text.ScoreComponent;
import me.lucko.spark.lib.adventure.text.SelectorComponent;
import me.lucko.spark.lib.adventure.text.StorageNBTComponent;
import me.lucko.spark.lib.adventure.text.TextComponent;
import me.lucko.spark.lib.adventure.text.TranslatableComponent;
import me.lucko.spark.lib.adventure.text.format.Style;
import org.jetbrains.annotations.Nullable;

final class ComponentSerializerImpl implements JsonDeserializer<Component>, JsonSerializer<Component> {
   static final String TEXT = "text";
   static final String TRANSLATE = "translate";
   static final String TRANSLATE_WITH = "with";
   static final String SCORE = "score";
   static final String SCORE_NAME = "name";
   static final String SCORE_OBJECTIVE = "objective";
   static final String SCORE_VALUE = "value";
   static final String SELECTOR = "selector";
   static final String KEYBIND = "keybind";
   static final String EXTRA = "extra";
   static final String NBT = "nbt";
   static final String NBT_INTERPRET = "interpret";
   static final String NBT_BLOCK = "block";
   static final String NBT_ENTITY = "entity";
   static final String NBT_STORAGE = "storage";
   static final String SEPARATOR = "separator";

   ComponentSerializerImpl() {
   }

   public Component deserialize(JsonElement json, Type typeOfT, JsonDeserializationContext context) throws JsonParseException {
      return this.deserialize0(json, context);
   }

   private BuildableComponent<?, ?> deserialize0(JsonElement element, JsonDeserializationContext context) throws JsonParseException {
      if (element.isJsonPrimitive()) {
         return Component.text(element.getAsString());
      } else if (element.isJsonArray()) {
         ComponentBuilder<?, ?> parent = null;

         for(JsonElement childElement : element.getAsJsonArray()) {
            BuildableComponent<?, ?> child = this.deserialize0(childElement, context);
            if (parent == null) {
               parent = child.toBuilder();
            } else {
               parent.append((Component)child);
            }
         }

         if (parent == null) {
            throw notSureHowToDeserialize(element);
         } else {
            return parent.build();
         }
      } else if (!element.isJsonObject()) {
         throw notSureHowToDeserialize(element);
      } else {
         JsonObject object = element.getAsJsonObject();
         ComponentBuilder<?, ?> component;
         if (object.has("text")) {
            component = Component.text().content(object.get("text").getAsString());
         } else if (object.has("translate")) {
            String key = object.get("translate").getAsString();
            if (!object.has("with")) {
               component = Component.translatable().key(key);
            } else {
               JsonArray with = object.getAsJsonArray("with");
               List<Component> args = new ArrayList(with.size());
               int i = 0;

               for(int size = with.size(); i < size; ++i) {
                  JsonElement argElement = with.get(i);
                  args.add(this.deserialize0(argElement, context));
               }

               component = Component.translatable().key(key).args(args);
            }
         } else if (object.has("score")) {
            JsonObject score = object.getAsJsonObject("score");
            if (!score.has("name") || !score.has("objective")) {
               throw new JsonParseException("A score component requires a name and objective");
            }

            ScoreComponent.Builder builder = Component.score().name(score.get("name").getAsString()).objective(score.get("objective").getAsString());
            if (score.has("value")) {
               component = builder.value(score.get("value").getAsString());
            } else {
               component = builder;
            }
         } else if (object.has("selector")) {
            Component separator = this.deserializeSeparator(object, context);
            component = Component.selector().pattern(object.get("selector").getAsString()).separator(separator);
         } else if (object.has("keybind")) {
            component = Component.keybind().keybind(object.get("keybind").getAsString());
         } else {
            if (!object.has("nbt")) {
               throw notSureHowToDeserialize(element);
            }

            String nbt = object.get("nbt").getAsString();
            boolean interpret = object.has("interpret") && object.getAsJsonPrimitive("interpret").getAsBoolean();
            Component separator = this.deserializeSeparator(object, context);
            if (object.has("block")) {
               BlockNBTComponent.Pos pos = (BlockNBTComponent.Pos)context.deserialize(object.get("block"), BlockNBTComponent.Pos.class);
               component = ((BlockNBTComponent.Builder)nbt(Component.blockNBT(), nbt, interpret, separator)).pos(pos);
            } else if (object.has("entity")) {
               component = ((EntityNBTComponent.Builder)nbt(Component.entityNBT(), nbt, interpret, separator)).selector(object.get("entity").getAsString());
            } else {
               if (!object.has("storage")) {
                  throw notSureHowToDeserialize(element);
               }

               component = ((StorageNBTComponent.Builder)nbt(Component.storageNBT(), nbt, interpret, separator)).storage((Key)context.deserialize(object.get("storage"), Key.class));
            }
         }

         if (object.has("extra")) {
            JsonArray extra = object.getAsJsonArray("extra");
            int i = 0;

            for(int size = extra.size(); i < size; ++i) {
               JsonElement extraElement = extra.get(i);
               component.append((Component)this.deserialize0(extraElement, context));
            }
         }

         Style style = (Style)context.deserialize(element, Style.class);
         if (!style.isEmpty()) {
            component.style(style);
         }

         return component.build();
      }
   }

   @Nullable
   private Component deserializeSeparator(JsonObject json, JsonDeserializationContext context) {
      return json.has("separator") ? this.deserialize0(json.get("separator"), context) : null;
   }

   private static <C extends NBTComponent<C, B>, B extends NBTComponentBuilder<C, B>> B nbt(B builder, String nbt, boolean interpret, @Nullable Component separator) {
      return (B)builder.nbtPath(nbt).interpret(interpret).separator(separator);
   }

   public JsonElement serialize(Component src, Type typeOfSrc, JsonSerializationContext context) {
      JsonObject object = new JsonObject();
      if (src.hasStyling()) {
         JsonElement style = context.serialize(src.style());
         if (style.isJsonObject()) {
            for(Entry<String, JsonElement> entry : ((JsonObject)style).entrySet()) {
               object.add((String)entry.getKey(), (JsonElement)entry.getValue());
            }
         }
      }

      List<Component> children = src.children();
      if (!children.isEmpty()) {
         JsonArray extra = new JsonArray();

         for(Component child : children) {
            extra.add(context.serialize(child));
         }

         object.add("extra", extra);
      }

      if (src instanceof TextComponent) {
         object.addProperty("text", ((TextComponent)src).content());
      } else if (src instanceof TranslatableComponent) {
         TranslatableComponent tc = (TranslatableComponent)src;
         object.addProperty("translate", tc.key());
         if (!tc.args().isEmpty()) {
            JsonArray with = new JsonArray();

            for(Component arg : tc.args()) {
               with.add(context.serialize(arg));
            }

            object.add("with", with);
         }
      } else if (src instanceof ScoreComponent) {
         ScoreComponent sc = (ScoreComponent)src;
         JsonObject score = new JsonObject();
         score.addProperty("name", sc.name());
         score.addProperty("objective", sc.objective());
         String value = sc.value();
         if (value != null) {
            score.addProperty("value", value);
         }

         object.add("score", score);
      } else if (src instanceof SelectorComponent) {
         SelectorComponent sc = (SelectorComponent)src;
         object.addProperty("selector", sc.pattern());
         this.serializeSeparator(context, object, sc.separator());
      } else if (src instanceof KeybindComponent) {
         object.addProperty("keybind", ((KeybindComponent)src).keybind());
      } else {
         if (!(src instanceof NBTComponent)) {
            throw notSureHowToSerialize(src);
         }

         NBTComponent<?, ?> nc = (NBTComponent)src;
         object.addProperty("nbt", nc.nbtPath());
         object.addProperty("interpret", Boolean.valueOf(nc.interpret()));
         if (src instanceof BlockNBTComponent) {
            JsonElement position = context.serialize(((BlockNBTComponent)nc).pos());
            object.add("block", position);
            this.serializeSeparator(context, object, nc.separator());
         } else if (src instanceof EntityNBTComponent) {
            object.addProperty("entity", ((EntityNBTComponent)nc).selector());
         } else {
            if (!(src instanceof StorageNBTComponent)) {
               throw notSureHowToSerialize(src);
            }

            object.add("storage", context.serialize(((StorageNBTComponent)nc).storage()));
         }
      }

      return object;
   }

   private void serializeSeparator(JsonSerializationContext context, JsonObject json, @Nullable Component separator) {
      if (separator != null) {
         json.add("separator", context.serialize(separator));
      }

   }

   static JsonParseException notSureHowToDeserialize(Object element) {
      return new JsonParseException("Don't know how to turn " + element + " into a Component");
   }

   private static IllegalArgumentException notSureHowToSerialize(Component component) {
      return new IllegalArgumentException("Don't know how to serialize " + component + " as a Component");
   }
}
