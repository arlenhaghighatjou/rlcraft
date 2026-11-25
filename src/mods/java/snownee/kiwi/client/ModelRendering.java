package snownee.kiwi.client;

import java.util.function.Consumer;
import java.util.function.Predicate;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import snownee.kiwi.KiwiManager;
import snownee.kiwi.block.IModBlock;
import snownee.kiwi.item.IModItem;
import snownee.kiwi.item.ItemModBlock;

@EventBusSubscriber(
   modid = "kiwi",
   value = {Side.CLIENT}
)
public class ModelRendering {
   public ModelRendering() {
   }

   @SubscribeEvent
   public static void onModelRegister(ModelRegistryEvent event) {
      KiwiManager.BLOCKS.keySet().forEach(IModBlock::mapModel);
      KiwiManager.ITEMS.keySet().stream().filter((item) -> {
         return !(item instanceof ItemModBlock);
      }).forEach(IModItem::mapModel);
   }
}
