package com.endertech.minecraft.forge.world;

import com.endertech.common.CommonMath;
import com.endertech.common.FloatBounds;
import com.endertech.common.IntBounds;
import com.endertech.minecraft.forge.ForgeBounds;
import com.endertech.minecraft.forge.ForgeMain;
import com.endertech.minecraft.forge.api.IWind;
import com.endertech.minecraft.forge.configs.ForgeConfig;
import com.endertech.minecraft.forge.configs.IHaveConfig;
import com.endertech.minecraft.forge.configs.UnitConfig;
import com.endertech.minecraft.forge.math.GameTime;
import com.endertech.minecraft.forge.math.Vect3d;
import com.endertech.minecraft.forge.messages.ForgeNetMsg;
import com.endertech.minecraft.forge.messages.ForgeNetMsgHandler;
import java.lang.reflect.Method;
import java.util.Optional;
import javax.annotation.Nullable;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.common.Loader;
import net.minecraftforge.fml.common.network.simpleimpl.IMessage;
import net.minecraftforge.fml.common.network.simpleimpl.MessageContext;

public class Wind implements IWind, IHaveConfig {
   public static final FloatBounds STRENGTH_BOUNDS = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(0.3F));
   protected static final FloatBounds STEP_BOUNDS = FloatBounds.from(Float.valueOf(0.0F), Float.valueOf(0.01F));
   protected static final int SCALE = 100;
   public static final Wind NONE = new Wind((ForgeConfig)null, 0, 0.0F, 0.0F, 0.0F);
   public static Wind defaultWind = NONE;
   protected final int biomeId;
   protected final FloatBounds strengthBounds;
   protected final float changeProbability;
   protected final float changeStep;
   protected final GameTime changeInterval = GameTime.second();
   protected final GameTime updateInterval = GameTime.quaterSecond();
   protected Vect3d currentMotion = Vect3d.ZERO;
   protected Vect3d targetMotion = Vect3d.ZERO;
   private final ForgeConfig config;

   public Wind(@Nullable ForgeConfig config, int biomeId, float strengthMax, float changeProbability, float changeStep) {
      this.config = config;
      this.biomeId = biomeId;
      strengthMax = (float)ForgeConfig.getInt(config, this.getClassCategory(), "maxStrength", Math.round(strengthMax * 100.0F), IntBounds.from(STRENGTH_BOUNDS.scale(100.0F)), "Defines maximum wind strength.") / 100.0F;
      this.strengthBounds = FloatBounds.from(Float.valueOf(-strengthMax), Float.valueOf(strengthMax));
      this.changeProbability = ForgeConfig.getFloat(config, this.getClassCategory(), "changeProbability", changeProbability * 100.0F, ForgeBounds.PERCENTAGE.getFloatBounds(), "Defines probability of wind strength and direction change in percentage.") / 100.0F;
      this.changeStep = ForgeConfig.getFloat(config, this.getClassCategory(), "changeStep", changeStep * 100.0F, STEP_BOUNDS.scale(100.0F), "Defines how fast the wind changes.") / 100.0F;
      this.randomChangeTargetMotion();
      this.currentMotion = this.targetMotion;
      this.saveConfig();
   }

   public static Wind from(UnitConfig config, int biomeId) {
      return new Wind(config, biomeId, defaultWind.strengthBounds.getMax().floatValue(), defaultWind.changeProbability, defaultWind.changeStep);
   }

   public Vect3d getMotion() {
      return this.currentMotion;
   }

   void setCurrentMotion(Vect3d motion) {
      this.currentMotion = motion;
   }

   public Vect3d getTargetMotion() {
      return this.targetMotion;
   }

   protected void randomChangeTargetMotion() {
      this.targetMotion = Vect3d.from((double)this.strengthBounds.randomBetween().floatValue(), 0.0D, (double)this.strengthBounds.randomBetween().floatValue());
   }

   protected double getChangedMotion(double motion, double targetMotion) {
      double delta = targetMotion - motion;
      if (Math.abs(delta) <= (double)Math.abs(this.changeStep)) {
         return targetMotion;
      } else {
         double changedMotion = motion;
         if (motion < targetMotion) {
            changedMotion = motion + (double)this.changeStep;
         }

         if (motion > targetMotion) {
            changedMotion -= (double)this.changeStep;
         }

         changedMotion = (double)this.strengthBounds.enclose(Float.valueOf((float)changedMotion)).floatValue();
         return changedMotion;
      }
   }

   public void update(World world) {
      if (this != NONE) {
         if (ForgeWorld.isServerSide(world) && this.changeInterval.pastIn(world) && CommonMath.Random.result(this.changeProbability)) {
            this.randomChangeTargetMotion();
            Wind.WindMsg message = new Wind.WindMsg(this);
            ForgeMain.instance.getConnection().sendToAll(message);
         }

         if (this.updateInterval.pastIn(world)) {
            double x = this.getChangedMotion(this.getMotion().x, this.getTargetMotion().x);
            double y = 0.0D;
            double z = this.getChangedMotion(this.getMotion().z, this.getTargetMotion().z);
            this.currentMotion = Vect3d.from(x, y, z);
         }

      }
   }

   @Nullable
   public ForgeConfig getConfig() {
      return this.config;
   }

   public boolean equalsDefault() {
      return this.strengthBounds.equals(defaultWind.strengthBounds) && this.changeProbability == defaultWind.changeProbability && this.changeStep == defaultWind.changeStep;
   }

   public static class StaticWind implements IWind {
      private final Vect3d motion;

      public StaticWind(Vect3d motion) {
         this.motion = motion;
      }

      public static Wind.StaticWind from(float strength, CommonMath.Angle angle) {
         double x = -angle.sin() * (double)strength;
         double z = angle.cos() * (double)strength;
         Vect3d motion = Vect3d.from(x, 0.0D, z);
         return new Wind.StaticWind(motion);
      }

      public Vect3d getMotion() {
         return this.motion;
      }
   }

   public static class Weather2 {
      protected static Method getWindAngle = null;
      protected static Method getWindSpeed = null;

      public Weather2() {
      }

      public static void initialize() {
         String originalName = "weather2.api.WindDataHelper";
         String remasteredName = "net.mrbt0907.weather2.api.WindDataHelper";
         if (Loader.isModLoaded("weather2")) {
            Class<?> clazz = null;

            try {
               clazz = Class.forName("net.mrbt0907.weather2.api.WindDataHelper");
            } catch (Exception var6) {
               ForgeMain.debugMsg(var6.toString());
            }

            if (clazz == null) {
               try {
                  clazz = Class.forName("weather2.api.WindDataHelper");
               } catch (Exception var5) {
                  ForgeMain.instance.getLogger().error(var5.toString());
               }
            }

            if (clazz != null) {
               try {
                  getWindAngle = clazz.getMethod("getWindAngle", World.class, BlockPos.class);
                  getWindSpeed = clazz.getMethod("getWindSpeed", World.class, BlockPos.class);
               } catch (Exception var4) {
                  ForgeMain.instance.getLogger().error(var4.toString());
               }
            }

         }
      }

      public static Optional<Wind.StaticWind> get(World world, BlockPos pos) {
         if (getWindAngle != null && getWindSpeed != null) {
            try {
               CommonMath.Angle angle = CommonMath.Angle.fromDegrees((double)((Float)getWindAngle.invoke((Object)null, world, pos)).floatValue());
               float speed = ((Float)getWindSpeed.invoke((Object)null, world, pos)).floatValue();
               float strength = CommonMath.Approx.up(speed, Wind.STRENGTH_BOUNDS);
               Wind.StaticWind wind = Wind.StaticWind.from(strength, angle);
               return Optional.of(wind);
            } catch (Exception var6) {
               ;
            }
         }

         return Optional.empty();
      }
   }

   public static class WindMsg extends ForgeNetMsg {
      public int biomeId;
      public Vect3d targetMotion;
      public boolean isDefaultWind;
      public float strengthMax;
      public float changeProbability;
      public float changeStep;

      public WindMsg() {
      }

      public WindMsg(Wind wind) {
         this.biomeId = wind.biomeId;
         this.targetMotion = wind.targetMotion;
         this.isDefaultWind = wind == Wind.defaultWind;
         this.strengthMax = wind.strengthBounds.getMax().floatValue();
         this.changeProbability = wind.changeProbability;
         this.changeStep = wind.changeStep;
      }

      public static class Handler extends ForgeNetMsgHandler<Wind.WindMsg> {
         public Handler() {
         }

         public IMessage onMessage(Wind.WindMsg message, MessageContext ctx) {
            Wind wind = new Wind((ForgeConfig)null, message.biomeId, message.strengthMax, message.changeProbability, message.changeStep);
            wind.targetMotion = message.targetMotion;
            wind.setCurrentMotion(message.targetMotion);
            if (message.isDefaultWind) {
               Vect3d motion = Wind.defaultWind.getMotion();
               wind.setCurrentMotion(motion);
               Wind.defaultWind = wind;
            } else {
               World world = this.getWorld(ctx);
               if (world != null) {
                  ForgeWorld.WorldData data = ForgeWorld.getData(world);
                  Wind oldWind = (Wind)data.biomeWindMap.get(Integer.valueOf(message.biomeId));
                  if (oldWind != null) {
                     wind.setCurrentMotion(oldWind.getMotion());
                  }

                  data.biomeWindMap.put(Integer.valueOf(wind.biomeId), wind);
               }
            }

            return null;
         }
      }
   }
}
