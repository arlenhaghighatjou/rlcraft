package com.teamacronymcoders.contenttweaker.modules.vanilla;

import com.teamacronymcoders.base.modulesystem.Module;
import com.teamacronymcoders.base.modulesystem.ModuleBase;
import com.teamacronymcoders.contenttweaker.api.ContentTweakerAPI;
import com.teamacronymcoders.contenttweaker.api.utils.ResourceListCommand;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.BlockBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.creativetab.CreativeTabBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.materials.MaterialBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds.SoundEventBracketHandler;
import com.teamacronymcoders.contenttweaker.modules.vanilla.resources.sounds.SoundTypeBracketHandler;
import crafttweaker.CraftTweakerAPI;
import crafttweaker.mc1120.commands.CTChatCommand;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Module("contenttweaker")
public class VanillaModule extends ModuleBase {
   public VanillaModule() {
   }

   public String getName() {
      return "Vanilla CraftTweaker";
   }

   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
      CraftTweakerAPI.registerBracketHandler(new MaterialBracketHandler());
      CTChatCommand.registerCommand(new ResourceListCommand("blockmaterial", ContentTweakerAPI.getInstance().getBlockMaterials().getAllNames()));
      CraftTweakerAPI.registerBracketHandler(new CreativeTabBracketHandler());
      CTChatCommand.registerCommand(new ResourceListCommand("creativetab", ContentTweakerAPI.getInstance().getCreativeTabs().getAllNames()));
      CraftTweakerAPI.registerBracketHandler(new SoundEventBracketHandler());
      CTChatCommand.registerCommand(new ResourceListCommand("soundevent", ContentTweakerAPI.getInstance().getSoundTypes().getAllNames()));
      CraftTweakerAPI.registerBracketHandler(new SoundTypeBracketHandler());
      CTChatCommand.registerCommand(new ResourceListCommand("soundtype", ContentTweakerAPI.getInstance().getSoundTypes().getAllNames()));
      CraftTweakerAPI.registerBracketHandler(new BlockBracketHandler());
   }
}
