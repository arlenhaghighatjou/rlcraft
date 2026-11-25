package org.orecruncher.dsurround.client.sound;

import net.minecraft.util.ResourceLocation;
import net.minecraft.util.SoundCategory;
import net.minecraft.util.SoundEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;
import org.orecruncher.dsurround.registry.RegistryManager;

@SideOnly(Side.CLIENT)
public final class Sounds {
   public static final SoundEffect CRAFTING = (new SoundEffect.Builder("crafting", SoundCategory.PLAYERS)).build();
   public static final SoundEffect SWORD_EQUIP = (new SoundEffect.Builder("sword.equip", SoundCategory.PLAYERS)).setVolume(0.5F).build();
   public static final SoundEffect SWORD_SWING = (new SoundEffect.Builder("sword.swing", SoundCategory.PLAYERS)).build();
   public static final SoundEffect AXE_EQUIP = (new SoundEffect.Builder("blunt.equip", SoundCategory.PLAYERS)).setVolume(0.35F).build();
   public static final SoundEffect AXE_SWING = (new SoundEffect.Builder("blunt.swing", SoundCategory.PLAYERS)).build();
   public static final SoundEffect BOW_EQUIP = (new SoundEffect.Builder("bow.equip", SoundCategory.PLAYERS)).setVolume(0.3F).build();
   public static final SoundEffect BOW_PULL = (new SoundEffect.Builder("bow.pull", SoundCategory.PLAYERS)).build();
   public static final SoundEffect TOOL_EQUIP = (new SoundEffect.Builder("tool.equip", SoundCategory.PLAYERS)).setVolume(0.3F).build();
   public static final SoundEffect TOOL_SWING = (new SoundEffect.Builder("tool.swing", SoundCategory.PLAYERS)).build();
   public static final SoundEffect UTILITY_EQUIP = (new SoundEffect.Builder("utility.equip", SoundCategory.PLAYERS)).setVolume(0.35F).build();
   public static final SoundEffect FOOD_EQUIP = (new SoundEffect.Builder("food.equip", SoundCategory.PLAYERS)).setVolume(0.35F).build();
   public static final SoundEffect SHIELD_EQUIP = (new SoundEffect.Builder("shield.equip", SoundCategory.PLAYERS)).setVolume(0.3F).build();
   public static final SoundEffect SHIELD_USE = (new SoundEffect.Builder("shield.use", SoundCategory.PLAYERS)).setVolume(0.3F).build();
   public static final SoundEffect LEATHER_ARMOR_EQUIP = (new SoundEffect.Builder("armor.light_walk", SoundCategory.PLAYERS)).build();
   public static final SoundEffect CHAIN_ARMOR_EQUIP = (new SoundEffect.Builder("armor.medium_walk", SoundCategory.PLAYERS)).build();
   public static final SoundEffect PLATE_ARMOR_EQUIP = (new SoundEffect.Builder("armor.heavy_walk", SoundCategory.PLAYERS)).build();
   public static final SoundEffect CRYSTAL_ARMOR_EQUIP = (new SoundEffect.Builder("armor.crystal_walk", SoundCategory.PLAYERS)).build();
   public static final SoundEffect BOOK_EQUIP = (new SoundEffect.Builder("pageflip", SoundCategory.PLAYERS)).build();
   public static final SoundEffect POTION_EQUIP = (new SoundEffect.Builder("potion.equip", SoundCategory.PLAYERS)).build();
   public static final SoundEffect THUNDER = (new SoundEffect.Builder("thunder", SoundCategory.WEATHER)).setVolume(10000.0F).build();
   public static final SoundEffect RAINFALL = (new SoundEffect.Builder("rain", SoundCategory.WEATHER)).build();
   public static final SoundEffect WATER_DROP = (new SoundEffect.Builder("waterdrops", SoundCategory.AMBIENT)).build();
   public static final SoundEffect WATER_DRIP = (new SoundEffect.Builder("waterdrips", SoundCategory.AMBIENT)).build();
   public static final SoundEffect STEAM_HISS = (new SoundEffect.Builder(new ResourceLocation("block.fire.extinguish"), SoundCategory.AMBIENT)).setVolume(0.1F).setPitch(1.0F).build();
   public static final SoundEffect FIRE = (new SoundEffect.Builder(new ResourceLocation("minecraft:block.fire.ambient"), SoundCategory.BLOCKS)).build();
   public static final SoundEffect WATERFALL0 = (new SoundEffect.Builder("waterfall.0", SoundCategory.AMBIENT)).build();
   public static final SoundEffect WATERFALL1 = (new SoundEffect.Builder("waterfall.1", SoundCategory.AMBIENT)).build();
   public static final SoundEffect WATERFALL2 = (new SoundEffect.Builder("waterfall.2", SoundCategory.AMBIENT)).build();
   public static final SoundEffect WATERFALL3 = (new SoundEffect.Builder("waterfall.3", SoundCategory.AMBIENT)).build();
   public static final SoundEffect WATERFALL4 = (new SoundEffect.Builder("waterfall.4", SoundCategory.AMBIENT)).build();
   public static final SoundEffect WATERFALL5 = (new SoundEffect.Builder("waterfall.5", SoundCategory.AMBIENT)).build();
   public static final SoundEvent RAIN = RegistryManager.SOUND.getSound(new ResourceLocation("dsurround", "rain"));
   public static final SoundEvent DUST = RegistryManager.SOUND.getSound(new ResourceLocation("dsurround", "dust"));
   public static final SoundEffect AMBIENT_CAVE = (new SoundEffect.Builder(new ResourceLocation("minecraft:ambient.cave"), SoundCategory.AMBIENT)).build();

   private Sounds() {
   }
}
