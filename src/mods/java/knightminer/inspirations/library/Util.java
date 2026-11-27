package knightminer.inspirations.library;

import net.lax1dude.eaglercraft.v1_8.HString;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import knightminer.inspirations.library.util.ReflectionUtil;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.init.Blocks;
import net.minecraft.init.Items;
import net.minecraft.item.EnumDyeColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.potion.PotionType;
import net.minecraft.util.NonNullList;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.oredict.OreDictionary;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;

public class Util {
   public Util() {
   }

   public static String resource(String name) {
      return HString.format("%s:%s", "inspirations", name.toLowerCase(Locale.US));
   }

   public static String prefix(String name) {
      return HString.format("%s.%s", "inspirations", name.toLowerCase(Locale.US));
   }

   public static ResourceLocation getResource(String res) {
      return new ResourceLocation("inspirations", res);
   }

   public static String translate(String key, Object... pars) {
      return I18n.translateToLocal(I18n.translateToLocal(HString.format(key, pars)).trim()).trim();
   }

   public static String translateFormatted(String key, Object... pars) {
      return I18n.translateToLocal(I18n.translateToLocalFormatted(key, pars).trim()).trim();
   }

   public static boolean canTranslate(String key) {
      return I18n.canTranslate(key);
   }

   public static Logger getLogger(String type) {
      String log = "inspirations";
      return LogManager.getLogger(log + "-" + type);
   }

   public static boolean clickedAABB(AxisAlignedBB aabb, float hitX, float hitY, float hitZ) {
      return aabb.minX <= (double)hitX && (double)hitX <= aabb.maxX && aabb.minY <= (double)hitY && (double)hitY <= aabb.maxY && aabb.minZ <= (double)hitZ && (double)hitZ <= aabb.maxZ;
   }

   public static ItemStack getStackFromState(@Nullable IBlockState state) {
      if (state == null) {
         return ItemStack.EMPTY;
      } else {
         Block block = state.getBlock();
         if (block == Blocks.AIR) {
            return ItemStack.EMPTY;
         } else {
            ItemStack drop = ReflectionUtil.invokeGetSilkTouchDrop(block, state);
            if (drop != null) {
               return drop;
            } else {
               InspirationsRegistry.log.error("Failed to get silk touch drop for {}, using fallback", state);
               Item item = Item.getItemFromBlock(block);
               if (item == Items.AIR) {
                  return ItemStack.EMPTY;
               } else {
                  int meta = block.damageDropped(state);
                  return new ItemStack(item, 1, meta);
               }
            }
         }
      }
   }

   @SafeVarargs
   public static <E> NonNullList<E> createNonNullList(E... elements) {
      NonNullList<E> list = NonNullList.create();
      list.addAll(Arrays.asList(elements));
      return list;
   }

   public static int combineColors(int color1, int color2, int scale) {
      if (scale == 0) {
         return color1;
      } else {
         int a = color1 >> 24 & 255;
         int r = color1 >> 16 & 255;
         int g = color1 >> 8 & 255;
         int b = color1 & 255;
         int a2 = color2 >> 24 & 255;
         int r2 = color2 >> 16 & 255;
         int g2 = color2 >> 8 & 255;
         int b2 = color2 & 255;

         for(int i = 0; i < scale; ++i) {
            a = (int)Math.sqrt((double)(a * a2));
            r = (int)Math.sqrt((double)(r * r2));
            g = (int)Math.sqrt((double)(g * g2));
            b = (int)Math.sqrt((double)(b * b2));
         }

         return a << 24 | r << 16 | g << 8 | b;
      }
   }

   public static float[] getColorComponents(int color) {
      int i = (color & 16777215) >> 16;
      int j = (color & '\uffff') >> 8;
      int k = color & 255;
      return new float[]{(float)i / 255.0F, (float)j / 255.0F, (float)k / 255.0F};
   }

   public static int getColorInteger(@Nonnull float[] component) {
      return ((int)(component[0] * 255.0F) & 255) << 16 | ((int)(component[1] * 255.0F) & 255) << 8 | (int)(component[2] * 255.0F) & 255;
   }

   public static void addPotionTooltip(PotionType potionType, List<String> lores) {
      List<PotionEffect> effects = potionType.getEffects();
      if (effects.isEmpty()) {
         String s = translate("effect.none").trim();
         lores.add(TextFormatting.GRAY + s);
      } else {
         for(PotionEffect effect : effects) {
            String effectString = translate(effect.getEffectName()).trim();
            Potion potion = effect.getPotion();
            if (effect.getAmplifier() > 0) {
               effectString = effectString + " " + translate("potion.potency." + effect.getAmplifier()).trim();
            }

            if (effect.getDuration() > 20) {
               effectString = effectString + " (" + Potion.getPotionDurationString(effect, 1.0F) + ")";
            }

            lores.add((potion.isBadEffect() ? TextFormatting.RED : TextFormatting.BLUE) + effectString);
         }

      }
   }

   public static EnumDyeColor getDyeForColor(int color) {
      for(EnumDyeColor dyeColor : EnumDyeColor.values()) {
         if (dyeColor.colorValue == color) {
            return dyeColor;
         }
      }

      return null;
   }

   public static RayTraceResult closestResult(List<RayTraceResult> list, Vec3d end) {
      RayTraceResult closest = null;
      double max = 0.0D;

      for(RayTraceResult raytraceresult : list) {
         if (raytraceresult != null) {
            double distance = raytraceresult.hitVec.squareDistanceTo(end);
            if (distance > max) {
               closest = raytraceresult;
               max = distance;
            }
         }
      }

      return closest;
   }

   public static boolean oreMatches(ItemStack stack, @Nonnull String ore) {
      if (stack.isEmpty()) {
         return false;
      } else {
         for(int id : OreDictionary.getOreIDs(stack)) {
            if (ore.equals(OreDictionary.getOreName(id))) {
               return true;
            }
         }

         return false;
      }
   }
}
