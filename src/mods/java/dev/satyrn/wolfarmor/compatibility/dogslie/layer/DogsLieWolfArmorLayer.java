package dev.satyrn.wolfarmor.compatibility.dogslie.layer;

import dev.satyrn.wolfarmor.client.renderer.entity.layer.LayerWolfArmor;
import dev.satyrn.wolfarmor.compatibility.dogslie.model.DogsLieWolfArmorModel;
import javax.annotation.Nonnull;
import net.minecraft.client.renderer.entity.RenderLiving;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
public class DogsLieWolfArmorLayer extends LayerWolfArmor {
   public DogsLieWolfArmorLayer(@Nonnull RenderLiving<?> renderer) {
      super(renderer);
   }

   protected void initArmor() {
      DogsLieWolfArmorModel innerLayer = new DogsLieWolfArmorModel(0.1F);
      DogsLieWolfArmorModel outerLayer = new DogsLieWolfArmorModel(0.2F);
      this.setArmorInnerLayer(innerLayer);
      this.setArmorOuterLayer(outerLayer);
   }
}
