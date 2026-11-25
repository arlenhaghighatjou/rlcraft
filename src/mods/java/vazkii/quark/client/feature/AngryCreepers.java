package vazkii.quark.client.feature;

import net.minecraft.entity.monster.EntityCreeper;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import vazkii.quark.base.module.Feature;
import vazkii.quark.client.render.RenderCreeperAngry;

public class AngryCreepers extends Feature {
   public AngryCreepers() {
   }

   @SideOnly(Side.CLIENT)
   public void preInitClient() {
      RenderingRegistry.registerEntityRenderingHandler(EntityCreeper.class, RenderCreeperAngry.factory());
   }

   public String getFeatureIngameConfigName() {
      return "Creepers Turn Red";
   }
}
