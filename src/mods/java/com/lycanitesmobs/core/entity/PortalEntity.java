package com.lycanitesmobs.core.entity;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.Utilities;
import com.lycanitesmobs.client.AssetManager;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.item.temp.ItemStaffSummoning;
import com.lycanitesmobs.core.tileentity.TileEntitySummoningPedestal;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.item.ItemStack;
import net.minecraft.network.datasync.DataParameter;
import net.minecraft.network.datasync.DataSerializers;
import net.minecraft.network.datasync.EntityDataManager;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;

public class PortalEntity extends BaseProjectileEntity {
   private double targetX;
   private double targetY;
   private double targetZ;
   public int summonAmount = 0;
   public int summonTick = 0;
   public int summonTime = 100;
   public double portalRange = 32.0D;
   public int summonDuration = 1200;
   public EntityPlayer shootingEntity;
   public Class summonClass;
   public ItemStaffSummoning portalItem;
   public String ownerName;
   public TileEntitySummoningPedestal summoningPedestal;
   protected static final DataParameter<String> OWNER_NAME = EntityDataManager.createKey(PortalEntity.class, DataSerializers.STRING);

   public PortalEntity(World world) {
      super(world);
      this.setStats();
      this.isImmuneToFire = true;
   }

   public PortalEntity(World world, EntityPlayer shooter, Class summonClass, ItemStaffSummoning portalItem) {
      super(world, shooter);
      this.shootingEntity = shooter;
      this.summonClass = summonClass;
      this.portalItem = portalItem;
      this.setStats();
   }

   public PortalEntity(World world, TileEntitySummoningPedestal summoningPedestal) {
      super(world);
      this.summoningPedestal = summoningPedestal;
      this.setStats();
      this.posX = (double)summoningPedestal.getPos().getX() + 0.5D;
      this.posY = (double)summoningPedestal.getPos().getY() + 1.5D;
      this.posZ = (double)summoningPedestal.getPos().getZ() + 0.5D;
   }

   public void setStats() {
      this.entityName = "summoningportal";
      this.setProjectileScale(6.0F);
      this.moveToTarget();
      this.textureOffsetY = -0.5F;
      this.animationFrameMax = 7;
      this.movement = false;
      this.waterProof = true;
      this.lavaProof = true;
      this.dataManager.register(OWNER_NAME, "");
   }

   public void onUpdate() {
      if (this.shootingEntity != null || this.summoningPedestal != null) {
         this.projectileLife = 5;
      }

      super.onUpdate();
      if (!this.getEntityWorld().isRemote && this.summoningPedestal != null) {
         this.shootingEntity = this.summoningPedestal.getPlayer();
         this.summonClass = this.summoningPedestal.getSummonClass();
      }

      if (!this.getEntityWorld().isRemote) {
         if (this.shootingEntity != null) {
            this.dataManager.set(OWNER_NAME, this.shootingEntity.getName());
         } else if (this.summoningPedestal != null) {
            this.dataManager.set(OWNER_NAME, this.summoningPedestal.getOwnerName());
         } else {
            this.dataManager.set(OWNER_NAME, "");
         }
      } else {
         this.ownerName = (String)this.dataManager.get(OWNER_NAME);
      }

      if (!this.getEntityWorld().isRemote && !this.isDead) {
         if (this.summoningPedestal != null) {
            if (this.summonClass == null) {
               this.setDead();
               return;
            }
         } else {
            if (this.shootingEntity == null || !this.shootingEntity.isEntityAlive() || this.portalItem == null) {
               this.setDead();
               return;
            }

            ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.shootingEntity);
            if (playerExt != null && playerExt.staffPortal != this) {
               this.setDead();
               return;
            }
         }
      }

      this.moveToTarget();
      if (this.shootingEntity != null && this.summoningPedestal == null) {
         ExtendedPlayer playerExt = ExtendedPlayer.getForPlayer(this.shootingEntity);
         if (playerExt != null && this.portalItem != null && ++this.summonTick >= this.portalItem.getRapidTime((ItemStack)null)) {
            this.summonDuration = this.portalItem.getSummonDuration();
            if (this.shootingEntity.capabilities.isCreativeMode) {
               this.summonAmount += this.portalItem.getSummonAmount();
            } else {
               float summonMultiplier = (float)(CreatureManager.getInstance().getCreature(this.summonClass).summonCost + this.portalItem.getSummonCostBoost()) * this.portalItem.getSummonCostMod();
               int summonCost = Math.round((float)playerExt.summonFocusCharge * summonMultiplier);
               if (playerExt.summonFocus >= summonCost && this.portalItem.getAdditionalCosts(this.shootingEntity)) {
                  playerExt.summonFocus -= summonCost;
                  this.summonAmount += this.portalItem.getSummonAmount();
               }
            }

            this.summonTick = 0;
         }
      } else if (this.summonClass != null && ++this.summonTick >= this.summonTime) {
         this.summonAmount = this.summoningPedestal.summonAmount;
         this.summonTick = 0;
      }

      if (this.getEntityWorld().isRemote) {
         for(int i = 0; i < 32; ++i) {
            double angle = Math.toRadians((double)(this.rand.nextFloat() * 360.0F));
            float distance = this.rand.nextFloat() * 0.5F;
            double x = (double)distance * Math.cos(angle) + Math.sin(angle);
            double z = (double)distance * Math.sin(angle) - Math.cos(angle);
            this.getEntityWorld().spawnParticle(EnumParticleTypes.PORTAL, this.posX + x, this.posY + (double)(6.0F * this.rand.nextFloat()) - 2.0D, this.posZ + z, ((double)this.rand.nextFloat() - 0.5D) * 4.0D, 0.0D, ((double)this.rand.nextFloat() - 0.5D) * 4.0D, new int[0]);
         }
      }

   }

   public int summonCreatures() {
      if (this.getEntityWorld().isRemote) {
         return 1;
      } else if (this.summonClass == null) {
         return 0;
      } else {
         for(int i = 0; i < this.summonAmount; ++i) {
            Entity entity = null;

            try {
               entity = (Entity)this.summonClass.getConstructor(World.class).newInstance(this.getEntityWorld());
            } catch (Exception var4) {
               LycanitesMobs.logWarning("", "A none Entity class type was passed to an EntityPortal, only entities can be summoned from portals!");
               var4.printStackTrace();
            }

            if (entity == null) {
               return 0;
            }

            entity.setLocationAndAngles(this.posX, this.posY, this.posZ, this.rand.nextFloat() * 360.0F, 0.0F);
            if (entity instanceof BaseCreatureEntity) {
               BaseCreatureEntity entityCreature = (BaseCreatureEntity)entity;
               if (this.shootingEntity != null && this.summoningPedestal == null) {
                  entityCreature.setMinion(true);
                  if (entityCreature instanceof TameableCreatureEntity) {
                     ((TameableCreatureEntity)entityCreature).setPlayerOwner(this.shootingEntity);
                     if (this.portalItem != null) {
                        this.portalItem.applyMinionBehaviour((TameableCreatureEntity)entityCreature, this.shootingEntity);
                        this.portalItem.applyMinionEffects(entityCreature);
                     }
                  }
               } else if (this.summoningPedestal != null && this.summoningPedestal.getOwnerUUID() != null) {
                  entityCreature.setMinion(true);
                  entityCreature.summoningPedestal = this.summoningPedestal;
                  if (entityCreature instanceof TameableCreatureEntity) {
                     ((TameableCreatureEntity)entityCreature).setOwnerId(this.summoningPedestal.getOwnerUUID());
                     this.summoningPedestal.applyMinionBehaviour((TameableCreatureEntity)entityCreature);
                  }
               }

               if (this.summonDuration > 0) {
                  entityCreature.setTemporary(this.summonDuration);
               }

               if (this.shootingEntity != null) {
                  this.shootingEntity.addStat(ObjectManager.getStat(entityCreature.creatureInfo.getName() + ".summon"), 1);
               }
            }

            this.getEntityWorld().spawnEntity(entity);
         }

         int amount = this.summonAmount;
         this.summonAmount = 0;
         return amount;
      }
   }

   protected float getGravityVelocity() {
      return 0.0F;
   }

   public void moveToTarget() {
      if (this.shootingEntity != null && this.summoningPedestal == null) {
         Vec3d lookDirection = this.shootingEntity.getLookVec();
         this.targetX = this.shootingEntity.posX + lookDirection.x * this.portalRange;
         this.targetY = this.shootingEntity.posY + (double)this.shootingEntity.getEyeHeight() + lookDirection.y * this.portalRange;
         this.targetZ = this.shootingEntity.posZ + lookDirection.z * this.portalRange;
         RayTraceResult target = Utilities.raytraceBlocks(this.getEntityWorld(), this.shootingEntity.getPositionEyes(1.0F), new Vec3d(this.targetX, this.targetY, this.targetZ), false, true);
         if (target != null && target.hitVec != null) {
            this.targetX = target.hitVec.x;
            this.targetY = target.hitVec.y;
            this.targetZ = target.hitVec.z;
         }

         this.posX = this.targetX;
         this.posY = this.targetY;
         this.posZ = this.targetZ;
      }

   }

   public double[] getFacingPosition(Entity entity, double distance) {
      double angle = Math.toRadians((double)this.rotationYaw);
      double xAmount = -Math.sin(angle);
      double zAmount = Math.cos(angle);
      double[] coords = new double[3];
      coords[0] = entity.posX + distance * xAmount;
      coords[1] = entity.posY;
      coords[2] = entity.posZ + distance * zAmount;
      return coords;
   }

   protected void onImpact(RayTraceResult movingObjectPos) {
   }

   public ResourceLocation getTexture() {
      if (AssetManager.getTexture(this.entityName) == null) {
         AssetManager.addTexture(this.entityName, LycanitesMobs.modInfo, "textures/particles/" + this.entityName.toLowerCase() + ".png");
      }

      if (AssetManager.getTexture(this.entityName + "_client") == null) {
         AssetManager.addTexture(this.entityName + "_client", LycanitesMobs.modInfo, "textures/particles/" + this.entityName.toLowerCase() + "_client.png");
      }

      if (AssetManager.getTexture(this.entityName + "_player") == null) {
         AssetManager.addTexture(this.entityName + "_player", LycanitesMobs.modInfo, "textures/particles/" + this.entityName.toLowerCase() + "_player.png");
      }

      if (this.ownerName != null) {
         if (this.ownerName.equalsIgnoreCase(LycanitesMobs.proxy.getClientPlayer().getName())) {
            return AssetManager.getTexture(this.entityName + "_client");
         }

         if (!this.ownerName.equalsIgnoreCase("")) {
            return AssetManager.getTexture(this.entityName + "_player");
         }
      }

      return AssetManager.getTexture(this.entityName);
   }

   public float getTextureOffsetY() {
      return this.height / 2.0F;
   }
}
