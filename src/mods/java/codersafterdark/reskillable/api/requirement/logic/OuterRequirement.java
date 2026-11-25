package codersafterdark.reskillable.api.requirement.logic;

import codersafterdark.reskillable.api.requirement.Requirement;
import java.util.List;
import javax.annotation.Nonnull;

public interface OuterRequirement {
   @Nonnull
   List<Class<? extends Requirement>> getInternalTypes();
}
