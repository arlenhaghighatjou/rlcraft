package com.endertech.minecraft.forge.units;

import com.endertech.minecraft.forge.blocks.BlockState;
import java.util.HashSet;

public class StateHashSet<T extends IWithMeta<?>> extends HashSet<T> {
   private static final long serialVersionUID = 8628983548946325905L;

   public StateHashSet() {
   }

   public boolean contains(Object o) {
      if (super.contains(o)) {
         return true;
      } else {
         return o instanceof IWithMeta ? super.contains(((IWithMeta)o).withMetaAll()) : false;
      }
   }

   public static StateHashSet<BlockState> parseBlockStatesFrom(String[] strArray) {
      StateHashSet<BlockState> set = new StateHashSet<BlockState>();

      for(String str : strArray) {
         str = str.trim();
         if (!str.isEmpty()) {
            UnitId id = UnitId.from(str);
            set.addAll(id.getAllBlockStates());
         }
      }

      return set;
   }
}
