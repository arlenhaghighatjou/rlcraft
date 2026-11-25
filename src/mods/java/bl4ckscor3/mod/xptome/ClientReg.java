package bl4ckscor3.mod.xptome;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;

public class ClientReg {
   public ClientReg() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, ClientReg::onModelRegistry);
   }

   public static void onModelRegistry(ModelRegistryEvent event) {
      ModelLoader.setCustomModelResourceLocation(XPTome.XP_BOOK, 0, new ModelResourceLocation(new ResourceLocation("xpbook", "xp_book"), "inventory"));
   }
}
