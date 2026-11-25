package net.mcft.copy.backpacks;

import java.util.ArrayList;
import java.util.Map.Entry;
import net.mcft.copy.backpacks.api.BackpackRegistry;
import net.mcft.copy.backpacks.block.BlockBackpack;
import net.mcft.copy.backpacks.block.entity.TileEntityBackpack;
import net.mcft.copy.backpacks.config.EntityListConfig;
import net.mcft.copy.backpacks.item.ItemBackpack;
import net.mcft.copy.backpacks.item.recipe.RecipeDyeableItem;
import net.minecraft.block.Block;
import net.minecraft.item.Item;
import net.minecraft.util.ResourceLocation;
import net.minecraft.world.storage.loot.LootTableList;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.registry.ForgeRegistries;
import net.minecraftforge.fml.common.registry.GameRegistry;
import org.apache.logging.log4j.Level;

public class BackpacksContent {
   public static ItemBackpack BACKPACK;

   public BackpacksContent() {
      BACKPACK = new ItemBackpack();
   }

   @SubscribeEvent
   public void onRegisterBlocks(Register<Block> event) {
      event.getRegistry().register((new BlockBackpack()).setRegistryName("wearablebackpacks", "backpack"));
      GameRegistry.registerTileEntity(TileEntityBackpack.class, new ResourceLocation("wearablebackpacks", "backpack"));
   }

   @SubscribeEvent
   public void onRegisterItems(Register<Item> event) {
      event.getRegistry().register(BACKPACK.setRegistryName("wearablebackpacks", "backpack"));
      String backpack = BACKPACK.getRegistryName().toString();
      String idDefault = "wearablebackpacks:default";
      String idColored = "wearablebackpacks:colored";
      if (EntityListConfig.getEntityList() != null && EntityListConfig.getBackpackList() != null) {
         for(Entry<String, BackpackRegistry.RenderOptions> entry : EntityListConfig.getEntityList().entrySet()) {
            BackpackRegistry.registerEntity((String)entry.getKey(), (BackpackRegistry.RenderOptions)entry.getValue());
         }

         for(Entry<String, ArrayList<String[]>> entry : EntityListConfig.getBackpackList().entrySet()) {
            for(String[] subEntry : (ArrayList)entry.getValue()) {
               try {
                  boolean colored = Boolean.parseBoolean(subEntry[0]);
                  int chance = Integer.parseInt(subEntry[1]);
                  String loottable = subEntry[2];
                  BackpackRegistry.registerBackpack((String)entry.getKey(), colored ? idColored : idDefault, backpack, chance, loottable, colored ? BackpackRegistry.ColorRange.DEFAULT : null);
               } catch (Exception var12) {
                  WearableBackpacks.LOG.log(Level.ERROR, "wearablebackpacks: Failed to parse entity backpack list entry: " + var12);
                  break;
               }
            }
         }
      }

      LootTableList.register(ItemBackpack.LOOT_TABLE);
   }

   public void registerRecipes() {
      ForgeRegistries.RECIPES.register((new RecipeDyeableItem()).setRegistryName("wearablebackpacks", "dyeable_item"));
   }
}
