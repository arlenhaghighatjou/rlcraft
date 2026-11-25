package codersafterdark.reskillable.api.data;

public interface FuzzyLockKey extends LockKey {
   boolean fuzzyEquals(FuzzyLockKey var1);

   boolean isNotFuzzy();

   LockKey getNotFuzzy();
}
