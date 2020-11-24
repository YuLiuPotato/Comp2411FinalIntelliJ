package hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.File;

/**
 * A subclass of Criterion representing Criterion that asserts on size of a <code>File</code>
 *
 * @see Critetion
 * @see File
 */
public class SizeCri extends Criterion{
    private Op comparator;
    private int comparand;


    /**
     * @param criName Name of this criterion, with specified constraints
     * @param op Operaotr to be used
     * @param size Operand of this criterion
     * @throws IllegalArgumentException thrown when name does not conform
     *
     * @see Op
     */
    public SizeCri(String criName, Op op, int size) throws IllegalArgumentException {
        super(criName, AttrName.size, op, size);
        this.comparator = op;
        this.comparand = size;
    }

    @Override
    public boolean assertCri(File file) {
        switch (comparator) {
            case E:
                return file.getSize() == comparand;
            case G:
                return file.getSize() > comparand;
            case GE:
                return file.getSize() >= comparand;
            case L:
                return file.getSize() < comparand;
            case LE:
                return file.getSize() <= comparand;
            case NE:
                return file.getSize() != comparand;
            default:
                //never reached
                return false;
        }
    }
}
