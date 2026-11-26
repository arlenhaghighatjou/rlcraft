package net.theawesomegem.fishingmadebetter.client.registry;

import net.minecraftforge.fml.common.eventhandler.EventBus;
import net.minecraft.client.renderer.block.model.IBakedModel;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.event.ModelBakeEvent;
import net.minecraftforge.client.event.ModelRegistryEvent;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.relauncher.Side;
import net.theawesomegem.fishingmadebetter.client.BakedModelRod;
import net.theawesomegem.fishingmadebetter.common.block.BlockManager;
import net.theawesomegem.fishingmadebetter.common.item.ItemManager;
import net.theawesomegem.fishingmadebetter.common.item.attachment.bobber.ItemBobber;
import net.theawesomegem.fishingmadebetter.common.item.attachment.hook.ItemHook;
import net.theawesomegem.fishingmadebetter.common.item.attachment.reel.ItemReel;
import net.theawesomegem.fishingmadebetter.common.item.fishingrod.ItemBetterFishingRod;

public class ClientRegistryManager {
   public ClientRegistryManager() {
   }

   public static class RegistryHandler {
      public RegistryHandler() {
      }

      public static void register(EventBus bus) {
         bus.register(ModelRegistryEvent.class, RegistryHandler::onRegisterModel);
         bus.register(ModelBakeEvent.class, RegistryHandler::onModelBake);
      }

      public static void onRegisterModel(ModelRegistryEvent e) {
         BlockManager.registerModels();
         ItemManager.registerModels();
         ItemReel[] reelList = ItemManager.reelAttachmentList();
         ItemBobber[] bobberList = ItemManager.bobberAttachmentList();
         ItemHook[] hookList = ItemManager.hookAttachmentList();

         for(ItemBetterFishingRod rod : ItemManager.rodList()) {
            ModelLoader.setCustomModelResourceLocation(rod, 0, new ModelResourceLocation("fishingmadebetter:rod/" + rod.getRegistryName().getResourcePath() + "/rod", "inventory"));
            ModelLoader.setCustomModelResourceLocation(rod, 1, new ModelResourceLocation("fishingmadebetter:rod/" + rod.getRegistryName().getResourcePath() + "/rod_cast", "inventory"));
         }

         for(int i = 0; i < reelList.length; ++i) {
            ItemReel reel = reelList[i];
            if (reel != null) {
               ModelLoader.setCustomModelResourceLocation(reel, 0, new ModelResourceLocation("fishingmadebetter:attachment/reel/" + reel.getRegistryName().getResourcePath(), "inventory"));
               ModelLoader.setCustomModelResourceLocation(reel, 1, new ModelResourceLocation("fishingmadebetter:mesh/reel/" + reel.getRegistryName().getResourcePath(), "inventory"));
            }
         }

         for(int i = 0; i < bobberList.length; ++i) {
            ItemBobber bobber = bobberList[i];
            if (bobber != null) {
               ModelLoader.setCustomModelResourceLocation(bobber, 0, new ModelResourceLocation("fishingmadebetter:attachment/bobber/" + bobber.getRegistryName().getResourcePath(), "inventory"));
               ModelLoader.setCustomModelResourceLocation(bobber, 1, new ModelResourceLocation("fishingmadebetter:mesh/bobber/" + bobber.getRegistryName().getResourcePath(), "inventory"));
            }
         }

         for(int i = 0; i < hookList.length; ++i) {
            ItemHook hook = hookList[i];
            if (hook != null) {
               ModelLoader.setCustomModelResourceLocation(hook, 0, new ModelResourceLocation("fishingmadebetter:attachment/hook/" + hook.getRegistryName().getResourcePath(), "inventory"));
               ModelLoader.setCustomModelResourceLocation(hook, 1, new ModelResourceLocation("fishingmadebetter:mesh/hook/" + hook.getRegistryName().getResourcePath(), "inventory"));
            }
         }

      }

      public static void onModelBake(ModelBakeEvent e) {
         ItemBetterFishingRod[] rodList = ItemManager.rodList();
         ItemReel[] reelList = ItemManager.reelAttachmentList();
         ItemBobber[] bobberList = ItemManager.bobberAttachmentList();
         ItemHook[] hookList = ItemManager.hookAttachmentList();
         IBakedModel[][] models = new IBakedModel[][]{new IBakedModel[reelList.length], new IBakedModel[bobberList.length], new IBakedModel[hookList.length]};

         for(int i = 0; i < reelList.length; ++i) {
            ItemReel reel = reelList[i];
            if (reel != null) {
               models[0][i] = (IBakedModel)e.getModelRegistry().getObject(new ModelResourceLocation("fishingmadebetter:mesh/reel/" + reel.getRegistryName().getResourcePath(), "inventory"));
            }
         }

         for(int i = 0; i < bobberList.length; ++i) {
            ItemBobber bobber = bobberList[i];
            if (bobber != null) {
               models[1][i] = (IBakedModel)e.getModelRegistry().getObject(new ModelResourceLocation("fishingmadebetter:mesh/bobber/" + bobber.getRegistryName().getResourcePath(), "inventory"));
            }
         }

         for(int i = 0; i < hookList.length; ++i) {
            ItemHook hook = hookList[i];
            if (hook != null) {
               models[2][i] = (IBakedModel)e.getModelRegistry().getObject(new ModelResourceLocation("fishingmadebetter:mesh/hook/" + hook.getRegistryName().getResourcePath(), "inventory"));
            }
         }

         for(ItemBetterFishingRod rod : rodList) {
            ModelResourceLocation mrl = new ModelResourceLocation("fishingmadebetter:rod/" + rod.getRegistryName().getResourcePath() + "/rod", "inventory");
            ModelResourceLocation mrlCast = new ModelResourceLocation("fishingmadebetter:rod/" + rod.getRegistryName().getResourcePath() + "/rod_cast", "inventory");
            IBakedModel mainModel = (IBakedModel)e.getModelRegistry().getObject(mrl);
            IBakedModel mainModelCast = (IBakedModel)e.getModelRegistry().getObject(mrlCast);
            e.getModelRegistry().putObject(mrl, new BakedModelRod(mainModel, mainModelCast, models));
         }

      }
   }
}
