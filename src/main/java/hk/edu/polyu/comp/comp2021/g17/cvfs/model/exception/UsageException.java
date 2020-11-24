package hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception;

/**
 * An Exception for reporting usage
 */
public class UsageException extends Exception{
    /**
     * @param msg message to send
     */
    public UsageException(String msg){
        super(msg);
    }
}
