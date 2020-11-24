package hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion;


/**
 * This enum class represents all possible operator used in constructing a <code>Criterion</code>
 *
 * @see Criterion
 */
public enum Op {
    /**
     * contains operator
     */
    contains,
    /**
     * equals operator
     */
    equals,
    /**
     * ">" operator
     */
    G,
    /**
     * ">=" operator
     */
    GE,
    /**
     * "<" operator
     */
    L,
    /**
     * "<" operator
     */
    LE,
    /**
     * = operator
     */
    E,
    /**
     * "!=" operator
     */
    NE,
    /**
     * negate version of contains operator
     */
    not_contains,
    /**
     * logical operaptr AND
     */
    AND,
    /**
     * logical operator OR
     */
    OR,
    /**
     * negate version of AND
     */
    NOT_AND,
    /**
     * negate version of OR
     */
    NOT_OR,
    /**
     * negate version of equals operator
     */
    not_equals;

    /**
     * Generate a negate version of this Op
     * @return a negate Op of this Op
     */
    public Op negate() {
        switch (this) {

            case contains:
                return not_contains;
            case E:
                return NE;
            case G:
                return L;
            case GE:
                return NE;
            case L:
                return G;
            case LE:
                return GE;
            case NE:
                return E;
            case equals:
                return not_equals;
            case not_contains:
                return contains;
            case not_equals:
                return equals;
            case OR:
                return NOT_OR;
            case AND:
                return NOT_AND;
            case NOT_OR:
                return OR;
            case NOT_AND:
                return NOT_AND;
            default:
                //never reached
                return null;
        }

    }
}
