package tschipp.carryon.common.helper;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import net.minecraft.block.Block;
import net.minecraft.block.material.Material;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTBase;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.PotionEffect;
import net.minecraft.scoreboard.Score;
import net.minecraft.scoreboard.ScoreObjective;
import net.minecraft.scoreboard.Scoreboard;
import net.minecraft.util.math.BlockPos;

public class ScriptParseHelper {
   public ScriptParseHelper() {
   }

   public static boolean matches(double number, String cond) {
      if (cond == null) {
         return true;
      } else {
         try {
            if (cond.contains("<=")) {
               return number <= Double.parseDouble(cond.replace("<=", ""));
            } else if (cond.contains(">=")) {
               return number >= Double.parseDouble(cond.replace(">=", ""));
            } else if (cond.contains("<")) {
               return number < Double.parseDouble(cond.replace("<", ""));
            } else if (cond.contains(">")) {
               return number > Double.parseDouble(cond.replace(">", ""));
            } else if (cond.contains("=")) {
               return number == Double.parseDouble(cond.replace("=", ""));
            } else {
               return number == Double.parseDouble(cond);
            }
         } catch (Exception var4) {
            (new InvalidConfigException(var4.getMessage())).printException();
            return false;
         }
      }
   }

   public static boolean matches(Block block, String cond) {
      if (cond == null) {
         return true;
      } else {
         Block toCheck = StringParser.getBlock(cond);
         if (toCheck != null) {
            return block == toCheck;
         } else {
            return false;
         }
      }
   }

   public static boolean matches(NBTTagCompound toCheck, NBTTagCompound toMatch) {
      if (toCheck != null && toMatch != null) {
         boolean matching = true;

         for(String key : toMatch.getKeySet()) {
            NBTBase tag = toMatch.getTag(key);
            key = key.replace("\"", "");
            NBTBase tagToCheck = toCheck.getTag(key);
            if (!tag.equals(tagToCheck)) {
               matching = false;
            }
         }

         return matching;
      } else {
         return true;
      }
   }

   public static double[] getXYZArray(String s) {
      double[] d = new double[3];
      d[0] = getValueFromString(s, "x");
      d[1] = getValueFromString(s, "y");
      d[2] = getValueFromString(s, "z");
      return d;
   }

   public static double[] getScale(String s) {
      double[] d = new double[3];
      d[0] = getScaleValueFromString(s, "x");
      d[1] = getScaleValueFromString(s, "y");
      d[2] = getScaleValueFromString(s, "z");
      return d;
   }

   public static double getScaleValueFromString(String toGetFrom, String key) {
      if (toGetFrom == null) {
         return 1.0D;
      } else {
         String[] s = toGetFrom.split(",");

         for(String string : s) {
            if (string.contains(key) && string.contains("=")) {
               double numb = 1.0D;
               string = string.replace(key + "=", "");

               try {
                  numb = Double.parseDouble(string);
               } catch (Exception var10) {
                  ;
               }

               return numb;
            }
         }

         return 1.0D;
      }
   }

   public static boolean matchesScore(EntityPlayer player, String cond) {
      if (cond == null) {
         return true;
      } else {
         Scoreboard score = player.getWorldScoreboard();
         int iE = cond.indexOf("=");
         int iG = cond.indexOf(">");
         int iL = cond.indexOf("<");
         String numb;
         if (iG != -1 && (iE >= iG || iL != -1) && (iE >= iL || iE == -1)) {
            if (iE != -1 && (iG >= iE || iL != -1) && (iG >= iL || iG == -1)) {
               numb = cond.substring(iL);
            } else {
               numb = cond.substring(iG);
            }
         } else {
            numb = cond.substring(iE);
         }

         String scorename = cond.replace(numb, "");
         Map<ScoreObjective, Score> o = score.getObjectivesForEntity(player.getGameProfile().getName());
         if (o != null) {
            Score sc = (Score)o.get(score.getObjective(scorename));
            if (sc != null) {
               int points = sc.getScorePoints();
               return matches((double)points, numb);
            }
         }

         return false;
      }
   }

   public static boolean matches(BlockPos pos, String cond) {
      if (cond == null) {
         return true;
      } else {
         BlockPos blockpos = new BlockPos(getValueFromString(cond, "x"), getValueFromString(cond, "y"), getValueFromString(cond, "z"));
         BlockPos expand = new BlockPos(getValueFromString(cond, "dx"), getValueFromString(cond, "dy"), getValueFromString(cond, "dz"));
         BlockPos expanded = blockpos.add(expand);
         boolean x = pos.getX() >= blockpos.getX() && pos.getX() <= expanded.getX() || blockpos.getX() == 0;
         boolean y = pos.getY() >= blockpos.getY() && pos.getY() <= expanded.getY() || blockpos.getY() == 0;
         boolean z = pos.getZ() >= blockpos.getZ() && pos.getZ() <= expanded.getZ() || blockpos.getZ() == 0;
         return x && y && z;
      }
   }

   public static double getValueFromString(String toGetFrom, String key) {
      if (toGetFrom == null) {
         return 0.0D;
      } else {
         String[] s = toGetFrom.split(",");

         for(String string : s) {
            if (string.contains(key) && string.contains("=")) {
               double numb = 0.0D;
               string = string.replace(key + "=", "");

               try {
                  numb = Double.parseDouble(string);
               } catch (Exception var10) {
                  ;
               }

               return numb;
            }
         }

         return 0.0D;
      }
   }

   public static boolean hasEffects(EntityPlayer player, String cond) {
      if (cond == null) {
         return true;
      } else {
         Collection<PotionEffect> effects = player.getActivePotionEffects();
         String[] potions = cond.split(",");
         List<String> names = new ArrayList();
         List<Integer> levels = new ArrayList();

         for(int i = 0; i < potions.length; ++i) {
            String pot = potions[i];
            if (pot.contains("#")) {
               String level = pot.substring(pot.indexOf("#"));
               String name = pot.substring(0, pot.indexOf("#"));
               level = level.replace("#", "");
               int lev = 0;

               try {
                  lev = Integer.parseInt(level);
               } catch (Exception var13) {
                  ;
               }

               levels.add(Integer.valueOf(lev));
               names.add(name);
            } else {
               levels.add(Integer.valueOf(0));
               names.add(pot);
            }
         }

         int matches = 0;

         for(PotionEffect effect : effects) {
            int amp = effect.getAmplifier();
            String name = effect.getPotion().getRegistryName().toString();
            if (names.contains(name)) {
               int idx = names.indexOf(name);
               int lev = ((Integer)levels.get(idx)).intValue();
               if (lev == amp) {
                  ++matches;
               }
            }
         }

         return matches == potions.length;
      }
   }

   public static boolean matches(Material material, String cond) {
      if (cond == null) {
         return true;
      } else {
         byte var3 = -1;
         switch(cond.hashCode()) {
         case -1498088022:
            if (cond.equals("circuits")) {
               var3 = 6;
            }
            break;
         case -1368035283:
            if (cond.equals("cactus")) {
               var3 = 3;
            }
            break;
         case -1367592757:
            if (cond.equals("carpet")) {
               var3 = 5;
            }
            break;
         case -1237460601:
            if (cond.equals("ground")) {
               var3 = 15;
            }
            break;
         case -1218452597:
            if (cond.equals("redstone_light")) {
               var3 = 24;
            }
            break;
         case -1106736996:
            if (cond.equals("leaves")) {
               var3 = 19;
            }
            break;
         case -987991687:
            if (cond.equals("piston")) {
               var3 = 21;
            }
            break;
         case -985762968:
            if (cond.equals("plants")) {
               var3 = 22;
            }
            break;
         case -982480788:
            if (cond.equals("portal")) {
               var3 = 23;
            }
            break;
         case -895764774:
            if (cond.equals("sponge")) {
               var3 = 28;
            }
            break;
         case -608029735:
            if (cond.equals("dragon_egg")) {
               var3 = 10;
            }
            break;
         case -333143113:
            if (cond.equals("barrier")) {
               var3 = 2;
            }
            break;
         case 96586:
            if (cond.equals("air")) {
               var3 = 0;
            }
            break;
         case 104075:
            if (cond.equals("ice")) {
               var3 = 16;
            }
            break;
         case 115002:
            if (cond.equals("tnt")) {
               var3 = 30;
            }
            break;
         case 117588:
            if (cond.equals("web")) {
               var3 = 33;
            }
            break;
         case 3045944:
            if (cond.equals("cake")) {
               var3 = 4;
            }
            break;
         case 3056225:
            if (cond.equals("clay")) {
               var3 = 7;
            }
            break;
         case 3143222:
            if (cond.equals("fire")) {
               var3 = 11;
            }
            break;
         case 3241160:
            if (cond.equals("iron")) {
               var3 = 17;
            }
            break;
         case 3314400:
            if (cond.equals("lava")) {
               var3 = 18;
            }
            break;
         case 3506021:
            if (cond.equals("rock")) {
               var3 = 25;
            }
            break;
         case 3522692:
            if (cond.equals("sand")) {
               var3 = 26;
            }
            break;
         case 3535235:
            if (cond.equals("snow")) {
               var3 = 27;
            }
            break;
         case 3619754:
            if (cond.equals("vine")) {
               var3 = 31;
            }
            break;
         case 3655341:
            if (cond.equals("wood")) {
               var3 = 34;
            }
            break;
         case 92975308:
            if (cond.equals("anvil")) {
               var3 = 1;
            }
            break;
         case 94756378:
            if (cond.equals("cloth")) {
               var3 = 8;
            }
            break;
         case 94848049:
            if (cond.equals("coral")) {
               var3 = 9;
            }
            break;
         case 98436988:
            if (cond.equals("glass")) {
               var3 = 12;
            }
            break;
         case 98545535:
            if (cond.equals("gourd")) {
               var3 = 13;
            }
            break;
         case 98615734:
            if (cond.equals("grass")) {
               var3 = 14;
            }
            break;
         case 112903447:
            if (cond.equals("water")) {
               var3 = 32;
            }
            break;
         case 140908100:
            if (cond.equals("packed_ice")) {
               var3 = 20;
            }
            break;
         case 1699106016:
            if (cond.equals("structure_void")) {
               var3 = 29;
            }
         }

         switch(var3) {
         case 0:
            return material == Material.AIR;
         case 1:
            return material == Material.ANVIL;
         case 2:
            return material == Material.BARRIER;
         case 3:
            return material == Material.CACTUS;
         case 4:
            return material == Material.CAKE;
         case 5:
            return material == Material.CARPET;
         case 6:
            return material == Material.CIRCUITS;
         case 7:
            return material == Material.CLAY;
         case 8:
            return material == Material.CLOTH;
         case 9:
            return material == Material.CORAL;
         case 10:
            return material == Material.DRAGON_EGG;
         case 11:
            return material == Material.FIRE;
         case 12:
            return material == Material.GLASS;
         case 13:
            return material == Material.GOURD;
         case 14:
            return material == Material.GRASS;
         case 15:
            return material == Material.GROUND;
         case 16:
            return material == Material.ICE;
         case 17:
            return material == Material.IRON;
         case 18:
            return material == Material.LAVA;
         case 19:
            return material == Material.LEAVES;
         case 20:
            return material == Material.PACKED_ICE;
         case 21:
            return material == Material.PISTON;
         case 22:
            return material == Material.PLANTS;
         case 23:
            return material == Material.PORTAL;
         case 24:
            return material == Material.REDSTONE_LIGHT;
         case 25:
            return material == Material.ROCK;
         case 26:
            return material == Material.SAND;
         case 27:
            return material == Material.SNOW;
         case 28:
            return material == Material.SPONGE;
         case 29:
            return material == Material.STRUCTURE_VOID;
         case 30:
            return material == Material.TNT;
         case 31:
            return material == Material.VINE;
         case 32:
            return material == Material.WATER;
         case 33:
            return material == Material.WEB;
         case 34:
            return material == Material.WOOD;
         default:
            return false;
         }
      }
   }
}
