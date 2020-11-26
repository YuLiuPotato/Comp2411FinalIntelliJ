package hk.edu.polyu.comp.comp2021.g17.cvfs.model.filesystem;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;
import java.util.regex.Pattern;
import java.nio.file.DirectoryStream;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion.Criterion;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception.MemoryException;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception.UsageException;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Directory;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Disk;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Document;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.DocumentType;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.File;


/**
 * FileSystem Interface Class
 * Each mathod correspond to a command
 */
public class FileSystem {
    /**
     * set public for testing purpose
     */
    private ArrayList<Disk> disks = new ArrayList<Disk>();
    /**
     * set public for testing purpose
     */
    private Disk currentDisk;
    /**
     * set public for testing purpose
     */
    private HashMap<String,Criterion> criteria = new HashMap<String, Criterion>();
    /**
     * set public for testing purpose
     */
    private ArrayList<String> commandHistory = new ArrayList<String>();
    /**
     * set public for testing purpose
     */
    private LinkedList<String> redoStack = new LinkedList<String>();
    /**
     * set public for testing purpose
     */
    private int commandPtr = -1; //points to last command

    /**
     * set public for testing purpose
     */

    public ArrayList<String> canAddToHistory = new ArrayList<String>(Arrays.asList("newDisk","newDir","newDoc","delete","newSimpleCri"
            ,"newNegation","newBinaryCri","store","load","rename"));

    /**
     * Interface Constructor
     * When this Constructor is called, an interactive session is created
     */
    public FileSystem() {
        //TODO
        //Calling constructor is when the system is entered. Interaction begins here
        //Arguments are passed as String

        Scanner sc = new Scanner(System.in);
        String commandName;
        String args;
        String commandPattern = "(newDisk|newDir|newDoc|delete|rename|changeDir|list|rList|newSimpleCri|newNegation|newBinaryCri|printAllCriteria"
                + "|search|rSearch|store|load|undo|redo)";


        while(true) {
            if (currentDisk == null) System.out.print("$: ");
            else System.out.print("$" + currentDisk.toString() + " ");
            try {
                commandName = sc.next();
                if (! Pattern.matches(commandPattern, commandName)) throw new IllegalArgumentException("Command not found");

                else {

                    args = sc.nextLine();

                    try {
                        Method m = this.getClass().getMethod(commandName,String.class);

                        m.invoke(this,args); //this swallows all exceptions!!!

                        //regular command
                        //store the command
                        //forward the pointer
                        if (canAddToHistory.contains(commandName)) {
                            commandHistory.add(++commandPtr, commandName + " " + args);
                        }

                        //irregular command
                        //do nothing

                    } catch(NoSuchMethodException e) {
                        //this exception should never be triggered
                    } catch(InvocationTargetException ie) {
                        throw ie.getCause();
                    }
                }

            }catch (IOException ioe){
                System.err.print("Local File System IO Error");
            }catch(Throwable e) {
                System.err.println(e.getMessage());
            }

        }
    }

    /**
     * for testing
     * @param test some String
     */
    public FileSystem(String test) {
        //Constructor for conducting tests
        //initialization
        disks = new ArrayList<Disk>();
        criteria = new HashMap<String, Criterion>();
        commandHistory = new ArrayList<String>();
        commandPtr = -1;
        redoStack = new LinkedList<String>();
    }
    
    /**
     * create a new disk and use it as current working disk
     * @param args user input argument String
     * @throws UsageException thrown when 1) argument is not an Integer 2) argument count does not match
     * @throws IllegalArgumentException thrown when try to construct a disk less than 40 bytes
     */
    public void newDisk(String args) throws UsageException {
        //parameter parsing ignores the insignificant parts
        try (Scanner sc = new Scanner(args)) {
            int size = sc.nextInt();
            if (sc.hasNext()) throw new UsageException("Usage: newDisk <diskSize>");
            currentDisk = new Disk(size);
            disks.add(currentDisk);
        } catch (NoSuchElementException e) {
            throw new UsageException("Usage: newDisk <diskSize>");
        }
    }

    /**
     * Add a new Directory in the current working directory
     * @param args user input argument String
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)name of the directory is not legal 2)there's already a file
     * named name in current working directory
     * @throws MemoryException thrwon when disk does not have enough memory to accpet a new directory
     */
    public  void newDir(String args) throws UsageException, IllegalArgumentException, MemoryException {
        try (Scanner sc = new Scanner(args)) {
            String name = sc.next();
            if (sc.hasNext()) throw new UsageException("Usage: newDir <dirName>");
            if (currentDisk == null) throw new MemoryException("No disk found. Try command 'newDisk' first");
            currentDisk.newDir(name);
        } catch (NoSuchElementException e) {
            throw new UsageException("Usage: newDir <dirName>");
        }
    }


    /**
     * Add a new Document in the current working directory
     * @param args user input argument String
     *             content should be surrouded with double quotes
     *             eg.newDoc testDoc txt "Content in testDoc"
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)name of the document is not legal 2)there's already a file
     * named name in current working directory
     * @throws MemoryException thrwon when disk does not have enough memory to this new document
     */
    public  void newDoc(String args) throws UsageException, IllegalArgumentException, MemoryException {
        Scanner sc= new Scanner(args);
        try {
            String name = sc.next();
            String type = sc.next("(html|txt|css|java)");
            String content = sc.next("\".*\"");
            StringBuilder temp = new StringBuilder(content);
            temp.deleteCharAt(content.length()-1);
            temp.deleteCharAt(0);
            content = temp.toString();
            if (sc.hasNext()) throw new UsageException("Usage: newDoc <name, type, content>");
            if (currentDisk == null) throw new MemoryException("No disk found. Try command 'newDisk' first");

            DocumentType dType;
            if (type.compareTo("html") == 0) dType = DocumentType.html;
            else if (type.compareTo("txt") == 0) dType = DocumentType.txt;
            else if (type.compareTo("css") == 0) dType = DocumentType.css;
            else dType = DocumentType.java;
            currentDisk.newDoc(name, dType, content);
        }catch(NoSuchElementException e) {
            throw new UsageException("Usage: newDoc <name, type, \"content\"> (content should be double quoted)");
        }

    }


    /**
     * delete a file in current working directory
     * @param args user input argument String
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)no such file in current working directory 2)"." or ".." is passed
     */
    public void delete(String args) throws UsageException, IllegalArgumentException {
        try (Scanner sc = new Scanner(args)) {
            String name = sc.next();
            if (sc.hasNext()) throw new UsageException("Usage: delete <docName>");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            currentDisk.delete(name);
        } catch (NoSuchElementException e) {
            throw new UsageException("Usage: delete <docName>");
        }
    }


    /**
     * rename a file in current working directory
     * @param args user input argument String
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)the file does not exist 2)the new name already exists
     * 3)new name is not a valid name 4)try to rename '.' or '..'
     */
    public  void rename(String args) throws UsageException, IllegalArgumentException {
        try (Scanner sc = new Scanner(args)) {
            String oldName = sc.next();
            String newName = sc.next();
            if (sc.hasNext()) throw new UsageException("Usage: rename <oldName, newName>");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            currentDisk.rename(oldName, newName);
        } catch (NoSuchElementException e) {
            throw new UsageException("Usage: rename <oldName, newName>");
        }
    }


    /**
     * Change current working directory to the directory specified
     * @param args User input argument String
     * @throws UsageException thrown when arugment count does not match
     * @throws IllegalArgumentException thrown when 1)the file does not exist 2)file specified is not a directory
     */
    public  void changeDir(String args) throws UsageException, IllegalArgumentException {
        try (Scanner sc = new Scanner(args)) {
            String name = sc.next();
            if (sc.hasNext()) throw new UsageException("Usage: changeDir <dirName>");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            currentDisk.changeDir(name);
        } catch (NoSuchElementException e) {
            throw new UsageException("Usage: changeDir <dirName>");
        }
    }


    /**
     * List all files in current working directory
     * @param args User input argument String
     * @throws UsageException thrown when args is not an empty String
     */
    public void list(String args) throws UsageException {
        try (Scanner sc = new Scanner(args)) {
            if (sc.hasNext()) throw new UsageException("Usage: list");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            currentDisk.list();
        }
    }

    /**
     * Recursively list all files in current working directory
     * @param args User input argument String
     * @throws UsageException thrown when args is not an empty String
     */
    public  void rList(String args) throws UsageException {
        try (Scanner sc = new Scanner(args)) {
            if (sc.hasNext()) throw new UsageException("Usage: rList <>");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            currentDisk.rList();
        }
    }

    /**
     * Make a simple Criterion
     * @param args User input argument String
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)Criterion name is not a legal name 2)Argument combination does not
     * conform to the rules
     * @see Criterion
     */
    public  void newSimpleCri(String args) throws UsageException, IllegalArgumentException {

        try (Scanner sc = new Scanner(args)) {
            String[] argStrings = new String[4];
            for (int i = 0; i < 4; i++) {
                argStrings[i] = sc.next();
            }

            if (sc.hasNext()) throw new UsageException("Usage: newSimpleCri <name, attrname, oprator, oprand>");
            criteria.put(argStrings[0], Criterion.newSimpleCri(argStrings[0], argStrings[1], argStrings[2], argStrings[3]));
        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: newSimpleCri <name, attrname, oprator, oprand>");
        }

    }

    /**
     * make a negate version of the criterion specified
     * @param args User input argument String
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)new criterion name is illegal 2)operator does not conform
     * 3)one or more specified criterions do not exist 4)criterion thus named already exists
     */
    public  void newNegation(String args) throws UsageException, IllegalArgumentException {

        try (Scanner sc = new Scanner(args)) {
            String negName = sc.next();
            String toNegName = sc.next();

            Criterion toNegCri = criteria.get(toNegName);
            if (criteria.get(negName) != null) throw new IllegalArgumentException(negName + " already exists");
            if (toNegCri == null) throw new IllegalArgumentException("No criterion: " + toNegName);
            criteria.put(negName, Criterion.newNegation(negName, toNegCri));
        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: newNegation <name, criterion>");
        }
    }


    /**
     * Make a binary composition of criterions specified
     * @param args User input argument String
     * @throws UsageException thrown when argument count does not match
     * @throws IllegalArgumentException thrown when 1)new criterion name is illegal 2)operator does not conform
     * 3)one or more specified criterions do not exist 4)criterion thus named already exists
     */
    public  void newBinaryCri(String args) throws UsageException, IllegalArgumentException {

        try (Scanner sc = new Scanner(args)) {
            String[] argStrings = new String[4];
            for (int i = 0; i < 4; i++) {
                argStrings[i] = sc.next();
            }

            if (sc.hasNext()) throw new UsageException("Usage: newBinaryCri <name, cri1, cri2, binaryOperator>");

            Criterion cri1 = criteria.get(argStrings[1]);
            Criterion cri2 = criteria.get(argStrings[3]);

            if (cri1 == null) throw new IllegalArgumentException(argStrings[1] + " does not exist");
            if (cri2 == null) throw new IllegalArgumentException(argStrings[3] + " does not exist");

            criteria.put(argStrings[0], Criterion.newBinaryCri(argStrings[0], cri1, cri2, argStrings[2]));
        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: newBinaryCri <name, cri1, cri2, binaryOperator>");
        }
    }

    /**
     * @param args User input argument String
     * @throws UsageException thrown when args is not an empty String
     */
    public void printAllCriteria(String args) throws UsageException {
        @SuppressWarnings("resource")
        Scanner sc = new Scanner(args);

        if (sc.hasNext()) throw new UsageException("Usage: printAllCriteria <>");

        String.format("%15s | %10s | %15s | %20s","name", "Attribute", "Operator", "Oprand");

        for (Criterion c : criteria.values()) {
            System.out.println(c.toString());
        }
    }


    /**
     * list all files in current working directory that satisfy the criterion specified
     * @param args User input argument String
     * @throws UsageException thrown when argument counts does not match
     * @throws IllegalArgumentException thrown when 1)criterion specified does not exist
     */
    public void search(String args) throws UsageException, IllegalArgumentException {

        try (Scanner sc = new Scanner(args)) {
            String criName = sc.next();
            Criterion cri = criteria.get(criName);
            if (cri == null) throw new IllegalArgumentException("Criterion '" + criName + "' does not exist");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");


            ArrayList<File> files = currentDisk.getcwd().getFiles();

            for (File f : files) {
                if (cri.assertCri(f)) System.out.println(f.toString());
            }

        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: search <criName>");
        }
    }

    /**
     * Recursively list all files in current working directory that satisfy the criterion specified
     * @param args User input argument String
     * @throws UsageException thrown when argument counts does not match
     * @throws IllegalArgumentException thrown when 1)criterion specified does not exist
     */
    public  void rSearch(String args) throws IllegalArgumentException, UsageException {

        try (Scanner sc = new Scanner(args)) {
            String criName = sc.next();
            Criterion cri = criteria.get(criName);
            if (cri == null) throw new IllegalArgumentException("Criterion '" + criName + "' does not exist");
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            ArrayList<File> files = currentDisk.getcwd().rGetFiles();

            for (File f : files) {
                if (cri.assertCri(f)) System.out.println(f.toString());
            }

        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: rSearch <criName>");
        }
    }


    private Path extendPath(Path path, String toAdd) {
        String pathString = path.toString();
        String[] paths = pathString.split("\\\\");
        String[] pathArray = new String[paths.length];
        if (paths.length - 1 >= 0) System.arraycopy(paths, 1, pathArray, 0, paths.length - 1);
        pathArray[pathArray.length-1] = toAdd;

        Path pathNew = FileSystems.getDefault().getPath(paths[0],pathArray);

        return pathNew;
    }

    private Path buildPath(String pathString) {
        String[] paths = pathString.split("\\\\");
        String[] pathArray = new String[paths.length-1];
        if (paths.length - 1 >= 0) System.arraycopy(paths, 1, pathArray, 0, paths.length - 1);
        Path path = FileSystems.getDefault().getPath(paths[0].trim(),pathArray);

        return path;
    }

    private void store(Directory dir, Path path) throws IOException {
        //create a directory in path
        path = extendPath(path, dir.getName());


        Files.createDirectory(path);


        ArrayList<File> files = dir.getFiles();

        for (File file : files) {
            if(file instanceof Document) {
                //this file is a document, write it
                BufferedWriter bw = Files.newBufferedWriter(extendPath(path,file.getName() + "." + ((Document)file).getDocumentType().name()));
                bw.write((String)file.getContent());
                bw.close();
            }

            else {
                //this file is a directory, call create

                store((Directory)file,path);
            }
        }
    }

    /**
     * Store the current Disk to Local File System
     * Only support Windows System
     * @param args User input argument String. A directory path is expected, where the files shall be stored
     *             A path should be in the format like "C:\\users\\Pubilc", or IOException shall be thrown
     * @throws UsageException thrown when argument count does not match
     * @throws IOException thrown when unexpected IO operation occurs
     */
    public  void store(String args) throws UsageException, IOException {

        try (Scanner sc = new Scanner(args)) {
            String input = sc.nextLine();
            Path path = buildPath(input);

            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            store(currentDisk.getRoot(), path);
        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: store <pathname>");
        }
    }

    private String getName(Path path) {
        String[] temp = path.toString().split("\\\\");
        return temp[temp.length-1];
    }

    private String getDocumentType(String extensionName) {
        String[] temp = extensionName.split("\\.");
        if (temp.length != 2) return "txt";
        else return extensionName.split("\\.")[1];
    }

    private String getContent(Path path) throws IOException {
        BufferedReader br = Files.newBufferedReader(path);
        StringBuilder sb = new StringBuilder();
        String line = br.readLine();
        while (line != null) {
            sb.append(line).append("\n");
            line = br.readLine();
        }
        return sb.toString();
    }

    private void load(Path path, Directory directory) throws IOException {
        DirectoryStream<Path> ds = Files.newDirectoryStream(path);
        ArrayList<String> validTypes = new ArrayList<String>();
        validTypes.add("txt");
        validTypes.add("html");
        validTypes.add("css");
        validTypes.add("java");

        for (Path p:ds) {
            if (Files.isDirectory(p)) {
                directory.newDir(getName(p));
                load(p,directory.findDir(getName(p)));
            }

            else {
                String extensionName = getName(p);
                String docType = getDocumentType(extensionName);
                if (!validTypes.contains(docType)) continue;
                directory.newDoc(extensionName.split("\\.")[0],getContent(p), docType);
            }
        }
    }

    /**
     * Load from local file System to Comp Vitural File System
     * Only support Windows System
     * @param args User input argument String. A path name is expected.
     *             A path should be in the format like "C:\\users\\Pubilc", or IOException shall be thrown
     * @throws UsageException thrown when argument count does not match
     * @throws IOException thrown when unexpected IO operation occurs
     */
    public void load(String args) throws UsageException, IOException {

        try (Scanner sc = new Scanner(args)) {
            String input = sc.nextLine();
            Path path = buildPath(input);
            if (currentDisk == null) throw new IllegalArgumentException("No disk found. Try command 'newDisk' first");

            load(path, currentDisk.getcwd());

        } catch (NoSuchElementException nsee) {
            throw new UsageException("Usage: load <pathname>");
        }
    }

    private Command string2Command(String commandString) {
        for(Command c : Command.values()) {
            if (c.name().compareTo(commandString) == 0) {
                return c;
            }
        }

        //never reached
        return null;
    }


    /**
     * Undo the last command
     * Currently only support undo for "newDir", "newDoc" and "rename"
     * @param args User input argument String. An empty String is expected
     * @throws UsageException thrown when argument count does not match
     */
    public void undo(String args) throws UsageException {

        Scanner scin = new Scanner(args);
        if (scin.hasNext()) throw new UsageException("Usage: undo <>");
        scin.close();

        Scanner sc = new Scanner(commandHistory.get(commandPtr));
        String command = sc.next();
        String first,second;

        switch(string2Command(command)) {
            case newDir:
            case newDoc:
                this.delete(sc.next());
                break;
            case rename:
                first = sc.next();
                second = sc.next();
                this.rename(second + " " + first);
                break;
            default:
                throw new UsageException("Unsupported");
        }

        redoStack.push(commandHistory.get(commandPtr));
        commandHistory.remove(commandPtr--);
    }

    /**
     * redo the last undid command
     * @param args User input argument String. An empty String is exptected
     * @throws UsageException thrown when argument count does not match
     * @throws MemoryException thrown when the operation cannot perform normally
     */
    @SuppressWarnings("resource")
    public void redo(String args) throws UsageException, MemoryException {
        Scanner scin = new Scanner(args);
        if (scin.hasNext()) throw new UsageException("Usage: undo <>");
        scin.close();

        if (redoStack.isEmpty()) throw new UsageException("No command history");
        Scanner sc = new Scanner(redoStack.pop());
        String command = sc.next();

        switch(string2Command(command)) {
            case newDir:
                this.newDir(sc.nextLine());
                break;
            case newDoc:
                this.newDoc(sc.nextLine());
                break;
            case rename:
                this.rename(sc.nextLine());
                break;
            default:
                throw new UsageException("Unsupported");
        }
    }

    public ArrayList<Disk> getDisks() {
        return disks;
    }
/*
    public void setDisks(ArrayList<Disk> disks) {
        this.disks = disks;
    }
*/
    public ArrayList<String> getCommandHistory() {
        return commandHistory;
    }
/*
    public void setCommandHistory(ArrayList<String> commandHistory) {
        this.commandHistory = commandHistory;
    }

    public ArrayList<String> getCanAddToHistory() {
        return canAddToHistory;
    }
    */
/*
    public void setCanAddToHistory(ArrayList<String> canAddToHistory) {
        this.canAddToHistory = canAddToHistory;
    }
*/
    public Disk getCurrentDisk() {
        return currentDisk;
    }
/*
    public void setCurrentDisk(Disk currentDisk) {
        this.currentDisk = currentDisk;
    }
*/
    public HashMap<String, Criterion> getCriteria() {
        return criteria;
    }
/*
    public void setCriteria(HashMap<String, Criterion> criteria) {
        this.criteria = criteria;
    }

    public LinkedList<String> getRedoStack() {
        return redoStack;
    }

    public void setRedoStack(LinkedList<String> redoStack) {
        this.redoStack = redoStack;
    }
    */

    public int getCommandPtr() {
        return commandPtr;
    }

    public void setCommandPtr(int commandPtr) {
        this.commandPtr = commandPtr;
    }
}
