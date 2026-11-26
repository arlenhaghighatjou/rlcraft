package hunternif.mc.atlas.client;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import hunternif.mc.atlas.AntiqueAtlasMod;
import hunternif.mc.atlas.util.AbstractJSONConfig;
import hunternif.mc.atlas.util.Log;
import net.lax1dude.eaglercraft.v1_8.internal.vfs2.VFile2;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.Map.Entry;
import java.util.function.Function;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryEntry.Impl;

@SideOnly(Side.CLIENT)
public class BiomeTextureConfig extends AbstractJSONConfig<BiomeTextureMap> {
   private static final int VERSION = 2;
   private final TextureSetMap textureSetMap;

   public BiomeTextureConfig(VFile2 file, TextureSetMap textureSetMap) {
      super(file);
      this.textureSetMap = textureSetMap;
   }

   public int currentVersion() {
      return 2;
   }

   protected void loadData(JsonObject json, BiomeTextureMap data, int version) {
      if (version == 0) {
         Log.warn("Too many biome textures changed since config version 0, disregarding this config entirely");
      } else if (version == 1) {
         Log.warn("Config version 1 no longer supported, config file will be reset We now use resource location to identify biomes");
      } else {
         for(Entry<String, JsonElement> entry : json.entrySet()) {
            Biome biome = (Biome)ForgeRegistries.BIOMES.getValue(new ResourceLocation((String)entry.getKey()));
            if (biome == null) {
               Log.warn("Unknown biome in texture map: %s", entry.getKey());
            } else if (!((JsonElement)entry.getValue()).isJsonArray()) {
               String name = ((JsonElement)entry.getValue()).getAsString();
               if (this.textureSetMap.isRegistered(name)) {
                  data.setTexture(biome, this.textureSetMap.getByName(name));
                  Log.info("Registered texture set %s for biome %s", name, biome.getRegistryName().toString());
               } else {
                  Log.warn("Unknown texture set %s for biome %s", name, biome.getRegistryName().toString());
               }
            } else {
               JsonArray array = ((JsonElement)entry.getValue()).getAsJsonArray();
               ResourceLocation[] textures = new ResourceLocation[array.size()];

               for(int i = 0; i < array.size(); ++i) {
                  String path = array.get(i).getAsString();
                  textures[i] = new ResourceLocation(path);
               }

               data.setTexture(biome, new TextureSet((String)null, textures));
               Log.info("Registered %d custom texture(s) for biome %s", textures.length, biome.getRegistryName().toString());
            }
         }

      }
   }

   protected void saveData(JsonObject json, BiomeTextureMap data) {
      List<Biome> keys = new ArrayList(data.biomeTextureMap.keySet());
      Collections.sort(keys, Comparator.comparing(Impl::getRegistryName));

      for(Biome key : keys) {
         int biomeID = Biome.getIdForBiome(key);
         if (biomeID >= 0 && (AntiqueAtlasMod.instance.jeidPresent || biomeID < 256)) {
            json.addProperty(key.getRegistryName().toString(), ((TextureSet)data.biomeTextureMap.get(key)).name);
         }
      }

   }
}
