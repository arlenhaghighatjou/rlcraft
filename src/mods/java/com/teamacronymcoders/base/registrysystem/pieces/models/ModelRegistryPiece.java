package com.teamacronymcoders.base.registrysystem.pieces.models;

import com.teamacronymcoders.base.client.models.IHasModel;
import com.teamacronymcoders.base.registrysystem.Registry;
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
   priority = EventPriority.LOW
)
public class ModelRegistryPiece extends RegistryPieceBase<IHasModel> {
   public ModelRegistryPiece() {
      super(IHasModel.class);
   }

   public boolean acceptsRegistry(Registry registry) {
      return "ITEM".equalsIgnoreCase(registry.getName());
   }

   public void onModelEvent(ResourceLocation name, IHasModel entry) {
      this.getMod().getLibProxy().setAllItemModels(entry);
   }
}
