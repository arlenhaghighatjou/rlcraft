package com.oblivioussp.spartanweaponry.api;

import com.oblivioussp.spartanweaponry.api.weaponproperty.IPropertyCallback;
import java.util.List;
import net.minecraft.client.util.ITooltipFlag;
import net.minecraft.item.ItemStack;
import net.minecraft.world.World;

public interface IWeaponCallback extends IPropertyCallback {
   void onTooltip(ToolMaterialEx var1, ItemStack var2, World var3, List<String> var4, ITooltipFlag var5);
}
