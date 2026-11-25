package com.teamacronymcoders.base.registrysystem.pieces;

import java.util.Comparator;
import net.minecraftforge.fml.common.eventhandler.EventPriority;

public class RegistryPieceComparator implements Comparator<IRegistryPiece> {
   public RegistryPieceComparator() {
   }

   public int compare(IRegistryPiece registryPiece1, IRegistryPiece registryPiece2) {
      RegistryPiece annotation1 = (RegistryPiece)registryPiece1.getClass().getAnnotation(RegistryPiece.class);
      RegistryPiece annotation2 = (RegistryPiece)registryPiece2.getClass().getAnnotation(RegistryPiece.class);
      EventPriority eventPriority1 = annotation1.priority();
      EventPriority eventPriority2 = annotation2.priority();
      return Integer.compare(eventPriority1.ordinal(), eventPriority2.ordinal());
   }
}
