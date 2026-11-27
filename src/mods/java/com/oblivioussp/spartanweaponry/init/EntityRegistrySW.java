package com.oblivioussp.spartanweaponry.init;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import com.oblivioussp.spartanweaponry.ModSpartanWeaponry;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowDiamond;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowExplosive;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowIron;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityArrowWood;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltDiamond;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltSpectral;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoltTipped;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityBoomerang;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityDynamite;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingAxe;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrowingKnife;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownJavelin;
import com.oblivioussp.spartanweaponry.entity.projectile.EntityThrownWeapon;
import com.oblivioussp.spartanweaponry.util.Log;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.registry.EntityEntry;
import net.minecraftforge.fml.common.registry.EntityRegistry;

public class EntityRegistrySW {
   public EntityRegistrySW() {
   }

   public static void register(EventBus bus) {
      bus.registerGeneric(Register.class, EntityEntry.class, EntityRegistrySW::registerEntities);
   }

   public static void registerEntities(Register<EntityEntry> ev) {
      int entityId = 0;
      Log.info("Registering entities!");
      ResourceLocation var10000 = new ResourceLocation("spartanweaponry", "bolt");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityBoltTipped.class, "spartanweaponry:bolt", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityBoltTipped::new);
      var10000 = new ResourceLocation("spartanweaponry", "bolt_specral");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityBoltSpectral.class, "spartanweaponry:bolt_spectral", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityBoltSpectral::new);
      var10000 = new ResourceLocation("spartanweaponry", "dagger_thrown");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityThrownWeapon.class, "spartanweaponry:dagger_thrown", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityThrownWeapon::new);
      var10000 = new ResourceLocation("spartanweaponry", "throwing_knife");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityThrowingKnife.class, "spartanweaponry:throwing_knife", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityThrowingKnife::new);
      var10000 = new ResourceLocation("spartanweaponry", "throwing_axe");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityThrowingAxe.class, "spartanweaponry:throwing_axe", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityThrowingAxe::new);
      var10000 = new ResourceLocation("spartanweaponry", "javelin_thrown");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityThrownJavelin.class, "spartanweaponry:javelin_thrown", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityThrownJavelin::new);
      var10000 = new ResourceLocation("spartanweaponry", "arrow_wood");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityArrowWood.class, "spartanweaponry:arrow_wood", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityArrowWood::new);
      var10000 = new ResourceLocation("spartanweaponry", "arrow_iron");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityArrowIron.class, "spartanweaponry:arrow_iron", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityArrowIron::new);
      var10000 = new ResourceLocation("spartanweaponry", "arrow_diamond");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityArrowDiamond.class, "spartanweaponry:arrow_diamond", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityArrowDiamond::new);
      var10000 = new ResourceLocation("spartanweaponry", "arrow_explosive");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityArrowExplosive.class, "spartanweaponry:arrow_explosive", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityArrowExplosive::new);
      var10000 = new ResourceLocation("spartanweaponry", "boomerang_thrown");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityBoomerang.class, "spartanweaponry:boomerang_thrown", entityId, ModSpartanWeaponry.instance, 64, 5, true, EntityBoomerang::new);
      var10000 = new ResourceLocation("spartanweaponry", "dynamite");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityDynamite.class, "spartanweaponry:dynamite", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityDynamite::new);
      var10000 = new ResourceLocation("spartanweaponry", "bolt_diamond");
      ++entityId;
      EntityRegistry.registerModEntity(var10000, EntityBoltDiamond.class, "spartanweaponry:bolt_diamond", entityId, ModSpartanWeaponry.instance, 64, 10, true, EntityBoltDiamond::new);
   }
}
