package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.ObjectManager;
import com.lycanitesmobs.core.entity.CustomProjectileEntity;
import com.lycanitesmobs.core.entity.CustomProjectileModelEntity;
import com.lycanitesmobs.core.info.CreatureInfo;
import com.lycanitesmobs.core.info.CreatureManager;
import com.lycanitesmobs.core.info.ModInfo;
import com.lycanitesmobs.core.info.projectile.ProjectileManager;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class RenderRegister {
   public ModInfo modInfo;

   public RenderRegister(ModInfo modInfo) {
      this.modInfo = modInfo;
   }

   public void registerRenderFactories() {
      for(CreatureInfo creatureInfo : CreatureManager.getInstance().creatures.values()) {
         if (creatureInfo.dummy) {
            RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryNone(creatureInfo.entityClass));
         }

         RenderingRegistry.registerEntityRenderingHandler(creatureInfo.entityClass, new RenderFactoryCreature(creatureInfo));
      }

      RenderingRegistry.registerEntityRenderingHandler(CustomProjectileModelEntity.class, new RenderFactoryProjectile(true));
      RenderingRegistry.registerEntityRenderingHandler(CustomProjectileEntity.class, new RenderFactoryProjectile(false));

      for(String projectileName : ProjectileManager.getInstance().oldSpriteProjectiles.keySet()) {
         Class projectileClass = (Class)ProjectileManager.getInstance().oldSpriteProjectiles.get(projectileName);
         RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile(projectileName, projectileClass, false));
      }

      for(String projectileName : ProjectileManager.getInstance().oldModelProjectiles.keySet()) {
         Class projectileClass = (Class)ProjectileManager.getInstance().oldModelProjectiles.get(projectileName);
         RenderingRegistry.registerEntityRenderingHandler(projectileClass, new RenderFactoryProjectile(projectileName, projectileClass, true));
      }

      for(Class specialClass : ObjectManager.specialEntities.values()) {
         RenderingRegistry.registerEntityRenderingHandler(specialClass, new RenderFactoryNone(specialClass));
      }

   }
}
