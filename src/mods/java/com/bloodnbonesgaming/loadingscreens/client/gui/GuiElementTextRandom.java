package com.bloodnbonesgaming.loadingscreens.client.gui;

import com.bloodnbonesgaming.loadingscreens.GamestageHandler;
import com.bloodnbonesgaming.loadingscreens.LoadingScreens;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Map.Entry;
import java.util.regex.Pattern;
import net.minecraft.client.Minecraft;
import net.minecraft.util.EnumTypeAdapterFactory;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.ITextComponent.Serializer;

public class GuiElementTextRandom extends GuiElementBase {
   public static final Gson GSON = (new GsonBuilder()).registerTypeHierarchyAdapter(ITextComponent.class, new Serializer()).registerTypeHierarchyAdapter(Style.class, new net.minecraft.util.text.Style.Serializer()).registerTypeAdapterFactory(new EnumTypeAdapterFactory()).create();
   private static final Pattern PATTERN = Pattern.compile("(.+) \\S+");
   private final Random random = new Random();
   private final List<String> strings = new ArrayList();
   private final Map<String, List<String>> stagedStrings = new HashMap();
   private long systemTime = -100L;
   private int currentIndex = 0;

   public GuiElementTextRandom(EnumGuiLocation location) {
      super(location);
   }

   public void addText(String text) {
      JsonParser parser = new JsonParser();
      JsonObject jsonObj;
      if (text.startsWith("{") && text.endsWith("}")) {
         jsonObj = (JsonObject)parser.parse("{\"Title\":" + text + "}");
      } else {
         jsonObj = (JsonObject)parser.parse("{\"Title\":\"" + text + "\"}");
      }

      ITextComponent component = (ITextComponent)GSON.fromJson(jsonObj.get("Title"), ITextComponent.class);
      this.strings.add(component.getFormattedText());
   }

   public void addStagedText(String stage, String text) {
      JsonParser parser = new JsonParser();
      JsonObject jsonObj;
      if (text.startsWith("{") && text.endsWith("}")) {
         jsonObj = (JsonObject)parser.parse("{\"Title\":" + text + "}");
      } else {
         jsonObj = (JsonObject)parser.parse("{\"Title\":\"" + text + "\"}");
      }

      ITextComponent component = (ITextComponent)GSON.fromJson(jsonObj.get("Title"), ITextComponent.class);
      if (!this.stagedStrings.containsKey(stage)) {
         this.stagedStrings.put(stage, new ArrayList());
      }

      ((List)this.stagedStrings.get(stage)).add(component.getFormattedText());
   }

   public void addTip(String text) {
      JsonParser parser = new JsonParser();
      JsonObject jsonObj;
      if (text.startsWith("{") && text.endsWith("}")) {
         jsonObj = (JsonObject)parser.parse("{\"Title\":" + text + "}");
      } else {
         jsonObj = (JsonObject)parser.parse("{\"Title\":{\"text\":\"Tip\\n\",\"color\":\"yellow\",\"bold\":true,\"extra\":[{\"text\":\"" + text + "\",\"color\":\"reset\",\"bold\":false}]}}");
      }

      ITextComponent component = (ITextComponent)GSON.fromJson(jsonObj.get("Title"), ITextComponent.class);
      this.strings.add(component.getFormattedText());
   }

   private List<String> findOptimalLines(Minecraft minecraft, String text, int width) {
      if (text.isEmpty()) {
         return Collections.emptyList();
      } else {
         List<String> list = minecraft.fontRenderer.listFormattedStringToWidth(text, width);
         return list;
      }
   }

   public void render(Minecraft minecraft, int guiWidth, int guiHeight) {
      List<String> strings = new ArrayList(this.strings);
      if (LoadingScreens.gamestages) {
         for(Entry<String, List<String>> entry : this.stagedStrings.entrySet()) {
            if (GamestageHandler.hasGamestage(minecraft, (String)entry.getKey())) {
               strings.addAll((Collection)entry.getValue());
            }
         }
      }

      if (strings.size() > 0) {
         if (strings.size() > 1) {
            long i = Minecraft.getSystemTime();
            if (i - this.systemTime >= 5000L) {
               this.systemTime = i;
               this.currentIndex = this.random.nextInt(strings.size());
            }
         }

         List<String> lines = this.findOptimalLines(minecraft, (String)strings.get(this.currentIndex), (int)((double)guiWidth - (this.relXOffset * (double)guiWidth + (double)this.absXOffset)));

         for(int i = 0; i < lines.size(); ++i) {
            String line = (String)lines.get(i);
            int width = minecraft.fontRenderer.getStringWidth(line);
            int height = minecraft.fontRenderer.FONT_HEIGHT;
            float x = (float)((double)this.location.getX(guiWidth, width) + this.relXOffset * (double)guiWidth + (double)this.absXOffset);
            float z = (float)((double)this.location.getY(guiHeight, height * lines.size()) + this.relYOffset * (double)guiWidth + (double)this.absYOffset);
            minecraft.fontRenderer.drawString(line, x, z + (float)(i * height), -1, false);
         }
      }

   }
}
