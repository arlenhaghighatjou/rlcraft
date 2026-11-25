package codersafterdark.reskillable.api.requirement;

@FunctionalInterface
public interface RequirementFunction<T, R> {
   R apply(T var1) throws RequirementException;
}
