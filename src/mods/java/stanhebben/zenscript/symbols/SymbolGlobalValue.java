package stanhebben.zenscript.symbols;

import org.objectweb.asm.ClassVisitor;
import stanhebben.zenscript.compiler.IEnvironmentMethod;
import stanhebben.zenscript.definitions.ParsedGlobalValue;
import stanhebben.zenscript.expression.partial.IPartialExpression;
import stanhebben.zenscript.expression.partial.PartialGlobalValue;
import stanhebben.zenscript.type.ZenType;
import stanhebben.zenscript.util.ZenPosition;

public class SymbolGlobalValue implements IZenSymbol {
   private final ParsedGlobalValue value;
   private final PartialGlobalValue instance;

   public SymbolGlobalValue(ParsedGlobalValue value, IEnvironmentMethod environmentClass) {
      this.value = value;
      this.instance = new PartialGlobalValue(this);
      this.initField(environmentClass.getClassOutput());
      this.compileGlobal(environmentClass);
   }

   public IPartialExpression instance(ZenPosition position) {
      return this.instance;
   }

   public String getName() {
      return this.value.getName();
   }

   public ZenType getType() {
      return this.value.getType();
   }

   public ParsedGlobalValue getValue() {
      return this.value;
   }

   public String getOwner() {
      return this.value.getOwner();
   }

   public String getASMDescriptor() {
      return this.getType().toASMType().getDescriptor();
   }

   public ZenPosition getPosition() {
      return this.value.getPosition();
   }

   private void initField(ClassVisitor visitor) {
      visitor.visitField(25, this.getName(), this.getASMDescriptor(), (String)null, (Object)null).visitEnd();
   }

   private void compileGlobal(IEnvironmentMethod clinitMethodEnvironment) {
      this.value.getValue().compile(clinitMethodEnvironment, this.getType()).eval(clinitMethodEnvironment).cast(this.getPosition(), clinitMethodEnvironment, this.getType()).compile(true, clinitMethodEnvironment);
      clinitMethodEnvironment.getOutput().putStaticField(this.getOwner(), this.getName(), this.getASMDescriptor());
   }
}
