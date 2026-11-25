package shadows.toaster;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import net.lax1dude.eaglercraft.v1_8.internal.KeyboardConstants;
import net.lax1dude.eaglercraft.v1_8.log4j.LogManager;
import net.lax1dude.eaglercraft.v1_8.log4j.Logger;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.common.config.ConfigEntry;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLConstructionEvent;
import net.minecraftforge.fml.common.event.FMLEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;

@Mod(
   modid = "toastcontrol",
   version = "1.8.1",
   name = "Toast Control",
   clientSideOnly = true,
   dependencies = "required-after:placebo@[1.6,)",
   acceptedMinecraftVersions = "[1.12.2]"
)
public class ToastControl {
   public static final String MODID = "toastcontrol";
   public static final String MODNAME = "Toast Control";
   public static final String VERSION = "1.8.1";
   public static final String DEPS = "required-after:placebo@[1.6,)";
   public static final Logger LOGGER = LogManager.getLogger(MODID);
   public static final KeyBinding CLEAR = new KeyBinding("key.toastcontrol.clear", KeyboardConstants.KEY_J, "key.toastcontrol.category");
   public static final ResourceLocation TRANSLUCENT = new ResourceLocation(MODID, "textures/gui/toasts.png");
   public static final ResourceLocation TRANSPARENT = new ResourceLocation(MODID, "textures/gui/toasts2.png");
   static final Set<String> BLOCKED_CLASSES = new HashSet<>();
   private BetterGuiToast gui;

   public void handleModStateEvent(FMLEvent event) {
      if (event instanceof FMLConstructionEvent) {
         ToastControlConfig.register();
      } else if (event instanceof FMLPreInitializationEvent) {
         this.gui = new BetterGuiToast();
         Minecraft.getMinecraft().setToastGui(this.gui);
         handleBlockedClasses();
         ClientRegistry.registerKeyBinding(CLEAR);
         MinecraftForge.EVENT_BUS.register(KeyInputEvent.class, this::keys);
         MinecraftForge.EVENT_BUS.register(ClientTickEvent.class, this::clientTick);
         MinecraftForge.EVENT_BUS.register(OnConfigChangedEvent.class, this::onConfigChanged);
      }
   }

   public void keys(KeyInputEvent event) {
      if (CLEAR.isKeyDown() && CLEAR.isPressed()) {
         this.gui.clear();
      }
   }

   private static void handleBlockedClasses() {
      BLOCKED_CLASSES.clear();
      BLOCKED_CLASSES.addAll(Arrays.asList(ToastControlConfig.blockedClasses));
   }

   public void clientTick(ClientTickEvent event) {
      if (event.phase == Phase.END) {
         this.gui.tick();
      }
   }

   public void onConfigChanged(OnConfigChangedEvent event) {
      if (MODID.equals(event.getModID())) {
         ConfigManager.sync(MODID, Type.INSTANCE);
         handleBlockedClasses();
         this.gui.updateToastCount();
         LOGGER.info("Toast control config reloaded.");
      }
   }


   @Config(
      modid = "toastcontrol",
      category = "Toast Types"
   )
   public static class ToastControlConfig {
      @Name("Disable Advancements")
      @Comment({"If advancement toasts are blocked. Enabling will block ALL advancements."})
      public static boolean advancements = true;
      @Name("Disable Recipes")
      @Comment({"If recipe unlock toasts are blocked. Blocks \"you have unlocked a new recipe\" toasts."})
      public static boolean recipes = true;
      @Name("Disable System Toasts")
      @Comment({"If system toasts are blocked. This is used only for the narrator toggle notification right now."})
      public static boolean system = false;
      @Name("Disable Tutorials")
      @Comment({"If tutorial toasts are blocked. Blocks useless things like use WASD to move."})
      public static boolean tutorial = true;
      @Name("Disable All Vanilla")
      @Comment({"If all vanilla toasts are blocked.  Includes advancements, recipes, system, and tutorials."})
      public static boolean globalVanilla = false;
      @Name("Disable All Non-Vanilla")
      @Comment({"If all non-vanilla toasts are blocked.  Blocks all toasts that do not extend vanilla classes."})
      public static boolean globalModded = false;
      @Name("Disable All")
      @Comment({"If all toasts are blocked."})
      public static boolean global = false;
      @Name("Forced Display Time")
      @Comment({"How long (in ticks) to force a toast to show for.  Higher is longer."})
      public static int forceTime = 0;
      @Name("Translucent Toasts")
      @Comment({"If toasts are slightly translucent."})
      public static boolean translucent = true;
      @Name("Transparent Toasts")
      @Comment({"If toasts do not draw a background."})
      public static boolean transparent = false;
      @Name("Max Toasts Shown")
      @Comment({"The maximum number of toasts on the screen at once.  Default 3, Vanilla uses 5."})
      @RangeInt(
         min = 1,
         max = 7
      )
      public static int toastCount = 3;
      @Name("Print Toast Classnames")
      @Comment({"A debug config to print the class of each toast that tries to enter the GUI.  Useful for finding classes to block."})
      public static boolean printClasses = false;
      @Name("Blacklisted Classes")
      @Comment({"A Class-specific blacklist for toasts.  Insert class names."})
      public static String[] blockedClasses = new String[0];
      @Name("Toast X Offset")
      @Comment({"The amount to offset a toast in the x axis."})
      public static int offsetX = 0;
      @Name("Toast Y Offset")
      @Comment({"The amount to offset a toast in the y axis."})
      public static int offsetY = 0;
      @Name("Disable Transitions")
      @Comment({"Set to true to disable toasts sliding in to view."})
      public static boolean noSlide = false;
      @Name("Transition from Left")
      @Comment({"Set to true to change the transition to start from the left."})
      public static boolean startLeft = false;

      public ToastControlConfig() {
      }

      public static void register() {
         ConfigManager.register("toastcontrol", "", "Toast Types", null,
            ConfigEntry.value("Disable Advancements", boolean.class, () -> ToastControlConfig.advancements, v -> ToastControlConfig.advancements = (Boolean)v).comment("If advancement toasts are blocked. Enabling will block ALL advancements.").langKey("toastcontrol.toast types.advancements"),
            ConfigEntry.value("Disable Recipes", boolean.class, () -> ToastControlConfig.recipes, v -> ToastControlConfig.recipes = (Boolean)v).comment("If recipe unlock toasts are blocked. Blocks \"you have unlocked a new recipe\" toasts.").langKey("toastcontrol.toast types.recipes"),
            ConfigEntry.value("Disable System Toasts", boolean.class, () -> ToastControlConfig.system, v -> ToastControlConfig.system = (Boolean)v).comment("If system toasts are blocked. This is used only for the narrator toggle notification right now.").langKey("toastcontrol.toast types.system"),
            ConfigEntry.value("Disable Tutorials", boolean.class, () -> ToastControlConfig.tutorial, v -> ToastControlConfig.tutorial = (Boolean)v).comment("If tutorial toasts are blocked. Blocks useless things like use WASD to move.").langKey("toastcontrol.toast types.tutorial"),
            ConfigEntry.value("Disable All Vanilla", boolean.class, () -> ToastControlConfig.globalVanilla, v -> ToastControlConfig.globalVanilla = (Boolean)v).comment("If all vanilla toasts are blocked.  Includes advancements, recipes, system, and tutorials.").langKey("toastcontrol.toast types.globalvanilla"),
            ConfigEntry.value("Disable All Non-Vanilla", boolean.class, () -> ToastControlConfig.globalModded, v -> ToastControlConfig.globalModded = (Boolean)v).comment("If all non-vanilla toasts are blocked.  Blocks all toasts that do not extend vanilla classes.").langKey("toastcontrol.toast types.globalmodded"),
            ConfigEntry.value("Disable All", boolean.class, () -> ToastControlConfig.global, v -> ToastControlConfig.global = (Boolean)v).comment("If all toasts are blocked.").langKey("toastcontrol.toast types.global"),
            ConfigEntry.value("Forced Display Time", int.class, () -> ToastControlConfig.forceTime, v -> ToastControlConfig.forceTime = (Integer)v).comment("How long (in ticks) to force a toast to show for.  Higher is longer.").langKey("toastcontrol.toast types.forcetime"),
            ConfigEntry.value("Translucent Toasts", boolean.class, () -> ToastControlConfig.translucent, v -> ToastControlConfig.translucent = (Boolean)v).comment("If toasts are slightly translucent.").langKey("toastcontrol.toast types.translucent"),
            ConfigEntry.value("Transparent Toasts", boolean.class, () -> ToastControlConfig.transparent, v -> ToastControlConfig.transparent = (Boolean)v).comment("If toasts do not draw a background.").langKey("toastcontrol.toast types.transparent"),
            ConfigEntry.value("Max Toasts Shown", int.class, () -> ToastControlConfig.toastCount, v -> ToastControlConfig.toastCount = (Integer)v).comment("The maximum number of toasts on the screen at once.  Default 3, Vanilla uses 5.").langKey("toastcontrol.toast types.toastcount").range(1, 7),
            ConfigEntry.value("Print Toast Classnames", boolean.class, () -> ToastControlConfig.printClasses, v -> ToastControlConfig.printClasses = (Boolean)v).comment("A debug config to print the class of each toast that tries to enter the GUI.  Useful for finding classes to block.").langKey("toastcontrol.toast types.printclasses"),
            ConfigEntry.value("Blacklisted Classes", String[].class, () -> ToastControlConfig.blockedClasses, v -> ToastControlConfig.blockedClasses = (String[])v).comment("A Class-specific blacklist for toasts.  Insert class names.").langKey("toastcontrol.toast types.blockedclasses"),
            ConfigEntry.value("Toast X Offset", int.class, () -> ToastControlConfig.offsetX, v -> ToastControlConfig.offsetX = (Integer)v).comment("The amount to offset a toast in the x axis.").langKey("toastcontrol.toast types.offsetx"),
            ConfigEntry.value("Toast Y Offset", int.class, () -> ToastControlConfig.offsetY, v -> ToastControlConfig.offsetY = (Integer)v).comment("The amount to offset a toast in the y axis.").langKey("toastcontrol.toast types.offsety"),
            ConfigEntry.value("Disable Transitions", boolean.class, () -> ToastControlConfig.noSlide, v -> ToastControlConfig.noSlide = (Boolean)v).comment("Set to true to disable toasts sliding in to view.").langKey("toastcontrol.toast types.noslide"),
            ConfigEntry.value("Transition from Left", boolean.class, () -> ToastControlConfig.startLeft, v -> ToastControlConfig.startLeft = (Boolean)v).comment("Set to true to change the transition to start from the left.").langKey("toastcontrol.toast types.startleft"));
      }
   }
}
