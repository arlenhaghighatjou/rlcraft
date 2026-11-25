package org.jwaresoftware.mcmods.armorunder.runtime;

import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.jwaresoftware.mcmods.armorunder.ModInfo;
import org.jwaresoftware.mcmods.lib.impl.ModInventoryModelHelperBase;

@SideOnly(Side.CLIENT)
class ModInventoryModelInit extends ModInventoryModelHelperBase {
   ModInventoryModelInit() {
      super(ModInfo.getInstance());
   }
}
