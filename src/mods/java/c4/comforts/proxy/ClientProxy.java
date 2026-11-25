package c4.comforts.proxy;

import c4.comforts.client.EventHandlerClient;
import c4.comforts.common.blocks.BlockHammock;
import c4.comforts.common.blocks.BlockSleepingBag;
import c4.comforts.common.blocks.ComfortsBlocks;
import c4.comforts.common.items.ComfortsItems;
import net.minecraft.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.color.BlockColors;
import net.minecraft.client.renderer.color.ItemColors;
import net.minecraft.item.Item;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.fml.common.Mod.EventBusSubscriber;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;

@EventBusSubscriber({Side.CLIENT})
public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void init(FMLInitializationEvent evt) {
      super.init(evt);
      MinecraftForge.EVENT_BUS.register(new EventHandlerClient());
      ItemColors itemColors = Minecraft.getMinecraft().getItemColors();
      itemColors.registerItemColorHandler(ComfortsItems.SLEEPING_BAG.getColorFromItemstack(), new Item[]{ComfortsItems.SLEEPING_BAG});
      itemColors.registerItemColorHandler(ComfortsItems.HAMMOCK.getColorFromItemstack(), new Item[]{ComfortsItems.HAMMOCK});
      BlockColors blockColors = Minecraft.getMinecraft().getBlockColors();

      for(BlockSleepingBag sleepingBag : ComfortsBlocks.SLEEPING_BAGS) {
         blockColors.registerBlockColorHandler(sleepingBag.colorMultiplier(), new Block[]{sleepingBag});
      }

      for(BlockHammock hammock : ComfortsBlocks.HAMMOCKS) {
         blockColors.registerBlockColorHandler(hammock.colorMultiplier(), new Block[]{hammock});
      }

   }

   @SubscribeEvent
   public static void registerModels(ModelRegistryEvent evt) {
      ComfortsBlocks.initModels();
      ComfortsItems.initModels();
   }
}
