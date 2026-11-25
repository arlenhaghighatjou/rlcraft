package pl.asie.patchy;

import java.util.List;

public abstract class TransformerHandler<T> {
   private final Patchy owner;

   public TransformerHandler(Patchy owner) {
      this.owner = owner;
   }

   public void add(TransformerFunction<T> function, String... names) {
      if (names.length == 0) {
         this.owner.registerGlobalTransformer(this.getType(), function);
      } else {
         for(String s : names) {
            this.owner.registerLocalTransformer(s, this.getType(), function);
         }
      }

   }

   protected abstract Class<T> getType();

   protected abstract byte[] process(byte[] var1, List<TransformerFunction<T>> var2);
}
