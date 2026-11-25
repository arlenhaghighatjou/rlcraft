package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.SRPMain;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleBiomass;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleFog;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleMultipleGore;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleRHappy;
import com.dhanantry.scapeandrunparasites.client.particle.ParticleSpore;
import com.dhanantry.scapeandrunparasites.entity.ai.EntityAISkill;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPMalleable;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityPStationary;
import com.dhanantry.scapeandrunparasites.entity.ai.misc.EntityParasiteBase;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.init.SRPPotions;
import com.dhanantry.scapeandrunparasites.item.tool.IHaveReach;
import com.dhanantry.scapeandrunparasites.item.tool.WeaponToolArmorBase;
import com.dhanantry.scapeandrunparasites.network.SRPPacketFog;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMeleeRange;
import com.dhanantry.scapeandrunparasites.network.SRPPacketMovingSound;
import com.dhanantry.scapeandrunparasites.network.SRPPacketParticle;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventEntity;
import com.dhanantry.scapeandrunparasites.util.ParasiteEventWorld;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigWorld;
import com.dhanantry.scapeandrunparasites.world.SRPWorldData;
import com.dhanantry.scapeandrunparasites.world.SRPWorldEntitySpawner;
import com.dhanantry.scapeandrunparasites.world.biome.BiomeParasite;
import java.util.ArrayList;
import java.util.List;
import net.lax1dude.eaglercraft.v1_8.EaglercraftRandom;
import net.minecraft.client.Minecraft;
import net.lax1dude.eaglercraft.v1_8.opengl.GlStateManager;
import net.minecraft.client.renderer.GlStateManager.FogMode;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityCreature;
import net.minecraft.entity.EntityList;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.entity.IEntityLivingData;
import net.minecraft.entity.ai.EntityAIAvoidEntity;
import net.minecraft.entity.ai.EntityAINearestAttackableTarget;
import net.minecraft.entity.item.EntityArmorStand;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Items;
import net.minecraft.init.MobEffects;
import net.minecraft.inventory.EntityEquipmentSlot;
import net.minecraft.item.Item;
import net.minecraft.item.ItemBlock;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.nbt.NBTTagList;
import net.minecraft.potion.PotionEffect;
import net.minecraft.util.DamageSource;
import net.minecraft.util.math.AxisAlignedBB;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.util.math.RayTraceResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.util.text.Style;
import net.minecraft.util.text.TextComponentTranslation;
import net.minecraft.util.text.TextFormatting;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraft.world.WorldType;
import net.minecraftforge.client.event.GuiOpenEvent;
import net.minecraftforge.client.event.MouseEvent;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogColors;
import net.minecraftforge.client.event.EntityViewRenderEvent.FogDensity;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.event.entity.EntityJoinWorldEvent;
import net.minecraftforge.event.entity.EntityStruckByLightningEvent;
import net.minecraftforge.event.entity.living.LivingDamageEvent;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingDropsEvent;
import net.minecraftforge.event.entity.living.LivingHealEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.event.entity.living.LivingEntityUseItemEvent.Start;
import net.minecraftforge.event.entity.player.ItemFishedEvent;
import net.minecraftforge.event.entity.player.PlayerWakeUpEvent;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteract;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.EntityInteractSpecific;
import net.minecraftforge.event.entity.player.PlayerInteractEvent.RightClickBlock;
import net.minecraftforge.event.world.BlockEvent.CropGrowEvent.Pre;
import net.minecraftforge.event.world.WorldEvent.Load;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.eventhandler.Event.Result;
import net.minecraftforge.fml.common.gameevent.TickEvent.Phase;
import net.minecraftforge.fml.common.gameevent.TickEvent.PlayerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.ServerTickEvent;
import net.minecraftforge.fml.common.gameevent.TickEvent.WorldTickEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import net.lax1dude.eaglercraft.v1_8.log4j.Level;

public class SRPEventHandlerBus {
   public static float fog = 0.0F;
   private boolean closeG = false;
   private int counerW = 0;
   private int blockInfestedCountCooldown;
   private int blockParasiteCountCooldown;
   private int heart;

   public SRPEventHandlerBus() {
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onWorldLoad(Load event) {
      fog = 0.0F;
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onEvent(FogDensity event) {
      if (fog > 0.0F && !Minecraft.getMinecraft().player.isPotionActive(MobEffects.BLINDNESS)) {
         GlStateManager.setFog(FogMode.EXP);
         event.setDensity(fog);
         event.setCanceled(true);
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onEvent(FogColors event) {
      if ((double)fog > (double)SRPConfigWorld.biomeFogDensity * 0.2D && !Minecraft.getMinecraft().player.isPotionActive(MobEffects.BLINDNESS)) {
         event.setRed(SRPConfigWorld.biomeFogRed / 255.0F);
         event.setGreen(SRPConfigWorld.biomeFogGreen / 255.0F);
         event.setBlue(SRPConfigWorld.biomeFogBlue / 255.0F);
      }

   }

   @SubscribeEvent
   public void cropGrow(Pre event) {
      if (event.getResult() != Result.DENY) {
         if (SRPConfigWorld.nodesActivated || SRPConfigSystems.useEvolution) {
            SRPWorldData data = SRPWorldData.get(event.getWorld());
            switch(data.nearestHeartAge(event.getPos(), false, 0)) {
            case 1:
               if (event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeOne) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 2:
               if (event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeTwo) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 3:
               if (event.getWorld().rand.nextDouble() < (double)SRPConfigWorld.nodeCropStopNodeThree) {
                  event.setResult(Result.DENY);
                  return;
               }
            }

            switch(data.getEvolutionPhase()) {
            case 1:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedOne) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 2:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedTwo) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 3:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedThree) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 4:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedFour) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 5:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedFive) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 6:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedSix) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 7:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedSeven) {
                  event.setResult(Result.DENY);
                  return;
               }
               break;
            case 8:
               if (event.getWorld().rand.nextDouble() < SRPConfigSystems.cropGrowStunnedEight) {
                  event.setResult(Result.DENY);
                  return;
               }
            }
         }

      }
   }

   @SubscribeEvent
   public void entityHeal(LivingHealEvent event) {
      if (!event.isCanceled()) {
         EntityLivingBase entity = event.getEntityLiving();
         if (entity != null && !entity.world.isRemote) {
            if (entity.world.getBiome(entity.getPosition()) instanceof BiomeParasite) {
               if (entity instanceof EntityParasiteBase) {
                  return;
               }

               if (entity instanceof EntityPlayer) {
                  event.setAmount(event.getAmount() * SRPConfigWorld.biomeHealPenalty);
               } else {
                  boolean flag = ParasiteEventEntity.checkName(EntityList.getKey(entity).toString(), SRPConfigWorld.biomeHealPenaltyBlackList, SRPConfigWorld.biomeHealPenaltyBlackListWhite);
                  if (SRPConfigWorld.biomeHealPenaltyBlackListWhite) {
                     if (flag) {
                        return;
                     }
                  } else if (flag) {
                     return;
                  }

                  event.setAmount(event.getAmount() * SRPConfigWorld.biomeHealPenalty);
               }
            }

         }
      }
   }

   @SubscribeEvent
   public void entityHurt(LivingHurtEvent event) {
      EntityLivingBase entity = event.getEntityLiving();
      if (entity != null && !entity.world.isRemote) {
         if (entity.isPotionActive(SRPPotions.VIRA_E) && SRPConfigSystems.viralEnable) {
            float amp = (float)(entity.getActivePotionEffect(SRPPotions.VIRA_E).getAmplifier() + 1);
            float damage = event.getAmount();
            event.setAmount(damage + damage * amp * SRPConfigSystems.viralAmount);
         }

         if (entity instanceof EntityPlayer) {
            EntityPlayer player = (EntityPlayer)entity;
            int red = 0;
            float damageV = event.getAmount();
            float realRed = 0.0F;
            String damage = "";
            boolean needCheckP = true;
            boolean paraA = false;

            for(ItemStack itemstack : player.inventory.armorInventory) {
               if (itemstack.getItem() instanceof WeaponToolArmorBase) {
                  paraA = true;
                  if (needCheckP) {
                     if (event.getSource() == DamageSource.IN_FIRE || event.getSource() == DamageSource.ON_FIRE || player.isBurning()) {
                        event.setAmount(damageV * SRPConfig.firemultyplier);
                        return;
                     }

                     if (event.getSource().getImmediateSource() instanceof EntityPlayer) {
                        damage = ((EntityPlayer)event.getSource().getImmediateSource()).getName();
                     } else if (event.getSource().getImmediateSource() instanceof EntityLivingBase) {
                        damage = EntityList.getKey(event.getSource().getImmediateSource()).toString();
                     } else {
                        damage = event.getSource().damageType;
                     }

                     needCheckP = false;
                  }

                  ArrayList<String> resistanceS = new ArrayList();
                  ArrayList<Integer> resistanceI = new ArrayList();
                  NBTTagCompound compound = itemstack.getTagCompound();
                  boolean lag = ((WeaponToolArmorBase)itemstack.getItem()).canCall();
                  if (compound == null) {
                     compound = new NBTTagCompound();
                  }

                  if (compound.hasKey("sprresistanceb", 99)) {
                     lag = compound.getBoolean("sprresistanceb");
                  }

                  if (compound.hasKey("sprresistances")) {
                     NBTTagList allResS = compound.getTagList("sprresistances", 10);
                     NBTTagList allResI = compound.getTagList("sprresistancei", 10);
                     if (allResS.tagCount() != allResI.tagCount()) {
                        return;
                     }

                     for(int i = 0; i < allResS.tagCount(); ++i) {
                        NBTTagCompound resT = allResS.getCompoundTagAt(i);
                        String res = resT.getString("resistance" + i);
                        resistanceS.add(i, res);
                        NBTTagCompound resU = allResI.getCompoundTagAt(i);
                        int resi = resU.getInteger("resistance" + i);
                        resistanceI.add(i, Integer.valueOf(resi));
                     }
                  }

                  red += this.hasResistance(damage, resistanceS, resistanceI, lag, player.world.rand);
                  if (lag) {
                     realRed = (float)red * SRPConfig.sentientPointReduction;
                  } else {
                     realRed = (float)red * SRPConfig.livingPointReduction;
                  }

                  if (resistanceS.size() != resistanceI.size()) {
                     return;
                  }

                  NBTTagList allResS = new NBTTagList();
                  NBTTagList allResI = new NBTTagList();

                  for(int i = 0; i < resistanceS.size(); ++i) {
                     String res = (String)resistanceS.get(i);
                     NBTTagCompound resT = new NBTTagCompound();
                     resT.setString("resistance" + i, res);
                     allResS.appendTag(resT);
                     int resi = ((Integer)resistanceI.get(i)).intValue();
                     NBTTagCompound resU = new NBTTagCompound();
                     resU.setInteger("resistance" + i, resi);
                     allResI.appendTag(resU);
                  }

                  compound.setTag("sprresistances", allResS);
                  compound.setTag("sprresistancei", allResI);
                  compound.setBoolean("sprresistanceb", lag);
                  itemstack.setTagCompound(compound);
               }
            }

            realRed = realRed * damageV;
            damageV = Math.max(damageV - realRed, 0.0F);
            event.setAmount(damageV);
            if (event.getSource().getImmediateSource() instanceof EntityLivingBase && paraA && SRPConfig.armorCoth) {
               SRPPotions.applyStackPotion(SRPPotions.COTH_E, (EntityLivingBase)event.getSource().getImmediateSource(), 400, 2);
            }
         }

      }
   }

   private int hasResistance(String damage, ArrayList<String> resistanceS, ArrayList<Integer> resistanceI, boolean stage, EaglercraftRandom rand) {
      if (this.checkList(damage)) {
         return 0;
      } else {
         double getChanceLearn = stage ? SRPConfig.sentientChanceLe : SRPConfig.livingChanceLe;
         if (rand.nextDouble() < getChanceLearn) {
            this.addResistance(damage, resistanceS, resistanceI, stage);
         }

         for(int i = 0; i < resistanceS.size(); ++i) {
            if (((String)resistanceS.get(i)).equals(damage)) {
               int tage = SRPConfig.livingPointCap;
               if (stage) {
                  tage = SRPConfig.sentientPointCap;
               }

               return Math.min(((Integer)resistanceI.get(i)).intValue(), tage);
            }
         }

         return 0;
      }
   }

   private void addResistance(String damage, ArrayList<String> resistanceS, ArrayList<Integer> resistanceI, boolean stage) {
      boolean flag = true;

      for(int i = 0; i < resistanceS.size(); ++i) {
         if (((String)resistanceS.get(i)).equals(damage)) {
            int iiii = ((Integer)resistanceI.get(i)).intValue() + 1;
            resistanceI.set(i, Integer.valueOf(iiii));
            flag = false;
            if (iiii <= 5) {
               ;
            }
            break;
         }
      }

      if (flag) {
         int lim = SRPConfig.livingDamageCap;
         if (stage) {
            lim = SRPConfig.sentientDamageCap;
         }

         if (resistanceS.size() >= lim) {
            return;
         }

         resistanceS.add(damage);
         resistanceI.add(Integer.valueOf(1));
      }

   }

   private boolean checkList(String damage) {
      return ParasiteEventEntity.checkName(damage, SRPConfig.armorDamageTypeBlackList, SRPConfig.armorDamageTypeBlackListWhite);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void stitchEventPre(net.minecraftforge.client.event.TextureStitchEvent.Pre event) {
      event.getMap().registerSprite(ParticleSpore.PARTICLES_TEXTURE);
      event.getMap().registerSprite(ParticleFog.PARTICLES_TEXTURE);
      event.getMap().registerSprite(ParticleMultipleGore.PARTICLES_TEXTUREINFECTED);
      event.getMap().registerSprite(ParticleMultipleGore.PARTICLES_TEXTURESPIDER);
      event.getMap().registerSprite(ParticleMultipleGore.PARTICLES_TEXTUREPRIMITIVE);
      event.getMap().registerSprite(ParticleMultipleGore.PARTICLES_TEXTUREADAPTED);
      event.getMap().registerSprite(ParticleMultipleGore.PARTICLES_TEXTUREVOMIT);
      event.getMap().registerSprite(ParticleRHappy.PARTICLES_TEXTURE);
      event.getMap().registerSprite(ParticleBiomass.PARTICLES_TEXTURE);
   }

   @SubscribeEvent
   public void playerFishing(ItemFishedEvent event) {
      if (!event.getEntity().world.isRemote && SRPConfigSystems.useEvolution) {
         SRPWorldData data = SRPWorldData.get(event.getEntity().world);
         if (data.getEvolutionPhase() >= SRPConfigSystems.evolutionStopFishing) {
            event.setCanceled(true);
         }
      }

   }

   @SubscribeEvent
   public void itemEntity(EntityInteract event) {
      if (event.getTarget() instanceof EntityLivingBase && !event.getWorld().isRemote) {
         ItemStack stack = event.getItemStack();
         String item = stack.getItem().getRegistryName().toString();
         String[] atm = new String[3];

         for(int i = 0; i < SRPConfigSystems.COTHItemPrevent.length; ++i) {
            atm = SRPConfigSystems.COTHItemPrevent[i].split(";");
            if (atm[0].equals(item)) {
               int dur = Integer.parseInt(atm[2]);
               if (!event.getEntityPlayer().capabilities.isCreativeMode) {
                  stack.shrink(1);
                  double chance = Double.parseDouble(atm[1]);
                  if (event.getWorld().rand.nextDouble() < chance) {
                     ((EntityLivingBase)((EntityLivingBase)event.getTarget())).addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, dur * 20, 0));
                     SRPMain.network.sendToAll(new SRPPacketParticle(event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ, event.getTarget().width, event.getTarget().height, (byte)3));
                  }
               } else {
                  ((EntityLivingBase)((EntityLivingBase)event.getTarget())).addPotionEffect(new PotionEffect(SRPPotions.EPEL_E, dur * 20, 0));
                  SRPMain.network.sendToAll(new SRPPacketParticle(event.getTarget().posX, event.getTarget().posY, event.getTarget().posZ, event.getTarget().width, event.getTarget().height, (byte)3));
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void itemPlayer(Start event) {
      if (event.getEntityLiving() instanceof EntityPlayer) {
         EntityPlayer player = (EntityPlayer)event.getEntityLiving();
         if (player != null) {
            if (player.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
               if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 1 && player.world.rand.nextDouble() < (double)SRPConfigSystems.fearItemChance && !ParasiteEventEntity.checkName(player.getItemStackFromSlot(EntityEquipmentSlot.MAINHAND).getItem().getRegistryName().toString(), SRPConfigSystems.fearItemBlackList, SRPConfigSystems.fearItemBlackListWhite)) {
                  player.sendStatusMessage((new TextComponentTranslation("message.srparasites.fearitem", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)), true);
                  event.setDuration(-1);
                  event.setCanceled(true);
                  return;
               }

               if (event.getItem().getItem() instanceof ItemBlock) {
                  ;
               }
            }

         }
      }
   }

   @SubscribeEvent
   public void blockPlayer(RightClickBlock event) {
      EntityPlayer player = event.getEntityPlayer();
      if (player != null) {
         if (player.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
            if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 2 && player.world.rand.nextDouble() < (double)SRPConfigSystems.fearBlockChance) {
               player.sendStatusMessage((new TextComponentTranslation("message.srparasites.fearblock", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)), true);
               event.setUseBlock(Result.DENY);
            } else if (player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 1 && player.world.rand.nextDouble() < (double)SRPConfigSystems.fearItemChance) {
               player.sendStatusMessage((new TextComponentTranslation("message.srparasites.fearitem", new Object[0])).setStyle((new Style()).setColor(TextFormatting.RED)), true);
               event.setUseItem(Result.DENY);
            }
         }

      }
   }

   @SubscribeEvent
   public void entityPlayer(EntityInteractSpecific event) {
      int heart = ParasiteEventWorld.canBiomeStillExist(event.getWorld(), event.getPos(), false);
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void guiPlayer(GuiOpenEvent event) {
      Minecraft mc = Minecraft.getMinecraft();
      EntityPlayer player = mc.player;
      if (player != null && event.getGui() != null) {
         if (!player.isCreative() && !event.getGui().doesGuiPauseGame()) {
            if (player.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive && !player.capabilities.disableDamage && player.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() >= 3 && player.world.rand.nextDouble() < (double)SRPConfigSystems.fearInvChance) {
               player.sendStatusMessage(new TextComponentTranslation("message.srparasites.feargui", new Object[0]), true);
               this.closeG = true;
            }

         }
      }
   }

   @SubscribeEvent
   public void mobFear(LivingDamageEvent event) {
      EntityLivingBase in = event.getEntityLiving();
      if (in != null) {
         if (in.isPotionActive(SRPPotions.FEAR_E) && SRPConfigSystems.fearActive) {
            if (event.getSource() == DamageSource.FALL && SRPConfigSystems.fearFallDamage != 0.0F) {
               event.setAmount(event.getAmount() * SRPConfigSystems.fearFallDamage * (float)(in.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() + 1));
            }

            if (SRPConfigSystems.fearAirDamage == 0.0F) {
               return;
            }

            if (event.getEntity() instanceof EntityPlayer) {
               if (((EntityPlayer)event.getEntity()).capabilities.isFlying) {
                  event.setAmount(event.getAmount() * SRPConfigSystems.fearAirDamage * (float)(in.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() + 1));
               }
            } else if (!event.getEntity().onGround) {
               event.setAmount(event.getAmount() * SRPConfigSystems.fearAirDamage * (float)(in.getActivePotionEffect(SRPPotions.FEAR_E).getAmplifier() + 1));
            }
         }

      }
   }

   @SubscribeEvent
   public void onEntitySpawn(EntityJoinWorldEvent event) {
      if (event.getEntity() != null) {
         if (event.getEntity() instanceof EntityLivingBase && !event.getEntity().world.isRemote) {
            if (event.getEntity() instanceof EntityPlayer) {
               return;
            }

            String mobname = null;

            try {
               mobname = EntityList.getKey(event.getEntity()).toString();
            } catch (Exception var7) {
               SRPMain.logger.log(Level.ERROR, "Problem while spawning entity", var7);
               return;
            }

            if (mobname == null) {
               return;
            }

            NBTTagCompound tags = event.getEntity().getEntityData();
            boolean parasite = event.getEntity() instanceof EntityParasiteBase;
            boolean flagNC = SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution;
            SRPWorldData data = null;
            if (flagNC) {
               data = SRPWorldData.get(event.getWorld());
            }

            if (SRPConfigWorld.nodesActivated && SRPConfigSystems.cothActive && data.nearestHeartAge(event.getEntity().getPosition(), false, 0) != -1) {
               ((EntityLivingBase)event.getEntity()).addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }

            if (!tags.hasKey("srpcothimmunity") && SRPConfigSystems.cothActive && !parasite) {
               this.writeCOTHTag((EntityLivingBase)event.getEntity(), mobname, tags, data);
            }

            if (parasite) {
               this.setNewParasiteTask((EntityParasiteBase)event.getEntity(), mobname, flagNC, data);
            } else if (event.getEntity() instanceof EntityCreature) {
               this.setNewCreatureTask((EntityCreature)event.getEntity(), mobname);
            }
         }

      }
   }

   private void writeCOTHTag(EntityLivingBase in, String mobname, NBTTagCompound tags, SRPWorldData data) {
      if (in instanceof EntityArmorStand) {
         tags.setInteger("srpcothimmunity", 0);
      } else if (ParasiteEventEntity.checkName(mobname, SRPConfigSystems.COTHImmuneList, SRPConfigSystems.COTHImmuneListWhite)) {
         tags.setInteger("srpcothimmunity", 0);
      } else {
         tags.setInteger("srpcothimmunity", 1);
      }

      this.setCOTH(in, data.getEvolutionPhase());
   }

   private void setNewParasiteTask(EntityParasiteBase entity, String mobname, boolean flagNC, SRPWorldData data) {
      if (flagNC) {
         entity.applyBonuses(data.totalColonyPoints(0), data.totalNodePoints(0), data.getEvolutionPhase());
      }

      if (SRPConfig.parasiteGriefing.length != 0) {
         String[] task = new String[4];

         for(int i = 0; i < SRPConfig.parasiteGriefing.length; ++i) {
            if (SRPConfig.parasiteGriefing[i] != null) {
               task = SRPConfig.parasiteGriefing[i].split(";");
               if (task[0].equals(mobname)) {
                  if (entity instanceof EntityPStationary) {
                     entity.setSkillBreakBlocksValues(Float.parseFloat(task[1]), MathHelper.ceil(entity.height), Integer.parseInt(task[3]));
                  } else {
                     entity.setSkillBreakBlocksValues(Float.parseFloat(task[1]), MathHelper.ceil(entity.height), Integer.parseInt(task[3]));
                     entity.tasks.addTask(9, new EntityAISkill(entity, Integer.parseInt(task[2]), 64, false, 13));
                  }
                  break;
               }
            }
         }
      }

      if (entity instanceof EntityPMalleable) {
         EntityPMalleable uwu = (EntityPMalleable)entity;
         String damage = data.getMostCommonDamageS();
         if (damage != null) {
            int times = data.getMostCommonDamageI();

            while(times > 0) {
               --times;
               uwu.addResistance(damage);
            }

            uwu.increaseDamageCap(1);
            uwu.colonySpawned = true;
         }
      }

   }

   private void setNewCreatureTask(EntityCreature entity, String mobname) {
      if (ParasiteEventEntity.checkName(mobname, SRPConfig.entitiesWillAttack, SRPConfig.entitiesWillAttackWhite)) {
         entity.targetTasks.addTask(5, new EntityAINearestAttackableTarget(entity, EntityParasiteBase.class, true));
      } else {
         if (ParasiteEventEntity.checkName(mobname, SRPConfig.entitiesWillAvoid, SRPConfig.entitiesWillAvoidWhite)) {
            entity.tasks.addTask(5, new EntityAIAvoidEntity(entity, EntityParasiteBase.class, 12.0F, 0.8D, 0.8D));
         }

      }
   }

   private void setCOTH(EntityLivingBase target, byte evo) {
      if (!target.isChild()) {
         EaglercraftRandom rand = new EaglercraftRandom();
         switch(evo) {
         case 1:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceOne) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 2:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceTwo) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 3:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceThree) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 4:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceFour) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 5:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceFive) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 6:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceSix) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 7:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceSeven) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
            break;
         case 8:
            if (rand.nextDouble() < SRPConfigSystems.mobSpawningCOTHChanceEight) {
               target.addPotionEffect(new PotionEffect(SRPPotions.COTH_E, 3600, 0, false, false));
            }
         }

      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void serverTick(ServerTickEvent event) {
      if (event.phase != Phase.END) {
         if (ParasiteEventWorld.blockInfestedCount > SRPConfig.BlockInfestedLimit) {
            ++this.blockInfestedCountCooldown;
            if (this.blockInfestedCountCooldown > SRPConfig.BlockInfestedLimitCD) {
               this.blockInfestedCountCooldown = 0;
               ParasiteEventWorld.blockInfestedCount = 0;
            }
         }

         if (ParasiteEventWorld.blockParasiteCount > SRPConfig.BlockParasiteLimit) {
            ++this.blockParasiteCountCooldown;
            if (this.blockParasiteCountCooldown > SRPConfig.BlockParasiteLimitCD) {
               this.blockParasiteCountCooldown = 0;
               ParasiteEventWorld.blockParasiteCount = 0;
            }
         }

         ++this.counerW;
         if (this.counerW >= SRPConfig.dayTickValue && (SRPConfigWorld.nodesActivated || SRPConfigWorld.coloniesActivated || SRPConfigSystems.useEvolution)) {
            if (SRPConfigWorld.coloniesActivated) {
               for(int i : SRPConfigWorld.blackListedDimensionsColonies) {
                  World worldX = DimensionManager.getWorld(i);
                  if (worldX != null) {
                     ParasiteEventWorld.checkColonyStatus(worldX);
                  }
               }
            }

            if (SRPConfigWorld.nodesActivated) {
               for(int i : SRPConfigWorld.blackListedDimensionsNodes) {
                  World worldX = DimensionManager.getWorld(i);
                  if (worldX != null) {
                     ParasiteEventWorld.checkNodeStatus(worldX);
                  }
               }
            }

            this.counerW -= SRPConfig.dayTickValue;
         }

      }
   }

   @SubscribeEvent
   public void setLoot(LivingDropsEvent event) {
      if (event.getEntityLiving() instanceof EntityParasiteBase) {
         if (!event.getEntityLiving().world.getGameRules().getBoolean("doMobLoot")) {
            return;
         }

         EntityParasiteBase mob = (EntityParasiteBase)event.getEntityLiving();
         switch(mob.getParasiteIDRegister()) {
         case 1:
            this.loot(event, SRPConfigMobs.shycoLoot);
            return;
         case 2:
            this.loot(event, SRPConfigMobs.dorpaLoot);
            return;
         case 3:
            this.loot(event, SRPConfigMobs.ratholLoot);
            return;
         case 4:
            this.loot(event, SRPConfigMobs.emanaLoot);
            return;
         case 5:
            this.loot(event, SRPConfigMobs.LodoLoot);
            return;
         case 6:
            this.loot(event, SRPConfigMobs.infhumanLoot);
            return;
         case 7:
            this.loot(event, SRPConfigMobs.hullLoot);
            return;
         case 8:
            this.loot(event, SRPConfigMobs.canraLoot);
            return;
         case 9:
            this.loot(event, SRPConfigMobs.alafhaLoot);
            return;
         case 10:
            this.loot(event, SRPConfigMobs.noglaLoot);
            return;
         case 11:
            this.loot(event, SRPConfigMobs.butholLoot);
            return;
         case 12:
            this.loot(event, SRPConfigMobs.mudoLoot);
            return;
         case 13:
            this.loot(event, SRPConfigMobs.infcowLoot);
            return;
         case 14:
            this.loot(event, SRPConfigMobs.infsheepLoot);
            return;
         case 15:
            this.loot(event, SRPConfigMobs.infwolfLoot);
            return;
         case 16:
            this.loot(event, SRPConfigMobs.venkrolLoot);
            return;
         case 17:
            this.loot(event, SRPConfigMobs.zetmoLoot);
            return;
         case 18:
            this.loot(event, SRPConfigMobs.venkrolsiiLoot);
            return;
         case 19:
            this.loot(event, SRPConfigMobs.venkrolsiiiLoot);
            return;
         case 20:
            this.loot(event, SRPConfigMobs.terlaLoot);
            return;
         case 21:
            this.loot(event, SRPConfigMobs.infwolfheadLoot);
            return;
         case 22:
            this.loot(event, SRPConfigMobs.infsheepheadLoot);
            return;
         case 23:
            this.loot(event, SRPConfigMobs.kolLoot);
            return;
         case 24:
            this.loot(event, SRPConfigMobs.oroncoLoot);
            return;
         case 25:
            this.loot(event, SRPConfigMobs.angedLoot);
            return;
         case 26:
            this.loot(event, SRPConfigMobs.infpigLoot);
            return;
         case 27:
            this.loot(event, SRPConfigMobs.infvillagerLoot);
            return;
         case 28:
            this.loot(event, SRPConfigMobs.infcowheadLoot);
            return;
         case 29:
            this.loot(event, SRPConfigMobs.tonroLoot);
            return;
         case 30:
            this.loot(event, SRPConfigMobs.unvoLoot);
            return;
         case 31:
            this.loot(event, SRPConfigMobs.infpigheadLoot);
            return;
         case 32:
            this.loot(event, SRPConfigMobs.infvillagerheadLoot);
            return;
         case 33:
            this.loot(event, SRPConfigMobs.ganroLoot);
            return;
         case 34:
            this.loot(event, SRPConfigMobs.pod1Loot);
            return;
         case 35:
         case 42:
         case 61:
         case 67:
         case 68:
         case 83:
         case 84:
         default:
            break;
         case 36:
            this.loot(event, SRPConfigMobs.kolLoot);
            return;
         case 37:
            this.loot(event, SRPConfigMobs.shycoLoot);
            return;
         case 38:
            this.loot(event, SRPConfigMobs.arachnidaLoot);
            return;
         case 39:
            this.loot(event, SRPConfigMobs.inhooSLoot);
            return;
         case 40:
            this.loot(event, SRPConfigMobs.infadventurerLoot);
            return;
         case 41:
            this.loot(event, SRPConfigMobs.venkrolsivLoot);
            return;
         case 43:
            this.loot(event, SRPConfigMobs.inhooMLoot);
            return;
         case 44:
            this.loot(event, SRPConfigMobs.infhorseLoot);
            return;
         case 45:
            this.loot(event, SRPConfigMobs.infhorseheadLoot);
            return;
         case 46:
            this.loot(event, SRPConfigMobs.infhumanheadLoot);
            return;
         case 47:
            this.loot(event, SRPConfigMobs.ombooLoot);
            return;
         case 48:
            this.loot(event, SRPConfigMobs.hostLoot);
            return;
         case 49:
            this.loot(event, SRPConfigMobs.infbearLoot);
            return;
         case 50:
            this.loot(event, SRPConfigMobs.esorLoot);
            return;
         case 51:
            this.loot(event, SRPConfigMobs.shycoadaptedloot);
            return;
         case 52:
            this.loot(event, SRPConfigMobs.hulladaptedloot);
            return;
         case 53:
            this.loot(event, SRPConfigMobs.canraadaptedloot);
            return;
         case 54:
            this.loot(event, SRPConfigMobs.noglaadaptedloot);
            return;
         case 55:
            this.loot(event, SRPConfigMobs.emanaadaptedloot);
            return;
         case 56:
            this.loot(event, SRPConfigMobs.zetmoadaptedloot);
            return;
         case 57:
            this.loot(event, SRPConfigMobs.shycoLoot);
            return;
         case 58:
            this.loot(event, SRPConfigMobs.arachnidaadaptedloot);
            return;
         case 59:
            this.loot(event, SRPConfigMobs.infendermanLoot);
            return;
         case 60:
            this.loot(event, SRPConfigMobs.flogLoot);
            return;
         case 62:
            this.loot(event, SRPConfigMobs.cruxaLoot);
            return;
         case 63:
            this.loot(event, SRPConfigMobs.heedLoot);
            return;
         case 64:
            this.loot(event, SRPConfigMobs.infdragoneLoot);
            return;
         case 65:
            this.loot(event, SRPConfigMobs.jinjoLoot);
            return;
         case 66:
            this.loot(event, SRPConfigMobs.lumLoot);
            return;
         case 69:
            this.loot(event, SRPConfigMobs.infendermanheadLoot);
            return;
         case 70:
            this.loot(event, SRPConfigMobs.infdragoneheadLoot);
            return;
         case 71:
            this.loot(event, SRPConfigMobs.infadventurerheadLoot);
            return;
         case 72:
            this.loot(event, SRPConfigMobs.nakLoot);
            return;
         case 73:
            this.loot(event, SRPConfigMobs.dodsiLoot);
            return;
         case 74:
            this.loot(event, SRPConfigMobs.ratholLoot);
            return;
         case 75:
            this.loot(event, SRPConfigMobs.herdLoot);
            return;
         case 76:
            this.loot(event, SRPConfigMobs.nuuhLoot);
            return;
         case 77:
            this.loot(event, SRPConfigMobs.dodsiiLoot);
            return;
         case 78:
            this.loot(event, SRPConfigMobs.dodsiiiLoot);
            return;
         case 79:
            this.loot(event, SRPConfigMobs.dodsivLoot);
            return;
         case 80:
            this.loot(event, SRPConfigMobs.thrallLoot);
            return;
         case 81:
            this.loot(event, SRPConfigMobs.lumadaptedloot);
            return;
         case 82:
            this.loot(event, SRPConfigMobs.ombooLoot);
            return;
         case 85:
            this.loot(event, SRPConfigMobs.elviaLoot);
            return;
         case 86:
            this.loot(event, SRPConfigMobs.lenciaLoot);
            return;
         case 87:
            this.loot(event, SRPConfigMobs.pheonLoot);
            return;
         case 88:
            this.loot(event, SRPConfigMobs.vestaLoot);
            return;
         }
      } else if (event.getEntity() instanceof EntityLivingBase && !(event.getEntity() instanceof EntityPlayer) && SRPConfigSystems.cothActive && ((EntityLivingBase)event.getEntity()).isPotionActive(SRPPotions.COTH_E)) {
         if (SRPConfigSystems.useEvolution) {
            SRPWorldData data = SRPWorldData.get(event.getEntity().world);
            if (data.getEvolutionPhase() >= SRPConfigSystems.evolutionCothStopLoot) {
               NBTTagCompound tags = event.getEntity().getEntityData();
               if (tags.hasKey("srpcothimmunity")) {
                  int key = tags.getInteger("srpcothimmunity");
                  if (key != 0) {
                     event.setCanceled(true);
                  }
               }
            }
         } else if (SRPConfigSystems.cothLootDisable) {
            NBTTagCompound tags = event.getEntity().getEntityData();
            if (tags.hasKey("srpcothimmunity")) {
               int key = tags.getInteger("srpcothimmunity");
               if (key != 0) {
                  event.setCanceled(true);
               }
            }
         }
      }

   }

   private void loot(LivingDropsEvent event, String[] drop) {
      try {
         if (drop.length != 0) {
            String[] dropping = new String[4];
            String[] dropped = new String[drop.length];
            EaglercraftRandom rand = new EaglercraftRandom();
            boolean always = false;
            int quantity = 0;
            int realquantity = 0;
            int chance = 0;
            int rng = 0;
            int totalFalse = 0;

            for(int i = 0; i < drop.length; ++i) {
               dropping = drop[i].split(";");
               always = Boolean.parseBoolean(dropping[3]);
               quantity = Integer.parseInt(dropping[2]);
               chance = Integer.parseInt(dropping[1]);
               if (always) {
                  rng = rand.nextInt(100);
                  if (rng <= chance - 1) {
                     Item item = Item.getByNameOrId(dropping[0]);
                     realquantity = rand.nextInt(quantity);

                     for(int j = 0; j <= realquantity; ++j) {
                        BlockPos pos = event.getEntity().getPosition();
                        event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(item)));
                     }
                  }
               } else {
                  dropped[totalFalse] = drop[i];
                  ++totalFalse;
               }
            }

            if (totalFalse != 0) {
               int n = rand.nextInt(totalFalse);
               String[] stringItem = dropped[n].split(";");
               quantity = Integer.parseInt(stringItem[2]);
               chance = Integer.parseInt(stringItem[1]);
               rng = rand.nextInt(100);
               if (rng <= chance - 1) {
                  Item item = Item.getByNameOrId(stringItem[0]);
                  realquantity = rand.nextInt(quantity);

                  for(int j = 0; j <= realquantity; ++j) {
                     BlockPos pos = event.getEntity().getPosition();
                     event.getDrops().add(new EntityItem(event.getEntity().getEntityWorld(), (double)pos.getX(), (double)pos.getY(), (double)pos.getZ(), new ItemStack(item)));
                  }
               }
            }
         }
      } catch (Exception var17) {
         SRPMain.logger.log(Level.ERROR, "Problem with loot event", var17);
      }

   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void playerTick(PlayerTickEvent event) {
      if (event.phase == Phase.START) {
         EntityPlayer thePlayer = event.player;
         if (thePlayer.world.isRemote) {
            if (this.closeG) {
               if (thePlayer.openContainer != null) {
                  thePlayer.closeScreen();
               }

               this.closeG = false;
            }
         } else {
            boolean dlag = thePlayer.world.getBiome(thePlayer.getPosition()) instanceof BiomeParasite;
            if (ParasiteEventWorld.canBiomeStillExist(thePlayer.world, thePlayer.getPosition(), false) < 1 && !dlag) {
               if (fog > 0.0F) {
                  fog = Math.max(fog - 8.0E-4F, 0.0F);
                  SRPMain.network.sendTo(new SRPPacketFog(fog), (EntityPlayerMP)thePlayer);
               }
            } else {
               fog = Math.min(fog + 4.5E-4F, SRPConfigWorld.biomeFogDensity);
               if (fog < SRPConfigWorld.biomeFogDensity) {
                  SRPMain.network.sendTo(new SRPPacketFog(fog), (EntityPlayerMP)thePlayer);
               }
            }

            ++this.heart;
            if (this.heart < SRPConfigWorld.biomeHeartFreq) {
               return;
            }

            this.heart = 0;
            if (dlag) {
               SRPWorldData data = SRPWorldData.get(thePlayer.world);
               int age = data.nearestHeartAge(thePlayer.getPosition(), true, 0);
               if (age > 0) {
                  int totalS = data.getDistanceSpreadByAge(age, true);
                  float vol = ((float)data.isInRangeOfHeart(thePlayer.getPosition(), (double)totalS) / (float)totalS - 1.0F) * -1.0F;
                  SRPMain.network.sendTo(new SRPPacketMovingSound(-1, vol), (EntityPlayerMP)thePlayer);
               }
            }
         }
      }

   }

   @SubscribeEvent
   public void playerDeath(LivingDeathEvent event) {
      if (event.getEntity() instanceof EntityPlayer && SRPConfigSystems.cothActive && SRPConfigMobs.infadventurerEnabled && SRPConfigMobs.infadventurerSpawnBy && event.getSource().getTrueSource() instanceof EntityParasiteBase) {
         EntityPlayer player = (EntityPlayer)event.getEntity();
         if (player.isPotionActive(SRPPotions.COTH_E)) {
            World world = player.world;
            EntityInfPlayer out = new EntityInfPlayer(world);
            ItemStack head = new ItemStack(player.getItemStackFromSlot(EntityEquipmentSlot.HEAD).getItem());
            ItemStack legs = new ItemStack(player.getItemStackFromSlot(EntityEquipmentSlot.LEGS).getItem());
            ItemStack feet = new ItemStack(player.getItemStackFromSlot(EntityEquipmentSlot.FEET).getItem());
            if (head.getItem() != Items.AIR) {
               out.setItemStackToSlot(EntityEquipmentSlot.HEAD, head);
               out.setHelmetSlot(true);
            }

            out.setItemStackToSlot(EntityEquipmentSlot.LEGS, legs);
            out.setItemStackToSlot(EntityEquipmentSlot.FEET, feet);
            out.copyLocationAndAnglesFrom(player);
            out.onInitialSpawn(world.getDifficultyForLocation(new BlockPos(out)), (IEntityLivingData)null);
            world.spawnEntity(out);
            world.playEvent((EntityPlayer)null, 1026, new BlockPos(out), 0);
            out.setCustomNameTag(player.getName());
            out.setAlwaysRenderNameTag(true);
            out.particleStatus((byte)7);
            out.cannotDespawn(false);
         }
      }
   }

   @SubscribeEvent
   public void playerUp(PlayerWakeUpEvent event) {
      if (SRPConfigSystems.useEvolution && !event.getEntity().world.isRemote) {
         SRPWorldData data = SRPWorldData.get(event.getEntityPlayer().world);
         World world = event.getEntityPlayer().world;
         if (event.getEntity().world.getWorldTime() % (long)SRPConfig.dayTickValue < 13000L) {
            int bonus = 1;
            if (data.getEvolutionPhase() >= SRPConfigSystems.evolutionSleepDenied) {
               bonus = 5;
            }

            data.setTotalKills(this.getSleepPointP(data.getEvolutionPhase()) * bonus, true, world, true);
         }

      }
   }

   private int getSleepPointP(byte phase) {
      switch(phase) {
      case 0:
         return SRPConfigSystems.sleepPenaltyZero;
      case 1:
         return SRPConfigSystems.sleepPenaltyOne;
      case 2:
         return SRPConfigSystems.sleepPenaltyTwo;
      case 3:
         return SRPConfigSystems.sleepPenaltyThree;
      case 4:
         return SRPConfigSystems.sleepPenaltyFour;
      case 5:
         return SRPConfigSystems.sleepPenaltyFive;
      case 6:
         return SRPConfigSystems.sleepPenaltySix;
      case 7:
         return SRPConfigSystems.sleepPenaltySeven;
      case 8:
         return SRPConfigSystems.sleepPenaltyEight;
      default:
         return 0;
      }
   }

   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void worldTick(WorldTickEvent event) {
      if (event.phase != Phase.END) {
         if (!event.world.isRemote && SRPConfigSystems.useEvolution && SRPConfigSystems.phaseCustomSpawner) {
            this.tickSpawn(event.world.getMinecraftServer().getWorld(event.world.provider.getDimension()));
         }

         List entityList = event.world.loadedEntityList;

         for(int i = 0; i < entityList.size(); ++i) {
            Entity var4 = (Entity)entityList.get(i);
         }

      }
   }

   private void tickSpawn(WorldServer server) {
      if (server.getGameRules().getBoolean("doMobSpawning") && server.getWorldInfo().getTerrainType() != WorldType.DEBUG_ALL_BLOCK_STATES) {
         SRPWorldEntitySpawner.findChunksForSpawning(server, true, false, server.getWorldInfo().getWorldTotalTime() % 400L == 0L);
      }

   }

   @SubscribeEvent
   public void light(EntityStruckByLightningEvent event) {
      if (event.getEntity() instanceof EntityParasiteBase) {
         ((EntityParasiteBase)event.getEntity()).setKillC(1000000.0D);
      }

   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent(
      priority = EventPriority.NORMAL,
      receiveCanceled = true
   )
   public void onEvent(MouseEvent event) {
      if (event.getButton() == 0 && event.isButtonstate()) {
         Minecraft mc = Minecraft.getMinecraft();
         EntityPlayer thePlayer = mc.player;
         if (thePlayer != null) {
            ItemStack itemstack = thePlayer.getHeldItemMainhand();
            if (itemstack != null) {
               IHaveReach ieri;
               if (itemstack.getItem() instanceof IHaveReach) {
                  ieri = (IHaveReach)itemstack.getItem();
               } else {
                  ieri = null;
               }

               if (ieri != null) {
                  float reach = ieri.getReach();
                  RayTraceResult mov = this.getMouseOverExtended(reach);
                  if (mov != null && mov.entityHit != null && mov.entityHit.hurtResistantTime == 0 && mov.entityHit != thePlayer) {
                     SRPMain.network.sendToServer(new SRPPacketMeleeRange(mov.entityHit.getEntityId()));
                  }
               }
            }
         }
      }

   }

   public RayTraceResult getMouseOverExtended(float dist) {
      Minecraft mc = FMLClientHandler.instance().getClient();
      Entity theRenderViewEntity = mc.getRenderViewEntity();
      AxisAlignedBB theViewBoundingBox = new AxisAlignedBB(theRenderViewEntity.posX - 0.5D, theRenderViewEntity.posY - 0.0D, theRenderViewEntity.posZ - 0.5D, theRenderViewEntity.posX + 0.5D, theRenderViewEntity.posY + 1.5D, theRenderViewEntity.posZ + 0.5D);
      RayTraceResult returnMOP = null;
      if (mc.world != null) {
         double var2 = (double)dist;
         returnMOP = theRenderViewEntity.rayTrace(var2, 0.0F);
         double calcdist = var2;
         Vec3d pos = theRenderViewEntity.getPositionEyes(0.0F);
         if (returnMOP != null) {
            calcdist = returnMOP.hitVec.distanceTo(pos);
         }

         Vec3d lookvec = theRenderViewEntity.getLook(0.0F);
         Vec3d var8 = pos.addVector(lookvec.x * var2, lookvec.y * var2, lookvec.z * var2);
         Entity pointedEntity = null;
         float var9 = 1.0F;
         List<Entity> list = mc.world.getEntitiesWithinAABBExcludingEntity(theRenderViewEntity, theViewBoundingBox.expand(lookvec.x * var2, lookvec.y * var2, lookvec.z * var2).grow((double)var9, (double)var9, (double)var9));
         double d = calcdist;

         for(Entity entity : list) {
            if (entity.canBeCollidedWith()) {
               float bordersize = entity.getCollisionBorderSize();
               AxisAlignedBB aabb = new AxisAlignedBB(entity.posX - (double)(entity.width / 2.0F), entity.posY, entity.posZ - (double)(entity.width / 2.0F), entity.posX + (double)(entity.width / 2.0F), entity.posY + (double)entity.height, entity.posZ + (double)(entity.width / 2.0F));
               aabb.expand((double)bordersize, (double)bordersize, (double)bordersize);
               RayTraceResult mop0 = aabb.calculateIntercept(pos, var8);
               if (aabb.contains(pos)) {
                  if (0.0D < d || d == 0.0D) {
                     pointedEntity = entity;
                     d = 0.0D;
                  }
               } else if (mop0 != null) {
                  double d1 = pos.distanceTo(mop0.hitVec);
                  if (d1 < d || d == 0.0D) {
                     pointedEntity = entity;
                     d = d1;
                  }
               }
            }
         }

         if (pointedEntity != null && (d < calcdist || returnMOP == null)) {
            returnMOP = new RayTraceResult(pointedEntity);
         }
      }

      return returnMOP;
   }
}
