package com.lycanitesmobs.core.spawner.condition;

import com.google.gson.JsonObject;
import com.lycanitesmobs.Utilities;
import java.util.Calendar;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class DateSpawnCondition extends SpawnCondition {
   public int monthMin = -1;
   public int monthMax = -1;
   public int dayMin = -1;
   public int dayMax = -1;
   public String season = "";

   public DateSpawnCondition() {
   }

   public void loadFromJSON(JsonObject json) {
      if (json.has("monthMin")) {
         this.monthMin = json.get("monthMin").getAsInt();
      }

      if (json.has("monthMax")) {
         this.monthMax = json.get("monthMax").getAsInt();
      }

      if (json.has("dayMin")) {
         this.dayMin = json.get("dayMin").getAsInt();
      }

      if (json.has("dayMax")) {
         this.dayMax = json.get("dayMax").getAsInt();
      }

      if (json.has("season")) {
         this.season = json.get("season").getAsString();
      }

      super.loadFromJSON(json);
   }

   public boolean isMet(World world, EntityPlayer player, BlockPos position) {
      Calendar calendar = Calendar.getInstance();
      int month = calendar.get(2) + 1;
      if (this.monthMin >= 0 && month < this.monthMin) {
         return false;
      } else if (this.monthMax >= 0 && month > this.monthMax) {
         return false;
      } else {
         int day = calendar.get(5);
         if (this.dayMin >= 0 && day < this.dayMin) {
            return false;
         } else if (this.dayMax >= 0 && day > this.dayMax) {
            return false;
         } else {
            if (!"".equals(this.season)) {
               if ("valentines".equalsIgnoreCase(this.season) && !Utilities.isValentines()) {
                  return false;
               }

               if ("easter".equalsIgnoreCase(this.season) && !Utilities.isEaster()) {
                  return false;
               }

               if ("midsummer".equalsIgnoreCase(this.season) && !Utilities.isMidsummer()) {
                  return false;
               }

               if ("halloween".equalsIgnoreCase(this.season) && !Utilities.isHalloween()) {
                  return false;
               }

               if (("yuletide".equalsIgnoreCase(this.season) || "christmas".equalsIgnoreCase(this.season)) && !Utilities.isYuletide()) {
                  return false;
               }

               if ("newyear".equalsIgnoreCase(this.season) && !Utilities.isNewYear()) {
                  return false;
               }
            }

            return super.isMet(world, player, position);
         }
      }
   }
}
