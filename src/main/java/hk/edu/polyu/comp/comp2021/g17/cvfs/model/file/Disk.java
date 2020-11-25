package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception.MemoryException;

import java.util.LinkedList;


/**
 * A class represents Disk
 * A disk has a root <code>Directory</code>
 * A disk has a current working <code>Directory</code>
 * A disk keeps a path to current working directory
 * A disk has a maximum size, as specified during construction
 *
 * @see Directory
 */
public class Disk {
    private static final int MINMAXSIZE = 40;
    private Directory root;
    private Directory cwd;
    private LinkedList<String> path;
    private final int maxSize;


    /**
     * @param size maximum size of this Disk
     * @throws IllegalArgumentException thrown when try to construct a disk less than 40 bytes, since a root directory is at least 40 bytes
     */
    public Disk(int size) throws IllegalArgumentException{
        //At least 40 bytes so it can have a root directory
        if (size <= MINMAXSIZE) throw new IllegalArgumentException("Cannot create a disk less than 40 bytes");

        path = new LinkedList<String>();
        maxSize = size;

        try {
            root = new Directory("root",null);
            cwd = root;
            path.add("/");
        } catch (IllegalArgumentException e) {
            //This exception is never triggered
            e.printStackTrace();
        }
    }

    /**
     * Change current working directory to the directory with name name
     * @param name the directory to go to. It must be in this directory
     * @throws IllegalArgumentException thrown when 1)file with name does not exist in this directory 2)file with name is not a directory
     */
    public void changeDir(String name) throws IllegalArgumentException {
        if (name.compareTo("..") == 0){
            if (cwd.getParent() == null) throw new IllegalArgumentException("No parent directory in root");
            else{
                cwd = cwd.getParent();
                path.removeLast();
            }
        }else if (name.compareTo(".") == 0){
            //do nothing
        }else{
            cwd = cwd.findDir(name);
            path.addLast(name);
        }
    }

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();

        if (path == null) return "";

        for (String s : path){
            sb.append(s).append(":");
        }

        return sb.toString();
    }

    /**
     * Create a new directory in current working directory
     * @param name name of the new Directory
     * @throws MemoryException thrown when Disk memory is not enough
     * @throws IllegalArgumentException thrown when name is not a valid name as specified in <code>File</code>
     *
     * @see File
     */
    public void newDir(String name) throws MemoryException, IllegalArgumentException {
        int capacity = this.maxSize - this.root.getSize();
        if (this.maxSize < this.root.getSize() + MINMAXSIZE) throw new MemoryException("Disk memory is not enough. Require: 40"+ "\tCapacity: " + capacity );
        cwd.newDir(name);
    }

    /**
     * Create a new Document in current working directory
     * @param name name of the new Document
     * @param type DoucmentType of the new Document
     * @param content content of the new Document
     * @throws MemoryException thrown when Disk memory is not enough
     * @throws IllegalArgumentException thrown when name is not a valid name as specified in <code>File</code>
     * 
     * @see File
     */
    public void newDoc(String name, DocumentType type, String content) throws MemoryException, IllegalArgumentException {
        int required = MINMAXSIZE + content.length() * 2;
        int capacity = this.maxSize - this.root.getSize();
        if (required > capacity) throw new MemoryException("Disk memory is not enough. Require: " + required + "\tCapacity: " + capacity );
        cwd.newDoc(name,content,type);
    }

    /**
     * Delete the file with name name in current working directory
     * The file can be a <code>Document</code> or a <code>Directory</code>
     * @param name name of the file to be deleted
     * @throws IllegalArgumentException thrown when name does not exist in current working directory
     */
    public void delete(String name) throws IllegalArgumentException {
        cwd.delete(name);
    }


    /**
     * @param oldName name of the file to be renamed
     * @param newName new name of the file
     * @throws IllegalArgumentException thrown when 1)oldName does not exist in current working directory
     * 2)newName already exists in current working directory 3)newName is not a valid file name as specified
     * 
     * @see File
     */
    public void rename(String oldName, String newName) throws IllegalArgumentException {
        cwd.rename(oldName,newName);
    }

    /**
     * List all files in the current working directory
     */
    public void list() {
        cwd.list();
    }

    /**
     * Recursicely list all files in the current working directory
     */
    public void rList() {
        cwd.rList();
    }

    /**
     * Getter method
     * @return root directory the this Disk
     */
    public Directory getRoot() {
        return root;
    }

    /**
     * Getter method
     * @return current working directory of this Disk
     */
    public Directory getcwd() {
        return cwd;
    }
}
