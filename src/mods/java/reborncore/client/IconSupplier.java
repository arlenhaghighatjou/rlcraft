package reborncore.client;

import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.renderer.texture.TextureMap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.client.event.TextureStitchEvent.Pre;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class IconSupplier {
   public static String armour_head_name = "reborncore:gui/slot_sprites/armour_head";
   @SideOnly(Side.CLIENT)
   public static TextureAtlasSprite armour_head;
   public static String armour_chest_name = "reborncore:gui/slot_sprites/armour_chest";
   @SideOnly(Side.CLIENT)
   public static TextureAtlasSprite armour_chest;
   public static String armour_legs_name = "reborncore:gui/slot_sprites/armour_legs";
   @SideOnly(Side.CLIENT)
   public static TextureAtlasSprite armour_legs;
   public static String armour_feet_name = "reborncore:gui/slot_sprites/armour_feet";
   @SideOnly(Side.CLIENT)
   public static TextureAtlasSprite armour_feet;

   public IconSupplier() {
   }

   @SideOnly(Side.CLIENT)
   @SubscribeEvent
   public void preTextureStitch(Pre event) {
      TextureMap map = event.getMap();
      armour_head = map.registerSprite(new ResourceLocation(armour_head_name));
      armour_chest = map.registerSprite(new ResourceLocation(armour_chest_name));
      armour_legs = map.registerSprite(new ResourceLocation(armour_legs_name));
      armour_feet = map.registerSprite(new ResourceLocation(armour_feet_name));
   }
}
