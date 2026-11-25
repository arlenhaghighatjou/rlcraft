package stanhebben.zenscript.compiler;

import java.lang.reflect.Type;
import stanhebben.zenscript.type.ZenType;

public interface ITypeRegistry {
   ZenType getType(Type var1);
}
