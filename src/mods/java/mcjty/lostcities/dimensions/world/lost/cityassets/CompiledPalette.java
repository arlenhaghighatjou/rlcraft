package mcjty.lostcities.dimensions.world.lost.cityassets;

import java.util.HashMap;
import java.util.Map;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import java.util.Set;
import java.util.Map.Entry;
import mcjty.lostcities.dimensions.world.terraingen.LostCitiesTerrainGenerator;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import org.apache.commons.lang3.tuple.Pair;

public class CompiledPalette {
   private final Map<Character, Object> palette = new HashMap();
   private final Map<Character, Character> damagedToBlock = new HashMap();
   private final Map<Character, CompiledPalette.Info> information = new HashMap();

   public CompiledPalette(CompiledPalette other, Palette... palettes) {
      this.palette.putAll(other.palette);
      this.damagedToBlock.putAll(other.damagedToBlock);
      this.information.putAll(other.information);
      this.addPalettes(palettes);
   }

   public CompiledPalette(Palette... palettes) {
      this.addPalettes(palettes);
   }

   private int addEntries(char[] randomBlocks, int idx, char c, int cnt) {
      for(int i = 0; i < cnt; ++i) {
         if (idx >= randomBlocks.length) {
            return idx;
         }

         randomBlocks[idx++] = c;
      }

      return idx;
   }

   public void addPalettes(Palette[] palettes) {
      for(Palette p : palettes) {
         for(Entry<Character, Object> entry : p.palette.entrySet()) {
            Object value = entry.getValue();
            if (value instanceof IBlockState) {
               this.palette.put(entry.getKey(), Character.valueOf((char)Block.BLOCK_STATE_IDS.get((IBlockState)value)));
            } else if (!(value instanceof Pair[])) {
               if (!(value instanceof String)) {
                  if (value == null) {
                     throw new RuntimeException("Invalid palette entry for '" + entry.getKey() + "'!");
                  }

                  this.palette.put(entry.getKey(), value);
               }
            } else {
               Pair<Integer, IBlockState>[] r = (Pair[])((Pair[])value);
               char[] randomBlocks = new char[128];
               int idx = 0;

               for(Pair<Integer, IBlockState> pair : r) {
                  idx = this.addEntries(randomBlocks, idx, (char)Block.BLOCK_STATE_IDS.get(pair.getRight()), ((Integer)pair.getLeft()).intValue());
                  if (idx >= randomBlocks.length) {
                     break;
                  }
               }

               this.palette.put(entry.getKey(), randomBlocks);
            }
         }
      }

      boolean dirty = true;

      while(dirty) {
         dirty = false;

         for(Palette p : palettes) {
            for(Entry<Character, Object> entry : p.palette.entrySet()) {
               Object value = entry.getValue();
               if (value instanceof String) {
                  char c = ((String)value).charAt(0);
                  if (this.palette.containsKey(Character.valueOf(c)) && !this.palette.containsKey(entry.getKey())) {
                     Object s = this.palette.get(Character.valueOf(c));
                     if (s instanceof IBlockState) {
                        s = (char)Block.BLOCK_STATE_IDS.get((IBlockState)value);
                     }

                     this.palette.put(entry.getKey(), s);
                     dirty = true;
                  }
               }
            }
         }
      }

      for(Palette p : palettes) {
         for(Entry<IBlockState, IBlockState> entry : p.getDamaged().entrySet()) {
            IBlockState c = (IBlockState)entry.getKey();
            this.damagedToBlock.put(Character.valueOf((char)Block.BLOCK_STATE_IDS.get(c)), Character.valueOf((char)Block.BLOCK_STATE_IDS.get(entry.getValue())));
         }

         for(Entry<Character, String> entry : p.getMobIds().entrySet()) {
            Character c = (Character)entry.getKey();
            this.information.put(c, new CompiledPalette.Info((String)entry.getValue(), (String)null, (Map)null));
         }

         for(Entry<Character, String> entry : p.getLootTables().entrySet()) {
            Character c = (Character)entry.getKey();
            this.information.put(c, new CompiledPalette.Info((String)null, (String)entry.getValue(), (Map)null));
         }

         for(Entry<Character, Map<String, Integer>> entry : p.getTorchOrientations().entrySet()) {
            Character c = (Character)entry.getKey();
            this.information.put(c, new CompiledPalette.Info((String)null, (String)null, (Map)entry.getValue()));
         }
      }

   }

   public Set<Character> getCharacters() {
      return this.palette.keySet();
   }

   public IBlockState getStraight(char c) {
      try {
         Object o = this.palette.get(Character.valueOf(c));
         if (o instanceof IBlockState) {
            return (IBlockState)o;
         } else if (o instanceof Character) {
            return (IBlockState)Block.BLOCK_STATE_IDS.getByValue(((Character)o).charValue());
         } else {
            char[] randomBlocks = (char[])o;
            return (IBlockState)Block.BLOCK_STATE_IDS.getByValue(randomBlocks[0]);
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public boolean isSimple(char c) {
      Object o = this.palette.get(Character.valueOf(c));
      return o instanceof Character;
   }

   public Character get(char c, EaglercraftRandom rand) {
      try {
         Object o = this.palette.get(Character.valueOf(c));
         if (o instanceof Character) {
            return (Character)o;
         } else if (o == null) {
            return null;
         } else {
            char[] randomBlocks = (char[])o;
            return randomBlocks[rand.nextInt(128)];
         }
      } catch (Exception var5) {
         var5.printStackTrace();
         return null;
      }
   }

   public Character get(char c) {
      try {
         Object o = this.palette.get(Character.valueOf(c));
         if (o instanceof Character) {
            return (Character)o;
         } else if (o == null) {
            return null;
         } else {
            char[] randomBlocks = (char[])o;
            return randomBlocks[LostCitiesTerrainGenerator.fastrand128()];
         }
      } catch (Exception var4) {
         var4.printStackTrace();
         return null;
      }
   }

   public Character canBeDamagedToIronBars(Character b) {
      return (Character)this.damagedToBlock.get(b);
   }

   public CompiledPalette.Info getInfo(Character c) {
      return (CompiledPalette.Info)this.information.get(c);
   }

   public static class Info {
      private final String mobId;
      private final String loot;
      private final Map<String, Integer> torchOrientations;

      public Info(String mobId, String loot, Map<String, Integer> torchOrientations) {
         this.mobId = mobId;
         this.loot = loot;
         this.torchOrientations = torchOrientations;
      }

      public String getMobId() {
         return this.mobId;
      }

      public String getLoot() {
         return this.loot;
      }

      public Map<String, Integer> getTorchOrientations() {
         return this.torchOrientations;
      }
   }
}
