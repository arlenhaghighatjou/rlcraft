package vazkii.quark.client.feature;

import javax.annotation.Nonnull;
import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.init.Items;
import net.minecraft.item.ItemStack;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.util.ModelHandler;
import vazkii.quark.base.module.Feature;

public class LessIntrusiveShields extends Feature {
   public LessIntrusiveShields() {
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      ModelHandler.registerModels(Items.SHIELD, "quark:", new String[]{"shield_override"}, (String)null, true);
      ModelLoader.setCustomMeshDefinition(Items.SHIELD, new ItemMeshDefinition() {
         @Nonnull
         public ModelResourceLocation getModelLocation(@Nonnull ItemStack stack) {
            return new ModelResourceLocation("quark:shield_override", "inventory");
         }
      });
   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }
}
