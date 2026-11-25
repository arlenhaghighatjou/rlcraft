package stanhebben.zenscript.type;

import org.objectweb.asm.Label;

public interface IZenIterator {
   void compileStart(int[] var1);

   void compilePreIterate(int[] var1, Label var2);

   void compilePostIterate(int[] var1, Label var2, Label var3);

   void compileEnd();

   ZenType getType(int var1);
}
