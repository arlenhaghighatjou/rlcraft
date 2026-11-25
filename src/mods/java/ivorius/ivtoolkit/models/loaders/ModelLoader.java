package ivorius.ivtoolkit.models.loaders;

import ivorius.ivtoolkit.models.Model;
import ivorius.ivtoolkit.models.textures.TextureProvider;
import java.io.Reader;

public interface ModelLoader {
   Model createModel(Reader var1, TextureProvider var2);
}
