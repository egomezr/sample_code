package org.dynamicloud.api.criteria;

/**
 * This is a builder to create conditions: AND, OR, LIKE, NOT LIKE, IN, NOT IN, EQUALS, GREATER THAN, GREATER EQUALS THAN
 * LESSER THAN, LESSER EQUALS THAN.
 *
 * @author Eleazar Gomez
 * @version 1.0.0
 * @since 8/22/15
 **/
public class Conditions {

    public static final Condition ROOT = new Condition() {
        @Override
        public String toRecordString(Condition parent) {
            throw new UnsupportedOperationException("This is a root condition, this condition is used to start condition building process.");
        }
    };

    private static char WITHOUT = '-';

    /**
     * It will build an and condition using two parts (Left and Right)
     *
     * @param left  left part of and
     * @param right right part of and
     * @return A built condition
     */
    public static Condition and(Condition left, Condition right) {
        return new ANDCondition(left, right);
    }

    /**
     * It will build an or condition using two parts (Left and Right)
     *
     * @param left  left part of or
     * @param right right part of or
     * @return A built condition.
     */
    public static Condition or(Condition left, Condition right) {
        return new ORCondition(left, right);
    }

    /**
     * It will an in condition using an array of values.
     *
     * @param left attribute to compare
     * @param values string values to build IN condition
     * @return a built condition.
     */
    public static Condition in(String left, String[] values) {
        return innerInCondition(left, values, false);
    }

    /**
     * It will an in condition using an array of values.
     *
     * @param left attribute to compare
     * @param values number values to build IN condition
     * @return a built condition.
     */
    public static Condition in(String left, Number[] values) {
        return innerInCondition(left, values, false);
    }

    /**
     * It will an in condition using an array of values.
     *
     * @param left attribute to compare
     * @param values character values to build IN condition
     * @return a built condition.
     */
    public static Condition in(String left, Character[] values) {
        return innerInCondition(left, values, false);
    }

    /**
     * It will an in condition using an array of values.
     *
     * @param left attribute to compare
     * @param values string values to build IN condition
     * @return a built condition.
     */
    public static Condition notIn(String left, String[] values) {
        return innerInCondition(left, values, true);
    }

    /**
     * It will an in condition using an array of values.
     *
     * @param left attribute to compare
     * @param values number values to build IN condition
     * @return a built condition.
     */
    public static Condition notIn(String left, Number[] values) {
        return innerInCondition(left, values, true);
    }

    /**
     * It will an in condition using an array of values.
     *
     * @param left attribute to compare
     * @param values character values to build IN condition
     * @return a built condition.
     */
    public static Condition notIn(String left, Character[] values) {
        return innerInCondition(left, values, true);
    }

    /**
     * It will build a like condition.
     *
     * @param left attribute to comapare
     * @param like String to use for like condition
     * @return a built condition.
     */
    public static Condition like(String left, String like) {
        return new LikeCondition(left, like, false);
    }

    /**
     * It will build a not like condition.
     *
     * @param left attribute to comapare
     * @param like String to use for like condition
     * @return a built condition.
     */
    public static Condition notLike(String left, String like) {
        return new LikeCondition(left, like, true);
    }

    /**
     * It will build an equals condition.
     *
     * @param left attribute to compare
     * @param right right part of this condition
     * @return a built condition.
     */
    public static Condition equals(String left, String right) {
        return innerEquals(left, right, WITHOUT);
    }

    /**
     * It will build an equals condition.
     *
     * @param left attribute to compare
     * @param right Number to use for equals condition
     * @return a built condition.
     */
    public static Condition equals(String left, Number right) {
        return innerEquals(left, right, WITHOUT);
    }

    /**
     * It will build an equals condition.
     *
     * @param left attribute to compare
     * @param right right part of this condition
     * @return a built condition.
     */
    public static Condition equals(String left, Character right) {
        return innerEquals(left, right, WITHOUT);
    }

    /**
     * It will build a not equals condition.
     *
     * @param left attribute to compare
     * @param right right part of this condition
     * @return a built condition.
     */
    public static Condition notEquals(String left, Object right) {
        return innerNotEquals(left, right);
    }

    /**
     * It will build a greater equals condition.
     *
     * @param left attribute to compare
     * @param right right part of this condition
     * @return a built condition.
     */
    public static Condition greaterEquals(String left, Object right) {
        return innerEquals(left, right, '>');
    }

    /**
     * It will build a lesser equals condition.
     *
     * @param left attribute to compare
     * @param right right part of this condition
     * @return a built condition.
     */
    public static Condition lesserEquals(String left, Object right) {
        return innerEquals(left, right, '<');
    }

    /**
     * This method will build a not equals condition.
     *
     * @param left value to compare
     * @param right right part of this condition
     * @return a built condition
     */
    private static Condition innerNotEquals(String left, Object right) {
        return new NotEqualCondition(left, right);
    }

    /**
     * This method will build either a equals condition.
     *
     * @param left value to compare
     * @param greaterLesser   indicates if greater or lesser condition must be added.
     * @return a built condition
     */
    private static Condition innerEquals(String left, Object right, char greaterLesser) {
        return new EqualCondition(left, right, greaterLesser);
    }

    /**
     * It will either an in or not in condition using an array of values and a boolean that indicates
     * what kind of IN will be built.
     *
     * @param left attribute to compare
     * @param values String values to build IN condition
     * @return a built condition.
     */
    private static Condition innerInCondition(String left, Object[] values, boolean notIn) {
        return new INCondition(left, values, notIn);
    }
}