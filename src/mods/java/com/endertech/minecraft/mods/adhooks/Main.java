package com.endertech.minecraft.mods.adhooks;

import com.endertech.minecraft.forge.ForgeMod;
import com.endertech.minecraft.forge.ForgeLocating.Folders;
import com.endertech.minecraft.forge.configs.ModConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.materials.CraftingRecipe;
import com.endertech.minecraft.forge.units.UnitId;
import com.endertech.minecraft.mods.adhooks.init.HookType;
import com.endertech.minecraft.mods.adhooks.items.Launcher;
import com.endertech.minecraft.mods.adhooks.items.Properties;
import com.endertech.minecraft.mods.adhooks.motion.BoostUpMsg;
import com.endertech.minecraft.mods.adhooks.shot.HookShot;
import com.endertech.minecraft.mods.adhooks.shot.HookShotRender;
import net.minecraft.item.crafting.IRecipe;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.Mod.Instance;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.minecraftforge.registries.IForgeRegistryModifiable;

@Mod(
   modid = "adhooks",
   name = "Advanced Hook Launchers",
   version = "1.12.2-3.3.1.0",
   acceptedMinecraftVersions = "[1.12.2]",
   dependencies = "required-after:forgeendertech@[1.12.2-4.5.6.0,)"
)
public class Main extends ForgeMod {
   public static final String ID = "adhooks";
   public static final String NAME = "Advanced Hook Launchers";
   @Instance("adhooks")
   public static Main instance;

   public Main() {
   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent event) {
      this.printInitMsg(event);
      this.setConfig(new ModConfig(this.getId(), event));
      this.getRegistrator().registerNetMessageHandledOnServer(Launcher.KeyPressedMsg.class, Launcher.KeyPressedMsg.Handler.class);
      this.getRegistrator().registerNetMessageHandledOnClient(Properties.class, Properties.Handler.class);
      this.getRegistrator().registerNetMessageHandledOnClient(BoostUpMsg.class, BoostUpMsg.Handler.class);
      this.getRegistrator().registerEntity(HookShot.class, HookShot.RENDER_DISTANCE, 20, true);
      HookType.values();
      this.addPrototypeRecipe();
      this.addHookRecipe(HookType.PUDGE, "011 101 200", new String[]{"ingotIron", "stick"});
      this.addHookRecipe(HookType.SPEAR, "011 021 100", new String[]{"ingotIron", "stickWood"});
      this.addHookRecipe(HookType.WEB, "001 020 200", new String[]{"web", "string"});
      this.addLauncherRecipe(HookType.PUDGE, "gray", "yellow", "red");
      this.addLauncherRecipe(HookType.SPEAR, "white", "gray", "black");
      this.addLauncherRecipe(HookType.WEB, "lightBlue", "white", "white");
      super.preInit(event);
   }

   @EventHandler
   @SideOnly(Side.CLIENT)
   public void preInitClient(FMLPreInitializationEvent event) {
      this.getRegistrator().registerRenderingHandler(HookShot.class, new HookShotRender.Factory());
   }

   @EventHandler
   public void init(FMLInitializationEvent event) {
      this.printInitMsg(event);
      super.init(event);
   }

   @EventHandler
   public void postInit(FMLPostInitializationEvent event) {
      this.printInitMsg(event);
      super.postInit(event);
   }

   public String getId() {
      return "adhooks";
   }

   /** @deprecated */
   @Deprecated
   private void addWebRecipe() {
      String pattern = "101 010 101";
      String web = "web";
      UnitConfig config = new UnitConfig(this.getConfig().getConfigDir(), Folders.ITEMS.getName(), HookType.WEB.name());
      IForgeRegistryModifiable<IRecipe> modRegistry = (IForgeRegistryModifiable)ForgeRegistries.RECIPES;
      modRegistry.remove(new ResourceLocation(web));
      CraftingRecipe main = CraftingRecipe.CreateMain(config, pattern, web, 1, new String[]{"string"});
      this.getRegistrator().addCrafting(main);
      config.save();
   }

   private void addPrototypeRecipe() {
      HookType prototype = HookType.PROTOTYPE;
      prototype.launcher.addCraftingRecipe("321 211 113", 1, new String[]{"ingotIron", "stone_button", "repeater"});
      prototype.launcher.getConfig().save();
      this.getRegistrator().removeUnit(prototype.hook);
   }

   private void addHookRecipe(HookType hookType, String pattern, String[] mainComponents) {
      hookType.hook.addCraftingRecipe(pattern, 1, mainComponents);
      hookType.hook.saveConfig();
   }

   private void addLauncherRecipe(HookType hookType, String mainColor, String ringColor, String ropeColor) {
      String pattern = "542 413 036";
      String launcher = HookType.PROTOTYPE.launcher.getStringId();
      String hook = hookType.hook.getStringId();
      String string = "string";
      hookType.launcher.addCraftingRecipe(pattern, 1, new String[]{launcher, hook, string, this.getDye(mainColor), this.getDye(ringColor), this.getDye(ropeColor)});
      hookType.launcher.saveConfig();
   }

   private String getDye(String color) {
      return UnitId.buildDictName(new String[]{"dye", color});
   }

   public String getName() {
      return "Advanced Hook Launchers";
   }

   public ForgeMod getInstance() {
      return instance;
   }
}
