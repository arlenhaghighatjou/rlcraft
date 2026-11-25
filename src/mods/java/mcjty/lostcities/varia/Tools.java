package mcjty.lostcities.varia;

import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.block.Block;
import net.minecraft.block.state.IBlockState;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.tuple.Pair;

public class Tools {
   public Tools() {
   }

   public static IBlockState stringToState(String s) {
      if (s.contains("@")) {
         String[] split = StringUtils.split(s, '@');
         Block value = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(split[0]));
         if (value == null) {
            throw new RuntimeException("Cannot find block: '" + split[0] + "'!");
         } else {
            try {
               int meta = Integer.parseInt(split[1]);
               return value.getStateFromMeta(meta);
            } catch (NumberFormatException var4) {
               throw new RuntimeException("Bad meta for: '" + s + "'!");
            }
         }
      } else {
         Block value = (Block)ForgeRegistries.BLOCKS.getValue(new ResourceLocation(s));
         if (value == null) {
            throw new RuntimeException("Cannot find block: '" + s + "'!");
         } else {
            return value.getDefaultState();
         }
      }
   }

   public static String stateToString(IBlockState state) {
      int meta = state.getBlock().getMetaFromState(state);
      return meta == 0 ? state.getBlock().getRegistryName().toString() : state.getBlock().getRegistryName().toString() + "@" + meta;
   }

   public static String getRandomFromList(EaglercraftRandom random, List<Pair<Float, String>> list) {
      if (list.isEmpty()) {
         return null;
      } else {
         List<Pair<Float, String>> elements = new ArrayList();
         float totalweight = 0.0F;

         for(Pair<Float, String> pair : list) {
            elements.add(pair);
            totalweight += ((Float)pair.getKey()).floatValue();
         }

         float r = random.nextFloat() * totalweight;

         for(Pair<Float, String> pair : elements) {
            r -= ((Float)pair.getKey()).floatValue();
            if (r <= 0.0F) {
               return (String)pair.getRight();
            }
         }

         return null;
      }
   }
}
