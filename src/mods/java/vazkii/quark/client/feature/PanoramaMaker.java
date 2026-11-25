package vazkii.quark.client.feature;

import com.google.common.collect.ImmutableSet;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.function.Predicate;
import javax.imageio.ImageIO;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.texture.DynamicTexture;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.ScreenShotHelper;
import net.minecraft.util.text.ITextComponent;
import net.minecraft.util.text.TextComponentString;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.event.ClickEvent;
import net.minecraft.util.text.event.ClickEvent.Action;
import net.minecraftforge.client.ForgeHooksClient;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.ScreenshotEvent;
import net.minecraftforge.fml.common.ObfuscationReflectionHelper;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import vazkii.quark.base.module.Feature;
import vazkii.quark.base.module.ModuleLoader;

public class PanoramaMaker extends Feature {
   private static final DateFormat DATE_FORMAT = new SimpleDateFormat("yyyy-MM-dd_HH.mm.ss");
   private File panoramaDir;
   private File currentDir;
   private float rotationYaw;
   private float rotationPitch;
   private int panoramaStep;
   private boolean takingPanorama;
   private int currentWidth;
   private int currentHeight;
   private boolean overriddenOnce;
   public static boolean overrideMainMenu;
   public static int panoramaSize;
   public static boolean fullscreen;

   public PanoramaMaker() {
   }

   public void setupConfig() {
      overrideMainMenu = this.loadPropBool("Use panorama screenshots on main menu", "", true);
      fullscreen = this.loadPropBool("Fullres screenshots", "Take panorama screenshots without changing the render size", false);
      panoramaSize = this.loadPropInt("Panorama Picture Resolution", "", 256);
   }

   @SubscribeEvent
   public void loadMainMenu(GuiOpenEvent event) {
      if (overrideMainMenu && !this.overriddenOnce && event.getGui() instanceof GuiMainMenu) {
         File mcDir = ModuleLoader.configFile.getParentFile().getParentFile();
         File panoramasDir = new File(mcDir, "/screenshots/panoramas");
         List<File[]> validFiles = new ArrayList();
         ImmutableSet<String> set = ImmutableSet.of("panorama_0.png", "panorama_1.png", "panorama_2.png", "panorama_3.png", "panorama_4.png", "panorama_5.png", new String[0]);
         if (panoramasDir.exists()) {
            File mainMenu = new File(panoramasDir, "main_menu");
            File[] subDirs;
            if (mainMenu.exists()) {
               subDirs = new File[]{mainMenu};
            } else {
               subDirs = panoramasDir.listFiles((f) -> {
                  return f.isDirectory() && !f.getName().endsWith("fullres");
               });
            }

            if (subDirs != null) {
               for(File f : subDirs) {
                  if (set.stream().allMatch((s) -> {
                     return (new File(f, s)).exists();
                  })) {
                     validFiles.add(f.listFiles((f1) -> {
                        return set.contains(f1.getName());
                     }));
                  }
               }
            }
         }

         if (!validFiles.isEmpty()) {
            File[] files = (File[])validFiles.get((new Random()).nextInt(validFiles.size()));
            Arrays.sort(files);
            Minecraft mc = Minecraft.getMinecraft();
            ResourceLocation[] resources = new ResourceLocation[6];

            for(int i = 0; i < resources.length; ++i) {
               File f = files[i];

               try {
                  BufferedImage img = ImageIO.read(f);
                  DynamicTexture tex = new DynamicTexture(img);
                  String name = "quark:" + f.getName();
                  resources[i] = mc.getTextureManager().getDynamicTextureLocation(name, tex);
               } catch (IOException var15) {
                  var15.printStackTrace();
                  return;
               }
            }

            try {
               Field field = ObfuscationReflectionHelper.findField(GuiMainMenu.class, "TITLE_PANORAMA_PATHS");
               field.setAccessible(true);
               if (Modifier.isFinal(field.getModifiers())) {
                  Field modifiers = Field.class.getDeclaredField("modifiers");
                  modifiers.setAccessible(true);
                  modifiers.setInt(field, field.getModifiers() & -17);
               }

               field.set((Object)null, resources);
            } catch (Exception var14) {
               var14.printStackTrace();
            }
         }

         this.overriddenOnce = true;
      }

   }

   @SubscribeEvent
   public void takeScreenshot(ScreenshotEvent event) {
      if (!this.takingPanorama) {
         if (GuiScreen.isCtrlKeyDown() && GuiScreen.isShiftKeyDown() && Minecraft.getMinecraft().currentScreen == null) {
            this.takingPanorama = true;
            this.panoramaStep = 0;
            if (this.panoramaDir == null) {
               this.panoramaDir = new File(event.getScreenshotFile().getParentFile(), "panoramas");
            }

            if (!this.panoramaDir.exists() && !this.panoramaDir.mkdirs()) {
               return;
            }

            String ts = getTimestamp();

            while(true) {
               if (fullscreen) {
                  this.currentDir = new File(this.panoramaDir + "_fullres", ts);
               } else {
                  this.currentDir = new File(this.panoramaDir, ts);
               }

               if (!this.currentDir.exists()) {
                  break;
               }
            }

            if (!this.currentDir.mkdirs()) {
               return;
            }

            event.setCanceled(true);
            ITextComponent panoramaDirComponent = new TextComponentString(this.currentDir.getName());
            panoramaDirComponent.getStyle().setClickEvent(new ClickEvent(Action.OPEN_FILE, this.currentDir.getAbsolutePath())).setUnderlined(Boolean.valueOf(true));
            event.setResultMessage(new TextComponentTranslation("quarkmisc.panoramaSaved", new Object[]{panoramaDirComponent}));
         }

      }
   }

   @SubscribeEvent
   public void renderTick(RenderTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (this.takingPanorama) {
         if (event.phase == Phase.START) {
            if (this.panoramaStep == 0) {
               mc.gameSettings.hideGUI = true;
               this.currentWidth = mc.displayWidth;
               this.currentHeight = mc.displayHeight;
               this.rotationYaw = mc.player.rotationYaw;
               this.rotationPitch = mc.player.rotationPitch;
               if (!fullscreen) {
                  mc.resize(panoramaSize, panoramaSize);
               }
            }

            switch(this.panoramaStep) {
            case 1:
               mc.player.rotationYaw = 180.0F;
               mc.player.rotationPitch = 0.0F;
               break;
            case 2:
               mc.player.rotationYaw = -90.0F;
               mc.player.rotationPitch = 0.0F;
               break;
            case 3:
               mc.player.rotationYaw = 0.0F;
               mc.player.rotationPitch = 0.0F;
               break;
            case 4:
               mc.player.rotationYaw = 90.0F;
               mc.player.rotationPitch = 0.0F;
               break;
            case 5:
               mc.player.rotationYaw = 180.0F;
               mc.player.rotationPitch = -90.0F;
               break;
            case 6:
               mc.player.rotationYaw = 180.0F;
               mc.player.rotationPitch = 90.0F;
            }

            mc.player.prevRotationYaw = mc.player.rotationYaw;
            mc.player.prevRotationPitch = mc.player.rotationPitch;
         } else {
            if (this.panoramaStep > 0) {
               saveScreenshot(this.currentDir, "panorama_" + (this.panoramaStep - 1) + ".png", mc.displayWidth, mc.displayHeight, mc.getFramebuffer());
            }

            ++this.panoramaStep;
            if (this.panoramaStep == 7) {
               mc.gameSettings.hideGUI = false;
               this.takingPanorama = false;
               mc.player.rotationYaw = this.rotationYaw;
               mc.player.rotationPitch = this.rotationPitch;
               mc.player.prevRotationYaw = mc.player.rotationYaw;
               mc.player.prevRotationPitch = mc.player.rotationPitch;
               mc.resize(this.currentWidth, this.currentHeight);
            }
         }
      }

   }

   public static void saveScreenshot(File dir, String screenshotName, int width, int height, Framebuffer buffer) {
      try {
         BufferedImage bufferedimage = ScreenShotHelper.createScreenshot(width, height, buffer);
         File file2 = new File(dir, screenshotName);
         ForgeHooksClient.onScreenshot(bufferedimage, file2);
         ImageIO.write(bufferedimage, "png", file2);
      } catch (Exception var7) {
         ;
      }

   }

   private static String getTimestamp() {
      return DATE_FORMAT.format(new Date());
   }

   public boolean hasSubscriptions() {
      return this.isClient();
   }

   public String getFeatureDescription() {
      return "Shift-Ctrl-F2 for panorama screenshot.\nPanoramas show up in the main menu.\nRename a panorama folder to 'main_menu' and it'll always show that one.";
   }
}
