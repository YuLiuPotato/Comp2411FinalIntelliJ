package hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.File;

/**
 * Criterion that asserts on name of a <code>File</code>
 * @see File
 */
public class NameCri extends Criterion{
    /**
     * Constructor
     * @param criName name of this Criterion
     * @param checkName Operand of this Criterion
     * @throws IllegalArgumentException thrown when name is illegal
     * @see Criterion
     */
    public NameCri(String criName, String checkName) throws IllegalArgumentException {
        super(criName, AttrName.name, Op.contains, checkName);
    }

    /**
     * Assert if the name of the file satisfies this Criterion
     * @param file <code>File</code> to be checked
     * @return true if the file satistifies, false otherwise
     */
    @Override
    public boolean assertCri(File file) {
        return file.getName().contains(super.getVal().toString());
    }
}
