package hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion;


import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.*;


class CriterionTest {

    Document testDoc1;
    Document testDoc2;
    Document testDoc3;
    Document testDoc4;
    Directory testDir1;
    Criterion criName;
    Criterion criType;
    Criterion criSize;
    Criterion criName1;
    Criterion criType1;
    Criterion criSize1;

    Criterion isDocument;
    Criterion binCri1;
    Criterion binCri2;
    Criterion binCri3;
    Criterion binCri4;

    Criterion negCri1, negCri2, negCri3;

    CriterionTest() {
        try {
            testDoc1 = new Document("testDoc1", DocumentType.txt, "A text file");
            testDoc2 = new Document("testDoc2", DocumentType.java, "A java file");
            testDoc3 = new Document("testDoc3", DocumentType.html, "A html file");
            testDoc4 = new Document("testDoc4", DocumentType.css, "A css file");
            testDir1 = new Directory("testDir1", null);

            criName = Criterion.newSimpleCri("cn", "name", "contains", "testDoc1");
            criSize = Criterion.newSimpleCri("sc", "size", "<=", "100");
            criType = Criterion.newSimpleCri("ct", "type", "==", "txt");
            isDocument = Criterion.genIsDocument();

            //
            criName1 = Criterion.newSimpleCri("cn", "name", "contains", "test");
            criSize1 = Criterion.newSimpleCri("sc", "size", "<=", "70");
            criType1 = Criterion.newSimpleCri("ct", "type", "equals", "html");

            binCri1 = Criterion.newBinaryCri("bc", criName, criSize, "AND");
            binCri2 = Criterion.newBinaryCri("bc", criName, criSize, "OR");

            negCri1 = Criterion.newNegation("nn", criName);
            negCri2 = Criterion.newNegation("nn", negCri1);
            negCri3 = Criterion.newNegation("nn", binCri1);

            //
            binCri3=Criterion.newBinaryCri("bc", criType, isDocument,"AND");
            binCri4=Criterion.newBinaryCri("bc",criSize,negCri1,"AND");

        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(1)
    void simpleAssertTest() {
        try {
            assertEquals(true, criName.assertCri(testDoc1));
            System.out.println(criName.toString());
            assertEquals(true, criType.assertCri(testDoc1));
            System.out.println(criType.toString());
            assertEquals(true, criSize.assertCri(testDoc1));
            System.out.println(criSize.toString());
            assertEquals(false, isDocument.assertCri(testDir1));
            System.out.println(isDocument.toString());

            //
            assertEquals(true, criName1.assertCri(testDoc2));
            System.out.println(criName.toString());
            assertEquals(false, criType1.assertCri(testDoc2));
            System.out.println(criType.toString());
            assertEquals(true, criSize1.assertCri(testDoc2));
            System.out.println(criSize.toString());
            assertEquals(true, criName1.assertCri(testDoc3));
            System.out.println(criName.toString());
            assertEquals(true, criType1.assertCri(testDoc3));
            System.out.println(criType.toString());
            assertEquals(true, criSize1.assertCri(testDoc3));
            System.out.println(criSize.toString());
            assertEquals(true, criName1.assertCri(testDoc4));
            System.out.println(criName.toString());
            assertEquals(false, criType1.assertCri(testDoc4));
            System.out.println(criType.toString());
            assertEquals(true, criSize1.assertCri(testDoc4));
            System.out.println(criSize.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Test
    @Order(2)
    void binAssertTest() {
        try {
            assertEquals(false, binCri1.assertCri(testDoc2));
            System.out.println(binCri1.toString());
            assertEquals(true, binCri2.assertCri(testDoc2));
            System.out.println(binCri2.toString());

            //
            //
            assertEquals(true, binCri1.assertCri(testDoc1));
            System.out.println(binCri1.toString());
            assertEquals(true, binCri2.assertCri(testDoc1));
            System.out.println(binCri2.toString());
            assertEquals(true, binCri3.assertCri(testDoc1));
            System.out.println(binCri1.toString());
            assertEquals(false, binCri4.assertCri(testDoc1));
            System.out.println(binCri2.toString());
            assertEquals(false, binCri1.assertCri(testDoc2));
            System.out.println(binCri1.toString());
            assertEquals(true, binCri2.assertCri(testDoc2));
            System.out.println(binCri2.toString());
            assertEquals(false, binCri3.assertCri(testDoc2));
            System.out.println(binCri3.toString());
            assertEquals(true, binCri4.assertCri(testDoc2));
            System.out.println(binCri4.toString());
            assertEquals(false, binCri1.assertCri(testDoc3));
            System.out.println(binCri1.toString());
            assertEquals(true, binCri2.assertCri(testDoc3));
            System.out.println(binCri2.toString());
            assertEquals(false, binCri3.assertCri(testDoc3));
            System.out.println(binCri3.toString());
            assertEquals(true, binCri4.assertCri(testDoc3));
            System.out.println(binCri4.toString());
            assertEquals(false, binCri1.assertCri(testDoc4));
            System.out.println(binCri1.toString());
            assertEquals(true, binCri2.assertCri(testDoc4));
            System.out.println(binCri2.toString());
            assertEquals(false, binCri3.assertCri(testDoc4));
            System.out.println(binCri3.toString());
            assertEquals(true, binCri4.assertCri(testDoc4));
            System.out.println(binCri4.toString());
            assertEquals(false, binCri1.assertCri(testDir1));
            System.out.println(binCri1.toString());
            assertEquals(true, binCri2.assertCri(testDir1));
            System.out.println(binCri2.toString());
            assertEquals(false, binCri3.assertCri(testDir1));
            System.out.println(binCri3.toString());
            assertEquals(true, binCri4.assertCri(testDir1));
            System.out.println(binCri4.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

    @Test
    @Order(3)
    void negAssertTest() {
        try {
            assertEquals(false, negCri1.assertCri(testDoc1));
            System.out.println(negCri1.toString());
            assertEquals(true, negCri2.assertCri(testDoc1));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDoc2));
            System.out.println(negCri3.toString());
            //
            assertEquals(false, negCri3.assertCri(testDoc1));
            System.out.println(negCri3.toString());
            assertEquals(true, negCri1.assertCri(testDoc2));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDoc2));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri1.assertCri(testDoc3));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDoc3));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDoc3));
            System.out.println(negCri3.toString());
            assertEquals(true, negCri1.assertCri(testDoc4));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDoc4));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDoc4));
            System.out.println(negCri3.toString());
            assertEquals(true, negCri1.assertCri(testDir1));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDir1));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDir1));
            System.out.println(negCri3.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    void throwIllegalArgumentTest() {
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("notAValidName","name","contains","ah"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","name","equals","ah"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","size","contains","ah"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","size","GE","notanumber"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("IsDocument","size","GE","100"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newBinaryCri("b1",criName,criSize,"AND"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newBinaryCri("bb",criName,criType,">"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newBinaryCri("bb",binCri1,isDocument,"Or"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newNegation("c1",criName));
    }

    @Test
    void toStringTest(){
        assertEquals("             cn |       name |        contains |             testDoc1",criName.toString());
        assertEquals("             sc |       size |              LE |                  100",criSize.toString());
        assertEquals("             ct |       type |          equals |                  txt",criType.toString());
    }

    @Test
    void isDocumentAssertTest(){
        try{
            assertTrue(isDocument.assertCri(testDoc1));
            System.out.println(isDocument.toString());
            assertFalse(isDocument.assertCri(testDir1));
            System.out.println(isDocument.toString());
        }
        catch (Exception e){
            e.printStackTrace();
        }
    }
}
