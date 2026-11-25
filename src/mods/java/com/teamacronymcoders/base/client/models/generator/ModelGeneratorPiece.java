package com.teamacronymcoders.base.client.models.generator;

import com.teamacronymcoders.base.IBaseMod;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPiece;
import com.teamacronymcoders.base.registrysystem.pieces.RegistryPieceBase;
import com.teamacronymcoders.base.registrysystem.pieces.RegistrySide;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.common.eventhandler.EventPriority;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

@SideOnly(Side.CLIENT)
@RegistryPiece(
   value = RegistrySide.CLIENT,
   priority = EventPriority.LOWEST
)
public class ModelGeneratorPiece extends RegistryPieceBase<IHasGeneratedModel> {
   private ModelGenerator modelGenerator;

   public ModelGeneratorPiece() {
      super(IHasGeneratedModel.class);
   }

   public void setMod(IBaseMod mod) {
      super.setMod(mod);
      this.modelGenerator = new ModelGenerator(mod);
   }

   public void onModelEvent(ResourceLocation name, IHasGeneratedModel entry) {
      this.modelGenerator.generate(entry);
   }
}
