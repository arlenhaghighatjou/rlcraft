package com.teamacronymcoders.base.client.models;

import java.util.Optional;
import net.minecraftforge.common.model.IModelPart;
import net.minecraftforge.common.model.IModelState;
import net.minecraftforge.common.model.TRSRTransformation;

public class EmptyModelState implements IModelState {
   public EmptyModelState() {
   }

   public Optional<TRSRTransformation> apply(Optional<? extends IModelPart> part) {
      return Optional.empty();
   }
}
