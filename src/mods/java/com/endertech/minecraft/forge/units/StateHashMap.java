package com.endertech.minecraft.forge.units;

import com.endertech.common.CommonString;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.blocks.BlockState;
import java.util.concurrent.ConcurrentHashMap;
import net.minecraft.block.state.IBlockState;

public class StateHashMap<K extends IWithMeta<?>, V> extends ConcurrentHashMap<K, V> {
   private static final long serialVersionUID = 8943547197335795989L;

   public StateHashMap() {
   }

   public V get(Object key) {
      V value = (V)super.get(key);
      if (value == null && key instanceof IWithMeta) {
         value = (V)super.get(((IWithMeta)key).withMetaAll());
      }

      return value;
   }

   public static StateHashMap<BlockState, IBlockState> parseReplacementsFrom(String[] strArray) {
      StateHashMap<BlockState, IBlockState> replacements = new StateHashMap<BlockState, IBlockState>();

      for(String str : strArray) {
         str = str.trim();
         if (!str.isEmpty()) {
            String[] split = str.split("->");
            boolean error = split.length < 2;
            if (!error) {
               UnitId source = UnitId.from(split[0].trim());
               UnitId dest = UnitId.from(split[1].trim());
               error = source.isEmpty() || dest.isEmpty();
               if (!error) {
                  IBlockState replacement = null;

                  try {
                     replacement = dest.getIBlockState();
                  } catch (Exception var13) {
                     error = true;
                  }

                  if (replacement != null) {
                     for(BlockState state : source.getAllBlockStates()) {
                        replacements.put(state, replacement);
                     }
                  }
               }
            }

            if (error) {
               ForgeMain.instance.getLogger().error("Unable to parse replacement from " + CommonString.doubleQuoted(str));
            }
         }
      }

      return replacements;
   }
}
