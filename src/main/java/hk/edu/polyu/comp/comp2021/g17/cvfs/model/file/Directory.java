package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;


import java.util.ArrayList;
import java.util.Iterator;

/**
 * A child class of <code>File</code> representing directory
 */
public class Directory extends File{

    private static final int BASE = 40;


    /**
     * Getter mehod
     * @return parent of this Directory
     */
    public Directory getParent() {
        return parent;
    }

    private Directory parent; //Keep a pointer to its parent for easy tracing-up

    @Override
    public Dirent getContent() {
        return content;
    }

    private Dirent content; //Get a content field for itself so we don't need to cast every time we use the dirent



    /**
     * This constructor call the constructor of <code>File</code> with FileType Directory and calculated size
     * @param name the name of this Directory with specified constraints
     * @see File
     * @param parent the parent Directory in which the Directory is created. null if this is a root directory
     * @throws IllegalArgumentException thrown when 1) directory name does not conform. 2) there's already a file with
     * name name in the parent directory
     */
    public Directory(String name, Directory parent) throws IllegalArgumentException {
        // size = 40 + total size of its contained file?
        super(FileType.Directory, name, BASE, null);
        this.parent = parent;
        this.content = new Dirent();
        try {
            content.put(".",this);
            if(parent != null)  content.put("..",parent);
        } catch (IllegalArgumentException e) {
            //This exception is never triggered
            e.printStackTrace();
        }

        super.setContent(content);

        if(this.parent != null){
            this.parent.getContent().put(name,this);
            changeSize(this.parent, this,'+');
        }
    }


    private void changeSize(Directory dic, File file, char sign){
        Directory cur = dic;
        while(cur != null){

            switch(sign){
                case '+':
                    cur.setSize(cur.getSize() + file.getSize());
                    break;
                case '-':
                    cur.setSize(cur.getSize() - file.getSize());
            }

            cur = cur.getParent();
        }
    }


    /**
     * Add a new document to this directory
     * @param name name of the document
     * @param content content of the document
     * @param type DocumentTyoe of the document
     * @throws IllegalArgumentException thrown when 1) name is not a valid name 2) there's already a <code>File</code> named name in this
     * Directory
     */
    public void newDoc(String name, String content, DocumentType type) throws IllegalArgumentException {
        Document newdoc =new Document(name,type,content);
        this.content.put(name,newdoc);
        changeSize(this, newdoc,'+');
    }

    /**
     * Add a new document to this directory
     * @param name name of the document
     * @param content content of the document
     * @param type a String representation of DocumentType
     * @throws IllegalArgumentException thrown when 1) name is not a valid name 2) there's already a <code>File</code> named name in this
     * Directory
     */
    public void newDoc(String name, String content, String type) throws IllegalArgumentException {
        DocumentType rightOne = null;
        if(type ==null) throw new IllegalArgumentException();
        for (DocumentType t:DocumentType.values()) {
            if (t.name().compareTo(type) == 0) {
                rightOne = t;
                break;
            }
        }
        newDoc(name, content, rightOne);
    }

    /**
     * Add a new Directory to this directory
     * @param name name of the directory
     * @throws IllegalArgumentException thrown when 1) name is not a valid name 2) there's already a <code>File</code> named name in this
     * Directory
     */
    public void newDir(String name) throws IllegalArgumentException {
        Directory newdir = new Directory(name,this);
    }

    /**
     * Delete the file with name name from this directory
     * The file can be a <code>Directory</code> or a <code>Document</code>
     * @param name name of the file to be deleted
     * @throws IllegalArgumentException thrown when 1)name is "." or ".." 2)file with name name does not exist in this directory
     */
    public void delete(String name) throws IllegalArgumentException {
        if(name.equals(".") || name.equals("..")) throw new IllegalArgumentException("Cannot delete directory " + name);
        File removed = this.content.get(name); // get the removed object, check if it exists or left for information
        changeSize(this,removed,'-');
        this.content.remove(name);
    }

    private boolean isLetterOrDigit(char character) {
        return ((int)character >= (int) '0' && (int)character <= (int) '9') || ((int)character >= (int) 'A' && (int)character <= (int) 'Z') || ((int)character >= (int) 'a' && (int)character <= (int) 'z');
    }


    private boolean isNameLegal(String name){
        if(name == null || name.length()>10 ||name.length()==0) return false;
        for (int i = 0; i < name.length(); i++) {
            if (!isLetterOrDigit(name.charAt(i)) ) {
                return false;
            }
        }
        return true;
    }

    /**
     * Rename the file from oldName to newName
     * @param oldName oldName of the file
     * @param newName newName of the file
     * @throws  IllegalArgumentException thrown when 1)oldName does not exist 2)newName already exists
     */
    public void rename(String oldName, String newName) throws IllegalArgumentException {
        File toChangeName = content.get(oldName);
        if(!isNameLegal(newName)) throw new IllegalArgumentException(newName + " is not a valid name");
        toChangeName.setName(newName);
        content.remove(oldName);
        content.put(newName,toChangeName);
    }

    /**
     * Find the directory whose name is name
     * @param name name of the directory to find
     * @return Directory with name name
     * @throws IllegalArgumentException thrown when 1)File "name" is not a directory 2)No file with name name exists in this directory
     */
    public Directory findDir(String name) throws IllegalArgumentException {
        if (this.content.get(name).getClass() != this.getClass()) throw new IllegalArgumentException(name + " is not a directory.");
        return (Directory) this.content.get(name);
    }

    /**
     * Find the document whose name is name
     * @param name name of the document to find
     * @return Document with name name
     * @throws IllegalArgumentException thrown when 1)File "name" is not a document 2)No file with name name exists in this directory
     */
    public Document findDoc(String name) throws IllegalArgumentException {
        if (this.content.get(name).getClass() == this.getClass()) throw new IllegalArgumentException(name + "is not a document.");
        return (Document) this.content.get(name);
    }


    /**
     * List all files in this directory.
     * This method does not follow directory.
     * @see #rList()
     */
    public void list() {

        Iterator<String> it = this.content.keyIterator();

        int number = 0, size = 0;

        System.out.println("In directory " + this.getName() + ": ");

        while (it.hasNext()){
            String itFile = it.next();
            if (itFile.compareTo(".") == 0 || itFile.compareTo("..") == 0) continue;

            if(content.get(itFile) instanceof Directory){
                System.out.println("\tDirectory: " + itFile + " | Size: " + this.content.get(itFile).getSize() );
            }

            else{
                System.out.println(("\tDocument: " + (Document)content.get(itFile)).toString() +
                        " | Size: " + this.content.get(itFile).getSize());
            }

            number++;
            size += this.content.get(itFile).getSize();
        }

        System.out.println("\nTotal number of files: " + number);
        System.out.println("Total sizes of files: " + size);
    }

    private void rList(int level){
        Iterator<String> it = this.content.keyIterator();
        while (it.hasNext()){
            String itFile = it.next();
            if (itFile.compareTo(".") == 0 || itFile.compareTo("..") == 0) continue;

            for (int i=0; i<level; i++) System.out.print("\t");

            if (content.get(itFile) instanceof Directory){
                System.out.println("Directory: " + itFile + " | Size: " + this.content.get(itFile).getSize() );
                ((Directory)this.content.get(itFile)).rList(level+1);
            }else{
                System.out.println("Document: " + ((Document)content.get(itFile)).toString() +
                        " | Size: " + this.content.get(itFile).getSize());
            }
        }
    }


    /**
     * Recursively list all files in this directory
     */
    public void rList(){
        System.out.println("In directory " + this.getName() + ":");
        rList(1);
    }

    @Override
    public String toString(){
        return this.getName();
    }

    /**
     * For debugging
     * @param arg an integer
     * @return a list of file this directory contains
     */
    public String toString(int arg) {
        StringBuilder sb = new StringBuilder();
        sb.append("[");
        int i = 0;
        Iterator<String> it = this.content.keyIterator();
        //it.next();
        while (it.hasNext()){
            String itFile = it.next();
            sb.append(itFile);
            if (it.hasNext())
                sb.append(",");
        }
        sb.append("]");
        return sb.toString();
    }


    /**
     * @return all files in this direcotry
     */
    public ArrayList<File> getFiles(){
        ArrayList<File> result = new ArrayList<File>();
        Iterator<String> ite = content.keyIterator();

        while (ite.hasNext()) {
            String name = ite.next();
            if (name.compareTo(".") == 0 ||name.compareTo("..") == 0) continue;
            try {
                result.add(content.get(name));
            } catch (IllegalArgumentException e) {
                // never reached
                e.printStackTrace();
            }
        }

        return result;
    }

    /**
     * @return Recursively get all files in this directory
     */
    public ArrayList<File> rGetFiles() {
        ArrayList<File> result = new ArrayList<File>();
        Iterator<String> ite = content.keyIterator();

        while (ite.hasNext()) {
            try {
                String fileName = ite.next();
                File file = content.get(fileName);

                if (file instanceof Document) result.add(file);

                else {
                    if (fileName.compareTo("..") == 0 || fileName.compareTo(".") == 0) {
                    } else {
                        result.add(file);
                        Directory dir = (Directory) file;
                        result.addAll(dir.rGetFiles());
                    }
                }
            } catch (IllegalArgumentException e) {
                // Never triggered
                e.printStackTrace();
            }
        }

        return result;
    }
}
