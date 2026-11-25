package vazkii.quark.world.effects;

import java.util.Collection;
import java.util.WeakHashMap;
import java.util.function.Function;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.potion.PotionEffect;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.client.event.RenderLivingEvent.Post;
import net.minecraftforge.client.event.RenderLivingEvent.Pre;
import net.minecraftforge.event.entity.living.PotionColorCalculationEvent;
import net.minecraftforge.event.entity.player.PlayerEvent.StartTracking;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.arl.network.NetworkHandler;
import vazkii.quark.base.client.ClientReflectiveAccessor;
import vazkii.quark.base.network.message.MessageSyncColors;
import vazkii.quark.base.potion.PotionMod;
import vazkii.quark.base.util.MutableVectorHolder;

public class PotionColorizer extends PotionMod {
   public final int color;
   public final float r;
   public final float g;
   public final float b;
   private static final WeakHashMap<EntityLivingBase, MutableVectorHolder> colors = new WeakHashMap();

   public PotionColorizer(String name, int color, int iconIndex) {
      super(name, true, color, iconIndex);
      this.color = color;
      this.r = (float)((color & 16711680) >> 16) / 255.0F;
      this.g = (float)((color & '\uff00') >> 8) / 255.0F;
      this.b = (float)(color & 255) / 255.0F;
   }

   @SubscribeEvent
   public static void onStartTracking(StartTracking event) {
      if (event.getEntityPlayer() instanceof EntityPlayerMP && event.getTarget() instanceof EntityLivingBase) {
         NetworkHandler.INSTANCE.sendTo(prepareMessage((EntityLivingBase)event.getTarget()), (EntityPlayerMP)event.getEntityPlayer());
      }

   }

   @SubscribeEvent
   public static void onCalculateColor(PotionColorCalculationEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      World world = entity.world;
      if (world instanceof WorldServer) {
         for(EntityPlayer tracker : ((WorldServer)world).getEntityTracker().getTrackingPlayers(entity)) {
            if (tracker instanceof EntityPlayerMP) {
               NetworkHandler.INSTANCE.sendTo(prepareMessage(entity.getEntityId(), event.getEffects()), (EntityPlayerMP)tracker);
            }
         }
      }

      if (entity instanceof EntityPlayerMP) {
         NetworkHandler.INSTANCE.sendTo(prepareMessage(entity.getEntityId(), event.getEffects()), (EntityPlayerMP)entity);
      }

   }

   private static MessageSyncColors prepareMessage(EntityLivingBase entity) {
      return prepareMessage(entity.getEntityId(), entity.getActivePotionEffects());
   }

   private static MessageSyncColors prepareMessage(int id, Collection<PotionEffect> effects) {
      float rMix = 0.0F;
      float gMix = 0.0F;
      float bMix = 0.0F;
      int total = 0;

      for(PotionEffect effect : effects) {
         if (effect.getPotion() instanceof PotionColorizer) {
            rMix += ((PotionColorizer)effect.getPotion()).r;
            gMix += ((PotionColorizer)effect.getPotion()).g;
            bMix += ((PotionColorizer)effect.getPotion()).b;
            ++total;
         }
      }

      if (total > 0) {
         rMix = rMix / (float)total;
         gMix = gMix / (float)total;
         bMix = bMix / (float)total;
         return new MessageSyncColors(id, (double)rMix, (double)gMix, (double)bMix);
      } else {
         return new MessageSyncColors(id, -1.0D, -1.0D, -1.0D);
      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void colorize(Pre event) {
      EntityLivingBase entity = event.getEntity();
      if (((Integer)entity.getDataManager().get(ClientReflectiveAccessor.potionEffectColor())).intValue() != 0) {
         MutableVectorHolder holder = (MutableVectorHolder)colors.get(event.getEntity());
         if (holder != null) {
            GlStateManager.color((float)holder.x, (float)holder.y, (float)holder.z);
         }

      }
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public static void colorize(Post event) {
      if (colors.containsKey(event.getEntity())) {
         GlStateManager.color(1.0F, 1.0F, 1.0F);
      }

   }

   public static void receiveColors(EntityLivingBase entity, double r, double g, double b) {
      if (r >= 0.0D && g >= 0.0D && b >= 0.0D) {
         MutableVectorHolder holder = (MutableVectorHolder)colors.computeIfAbsent(entity, (e) -> {
            return new MutableVectorHolder();
         });
         holder.x = r;
         holder.y = g;
         holder.z = b;
      } else {
         colors.remove(entity);
      }

   }
}
