package fr.samlegamer.spartandefiled.utils;

import com.oblivioussp.spartanweaponry.api.ToolMaterialEx;
import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import java.util.ArrayList;

public class SDMatConvert {
   public final ToolMaterialEx material;
   public final String name;
   public final ArrayList<WeaponProperty> properties;

   public SDMatConvert(String name, ToolMaterialEx material, WeaponProperty... properties) {
      this.name = name;
      this.material = material;
      this.properties = new ArrayList();

      for(WeaponProperty prop : properties) {
         this.properties.add(prop);
      }

   }
}
