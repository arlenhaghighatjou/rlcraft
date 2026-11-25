package shadows.toaster;

import java.util.ArrayList;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.toasts.IToast;
import net.minecraft.client.gui.toasts.GuiToast.ToastInstance;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Config;
import net.minecraftforge.common.config.ConfigManager;
import net.minecraftforge.common.config.Config.Comment;
import net.minecraftforge.common.config.Config.Name;
import net.minecraftforge.common.config.Config.RangeInt;
import net.minecraftforge.common.config.Config.Type;
import net.minecraftforge.fml.client.event.ConfigChangedEvent.OnConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent.KeyInputEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

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
   public static final Logger LOGGER = LogManager.getLogger("toastcontrol");
   public static final KeyBinding CLEAR = new KeyBinding("key.toastcontrol.clear", 36, "key.toastcontrol.category");
   public static final ResourceLocation TRANSLUCENT = new ResourceLocation("toastcontrol", "textures/gui/toasts.png");
   public static final ResourceLocation TRANSPARENT = new ResourceLocation("toastcontrol", "textures/gui/toasts2.png");
   public static final ResourceLocation ORIGINAL = new ResourceLocation("textures/gui/toasts.png");
   public static final List<Class<?>> BLOCKED_CLASSES = new ArrayList();
   public static List<BetterGuiToast.BetterToastInstance<?>> tracker = new ArrayList();

   public ToastControl() {
   }

   @SubscribeEvent
   public void keys(KeyInputEvent e) {
      if (CLEAR.isKeyDown() && CLEAR.isPressed()) {
         Minecraft.getMinecraft().getToastGui().clear();
      }

   }

   @EventHandler
   public void preInit(FMLPreInitializationEvent e) {
      Minecraft.getMinecraft().toastGui = new BetterGuiToast();
      MinecraftForge.EVENT_BUS.register(this);
      handleToastReloc();
      handleBlockedClasses();
      ClientRegistry.registerKeyBinding(CLEAR);
   }

   private static void handleToastReloc() {
      ResourceLocation target = IToast.TEXTURE_TOASTS;
      if (ToastControl.ToastControlConfig.translucent) {
         change(target, TRANSLUCENT);
      }

      if (ToastControl.ToastControlConfig.transparent) {
         change(target, TRANSPARENT);
      } else if (!ToastControl.ToastControlConfig.translucent && !ToastControl.ToastControlConfig.transparent) {
         change(target, ORIGINAL);
      }

   }

   private static void change(ResourceLocation a, ResourceLocation b) {
      ReflectionHelper.setPrivateValue(ResourceLocation.class, a, b.getResourceDomain(), "namespace", "resourceDomain");
      ReflectionHelper.setPrivateValue(ResourceLocation.class, a, b.getResourcePath(), "path", "resourcePath");
   }

   private static void handleBlockedClasses() {
      BLOCKED_CLASSES.clear();

      for(String s : ToastControl.ToastControlConfig.blockedClasses) {
         try {
            Class<?> c = Class.forName(s);
            BLOCKED_CLASSES.add(c);
         } catch (ClassNotFoundException var5) {
            LOGGER.error("Invalid class string provided to toast control: " + s);
         }
      }

   }

   @SubscribeEvent
   public void clientTick(ClientTickEvent e) {
      for(BetterGuiToast.BetterToastInstance<?> t : tracker) {
         t.tick();
      }

   }

   @SubscribeEvent
   public void onConfigChanged(OnConfigChangedEvent event) {
      if (event.getModID().equals("toastcontrol")) {
         ConfigManager.sync("toastcontrol", Type.INSTANCE);
         handleToastReloc();
         handleBlockedClasses();
         ((BetterGuiToast)Minecraft.getMinecraft().toastGui).visible = new ToastInstance[ToastControl.ToastControlConfig.toastCount];
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
   }
}
