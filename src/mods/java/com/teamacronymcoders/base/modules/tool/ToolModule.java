package com.teamacronymcoders.base.modules.tool;

import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import net.minecraft.item.Item;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Module("base")
public class ToolModule extends ModuleBase {
   public static Item wrench;

   public ToolModule() {
   }

   public String getName() {
      return "Tool";
   }

   public void preInit(FMLPreInitializationEvent event) {
      wrench = new ItemWrench();
      this.getItemRegistry().register(wrench);
   }
}
