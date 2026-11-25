package xzeroair.trinkets.traits.abilities;

import java.util.List;
import java.util.Random;
import net.minecraft.block.state.IBlockState;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityLiving;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityEnderman;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.SoundEvents;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.potion.Potion;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.EntityDamageSourceIndirect;
import net.minecraft.util.EnumParticleTypes;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.entity.living.EnderTeleportEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import xzeroair.trinkets.api.TrinketHelper;
import xzeroair.trinkets.capabilities.Capabilities;
import xzeroair.trinkets.capabilities.Vip.VipStatus;
import xzeroair.trinkets.capabilities.magic.MagicStats;
import xzeroair.trinkets.client.keybinds.ModKeyBindings;
import xzeroair.trinkets.init.ModItems;
import xzeroair.trinkets.init.TrinketsDamageSource;
import xzeroair.trinkets.traits.abilities.interfaces.IAttackAbility;
import xzeroair.trinkets.traits.abilities.interfaces.IKeyBindInterface;
import xzeroair.trinkets.traits.abilities.interfaces.IPotionAbility;
import xzeroair.trinkets.traits.abilities.interfaces.ITickableAbility;
import xzeroair.trinkets.util.Reference;
import xzeroair.trinkets.util.TrinketsConfig;
import xzeroair.trinkets.util.compat.lycanitesmobs.LycanitesCompat;
import xzeroair.trinkets.util.config.trinkets.ConfigEnderCrown;
import xzeroair.trinkets.util.handlers.Counter;
import xzeroair.trinkets.util.helpers.StringUtils;

public class AbilityEnderQueen extends Ability implements ITickableAbility, IPotionAbility, IAttackAbility, IKeyBindInterface {
   protected static final ConfigEnderCrown serverConfig;

   public AbilityEnderQueen() {
      super("ender_queen");
   }

   public void tickAbility(EntityLivingBase entity) {
      if (!entity.world.isRemote) {
         LycanitesCompat.removeInstability(entity);
         if (serverConfig.water_hurts) {
            Counter counter = this.tickHandler.getCounter("water_hurt", 20, true, true, true, true);
            if (counter.Tick() && (entity.isInWater() || entity.isWet())) {
               MagicStats magic = Capabilities.getMagicStats(entity);
               if (magic != null && magic.spendMana(5.0F)) {
                  magic.setManaRegenTimeout(TrinketsConfig.SERVER.mana.mana_regen_timeout * 2);
               } else if (TrinketHelper.AccessoryCheck(entity, ModItems.trinkets.TrinketDragonsEye)) {
                  entity.attackEntityFrom(TrinketsDamageSource.water.setDamageBypassesArmor().setMagicDamage(), 4.0F);
               } else {
                  entity.attackEntityFrom(TrinketsDamageSource.water.setDamageBypassesArmor().setMagicDamage(), 2.0F);
               }
            }
         }

      }
   }

   public boolean potionApplied(EntityLivingBase entity, PotionEffect effect, boolean cancel) {
      String e = effect.getPotion().getRegistryName().toString();
      Potion instability = LycanitesCompat.getPotionEffectByName("instability");
      return instability != null && e.contentEquals(instability.getRegistryName().toString()) ? true : cancel;
   }

   public boolean attacked(EntityLivingBase attacked, DamageSource source, float dmg, boolean cancel) {
      boolean client = attacked.getEntityWorld().isRemote;
      Entity attacker = source.getTrueSource();
      if (attacker instanceof EntityLivingBase && attacker != attacked && dmg > 0.0F) {
         if (serverConfig.dmgChance || serverConfig.spawnChance) {
            int chanceNum = serverConfig.chance;
            if (chanceNum > 0) {
               int chance = this.random.nextInt(chanceNum);
               if (chance == 0) {
                  String string = "The void protects me!";
                  String string2 = "Go, my loyal subject!";
                  if (TrinketsConfig.SERVER.misc.retrieveVIP) {
                     VipStatus vip = Capabilities.getVipStatus(attacked);
                     if (vip != null) {
                        String quote = vip.getRandomQuote();
                        if (!quote.isEmpty()) {
                           string = quote;
                        }
                     }
                  }

                  if (serverConfig.spawnChance) {
                     if (client) {
                        StringUtils.sendMessageToPlayer(attacked, TextFormatting.BOLD + "" + TextFormatting.GOLD + "Go, my loyal subject!", false);
                     }

                     if (!client) {
                        try {
                           EntityEnderman knight = new EntityEnderman(attacked.getEntityWorld());
                           double x = (double)attacked.getPosition().getX();
                           double y = (double)attacked.getPosition().getY();
                           double z = (double)attacked.getPosition().getZ();
                           knight.setPosition(x, y, z);
                           knight.getEntityData().setBoolean("xat:summoned", true);
                           knight.setCanPickUpLoot(false);
                           attacked.getEntityWorld().spawnEntity(knight);
                           knight.setAttackTarget((EntityLivingBase)attacker);
                        } catch (Exception var18) {
                           var18.printStackTrace();
                        }
                     }
                  }

                  if (serverConfig.dmgChance) {
                     cancel = true;
                     if (client) {
                        StringUtils.sendMessageToPlayer(attacked, TextFormatting.BOLD + "" + TextFormatting.GOLD + string, false);
                     }
                  }
               }
            }
         }

         if (serverConfig.teleportOnHurt && source instanceof EntityDamageSourceIndirect) {
            int chanceNum = serverConfig.teleportChance;
            if (chanceNum > 0) {
               int chance = this.random.nextInt(chanceNum);
               if (chance == 0) {
                  boolean isBoss = source.getTrueSource() != null && !source.getTrueSource().isNonBoss();
                  MagicStats magic = Capabilities.getMagicStats(attacked);
                  if (!isBoss && !attacked.isActiveItemStackBlocking() && magic != null && magic.getMana() >= magic.getMaxMana() * 0.5F) {
                     for(int i = 0; i < 32; ++i) {
                        if (this.teleportRandomly(attacked) && magic.spendMana(magic.getMaxMana() * 0.5F)) {
                           cancel = true;
                           break;
                        }
                     }
                  }
               }
            }
         }
      }

      return cancel;
   }

   public void targetedByEnemy(EntityLivingBase enemy) {
      if (!serverConfig.attackBack && enemy instanceof EntityEnderman && enemy instanceof EntityLiving) {
         EntityLivingBase target = ((EntityLiving)enemy).getAttackTarget();
         if (target != null) {
            ((EntityLiving)enemy).setAttackTarget((EntityLivingBase)null);
         }
      }

   }

   public int killedEntityExpDrop(EntityLivingBase target, int originalExp, int droppedExp) {
      return !serverConfig.expDrop && target instanceof EntityEnderman ? 0 : droppedExp;
   }

   public void killedEntityItemDrops(EntityLivingBase target, DamageSource source, int lootingLevel, List<EntityItem> drops) {
      if (!serverConfig.itemDrop && target instanceof EntityEnderman && !drops.isEmpty()) {
         drops.clear();
      }

   }

   public float damageEntity(EntityLivingBase target, DamageSource source, float dmg) {
      if (target instanceof EntityEnderman && dmg > 0.0F) {
         try {
            EntityEnderman enderman = (EntityEnderman)target;
            NBTTagCompound data = enderman.getEntityData();
            data.setBoolean("xat:summoned", true);
         } catch (Exception var6) {
            ;
         }
      }

      return dmg;
   }

   protected boolean teleportRandomly(EntityLivingBase entity) {
      double d0 = entity.posX + (Reference.random.nextDouble() - 0.5D) * 32.0D;
      double d1 = entity.posY + (double)(Reference.random.nextInt(16) - 8);
      double d2 = entity.posZ + (Reference.random.nextDouble() - 0.5D) * 32.0D;
      boolean teleport = this.teleportTo(entity, d0, d1, d2);
      return teleport;
   }

   private boolean teleportTo(EntityLivingBase entity, double x, double y, double z) {
      EnderTeleportEvent event = new EnderTeleportEvent(entity, x, y, z, 0.0F);
      if (MinecraftForge.EVENT_BUS.post(event)) {
         return false;
      } else if (entity instanceof EntityPlayer) {
         if (entity instanceof EntityPlayerMP) {
            if (entity.isRiding()) {
               entity.dismountRidingEntity();
            }

            if (entity.attemptTeleport(x, y, z)) {
               if (entity.fallDistance > 0.0F) {
                  entity.fallDistance = 0.0F;
               }

               entity.lastTickPosX = entity.posX;
               entity.lastTickPosY = entity.posY;
               entity.lastTickPosZ = entity.posZ;
               entity.world.playSound((EntityPlayer)null, x, y, z, SoundEvents.ENTITY_ENDERMEN_TELEPORT, SoundCategory.PLAYERS, 1.0F, 1.0F);
               return true;
            }
         }

         return false;
      } else {
         boolean flag = this.attemptTeleport(entity, x, y, z);
         if (flag) {
            entity.world.playSound((EntityPlayer)null, entity.prevPosX, entity.prevPosY, entity.prevPosZ, SoundEvents.ENTITY_ENDERMEN_TELEPORT, entity.getSoundCategory(), 1.0F, 1.0F);
            entity.playSound(SoundEvents.ENTITY_ENDERMEN_TELEPORT, 1.0F, 1.0F);
         }

         return flag;
      }
   }

   protected boolean teleportToEntity(EntityLivingBase entity, Entity target) {
      Vec3d vec3d = new Vec3d(entity.posX - target.posX, entity.getEntityBoundingBox().minY + (double)(entity.height / 2.0F) - target.posY + (double)target.getEyeHeight(), entity.posZ - target.posZ);
      vec3d = vec3d.normalize();
      double d0 = 16.0D;
      double d1 = entity.posX + (Reference.random.nextDouble() - 0.5D) * 8.0D - vec3d.x * 16.0D;
      double d2 = entity.posY + (double)(Reference.random.nextInt(16) - 8) - vec3d.y * 16.0D;
      double d3 = entity.posZ + (Reference.random.nextDouble() - 0.5D) * 8.0D - vec3d.z * 16.0D;
      return this.teleportTo(entity, d1, d2, d3);
   }

   protected boolean attemptTeleport(EntityLivingBase entity, double x, double y, double z) {
      double d0 = entity.posX;
      double d1 = entity.posY;
      double d2 = entity.posZ;
      entity.posX = x;
      entity.posY = y;
      entity.posZ = z;
      boolean flag = false;
      BlockPos blockpos = new BlockPos(entity);
      World world = entity.world;
      Random random = this.random;
      if (world.isBlockLoaded(blockpos)) {
         boolean flag1 = false;

         while(!flag1 && blockpos.getY() > 0) {
            BlockPos blockpos1 = blockpos.down();
            IBlockState iblockstate = world.getBlockState(blockpos1);
            if (iblockstate.getMaterial().blocksMovement()) {
               flag1 = true;
            } else {
               --entity.posY;
               blockpos = blockpos1;
            }
         }

         if (flag1) {
            entity.setPositionAndUpdate(entity.posX, entity.posY, entity.posZ);
            if (world.getCollisionBoxes(entity, entity.getEntityBoundingBox()).isEmpty() && !world.containsAnyLiquid(entity.getEntityBoundingBox())) {
               flag = true;
            }
         }
      }

      if (!flag) {
         entity.setPositionAndUpdate(d0, d1, d2);
         return false;
      } else {
         int i = 128;

         for(int j = 0; j < 128; ++j) {
            double d6 = (double)j / 127.0D;
            float f = (random.nextFloat() - 0.5F) * 0.2F;
            float f1 = (random.nextFloat() - 0.5F) * 0.2F;
            float f2 = (random.nextFloat() - 0.5F) * 0.2F;
            double d3 = d0 + (entity.posX - d0) * d6 + (random.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
            double d4 = d1 + (entity.posY - d1) * d6 + random.nextDouble() * (double)entity.height;
            double d5 = d2 + (entity.posZ - d2) * d6 + (random.nextDouble() - 0.5D) * (double)entity.width * 2.0D;
            world.spawnParticle(EnumParticleTypes.PORTAL, d3, d4, d5, (double)f, (double)f1, (double)f2, new int[0]);
         }

         if (entity instanceof EntityCreature) {
            ((EntityCreature)entity).getNavigator().clearPath();
         }

         return true;
      }
   }

   @SideOnly(Side.CLIENT)
   public String getKey() {
      return ModKeyBindings.RACE_ABILITY.getDisplayName();
   }

   @SideOnly(Side.CLIENT)
   public String getAuxKey() {
      return ModKeyBindings.AUX_KEY.getDisplayName();
   }

   public boolean onKeyPress(Entity entity, boolean Aux) {
      World world = entity.getEntityWorld();
      return true;
   }

   static {
      serverConfig = TrinketsConfig.SERVER.Items.ENDER_CROWN;
   }
}
