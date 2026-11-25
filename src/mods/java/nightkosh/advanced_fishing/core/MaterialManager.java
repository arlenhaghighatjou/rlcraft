package nightkosh.advanced_fishing.core;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.minecraft.block.material.Material;
import nightkosh.advanced_fishing.api.material.IMaterialManager;

public class MaterialManager implements IMaterialManager {
   public static final MaterialManager INSTANCE = new MaterialManager();
   public static final Set<Material> MATERIAL_SET = new HashSet();

   public MaterialManager() {
   }

   public void addMaterial(Material material) {
      MATERIAL_SET.add(material);
   }

   static {
      MATERIAL_SET.addAll(Arrays.asList(Material.WATER, Material.LAVA));
   }
}
