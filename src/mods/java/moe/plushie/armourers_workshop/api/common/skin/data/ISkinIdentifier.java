package moe.plushie.armourers_workshop.api.common.skin.data;

import moe.plushie.armourers_workshop.api.common.library.ILibraryFile;
import moe.plushie.armourers_workshop.api.common.skin.type.ISkinType;

public interface ISkinIdentifier {
   boolean hasLocalId();

   boolean hasLibraryFile();

   boolean hasGlobalId();

   boolean isValid();

   int getSkinLocalId();

   ILibraryFile getSkinLibraryFile();

   int getSkinGlobalId();

   ISkinType getSkinType();
}
