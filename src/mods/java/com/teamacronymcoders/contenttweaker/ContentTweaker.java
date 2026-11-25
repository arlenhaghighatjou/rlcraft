package com.teamacronymcoders.contenttweaker;

import com.teamacronymcoders.base.BaseModFoundation;
import com.teamacronymcoders.base.subblocksystem.SubBlockSystem;
import com.teamacronymcoders.base.util.OreDictUtils;
import com.teamacronymcoders.contenttweaker.api.ContentTweakerAPI;
import crafttweaker.CraftTweakerAPI;
import net.minecraft.creativetab.CreativeTabs;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

@Mod(
   modid = "contenttweaker",
   name = "ContentTweaker",
   version = "1.12.2-4.10.0",
   dependencies = "required-after:base@[3.11.0,);required-after:crafttweaker;after:chickens;after:tconstruct",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ContentTweaker extends BaseModFoundation<ContentTweaker> {
   public static final String MOD_ID = "contenttweaker";
   public static final String MOD_NAME = "ContentTweaker";
   public static final String VERSION = "1.12.2-4.10.0";
   public static final String DEPENDS = "required-after:base@[3.11.0,);required-after:crafttweaker;after:chickens;after:tconstruct";
   @Instance("contenttweaker")
   public static ContentTweaker instance;
   public static boolean scriptsSuccessful;

   public ContentTweaker() {
      super("contenttweaker", "ContentTweaker", "1.12.2-4.10.0", (CreativeTabs)null, false);
      ContentTweakerAPI.setInstance(new ContentTweakerAPI(new ModWrapper()));
      this.subBlockSystem = new SubBlockSystem(this);
      this.materialUser = new CTMaterialUser(this);
      OreDictUtils.addDefaultModId("contenttweaker");
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      super.preInit(event);
   }

   public void afterModuleHandlerInit(FMLPreInitializationEvent event) {
      scriptsSuccessful = CraftTweakerAPI.tweaker.loadScript(false, "contenttweaker");
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      super.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      super.postInit(event);
   }

   public boolean addOBJDomain() {
      return true;
   }

   public boolean hasExternalResources() {
      return true;
   }

   public ContentTweaker getInstance() {
      return this;
   }
}
