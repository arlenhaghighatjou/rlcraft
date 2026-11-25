package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.weaponproperty.WeaponProperty;
import java.util.List;
import net.minecraft.item.Item;

public interface IWeaponPropertyContainer<T extends Item> {
   boolean hasWeaponProperty(WeaponProperty var1);

   T addWeaponProperty(WeaponProperty var1);

   WeaponProperty getFirstWeaponPropertyWithType(String var1);

   List<WeaponProperty> getAllWeaponPropertiesWithType(String var1);

   List<WeaponProperty> getAllWeaponProperties();

   ToolMaterialEx getMaterialEx();

   float getDirectAttackDamage();
}
