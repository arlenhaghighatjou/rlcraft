package com.dhanantry.scapeandrunparasites.util.handlers;

import com.dhanantry.scapeandrunparasites.client.SRPProjectile;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.RenderDamage;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.RenderProjectileHomming;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.RenderScent;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.RenderSource;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.RenderTCloud;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderBanoAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderCanraAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderEmanaAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderHullAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderNoglaAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderRanracAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.adapted.RenderShycoAdapted;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient.RenderAncientPod;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient.RenderOronco;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient.RenderOroncoTen;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.ancient.RenderTerla;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.awakened.RenderOroncoAW;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderCrux;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderHeed;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderHost;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderHostII;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderInhooM;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderInhooS;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.crude.RenderMes;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.derived.RenderKirin;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.RenderDodT;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.RenderNak;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.RenderTonro;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.RenderUnvo;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderDod;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderDodSII;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderDodSIII;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderDodSIV;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderVenkrol;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderVenkrolSII;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderVenkrolSIII;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.deterrent.nexus.RenderVenkrolSIV;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderButhol;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderKol;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderLesh;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderLodo;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderMudo;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderNuuh;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.inborn.RenderRathol;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderDorpa;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfBear;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfCow;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfCowHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfDragonE;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfDragonEHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfEnderman;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfEndermanHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfHorse;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfHorseHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfHuman;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfHumanHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfPig;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfPigHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfPlayer;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfPlayerHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfSheep;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfSheepHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfVillager;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfVillagerHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfWolf;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.infected.RenderInfWolfHead;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderBiomass;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderBomb;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderEntityBody;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderEntityBodyModel;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderGore;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderNade;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderRemain;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderScaryOrb;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderTendril;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.misc.RenderWave;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderBano;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderCanra;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderEmana;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderHull;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderLum;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderNogla;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderRanrac;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderShyco;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.primitive.RenderWymo;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderAlafha;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderAnged;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderEsor;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderFlog;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderGanro;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderOmboo;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.RenderOrch;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent.RenderElvia;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent.RenderFlam;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent.RenderJinjo;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent.RenderLencia;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent.RenderPheon;
import com.dhanantry.scapeandrunparasites.client.renderer.entity.pure.preeminent.RenderVesta;
import com.dhanantry.scapeandrunparasites.entity.EntityBody;
import com.dhanantry.scapeandrunparasites.entity.EntityBodyModel;
import com.dhanantry.scapeandrunparasites.entity.EntityDamage;
import com.dhanantry.scapeandrunparasites.entity.EntityParasiticScent;
import com.dhanantry.scapeandrunparasites.entity.EntityRemain;
import com.dhanantry.scapeandrunparasites.entity.EntityScaryOrb;
import com.dhanantry.scapeandrunparasites.entity.EntitySource;
import com.dhanantry.scapeandrunparasites.entity.EntityToxicCloud;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityBiomass;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityTendril;
import com.dhanantry.scapeandrunparasites.entity.monster.EntityWave;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityBanoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityCanraAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityEmanaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityHullAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityNoglaAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityRanracAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.adapted.EntityShycoAdapted;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOronco;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityOroncoTen;
import com.dhanantry.scapeandrunparasites.entity.monster.ancient.EntityTerla;
import com.dhanantry.scapeandrunparasites.entity.monster.awakened.EntityOroncoAW;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityCrux;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHeed;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHost;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityHostII;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooM;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityInhooS;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityLesh;
import com.dhanantry.scapeandrunparasites.entity.monster.crude.EntityMes;
import com.dhanantry.scapeandrunparasites.entity.monster.derived.EntityKirin;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityDodT;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityNak;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityTonro;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.EntityUnvo;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDod;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityDodSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrol;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIII;
import com.dhanantry.scapeandrunparasites.entity.monster.deterrent.nexus.EntityVenkrolSIV;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityButhol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityKol;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityLodo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityMudo;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityNuuh;
import com.dhanantry.scapeandrunparasites.entity.monster.inborn.EntityRathol;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityDorpa;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfBear;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCow;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfCowHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonE;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfDragonEHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEnderman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfEndermanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorse;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHorseHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHuman;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfHumanHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPig;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPigHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayer;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfPlayerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheep;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfSheepHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillager;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfVillagerHead;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolf;
import com.dhanantry.scapeandrunparasites.entity.monster.infected.EntityInfWolfHead;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityBano;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityCanra;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityEmana;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityHull;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityLum;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityNogla;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityRanrac;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityShyco;
import com.dhanantry.scapeandrunparasites.entity.monster.primitive.EntityWymo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAlafha;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityAnged;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityEsor;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityFlog;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityGanro;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOmboo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.EntityOrch;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityElvia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityFlam;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityJinjo;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityLencia;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityPheon;
import com.dhanantry.scapeandrunparasites.entity.monster.pure.preeminent.EntityVesta;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityBomb;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityDropPod;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityGore;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAlafhaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAncientball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileAngedball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileBiomass;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileDragonE;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileElviaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileHomming;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileLenciaBall;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileNade;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectilePullball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileSpineball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityProjectileWebball;
import com.dhanantry.scapeandrunparasites.entity.projectile.EntityThrowableAntiInfestedBlock;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfig;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigMobs;
import com.dhanantry.scapeandrunparasites.util.config.SRPConfigSystems;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.IRenderFactory;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class SRPRenderHandler {
   public SRPRenderHandler() {
   }

   public static void registryEntityRenders() {
      if (SRPConfig.allowMobs) {
         RenderingRegistry.registerEntityRenderingHandler(EntityKirin.class, new IRenderFactory<EntityKirin>() {
            public Render<? super EntityKirin> createRenderFor(RenderManager manager) {
               return new RenderKirin(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityOroncoAW.class, new IRenderFactory<EntityOroncoAW>() {
            public Render<? super EntityOroncoAW> createRenderFor(RenderManager manager) {
               return new RenderOroncoAW(manager);
            }
         });
         if (SRPConfigMobs.ratholEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRathol.class, new IRenderFactory<EntityRathol>() {
               public Render<? super EntityRathol> createRenderFor(RenderManager manager) {
                  return new RenderRathol(manager);
               }
            });
         }

         if (SRPConfigMobs.lodoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityLodo.class, new IRenderFactory<EntityLodo>() {
               public Render<? super EntityLodo> createRenderFor(RenderManager manager) {
                  return new RenderLodo(manager);
               }
            });
         }

         if (SRPConfigMobs.butholEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityButhol.class, new IRenderFactory<EntityButhol>() {
               public Render<? super EntityButhol> createRenderFor(RenderManager manager) {
                  return new RenderButhol(manager);
               }
            });
         }

         if (SRPConfigMobs.mudoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityMudo.class, new IRenderFactory<EntityMudo>() {
               public Render<? super EntityMudo> createRenderFor(RenderManager manager) {
                  return new RenderMudo(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityNuuh.class, new IRenderFactory<EntityNuuh>() {
            public Render<? super EntityNuuh> createRenderFor(RenderManager manager) {
               return new RenderNuuh(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityVenkrol.class, new IRenderFactory<EntityVenkrol>() {
            public Render<? super EntityVenkrol> createRenderFor(RenderManager manager) {
               return new RenderVenkrol(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityVenkrolSII.class, new IRenderFactory<EntityVenkrolSII>() {
            public Render<? super EntityVenkrolSII> createRenderFor(RenderManager manager) {
               return new RenderVenkrolSII(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityVenkrolSIII.class, new IRenderFactory<EntityVenkrolSIII>() {
            public Render<? super EntityVenkrolSIII> createRenderFor(RenderManager manager) {
               return new RenderVenkrolSIII(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityVenkrolSIV.class, new IRenderFactory<EntityVenkrolSIV>() {
            public Render<? super EntityVenkrolSIV> createRenderFor(RenderManager manager) {
               return new RenderVenkrolSIV(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityDodT.class, new IRenderFactory<EntityDodT>() {
            public Render<? super EntityDodT> createRenderFor(RenderManager manager) {
               return new RenderDodT(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityDod.class, new IRenderFactory<EntityDod>() {
            public Render<? super EntityDod> createRenderFor(RenderManager manager) {
               return new RenderDod(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityDodSII.class, new IRenderFactory<EntityDodSII>() {
            public Render<? super EntityDodSII> createRenderFor(RenderManager manager) {
               return new RenderDodSII(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityDodSIII.class, new IRenderFactory<EntityDodSIII>() {
            public Render<? super EntityDodSIII> createRenderFor(RenderManager manager) {
               return new RenderDodSIII(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityDodSIV.class, new IRenderFactory<EntityDodSIV>() {
            public Render<? super EntityDodSIV> createRenderFor(RenderManager manager) {
               return new RenderDodSIV(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityLesh.class, new IRenderFactory<EntityLesh>() {
            public Render<? super EntityLesh> createRenderFor(RenderManager manager) {
               return new RenderLesh(manager);
            }
         });
         if (SRPConfigMobs.tonroEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityTonro.class, new IRenderFactory<EntityTonro>() {
               public Render<? super EntityTonro> createRenderFor(RenderManager manager) {
                  return new RenderTonro(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityNak.class, new IRenderFactory<EntityNak>() {
            public Render<? super EntityNak> createRenderFor(RenderManager manager) {
               return new RenderNak(manager);
            }
         });
         if (SRPConfigMobs.unvoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityUnvo.class, new IRenderFactory<EntityUnvo>() {
               public Render<? super EntityUnvo> createRenderFor(RenderManager manager) {
                  return new RenderUnvo(manager);
               }
            });
         }

         if (SRPConfigMobs.kolEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityKol.class, new IRenderFactory<EntityKol>() {
               public Render<? super EntityKol> createRenderFor(RenderManager manager) {
                  return new RenderKol(manager);
               }
            });
         }

         if (SRPConfigMobs.terlaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityTerla.class, new IRenderFactory<EntityTerla>() {
               public Render<? super EntityTerla> createRenderFor(RenderManager manager) {
                  return new RenderTerla(manager);
               }
            });
         }

         if (SRPConfigMobs.oroncoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityOronco.class, new IRenderFactory<EntityOronco>() {
               public Render<? super EntityOronco> createRenderFor(RenderManager manager) {
                  return new RenderOronco(manager);
               }
            });
         }

         if (SRPConfigMobs.oroncoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityOroncoTen.class, new IRenderFactory<EntityOroncoTen>() {
               public Render<? super EntityOroncoTen> createRenderFor(RenderManager manager) {
                  return new RenderOroncoTen(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityDropPod.class, new IRenderFactory<EntityDropPod>() {
            public Render<? super EntityDropPod> createRenderFor(RenderManager manager) {
               return new RenderAncientPod(manager);
            }
         });
         if (SRPConfigMobs.hostEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityHost.class, new IRenderFactory<EntityHost>() {
               public Render<? super EntityHost> createRenderFor(RenderManager manager) {
                  return new RenderHost(manager);
               }
            });
         }

         if (SRPConfigMobs.hostEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityHostII.class, new IRenderFactory<EntityHostII>() {
               public Render<? super EntityHostII> createRenderFor(RenderManager manager) {
                  return new RenderHostII(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityMes.class, new IRenderFactory<EntityMes>() {
            public Render<? super EntityMes> createRenderFor(RenderManager manager) {
               return new RenderMes(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityInhooS.class, new IRenderFactory<EntityInhooS>() {
            public Render<? super EntityInhooS> createRenderFor(RenderManager manager) {
               return new RenderInhooS(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityInhooM.class, new IRenderFactory<EntityInhooM>() {
            public Render<? super EntityInhooM> createRenderFor(RenderManager manager) {
               return new RenderInhooM(manager);
            }
         });
         if (SRPConfigMobs.dorpaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityDorpa.class, new IRenderFactory<EntityDorpa>() {
               public Render<? super EntityDorpa> createRenderFor(RenderManager manager) {
                  return new RenderDorpa(manager);
               }
            });
         }

         if (SRPConfigMobs.infendermanEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfEnderman.class, new IRenderFactory<EntityInfEnderman>() {
               public Render<? super EntityInfEnderman> createRenderFor(RenderManager manager) {
                  return new RenderInfEnderman(manager);
               }
            });
         }

         if (SRPConfigMobs.infendermanEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfEndermanHead.class, new IRenderFactory<EntityInfEndermanHead>() {
               public Render<? super EntityInfEndermanHead> createRenderFor(RenderManager manager) {
                  return new RenderInfEndermanHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infhumanEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfHuman.class, new IRenderFactory<EntityInfHuman>() {
               public Render<? super EntityInfHuman> createRenderFor(RenderManager manager) {
                  return new RenderInfHuman(manager);
               }
            });
         }

         if (SRPConfigMobs.infhumanEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfHumanHead.class, new IRenderFactory<EntityInfHumanHead>() {
               public Render<? super EntityInfHumanHead> createRenderFor(RenderManager manager) {
                  return new RenderInfHumanHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infcowEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfCow.class, new IRenderFactory<EntityInfCow>() {
               public Render<? super EntityInfCow> createRenderFor(RenderManager manager) {
                  return new RenderInfCow(manager);
               }
            });
         }

         if (SRPConfigMobs.infcowEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfCowHead.class, new IRenderFactory<EntityInfCowHead>() {
               public Render<? super EntityInfCowHead> createRenderFor(RenderManager manager) {
                  return new RenderInfCowHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infsheepEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfSheep.class, new IRenderFactory<EntityInfSheep>() {
               public Render<? super EntityInfSheep> createRenderFor(RenderManager manager) {
                  return new RenderInfSheep(manager);
               }
            });
         }

         if (SRPConfigMobs.infsheepEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfSheepHead.class, new IRenderFactory<EntityInfSheepHead>() {
               public Render<? super EntityInfSheepHead> createRenderFor(RenderManager manager) {
                  return new RenderInfSheepHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infwolfEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfWolf.class, new IRenderFactory<EntityInfWolf>() {
               public Render<? super EntityInfWolf> createRenderFor(RenderManager manager) {
                  return new RenderInfWolf(manager);
               }
            });
         }

         if (SRPConfigMobs.infwolfEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfWolfHead.class, new IRenderFactory<EntityInfWolfHead>() {
               public Render<? super EntityInfWolfHead> createRenderFor(RenderManager manager) {
                  return new RenderInfWolfHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infpigEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfPig.class, new IRenderFactory<EntityInfPig>() {
               public Render<? super EntityInfPig> createRenderFor(RenderManager manager) {
                  return new RenderInfPig(manager);
               }
            });
         }

         if (SRPConfigMobs.infpigEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfPigHead.class, new IRenderFactory<EntityInfPigHead>() {
               public Render<? super EntityInfPigHead> createRenderFor(RenderManager manager) {
                  return new RenderInfPigHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infvillagerEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfVillager.class, new IRenderFactory<EntityInfVillager>() {
               public Render<? super EntityInfVillager> createRenderFor(RenderManager manager) {
                  return new RenderInfVillager(manager);
               }
            });
         }

         if (SRPConfigMobs.infvillagerEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfVillagerHead.class, new IRenderFactory<EntityInfVillagerHead>() {
               public Render<? super EntityInfVillagerHead> createRenderFor(RenderManager manager) {
                  return new RenderInfVillagerHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infhorseEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfHorse.class, new IRenderFactory<EntityInfHorse>() {
               public Render<? super EntityInfHorse> createRenderFor(RenderManager manager) {
                  return new RenderInfHorse(manager);
               }
            });
         }

         if (SRPConfigMobs.infhorseEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfHorseHead.class, new IRenderFactory<EntityInfHorseHead>() {
               public Render<? super EntityInfHorseHead> createRenderFor(RenderManager manager) {
                  return new RenderInfHorseHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infadventurerEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfPlayer.class, new IRenderFactory<EntityInfPlayer>() {
               public Render<? super EntityInfPlayer> createRenderFor(RenderManager manager) {
                  return new RenderInfPlayer(manager);
               }
            });
         }

         if (SRPConfigMobs.infadventurerEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfPlayerHead.class, new IRenderFactory<EntityInfPlayerHead>() {
               public Render<? super EntityInfPlayerHead> createRenderFor(RenderManager manager) {
                  return new RenderInfPlayerHead(manager);
               }
            });
         }

         if (SRPConfigMobs.infbearEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfBear.class, new IRenderFactory<EntityInfBear>() {
               public Render<? super EntityInfBear> createRenderFor(RenderManager manager) {
                  return new RenderInfBear(manager);
               }
            });
         }

         if (SRPConfigMobs.infdragoneEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfDragonE.class, new IRenderFactory<EntityInfDragonE>() {
               public Render<? super EntityInfDragonE> createRenderFor(RenderManager manager) {
                  return new RenderInfDragonE(manager);
               }
            });
         }

         if (SRPConfigMobs.infdragoneEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityInfDragonEHead.class, new IRenderFactory<EntityInfDragonEHead>() {
               public Render<? super EntityInfDragonEHead> createRenderFor(RenderManager manager) {
                  return new RenderInfDragonEHead(manager);
               }
            });
         }

         if (SRPConfigMobs.emanaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityEmana.class, new IRenderFactory<EntityEmana>() {
               public Render<? super EntityEmana> createRenderFor(RenderManager manager) {
                  return new RenderEmana(manager);
               }
            });
         }

         if (SRPConfigMobs.emanaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityEmanaAdapted.class, new IRenderFactory<EntityEmanaAdapted>() {
               public Render<? super EntityEmanaAdapted> createRenderFor(RenderManager manager) {
                  return new RenderEmanaAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.lumEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityLum.class, new IRenderFactory<EntityLum>() {
               public Render<? super EntityLum> createRenderFor(RenderManager manager) {
                  return new RenderLum(manager);
               }
            });
         }

         if (SRPConfigMobs.hullEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityHull.class, new IRenderFactory<EntityHull>() {
               public Render<? super EntityHull> createRenderFor(RenderManager manager) {
                  return new RenderHull(manager);
               }
            });
         }

         if (SRPConfigMobs.hullEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityHullAdapted.class, new IRenderFactory<EntityHullAdapted>() {
               public Render<? super EntityHullAdapted> createRenderFor(RenderManager manager) {
                  return new RenderHullAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.canraEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCanra.class, new IRenderFactory<EntityCanra>() {
               public Render<? super EntityCanra> createRenderFor(RenderManager manager) {
                  return new RenderCanra(manager);
               }
            });
         }

         if (SRPConfigMobs.canraEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCanraAdapted.class, new IRenderFactory<EntityCanraAdapted>() {
               public Render<? super EntityCanraAdapted> createRenderFor(RenderManager manager) {
                  return new RenderCanraAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.noglaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityNogla.class, new IRenderFactory<EntityNogla>() {
               public Render<? super EntityNogla> createRenderFor(RenderManager manager) {
                  return new RenderNogla(manager);
               }
            });
         }

         if (SRPConfigMobs.noglaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityNoglaAdapted.class, new IRenderFactory<EntityNoglaAdapted>() {
               public Render<? super EntityNoglaAdapted> createRenderFor(RenderManager manager) {
                  return new RenderNoglaAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.zetmoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityBano.class, new IRenderFactory<EntityBano>() {
               public Render<? super EntityBano> createRenderFor(RenderManager manager) {
                  return new RenderBano(manager);
               }
            });
         }

         if (SRPConfigMobs.zetmoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityBanoAdapted.class, new IRenderFactory<EntityBanoAdapted>() {
               public Render<? super EntityBanoAdapted> createRenderFor(RenderManager manager) {
                  return new RenderBanoAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.shycoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityShyco.class, new IRenderFactory<EntityShyco>() {
               public Render<? super EntityShyco> createRenderFor(RenderManager manager) {
                  return new RenderShyco(manager);
               }
            });
         }

         if (SRPConfigMobs.shycoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityShycoAdapted.class, new IRenderFactory<EntityShycoAdapted>() {
               public Render<? super EntityShycoAdapted> createRenderFor(RenderManager manager) {
                  return new RenderShycoAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.shycoEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityWymo.class, new IRenderFactory<EntityWymo>() {
               public Render<? super EntityWymo> createRenderFor(RenderManager manager) {
                  return new RenderWymo(manager);
               }
            });
         }

         if (SRPConfigMobs.arachnidaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRanrac.class, new IRenderFactory<EntityRanrac>() {
               public Render<? super EntityRanrac> createRenderFor(RenderManager manager) {
                  return new RenderRanrac(manager);
               }
            });
         }

         if (SRPConfigMobs.arachnidaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityRanracAdapted.class, new IRenderFactory<EntityRanracAdapted>() {
               public Render<? super EntityRanracAdapted> createRenderFor(RenderManager manager) {
                  return new RenderRanracAdapted(manager);
               }
            });
         }

         if (SRPConfigMobs.alafhaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityAlafha.class, new IRenderFactory<EntityAlafha>() {
               public Render<? super EntityAlafha> createRenderFor(RenderManager manager) {
                  return new RenderAlafha(manager);
               }
            });
         }

         if (SRPConfigMobs.ganroEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityGanro.class, new IRenderFactory<EntityGanro>() {
               public Render<? super EntityGanro> createRenderFor(RenderManager manager) {
                  return new RenderGanro(manager);
               }
            });
         }

         if (SRPConfigMobs.angedEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityAnged.class, new IRenderFactory<EntityAnged>() {
               public Render<? super EntityAnged> createRenderFor(RenderManager manager) {
                  return new RenderAnged(manager);
               }
            });
         }

         if (SRPConfigMobs.ombooEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityOmboo.class, new IRenderFactory<EntityOmboo>() {
               public Render<? super EntityOmboo> createRenderFor(RenderManager manager) {
                  return new RenderOmboo(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityJinjo.class, new IRenderFactory<EntityJinjo>() {
            public Render<? super EntityJinjo> createRenderFor(RenderManager manager) {
               return new RenderJinjo(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityFlam.class, new IRenderFactory<EntityFlam>() {
            public Render<? super EntityFlam> createRenderFor(RenderManager manager) {
               return new RenderFlam(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityPheon.class, new IRenderFactory<EntityPheon>() {
            public Render<? super EntityPheon> createRenderFor(RenderManager manager) {
               return new RenderPheon(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityElvia.class, new IRenderFactory<EntityElvia>() {
            public Render<? super EntityElvia> createRenderFor(RenderManager manager) {
               return new RenderElvia(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityLencia.class, new IRenderFactory<EntityLencia>() {
            public Render<? super EntityLencia> createRenderFor(RenderManager manager) {
               return new RenderLencia(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityVesta.class, new IRenderFactory<EntityVesta>() {
            public Render<? super EntityVesta> createRenderFor(RenderManager manager) {
               return new RenderVesta(manager);
            }
         });
         if (SRPConfigMobs.esorEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityEsor.class, new IRenderFactory<EntityEsor>() {
               public Render<? super EntityEsor> createRenderFor(RenderManager manager) {
                  return new RenderEsor(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityOrch.class, new IRenderFactory<EntityOrch>() {
            public Render<? super EntityOrch> createRenderFor(RenderManager manager) {
               return new RenderOrch(manager);
            }
         });
         if (SRPConfigMobs.flogEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityFlog.class, new IRenderFactory<EntityFlog>() {
               public Render<? super EntityFlog> createRenderFor(RenderManager manager) {
                  return new RenderFlog(manager);
               }
            });
         }

         if (SRPConfigMobs.heedEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityHeed.class, new IRenderFactory<EntityHeed>() {
               public Render<? super EntityHeed> createRenderFor(RenderManager manager) {
                  return new RenderHeed(manager);
               }
            });
         }

         if (SRPConfigMobs.cruxaEnabled) {
            RenderingRegistry.registerEntityRenderingHandler(EntityCrux.class, new IRenderFactory<EntityCrux>() {
               public Render<? super EntityCrux> createRenderFor(RenderManager manager) {
                  return new RenderCrux(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileWebball.class, new IRenderFactory<EntityProjectileWebball>() {
            public Render<EntityProjectileWebball> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/webball.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileSpineball.class, new IRenderFactory<EntityProjectileSpineball>() {
            public Render<EntityProjectileSpineball> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/spineball.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileNade.class, new IRenderFactory<EntityProjectileNade>() {
            public Render<EntityProjectileNade> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/nade.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileAlafhaBall.class, new IRenderFactory<EntityProjectileAlafhaBall>() {
            public Render<EntityProjectileAlafhaBall> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/alafha.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileAngedball.class, new IRenderFactory<EntityProjectileAngedball>() {
            public Render<EntityProjectileAngedball> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/anged.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectilePullball.class, new IRenderFactory<EntityProjectilePullball>() {
            public Render<EntityProjectilePullball> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/pullingweb.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileAncientball.class, new IRenderFactory<EntityProjectileAncientball>() {
            public Render<EntityProjectileAncientball> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/ancient.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityThrowableAntiInfestedBlock.class, new IRenderFactory<EntityThrowableAntiInfestedBlock>() {
            public Render<EntityThrowableAntiInfestedBlock> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/cleaner.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileBiomass.class, new IRenderFactory<EntityProjectileBiomass>() {
            public Render<EntityProjectileBiomass> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/biomass.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileDragonE.class, new IRenderFactory<EntityProjectileDragonE>() {
            public Render<EntityProjectileDragonE> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/dragone.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileLenciaBall.class, new IRenderFactory<EntityProjectileLenciaBall>() {
            public Render<EntityProjectileLenciaBall> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/lencia.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileElviaBall.class, new IRenderFactory<EntityProjectileElviaBall>() {
            public Render<EntityProjectileElviaBall> createRenderFor(RenderManager manager) {
               return new SRPProjectile(manager, 0.5F, new ResourceLocation("srparasites", "textures/entity/projectile/elvia.png"));
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityTendril.class, new IRenderFactory<EntityTendril>() {
            public Render<? super EntityTendril> createRenderFor(RenderManager manager) {
               return new RenderTendril(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityDamage.class, new IRenderFactory<EntityDamage>() {
            public Render<? super EntityDamage> createRenderFor(RenderManager manager) {
               return new RenderDamage(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityBomb.class, new IRenderFactory<EntityBomb>() {
            public Render<? super EntityBomb> createRenderFor(RenderManager manager) {
               return new RenderBomb(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityBiomass.class, new IRenderFactory<EntityBiomass>() {
            public Render<? super EntityBiomass> createRenderFor(RenderManager manager) {
               return new RenderBiomass(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityGore.class, new IRenderFactory<EntityGore>() {
            public Render<? super EntityGore> createRenderFor(RenderManager manager) {
               return new RenderGore(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityProjectileHomming.class, new IRenderFactory<EntityProjectileHomming>() {
            public Render<? super EntityProjectileHomming> createRenderFor(RenderManager manager) {
               return new RenderProjectileHomming(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityBody.class, new IRenderFactory<EntityBody>() {
            public Render<? super EntityBody> createRenderFor(RenderManager manager) {
               return new RenderEntityBody(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityBodyModel.class, new IRenderFactory<EntityBodyModel>() {
            public Render<? super EntityBodyModel> createRenderFor(RenderManager manager) {
               return new RenderEntityBodyModel(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityToxicCloud.class, new IRenderFactory<EntityToxicCloud>() {
            public Render<? super EntityToxicCloud> createRenderFor(RenderManager manager) {
               return new RenderTCloud(manager);
            }
         });
         if (!SRPConfigSystems.oneMindDebug) {
            RenderingRegistry.registerEntityRenderingHandler(EntityParasiticScent.class, new IRenderFactory<EntityParasiticScent>() {
               public Render<? super EntityParasiticScent> createRenderFor(RenderManager manager) {
                  return new RenderScent(manager);
               }
            });
         }

         RenderingRegistry.registerEntityRenderingHandler(EntitySource.class, new IRenderFactory<EntitySource>() {
            public Render<? super EntitySource> createRenderFor(RenderManager manager) {
               return new RenderSource(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityScaryOrb.class, new IRenderFactory<EntityScaryOrb>() {
            public Render<? super EntityScaryOrb> createRenderFor(RenderManager manager) {
               return new RenderScaryOrb(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityNade.class, new IRenderFactory<EntityNade>() {
            public Render<? super EntityNade> createRenderFor(RenderManager manager) {
               return new RenderNade(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityWave.class, new IRenderFactory<EntityWave>() {
            public Render<? super EntityWave> createRenderFor(RenderManager manager) {
               return new RenderWave(manager);
            }
         });
         RenderingRegistry.registerEntityRenderingHandler(EntityRemain.class, new IRenderFactory<EntityRemain>() {
            public Render<? super EntityRemain> createRenderFor(RenderManager manager) {
               return new RenderRemain(manager);
            }
         });
      }
   }
}
