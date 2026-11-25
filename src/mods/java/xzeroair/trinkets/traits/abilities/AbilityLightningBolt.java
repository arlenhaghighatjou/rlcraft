package xzeroair.trinkets.traits.abilities;

import com.google.common.base.Predicate;
import com.google.common.base.Predicates;
import java.util.function.BiFunction;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.effect.EntityLightningBolt;
import net.minecraft.entity.monster.EntityCreeper;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.util.EntityDamageSource;
import net.minecraft.util.EntitySelectors;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.Trinkets;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.client.events.ScreenOverlayEvents;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.particles.EffectsRenderPacket;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.lycanitesmobs.LycanitesCompat;
import xzeroair.trinkets.util.handlers.Counter;
import xzeroair.trinkets.util.helpers.RayTraceHelper;
import xzeroair.trinkets.util.helpers.StringUtils;

public class AbilityLightningBolt extends Ability implements ITickableAbility, IKeyBindInterface {
   protected Predicate<Entity> Targets = Predicates.and(EntitySelectors.NOT_SPECTATING, (ent) -> {
      return ent != null && ent.canBeCollidedWith();
   });

   public AbilityLightningBolt() {
      super("lightning_bolt");
   }

   public void tickAbility(EntityLivingBase entity) {
      LycanitesCompat.removeParalysis(entity);
   }

   protected boolean prepareBolt(EntityLivingBase entity, MagicStats magic, boolean def, boolean aux) {
      float cfgCost = TrinketsConfig.SERVER.Items.ARCING_ORB.attackCost;
      int length = Math.min((int)(300.0D * Math.min(MathHelper.pct((double)magic.getMana(), 0.0D, (double)cfgCost), 1.0D)), 300);
      Counter counter = this.tickHandler.getCounter("heldCounter", length, false, true, false, true, false);
      counter.setLength(length);
      return def;
   }

   protected boolean chargeBolt(EntityLivingBase entity, MagicStats magic, boolean def, boolean aux) {
      float cfgCost = TrinketsConfig.SERVER.Items.ARCING_ORB.attackCost;
      float maxMP = magic.getMaxMana();
      float mp = magic.getMana();
      Counter counter = this.tickHandler.getCounter("heldCounter");
      int tick = counter.getTick();
      float multi = (float)MathHelper.pct((double)tick, 0.0D, (double)counter.getLength());
      float realCost = (float)((double)cfgCost * MathHelper.pct((double)tick, 0.0D, 300.0D));
      if (!counter.Tick()) {
         if (entity instanceof EntityPlayer && entity.world.isRemote) {
            entity.world.playSound((EntityPlayer)entity, entity.getPosition(), SoundEvents.BLOCK_NOTE_BASS, SoundCategory.PLAYERS, 0.5F, (float)StringUtils.getAccurateDouble((double)Math.min(0.6F + 0.4F * multi, 1.0F)));
            ScreenOverlayEvents.instance.SyncCost(realCost);
         }

         return realCost == mp ? false : def;
      } else {
         if (entity instanceof EntityPlayer && entity.world.isRemote) {
            entity.world.playSound((EntityPlayer)entity, entity.getPosition(), SoundEvents.BLOCK_END_GATEWAY_SPAWN, SoundCategory.PLAYERS, 0.5F, 0.5F);
            ScreenOverlayEvents.instance.SyncCost(realCost);
         }

         return false;
      }
   }

   protected boolean castBolt(EntityLivingBase entity, MagicStats magic, boolean def, boolean aux) {
      float cfgCost = TrinketsConfig.SERVER.Items.ARCING_ORB.attackCost;
      float cfgDmg = TrinketsConfig.SERVER.Items.ARCING_ORB.attackDmg;
      float maxMP = magic.getMaxMana();
      float mp = magic.getMana();
      Counter counter = this.tickHandler.getCounter("heldCounter");
      int tick = counter.getTick();
      float multi = (float)MathHelper.pct((double)tick, 0.0D, 300.0D);
      float realCost = (float)StringUtils.getAccurateDouble((double)(cfgCost * multi));
      if ((double)tick > (double)counter.getLength() * 0.1D) {
         float finalDamage = cfgDmg * multi;
         if (magic.spendMana(realCost)) {
            double maxDist = 15.0D;
            Vec3d start = entity.getPositionEyes(1.0F);
            Vec3d lookVec = entity.getLookVec();
            Vec3d end = start.addVector(lookVec.x * 15.0D, lookVec.y * 15.0D, lookVec.z * 15.0D);
            RayTraceResult result = RayTraceHelper.rayTrace(entity, 15.0D);
            Vec3d hitLoc = end;
            if (result != null) {
               if (result.typeOfHit == Type.ENTITY) {
                  Entity hit = result.entityHit;
                  hitLoc = result.hitVec.addVector(0.0D, (double)(hit.height * 0.5F), 0.0D);
               } else if (result.typeOfHit == Type.BLOCK) {
                  hitLoc = result.hitVec;
               }
            }

            if (Trinkets.proxy.getSide() == Side.CLIENT) {
               NetworkHandler.sendToServer(new EffectsRenderPacket(entity, start.x, start.y, start.z, hitLoc.x, hitLoc.y, hitLoc.z, 2515356, 1, 0.9F, 3.0F));
               NetworkHandler.sendToServer(new EffectsRenderPacket(entity, hitLoc.x, hitLoc.y, hitLoc.z, hitLoc.x, hitLoc.y, hitLoc.z, 2515356, 2, 0.9F, 3.0F));
            }

            if (!entity.getEntityWorld().isRemote) {
               boolean pvpEnabled = false;

               try {
                  if (entity instanceof EntityPlayerMP) {
                     pvpEnabled = ((EntityPlayerMP)entity).getServer().isPVPEnabled();
                  }
               } catch (Exception var28) {
                  var28.printStackTrace();
               }

               AxisAlignedBB bb1 = (new AxisAlignedBB(new BlockPos(hitLoc))).grow(1.0D);

               for(Entity e : entity.world.getEntitiesInAABBexcluding(entity, bb1, this.Targets)) {
                  if (!(e instanceof EntityPlayer) || pvpEnabled) {
                     if (multi > 0.33F && e instanceof EntityCreeper) {
                        EntityCreeper creep = (EntityCreeper)e;
                        EntityLightningBolt bolt = new EntityLightningBolt(entity.getEntityWorld(), creep.posX, creep.posY, creep.posZ, true);
                        creep.onStruckByLightning(bolt);
                        creep.extinguish();
                     }

                     e.attackEntityFrom(new EntityDamageSource("lightningBolt", entity), finalDamage);
                  }
               }
            }

            this.reset(entity);
            return true;
         }
      }

      this.reset(entity);
      return def;
   }

   protected void reset(Entity entity) {
      Counter counter = this.tickHandler.getCounter("heldCounter");
      if (counter != null) {
         counter.resetTick();
         counter.setLength(300);
      }

      if (entity.world.isRemote) {
         ScreenOverlayEvents.instance.SyncCost(0.0F);
      }

   }

   @SideOnly(Side.CLIENT)
   public String getKey() {
      return ModKeyBindings.ARCING_ORB_ABILITY.getDisplayName();
   }

   public boolean onKeyPress(Entity entity, boolean Aux) {
      return ((Boolean)Capabilities.getMagicStats(entity, Boolean.valueOf(true), (magic, ret) -> {
         return this.prepareBolt((EntityLivingBase)entity, magic, ret.booleanValue(), Aux);
      })).booleanValue();
   }

   public boolean onKeyDown(Entity entity, boolean Aux) {
      return ((Boolean)Capabilities.getMagicStats(entity, Boolean.valueOf(true), (magic, ret) -> {
         return this.chargeBolt((EntityLivingBase)entity, magic, ret.booleanValue(), Aux);
      })).booleanValue();
   }

   public boolean onKeyRelease(Entity entity, boolean Aux) {
      return ((Boolean)Capabilities.getMagicStats(entity, Boolean.valueOf(true), (magic, ret) -> {
         return this.castBolt((EntityLivingBase)entity, magic, ret.booleanValue(), Aux);
      })).booleanValue();
   }
}
