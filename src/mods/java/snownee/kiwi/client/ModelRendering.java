package snownee.kiwi.client;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.kiwi.KiwiManager;
import snownee.kiwi.block.IModBlock;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.item.ItemModBlock;

public class ModelRendering {
   public ModelRendering() {
   }

   public static void register(EventBus bus) {
      bus.register(ModelRegistryEvent.class, ModelRendering::onModelRegister);
   }

   public static void onModelRegister(ModelRegistryEvent event) {
      KiwiManager.BLOCKS.keySet().forEach(IModBlock::mapModel);
      KiwiManager.ITEMS.keySet().stream().filter((item) -> {
         return !(item instanceof ItemModBlock);
      }).forEach(IModItem::mapModel);
   }
}
