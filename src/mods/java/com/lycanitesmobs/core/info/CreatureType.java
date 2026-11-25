package com.lycanitesmobs.core.info;

import com.google.gson.JsonObject;
import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.client.localisation.LanguageManager;
import com.lycanitesmobs.core.item.ItemCustomSpawnEgg;
import com.lycanitesmobs.core.item.consumable.CreatureTreatItem;
import com.lycanitesmobs.core.item.equipment.CreatureSaddleItem;
import com.lycanitesmobs.core.item.special.ItemSoulstone;
import com.lycanitesmobs.core.item.special.ItemSoulstoneFilled;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import net.minecraft.item.Item;

public class CreatureType {
   protected String name;
   public ModInfo modInfo;
   public Map<String, CreatureInfo> creatures = new HashMap();
   public List<CreatureInfo> tameableCreatures = new ArrayList();
   protected Item treat;
   public Item saddle;
   protected ItemSoulstone soulstone;
   protected Item spawnEgg;
   public String dietProvided = "none";

   public CreatureType(ModInfo group) {
      this.modInfo = group;
   }

   public void loadFromJSON(JsonObject json) {
      this.name = json.get("name").getAsString();
      if (json.has("dietProvided")) {
         this.dietProvided = json.get("dietProvided").getAsString();
      }

   }

   public void load() {
      this.createItems();
      LycanitesMobs.logDebug("Creature Type", "Loaded Creature Type: " + this.getTitle());
   }

   public String getName() {
      return this.name;
   }

   public String getTitle() {
      return LanguageManager.translate("creaturetype." + this.getName() + ".name");
   }

   public String getTreatName() {
      return this.getName() + "treat";
   }

   public Item getTreatItem() {
      return this.treat;
   }

   public String getSaddleName() {
      return "saddle_" + this.getName();
   }

   public Item getSaddleItem() {
      return this.saddle;
   }

   public String getSoulstoneName() {
      return "soulstone_" + this.getName();
   }

   public ItemSoulstone getSoulstoneItem() {
      return this.soulstone;
   }

   public String getSpawnEggName() {
      return this.getName() + "spawn";
   }

   public void addCreature(CreatureInfo creatureInfo) {
      if (!this.creatures.containsKey(creatureInfo.getName())) {
         this.creatures.put(creatureInfo.getName(), creatureInfo);
         if (creatureInfo.isTameable()) {
            this.tameableCreatures.add(creatureInfo);
         }

      }
   }

   public Item getSpawnEgg() {
      return this.spawnEgg;
   }

   public void createItems() {
      this.spawnEgg = ObjectManager.getItem(this.getSpawnEggName());
      if (this.spawnEgg != null) {
         if (this.spawnEgg instanceof ItemCustomSpawnEgg) {
            ((ItemCustomSpawnEgg)this.spawnEgg).creatureType = this;
         }

      } else {
         this.spawnEgg = new ItemCustomSpawnEgg(this.getSpawnEggName(), this);
         ObjectManager.addItem(this.getSpawnEggName(), this.spawnEgg);
         this.treat = ObjectManager.getItem(this.getTreatName());
         if (this.treat == null) {
            this.treat = new CreatureTreatItem(this);
            ObjectManager.addItem(this.getTreatName(), this.treat);
            this.saddle = ObjectManager.getItem(this.getSaddleName());
            if (this.saddle == null) {
               this.saddle = new CreatureSaddleItem(this);
               ObjectManager.addItem(this.getSaddleName(), this.saddle);
               this.soulstone = (ItemSoulstone)ObjectManager.getItem(this.getSoulstoneName());
               if (this.soulstone == null) {
                  this.soulstone = new ItemSoulstoneFilled(this.modInfo, this);
                  ObjectManager.addItem(this.getSoulstoneName(), this.soulstone);
               }
            }
         }
      }
   }
}
