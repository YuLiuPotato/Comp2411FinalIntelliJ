package hk.edu.polyu.comp.comp2021.g17.cvfs.model.criterion;


import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.DocumentType;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.File;
import hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.FileType;

import java.util.ArrayList;
import java.util.Arrays;

/**
 * This class represents Criterion.
 * Criterion asserts <code>File</code>
 * @see File
 */
public abstract class Criterion{
    private String name;

    /**
     * Getter Method
     * @return name of this Criterion
     */
    public String getName(){
        return name;
    }
    /**
     * Getter Method
     * @return AttrName of this Criterion
     */
    public AttrName getAttrname() {
        return attrname;
    }

    /**
     * Getter Method
     * @return Operator of this Criterion
     */
    public Op getOp() {
        return op;
    }

    /**
     * Getter Method
     * @return Operator of this Criterion
     */
    public Object getVal() {
        return val;
    }

    private AttrName attrname;
    private Op op;
    private Object val;


    /**
     * Constructor called by the subclasses
     * @see SizeCri
     * @see NameCri
     * @see TypeCri
     * @param name name of this Criterion
     * @param attrname attribute of this Criterion
     * @param op operator used in this Criterion
     * @param val operand used in this Criterion
     * @throws IllegalArgumentException thrown when 1) name is not a valid name(a Criterion name can only be two letters)
     * 2)combination of parameters does not yield a valid Criterion
     *
     * @see Op
     * @see AttrName
     */
    protected Criterion(String name, AttrName attrname, Op op, Object val) throws IllegalArgumentException {
        if (!isValidCriName(name) || !isValidCri(attrname, op, val)) throw new IllegalArgumentException("Invalid argument combination is passed when creating criterion");
        this.name = name;
        this.attrname = attrname;
        this.op = op;
        this.val = val;
    }

    private boolean isLetter(char character) {
        return ((int) character >= (int) 'A' && (int) character <= (int) 'Z') || ((int) character >= (int) 'a' && (int) character <= (int) 'z');
    }

    private boolean isValidCriName(String name) {
        if (name.compareTo("isDocument") == 0) return true;
        if (name.length() == 2) {
            return isLetter(name.charAt(0)) && isLetter(name.charAt(1));
        }

        return false;
    }

    private boolean isValidCri(AttrName an, Op op, Object val) {
        ArrayList<Op> sizeOps = new ArrayList<Op>(Arrays.asList(Op.G, Op.GE, Op.L, Op.LE, Op.E, Op.NE));
        ArrayList<Op> nameOps = new ArrayList<Op>(Arrays.asList(Op.contains, Op.not_contains));
        ArrayList<Op> typeOps = new ArrayList<Op>(Arrays.asList(Op.equals, Op.not_equals));
        ArrayList<Op> compOps = new ArrayList<Op>((Arrays.asList(Op.AND, Op.OR, Op.NOT_AND, Op.NOT_OR)));

        switch (an) {

            case size:
                return sizeOps.contains(op) && val instanceof Integer;
            case name:
                return nameOps.contains(op) && val instanceof String;
            case type:
            case filetype:
                return typeOps.contains(op) && (val instanceof DocumentType || val instanceof FileType);
            default:
                return compOps.contains(op) && val instanceof String;
        }

       
    }

    /**
     * Assert on file to check if the file meets this Criterion
     * @param file <code>File</code> to be checked against
     * @return true if the file meets this Criterion, false otherwise
     */
    public abstract boolean assertCri(File file);

    private static Op string2Op(String sop) throws IllegalArgumentException {
        Op result = null;

        for (Op op : Op.values()) {
            if (op.name().compareTo(sop) == 0) {
                result = op;
                break;
            }
        }

        if (result == null) throw new IllegalArgumentException("Invalid Operator " + sop);

        return result;
    }

    private static DocumentType string2Type(String stype) throws IllegalArgumentException {
        DocumentType result = null;

        for (DocumentType t : DocumentType.values()) {
            if (t.name().compareTo(stype) == 0) {
                result = t;
                break;
            }
        }

        if (result == null) throw new IllegalArgumentException("Invalid Document Type " + stype);

        return result;
    }

    /**
     * Create a simple Criterion
     * This Criterion can only be NameCri, TypeCri or SizeCri
     * @param name name of the Criterion
     * @param attrname String representation of Attribute of the Criterion
     * @param op String representation of Operator used in this Criterion
     * @param val String representation of Oprand used in this Criterion
     * @return the Criterion constructed
     * @throws IllegalArgumentException thrown when 1)name is illegal 2)argument combination is illegal
     *
     * @see NameCri
     * @see SizeCri
     * @see TypeCri
     */
    public static Criterion newSimpleCri(String name, String attrname, String op, String val) throws IllegalArgumentException{

        AttrName attr = null;

        for (AttrName an : AttrName.values()) {
            if (an.name().compareTo(attrname) == 0) {
                attr = an;
                break;
            }
        }

        if (attr == null) throw new IllegalArgumentException("No such attrbute name: " + attrname);

        switch (attr) {

            case name:
                if (op.compareTo("contains") != 0)
                    throw new IllegalArgumentException("Operator must be 'contains'");
                return new NameCri(name, val);

            case size:
                if (op.trim().compareTo(">") == 0) op = "G";
                else if (op.trim().compareTo(">=") == 0) op = "GE";
                else if (op.trim().compareTo("<") == 0) op = "L";
                else if (op.trim().compareTo("<=") == 0) op = "LE";
                else if (op.trim().compareTo("==") == 0) op = "E";
                else if (op.trim().compareTo("!=") == 0) op = "NE";
                else throw new IllegalArgumentException("No such operator: " + op);

                try {
                    return new SizeCri(name, string2Op(op), Integer.parseInt(val));
                }catch(NumberFormatException nfe) {
                    throw new IllegalArgumentException(val + " is not a number");
                }

            case type:
                return new TypeCri(name,string2Type(val));

            default:
                throw new IllegalArgumentException("Unknown arguments");
        }
    }

    /**
     * Generate a negate version of Criterion cri
     * @param name name of the Criterion generated
     * @param cri Criterion to be negated
     * @return the nageted Criterion
     * @throws IllegalArgumentException thrown when name is not legal
     */
    public static Criterion newNegation(String name, final Criterion cri) throws IllegalArgumentException {
        return new Criterion(name, cri.getAttrname(), cri.getOp().negate(), cri.getVal()){
            @Override
            public boolean assertCri(File file) {
                return !cri.assertCri(file);
            }
        };
    }

    /**
     * Construct a binary composition of Criterions
     * @param name name of the new Criterion
     * @param c1 first Criterion used
     * @param c2 second Criterion used
     * @param boString a String representation of binary operator "AND", "OR"
     * @return the constructed Criterion
     * @throws IllegalArgumentException thrown when name is illegal
     */
    public static Criterion newBinaryCri(String name, Criterion c1, Criterion c2, String boString) throws IllegalArgumentException {

        Op bo = string2Op(boString);
        return new MyCriterion(name, bo, c1, c2);
    }

    /**
     * Generate Criterion 'isDocument', which check whether a file is a <code>Directory</code> or <code>Document</code>
     * @see hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Directory
     * @see hk.edu.polyu.comp.comp2021.g17.cvfs.model.file.Document
     * @return the Criterion 'isDocument'
     */
    public static Criterion genIsDocument() {
       
            return new Criterion("isDocument", AttrName.filetype, Op.equals, FileType.Document) {
                @Override
                public boolean assertCri(File file) {
                    return file.getFileType() == FileType.Document;
                }
            };
       
    }

    @Override
    public String toString() {
        return String.format("%15s | %10s | %15s | %20s",name, attrname, op.name(), val.toString());
    }

    private static class MyCriterion extends Criterion {
        private final Op bo;
        private final Criterion c1;
        private final Criterion c2;

        public MyCriterion(String name, Op bo, Criterion c1, Criterion c2) throws IllegalArgumentException {
            super(name, AttrName.composite, bo, c1.getName() + " " + c2.getName());
            this.bo = bo;
            this.c1 = c1;
            this.c2 = c2;
        }

        @Override
        public boolean assertCri(File file) {
            switch (bo) {
                case AND:
                    return c1.assertCri(file) && c2.assertCri(file);
                default:
                    return c1.assertCri(file) || c2.assertCri(file);
               
            }
         
        }
    }
}

