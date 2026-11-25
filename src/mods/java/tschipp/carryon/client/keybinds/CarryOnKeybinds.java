package tschipp.carryon.client.keybinds;

import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public class CarryOnKeybinds {
   public static final String KEYBIND_KEY = "carryOnKeyPressed";
   public static KeyBinding carryKey;

   public CarryOnKeybinds() {
   }

   @SideOnly(Side.CLIENT)
   public static void init() {
      carryKey = new KeyBinding("key.carry.desc", 42, "key.carry.category");
      ClientRegistry.registerKeyBinding(carryKey);
   }

   public static boolean isKeyPressed(EntityPlayer player) {
      NBTTagCompound tag = player.getEntityData();
      return tag != null && tag.hasKey("carryOnKeyPressed") ? tag.getBoolean("carryOnKeyPressed") : false;
   }

   public static void setKeyPressed(EntityPlayer player, boolean pressed) {
      NBTTagCompound tag = player.getEntityData();
      tag.setBoolean("carryOnKeyPressed", pressed);
   }
}
