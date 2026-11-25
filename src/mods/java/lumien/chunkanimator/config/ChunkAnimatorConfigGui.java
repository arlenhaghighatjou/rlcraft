package lumien.chunkanimator.config;

import java.util.ArrayList;
import java.util.List;
import lumien.chunkanimator.ChunkAnimator;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.fml.client.config.GuiConfig;
import net.minecraftforge.fml.client.config.IConfigElement;

public class ChunkAnimatorConfigGui extends GuiConfig {
   public ChunkAnimatorConfigGui(GuiScreen parent) {
      super(parent, getConfigElements(), "chunkanimator", false, false, GuiConfig.getAbridgedConfigPath(ChunkAnimator.INSTANCE.config.getString()));
   }

   private static List<IConfigElement> getConfigElements() {
      List<IConfigElement> list = new ArrayList();
      list.addAll(ChunkAnimator.INSTANCE.config.getConfigElements());
      return list;
   }
}
