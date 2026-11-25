package com.teamacronymcoders.contenttweaker.modules.materials;

import com.teamacronymcoders.base.materialsystem.MaterialSystem;
import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.contenttweaker.api.utils.ResourceListCommand;
import com.teamacronymcoders.contenttweaker.modules.materials.brackethandler.MaterialPartBracketHandler;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Module("contenttweaker")
public class MaterialModule extends ModuleBase {
   public MaterialModule() {
   }

   public String getName() {
      return "Materials";
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      CTChatCommand.registerCommand(new ResourceListCommand("materialparts", MaterialSystem.getMaterialParts().keySet()));
      CTChatCommand.registerCommand(new ResourceListCommand("materials", MaterialSystem.getMaterials().keySet()));
      CTChatCommand.registerCommand(new ResourceListCommand("parts", MaterialSystem.getParts().keySet()));
      CTChatCommand.registerCommand(new ResourceListCommand("parttypes", MaterialSystem.getPartTypes().keySet()));
      CraftTweakerAPI.registerBracketHandler(new MaterialPartBracketHandler());
   }
}
