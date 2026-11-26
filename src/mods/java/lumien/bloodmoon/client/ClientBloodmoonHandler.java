package lumien.bloodmoon.client;

import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import lumien.bloodmoon.config.BloodmoonConfig;
import net.minecraft.client.Minecraft;
import net.minecraft.client.entity.EntityPlayerSP;
import net.minecraft.client.multiplayer.WorldClient;
import net.minecraft.util.math.Vec3d;
import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraftforge.fml.common.gameevent.TickEvent.ClientTickEvent;

public class ClientBloodmoonHandler {
   public static ClientBloodmoonHandler INSTANCE = new ClientBloodmoonHandler();
   boolean bloodmoonActive = false;
   final float sinMax = 2.6179937E-4F;
   float lightSub;
   public float fogRemove;
   float skyColorAdd;
   float moonColorRed;
   float d = 6.666667E-5F;
   int difTime = 0;
   double sin;

   public ClientBloodmoonHandler() {
   }

   public boolean isBloodmoonActive() {
      return this.bloodmoonActive;
   }

   public void setBloodmoon(boolean active) {
      this.bloodmoonActive = active;
   }

   public void moonColorHook() {
      if (this.isBloodmoonActive() && BloodmoonConfig.APPEARANCE.RED_MOON) {
         GlStateManager.color(0.8F, 0.0F, 0.0F);
      }

   }

   public Vec3d skyColorHook(Vec3d color) {
      if (this.isBloodmoonActive() && BloodmoonConfig.APPEARANCE.RED_SKY) {
         color.x += (double)INSTANCE.skyColorAdd;
      }

      return color;
   }

   public int manipulateRed(int position, int originalValue) {
      return originalValue;
   }

   public int manipulateGreen(int position, int originalValue) {
      if (this.isBloodmoonActive() && BloodmoonConfig.APPEARANCE.RED_LIGHT) {
         int height = position / 16;
         if (height < 16) {
            float mod = 0.0625F * (float)height;
            originalValue = (int)((double)originalValue - (double)(mod * this.lightSub) * (this.sin / 2.0D + 1.0D));
            return Math.max(originalValue, 0);
         }
      }

      return originalValue;
   }

   public int manipulateBlue(int position, int originalValue) {
      if (this.isBloodmoonActive() && BloodmoonConfig.APPEARANCE.RED_LIGHT) {
         int height = position / 16;
         if (height < 16) {
            float mod = 0.0625F * (float)height;
            originalValue = (int)((float)originalValue - mod * this.lightSub * 2.3F);
            return Math.max(originalValue, 0);
         }
      }

      return originalValue;
   }

   public void register(EventBus bus) {
      bus.register(ClientTickEvent.class, this::clientTick);
   }

   public void clientTick(ClientTickEvent event) {
      if (this.isBloodmoonActive()) {
         WorldClient world = Minecraft.getMinecraft().world;
         EntityPlayerSP player = Minecraft.getMinecraft().player;
         if (world != null && player != null) {
            float difTime = (float)((int)(world.getWorldTime() % 24000L) - 12000);
            this.sin = Math.sin((double)(difTime * 2.6179937E-4F));
            this.lightSub = (float)(this.sin * 150.0D);
            this.skyColorAdd = (float)(this.sin * 0.10000000149011612D);
            this.moonColorRed = (float)(this.sin * 0.699999988079071D);
            this.fogRemove = (float)(this.sin * (double)this.d * 6000.0D);
            if (world.provider.getDimension() != 0) {
               this.bloodmoonActive = false;
            }
         } else if (this.bloodmoonActive) {
            this.bloodmoonActive = false;
         }
      }

   }
}
