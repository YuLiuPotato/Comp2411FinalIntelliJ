package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;

/**
 * A child class of <code>File</code>
 * A Document has a <code>DocumentType</code>.
 * The content of a Document is a String
 *
 * @see File
 * @see DocumentType
 */
public class Document extends File {
    private DocumentType type;
    private static final int BASE = 40;
    private static final int SCALE = 2;

    /**
     * This constructor call the constructor of <code>File</code> with FileType Document and calculated size.
     * In addition, a DocumentType is passed in to indicate the DocumentType of this Documenet
     *
     * @param name Name of the document, with specified constraints.
     * @param type DocumentType of the document
     * @see DocumentType
     * @param content A string represent content of the document
     * @throws IllegalArgumentException The Exception shall be thrown when name does not conform to the constraints
     */
    public Document(String name, DocumentType type, String content) throws IllegalArgumentException {
        super(FileType.Document, name, BASE + SCALE * content.length(), content);
        if(type == null) throw new IllegalArgumentException();
        this.type = type;
    }


    /**
     * Getter method
     * @return DocumentType of this Document
     */
    public DocumentType getDocumentType() {
        return type;
    }

    @Override
    public String toString(){
        return super.getName() + "." + type.name();
    }
}
