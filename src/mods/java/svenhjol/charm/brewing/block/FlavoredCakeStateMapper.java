package svenhjol.charm.brewing.block;

import java.util.HashMap;
import net.minecraft.block.BlockCake;
import net.minecraft.block.state.IBlockState;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.block.statemap.StateMapperBase;
import net.minecraft.util.ResourceLocation;

public class FlavoredCakeStateMapper extends StateMapperBase {
   public static final FlavoredCakeStateMapper INSTANCE = new FlavoredCakeStateMapper();
   public static final ResourceLocation RES = new ResourceLocation("minecraft", "cake");
   public static HashMap<Integer, ModelResourceLocation> LOCATIONS = new HashMap();

   public FlavoredCakeStateMapper() {
   }

   protected ModelResourceLocation getModelResourceLocation(IBlockState state) {
      Integer bites = (Integer)state.getValue(BlockCake.BITES);
      if (LOCATIONS.get(bites) == null) {
         LOCATIONS.put(bites, new ModelResourceLocation(RES, "bites=" + bites.toString()));
      }

      return (ModelResourceLocation)LOCATIONS.get(bites);
   }
}
