package me.ichun.mods.ichunutil.client.core.event;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map.Entry;
import me.ichun.mods.ichunutil.client.core.EntityTrackerHandler;
import me.ichun.mods.ichunutil.client.entity.EntityLatchedRenderer;
import me.ichun.mods.ichunutil.client.gui.config.GuiConfigs;
import me.ichun.mods.ichunutil.client.keybind.KeyBind;
import me.ichun.mods.ichunutil.client.module.eula.GuiEulaNotifier;
import me.ichun.mods.ichunutil.client.module.patron.LayerPatronEffect;
import me.ichun.mods.ichunutil.client.module.patron.PatronEffectRenderer;
import me.ichun.mods.ichunutil.client.module.update.GuiUpdateNotifier;
import me.ichun.mods.ichunutil.client.render.RendererHelper;
import me.ichun.mods.ichunutil.client.render.entity.RenderLatchedRenderer;
import me.ichun.mods.ichunutil.client.render.item.ItemRenderingHelper;
import me.ichun.mods.ichunutil.client.render.world.RenderGlobalProxy;
import me.ichun.mods.ichunutil.common.iChunUtil;
import me.ichun.mods.ichunutil.common.core.config.ConfigBase;
import me.ichun.mods.ichunutil.common.core.config.ConfigHandler;
import me.ichun.mods.ichunutil.common.core.tracker.EntityTrackerRegistry;
import me.ichun.mods.ichunutil.common.core.util.EntityHelper;
import me.ichun.mods.ichunutil.common.core.util.ObfHelper;
import me.ichun.mods.ichunutil.common.grab.GrabHandler;
import me.ichun.mods.ichunutil.common.packet.mod.PacketPatronInfo;
import net.minecraft.client.Minecraft;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.client.gui.GuiControls;
import net.minecraft.client.gui.GuiIngameMenu;
import net.minecraft.client.gui.GuiMainMenu;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import net.minecraft.client.multiplayer.WorldClient;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.client.renderer.entity.RenderPlayer;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.client.shader.Framebuffer;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.Item;
import net.minecraft.util.IThreadListener;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.text.translation.I18n;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.event.RenderSpecificHandEvent;
import net.minecraftforge.client.event.RenderPlayerEvent.Post;
import net.minecraftforge.client.event.RenderPlayerEvent.Pre;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.RenderTickEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientConnectedToServerEvent;
import net.minecraftforge.fml.common.network.FMLNetworkEvent.ClientDisconnectionFromServerEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.registries.ForgeRegistry;
import net.lax1dude.eaglercraft.v1_8.Mouse;

public class EventHandlerClient {
   public boolean hasShownFirstGui;
   public boolean connectingToServer;
   public int ticks;
   public float renderTick;
   public boolean hasScreen;
   public int screenWidth;
   public int screenHeight;
   public boolean mouseLeftDown;
   public ArrayList<KeyBind> keyBindList = new ArrayList();
   public HashMap<KeyBinding, KeyBind> mcKeyBindList = new HashMap();
   protected WorldClient renderGlobalWorldInstance;
   public boolean patronUpdateServerAsPatron;

   public EventHandlerClient() {
      Minecraft mc = Minecraft.getMinecraft();
      this.screenWidth = mc.displayWidth;
      this.screenHeight = mc.displayHeight;
      EntityTrackerHandler.init();
   }

   @SubscribeEvent
   public void onRendererSafeCompatibility(RendererSafeCompatibilityEvent event) {
      for(Entry<String, RenderPlayer> e : Minecraft.getMinecraft().getRenderManager().skinMap.entrySet()) {
         ((RenderPlayer)e.getValue()).addLayer(new LayerPatronEffect((RenderPlayer)e.getValue()));
      }

      GuiEulaNotifier.createIfRequired();
   }

   @SubscribeEvent
   public void onModelRegistry(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(Item.getItemFromBlock(iChunUtil.blockCompactPorkchop), 0, new ModelResourceLocation("ichunutil:compact_porkchop", "inventory"));
   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onPlayerRenderPre(Pre event) {
      PatronEffectRenderer.onPlayerRenderPre(event);
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onPlayerRenderPost(Post event) {
      PatronEffectRenderer.onPlayerRenderPost(event);
   }

   @SubscribeEvent
   public void onRenderSpecificHand(RenderSpecificHandEvent event) {
      ItemRenderingHelper.onRenderSpecificHand(event);
   }

   @SubscribeEvent
   public void onRenderTick(RenderTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      this.renderTick = event.renderTickTime;
      if (event.phase == Phase.START) {
         if (this.screenWidth != mc.displayWidth || this.screenHeight != mc.displayHeight) {
            this.screenWidth = mc.displayWidth;
            this.screenHeight = mc.displayHeight;

            for(Framebuffer buffer : RendererHelper.frameBuffers) {
               buffer.createBindFramebuffer(this.screenWidth, this.screenHeight);
            }
         }

         if (this.renderGlobalWorldInstance != mc.renderGlobal.world) {
            this.renderGlobalWorldInstance = mc.renderGlobal.world;

            for(RenderGlobalProxy proxy : RendererHelper.renderGlobalProxies) {
               if (!proxy.released) {
                  proxy.setWorldAndLoadRenderers(this.renderGlobalWorldInstance);
               }
            }
         }

         ItemRenderingHelper.handlePreRender(mc);
         EntityTrackerHandler.onRenderTickStart(event);
      } else {
         ScaledResolution reso = new ScaledResolution(mc);
         GuiUpdateNotifier.update();
         GuiEulaNotifier.update();
         if (mc.currentScreen instanceof GuiControls && !this.keyBindList.isEmpty()) {
            String s = I18n.translateToLocal("ichunutil.config.controls.moreKeys");
            int width = Math.round((float)mc.fontRenderer.getStringWidth(s) / 2.0F);
            GlStateManager.pushMatrix();
            GlStateManager.translate((double)(reso.getScaledWidth() - width - 2), (double)reso.getScaledHeight() - (double)mc.fontRenderer.FONT_HEIGHT / 2.0D - 2.0D, 0.0D);
            GlStateManager.scale(0.5F, 0.5F, 0.5F);
            mc.fontRenderer.drawString(s, 0.0F, 0.0F, 16777215, true);
            GlStateManager.popMatrix();
            int i = Mouse.getX() * reso.getScaledWidth() / mc.displayWidth;
            int j = reso.getScaledHeight() - Mouse.getY() * reso.getScaledHeight() / mc.displayHeight - 1;
            if (!this.mouseLeftDown && Mouse.isButtonDown(0) && i >= reso.getScaledWidth() - width - 2 && i <= reso.getScaledWidth() && j >= reso.getScaledHeight() - mc.fontRenderer.FONT_HEIGHT - 2 && j <= reso.getScaledHeight()) {
               mc.getSoundHandler().playSound(PositionedSoundRecord.getMasterRecord(SoundEvents.UI_BUTTON_CLICK, 1.0F));
               FMLClientHandler.instance().showGuiScreen(new GuiConfigs(mc.currentScreen));
            }

            this.mouseLeftDown = Mouse.isButtonDown(0);
         }
      }

   }

   @SubscribeEvent
   public void onClientTick(ClientTickEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      if (event.phase.equals(Phase.END)) {
         if (mc.world != null) {
            if (this.connectingToServer) {
               this.connectingToServer = false;
               if (((ForgeRegistry)ForgeRegistries.RECIPES).getID(new ResourceLocation("ichunutil", "compact_porkchop")) == -1) {
                  iChunUtil.config.enableCompactPorkchop = 0;
               }

               MinecraftForge.EVENT_BUS.post(new ServerPacketableEvent());
            }

            if (this.patronUpdateServerAsPatron) {
               this.patronUpdateServerAsPatron = false;
               iChunUtil.channel.sendToServer(new PacketPatronInfo(iChunUtil.proxy.getPlayerId(), iChunUtil.config.patronRewardType, iChunUtil.config.showPatronReward == 1));
            }

            for(KeyBind bind : this.keyBindList) {
               bind.tick();
            }

            for(Entry<KeyBinding, KeyBind> e : this.mcKeyBindList.entrySet()) {
               if (((KeyBind)e.getValue()).keyIndex != ((KeyBinding)e.getKey()).getKeyCode()) {
                  e.setValue(new KeyBind(((KeyBinding)e.getKey()).getKeyCode()));
               }

               ((KeyBind)e.getValue()).tick();
            }

            this.hasScreen = mc.currentScreen != null;
            if (!mc.isGamePaused()) {
               EntityTrackerHandler.tick();
               GrabHandler.tick(Side.CLIENT);
               if (!ObfHelper.obfuscated() && Minecraft.getMinecraft().getSession().getProfile().getName().equals("iChun") && mc.player.isElytraFlying() && mc.gameSettings.keyBindJump.isKeyDown()) {
                  mc.player.motionY += 0.05000000074505806D;
               }
            }
         }

         ++this.ticks;
      }

   }

   @SubscribeEvent
   public void onPlayerTick(PlayerTickEvent event) {
      if (event.side.isClient() && event.phase == Phase.END) {
         Minecraft mc = Minecraft.getMinecraft();
         ItemRenderingHelper.handlePlayerTick(mc, event.player);
      }

   }

   @SubscribeEvent
   public void onClientConnection(ClientConnectedToServerEvent event) {
      this.connectingToServer = true;
      if (iChunUtil.userIsPatron) {
         this.patronUpdateServerAsPatron = true;
      }

      for(ConfigBase conf : ConfigHandler.configs) {
         conf.storeSession();
      }

   }

   @SubscribeEvent
   public void onClientDisconnect(ClientDisconnectionFromServerEvent event) {
      IThreadListener thread = Minecraft.getMinecraft();
      if (thread.isCallingFromMinecraftThread()) {
         this.onClientDisconnect();
      } else {
         thread.addScheduledTask(this::onClientDisconnect);
      }

   }

   public void onClientDisconnect() {
      EntityTrackerHandler.onClientDisconnect();
      PatronEffectRenderer.onClientDisconnect();
      ((ArrayList)GrabHandler.grabbedEntities.get(Side.CLIENT)).clear();

      for(ConfigBase conf : ConfigHandler.configs) {
         conf.resetSession();
      }

      EntityHelper.profileCache = null;
      EntityHelper.sessionService = null;
   }

   @SubscribeEvent(
      priority = EventPriority.HIGHEST
   )
   public void onInitGuiPost(net.minecraftforge.client.event.GuiScreenEvent.InitGuiEvent.Post event) {
      if (iChunUtil.hasPostInit() && !this.hasShownFirstGui) {
         this.hasShownFirstGui = true;
         MinecraftForge.EVENT_BUS.post(new RendererSafeCompatibilityEvent());
      }

   }

   @SubscribeEvent
   public void onWorldLoad(Load event) {
      if (event.getWorld().isRemote) {
         for(GrabHandler handler : (ArrayList)GrabHandler.grabbedEntities.get(Side.CLIENT)) {
            handler.grabber = null;
            handler.grabbed = null;
         }
      }

   }

   @SubscribeEvent(
      priority = EventPriority.LOWEST
   )
   public void onEntitySpawn(EntityJoinWorldEvent event) {
      EntityTrackerHandler.onEntitySpawn(event);
   }

   @SubscribeEvent
   public void onLatchedRendererUpdate(EntityLatchedRenderer.EntityLatchedRendererUpdateEvent event) {
      EntityTrackerHandler.onLatchedRendererUpdate(event);
   }

   @SubscribeEvent
   public void onLatchedRendererRender(RenderLatchedRenderer.RenderLatchedRendererEvent event) {
      EntityTrackerHandler.onLatchedRendererRender(event);
   }

   /** @deprecated */
   @Deprecated
   public EntityTrackerRegistry getEntityTrackerRegistry() {
      return EntityTrackerHandler.getEntityTrackerRegistry();
   }

   public WorldClient getRenderGlobalWorldInstance() {
      return this.renderGlobalWorldInstance;
   }

   @SubscribeEvent
   public void onGuiActionPerformed(net.minecraftforge.client.event.GuiScreenEvent.ActionPerformedEvent.Pre event) {
      if (!ObfHelper.obfuscated() && Minecraft.getMinecraft().getSession().getProfile().getName().equals("iChun") && (event.getGui().getClass() == GuiIngameMenu.class && event.getButton().id == 12 || event.getGui().getClass() == GuiMainMenu.class && event.getButton().id == 6) && !GuiScreen.isShiftKeyDown()) {
         event.setCanceled(true);
         Minecraft.getMinecraft().displayGuiScreen(new GuiConfigs(Minecraft.getMinecraft().currentScreen));
      }

   }
}
