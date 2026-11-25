package ivorius.ivtoolkit;

import com.google.common.eventbus.EventBus;
import java.util.Collections;
import net.minecraftforge.fml.common.DummyModContainer;
import net.minecraftforge.fml.common.LoadController;
import net.minecraftforge.fml.common.ModMetadata;

public class IvToolkitCoreContainer extends DummyModContainer {
   public IvToolkitCoreContainer() {
      super(new ModMetadata());
      ModMetadata myMeta = super.getMetadata();
      myMeta.authorList = Collections.singletonList("Ivorius");
      myMeta.description = "Uncategorized framework";
      myMeta.modId = "ivtoolkit";
      myMeta.version = "1.3.3-1.12";
      myMeta.name = "IvToolkit";
      myMeta.url = "http://www.minecraftforum.net/topic/563257-172";
   }

   public boolean registerBus(EventBus bus, LoadController controller) {
      bus.register(this);
      return true;
   }
}
