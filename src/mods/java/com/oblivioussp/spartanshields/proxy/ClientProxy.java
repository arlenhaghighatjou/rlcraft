package com.oblivioussp.spartanshields.proxy;

import com.oblivioussp.spartanshields.client.model.ModelShieldKite;
import com.oblivioussp.spartanshields.client.model.ModelShieldTower;
import com.oblivioussp.spartanshields.client.render.item.TileEntityItemStackRendererSS;
import com.oblivioussp.spartanshields.init.ItemRegistrySS;
import net.minecraft.client.model.ModelShield;
import net.minecraft.client.renderer.color.IItemColor;
import net.minecraft.item.Item;
import net.minecraft.item.ItemShield;
import net.minecraft.item.ItemStack;
import net.minecraftforge.fml.client.FMLClientHandler;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {
   public ClientProxy() {
   }

   public void preInit(FMLPreInitializationEvent ev) {
   }

   public void init(FMLInitializationEvent ev) {
      this.registerSimpleItemColourHandler(ItemRegistrySS.shieldFEEnderIO, 8454048);
      this.setShieldTEISR(ItemRegistrySS.shieldTowerWood, "shield_tower_wood", "S_WOOD", new ModelShield());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerStone, "shield_tower_stone", "S_STONE", new ModelShield());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerIron, "shield_tower_iron", "S_IRON", new ModelShieldKite());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerGold, "shield_tower_gold", "S_GOLD", new ModelShieldTower());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerDiamond, "shield_tower_diamond", "S_DIAMOND", new ModelShieldKite());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerObsidian, "shield_tower_obsidian", "S_OBSIDIAN", new ModelShield());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerBronze, "shield_tower_bronze", "S_BRONZE", new ModelShieldKite());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerSteel, "shield_tower_steel", "S_STEEL", new ModelShieldKite());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerCopper, "shield_tower_copper", "S_COPPER", new ModelShield());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerTin, "shield_tower_tin", "S_TIN", new ModelShield());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerSilver, "shield_tower_silver", "S_SILVER", new ModelShieldTower());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerInvar, "shield_tower_invar", "S_INVAR", new ModelShieldTower());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerPlatinum, "shield_tower_platinum", "S_PLATINUM", new ModelShieldKite());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerElectrum, "shield_tower_electrum", "S_ELECTRUM", new ModelShieldTower());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerNickel, "shield_tower_nickel", "S_NICKEL", new ModelShield());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerLead, "shield_tower_lead", "S_LEAD", new ModelShieldKite());
      this.setShieldTEISR(ItemRegistrySS.shieldTowerConstantan, "shield_tower_constantan", "S_CONSTANTAN", new ModelShieldTower());
   }

   private void registerSimpleItemColourHandler(Item item, final int colour) {
      if (item != null) {
         FMLClientHandler.instance().getClient().getItemColors().registerItemColorHandler(new IItemColor() {
            public int colorMultiplier(ItemStack stack, int tintIndex) {
               return tintIndex == 1 ? colour : 16777215;
            }
         }, new Item[]{item});
      }
   }

   private void setShieldTEISR(ItemShield shield, String textureName, String bannerID, ModelShield model) {
      if (shield != null) {
         shield.setTileEntityItemStackRenderer(new TileEntityItemStackRendererSS(shield, String.format("textures/entity/%s_nopattern.png", textureName), String.format("textures/entity/%s_pattern.png", textureName), bannerID, model));
      }
   }

   public void postInit(FMLPostInitializationEvent ev) {
   }
}
