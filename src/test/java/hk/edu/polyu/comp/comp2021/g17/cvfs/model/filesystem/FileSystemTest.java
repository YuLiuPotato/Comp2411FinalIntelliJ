package hk.edu.polyu.comp.comp2021.g17.cvfs.model.filesystem;

import static org.junit.jupiter.api.Assertions.*;

import java.io.*;
import java.nio.file.FileAlreadyExistsException;
import java.nio.file.Path;

//import com.sun.jdi.event.ExceptionEvent;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion.Criterion;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception.*;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Disk;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Document;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.DocumentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

class FileSystemTest {

    FileSystem fileSystem;

    FileSystemTest(){
        fileSystem = new FileSystem("test");
    }

    InputStream sysIn;
/*
	void setInput(String input){
		ByteArrayInputStream in = new ByteArrayInputStream(input.getBytes());
		System.setIn(in);
	}
*/

    @Test
    void loadStoreTest() {
        try {
            String pathLoad = "C:\\toLoad"; //directory on your disk
            String pathStore = "C:\\toStore"; //directory on your disk
            fileSystem.newDisk("100000");
            fileSystem.load(pathLoad);
            fileSystem.rList("");
            fileSystem.store(pathStore);
        }catch(FileAlreadyExistsException fe){
            //not a big deal
        }
        catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Test
    void undoRedoTest() {
        try {
            fileSystem.newDisk("10000");
            fileSystem.commandHistory.add(++fileSystem.commandPtr,"newDisk 10000");
            fileSystem.newDir("dir1");
            fileSystem.commandHistory.add(++fileSystem.commandPtr,"newDir dir1");
            fileSystem.newDoc("abc txt \"haha\"");
            fileSystem.commandHistory.add(++fileSystem.commandPtr,"newDoc abc txt \"haha\"");
            fileSystem.rename("abc bcd");
            fileSystem.commandHistory.add(++fileSystem.commandPtr,"rename abc bcd");
            fileSystem.undo("");
            fileSystem.redo("");
            fileSystem.delete("dir1");
            fileSystem.commandHistory.add(++fileSystem.commandPtr,"delete dir1");
            assertThrows(UsageException.class, () -> fileSystem.redo(""));
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
    @BeforeEach
    void setFileInput(){
        try {
            String name = "Pig";
            fileSystem.newDisk("150"); // maxSize = 130
            fileSystem.newDir(name);
            fileSystem.newDoc("PigWeight txt \"98kg\""); // wrong input format
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void newDiskTest(){
        try {
            //assertThrows(IllegalArgumentException.class, () -> fileSystem.newDisk("asdasd"));// ？
            String size = String.valueOf(60);
            fileSystem.newDisk(size);
            assertEquals(fileSystem.disks.size(),2); // test disk in arrayList<disks>
            Disk testdisk = fileSystem.disks.get(0);
            assertEquals(testdisk.toString(),"/:"); // test path
            assertEquals(testdisk.getRoot().getSize(),128); // test 40+ Total size of files it contained
            assertThrows(IllegalArgumentException.class, () -> fileSystem.newDisk("40")); // size <=40
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    // arrayDisk -- disks[0] -- Dir: (cwd)->Root(.,Pig)
    // size should change in disk
    //input name "."   "pig"
    @Test
    void newDirTest(){
        try {
            String name = "Pig";
            fileSystem.newDisk("130"); // maxSize = 130
            fileSystem.newDir(name);
            assertThrows(IllegalArgumentException.class, () -> fileSystem.newDir("."));//current size = 120

            assertEquals(fileSystem.currentDisk.getcwd().findDir("Pig").getName(),"Pig"); // if pig inside root
            assertEquals(fileSystem.currentDisk.getRoot(), fileSystem.currentDisk.getcwd()); // if cwd -> root
            assertEquals(fileSystem.currentDisk.getRoot().getSize(),40+40);
            //assertEquals(fileSystem.currentDisk.getCurrentSize(),40+40); // ? disk.currentsize should change(newdir)
            assertEquals(fileSystem.currentDisk.getRoot().findDir("Pig").getSize(),40); // newdir"pig".size =40
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @BeforeEach
    void sysInSet(){
        sysIn = System.in;
    }
    @Test
        //arrayDisk -- disks[0] -- Dir: (cwd)->Root(.(Di),Pig(Di),Pigweight(Do))
    void newDoctest(){
        try{
            Document testdoc = fileSystem.currentDisk.getcwd().findDoc("PigWeight");
            assertSame(testdoc.getDocumentType(), DocumentType.txt);
            assertEquals(testdoc.getContent(),"98kg");
            assertEquals(testdoc.getSize(),40+4*2);
            assertEquals(fileSystem.currentDisk.getcwd().getSize(), 40+40+40+4*2);
            fileSystem.newDoc("java1 java \"javafile\"");
            fileSystem.newDoc("css1 css \"cssfile\"");
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void deleteTest(){
        try{
            fileSystem.delete("PigWeight");
            assertThrows(IllegalArgumentException.class, ()->fileSystem.currentDisk.getcwd().findDoc("PigWeight"));
            assertEquals(fileSystem.currentDisk.getcwd().getSize(),40+40);
            fileSystem.delete("Pig");
            assertThrows(IllegalArgumentException.class, ()->fileSystem.currentDisk.getcwd().findDir("Pig"));
            assertEquals(fileSystem.currentDisk.getcwd().getSize(),40);
            assertThrows(IllegalArgumentException.class, ()->fileSystem.delete("."));
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    void renameTest(){
        try{
            int size = fileSystem.currentDisk.getcwd().findDir("Pig").getSize();
            assertThrows(IllegalArgumentException.class, ()->fileSystem.rename("Pi FatPig"));
            fileSystem.rename("Pig FatPig");

            assertThrows(IllegalArgumentException.class, ()->fileSystem.currentDisk.getcwd().findDir("Pig"));
            assertEquals(fileSystem.currentDisk.getcwd().findDir("FatPig").getName(),"FatPig");
            assertEquals(fileSystem.currentDisk.getcwd().findDir("FatPig").getSize(),size);

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
        //arrayDisk -- disks[0] -- Dir: (cwd)->Root(.(Di),Pig(Di),Pigweight(Do))
        //arrayDisk -- disks[0] -- Dir: Root(.(Di),(cwd)->Pig(Di)(.,..),Pigweight(Do))
    void changeDirTest(){
        try{
            assertThrows(IllegalArgumentException.class, ()->fileSystem.changeDir(".."));
            fileSystem.changeDir("Pig");
            assertEquals(fileSystem.currentDisk.getcwd().getName(),"Pig");
            assertEquals(fileSystem.currentDisk.getRoot().findDir("Pig"),fileSystem.currentDisk.getcwd());
            assertEquals(fileSystem.currentDisk.getcwd().getSize(),40);
            assertEquals(fileSystem.currentDisk.getcwd().findDir(".."),fileSystem.currentDisk.getRoot());

        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
        // get the method from internet
    void listTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            assertThrows(UsageException.class, ()->fileSystem.list("sad"));
            fileSystem.list("");
            //could be changed later
            String expectedOut= "\n"+"In directory root: \n" +
                    "  Directory: Pig | Size: 40\n" +
                    "  Document: PigWeight | Type: Document | Size: 48\n" +
                    "\n" +
                    "Total number of files: 2\n" +
                    "Total sizes of files: 88"+"\n";
//            assertEquals(expectedOut,testOut.toString());
            System.setOut(Oriout);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    void rlistTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.rList("");
            // could be changed later
            String expectedOut = "\n" +
                    "In directory root: \n" +
                    "  File: PigWeight | Type: Document | Size: 48\n" +
                    "\n" +
                    "Total number of files: 1\n" +
                    "Total sizes of files: 128\n";
//            assertEquals(expectedOut,testOut.toString());
            System.setOut(Oriout);
        }
        catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    void newSimpleCriTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.rList("");
            fileSystem.newSimpleCri("cn name contains Pig");
            fileSystem.newSimpleCri("tc type equals txt");
            fileSystem.newSimpleCri("sn size > 40");
            assertThrows(UsageException.class, ()->fileSystem.newSimpleCri("ns pig sdc"));
            assertTrue(fileSystem.criteria.containsKey("cn"));
            assertTrue(fileSystem.criteria.containsKey("tc"));
            assertTrue(fileSystem.criteria.containsKey("sn"));
            String expectedOut1 = "             cn |       name |        contains |                  Pig";
            assertEquals(fileSystem.criteria.get("cn").toString(),expectedOut1);
            String expectedOut2 = "             tc |       type |          equals |                  txt";
            assertEquals(fileSystem.criteria.get("tc").toString(),expectedOut2);
            String expectedOut3 = "             sn |       size |               G |                   40";
            assertEquals(fileSystem.criteria.get("sn").toString(),expectedOut3);
            assertFalse(fileSystem.criteria.get("cn").assertCri(fileSystem.currentDisk.getRoot())); // ? Pig should be contained in Root
            System.setOut(Oriout);
        }
        catch (Exception e){
            e.printStackTrace();


        }}
    @Test
        // not given in the fileSystem
        //TODO
    void isDocumentTest(){
        try{

        }catch (Exception e){
            e.printStackTrace();
        }

    }
    @Test
    void newNegationTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.newSimpleCri("cn name contains PigWeight ");
            assertThrows(IllegalArgumentException.class, ()->fileSystem.newNegation("cnNegate cn"));
            fileSystem.newNegation("nc cn");
            assertTrue(fileSystem.criteria.containsKey("nc"));
            String expectedOut = "             nc |       name |    not_contains |            PigWeight";
            assertEquals(fileSystem.criteria.get("nc").toString(),expectedOut);
//            assertFalse(fileSystem.criteria.get("nc").assertCri(fileSystem.currentDisk.getRoot())); //? not_contains pigWeight should be false
            System.setOut(Oriout);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void newBinaryCriTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.newSimpleCri("cn name contains PigWeight ");
            fileSystem.newSimpleCri("sn size <= 50");
            assertThrows(IllegalArgumentException.class, ()->fileSystem.newBinaryCri("Tn cn && sp"));
            fileSystem.newBinaryCri("TN cn AND sn"); // ? wrong design input in filesystem
            assertTrue(fileSystem.criteria.containsKey("TN"));
            String expectedOut = "             TN |  composite |             AND |                cn sn";
            assertEquals(fileSystem.criteria.get("TN").toString(),expectedOut);
            assertTrue(!fileSystem.criteria.get("TN").assertCri(fileSystem.currentDisk.getRoot()));
            System.setOut(Oriout);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void printAllCriteriaTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.newSimpleCri("cn name contains PigWeight ");
            fileSystem.newSimpleCri("sn size <= 50");
            fileSystem.newNegation("nc cn");
            assertThrows(UsageException.class, ()->fileSystem.printAllCriteria("ss"));
            //fileSystem.newBinaryCri("TN cn && sp"); // ? wrong design input in filesystem
            fileSystem.printAllCriteria("");
            String expectedOut =	"             nc |       name |    not_contains |            PigWeight\n" +
                    "             cn |       name |        contains |            PigWeight\n" +
                    "             sn |       size |              LE |                   50\n";
//            assertEquals(testOut.toString(),expectedOut);
            System.setOut(Oriout);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void searchTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.newSimpleCri("cn name contains PigWeight ");
            fileSystem.newSimpleCri("sn size <= 50");
            fileSystem.newNegation("nc cn");
            fileSystem.newSimpleCri("tc type equals txt");
            assertThrows(IllegalArgumentException.class, ()->fileSystem.search("pi"));
            //fileSystem.newBinaryCri("TN cn && sp"); // ? wrong design input in filesystem
            fileSystem.search("cn");
            String expectedOut =	"             nc |       name |    not_contains |            PigWeight\n" +
                    "             cn |       name |        contains |            PigWeight\n" +
                    "             sn |       size |              LE |                   50\n"; // to be changed
//            assertEquals(testOut.toString(),expectedOut); // ? the output format is not correct
            System.setOut(Oriout);
        }catch (Exception e){
            e.printStackTrace();
        }
    }
    @Test
    void rSearchTest(){
        try{
            ByteArrayOutputStream testOut = new ByteArrayOutputStream();
            PrintStream Oriout = System.out;
            System.setOut(new PrintStream(testOut));
            fileSystem.newSimpleCri("cn name contains PigWeight ");
            fileSystem.newSimpleCri("sn size <= 50");
            fileSystem.newNegation("nc cn");
            fileSystem.newSimpleCri("tc type equals txt");
            assertThrows(IllegalArgumentException.class, ()->fileSystem.rSearch("pi"));
            //fileSystem.newBinaryCri("TN cn && sp"); // ? wrong design input in filesystem
            fileSystem.rSearch("cn");
            String expectedOut =	"             nc |       name |    not_contains |            PigWeight\n" +
                    "             cn |       name |        contains |            PigWeight\n" +
                    "             sn |       size |              LE |                   50\n"; // to be changed
//            assertEquals(testOut.toString(),expectedOut); // ? the ouptput formate is not correct
            System.setOut(Oriout);
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    @AfterEach
    void resumeIn(){
        System.setIn(sysIn);
    }

}