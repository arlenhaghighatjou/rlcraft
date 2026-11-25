package com.teammetallurgy.aquaculture.proxy;

import com.teammetallurgy.aquaculture.client.CustomFishingHookRenderFactory;
import com.teammetallurgy.aquaculture.handlers.AquacultureEntitySpawnHandler;
import com.teammetallurgy.aquaculture.handlers.EntityCustomFishHook;
import com.teammetallurgy.aquaculture.items.AquacultureItems;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraftforge.client.model.ModelLoader;
import net.minecraftforge.fml.client.registry.RenderingRegistry;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void registerParticles() {
   }

   public void registerModelRenderers() {
      RenderingRegistry.registerEntityRenderingHandler(EntityCustomFishHook.class, new CustomFishingHookRenderFactory());
   }

   public void registerItemModels() {
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.woodenFishingRod, 0, new ModelResourceLocation("aquaculture:fishing_rod", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.woodenFishingRod, 1, new ModelResourceLocation("aquaculture:fishing_rod_cast", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.adminFishingRod, 0, new ModelResourceLocation("aquaculture:admin_fishing_rod", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.adminFishingRod, 1, new ModelResourceLocation("aquaculture:admin_fishing_rod_cast", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.ironFishingRod, 0, new ModelResourceLocation("aquaculture:iron_fishing_rod", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.ironFishingRod, 1, new ModelResourceLocation("aquaculture:iron_fishing_rod_cast", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.goldFishingRod, 0, new ModelResourceLocation("aquaculture:gold_fishing_rod", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.goldFishingRod, 1, new ModelResourceLocation("aquaculture:gold_fishing_rod_cast", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.diamondFishingRod, 0, new ModelResourceLocation("aquaculture:diamond_fishing_rod", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.diamondFishingRod, 1, new ModelResourceLocation("aquaculture:diamond_fishing_rod_cast", "inventory"));
      String[] food = new String[]{"seaweed", "algae", "whale_steak_raw", "fish_fillet_raw", "fish_fillet_cooked", "whale_steak_cooked", "whale_burger", "frog_legs_raw", "frog_legs_cooked", "turtle_soup", "sushi"};

      for(int i = 0; i < food.length; ++i) {
         ModelLoader.setCustomModelResourceLocation(AquacultureItems.metaFoodItem, i, new ModelResourceLocation("aquaculture:loot/" + food[i], "inventory"));
      }

      String[] loot = new String[]{"driftwood", "neptunium_ingot", "tin_can", "message_in_a_bottle", "box", "lockbox", "treasure_chest", "neptunes_bounty"};

      for(int i = 0; i < loot.length; ++i) {
         ModelLoader.setCustomModelResourceLocation(AquacultureItems.metaLootItem, i, new ModelResourceLocation("aquaculture:loot/" + loot[i], "inventory"));
      }

      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumPickaxe, 0, new ModelResourceLocation("aquaculture:neptunium_pickaxe", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumShovel, 0, new ModelResourceLocation("aquaculture:neptunium_shovel", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumAxe, 0, new ModelResourceLocation("aquaculture:neptunium_axe", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumHoe, 0, new ModelResourceLocation("aquaculture:neptunium_hoe", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumSword, 0, new ModelResourceLocation("aquaculture:neptunium_sword", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumHelmet, 0, new ModelResourceLocation("aquaculture:neptunium_helmet", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumPlate, 0, new ModelResourceLocation("aquaculture:neptunium_chestplate", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumLegs, 0, new ModelResourceLocation("aquaculture:neptunium_leggings", "inventory"));
      ModelLoader.setCustomModelResourceLocation(AquacultureItems.neptuniumBoots, 0, new ModelResourceLocation("aquaculture:neptunium_boots", "inventory"));
      String[] fish = new String[]{"bluegill", "perch", "gar", "bass", "muskellunge", "trout_brown", "catfish", "carp", "blowfish", "grouper_red", "salmon", "tuna", "swordfish", "shark", "whale", "squid", "jellyfish", "frog", "turtle", "leech", "pirahna", "electric_eel", "tambaqui", "arapaima", "cod", "pollock", "herring", "halibut", "salmon_pink", "trout_rainbow", "blackfish", "capitaine", "boulti", "bagrid", "syndontis", "shrooma_red", "shrooma_brown", "goldfish", "fish_bones"};

      for(int i = 0; i < fish.length; ++i) {
         ModelLoader.setCustomModelResourceLocation(AquacultureItems.fish, i, new ModelResourceLocation("aquaculture:fish/" + fish[i], "inventory"));
      }

   }

   public void registerEntities() {
      super.registerEntities();
      AquacultureEntitySpawnHandler.init();
   }
}
