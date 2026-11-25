package xzeroair.trinkets.traits.abilities;

import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.util.EnumFacing.Axis;
import net.minecraft.util.EnumFacing.AxisDirection;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.statushandler.StatusHandler;
import xzeroair.trinkets.capabilities.statushandler.TrinketStatusEffect;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.particles.EffectsRenderPacket;
import xzeroair.trinkets.traits.abilities.interfaces.IMovementAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.traits.statuseffects.StatusEffectsEnum;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.trinkets.ConfigArcingOrb;
import xzeroair.trinkets.util.handlers.Counter;

public class AbilityDodge extends Ability implements ITickableAbility, IMovementAbility {
   public final ConfigArcingOrb serverConfig;
   int keyPresses;
   String keyPressed;
   int direction;
   Long lastKeyPress;
   boolean trigger;

   public AbilityDodge() {
      super("dodging");
      this.serverConfig = TrinketsConfig.SERVER.Items.ARCING_ORB;
      this.keyPresses = 0;
      this.direction = -1;
      this.lastKeyPress = -1L;
   }

   public void tickAbility(EntityLivingBase entity) {
      Counter counter = this.tickHandler.getCounter("lastKeyPress");
      if (counter != null && counter.Tick()) {
         this.reset();
      }

   }

   public void handleDodge(Entity entity, int direction) {
      if (entity != null) {
         float yaw = entity.rotationYaw;
         Vec3d look = entity.getLookVec();
         AxisDirection facing = entity.getHorizontalFacing().getAxisDirection();
         Axis axis = entity.getHorizontalFacing().getAxis();
         double x = 0.0D;
         double z = 0.0D;
         if (axis == Axis.Z) {
            if (facing == AxisDirection.POSITIVE) {
               x = entity.getLookVec().z;
               z = -entity.getLookVec().x;
            } else {
               x = entity.getLookVec().z;
               z = -entity.getLookVec().x;
            }
         } else if (facing == AxisDirection.POSITIVE) {
            x = entity.getLookVec().z;
            z = -entity.getLookVec().x;
         } else {
            x = entity.getLookVec().z;
            z = -entity.getLookVec().x;
         }

         double spd = 1.25D;
         double xV = 0.0D;
         double yV = 0.0D;
         double zV = 0.0D;
         if (direction == 1) {
            xV = look.z * 1.25D;
            yV = 0.3D;
            zV = -look.x * 1.25D;
         } else if (direction == 3) {
            xV = -look.z * 1.25D;
            yV = 0.3D;
            zV = look.x * 1.25D;
         } else if (direction == 2) {
            xV = look.x * 1.25D;
            yV = 0.3D;
            zV = look.z * 1.25D;
         } else if (direction == 0) {
            xV = -look.x * 2.0D;
            yV = 0.5D;
            zV = -look.z * 2.0D;
         } else {
            xV = 0.0D;
            yV = 0.0D;
            zV = 0.0D;
         }

         if (entity.onGround) {
            if (entity.world.isRemote) {
               entity.setVelocity(xV, yV, zV);
            } else {
               this.dodge(entity);
            }
         }

      }
   }

   public void dodge(Entity entity) {
      if (!entity.getEntityWorld().isRemote) {
         World w = entity.getEntityWorld();
         if (w instanceof WorldServer) {
            WorldServer world = (WorldServer)w;
            NetworkHandler.sendToClients(world, entity.getPosition(), new EffectsRenderPacket(entity, entity.posX, entity.posY + (double)(entity.height * 0.5F), entity.posZ, entity.posX, entity.posY, entity.posZ, 2515356, 2, 1.0F, 1.0F));
         }
      }

      double distance = TrinketsConfig.SERVER.Items.ARCING_ORB.stunDistance;

      for(EntityLivingBase targetEntity : entity.world.getEntitiesWithinAABB(EntityLivingBase.class, entity.getEntityBoundingBox().grow(distance, 1.0D, distance))) {
         if (targetEntity != entity) {
            StatusHandler status = Capabilities.getStatusHandler(targetEntity);
            if (status != null) {
               TrinketStatusEffect effect = new TrinketStatusEffect(StatusEffectsEnum.paralysis, 60, 1, entity);
               status.apply(effect);
            }
         }
      }

   }

   public boolean triggerDodge(Entity entity) {
      if (!this.trigger) {
         return false;
      } else {
         this.trigger = false;
         if (!entity.world.isRemote || entity.onGround && !entity.isSneaking()) {
            if (Trinkets.ElenaiDodge1 && TrinketsConfig.compat.elenaiDodge) {
               return false;
            } else {
               float cost = this.serverConfig.dodgeCost;
               return ((Boolean)Capabilities.getMagicStats(entity, Boolean.valueOf(true), (magic, rtn) -> {
                  return magic.spendMana(cost);
               })).booleanValue();
            }
         } else {
            return false;
         }
      }
   }

   public boolean left(Entity entity, int state) {
      if (state == 0) {
         if (this.handleKeys(AbilityDodge.EnumDirection.Left)) {
            if (!this.triggerDodge(entity)) {
               return false;
            }

            this.handleDodge(entity, 1);
         }

         return true;
      } else {
         return state == 1 ? true : true;
      }
   }

   public boolean right(Entity entity, int state) {
      if (state == 0) {
         if (this.handleKeys(AbilityDodge.EnumDirection.Right)) {
            if (!this.triggerDodge(entity)) {
               return false;
            }

            this.handleDodge(entity, 3);
         }

         return true;
      } else {
         return state == 1 ? true : true;
      }
   }

   public boolean forward(Entity entity, int state) {
      if (state == 0) {
         if (this.handleKeys(AbilityDodge.EnumDirection.Forward)) {
            if (!this.triggerDodge(entity)) {
               return false;
            }

            this.handleDodge(entity, 2);
         }

         return true;
      } else {
         return state == 1 ? true : true;
      }
   }

   public boolean back(Entity entity, int state) {
      if (state == 0) {
         if (this.handleKeys(AbilityDodge.EnumDirection.Back)) {
            if (!this.triggerDodge(entity)) {
               return false;
            }

            this.handleDodge(entity, 0);
         }

         return true;
      } else {
         return state == 1 ? true : true;
      }
   }

   public boolean jump(Entity entity, int state) {
      return false;
   }

   public boolean sneak(Entity entity, int state) {
      return false;
   }

   private void reset() {
      this.trigger = false;
      this.keyPresses = 0;
      this.lastKeyPress = -1L;
      this.direction = -1;
      this.tickHandler.removeCounter("lastKeyPress");
   }

   private boolean handleKeys(int direction) {
      if (this.direction != direction) {
         this.reset();
      }

      Counter counter = this.tickHandler.getCounter("lastKeyPress");
      if (counter != null) {
         ++this.keyPresses;
      } else {
         Counter newCounter = this.tickHandler.getCounter("lastKeyPress", 3, true, true, true, false);
         this.direction = direction;
         this.keyPresses = 1;
      }

      if (this.keyPresses >= 2) {
         this.reset();
         this.trigger = true;
      }

      return this.trigger;
   }

   private boolean handleKeys(AbilityDodge.EnumDirection direction) {
      return this.handleKeys(direction.getID());
   }

   private static enum EnumDirection {
      Left(1),
      Right(3),
      Forward(2),
      Back(0),
      Up(4),
      Down(5);

      private final int ID;

      private EnumDirection(int direction) {
         this.ID = direction;
      }

      public int getID() {
         return this.ID;
      }
   }
}
