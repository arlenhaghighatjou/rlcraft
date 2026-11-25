package ivorius.reccomplex.operation;

import ivorius.ivtoolkit.tools.NBTCompoundObject;
import net.minecraft.command.ICommandSender;
import net.minecraft.world.World;
import net.minecraft.world.WorldServer;
import net.minecraftforge.fml.relauncher.Side;
import net.minecraftforge.fml.relauncher.SideOnly;

public interface Operation extends NBTCompoundObject {
   void perform(WorldServer var1);

   default void update(World world, int ticks) {
   }

   default boolean checkDead(ICommandSender target) {
      return false;
   }

   @SideOnly(Side.CLIENT)
   void renderPreview(Operation.PreviewType var1, World var2, int var3, float var4);

   public static enum PreviewType {
      NONE("none"),
      BOUNDING_BOX("bounds"),
      SHAPE("shape");

      public final String key;

      private PreviewType(String key) {
         this.key = key;
      }

      public static String[] keys() {
         Operation.PreviewType[] values = values();
         String[] keys = new String[values.length];

         for(int i = 0; i < values.length; ++i) {
            keys[i] = values[i].key;
         }

         return keys;
      }

      public static Operation.PreviewType findOrDefault(String key, Operation.PreviewType defaultType) {
         Operation.PreviewType type = find(key);
         return type != null ? type : defaultType;
      }

      public static Operation.PreviewType find(String key) {
         for(Operation.PreviewType type : values()) {
            if (type.key.equals(key)) {
               return type;
            }
         }

         return null;
      }
   }
}
