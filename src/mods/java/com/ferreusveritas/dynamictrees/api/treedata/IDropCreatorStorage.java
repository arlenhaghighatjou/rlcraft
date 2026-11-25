package com.ferreusveritas.dynamictrees.api.treedata;

import java.util.Map;
import net.minecraft.util.ResourceLocation;

public interface IDropCreatorStorage extends IDropCreator {
   boolean addDropCreator(IDropCreator var1);

   IDropCreator findDropCreator(ResourceLocation var1);

   boolean remDropCreator(ResourceLocation var1);

   Map<ResourceLocation, IDropCreator> getDropCreators();
}
