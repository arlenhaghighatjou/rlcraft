package ivorius.reccomplex.utils.accessor;

import java.lang.reflect.Field;
import net.minecraft.command.CommandBase;
import net.minecraft.command.ICommandListener;
import net.minecraftforge.fml.relauncher.ReflectionHelper;

public class RCAccessorCommandBase {
   private static Field commandAdmin;

   public RCAccessorCommandBase() {
   }

   private static void initializeUniqueID() {
      if (commandAdmin == null) {
         commandAdmin = ReflectionHelper.findField(CommandBase.class, "commandListener", "commandListener");
      }

   }

   public static ICommandListener getCommandAdmin() {
      initializeUniqueID();

      try {
         return (ICommandListener)commandAdmin.get((Object)null);
      } catch (IllegalAccessException var1) {
         var1.printStackTrace();
         return null;
      }
   }
}
