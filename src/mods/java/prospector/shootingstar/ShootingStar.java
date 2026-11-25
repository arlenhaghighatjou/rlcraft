package prospector.shootingstar;

import java.util.ArrayList;
import java.util.List;
import prospector.shootingstar.model.ModelCompound;
import prospector.shootingstar.model.ModelMethods;

public class ShootingStar {
   protected static List<ModelCompound> modelList = new ArrayList();

   public ShootingStar() {
   }

   public static void registerModel(ModelCompound modelCompound) {
      modelList.add(modelCompound);
   }

   public static void registerModels(String modid) {
      for(ModelCompound compound : modelList) {
         if (compound.getModid().equals(modid)) {
            if (compound.isBlock()) {
               if (compound.getFileName().equals("shootingstar.undefinedfilename")) {
                  ModelMethods.registerItemModel(compound.getItem(), compound.getMeta(), compound.getBlockStatePath(), compound.getInventoryVariant());
               } else {
                  ModelMethods.registerItemModel(compound.getItem(), compound.getMeta(), compound.getFileName(), compound.getBlockStatePath(), compound.getInventoryVariant());
               }
            }

            if (compound.isBlock()) {
               if (compound.getFileName().equals("shootingstar.undefinedfilename")) {
                  ModelMethods.setBlockStateMapper(compound.getBlock(), compound.getBlockStatePath(), compound.getIgnoreProperties());
               } else {
                  ModelMethods.setBlockStateMapper(compound.getBlock(), compound.getFileName(), compound.getBlockStatePath(), compound.getIgnoreProperties());
               }
            }
         }
      }

   }
}
