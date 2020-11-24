package hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Document;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.DocumentType;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.File;

/**
 * A Criterion that asserts on <code>DocumentType</code> of a <code>Document</code>
 * @see DocumentType
 * @see Document
 */
public class TypeCri extends Criterion{
    private final DocumentType comparand;

    /**
     * @param criName name of this Criterion
     * @param type Operand of this Criterion
     * @throws IllegalArgumentException thrown when name is illegal
     */
    public TypeCri(String criName, DocumentType type) throws IllegalArgumentException {
        super(criName, AttrName.type, Op.equals, (Object)type);
        this.comparand = type;
    }
    @Override
    public boolean assertCri(File file) {
        Document doc = (Document) file;

        return doc.getDocumentType() == comparand;
    }
}
