package hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception;


/**
 * An Exception to handle memory issue
 */
public class MemoryException extends Exception{
    /**
     * @param msg message wriiten in this exception
     */
    public MemoryException(String msg){
        super(msg);
    }
}
