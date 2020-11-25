package hk.edu.polyu.comp.comp2021.g17.cvfs.model.file;

import static java.time.Duration.ofMillis;
import static org.junit.jupiter.api.Assertions.*;


import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;

import hk.edu.polyu.comp.comp2021.g17.cvfs.model.exception.MemoryException;


class DiskTest {
	
	Disk disk = new Disk(1000);
	
	@Test
	@Order(1)
	void constructorAndToStringTest() {
		try {
			disk = new Disk(1000);
			assertEquals("/:",disk.toString(),"Disk now should only contain root directory");
			assertThrows(IllegalArgumentException.class, () -> new Disk(39),
					"Invalid Argument should be thrown when disk size less than 40 is attempted.");
		} catch (Exception e) {
			assertEquals(1,2,"This exception should not be triggered.");
		}
		
	}
	
	@Test
	@Order(2)
	void newDirTest() {
		try {
			disk.newDir("dir1");
		}catch(Exception e) {
			e.printStackTrace();
			assertEquals(1,2,"This exception should not be triggered.");
		}
	}
	
	@Test
	@Order(3)
	void newDocTest() {
		try {
			disk.newDoc("doc1",DocumentType.txt, "Something is written.");
		}catch(Exception e) {
			assertEquals(1,2,"This exception should not be triggered.");
		}
	}
	
	@Test
	@Order(4)
	void changeDirTest() {
		try {
			disk.newDir("dir1");
			assertThrows(IllegalArgumentException.class, () -> disk.changeDir("Doc1"),
					"IllegalArgumentException shall be thrown when changing to a Document is attempted");
			disk.changeDir("dir1");
			assertEquals("/:dir1:", disk.toString(), "Should change to dir1");
			disk.changeDir("..");
			assertEquals("/:", disk.toString(), "Should change to root");
			disk.changeDir(".");
			assertEquals("/:", disk.toString(), "Should change to root");
		}catch(Exception e) {
			e.printStackTrace();
			assertEquals(1,2,"This exception should not be triggered.");
		}
		
	}
	
	@Test
	@Order(5)
	void deleteTest() {
		try {
			disk.newDoc("doc1",DocumentType.txt, "Something is written.");
			disk.delete("doc1");
			assertThrows(IllegalArgumentException.class, () -> disk.delete("doc2"),
					"IllegalArgumentException should be thrown");
			disk.newDoc("doc1", DocumentType.txt, "content goes here");
		}catch(Exception e) {
			e.printStackTrace();
			assertEquals(1,2,"This exception should not be triggered.");
		}
	}
	
	@Test
	@Order(6)
	void renameTest() {
		try {
			disk.newDoc("doc1",DocumentType.txt, "Something is written.");
			disk.rename("doc1","newdoc1");
			disk.rename("newdoc1", "doc1");
			assertThrows(IllegalArgumentException.class, () -> disk.rename("newdoc1", "doc1"),
					"IllegalArgumentException should be thrown when renaming non-existant file is attempted");
		}catch(Exception e) {
			e.printStackTrace();
			assertEquals(1,2,"This exception should not be triggered.");
		}
	}
	
	
	@Test
	@Order(7)
	void listTest() {
		assertTimeout(ofMillis(2000),() -> disk.list());
		assertTimeout(ofMillis(2000),() -> disk.rList());
	}
	
	@Test
	@Order(8)
	void memoryExceptionTest() {
		try {
			Disk smallDisk = new Disk(41);
			assertThrows(MemoryException.class, () -> smallDisk.newDir("dir1"));
			assertThrows(MemoryException.class, () -> smallDisk.newDoc("doc1",DocumentType.txt,"safas"));
		} catch (Exception e) {
			assertEquals(1,2,"This exception should not be triggered.");
		}
	}
	
}