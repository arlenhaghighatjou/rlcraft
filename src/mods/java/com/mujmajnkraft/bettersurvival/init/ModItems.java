package com.mujmajnkraft.bettersurvival.init;

import com.mujmajnkraft.bettersurvival.BetterSurvival;
import com.mujmajnkraft.bettersurvival.config.ForgeConfigHandler;
import com.mujmajnkraft.bettersurvival.integration.InFCompat;
import com.mujmajnkraft.bettersurvival.items.ItemBattleAxe;
import com.mujmajnkraft.bettersurvival.items.ItemCrossbow;
import com.mujmajnkraft.bettersurvival.items.ItemCustomShield;
import com.mujmajnkraft.bettersurvival.items.ItemDagger;
import com.mujmajnkraft.bettersurvival.items.ItemHammer;
import com.mujmajnkraft.bettersurvival.items.ItemNunchaku;
import com.mujmajnkraft.bettersurvival.items.ItemSpear;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.List;
import net.minecraft.client.Minecraft;
import net.minecraft.client.renderer.block.model.ModelResourceLocation;
import net.minecraft.item.Item;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Item.ToolMaterial;
import net.minecraftforge.common.util.EnumHelper;
import net.minecraftforge.event.RegistryEvent.Register;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistryEntry;

public class ModItems {
   public static ToolMaterial COPPER;
   public static ToolMaterial BRONZE;
   public static ToolMaterial INVAR;
   public static ToolMaterial SILVER;
   public static ToolMaterial ELECTRUM;
   public static ToolMaterial ALUMINIUM;
   public static ToolMaterial STEEL;
   public static ToolMaterial SIGNALUM;
   public static ToolMaterial LUMIUM;
   public static ToolMaterial ENDERIUM;
   public static final Item crossbow = new ItemCrossbow();
   public static final Item smallshield = new ItemCustomShield(0.5F, 1);
   public static final Item bigshield = new ItemCustomShield(0.8F, 3);
   private static List<ToolMaterial> materials;
   private static List<Item> items = new ArrayList();

   public ModItems() {
   }

   public static void setRepairMaterials() {
      if (BetterSurvival.isIafLoaded) {
         if (Item.getByNameOrId("iceandfire:myrmex_desert_chitin") != null) {
            InFCompat.DESERT_CHITIN.setRepairItem(new ItemStack(Item.getByNameOrId("iceandfire:myrmex_desert_chitin")));
         }

         if (Item.getByNameOrId("iceandfire:myrmex_jungle_chitin") != null) {
            InFCompat.JUNGLE_CHITIN.setRepairItem(new ItemStack(Item.getByNameOrId("iceandfire:myrmex_jungle_chitin")));
         }
      }

   }

   @SubscribeEvent
   public void registerItems(Register<Item> event) {
      if (BetterSurvival.isIafLoaded) {
         materials.remove(SILVER);
         materials.addAll(Arrays.asList(InFCompat.SILVER, InFCompat.DRAGON_BONE, InFCompat.DRAGON_BONE_FLAMED, InFCompat.DRAGON_BONE_ICED, InFCompat.JUNGLE_CHITIN, InFCompat.DESERT_CHITIN));
      }

      crossbow.setRegistryName("mujmajnkraftsbettersurvival", "itemcrossbow");
      crossbow.setUnlocalizedName("crossbow");
      items.add(crossbow);
      smallshield.setRegistryName("mujmajnkraftsbettersurvival", "itemsmallshield");
      smallshield.setUnlocalizedName("smallshield");
      items.add(smallshield);
      bigshield.setRegistryName("mujmajnkraftsbettersurvival", "itembigshield");
      bigshield.setUnlocalizedName("bigshield");
      items.add(bigshield);

      for(ToolMaterial mat : materials) {
         items.add(new ItemHammer(mat));
         items.add(new ItemSpear(mat));
         items.add(new ItemDagger(mat));
         items.add(new ItemBattleAxe(mat));
         items.add(new ItemNunchaku(mat));
      }

      event.getRegistry().registerAll((IForgeRegistryEntry[])items.toArray(new Item[0]));
   }

   public static void registerRenders() {
      for(Item item : items) {
         registerRender(item);
      }

   }

   private static void registerRender(Item item) {
      Minecraft.getMinecraft().getRenderItem().getItemModelMesher().register(item, 0, new ModelResourceLocation(item.getRegistryName(), "inventory"));
   }

   static {
      COPPER = EnumHelper.addToolMaterial("Copper", ForgeConfigHandler.materials.copperStats[0].intValue(), ForgeConfigHandler.materials.copperStats[1].intValue(), ForgeConfigHandler.materials.copperStats[2].floatValue(), ForgeConfigHandler.materials.copperStats[3].floatValue(), ForgeConfigHandler.materials.copperStats[4].intValue());
      BRONZE = EnumHelper.addToolMaterial("Bronze", ForgeConfigHandler.materials.bronzeStats[0].intValue(), ForgeConfigHandler.materials.bronzeStats[1].intValue(), ForgeConfigHandler.materials.bronzeStats[2].floatValue(), ForgeConfigHandler.materials.bronzeStats[3].floatValue(), ForgeConfigHandler.materials.bronzeStats[4].intValue());
      INVAR = EnumHelper.addToolMaterial("Invar", ForgeConfigHandler.materials.invarStats[0].intValue(), ForgeConfigHandler.materials.invarStats[1].intValue(), ForgeConfigHandler.materials.invarStats[2].floatValue(), ForgeConfigHandler.materials.invarStats[3].floatValue(), ForgeConfigHandler.materials.invarStats[4].intValue());
      SILVER = EnumHelper.addToolMaterial("Silver", ForgeConfigHandler.materials.silverStats[0].intValue(), ForgeConfigHandler.materials.silverStats[1].intValue(), ForgeConfigHandler.materials.silverStats[2].floatValue(), ForgeConfigHandler.materials.silverStats[3].floatValue(), ForgeConfigHandler.materials.silverStats[4].intValue());
      ELECTRUM = EnumHelper.addToolMaterial("Electrum", ForgeConfigHandler.materials.electrumStats[0].intValue(), ForgeConfigHandler.materials.electrumStats[1].intValue(), ForgeConfigHandler.materials.electrumStats[2].floatValue(), ForgeConfigHandler.materials.electrumStats[3].floatValue(), ForgeConfigHandler.materials.electrumStats[4].intValue());
      ALUMINIUM = EnumHelper.addToolMaterial("Aluminium", ForgeConfigHandler.materials.aluminiumStats[0].intValue(), ForgeConfigHandler.materials.aluminiumStats[1].intValue(), ForgeConfigHandler.materials.aluminiumStats[2].floatValue(), ForgeConfigHandler.materials.aluminiumStats[3].floatValue(), ForgeConfigHandler.materials.aluminiumStats[4].intValue());
      STEEL = EnumHelper.addToolMaterial("Steel", ForgeConfigHandler.materials.steelStats[0].intValue(), ForgeConfigHandler.materials.steelStats[1].intValue(), ForgeConfigHandler.materials.steelStats[2].floatValue(), ForgeConfigHandler.materials.steelStats[3].floatValue(), ForgeConfigHandler.materials.steelStats[4].intValue());
      SIGNALUM = EnumHelper.addToolMaterial("Signalum", ForgeConfigHandler.materials.signalumStats[0].intValue(), ForgeConfigHandler.materials.signalumStats[1].intValue(), ForgeConfigHandler.materials.signalumStats[2].floatValue(), ForgeConfigHandler.materials.signalumStats[3].floatValue(), ForgeConfigHandler.materials.signalumStats[4].intValue());
      LUMIUM = EnumHelper.addToolMaterial("Lumium", ForgeConfigHandler.materials.lumiumStats[0].intValue(), ForgeConfigHandler.materials.lumiumStats[1].intValue(), ForgeConfigHandler.materials.lumiumStats[2].floatValue(), ForgeConfigHandler.materials.lumiumStats[3].floatValue(), ForgeConfigHandler.materials.lumiumStats[4].intValue());
      ENDERIUM = EnumHelper.addToolMaterial("Enderium", ForgeConfigHandler.materials.enderiumStats[0].intValue(), ForgeConfigHandler.materials.enderiumStats[1].intValue(), ForgeConfigHandler.materials.enderiumStats[2].floatValue(), ForgeConfigHandler.materials.enderiumStats[3].floatValue(), ForgeConfigHandler.materials.enderiumStats[4].intValue());
      materials = new ArrayList(EnumSet.of(COPPER, BRONZE, INVAR, SILVER, ELECTRUM, ALUMINIUM, STEEL, SIGNALUM, LUMIUM, ENDERIUM, ToolMaterial.WOOD, ToolMaterial.STONE, ToolMaterial.IRON, ToolMaterial.GOLD, ToolMaterial.DIAMOND));
   }
}
