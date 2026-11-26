package org.jwaresoftware.mcmods.armorunder;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.crafting.CraftingHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.common.FMLCommonHandler;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStoppingEvent;
import org.jwaresoftware.mcmods.lib.api.mod.IModRuntime;

@Mod(
   modid = "armorunder",
   name = "Armor Underwear",
   version = "1.0.0",
   acceptedMinecraftVersions = "[1.12,)",
   updateJSON = "http://jwaresoftware.org/files/mcmods/versions/armorunder.json",
   dependencies = "required-after:forge@[14.23.4.2705,);required-after:carrots@[1.0.0b1]"
)
public final class ArmorUnderwear {
   public static ArmorUnderwear instance;
   public static IModRuntime runtime;

   public ArmorUnderwear() {
   }

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         instance = this;
         runtime = FMLCommonHandler.instance().getSide() == Side.CLIENT ? new org.jwaresoftware.mcmods.armorunder.runtime.ModRuntimeSP() : new org.jwaresoftware.mcmods.armorunder.runtime.ModRuntimeDS();
         org.jwaresoftware.mcmods.armorunder.recipes.TLinedArmorRecipe.registerHolders();
         CraftingHelper.register(new ResourceLocation("armorunder", "compat_mode"), new org.jwaresoftware.mcmods.armorunder.recipes.IsSupercompatibilityModeConditionFactory());
         CraftingHelper.register(new ResourceLocation("armorunder", "is_allowed"), new org.jwaresoftware.mcmods.armorunder.recipes.ConfigAllowsConditionFactory());
         CraftingHelper.register(new ResourceLocation("armorunder", "lined_armor_shapeless"), new org.jwaresoftware.mcmods.armorunder.recipes.TLinedArmorRecipe.Factory());
         CraftingHelper.register(new ResourceLocation("armorunder", "unlined_armor_shapeless"), new org.jwaresoftware.mcmods.armorunder.recipes.StripArmorTLiningRecipe.Factory());
         CraftingHelper.register(new ResourceLocation("armorunder", "autoreg_armor_shapeless"), new org.jwaresoftware.mcmods.armorunder.recipes.AutoregLinedArmorRecipe.Factory());
         CraftingHelper.register(new ResourceLocation("armorunder", "autoreg_lining_shaped"), new org.jwaresoftware.mcmods.armorunder.recipes.BuffAutoregLiningRecipe.Factory());
         CraftingHelper.register(new ResourceLocation("armorunder", "barrier_shielded_armor_shapeless"), new org.jwaresoftware.mcmods.armorunder.recipes.BarrierReinforcedArmorRecipe.Factory());
      } else if (event instanceof FMLPreInitializationEvent) {
         this.preInit((FMLPreInitializationEvent)event);
      } else if (event instanceof FMLInitializationEvent) {
         this.init((FMLInitializationEvent)event);
      } else if (event instanceof FMLPostInitializationEvent) {
         this.postInit((FMLPostInitializationEvent)event);
      } else if (event instanceof FMLServerStoppingEvent) {
         this.stop((FMLServerStoppingEvent)event);
      }

   }

   public void preInit(FMLPreInitializationEvent event) {
      runtime.beginStartUp(event);
      runtime.getConfig().persist();
   }

   public void init(FMLInitializationEvent event) {
      runtime.midStartUp(event);
   }

   public void postInit(FMLPostInitializationEvent event) {
      runtime.finishStartUp(event);
   }

   public void stop(FMLServerStoppingEvent event) {
      runtime.shutDown(event);
   }
}
