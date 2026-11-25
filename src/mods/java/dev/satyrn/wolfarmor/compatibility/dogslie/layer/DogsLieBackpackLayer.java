package dev.satyrn.wolfarmor.compatibility.dogslie.layer;

import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfBackpack;
import dev.satyrn.wolfarmor.compatibility.dogslie.model.DogsLieWolfBackpackModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DogsLieBackpackLayer extends LayerWolfBackpack {
   public DogsLieBackpackLayer(@Nonnull RenderLiving<?> renderer) {
      super(renderer);
      this.modelWolfBackpack = new DogsLieWolfBackpackModel(0.0F);
   }
}
