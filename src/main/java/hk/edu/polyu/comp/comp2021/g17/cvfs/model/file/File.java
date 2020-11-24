package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;

/**
 * Abstract class <code>File</code> representing file objects. A <code>File</code> can be either <code>Document</code>
 * or <code>Directory</code>
 *
 * @see File
 * @see Document
 */
public abstract class File {

    /**
     * Type of a file, either Directory or Document
     *
     * @see FileType
     */
    private final FileType fileType;

    /**
     * Name of a file. Only digits and letters are permitted. Length of the name shall not exceed 10 characters
     * Otherwise <code>InvalidArgumentException</code> is thrown
     */
    private String name;

    /**
     * Size of a file. The size of a <code>Document</code> is calculated as 40 + 2 * content.length.
     * The size of a <code>Directory</code> is calculated as 40 + total of contained <code>File</code>
     */
    private int size;

    /**
     * Content of a file. For <code>Document</code>, the content is a <code>String</code>. For <code>Directory</code>,
     * the content is a <code>Direct</code>
     *
     * @see Dirent
     */
    private Object content;


    /**
     * Constructor to be used by its child classes
     *
     * @param fileType Type of the file to be constructed, either Document or Directory @see fileType
     * @param name Name of the file with constraints. @see name
     * @param size Size of the file to be constructed. @see size
     * @param content Content of the file. @see content
     * @throws IllegalArgumentException This exception shall be thrown when the name passed does not meet the constraints specified
     */
    protected File(FileType fileType,String name, int size, Object content) throws IllegalArgumentException {
        if (!isNameLegal(name))
            throw new IllegalArgumentException(name + " is not a valid name\nRequirement: only digits and letters, length not exceeds 10");
        this.name = name;
        this.size = size;
        this.content = content;
        this.fileType = fileType;
    }


    private boolean isLetterOrDigit(char character) {
        return ((int)character >= (int) '0' && (int)character <= (int) '9') || ((int)character >= (int) 'A' && (int)character <= (int) 'Z') || ((int)character >= (int) 'a' && (int)character <= (int) 'z');
    }

    private boolean isNameLegal(String name){
        if ( name != null && name.length() <= 10) {
            for (int i = 0; i < name.length(); i++) {
                if (!isLetterOrDigit(name.charAt(i)) ) {
                    return false;
                }
            }
            return true;
        }
        return false;
    }

    /**
     * Getter method
     * @return type of this file, either <code>Document</code> or <code>Directory</code>
     */
    public FileType getFileType() {
        return fileType;
    }

    /**
     * Getter method
     * @return name of this file
     */
    public String getName() {
        return name;
    }

    /**
     * Setter method
     * @param name new name of the file, with specified constraints
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Getter method
     * @return size the this file as described
     */
    public int getSize() {
        return size;
    }

    /**
     * Setter method
     * @param size size to be set
     */
    public void setSize(int size){
        this.size = size;
    }
    /**Getter method
     * @return content of this file as described
     */
    public Object getContent() {
        return content;
    }

    /**
     * Setter method
     * @param content the content of this File
     */
    public void setContent(Object content){
        this.content = content;
    }



}
