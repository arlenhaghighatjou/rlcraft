package com.lycanitesmobs.core;

import com.lycanitesmobs.core.entity.EntityProjectileLaser;
import java.util.HashMap;
import java.util.Map;
import java.util.function.BiFunction;
import java.util.function.Function;
import net.minecraft.entity.Entity;
import net.minecraft.entity.EntityLivingBase;
import net.minecraft.world.World;

public class LycanitesClasses {
   private static final Map<String, Class<?>> CLASSES = new HashMap();
   private static final Map<Class<?>, Function<World, ? extends Entity>> WORLD = new HashMap();
   private static final Map<Class<?>, BiFunction<World, EntityLivingBase, ? extends Entity>> SHOOTER = new HashMap();
   private static final Map<Class<?>, LycanitesClasses.PositionFactory> POSITION = new HashMap();
   private static final Map<Class<?>, LycanitesClasses.LaserPositionFactory> LASER_POSITION = new HashMap();
   private static final Map<Class<?>, LycanitesClasses.LaserShooterFactory> LASER_SHOOTER = new HashMap();

   public LycanitesClasses() {
   }

   public static Class<?> forName(String name) throws ClassNotFoundException {
      Class<?> clazz = CLASSES.get(name);
      if (clazz == null) {
         throw new ClassNotFoundException(name);
      } else {
         return clazz;
      }
   }

   public static <T extends Entity> Function<World, T> worldFactory(Class<?> clazz) {
      return (Function)WORLD.get(clazz);
   }

   public static Entity create(Class<?> clazz, World world) {
      Function<World, ? extends Entity> factory = WORLD.get(clazz);
      if (factory == null) {
         throw new IllegalArgumentException("No world constructor for " + clazz);
      } else {
         return factory.apply(world);
      }
   }

   public static Entity create(Class<?> clazz, World world, EntityLivingBase shooter) {
      BiFunction<World, EntityLivingBase, ? extends Entity> factory = SHOOTER.get(clazz);
      if (factory == null) {
         throw new IllegalArgumentException("No shooter constructor for " + clazz);
      } else {
         return factory.apply(world, shooter);
      }
   }

   public static Entity create(Class<?> clazz, World world, double x, double y, double z) {
      LycanitesClasses.PositionFactory factory = POSITION.get(clazz);
      if (factory == null) {
         throw new IllegalArgumentException("No position constructor for " + clazz);
      } else {
         return factory.create(world, x, y, z);
      }
   }

   public static Entity create(Class<?> clazz, World world, double x, double y, double z, EntityProjectileLaser laser) {
      LycanitesClasses.LaserPositionFactory factory = LASER_POSITION.get(clazz);
      if (factory == null) {
         throw new IllegalArgumentException("No laser position constructor for " + clazz);
      } else {
         return factory.create(world, x, y, z, laser);
      }
   }

   public static Entity create(Class<?> clazz, World world, EntityLivingBase shooter, EntityProjectileLaser laser) {
      LycanitesClasses.LaserShooterFactory factory = LASER_SHOOTER.get(clazz);
      if (factory == null) {
         throw new IllegalArgumentException("No laser shooter constructor for " + clazz);
      } else {
         return factory.create(world, shooter, laser);
      }
   }

   static {
      CLASSES.put("com.lycanitesmobs.core.entity.BaseProjectileEntity", com.lycanitesmobs.core.entity.BaseProjectileEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.BaseProjectileEntity.class, com.lycanitesmobs.core.entity.BaseProjectileEntity::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.BaseProjectileEntity.class, com.lycanitesmobs.core.entity.BaseProjectileEntity::new);
      POSITION.put(com.lycanitesmobs.core.entity.BaseProjectileEntity.class, com.lycanitesmobs.core.entity.BaseProjectileEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.CustomProjectileEntity", com.lycanitesmobs.core.entity.CustomProjectileEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.CustomProjectileEntity.class, com.lycanitesmobs.core.entity.CustomProjectileEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.CustomProjectileModelEntity", com.lycanitesmobs.core.entity.CustomProjectileModelEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.CustomProjectileModelEntity.class, com.lycanitesmobs.core.entity.CustomProjectileModelEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.EntityHitArea", com.lycanitesmobs.core.entity.EntityHitArea.class);
      WORLD.put(com.lycanitesmobs.core.entity.EntityHitArea.class, com.lycanitesmobs.core.entity.EntityHitArea::new);
      CLASSES.put("com.lycanitesmobs.core.entity.EntityItemCustom", com.lycanitesmobs.core.entity.EntityItemCustom.class);
      WORLD.put(com.lycanitesmobs.core.entity.EntityItemCustom.class, com.lycanitesmobs.core.entity.EntityItemCustom::new);
      POSITION.put(com.lycanitesmobs.core.entity.EntityItemCustom.class, com.lycanitesmobs.core.entity.EntityItemCustom::new);
      CLASSES.put("com.lycanitesmobs.core.entity.EntityParticle", com.lycanitesmobs.core.entity.EntityParticle.class);
      CLASSES.put("com.lycanitesmobs.core.entity.EntityProjectileLaser", com.lycanitesmobs.core.entity.EntityProjectileLaser.class);
      WORLD.put(com.lycanitesmobs.core.entity.EntityProjectileLaser.class, com.lycanitesmobs.core.entity.EntityProjectileLaser::new);
      CLASSES.put("com.lycanitesmobs.core.entity.EntityProjectileModel", com.lycanitesmobs.core.entity.EntityProjectileModel.class);
      WORLD.put(com.lycanitesmobs.core.entity.EntityProjectileModel.class, com.lycanitesmobs.core.entity.EntityProjectileModel::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.EntityProjectileModel.class, com.lycanitesmobs.core.entity.EntityProjectileModel::new);
      POSITION.put(com.lycanitesmobs.core.entity.EntityProjectileModel.class, com.lycanitesmobs.core.entity.EntityProjectileModel::new);
      CLASSES.put("com.lycanitesmobs.core.entity.FearEntity", com.lycanitesmobs.core.entity.FearEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.FearEntity.class, com.lycanitesmobs.core.entity.FearEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.LaserEndProjectileEntity", com.lycanitesmobs.core.entity.LaserEndProjectileEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.LaserEndProjectileEntity.class, com.lycanitesmobs.core.entity.LaserEndProjectileEntity::new);
      LASER_POSITION.put(com.lycanitesmobs.core.entity.LaserEndProjectileEntity.class, com.lycanitesmobs.core.entity.LaserEndProjectileEntity::new);
      LASER_SHOOTER.put(com.lycanitesmobs.core.entity.LaserEndProjectileEntity.class, com.lycanitesmobs.core.entity.LaserEndProjectileEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.PortalEntity", com.lycanitesmobs.core.entity.PortalEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.PortalEntity.class, com.lycanitesmobs.core.entity.PortalEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.RapidFireProjectileEntity", com.lycanitesmobs.core.entity.RapidFireProjectileEntity.class);
      CLASSES.put("com.lycanitesmobs.core.entity.RideableCreatureEntity", com.lycanitesmobs.core.entity.RideableCreatureEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.RideableCreatureEntity.class, com.lycanitesmobs.core.entity.RideableCreatureEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.TameableCreatureEntity", com.lycanitesmobs.core.entity.TameableCreatureEntity.class);
      WORLD.put(com.lycanitesmobs.core.entity.TameableCreatureEntity.class, com.lycanitesmobs.core.entity.TameableCreatureEntity::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAbaia", com.lycanitesmobs.core.entity.creature.EntityAbaia.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAbaia.class, com.lycanitesmobs.core.entity.creature.EntityAbaia::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAbtu", com.lycanitesmobs.core.entity.creature.EntityAbtu.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAbtu.class, com.lycanitesmobs.core.entity.creature.EntityAbtu::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAegis", com.lycanitesmobs.core.entity.creature.EntityAegis.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAegis.class, com.lycanitesmobs.core.entity.creature.EntityAegis::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAfrit", com.lycanitesmobs.core.entity.creature.EntityAfrit.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAfrit.class, com.lycanitesmobs.core.entity.creature.EntityAfrit::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAglebemu", com.lycanitesmobs.core.entity.creature.EntityAglebemu.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAglebemu.class, com.lycanitesmobs.core.entity.creature.EntityAglebemu::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAmalgalich", com.lycanitesmobs.core.entity.creature.EntityAmalgalich.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAmalgalich.class, com.lycanitesmobs.core.entity.creature.EntityAmalgalich::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityArchvile", com.lycanitesmobs.core.entity.creature.EntityArchvile.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityArchvile.class, com.lycanitesmobs.core.entity.creature.EntityArchvile::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityArgus", com.lycanitesmobs.core.entity.creature.EntityArgus.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityArgus.class, com.lycanitesmobs.core.entity.creature.EntityArgus::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityArisaur", com.lycanitesmobs.core.entity.creature.EntityArisaur.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityArisaur.class, com.lycanitesmobs.core.entity.creature.EntityArisaur::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityArix", com.lycanitesmobs.core.entity.creature.EntityArix.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityArix.class, com.lycanitesmobs.core.entity.creature.EntityArix::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAsmodeus", com.lycanitesmobs.core.entity.creature.EntityAsmodeus.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAsmodeus.class, com.lycanitesmobs.core.entity.creature.EntityAsmodeus::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAspid", com.lycanitesmobs.core.entity.creature.EntityAspid.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAspid.class, com.lycanitesmobs.core.entity.creature.EntityAspid::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityAstaroth", com.lycanitesmobs.core.entity.creature.EntityAstaroth.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityAstaroth.class, com.lycanitesmobs.core.entity.creature.EntityAstaroth::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBalayang", com.lycanitesmobs.core.entity.creature.EntityBalayang.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBalayang.class, com.lycanitesmobs.core.entity.creature.EntityBalayang::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBanshee", com.lycanitesmobs.core.entity.creature.EntityBanshee.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBanshee.class, com.lycanitesmobs.core.entity.creature.EntityBanshee::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBarghest", com.lycanitesmobs.core.entity.creature.EntityBarghest.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBarghest.class, com.lycanitesmobs.core.entity.creature.EntityBarghest::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBehemoth", com.lycanitesmobs.core.entity.creature.EntityBehemoth.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBehemoth.class, com.lycanitesmobs.core.entity.creature.EntityBehemoth::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBeholder", com.lycanitesmobs.core.entity.creature.EntityBeholder.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBeholder.class, com.lycanitesmobs.core.entity.creature.EntityBeholder::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBelph", com.lycanitesmobs.core.entity.creature.EntityBelph.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBelph.class, com.lycanitesmobs.core.entity.creature.EntityBelph::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityBobeko", com.lycanitesmobs.core.entity.creature.EntityBobeko.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityBobeko.class, com.lycanitesmobs.core.entity.creature.EntityBobeko::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCacodemon", com.lycanitesmobs.core.entity.creature.EntityCacodemon.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCacodemon.class, com.lycanitesmobs.core.entity.creature.EntityCacodemon::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCalpod", com.lycanitesmobs.core.entity.creature.EntityCalpod.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCalpod.class, com.lycanitesmobs.core.entity.creature.EntityCalpod::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCephignis", com.lycanitesmobs.core.entity.creature.EntityCephignis.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCephignis.class, com.lycanitesmobs.core.entity.creature.EntityCephignis::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityChupacabra", com.lycanitesmobs.core.entity.creature.EntityChupacabra.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityChupacabra.class, com.lycanitesmobs.core.entity.creature.EntityChupacabra::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCinder", com.lycanitesmobs.core.entity.creature.EntityCinder.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCinder.class, com.lycanitesmobs.core.entity.creature.EntityCinder::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityClink", com.lycanitesmobs.core.entity.creature.EntityClink.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityClink.class, com.lycanitesmobs.core.entity.creature.EntityClink::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCockatrice", com.lycanitesmobs.core.entity.creature.EntityCockatrice.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCockatrice.class, com.lycanitesmobs.core.entity.creature.EntityCockatrice::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityConba", com.lycanitesmobs.core.entity.creature.EntityConba.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityConba.class, com.lycanitesmobs.core.entity.creature.EntityConba::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityConcapedeHead", com.lycanitesmobs.core.entity.creature.EntityConcapedeHead.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityConcapedeHead.class, com.lycanitesmobs.core.entity.creature.EntityConcapedeHead::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityConcapedeSegment", com.lycanitesmobs.core.entity.creature.EntityConcapedeSegment.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityConcapedeSegment.class, com.lycanitesmobs.core.entity.creature.EntityConcapedeSegment::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCrusk", com.lycanitesmobs.core.entity.creature.EntityCrusk.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCrusk.class, com.lycanitesmobs.core.entity.creature.EntityCrusk::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityCryptkeeper", com.lycanitesmobs.core.entity.creature.EntityCryptkeeper.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityCryptkeeper.class, com.lycanitesmobs.core.entity.creature.EntityCryptkeeper::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityDarkling", com.lycanitesmobs.core.entity.creature.EntityDarkling.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityDarkling.class, com.lycanitesmobs.core.entity.creature.EntityDarkling::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityDawon", com.lycanitesmobs.core.entity.creature.EntityDawon.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityDawon.class, com.lycanitesmobs.core.entity.creature.EntityDawon::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityDjinn", com.lycanitesmobs.core.entity.creature.EntityDjinn.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityDjinn.class, com.lycanitesmobs.core.entity.creature.EntityDjinn::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityDweller", com.lycanitesmobs.core.entity.creature.EntityDweller.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityDweller.class, com.lycanitesmobs.core.entity.creature.EntityDweller::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityEechetik", com.lycanitesmobs.core.entity.creature.EntityEechetik.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityEechetik.class, com.lycanitesmobs.core.entity.creature.EntityEechetik::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityEnt", com.lycanitesmobs.core.entity.creature.EntityEnt.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityEnt.class, com.lycanitesmobs.core.entity.creature.EntityEnt::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityEpion", com.lycanitesmobs.core.entity.creature.EntityEpion.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityEpion.class, com.lycanitesmobs.core.entity.creature.EntityEpion::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityErepede", com.lycanitesmobs.core.entity.creature.EntityErepede.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityErepede.class, com.lycanitesmobs.core.entity.creature.EntityErepede::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityEttin", com.lycanitesmobs.core.entity.creature.EntityEttin.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityEttin.class, com.lycanitesmobs.core.entity.creature.EntityEttin::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityEyewig", com.lycanitesmobs.core.entity.creature.EntityEyewig.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityEyewig.class, com.lycanitesmobs.core.entity.creature.EntityEyewig::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityFeradon", com.lycanitesmobs.core.entity.creature.EntityFeradon.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityFeradon.class, com.lycanitesmobs.core.entity.creature.EntityFeradon::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityFrostweaver", com.lycanitesmobs.core.entity.creature.EntityFrostweaver.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityFrostweaver.class, com.lycanitesmobs.core.entity.creature.EntityFrostweaver::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGeist", com.lycanitesmobs.core.entity.creature.EntityGeist.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGeist.class, com.lycanitesmobs.core.entity.creature.EntityGeist::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGeken", com.lycanitesmobs.core.entity.creature.EntityGeken.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGeken.class, com.lycanitesmobs.core.entity.creature.EntityGeken::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGeonach", com.lycanitesmobs.core.entity.creature.EntityGeonach.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGeonach.class, com.lycanitesmobs.core.entity.creature.EntityGeonach::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGhoul", com.lycanitesmobs.core.entity.creature.EntityGhoul.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGhoul.class, com.lycanitesmobs.core.entity.creature.EntityGhoul::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGnekk", com.lycanitesmobs.core.entity.creature.EntityGnekk.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGnekk.class, com.lycanitesmobs.core.entity.creature.EntityGnekk::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGorger", com.lycanitesmobs.core.entity.creature.EntityGorger.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGorger.class, com.lycanitesmobs.core.entity.creature.EntityGorger::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGorgomite", com.lycanitesmobs.core.entity.creature.EntityGorgomite.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGorgomite.class, com.lycanitesmobs.core.entity.creature.EntityGorgomite::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGrell", com.lycanitesmobs.core.entity.creature.EntityGrell.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGrell.class, com.lycanitesmobs.core.entity.creature.EntityGrell::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGrigori", com.lycanitesmobs.core.entity.creature.EntityGrigori.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGrigori.class, com.lycanitesmobs.core.entity.creature.EntityGrigori::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityGrue", com.lycanitesmobs.core.entity.creature.EntityGrue.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityGrue.class, com.lycanitesmobs.core.entity.creature.EntityGrue::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityHerma", com.lycanitesmobs.core.entity.creature.EntityHerma.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityHerma.class, com.lycanitesmobs.core.entity.creature.EntityHerma::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityIgnibus", com.lycanitesmobs.core.entity.creature.EntityIgnibus.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityIgnibus.class, com.lycanitesmobs.core.entity.creature.EntityIgnibus::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityIka", com.lycanitesmobs.core.entity.creature.EntityIka.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityIka.class, com.lycanitesmobs.core.entity.creature.EntityIka::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityIoray", com.lycanitesmobs.core.entity.creature.EntityIoray.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityIoray.class, com.lycanitesmobs.core.entity.creature.EntityIoray::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityJabberwock", com.lycanitesmobs.core.entity.creature.EntityJabberwock.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityJabberwock.class, com.lycanitesmobs.core.entity.creature.EntityJabberwock::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityJengu", com.lycanitesmobs.core.entity.creature.EntityJengu.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityJengu.class, com.lycanitesmobs.core.entity.creature.EntityJengu::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityJoust", com.lycanitesmobs.core.entity.creature.EntityJoust.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityJoust.class, com.lycanitesmobs.core.entity.creature.EntityJoust::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityJoustAlpha", com.lycanitesmobs.core.entity.creature.EntityJoustAlpha.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityJoustAlpha.class, com.lycanitesmobs.core.entity.creature.EntityJoustAlpha::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityKhalk", com.lycanitesmobs.core.entity.creature.EntityKhalk.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityKhalk.class, com.lycanitesmobs.core.entity.creature.EntityKhalk::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityKobold", com.lycanitesmobs.core.entity.creature.EntityKobold.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityKobold.class, com.lycanitesmobs.core.entity.creature.EntityKobold::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityKrake", com.lycanitesmobs.core.entity.creature.EntityKrake.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityKrake.class, com.lycanitesmobs.core.entity.creature.EntityKrake::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityLacedon", com.lycanitesmobs.core.entity.creature.EntityLacedon.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityLacedon.class, com.lycanitesmobs.core.entity.creature.EntityLacedon::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityLobber", com.lycanitesmobs.core.entity.creature.EntityLobber.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityLobber.class, com.lycanitesmobs.core.entity.creature.EntityLobber::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityLurker", com.lycanitesmobs.core.entity.creature.EntityLurker.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityLurker.class, com.lycanitesmobs.core.entity.creature.EntityLurker::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityMaka", com.lycanitesmobs.core.entity.creature.EntityMaka.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityMaka.class, com.lycanitesmobs.core.entity.creature.EntityMaka::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityMakaAlpha", com.lycanitesmobs.core.entity.creature.EntityMakaAlpha.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityMakaAlpha.class, com.lycanitesmobs.core.entity.creature.EntityMakaAlpha::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityMaug", com.lycanitesmobs.core.entity.creature.EntityMaug.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityMaug.class, com.lycanitesmobs.core.entity.creature.EntityMaug::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityMorock", com.lycanitesmobs.core.entity.creature.EntityMorock.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityMorock.class, com.lycanitesmobs.core.entity.creature.EntityMorock::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityNecrovore", com.lycanitesmobs.core.entity.creature.EntityNecrovore.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityNecrovore.class, com.lycanitesmobs.core.entity.creature.EntityNecrovore::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityNymph", com.lycanitesmobs.core.entity.creature.EntityNymph.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityNymph.class, com.lycanitesmobs.core.entity.creature.EntityNymph::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityPinky", com.lycanitesmobs.core.entity.creature.EntityPinky.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityPinky.class, com.lycanitesmobs.core.entity.creature.EntityPinky::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityPixen", com.lycanitesmobs.core.entity.creature.EntityPixen.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityPixen.class, com.lycanitesmobs.core.entity.creature.EntityPixen::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityQuetzodracl", com.lycanitesmobs.core.entity.creature.EntityQuetzodracl.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityQuetzodracl.class, com.lycanitesmobs.core.entity.creature.EntityQuetzodracl::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityQuillbeast", com.lycanitesmobs.core.entity.creature.EntityQuillbeast.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityQuillbeast.class, com.lycanitesmobs.core.entity.creature.EntityQuillbeast::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityRahovart", com.lycanitesmobs.core.entity.creature.EntityRahovart.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityRahovart.class, com.lycanitesmobs.core.entity.creature.EntityRahovart::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityRaiko", com.lycanitesmobs.core.entity.creature.EntityRaiko.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityRaiko.class, com.lycanitesmobs.core.entity.creature.EntityRaiko::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityReaper", com.lycanitesmobs.core.entity.creature.EntityReaper.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityReaper.class, com.lycanitesmobs.core.entity.creature.EntityReaper::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityReiver", com.lycanitesmobs.core.entity.creature.EntityReiver.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityReiver.class, com.lycanitesmobs.core.entity.creature.EntityReiver::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityRemobra", com.lycanitesmobs.core.entity.creature.EntityRemobra.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityRemobra.class, com.lycanitesmobs.core.entity.creature.EntityRemobra::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityRoa", com.lycanitesmobs.core.entity.creature.EntityRoa.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityRoa.class, com.lycanitesmobs.core.entity.creature.EntityRoa::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityRoc", com.lycanitesmobs.core.entity.creature.EntityRoc.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityRoc.class, com.lycanitesmobs.core.entity.creature.EntityRoc::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySalamander", com.lycanitesmobs.core.entity.creature.EntitySalamander.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySalamander.class, com.lycanitesmobs.core.entity.creature.EntitySalamander::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySerpix", com.lycanitesmobs.core.entity.creature.EntitySerpix.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySerpix.class, com.lycanitesmobs.core.entity.creature.EntitySerpix::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityShade", com.lycanitesmobs.core.entity.creature.EntityShade.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityShade.class, com.lycanitesmobs.core.entity.creature.EntityShade::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityShambler", com.lycanitesmobs.core.entity.creature.EntityShambler.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityShambler.class, com.lycanitesmobs.core.entity.creature.EntityShambler::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySilex", com.lycanitesmobs.core.entity.creature.EntitySilex.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySilex.class, com.lycanitesmobs.core.entity.creature.EntitySilex::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySkylus", com.lycanitesmobs.core.entity.creature.EntitySkylus.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySkylus.class, com.lycanitesmobs.core.entity.creature.EntitySkylus::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySpectre", com.lycanitesmobs.core.entity.creature.EntitySpectre.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySpectre.class, com.lycanitesmobs.core.entity.creature.EntitySpectre::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySpriggan", com.lycanitesmobs.core.entity.creature.EntitySpriggan.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySpriggan.class, com.lycanitesmobs.core.entity.creature.EntitySpriggan::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityStrider", com.lycanitesmobs.core.entity.creature.EntityStrider.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityStrider.class, com.lycanitesmobs.core.entity.creature.EntityStrider::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySutiramu", com.lycanitesmobs.core.entity.creature.EntitySutiramu.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySutiramu.class, com.lycanitesmobs.core.entity.creature.EntitySutiramu::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntitySylph", com.lycanitesmobs.core.entity.creature.EntitySylph.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntitySylph.class, com.lycanitesmobs.core.entity.creature.EntitySylph::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTarantula", com.lycanitesmobs.core.entity.creature.EntityTarantula.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTarantula.class, com.lycanitesmobs.core.entity.creature.EntityTarantula::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityThresher", com.lycanitesmobs.core.entity.creature.EntityThresher.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityThresher.class, com.lycanitesmobs.core.entity.creature.EntityThresher::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTpumpkyn", com.lycanitesmobs.core.entity.creature.EntityTpumpkyn.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTpumpkyn.class, com.lycanitesmobs.core.entity.creature.EntityTpumpkyn::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTreant", com.lycanitesmobs.core.entity.creature.EntityTreant.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTreant.class, com.lycanitesmobs.core.entity.creature.EntityTreant::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTremor", com.lycanitesmobs.core.entity.creature.EntityTremor.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTremor.class, com.lycanitesmobs.core.entity.creature.EntityTremor::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTriffid", com.lycanitesmobs.core.entity.creature.EntityTriffid.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTriffid.class, com.lycanitesmobs.core.entity.creature.EntityTriffid::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTrite", com.lycanitesmobs.core.entity.creature.EntityTrite.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTrite.class, com.lycanitesmobs.core.entity.creature.EntityTrite::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityTroll", com.lycanitesmobs.core.entity.creature.EntityTroll.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityTroll.class, com.lycanitesmobs.core.entity.creature.EntityTroll::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityUvaraptor", com.lycanitesmobs.core.entity.creature.EntityUvaraptor.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityUvaraptor.class, com.lycanitesmobs.core.entity.creature.EntityUvaraptor::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityVapula", com.lycanitesmobs.core.entity.creature.EntityVapula.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityVapula.class, com.lycanitesmobs.core.entity.creature.EntityVapula::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityVentoraptor", com.lycanitesmobs.core.entity.creature.EntityVentoraptor.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityVentoraptor.class, com.lycanitesmobs.core.entity.creature.EntityVentoraptor::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityVespid", com.lycanitesmobs.core.entity.creature.EntityVespid.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityVespid.class, com.lycanitesmobs.core.entity.creature.EntityVespid::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityVespidQueen", com.lycanitesmobs.core.entity.creature.EntityVespidQueen.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityVespidQueen.class, com.lycanitesmobs.core.entity.creature.EntityVespidQueen::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityVolcan", com.lycanitesmobs.core.entity.creature.EntityVolcan.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityVolcan.class, com.lycanitesmobs.core.entity.creature.EntityVolcan::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityVorach", com.lycanitesmobs.core.entity.creature.EntityVorach.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityVorach.class, com.lycanitesmobs.core.entity.creature.EntityVorach::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityWarg", com.lycanitesmobs.core.entity.creature.EntityWarg.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityWarg.class, com.lycanitesmobs.core.entity.creature.EntityWarg::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityWendigo", com.lycanitesmobs.core.entity.creature.EntityWendigo.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityWendigo.class, com.lycanitesmobs.core.entity.creature.EntityWendigo::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityWildkin", com.lycanitesmobs.core.entity.creature.EntityWildkin.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityWildkin.class, com.lycanitesmobs.core.entity.creature.EntityWildkin::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityWisp", com.lycanitesmobs.core.entity.creature.EntityWisp.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityWisp.class, com.lycanitesmobs.core.entity.creature.EntityWisp::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityWraamon", com.lycanitesmobs.core.entity.creature.EntityWraamon.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityWraamon.class, com.lycanitesmobs.core.entity.creature.EntityWraamon::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityWraith", com.lycanitesmobs.core.entity.creature.EntityWraith.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityWraith.class, com.lycanitesmobs.core.entity.creature.EntityWraith::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityXaphan", com.lycanitesmobs.core.entity.creature.EntityXaphan.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityXaphan.class, com.lycanitesmobs.core.entity.creature.EntityXaphan::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityYale", com.lycanitesmobs.core.entity.creature.EntityYale.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityYale.class, com.lycanitesmobs.core.entity.creature.EntityYale::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityZephyr", com.lycanitesmobs.core.entity.creature.EntityZephyr.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityZephyr.class, com.lycanitesmobs.core.entity.creature.EntityZephyr::new);
      CLASSES.put("com.lycanitesmobs.core.entity.creature.EntityZoataur", com.lycanitesmobs.core.entity.creature.EntityZoataur.class);
      WORLD.put(com.lycanitesmobs.core.entity.creature.EntityZoataur.class, com.lycanitesmobs.core.entity.creature.EntityZoataur::new);
      CLASSES.put("com.lycanitesmobs.core.entity.damagesources.ElementDamageSource", com.lycanitesmobs.core.entity.damagesources.ElementDamageSource.class);
      CLASSES.put("com.lycanitesmobs.core.entity.damagesources.MinionEntityDamageSource", com.lycanitesmobs.core.entity.damagesources.MinionEntityDamageSource.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.BaseGoal", com.lycanitesmobs.core.entity.goals.BaseGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal", com.lycanitesmobs.core.entity.goals.actions.AttackMeleeGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal", com.lycanitesmobs.core.entity.goals.actions.AttackRangedGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.AvoidGoal", com.lycanitesmobs.core.entity.goals.actions.AvoidGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.BegGoal", com.lycanitesmobs.core.entity.goals.actions.BegGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal", com.lycanitesmobs.core.entity.goals.actions.BreakDoorGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.BuildAroundTargetGoal", com.lycanitesmobs.core.entity.goals.actions.BuildAroundTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.ChaseGoal", com.lycanitesmobs.core.entity.goals.actions.ChaseGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.EatBlockGoal", com.lycanitesmobs.core.entity.goals.actions.EatBlockGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal", com.lycanitesmobs.core.entity.goals.actions.FindNearbyPlayersGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.FollowFuseGoal", com.lycanitesmobs.core.entity.goals.actions.FollowFuseGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.FollowMasterGoal", com.lycanitesmobs.core.entity.goals.actions.FollowMasterGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.FollowOwnerGoal", com.lycanitesmobs.core.entity.goals.actions.FollowOwnerGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal", com.lycanitesmobs.core.entity.goals.actions.FollowParentGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.LookIdleGoal", com.lycanitesmobs.core.entity.goals.actions.LookIdleGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.MateGoal", com.lycanitesmobs.core.entity.goals.actions.MateGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.MoveRestrictionGoal", com.lycanitesmobs.core.entity.goals.actions.MoveRestrictionGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal", com.lycanitesmobs.core.entity.goals.actions.MoveVillageGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.PaddleGoal", com.lycanitesmobs.core.entity.goals.actions.PaddleGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.PlayerControlGoal", com.lycanitesmobs.core.entity.goals.actions.PlayerControlGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.StayByHomeGoal", com.lycanitesmobs.core.entity.goals.actions.StayByHomeGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.StayByWaterGoal", com.lycanitesmobs.core.entity.goals.actions.StayByWaterGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.StayGoal", com.lycanitesmobs.core.entity.goals.actions.StayGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.TemptGoal", com.lycanitesmobs.core.entity.goals.actions.TemptGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.WanderGoal", com.lycanitesmobs.core.entity.goals.actions.WanderGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.WatchClosestGoal", com.lycanitesmobs.core.entity.goals.actions.WatchClosestGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.EffectAuraGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.EffectAuraGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.FaceTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.FireProjectilesGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.ForceGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.ForceGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.GetBlockGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.GetBlockGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.GetItemGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.GetItemGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.GrowGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.GrowGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.HealWhenNoPlayersGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.PlaceBlockGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.PlaceBlockGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.StealthGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.SuicideGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.SuicideGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal", com.lycanitesmobs.core.entity.goals.actions.abilities.SummonMinionsGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.AvoidIfHitGoal", com.lycanitesmobs.core.entity.goals.targeting.AvoidIfHitGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.CopyMasterAttackTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.CopyOwnerAttackTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.CopyOwnerAttackTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.CopyParentAttackTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.CopyParentAttackTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.CopyRiderAttackTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.CopyRiderAttackTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal", com.lycanitesmobs.core.entity.goals.targeting.DefendEntitiesGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.DefendOwnerGoal", com.lycanitesmobs.core.entity.goals.targeting.DefendOwnerGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal", com.lycanitesmobs.core.entity.goals.targeting.DefendVillageGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.FindAttackTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.FindAvoidTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindFuseTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.FindFuseTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindGroupAttackTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.FindGroupAttackTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindGroupAvoidTargetGoal", com.lycanitesmobs.core.entity.goals.targeting.FindGroupAvoidTargetGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal", com.lycanitesmobs.core.entity.goals.targeting.FindMasterGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.FindParentGoal", com.lycanitesmobs.core.entity.goals.targeting.FindParentGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal", com.lycanitesmobs.core.entity.goals.targeting.RevengeGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.RevengeOwnerGoal", com.lycanitesmobs.core.entity.goals.targeting.RevengeOwnerGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.goals.targeting.RevengeRiderGoal", com.lycanitesmobs.core.entity.goals.targeting.RevengeRiderGoal.class);
      CLASSES.put("com.lycanitesmobs.core.entity.navigate.CreatureMoveHelper", com.lycanitesmobs.core.entity.navigate.CreatureMoveHelper.class);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityDevilGatling", com.lycanitesmobs.core.entity.projectile.EntityDevilGatling.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityDevilGatling.class, com.lycanitesmobs.core.entity.projectile.EntityDevilGatling::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityDevilGatling.class, com.lycanitesmobs.core.entity.projectile.EntityDevilGatling::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityDevilGatling.class, com.lycanitesmobs.core.entity.projectile.EntityDevilGatling::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellLaser", com.lycanitesmobs.core.entity.projectile.EntityHellLaser.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellLaser.class, com.lycanitesmobs.core.entity.projectile.EntityHellLaser::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd", com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd.class, com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd::new);
      LASER_POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd.class, com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd::new);
      LASER_SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd.class, com.lycanitesmobs.core.entity.projectile.EntityHellLaserEnd::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellShield", com.lycanitesmobs.core.entity.projectile.EntityHellShield.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellShield.class, com.lycanitesmobs.core.entity.projectile.EntityHellShield::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellShield.class, com.lycanitesmobs.core.entity.projectile.EntityHellShield::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellShield.class, com.lycanitesmobs.core.entity.projectile.EntityHellShield::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier", com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrier::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart", com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireBarrierPart::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb", com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireOrb::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellfireWall", com.lycanitesmobs.core.entity.projectile.EntityHellfireWall.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWall.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWall::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWall.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWall::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWall.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWall::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellfireWave", com.lycanitesmobs.core.entity.projectile.EntityHellfireWave.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWave.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWave::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWave.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWave::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWave.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWave::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart", com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart.class, com.lycanitesmobs.core.entity.projectile.EntityHellfireWavePart::new);
      CLASSES.put("com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier", com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier.class);
      WORLD.put(com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier.class, com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier::new);
      SHOOTER.put(com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier.class, com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier::new);
      POSITION.put(com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier.class, com.lycanitesmobs.core.entity.projectile.EntityShadowfireBarrier::new);
   }

   public interface PositionFactory {
      Entity create(World world, double x, double y, double z);
   }

   public interface LaserPositionFactory {
      Entity create(World world, double x, double y, double z, EntityProjectileLaser laser);
   }

   public interface LaserShooterFactory {
      Entity create(World world, EntityLivingBase shooter, EntityProjectileLaser laser);
   }
}
