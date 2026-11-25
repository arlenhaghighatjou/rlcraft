package stanhebben.zenscript.type.casting;

import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.type.ZenType;

public interface ICastingRule {
   void compile(IEnvironmentMethod var1);

   ZenType getInputType();

   ZenType getResultingType();
}
