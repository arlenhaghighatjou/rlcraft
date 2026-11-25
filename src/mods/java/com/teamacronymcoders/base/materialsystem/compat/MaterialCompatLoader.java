package com.teamacronymcoders.base.materialsystem.compat;

import com.teamacronymcoders.base.Base;
import com.teamacronymcoders.base.materialsystem.MaterialException;
import com.teamacronymcoders.base.util.ClassLoading;
import java.util.List;
import java.util.function.Consumer;
import net.minecraftforge.fml.common.discovery.ASMDataTable;

public class MaterialCompatLoader {
   private List<IMaterialCompat> materialCompatList;

   public MaterialCompatLoader() {
   }

   public void loadCompat(ASMDataTable dataTable) {
      this.materialCompatList = ClassLoading.<IMaterialCompat>getInstances(dataTable, MaterialCompat.class, IMaterialCompat.class);
   }

   public void doCompat() {
      this.materialCompatList.forEach((materialCompat) -> {
         try {
            materialCompat.doCompat();
         } catch (MaterialException var2) {
            Base.instance.getLogger().warning(var2.getMessage());
         }

      });
   }
}
