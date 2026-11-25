package rustic.common.entities;

import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.client.registry.RenderingRegistry;
import net.minecraftforge.fml.common.registry.EntityRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import rustic.client.renderer.RenderTomato;
import rustic.common.blocks.BlockChair;
import rustic.core.Rustic;

public class ModEntities {
   public ModEntities() {
   }

   public static void init() {
      int id = 0;
      EntityRegistry.registerModEntity(new ResourceLocation("rustic:tomato"), EntityTomato.class, "tomato", id++, Rustic.instance, 64, 1, true);
      EntityRegistry.registerModEntity(new ResourceLocation("rustic:chair"), BlockChair.EntityChair.class, "chair", id++, Rustic.instance, 64, 128, false);
   }

   @SideOnly(Side.CLIENT)
   public static void initRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityTomato.class, new RenderTomato.Factory());
   }
}
