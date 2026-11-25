package vazkii.arl.interf;

import net.minecraft.client.renderer.ItemMeshDefinition;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface IVariantHolder {
   String[] getVariants();

   @SideOnly(Side.CLIENT)
   default ItemMeshDefinition getCustomMeshDefinition() {
      return null;
   }

   default String getUniqueModel() {
      return null;
   }

   String getModNamespace();

   default String getPrefix() {
      return this.getModNamespace() + ":";
   }
}
