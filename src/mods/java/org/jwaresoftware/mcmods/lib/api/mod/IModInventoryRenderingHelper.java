package org.jwaresoftware.mcmods.lib.api.mod;

import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraftforge.fluids.Fluid;

public interface IModInventoryRenderingHelper {
   IModInventoryRenderingHelper noINSTANCE = new IModInventoryRenderingHelper() {
      public void doRenderSetupOrFail(String oid, Item singleton) {
         throw new UnsupportedOperationException("render_1");
      }

      public void doRenderSetupOrFail(String oid, Block singleton) {
         throw new UnsupportedOperationException("render_2");
      }

      public void doRenderSetupOrFail(String oid, Item singleton, String[] fmlids, int[] metas) {
         throw new UnsupportedOperationException("render_3");
      }

      public void doRenderSetupOrFail(String oid, Block singleton, String[] fmlids, int[] metas) {
         throw new UnsupportedOperationException("render_4");
      }

      public void doRenderFluidOrFail(String oid, Block singleton, Fluid fluid) {
         throw new UnsupportedOperationException("render_5");
      }
   };

   void doRenderSetupOrFail(String var1, Item var2);

   void doRenderSetupOrFail(String var1, Block var2);

   void doRenderSetupOrFail(String var1, Item var2, String[] var3, int[] var4);

   void doRenderSetupOrFail(String var1, Block var2, String[] var3, int[] var4);

   void doRenderFluidOrFail(String var1, Block var2, Fluid var3);
}
