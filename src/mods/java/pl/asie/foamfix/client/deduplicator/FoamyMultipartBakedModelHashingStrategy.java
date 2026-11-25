package pl.asie.foamfix.client.deduplicator;

import gnu.trove.strategy.HashingStrategy;
import java.util.Arrays;
import pl.asie.foamfix.client.FoamyMultipartBakedModel;

public class FoamyMultipartBakedModelHashingStrategy implements HashingStrategy<FoamyMultipartBakedModel> {
   public FoamyMultipartBakedModelHashingStrategy() {
   }

   public int computeHashCode(FoamyMultipartBakedModel object) {
      int hash = Arrays.hashCode(object.predicates);

      for(int i = 0; i < object.models.length; ++i) {
         hash = hash * 31 + System.identityHashCode(object.models[i]);
      }

      return hash;
   }

   public boolean equals(FoamyMultipartBakedModel o1, FoamyMultipartBakedModel o2) {
      if (!Arrays.equals(o1.predicates, o2.predicates)) {
         return false;
      } else if (o1.models.length != o2.models.length) {
         return false;
      } else {
         for(int i = 0; i < o1.models.length; ++i) {
            if (o1.models[i] != o2.models[i]) {
               return false;
            }
         }

         return true;
      }
   }
}
