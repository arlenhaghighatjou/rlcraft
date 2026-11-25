package codersafterdark.compatskills.common.compats.immersiveengineering;

import blusunrize.immersiveengineering.api.MultiblockHandler;
import blusunrize.immersiveengineering.api.MultiblockHandler.IMultiblock;
import codersafterdark.compatskills.CompatSkills;
import codersafterdark.compatskills.utils.CompatModuleBase;
import codersafterdark.compatskills.utils.multiblock.MultiBlockCommand;
import codersafterdark.compatskills.utils.multiblock.MultiBlockGate;
import codersafterdark.reskillable.api.data.RequirementHolder;
import codersafterdark.reskillable.base.LevelLockHandler;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;
import net.minecraftforge.common.MinecraftForge;

public class IECompatHandler extends CompatModuleBase {
   public static boolean ENABLED;
   private static boolean registered;

   public IECompatHandler() {
   }

   public static void addIELock(MultiBlockGate key, RequirementHolder holder) {
      if (!registered) {
         MinecraftForge.EVENT_BUS.register(new IEMultiBlockHandler());
         registered = true;
      }

      LevelLockHandler.addLockByKey(key, holder);
   }

   public void preInit() {
      ENABLED = true;
   }

   public void loadComplete() {
      if (CompatSkills.craftweakerLoaded) {
         CompatSkills.registerCommand(new MultiBlockCommand("ie") {
            public List<String> getMultiBlockNames() {
               return (List)MultiblockHandler.getMultiblocks().stream().map(IMultiblock::getUniqueName).collect(Collectors.toList());
            }
         });
      }

   }
}
