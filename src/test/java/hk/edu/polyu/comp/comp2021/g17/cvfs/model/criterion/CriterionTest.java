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
    Criterion criSize2;
    Criterion criSize3;
    Criterion criSize4;
    Criterion criSize5;


    Criterion isDocument;
    Criterion binCri1;
    Criterion binCri2;
    Criterion binCri3;
    Criterion binCri4;

    Criterion negCri1, negCri2, negCri3,negCri4, negCri5, negCri6,negCri7, negCri8, negCri9,negCri10, negCri11, negCri12;

    CriterionTest() {
        try {
            testDoc1 = new Document("testDoc1", DocumentType.txt, "A text file");
            testDoc2 = new Document("testDoc2", DocumentType.java, "A java file");
            testDoc3 = new Document("testDoc3", DocumentType.html, "A html file");
            testDoc4 = new Document("testDoc4", DocumentType.css, "A css file");
            testDir1 = new Directory("testDir1", null);

            criName = Criterion.newSimpleCri("cn", "name", "contains", "testDoc1");
            criSize = Criterion.newSimpleCri("sc", "size", "<=", "100");
            criType = Criterion.newSimpleCri("ct", "type", "equals", "txt");
            isDocument = Criterion.genIsDocument();

            //
            criName1 = Criterion.newSimpleCri("cn", "name", "contains", "test");
            criSize1 = Criterion.newSimpleCri("sc", "size", "<=", "70");
            criType1 = Criterion.newSimpleCri("ct", "type", "equals", "html");
            criSize2= Criterion.newSimpleCri("sc","size",">","60");
            criSize3= Criterion.newSimpleCri("sc","size","<","60");
            criSize4= Criterion.newSimpleCri("sc","size","==","62");
            criSize5=Criterion.newSimpleCri("sc","size","!=","100");

            binCri1 = Criterion.newBinaryCri("bc", criName, criSize, "AND");
            binCri2 = Criterion.newBinaryCri("bc", criName, criSize, "OR");

            negCri1 = Criterion.newNegation("nn", criName);
            negCri2 = Criterion.newNegation("nn", negCri1);
            negCri3 = Criterion.newNegation("nn", binCri1);
            negCri4= Criterion.newNegation("nn",criSize);
            negCri5= Criterion.newNegation("nn",criSize1);
            negCri6= Criterion.newNegation("nn",criSize2);
            negCri7= Criterion.newNegation("nn",criSize3);
            negCri8= Criterion.newNegation("nn",criSize4);
            negCri9= Criterion.newNegation("nn",criSize5);
            negCri10=Criterion.newNegation("nn",criType);
            negCri11=Criterion.newNegation("nn",negCri10);
            negCri12=Criterion.newNegation("nn",binCri2);

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
            //注意 改一下
            assertEquals(true, criName1.assertCri(testDoc2));
            System.out.println(criName1.toString());
            assertEquals(false, criType1.assertCri(testDoc2));
            System.out.println(criType1.toString());
            assertEquals(true, criSize1.assertCri(testDoc2));
            System.out.println(criSize1.toString());
            assertEquals(true, criName1.assertCri(testDoc3));
            System.out.println(criName1.toString());
            assertEquals(true, criType1.assertCri(testDoc3));
            System.out.println(criType1.toString());
            assertEquals(true, criSize1.assertCri(testDoc3));
            System.out.println(criSize1.toString());
            assertEquals(true, criName1.assertCri(testDoc4));
            System.out.println(criName1.toString());
            assertEquals(false, criType1.assertCri(testDoc4));
            System.out.println(criType1.toString());
            assertEquals(true, criSize1.assertCri(testDoc4));
            System.out.println(criSize1.toString());

            //
            assertEquals(true, criSize2.assertCri(testDoc1));
            System.out.println(criSize2.toString());
            assertEquals(true, criSize2.assertCri(testDoc2));
            System.out.println(criSize2.toString());
            assertEquals(true, criSize2.assertCri(testDoc3));
            System.out.println(criSize2.toString());
            assertEquals(false, criSize2.assertCri(testDoc4));
            System.out.println(criSize2.toString());
            assertEquals(false, criSize2.assertCri(testDir1));
            System.out.println(criSize2.toString());
            assertEquals(false, criSize3.assertCri(testDoc1));
            System.out.println(criSize3.toString());
            assertEquals(false, criSize3.assertCri(testDoc2));
            System.out.println(criSize3.toString());
            assertEquals(false, criSize3.assertCri(testDoc3));
            System.out.println(criSize3.toString());
            assertEquals(false, criSize3.assertCri(testDoc4));
            System.out.println(criSize3.toString());
            assertEquals(true, criSize3.assertCri(testDir1));
            System.out.println(criSize3.toString());
            assertEquals(true, criSize4.assertCri(testDoc1));
            System.out.println(criSize4.toString());
            assertEquals(true, criSize4.assertCri(testDoc2));
            System.out.println(criSize4.toString());
            assertEquals(true, criSize4.assertCri(testDoc3));
            System.out.println(criSize4.toString());
            assertEquals(false, criSize4.assertCri(testDoc4));
            System.out.println(criSize4.toString());
            assertEquals(false, criSize4.assertCri(testDir1));
            System.out.println(criSize4.toString());
            assertEquals(true, criSize5.assertCri(testDoc1));
            System.out.println(criSize5.toString());
            assertEquals(true, criSize5.assertCri(testDoc2));
            System.out.println(criSize5.toString());
            assertEquals(true, criSize5.assertCri(testDoc3));
            System.out.println(criSize5.toString());
            assertEquals(true, criSize5.assertCri(testDoc4));
            System.out.println(criSize5.toString());
            assertEquals(true, criSize5.assertCri(testDir1));
            System.out.println(criSize5.toString());


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
            assertEquals(false, negCri3.assertCri(testDoc1));
            System.out.println(negCri3.toString());
            assertEquals(false, negCri4.assertCri(testDoc1));
            System.out.println(negCri4.toString());
            assertEquals(false, negCri5.assertCri(testDoc1));
            System.out.println(negCri5.toString());
            assertEquals(false, negCri6.assertCri(testDoc1));
            System.out.println(negCri6.toString());
            assertEquals(true, negCri7.assertCri(testDoc1));
            System.out.println(negCri7.toString());
            assertEquals(false, negCri8.assertCri(testDoc1));
            System.out.println(negCri8.toString());
            assertEquals(false, negCri9.assertCri(testDoc1));
            System.out.println(negCri9.toString());
            assertEquals(false, negCri10.assertCri(testDoc1));
            System.out.println(negCri10.toString());
            assertEquals(true, negCri11.assertCri(testDoc1));
            System.out.println(negCri11.toString());
            assertEquals(false, negCri12.assertCri(testDoc1));
            System.out.println(negCri12.toString());







            assertEquals(true, negCri1.assertCri(testDoc2));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDoc2));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDoc2));
            System.out.println(negCri3.toString());
            assertEquals(false, negCri4.assertCri(testDoc2));
            System.out.println(negCri4.toString());
            assertEquals(false, negCri5.assertCri(testDoc2));
            System.out.println(negCri5.toString());
            assertEquals(false, negCri6.assertCri(testDoc2));
            System.out.println(negCri6.toString());
            assertEquals(true, negCri7.assertCri(testDoc2));
            System.out.println(negCri7.toString());
            assertEquals(false, negCri8.assertCri(testDoc2));
            System.out.println(negCri8.toString());
            assertEquals(false, negCri9.assertCri(testDoc2));
            System.out.println(negCri9.toString());
            assertEquals(true, negCri10.assertCri(testDoc2));
            System.out.println(negCri10.toString());
            assertEquals(false, negCri11.assertCri(testDoc2));
            System.out.println(negCri11.toString());
            assertEquals(false, negCri12.assertCri(testDoc2));
            System.out.println(negCri12.toString());





            assertEquals(true, negCri1.assertCri(testDoc3));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDoc3));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDoc3));
            System.out.println(negCri3.toString());
            assertEquals(false, negCri4.assertCri(testDoc3));
            System.out.println(negCri4.toString());
            assertEquals(false, negCri5.assertCri(testDoc3));
            System.out.println(negCri5.toString());
            assertEquals(false, negCri6.assertCri(testDoc3));
            System.out.println(negCri6.toString());
            assertEquals(true, negCri7.assertCri(testDoc3));
            System.out.println(negCri7.toString());
            assertEquals(false, negCri8.assertCri(testDoc3));
            System.out.println(negCri8.toString());
            assertEquals(false, negCri9.assertCri(testDoc3));
            System.out.println(negCri9.toString());
            assertEquals(true, negCri10.assertCri(testDoc3));
            System.out.println(negCri10.toString());
            assertEquals(false, negCri11.assertCri(testDoc3));
            System.out.println(negCri11.toString());
            assertEquals(false, negCri12.assertCri(testDoc3));
            System.out.println(negCri12.toString());



            assertEquals(true, negCri1.assertCri(testDoc4));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDoc4));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDoc4));
            System.out.println(negCri3.toString());
            assertEquals(false, negCri4.assertCri(testDoc4));
            System.out.println(negCri4.toString());
            assertEquals(false, negCri5.assertCri(testDoc4));
            System.out.println(negCri5.toString());
            assertEquals(true, negCri6.assertCri(testDoc4));
            System.out.println(negCri6.toString());
            assertEquals(true, negCri7.assertCri(testDoc4));
            System.out.println(negCri7.toString());
            assertEquals(true, negCri8.assertCri(testDoc4));
            System.out.println(negCri8.toString());
            assertEquals(false, negCri9.assertCri(testDoc4));
            System.out.println(negCri9.toString());
            assertEquals(true, negCri10.assertCri(testDoc4));
            System.out.println(negCri10.toString());
            assertEquals(false, negCri11.assertCri(testDoc4));
            System.out.println(negCri11.toString());
            assertEquals(false, negCri12.assertCri(testDoc4));
            System.out.println(negCri12.toString());


            assertEquals(true, negCri1.assertCri(testDir1));
            System.out.println(negCri1.toString());
            assertEquals(false, negCri2.assertCri(testDir1));
            System.out.println(negCri2.toString());
            assertEquals(true, negCri3.assertCri(testDir1));
            System.out.println(negCri3.toString());
            assertEquals(false, negCri4.assertCri(testDir1));
            System.out.println(negCri4.toString());
            assertEquals(false, negCri5.assertCri(testDir1));
            System.out.println(negCri5.toString());
            assertEquals(true, negCri6.assertCri(testDir1));
            System.out.println(negCri6.toString());
            assertEquals(false, negCri7.assertCri(testDir1));
            System.out.println(negCri7.toString());
            assertEquals(true, negCri8.assertCri(testDir1));
            System.out.println(negCri8.toString());
            assertEquals(false, negCri9.assertCri(testDir1));
            System.out.println(negCri9.toString());
            assertEquals(false, negCri12.assertCri(testDir1));
            System.out.println(negCri12.toString());
        }catch(Exception e) {
            e.printStackTrace();
        }

    }


    @Test
    void throwInvalidArgumentTest() {
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("notAValidName","name","contains","ah"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","name","equals","ah"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","size","contains","ah"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","size",">=","notanumber"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("IsDocument","size",">=","100"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newBinaryCri("b1",criName,criSize,"AND"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newBinaryCri("bb",criName,criType,">"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newBinaryCri("bb",binCri1,isDocument,"Or"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newNegation("c1",criName));

        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","filetype","equals","txt"));
        assertThrows(IllegalArgumentException.class, () -> Criterion.newSimpleCri("aa","size","E","110.7"));
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
