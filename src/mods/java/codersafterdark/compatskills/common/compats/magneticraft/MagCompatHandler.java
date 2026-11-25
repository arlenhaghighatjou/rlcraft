package codersafterdark.compatskills.common.compats.magneticraft;

import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CompatModuleBase;
import codersafterdark.compatskills.utils.multiblock.MultiBlockCommand;
import codersafterdark.compatskills.utils.multiblock.MultiBlockGate;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import com.cout970.magneticraft.api.MagneticraftApi;
import java.util.ArrayList;
import java.util.List;
import net.minecraftforge.common.MinecraftForge;

public class MagCompatHandler extends CompatModuleBase {
   public static boolean ENABLED;
   private static boolean registered;

   public MagCompatHandler() {
   }

   public static void addMagLock(MultiBlockGate key, RequirementHolder holder) {
      if (!registered) {
         MinecraftForge.EVENT_BUS.register(new MagMultiBlockHandler());
         registered = true;
      }

      LevelLockHandler.addLockByKey(key, holder);
   }

   public void preInit() {
      ENABLED = true;
   }

   public void loadComplete() {
      if (CompatSkills.craftweakerLoaded) {
         CompatSkills.registerCommand(new MultiBlockCommand("mag") {
            public List<String> getMultiBlockNames() {
               return new ArrayList(MagneticraftApi.getMultiblockManager().getRegisteredMultiblocks().keySet());
            }
         });
      }

   }
}
