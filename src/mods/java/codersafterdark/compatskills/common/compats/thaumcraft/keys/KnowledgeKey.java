package codersafterdark.compatskills.common.compats.thaumcraft.keys;

import codersafterdark.reskillable.api.data.LockKey;
import java.util.Objects;
import thaumcraft.api.research.ResearchCategory;

public class KnowledgeKey implements LockKey {
   private final ResearchCategory category;
   private final String knowledgeType;

   public KnowledgeKey(ResearchCategory category, String knowledgeType) {
      this.category = category;
      this.knowledgeType = knowledgeType == null ? "" : knowledgeType;
   }

   public boolean equals(Object o) {
      if (o == this) {
         return true;
      } else if (!(o instanceof KnowledgeKey)) {
         return false;
      } else {
         KnowledgeKey other = (KnowledgeKey)o;
         if (this.category == null) {
            return other.category == null && this.knowledgeType.equals(other.knowledgeType);
         } else {
            return this.category.equals(other.category) && this.knowledgeType.equals(other.knowledgeType);
         }
      }
   }

   public int hashCode() {
      return Objects.hash(new Object[]{this.category, this.knowledgeType});
   }
}
