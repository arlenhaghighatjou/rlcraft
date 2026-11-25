package crafttweaker.api.dispenser;

import crafttweaker.annotations.ZenRegister;
import stanhebben.zenscript.annotations.ZenClass;

@ZenRegister
@ZenClass("crafttweaker.dispenser.IDispenserSoundFunction")
public interface IDispenserSoundFunction {
   DispenserSound apply(IBlockSource var1);
}
