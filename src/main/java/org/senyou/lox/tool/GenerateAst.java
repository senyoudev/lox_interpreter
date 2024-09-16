package org.senyou.lox.tool;


import java.io.FileNotFoundException;
import java.io.PrintWriter;
import java.io.UnsupportedEncodingException;
import java.util.List;

/**
 * Generate the abstract syntax tree classes for the Lox programming language.
 * Abstract syntax tree is a tree representation of the source code.
 * For example, the expression 1 + 2 * 3 would be represented as:
 * Binary(
 *   left: Literal(1),
 *   operator: '+',
 *   right: Binary(
 *   left: Literal(2),
 *   operator: '*',
 *   right: Literal(3)
 *   )
 *   )
 */
public class GenerateAst {

    public static void main(String[] args) throws FileNotFoundException, UnsupportedEncodingException {
        if(args.length != 1) {
            System.err.println("Usage: generate_ast <output directory>");
            System.exit(64);
        }
        String outputDir = args[0];
        defineAst(outputDir, "Expr", List.of(
            "Binary   : Expr left, Token operator, Expr right",
            "Grouping : Expr expression",
            "Literal  : Object value",
            "Unary    : Token operator, Expr right"
        ));
    }

    /**
     * Define the AST classes.
     * @param outputDir
     * @param baseName
     * @param types
     * @throws FileNotFoundException
     * @throws UnsupportedEncodingException
     */
    private static void defineAst(String outputDir, String baseName, List<String> types)
            throws FileNotFoundException, UnsupportedEncodingException {
        String path = outputDir + "/" + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");

        writer.println("package org.senyou.lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("abstract class " + baseName + " {");

        // The base accept() method
        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");
        writer.println();

        defineVisitor(writer, baseName, types);


        // The AST classes
        for(String type : types) {
            String className = type.split(":")[0].trim();
            String fields = type.split(":")[1].trim();
            defineType(writer, baseName, className, fields);
        }



        writer.println("}");
        writer.flush();
        writer.close();
    }

    /**
     * Define a type in the AST.
     * @param writer
     * @param baseName
     * @param className
     * @param fieldList
     */
    private static void defineType(PrintWriter writer, String baseName, String className, String fieldList) {
        writer.println("    static class " + className + " extends " + baseName + " {");

        // Fields
        String[] fields = fieldList.split(", ");
        for(String field : fields) {
            writer.println("        final " + field + ";");
        }

        // Constructor
        writer.println("        " + className + "(" + fieldList + ") {");

        // Store parameters in fields
        for(String field : fields) {
            String name = field.split(" ")[1];
            writer.println("            this." + name + " = " + name + ";");
        }

        writer.println("        }");

        // Visitor pattern
        writer.println();
        writer.println("        @Override");
        writer.println("        <R> R accept(Visitor<R> visitor) {");
        writer.println("            return visitor.visit" + className + baseName + "(this);");
        writer.println("        }");



        writer.println("    }");
    }

    /**
     * Define the visitor interface.
     * @param writer
     * @param baseName
     * @param types
     */
    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("    interface Visitor<R> {");

        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "(" + typeName + " " + baseName.toLowerCase() + ");");
        }

        writer.println("    }");
        writer.println();
    }

}
