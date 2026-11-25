package com.lycanitesmobs.client.renderer;

import com.lycanitesmobs.LycanitesMobs;
import com.lycanitesmobs.core.info.CreatureInfo;
import net.minecraft.client.renderer.entity.Render;
import net.minecraft.client.renderer.entity.RenderManager;
import net.minecraft.entity.Entity;
import net.minecraftforge.fml.client.registry.IRenderFactory;

public class RenderFactoryCreature<T extends Entity> implements IRenderFactory {
   protected CreatureInfo creatureInfo;

   public RenderFactoryCreature(CreatureInfo creatureInfo) {
      this.creatureInfo = creatureInfo;
   }

   public Render createRenderFor(RenderManager manager) {
      try {
         return new RenderCreature(this.creatureInfo.getName(), manager, (float)this.creatureInfo.width / 2.0F);
      } catch (Exception var3) {
         LycanitesMobs.logWarning("", "An exception occurred rendering a creature model:");
         var3.printStackTrace();
         return null;
      }
   }
}
