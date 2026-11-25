package pl.asie.patchy.handlers;

import java.util.List;
import pl.asie.patchy.Patchy;
import pl.asie.patchy.TransformerFunction;
import pl.asie.patchy.TransformerHandler;

public class TransformerHandlerByteArray extends TransformerHandler<byte[]> {
   public TransformerHandlerByteArray(Patchy owner) {
      super(owner);
   }

   protected Class<byte[]> getType() {
      return byte[].class;
   }

   protected byte[] process(byte[] data, List<TransformerFunction<byte[]>> transformerFunctions) {
      for(TransformerFunction<byte[]> function : transformerFunctions) {
         data = (byte[])function.apply(data);
      }

      return data;
   }
}
