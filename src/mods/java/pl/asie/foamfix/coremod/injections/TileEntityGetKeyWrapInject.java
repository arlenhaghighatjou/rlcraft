package pl.asie.foamfix.coremod.injections;

import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.ResourceLocation;
import pl.asie.foamfix.FoamFix;

public class TileEntityGetKeyWrapInject {
   public TileEntityGetKeyWrapInject() {
   }

   public static ResourceLocation getKey(Class<? extends TileEntity> c) {
      ResourceLocation loc = (ResourceLocation)FoamFix.TILE_OVERRIDES.get(c);
      return loc != null ? loc : getKey_foamfix_old(c);
   }

   public static ResourceLocation getKey(Class<? extends TileEntity> c) {
      ResourceLocation loc = (ResourceLocation)FoamFix.TILE_OVERRIDES.get(c);
      return loc != null ? loc : getKey_foamfix_old(c);
   }

   public static ResourceLocation getKey_foamfix_old(Class<? extends TileEntity> c) {
      return null;
   }
}
