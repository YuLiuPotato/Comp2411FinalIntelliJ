package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;

import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;

/**
 * A class represents the content of a <code>Directory</code>
 * It stores a table of String-File pair, the String being name of the <code>File</code>
 *
 * @see File
 */
public class Dirent implements Iterable<File>{
    
    //A Dirent is a dictionary keys -- String mapped object -- File 16 of initial capacity 16*0.75 to double the capacity
    private HashMap<String,File> dirent = new LinkedHashMap<String,File>();

    /**
     * Register a new File in the entry table
     * @param name name of the file
     * @param file the <code>File</code> to be registered
     * @throws IllegalArgumentException This exception shall be thrown when the String-File pair with String being name has ready existed
     */
    public void put(String name, File file) throws IllegalArgumentException {
        File temp = dirent.get(name);
        //We do not permit directory and document to share the same name
        if (temp != null) throw new IllegalArgumentException("File already exists.");
        dirent.put(name, file);
    }


    /**
     * Retrieve the <code>File</code> object from the table with name name
     * @param name the name of the <code>File</code> to retrive
     * @return the <code>File</code> object with name name
     * @throws IllegalArgumentException thrown when there is no File with name name in the entry
     */
    public File get(String name) throws IllegalArgumentException {
        File temp = dirent.get(name);
        if (temp == null) throw new IllegalArgumentException("File does not exist");
        return dirent.get(name);
    }


    /**
     * Romove the File with name name in the entry
     * @param name the name of the File to remove
     * @return the size of the removed file
     * @throws IllegalArgumentException thrown when there is no File with name name in the entry
     */
    public int remove(String name) throws IllegalArgumentException {
        File temp = this.get(name);
        int size = temp.getSize();
        dirent.remove(name);
        return size;
    }

    /**
     * Get all the files in the entry
     * @return An iterator of <code>File</code> in this entry
     */
    @Override
    public Iterator<File> iterator() { return dirent.values().iterator(); }

    /**
     * Get all the file names in the entry
     * @return An iterator of file names in this entry
     */
    public Iterator<String> keyIterator(){ return dirent.keySet().iterator(); }
}
