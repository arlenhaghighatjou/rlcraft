package com.lycanitesmobs.core.dungeon.definition;

import com.google.gson.JsonObject;

public class DungeonStructure {
   public String name = "";
   public String type = "decoration";
   public int weight = 8;
   public String nbt = "";

   public DungeonStructure() {
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString().toLowerCase();
      if (json.has("type")) {
         this.type = json.get("type").getAsString().toLowerCase();
      }

      if (json.has("nbt")) {
         this.nbt = json.get("nbt").getAsString().toLowerCase();
      }

   }
}
