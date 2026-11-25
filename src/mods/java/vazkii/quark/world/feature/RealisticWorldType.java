package vazkii.quark.world.feature;

import net.minecraft.client.gui.GuiCreateWorld;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Pre;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.world.world.WorldTypeRealistic;

public class RealisticWorldType extends Feature {
   public static WorldType realistic;
   public static boolean makeRealisticDefault;
   public static double realisticCloudHeight;

   public RealisticWorldType() {
   }

   public void setupConfig() {
      makeRealisticDefault = this.loadPropBool("Make Realistic Default", "Makes realistic the default world type. Only works for singleplayer.", false);
      realisticCloudHeight = this.loadPropDouble("Realistic Cloud Height", "What cloud height should realistic worlds have? 128 is default for vanilla worlds.", 260.0D);
   }

   public void postInit() {
      realistic = new WorldTypeRealistic("quark_realistic");
   }

   public String getFeatureDescription() {
      return "Allows for usage of a new Realistic world type, made by /u/Soniop.\nhttps://www.reddit.com/r/Minecraft/comments/4nfw3t/more_realistic_generation_preset_in_comment/\nIf you want to use it in multiplayer, set the world type to \"quark_realistic\"";
   }

   @SubscribeEvent
   @SideOnly(Side.CLIENT)
   public void openGUI(Pre event) {
      if (makeRealisticDefault && event.getGui() instanceof GuiCreateWorld) {
         GuiCreateWorld create = (GuiCreateWorld)event.getGui();
         int index = ((Integer)ObfuscationReflectionHelper.getPrivateValue(GuiCreateWorld.class, create, "selectedIndex")).intValue();
         if (index == WorldType.DEFAULT.getId()) {
            ObfuscationReflectionHelper.setPrivateValue(GuiCreateWorld.class, create, Integer.valueOf(realistic.getId()), "selectedIndex");
         }
      }

   }

   public boolean requiresMinecraftRestartToEnable() {
      return true;
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }
}
