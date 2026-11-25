package stanhebben.zenscript.compiler;

import org.objectweb.asm.ClassVisitor;

public interface IEnvironmentClass extends IEnvironmentGlobal {
   ClassVisitor getClassOutput();
}
