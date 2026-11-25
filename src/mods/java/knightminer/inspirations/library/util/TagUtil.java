package knightminer.inspirations.library.util;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NBTTagCompound;
import net.minecraft.util.math.BlockPos;

public final class TagUtil {
   private TagUtil() {
   }

   public static NBTTagCompound getTagSafe(ItemStack stack) {
      return stack != null && stack.getItem() != null && stack.hasTagCompound() ? stack.getTagCompound() : new NBTTagCompound();
   }

   public static NBTTagCompound getTagSafe(NBTTagCompound tag, String key) {
      return tag == null ? new NBTTagCompound() : tag.getCompoundTag(key);
   }

   public static NBTTagCompound writePos(BlockPos pos) {
      NBTTagCompound tag = new NBTTagCompound();
      if (pos != null) {
         tag.setInteger("X", pos.getX());
         tag.setInteger("Y", pos.getY());
         tag.setInteger("Z", pos.getZ());
      }

      return tag;
   }

   public static BlockPos readPos(NBTTagCompound tag) {
      return tag != null && tag.hasKey("X", 99) && tag.hasKey("Y", 99) && tag.hasKey("Z", 99) ? new BlockPos(tag.getInteger("X"), tag.getInteger("Y"), tag.getInteger("Z")) : null;
   }

   public static JsonElement getElement(JsonObject json, String memberName) {
      if (json.has(memberName)) {
         return json.get(memberName);
      } else {
         throw new JsonSyntaxException("Missing " + memberName + " from the current json, Invalid JSON!");
      }
   }
}
