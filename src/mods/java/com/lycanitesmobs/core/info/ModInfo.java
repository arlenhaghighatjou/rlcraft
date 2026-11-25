package com.lycanitesmobs.core.info;

import com.lycanitesmobs.client.localisation.LanguageManager;
import java.util.HashMap;
import java.util.Map;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary.Type;

public class ModInfo {
   public static Map<String, ModInfo> modInfos = new HashMap();
   public Object mod;
   public int order;
   public String name;
   public String modid;
   public String dimensionEntries = "";
   public boolean dimensionWhitelist = false;
   public String biomeEntries = "";
   public Biome[] biomes = new Biome[0];
   public Type[] biomeTypesAllowed;
   public Type[] biomeTypesDenied;

   public ModInfo(Object mod, String name, int order) {
      this.mod = mod;
      this.name = name;
      this.modid = name.toLowerCase().replace(" ", "");
      this.order = order;
      modInfos.put(this.name, this);
   }

   public String getTitle() {
      return LanguageManager.translate(this.modid + ".name");
   }
}
