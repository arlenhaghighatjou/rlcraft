package codersafterdark.reskillable.api.requirement.logic;

import codersafterdark.reskillable.api.ReskillableAPI;
import codersafterdark.reskillable.api.requirement.Requirement;
import codersafterdark.reskillable.api.requirement.RequirementComparision;
import codersafterdark.reskillable.api.requirement.RequirementException;
import codersafterdark.reskillable.api.requirement.RequirementRegistry;
import codersafterdark.reskillable.api.requirement.UnobtainableRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.ANDRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.NANDRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.NORRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.NOTRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.ORRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.XNORRequirement;
import codersafterdark.reskillable.api.requirement.logic.impl.XORRequirement;

public class LogicParser {
   public static final FalseRequirement FALSE = new FalseRequirement();
   public static final TrueRequirement TRUE = new TrueRequirement();

   public LogicParser() {
   }

   public static Requirement parseNOT(String input) {
      return input != null && !input.isEmpty() ? parseNOT(ReskillableAPI.getInstance().getRequirementRegistry().getRequirement(input)) : null;
   }

   private static Requirement parseNOT(Requirement requirement) {
      if (requirement == null) {
         return null;
      } else if (!(requirement instanceof FalseRequirement) && !(requirement instanceof UnobtainableRequirement)) {
         if (requirement instanceof TrueRequirement) {
            return FALSE;
         } else if (requirement instanceof NOTRequirement) {
            return ((NOTRequirement)requirement).getRequirement();
         } else {
            if (requirement instanceof DoubleRequirement) {
               DoubleRequirement doubleRequirement = (DoubleRequirement)requirement;
               if (requirement instanceof ANDRequirement) {
                  return new NANDRequirement(doubleRequirement.getLeft(), doubleRequirement.getRight());
               }

               if (requirement instanceof NANDRequirement) {
                  return new ANDRequirement(doubleRequirement.getLeft(), doubleRequirement.getRight());
               }

               if (requirement instanceof ORRequirement) {
                  return new NORRequirement(doubleRequirement.getLeft(), doubleRequirement.getRight());
               }

               if (requirement instanceof NORRequirement) {
                  return new ORRequirement(doubleRequirement.getLeft(), doubleRequirement.getRight());
               }

               if (requirement instanceof XORRequirement) {
                  return new XNORRequirement(doubleRequirement.getLeft(), doubleRequirement.getRight());
               }

               if (requirement instanceof XNORRequirement) {
                  return new XORRequirement(doubleRequirement.getLeft(), doubleRequirement.getRight());
               }
            }

            return new NOTRequirement(requirement);
         }
      } else {
         return TRUE;
      }
   }

   public static Requirement parseAND(String input) throws RequirementException {
      LogicParser.RequirementPair subRequirements = getSubRequirements(input);
      Requirement left = subRequirements.getLeft();
      Requirement right = subRequirements.getRight();
      if (!(left instanceof FalseRequirement) && !(right instanceof FalseRequirement)) {
         if (!(left instanceof UnobtainableRequirement) && !(right instanceof UnobtainableRequirement)) {
            if (left instanceof TrueRequirement) {
               return right;
            } else if (right instanceof TrueRequirement) {
               return left;
            } else {
               RequirementComparision matches = left.matches(right);
               if (!matches.equals(RequirementComparision.EQUAL_TO) && !matches.equals(RequirementComparision.GREATER_THAN)) {
                  return (Requirement)(matches.equals(RequirementComparision.LESS_THAN) ? right : new ANDRequirement(left, right));
               } else {
                  return left;
               }
            }
         } else {
            return new UnobtainableRequirement();
         }
      } else {
         return FALSE;
      }
   }

   public static Requirement parseNAND(String input) throws RequirementException {
      LogicParser.RequirementPair subRequirements = getSubRequirements(input);
      Requirement left = subRequirements.getLeft();
      Requirement right = subRequirements.getRight();
      if (!(left instanceof FalseRequirement) && !(right instanceof FalseRequirement) && !(left instanceof UnobtainableRequirement) && !(right instanceof UnobtainableRequirement)) {
         if (left instanceof TrueRequirement) {
            return parseNOT(right);
         } else if (right instanceof TrueRequirement) {
            return parseNOT(left);
         } else {
            return (Requirement)(left.matches(right).equals(RequirementComparision.EQUAL_TO) ? parseNOT(left) : new NANDRequirement(left, right));
         }
      } else {
         return TRUE;
      }
   }

   public static Requirement parseOR(String input) throws RequirementException {
      LogicParser.RequirementPair subRequirements = getSubRequirements(input);
      Requirement left = subRequirements.getLeft();
      Requirement right = subRequirements.getRight();
      if (!(left instanceof TrueRequirement) && !(right instanceof TrueRequirement)) {
         if (left instanceof FalseRequirement) {
            return right;
         } else if (right instanceof FalseRequirement) {
            return left;
         } else {
            RequirementComparision matches = left.matches(right);
            if (!matches.equals(RequirementComparision.EQUAL_TO) && !matches.equals(RequirementComparision.LESS_THAN)) {
               if (matches.equals(RequirementComparision.GREATER_THAN)) {
                  return right;
               } else {
                  if (left instanceof NOTRequirement) {
                     if (!(right instanceof NOTRequirement) && ((NOTRequirement)left).getRequirement().matches(right).equals(RequirementComparision.EQUAL_TO)) {
                        return TRUE;
                     }
                  } else if (right instanceof NOTRequirement && left.matches(((NOTRequirement)right).getRequirement()).equals(RequirementComparision.EQUAL_TO)) {
                     return TRUE;
                  }

                  return new ORRequirement(left, right);
               }
            } else {
               return left;
            }
         }
      } else {
         return TRUE;
      }
   }

   public static Requirement parseNOR(String input) throws RequirementException {
      LogicParser.RequirementPair subRequirements = getSubRequirements(input);
      Requirement left = subRequirements.getLeft();
      Requirement right = subRequirements.getRight();
      if (left instanceof TrueRequirement && right instanceof TrueRequirement) {
         return FALSE;
      } else if (left instanceof FalseRequirement) {
         return parseNOT(right);
      } else if (right instanceof FalseRequirement) {
         return parseNOT(left);
      } else {
         return (Requirement)(left.matches(right).equals(RequirementComparision.EQUAL_TO) ? parseNOT(left) : new NORRequirement(left, right));
      }
   }

   public static Requirement parseXOR(String input) throws RequirementException {
      LogicParser.RequirementPair subRequirements = getSubRequirements(input);
      Requirement left = subRequirements.getLeft();
      Requirement right = subRequirements.getRight();
      if (left instanceof TrueRequirement) {
         return parseNOT(right);
      } else if (left instanceof FalseRequirement) {
         return right;
      } else if (right instanceof TrueRequirement) {
         return parseNOT(left);
      } else if (right instanceof FalseRequirement) {
         return left;
      } else {
         return (Requirement)(left.matches(right).equals(RequirementComparision.EQUAL_TO) ? FALSE : new XORRequirement(left, right));
      }
   }

   public static Requirement parseXNOR(String input) throws RequirementException {
      LogicParser.RequirementPair subRequirements = getSubRequirements(input);
      Requirement left = subRequirements.getLeft();
      Requirement right = subRequirements.getRight();
      if (left instanceof TrueRequirement) {
         return right;
      } else if (left instanceof FalseRequirement) {
         return parseNOT(right);
      } else if (right instanceof TrueRequirement) {
         return left;
      } else if (right instanceof FalseRequirement) {
         return parseNOT(left);
      } else {
         return (Requirement)(left.matches(right).equals(RequirementComparision.EQUAL_TO) ? TRUE : new XNORRequirement(left, right));
      }
   }

   private static LogicParser.RequirementPair getSubRequirements(String input) throws RequirementException {
      if (input != null && input.length() >= 5 && input.startsWith("[") && input.endsWith("]")) {
         String first = "";
         int count = 1;
         char lastChar = '[';
         int secondStart = 4;
         int closeBrackets = 0;

         for(int i = 1; i < input.length(); ++i) {
            char c = input.charAt(i);
            if (lastChar == ']') {
               if (c == ']') {
                  ++closeBrackets;
               } else {
                  if (c == '~') {
                     count = count - 1 - closeBrackets;
                  }

                  closeBrackets = 0;
               }
            } else {
               if ((lastChar == '|' || lastChar == '~') && c == '[') {
                  ++count;
               }

               closeBrackets = 0;
            }

            if (count == 0) {
               if (!first.isEmpty()) {
                  throw new RequirementException("Something went wrong, double check brackets.");
               }

               first = input.substring(1, i - 1);
               secondStart = i + 2;
            }

            lastChar = c;
         }

         count = count - 1 - closeBrackets;
         if (count != 0) {
            throw new RequirementException("Mismatched brackets.");
         } else {
            RequirementRegistry registry = ReskillableAPI.getInstance().getRequirementRegistry();
            Requirement left = registry.getRequirement(first);
            if (left == null) {
               throw new RequirementException("Invalid left-hand requirement '" + first + "'.");
            } else {
               String second = input.substring(secondStart, input.length() - 1);
               Requirement right = registry.getRequirement(second);
               if (right == null) {
                  throw new RequirementException("Invalid right-hand requirement '" + second + "'.");
               } else {
                  return new LogicParser.RequirementPair(left, right);
               }
            }
         }
      } else {
         throw new RequirementException("Invalid format for double requirement.");
      }
   }

   private static class RequirementPair {
      private Requirement left;
      private Requirement right;

      private RequirementPair(Requirement left, Requirement right) {
         this.left = left;
         this.right = right;
      }

      private Requirement getLeft() {
         return this.left;
      }

      private Requirement getRight() {
         return this.right;
      }
   }
}
