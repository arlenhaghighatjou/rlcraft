package hunternif.mc.atlas.client;

import hunternif.mc.atlas.core.Tile;
import hunternif.mc.atlas.util.Log;
import hunternif.mc.atlas.util.SaveData;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.Map.Entry;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.biome.Biome;
import net.minecraftforge.common.BiomeDictionary;
import net.minecraftforge.common.BiomeDictionary.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class BiomeTextureMap extends SaveData {
   private static final BiomeTextureMap INSTANCE = new BiomeTextureMap();
   final Map<Biome, TextureSet> biomeTextureMap = new HashMap();
   final Map<Integer, TextureSet> pseudoBiomeTextureMap = new HashMap();
   public static final TextureSet defaultTexture = TextureSet.PLAINS;

   public BiomeTextureMap() {
   }

   public static BiomeTextureMap instance() {
      return INSTANCE;
   }

   public void setTexture(Biome biome, TextureSet textureSet) {
      if (textureSet == null) {
         if (this.biomeTextureMap.remove(biome) != null) {
            Log.warn("Removing old texture for biome %s", biome.getRegistryName());
         }

      } else {
         TextureSet previous = (TextureSet)this.biomeTextureMap.put(biome, textureSet);
         if (previous == null) {
            this.markDirty();
         } else if (!previous.equals(textureSet)) {
            Log.warn("Overwriting texture set for biome %s", biome.getRegistryName());
            this.markDirty();
         }

      }
   }

   public void setTexture(int pseudoID, TextureSet textureSet) {
      if (textureSet == null) {
         if (this.pseudoBiomeTextureMap.remove(Integer.valueOf(pseudoID)) != null) {
            Log.warn("Removing old texture for pseudo-biome %d", pseudoID);
         }

      } else {
         TextureSet previous = (TextureSet)this.pseudoBiomeTextureMap.put(Integer.valueOf(pseudoID), textureSet);
         if (previous == null) {
            this.markDirty();
         } else if (!previous.equals(textureSet)) {
            Log.warn("Overwriting texture set for pseudo-biome %d", pseudoID);
            this.markDirty();
         }

      }
   }

   private void autoRegister(Biome biome) {
      if (biome == null) {
         Log.warn("Biome is null");
      } else {
         Set<Type> types = BiomeDictionary.getTypes(biome);
         if (types.contains(Type.SWAMP)) {
            if (types.contains(Type.HILLS)) {
               this.setTexture(biome, TextureSet.SWAMP_HILLS);
            } else {
               this.setTexture(biome, TextureSet.SWAMP);
            }
         } else if (!types.contains(Type.WATER) && !types.contains(Type.RIVER)) {
            if (types.contains(Type.BEACH)) {
               if (types.contains(Type.MOUNTAIN)) {
                  this.setTexture(biome, TextureSet.ROCK_SHORE);
               } else {
                  this.setTexture(biome, TextureSet.SHORE);
               }
            } else if (types.contains(Type.JUNGLE)) {
               if (types.contains(Type.MOUNTAIN)) {
                  this.setTexture(biome, TextureSet.JUNGLE_CLIFFS);
               } else if (types.contains(Type.HILLS)) {
                  this.setTexture(biome, TextureSet.JUNGLE_HILLS);
               } else {
                  this.setTexture(biome, TextureSet.JUNGLE);
               }
            } else if (types.contains(Type.SAVANNA)) {
               if (!types.contains(Type.MOUNTAIN) && !types.contains(Type.HILLS)) {
                  this.setTexture(biome, TextureSet.SAVANNA);
               } else {
                  this.setTexture(biome, TextureSet.SAVANNA_CLIFFS);
               }
            } else if (types.contains(Type.CONIFEROUS)) {
               if (!types.contains(Type.MOUNTAIN) && !types.contains(Type.HILLS)) {
                  this.setTexture(biome, TextureSet.PINES);
               } else {
                  this.setTexture(biome, TextureSet.PINES_HILLS);
               }
            } else if (types.contains(Type.MESA)) {
               if (types.contains(Type.FOREST)) {
                  this.setTexture(biome, TextureSet.PLATEAU_MESA_TREES);
               } else {
                  this.setTexture(biome, TextureSet.PLATEAU_MESA);
               }
            } else if (types.contains(Type.FOREST)) {
               if (types.contains(Type.SNOWY)) {
                  if (types.contains(Type.HILLS)) {
                     this.setTexture(biome, TextureSet.SNOW_PINES_HILLS);
                  } else {
                     this.setTexture(biome, TextureSet.SNOW_PINES);
                  }
               } else if (types.contains(Type.SPARSE)) {
                  if (types.contains(Type.HILLS)) {
                     this.setTexture(biome, TextureSet.SPARSE_FOREST_HILLS);
                  } else {
                     this.setTexture(biome, TextureSet.SPARSE_FOREST);
                  }
               } else if (types.contains(Type.DENSE)) {
                  if (types.contains(Type.HILLS)) {
                     this.setTexture(biome, TextureSet.DENSE_FOREST_HILLS);
                  } else {
                     this.setTexture(biome, TextureSet.DENSE_FOREST);
                  }
               } else if (types.contains(Type.HILLS)) {
                  this.setTexture(biome, TextureSet.FOREST_HILLS);
               } else {
                  this.setTexture(biome, TextureSet.FOREST);
               }
            } else if (!types.contains(Type.PLAINS) && !types.contains(Type.WASTELAND)) {
               if (types.contains(Type.MOUNTAIN)) {
                  this.setTexture(biome, TextureSet.MOUNTAINS_NAKED);
               } else if (types.contains(Type.HILLS)) {
                  if (!types.contains(Type.SNOWY) && !types.contains(Type.COLD)) {
                     if (types.contains(Type.SANDY)) {
                        this.setTexture(biome, TextureSet.DESERT_HILLS);
                     } else {
                        this.setTexture(biome, TextureSet.HILLS);
                     }
                  } else {
                     this.setTexture(biome, TextureSet.SNOW_HILLS);
                  }
               } else {
                  this.setTexture(biome, defaultTexture);
               }
            } else if (!types.contains(Type.SNOWY) && !types.contains(Type.COLD)) {
               if (types.contains(Type.HOT)) {
                  if (!types.contains(Type.HILLS) && !types.contains(Type.MOUNTAIN)) {
                     this.setTexture(biome, TextureSet.DESERT);
                  } else {
                     this.setTexture(biome, TextureSet.DESERT_HILLS);
                  }
               } else if (!types.contains(Type.HILLS) && !types.contains(Type.MOUNTAIN)) {
                  this.setTexture(biome, TextureSet.PLAINS);
               } else {
                  this.setTexture(biome, TextureSet.HILLS);
               }
            } else if (types.contains(Type.MOUNTAIN)) {
               this.setTexture(biome, TextureSet.MOUNTAINS_SNOW_CAPS);
            } else if (types.contains(Type.HILLS)) {
               this.setTexture(biome, TextureSet.SNOW_HILLS);
            } else {
               this.setTexture(biome, TextureSet.SNOW);
            }
         } else if (!types.contains(Type.FOREST) && !types.contains(Type.JUNGLE)) {
            if (types.contains(Type.SNOWY)) {
               this.setTexture(biome, TextureSet.ICE);
            } else {
               this.setTexture(biome, TextureSet.WATER);
            }
         } else if (types.contains(Type.HILLS)) {
            this.setTexture(biome, TextureSet.SWAMP_HILLS);
         } else {
            this.setTexture(biome, TextureSet.SWAMP);
         }

         Log.info("Auto-registered standard texture set for biome %s", biome.getRegistryName().toString());
      }
   }

   public void checkRegistration(Biome biome) {
      if (!this.isRegistered(biome)) {
         this.autoRegister(biome);
         this.markDirty();
      }

   }

   private void checkRegistration(int pseudoID) {
      if (!this.isRegistered(pseudoID)) {
         this.setTexture(pseudoID, defaultTexture);
      }

   }

   public boolean isRegistered(Biome biome) {
      return this.biomeTextureMap.containsKey(biome);
   }

   public boolean isRegistered(int pseudoID) {
      return this.pseudoBiomeTextureMap.containsKey(Integer.valueOf(pseudoID));
   }

   public TextureSet getTextureSet(Tile tile) {
      if (tile == null) {
         return defaultTexture;
      } else if (tile.biomeID >= 0) {
         Biome biome = Biome.getBiomeForId(tile.biomeID);
         this.checkRegistration(biome);
         return (TextureSet)this.biomeTextureMap.getOrDefault(biome, defaultTexture);
      } else {
         this.checkRegistration(tile.biomeID);
         return (TextureSet)this.pseudoBiomeTextureMap.get(Integer.valueOf(tile.biomeID));
      }
   }

   public ResourceLocation getTexture(Tile tile) {
      TextureSet set = this.getTextureSet(tile);
      int i = MathHelper.floor((float)tile.getVariationNumber() / 32767.0F * (float)set.textures.length);
      return set.textures[i];
   }

   public List<ResourceLocation> getAllTextures() {
      List<ResourceLocation> list = new ArrayList(this.biomeTextureMap.size());

      for(Entry<Biome, TextureSet> entry : this.biomeTextureMap.entrySet()) {
         list.addAll(Arrays.asList(((TextureSet)entry.getValue()).textures));
      }

      for(Entry<Integer, TextureSet> entry : this.pseudoBiomeTextureMap.entrySet()) {
         list.addAll(Arrays.asList(((TextureSet)entry.getValue()).textures));
      }

      return list;
   }
}
