package org.orecruncher.dsurround.client.handlers;

import it.unimi.dsi.fastutil.objects.Reference2ObjectOpenHashMap;
import java.util.Map;
import javax.annotation.Nonnull;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.relauncher.Side;
import org.orecruncher.dsurround.ModBase;
import org.orecruncher.dsurround.ModOptions;
import org.orecruncher.lib.collections.ObjectArray;
import org.orecruncher.lib.compat.EntityLivingBaseUtil;
import org.orecruncher.lib.math.TimerEMA;

@EventBusSubscriber(
   value = {Side.CLIENT},
   modid = "dsurround"
)
public class EffectManager {
   private static final EffectManager instance_ = new EffectManager();
   private static boolean isConnected = false;
   private final ObjectArray<EffectHandlerBase> effectHandlers = new ObjectArray();
   private final Map<Class<? extends EffectHandlerBase>, EffectHandlerBase> services = new Reference2ObjectOpenHashMap();
   private final TimerEMA computeTime = new TimerEMA("Processing");

   public static EffectManager instance() {
      return instance_;
   }

   private EffectManager() {
      this.init();
   }

   private void register(@Nonnull EffectHandlerBase handler) {
      this.effectHandlers.add(handler);
      this.services.put(handler.getClass(), handler);
      ModBase.log().debug("Registered handler [%s]", new Object[]{handler.getClass().getName()});
   }

   private void init() {
      this.register(new EnvironStateHandler());
      this.register(new AreaBlockEffectsHandler());
      this.register(new FogHandler());
      this.register(new ParticleSystemHandler());
      this.register(new BiomeSoundEffectsHandler());
      this.register(new AuroraEffectHandler());
      this.register(new WeatherHandler());
      this.register(new FxHandler());
      this.register(SoundEffectHandler.INSTANCE);
      this.register(new DiagnosticHandler());
   }

   private void onConnect() {
      for(EffectHandlerBase h : this.effectHandlers) {
         h.connect0();
      }

      MinecraftForge.EVENT_BUS.register(this);
      ((DiagnosticHandler)this.lookupService(DiagnosticHandler.class)).addTimer(this.computeTime);
   }

   private void onDisconnect() {
      MinecraftForge.EVENT_BUS.unregister(this);

      for(EffectHandlerBase h : this.effectHandlers) {
         h.disconnect0();
      }

   }

   public <T> T lookupService(@Nonnull Class<? extends EffectHandlerBase> service) {
      EffectHandlerBase eh = (EffectHandlerBase)this.services.get(service);
      if (eh == null) {
         ModBase.log().warn("Unable to locate handler service [%s]", new Object[]{service.getName()});
      }

      return (T)eh;
   }

   public boolean isConnected() {
      return isConnected;
   }

   public static void connect() {
      if (isConnected) {
         ModBase.log().warn("Attempt to initialize EffectManager when it is already initialized", new Object[0]);
         disconnect();
      }

      instance_.onConnect();
      isConnected = true;
   }

   public static void disconnect() {
      if (isConnected) {
         instance_.onDisconnect();
         isConnected = false;
      }

   }

   protected static EntityPlayer getPlayer() {
      return Minecraft.getMinecraft().player;
   }

   protected boolean checkReady(@Nonnull ClientTickEvent event) {
      if (event.side != Side.SERVER && event.phase != Phase.END && !Minecraft.getMinecraft().isGamePaused()) {
         if (Minecraft.getMinecraft().gameSettings == null) {
            return false;
         } else if (Minecraft.getMinecraft().getRenderManager().options == null) {
            return false;
         } else {
            EntityPlayer player = getPlayer();
            return player != null && player.getEntityWorld() != null;
         }
      } else {
         return false;
      }
   }

   public void onTick(@Nonnull ClientTickEvent event) {
      if (this.checkReady(event)) {
         long start = System.nanoTime();
         EntityPlayer player = getPlayer();
         if (ModOptions.player.suppressPotionParticles) {
            player.getDataManager().set(EntityLivingBaseUtil.getHideParticles(), Boolean.valueOf(true));
         }

         int tick = EnvironStateHandler.EnvironState.getTickCounter();

         for(int i = 0; i < this.effectHandlers.size(); ++i) {
            EffectHandlerBase handler = (EffectHandlerBase)this.effectHandlers.get(i);
            long mark = System.nanoTime();
            if (handler.doTick(tick)) {
               handler.process(player);
            }

            handler.updateTimer(System.nanoTime() - mark);
         }

         this.computeTime.update((double)(System.nanoTime() - start));
      }
   }

   @SubscribeEvent
   public static void clientTick(@Nonnull ClientTickEvent event) {
      if (isConnected) {
         instance_.onTick(event);
      }

   }
}
