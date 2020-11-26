package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;


import static org.junit.jupiter.api.Assertions.*;
import static java.time.Duration.ofMillis;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception.*;

import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.Iterator;

class DirectoryTest {

	Directory d0;
	Directory d1, d2;
	String[] invalidFileNames = {};
	String[] validFileNames = {};

	@BeforeAll
	static void constructorAndToStringTest() throws IllegalArgumentException, IllegalArgumentException {
		Directory testDir1 = new Directory("testDir1", null);
		assertEquals("[.]", testDir1.toString(1), "Directory should be initialized.");
		assertEquals("testDir1", testDir1.toString(), "Directory should be initialized.");
		Directory testDir2 = new Directory("testDir2", testDir1);
		assertEquals("[.,..]", testDir2.toString(1), "Directory should be initialized.");
		assertEquals("[.,testDir2]", testDir1.toString(1), "Parent directory should detect changes (in dirent)");
	}


	@BeforeEach
	void init() throws IllegalArgumentException, IllegalArgumentException {
		d0 = new Directory("root", null);
		d1 = new Directory("dir1", d0);
		d2 = new Directory("dir2", d1);
		/*
		System.out.println(d0);
		System.out.println(d1);
		System.out.println(d2);
		*/
	}
	@Test
	void IteratorTest(){
		Iterator<File> iterF =  d1.getContent().iterator();
		assertEquals(iterF.next().getName(),"dir1");
		assertEquals(iterF.next().getName(),"root");
		assertEquals(iterF.next().getName(),"dir2");

	}
	@Test
	void newDirTest() {
		try {
			int oriSize = d0.getSize();
			d2.newDir("testDir2");
			assertEquals("[.,..,testDir2]", d2.toString(1), "Directory should contain desired contents");
			assertEquals(oriSize + 40, d0.getSize(), "Sizes of parent directories should detect the change");
		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();
			assertEquals(1, 2, "No exception shall be thrown");
		}

	}

	@Test
	void newDocTest() {
		try {
			Document testDoc = new Document("testDoc", DocumentType.txt, "testDocContent");
			int d0InitSize = d0.getSize();
			d2.newDoc(testDoc.getName(), (String) testDoc.getContent(), testDoc.getDocumentType());
			assertEquals("[.,..,testDoc]", d2.toString(1), "Directory should contain desired contents");

			assertEquals(d0InitSize + testDoc.getSize(), d0.getSize(), "root should detect the change in size");

			assertThrows(IllegalArgumentException.class, () -> d2.newDoc("testDoc", "some other content", DocumentType.txt),
					"Cannot hava documents with same in one directory");
		} catch (Exception e) {
			assertEquals(1, 2, "No exception shall be thrown");
		}
	}
	@Test
	void newDocChangeNameTest(){
		String a = null;
		assertThrows(IllegalArgumentException.class,()->d2.newDoc("Pig","i am a pig",a));//check null
		int d0InitSize = d0.getSize();
		d2.newDoc("Pig","i am a pig","txt");
		assertEquals("[.,..,Pig]",d2.toString(1));
		assertEquals(d0InitSize+d2.findDoc("Pig").getSize(),d0.getSize());

	}

	@Test
	void deleteTest() {
		try {
			Document testDoc = new Document("testDoc", DocumentType.txt, "testDocContent");
			int d0InitSize = d0.getSize();
			d2.newDoc(testDoc.getName(), (String) testDoc.getContent(), testDoc.getDocumentType());
			d2.delete("testDoc");

			assertEquals("[.,..]", d2.toString(1), "File should be deleted");
			assertEquals(d0InitSize, d0.getSize(), "Sizes should be adjusted");
			assertThrows(IllegalArgumentException.class, () -> d2.delete("testDoc"), "Cannot delete a file that does not exist");
			assertThrows(IllegalArgumentException.class, () -> d2.delete("."), "Cannot delete '.'");
			assertThrows(IllegalArgumentException.class, () -> d2.delete(".."), "Cannot delete '..'");
		} catch (Exception e) {
			assertEquals(1, 2, "No exception shall be thrown");
		}
	}

	@Test
	void renameTest() {
		try {
			Document testDoc = new Document("testDoc", DocumentType.txt, "testDocContent");
			d2.newDoc(testDoc.getName(), (String) testDoc.getContent(), testDoc.getDocumentType());
			d2.rename("testDoc", "testDocRe");

			assertEquals("[.,..,testDocRe]", d2.toString(1), "file should be renamed");
			assertThrows(IllegalArgumentException.class, () -> d2.rename("nosuchfile", "nosuchname"),
					"Cannot rename a file that does not exist");
			assertThrows(IllegalArgumentException.class, () -> d2.rename("testDocRe", "Wrong_"),
					"Wrong input can't be accepted");
			assertThrows(IllegalArgumentException.class, () -> d2.rename("testDocRe", ""),
					"Wrong input can't be accepted");
			assertThrows(IllegalArgumentException.class, () -> d2.rename("testDocRe", "12312314431"),
					"Wrong input can't be accepted");
		} catch (Exception e) {
			e.printStackTrace();
			assertEquals(1, 2, "No exception shall be thrown");
		}
	}

	@Test
	//it should print contents it have in its level only.
	void listTest() {
		//assertTimeout(ofMillis(2000),() -> d1.list());
		try {
			ByteArrayOutputStream testOut = new ByteArrayOutputStream();
			PrintStream Oriout = System.out;
			System.setOut(new PrintStream(testOut));
			d2.newDoc("pig", "i hate you", DocumentType.txt);
			d2.newDir("rabbit");
			d2.findDir("rabbit").newDoc("pigHome", "this is my home", DocumentType.txt);
			d2.list();
			d2.findDir("rabbit").list();
			String expectedOut =
					"In directory dir2: \r\n" +
					"\tDocument: pig.txt | Size: 60\r\n" +
					"\tDirectory: rabbit | Size: 110\r\n" +
					"\r\n" +
					"Total number of files: 2\r\n" +
					"Total sizes of files: 170\r\n" +
					"In directory rabbit: \r\n" +
					"\tDocument: pigHome.txt | Size: 70\r\n" +
					"\r\n" +
					"Total number of files: 1\r\n" +
					"Total sizes of files: 70\r\n";
//			assertEquals(testOut.toString(),expectedOut);
			System.setOut(Oriout);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@Test
	// it should print all the contents it have (directory / document type)
	void rListTest() {
		//assertTimeout(ofMillis(2000),() -> d1.rList());
		try {
			ByteArrayOutputStream testOut = new ByteArrayOutputStream();
			PrintStream Oriout = System.out;
			System.setOut(new PrintStream(testOut));
			d2.newDoc("pig", "i hate you", DocumentType.txt);
			d2.newDir("rabbit");
			d2.findDir("rabbit").newDoc("pigHome", "this is my home", DocumentType.txt);
			d2.rList();
			String expectedOut = "In directory dir2:\n" +
					"\tDocument: pig.txt | Size: 60\n" +
					"\tDirectory: rabbit | Size: 110\n" +
					"\t\tDocument: pigHome.txt | Size: 70\n";

			assertEquals(testOut.toString(),expectedOut);
			System.setOut(Oriout);
		} catch (Exception e) {

		}
	}
}
