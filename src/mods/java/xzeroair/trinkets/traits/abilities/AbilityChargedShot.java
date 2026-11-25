package xzeroair.trinkets.traits.abilities;

import java.util.List;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.init.SoundEvents;
import net.minecraft.item.ItemBow;
import net.minecraft.item.ItemStack;
import net.minecraft.util.DamageSource;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.RayTraceResult.Type;
import net.minecraft.world.World;
import net.minecraftforge.event.entity.ProjectileImpactEvent.Arrow;
import net.minecraftforge.event.entity.player.ArrowLooseEvent;
import net.minecraftforge.event.entity.player.ArrowNockEvent;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.network.NetworkHandler;
import xzeroair.trinkets.network.particles.EffectsRenderPacket;
import xzeroair.trinkets.races.elf.config.ElfConfig;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IBowAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.config.ConfigHelper;

public class AbilityChargedShot extends Ability implements ITickableAbility, IBowAbility, IAttackAbility {
   public static final ElfConfig serverConfig;
   protected boolean critReady;
   protected boolean hit;
   protected boolean doDamage;
   protected boolean useMP;
   protected boolean doAbility;
   protected boolean pullingBow;
   protected boolean release;
   protected boolean cancel;
   protected boolean doExplosion = false;
   protected int v1;
   protected int v2;
   protected int v3;
   protected int poundage;
   protected int tension;
   protected int resistance = 0;
   protected float chargeMultiplier;
   protected float damageMultiplier;
   protected float pounds;
   protected float f1;
   protected float f2;
   protected float f3 = 0.0F;
   protected int heldDuration = 0;

   public AbilityChargedShot() {
      super("charged_shot");
   }

   public void tickAbility(EntityLivingBase entity) {
      float ManaCost = serverConfig.CS_Cost;
      boolean blacklistedBow = false;
      if (entity.getActiveItemStack() != null && !entity.getActiveItemStack().isEmpty()) {
         try {
            String[] configList = serverConfig.bowBlacklist;

            for(String s : configList) {
               ConfigHelper.ConfigObject object = new ConfigHelper.ConfigObject(s);
               if (object.doesItemMatchEntry(entity.getActiveItemStack())) {
                  blacklistedBow = true;
                  break;
               }
            }
         } catch (Exception var11) {
            var11.printStackTrace();
         }
      }

      boolean bow = entity.isHandActive() && entity.getActiveItemStack().getItem() instanceof ItemBow && !blacklistedBow;
      MagicStats magic = Capabilities.getMagicStats(entity);
      boolean sneaking = entity.isSneaking();
      if (magic != null) {
         if (sneaking) {
            if (bow) {
               if (!this.pullingBow) {
                  this.cancel();
                  this.heldDuration = serverConfig.ChargeTime / 10;
               } else {
                  ++this.heldDuration;
               }

               this.doAbility = true;
               this.pullingBow = true;
               if (serverConfig.ChargeTime < 12) {
                  serverConfig.ChargeTime = 12;
               }

               if (this.heldDuration > serverConfig.ChargeTime) {
                  this.heldDuration = serverConfig.ChargeTime;
               }

               float mana = magic.getMana();
               float scale = (float)((double)(this.heldDuration * 100 / serverConfig.ChargeTime) * 0.01D);
               float testCost = ManaCost * scale * 10.0F;
               if (testCost > 0.0F && testCost > mana) {
                  scale = mana / testCost;
                  this.cancel = true;
               } else {
                  this.cancel = false;
               }

               if (scale > 0.0F && (scale <= 0.0F || scale > 0.25F) && (scale <= 0.25F || scale > 0.41F) && (scale <= 0.41F || scale > 0.65F) && (scale <= 0.65F || scale > 1.0F) && scale > 1.0F) {
                  ;
               }

               if (scale > 0.0F) {
                  if (!this.cancel) {
                     if (scale < 1.0F) {
                        if (entity.world.isRemote && entity instanceof EntityPlayer) {
                           entity.world.playSound((EntityPlayer)entity, entity.getPosition(), SoundEvents.BLOCK_NOTE_GUITAR, SoundCategory.PLAYERS, 0.1F, scale + 0.5F);
                        }

                        this.critReady = false;
                     } else if (!this.critReady) {
                        if (entity.world.isRemote && entity instanceof EntityPlayer) {
                           entity.world.playSound((EntityPlayer)entity, entity.getPosition(), SoundEvents.EVOCATION_ILLAGER_CAST_SPELL, SoundCategory.PLAYERS, 1.0F, 0.5F);
                        }

                        this.critReady = true;
                     }
                  }

                  float Cost = MathHelper.clamp(ManaCost * scale * 10.0F, 0.0F, magic.getMana());
                  magic.syncToManaCostToHud(Cost);
               }

            } else {
               if (this.pullingBow) {
                  magic.syncToManaCostToHud(0.0F);
                  this.cancel();
                  this.pullingBow = false;
               }

            }
         } else {
            if (this.doAbility || this.pullingBow) {
               magic.syncToManaCostToHud(0.0F);
               this.reset();
            }

         }
      }
   }

   public void knockArrow(ArrowNockEvent event) {
      if (!event.getWorld().isRemote) {
         this.doAbility = false;
         this.release = false;
         this.hit = false;
         this.doDamage = false;
         this.critReady = false;
         this.doExplosion = false;
         this.useMP = false;
      }

   }

   public int onItemUseTick(EntityLivingBase entity, ItemStack stack, int duration) {
      return duration;
   }

   public void onItemUseStop(EntityLivingBase entity, ItemStack stack, int duration) {
      if (!entity.world.isRemote && this.doAbility) {
         if (!this.release) {
            float ManaCost = serverConfig.CS_Cost;
            MagicStats magic = Capabilities.getMagicStats(entity);
            float mana = magic.getMana();
            float scale = (float)((double)(this.heldDuration * 100 / serverConfig.ChargeTime) * 0.01D);
            float testCost = ManaCost * scale * 10.0F;
            if (testCost > 0.0F && testCost > mana) {
               scale = mana / testCost;
            }

            float Cost = MathHelper.clamp(ManaCost * scale * 10.0F, 0.0F, magic.getMana());
            if (magic.spendMana(Cost)) {
               this.damageMultiplier = scale;
               this.useMP = true;
            } else {
               this.damageMultiplier = 0.0F;
               this.cancel();
            }

            magic.syncToManaCostToHud(0.0F);
         }

         this.pullingBow = false;
         this.release = true;
      }

   }

   public void looseArrow(ArrowLooseEvent event) {
   }

   public void arrowImpact(Arrow event) {
      World world = event.getEntity().getEntityWorld();
      RayTraceResult rayTraceResult = event.getRayTraceResult();
      EntityArrow arrow = event.getArrow();
      if (!world.isRemote && rayTraceResult.typeOfHit != Type.MISS && this.doAbility) {
         if (rayTraceResult.typeOfHit == Type.ENTITY) {
            this.hit = true;
            this.doDamage = true;
         } else if (this.useMP && this.critReady && serverConfig.explode) {
            world.createExplosion(arrow, arrow.posX, arrow.posY, arrow.posZ, 2.0F, false);
            this.useMP = false;
         }
      }

   }

   public float hurtEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if ((source.damageType.contentEquals("arrow") || source.isProjectile() && !source.isMagicDamage()) && this.damageMultiplier > 0.0F) {
         float baseMultiplier = serverConfig.baseMultiplier;
         float adjustedValue = dmg * baseMultiplier;
         if (adjustedValue < serverConfig.minimumDamageMultiplier) {
            adjustedValue = serverConfig.minimumDamageMultiplier;
         }

         dmg += adjustedValue * this.damageMultiplier;

         try {
            if (this.useMP && this.critReady && serverConfig.explode) {
               Entity attacker = source.getTrueSource();
               NetworkHandler.sendToTracking(new EffectsRenderPacket(attacker, source.getImmediateSource().posX, source.getImmediateSource().posY, source.getImmediateSource().posZ, 0.0D, 0.0D, 0.0D, 0, 5, 0.0F, 0.0F), source.getImmediateSource());
               List<Entity> splash = attacker.world.getEntitiesWithinAABBExcludingEntity(target, target.getEntityBoundingBox().grow(1.0D));
               DamageSource source1 = DamageSource.causeIndirectDamage(source.getImmediateSource(), (EntityLivingBase)attacker).setProjectile().setMagicDamage();

               for(Entity e : splash) {
                  if (e instanceof EntityLivingBase && e != attacker) {
                     e.attackEntityFrom(source1, dmg * 0.75F);
                  }
               }
            }
         } catch (Exception var11) {
            ;
         }

         this.cancel();
      }

      return dmg;
   }

   private void reset() {
      this.cancel();
      this.pullingBow = false;
   }

   private void cancel() {
      this.doAbility = false;
      this.release = false;
      this.hit = false;
      this.doDamage = false;
      this.critReady = false;
      this.doExplosion = false;
      this.useMP = false;
      this.heldDuration = 0;
      this.damageMultiplier = 0.0F;
   }

   public void onAbilityRemoved(EntityLivingBase entity) {
      MagicStats magic = Capabilities.getMagicStats(entity);
      magic.syncToManaCostToHud(0.0F);
   }

   static {
      serverConfig = TrinketsConfig.SERVER.races.elf;
   }
}
