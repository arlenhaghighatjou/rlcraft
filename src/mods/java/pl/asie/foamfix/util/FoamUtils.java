package pl.asie.foamfix.util;

import java.lang.invoke.MethodHandle;
import net.minecraft.block.Block;
import net.minecraft.client.renderer.block.statemap.BlockStateMapper;
import net.minecraft.util.ResourceLocation;

public final class FoamUtils {
   public static final MethodHandle PMW_GET_PARENT;
   public static final MethodHandle MLR_GET_TEXTURES;
   public static final MethodHandle ML_LOAD_BLOCK;

   private FoamUtils() {
   }

   static {
      MethodHandle MLR_GET_TEXTURES_TMP = null;

      try {
         Class k = Class.forName("net.minecraftforge.client.model.ModelLoaderRegistry");
         MLR_GET_TEXTURES_TMP = MethodHandleHelper.findMethod(k, "getTextures", "getTextures");
      } catch (Exception var6) {
         var6.printStackTrace();
      }

      MLR_GET_TEXTURES = MLR_GET_TEXTURES_TMP;
      MethodHandle PMW_GET_PARENT_TMP = null;

      try {
         Class k = Class.forName("net.minecraftforge.client.model.PerspectiveMapWrapper");
         PMW_GET_PARENT_TMP = MethodHandleHelper.findFieldGetter(k, "parent");
      } catch (Exception var5) {
         var5.printStackTrace();
      }

      PMW_GET_PARENT = PMW_GET_PARENT_TMP;
      MethodHandle ML_LOAD_BLOCK_TMP = null;

      try {
         Class k = Class.forName("net.minecraft.client.renderer.block.model.ModelBakery");
         ML_LOAD_BLOCK_TMP = MethodHandleHelper.findMethod(k, "loadBlock", "loadBlock", BlockStateMapper.class, Block.class, ResourceLocation.class);
      } catch (Exception var4) {
         var4.printStackTrace();
      }

      ML_LOAD_BLOCK = ML_LOAD_BLOCK_TMP;
   }
}
