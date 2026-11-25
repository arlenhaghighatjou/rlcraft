package net.ilexiconn.llibrary.client;

import com.google.common.util.concurrent.ListenableFuture;
import com.google.gson.Gson;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.Executor;
import net.ilexiconn.llibrary.LLibrary;
import net.ilexiconn.llibrary.client.gui.SnackbarGUI;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTab;
import net.ilexiconn.llibrary.client.gui.survivaltab.SurvivalTabHandler;
import net.ilexiconn.llibrary.client.lang.LanguageHandler;
import net.ilexiconn.llibrary.client.model.tabula.TabulaModelHandler;
import net.ilexiconn.llibrary.client.render.entity.PartRenderer;
import net.ilexiconn.llibrary.server.ServerProxy;
import net.ilexiconn.llibrary.server.entity.multipart.PartEntity;
import net.ilexiconn.llibrary.server.network.AbstractMessage;
import net.ilexiconn.llibrary.server.snackbar.Snackbar;
import net.ilexiconn.llibrary.server.util.WebUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.inventory.GuiInventory;
import net.minecraft.util.Timer;
import net.minecraftforge.client.model.ModelLoaderRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;
import net.minecraftforge.fml.relauncher.ReflectionHelper;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class ClientProxy extends ServerProxy {
   public static final Minecraft MINECRAFT = Minecraft.getMinecraft();
   public static final int UPDATE_BUTTON_ID = "UPDATE_BUTTON_ID".hashCode();
   public static final List<SnackbarGUI> SNACKBAR_LIST = new ArrayList();
   public static final Set<String> PATRONS = new HashSet();
   public static final Timer TIMER = (Timer)ReflectionHelper.getPrivateValue(Minecraft.class, MINECRAFT, new String[]{"timer", "timer", "aa"});
   public static final SurvivalTab INVENTORY_TAB = SurvivalTabHandler.INSTANCE.create("container.inventory", GuiInventory.class);

   public ClientProxy() {
   }

   public void onPreInit() {
      super.onPreInit();
      ListenableFuture<String> patronFuture = WebUtils.readURLAsync("https://gist.githubusercontent.com/Gegy/7a6d39cf7a2c1f794ffb9037e8146adc/raw/llibrary_patrons.json");
      patronFuture.addListener(() -> {
         try {
            String result = (String)patronFuture.get();
            if (result != null) {
               Collections.addAll(PATRONS, (new Gson()).fromJson(result, String[].class));
            }
         } catch (Exception var2) {
            LLibrary.LOGGER.error("Failed to load Patron list", var2);
         }

      }, Runnable::run);
      MinecraftForge.EVENT_BUS.register(ClientEventHandler.INSTANCE);
      ModelLoaderRegistry.registerLoader(TabulaModelHandler.INSTANCE);
      TabulaModelHandler.INSTANCE.addDomain("llibrary");
      RenderingRegistry.registerEntityRenderingHandler(PartEntity.class, new PartRenderer.Factory());
      LanguageHandler var10002 = LanguageHandler.INSTANCE;
      LanguageHandler.INSTANCE.getClass();
      Thread thread = new Thread(var10002::load);
      thread.setName("LLibrary Language Download");
      thread.setDaemon(true);
      thread.start();
   }

   public void onInit() {
      super.onInit();
   }

   public void onPostInit() {
      super.onPostInit();
   }

   public <T extends AbstractMessage<T>> void handleMessage(T message, MessageContext messageContext) {
      if (messageContext.side.isServer()) {
         super.handleMessage(message, messageContext);
      } else {
         MINECRAFT.addScheduledTask(() -> {
            message.onClientReceived(MINECRAFT, message, MINECRAFT.player, messageContext);
         });
      }

   }

   public float getPartialTicks() {
      return TIMER.renderPartialTicks;
   }

   public void showSnackbar(Snackbar snackbar) {
      SNACKBAR_LIST.add(new SnackbarGUI(snackbar));
   }

   public void setTPS(float tickRate) {
      TIMER.tickLength = 1000.0F / tickRate;
   }
}
