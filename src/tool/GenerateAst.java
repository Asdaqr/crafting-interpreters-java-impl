package tool;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Arrays;
import java.util.List;

public class GenerateAst {
    public static void main(String[] args) throws IOException {
        if (args.length != 1) {
            System.out.println("Usage: java GenerateAst <output-directory>");
            System.exit(64);
        }
        String outputDir = args[0];

        defineAst(outputDir, "Expr", Arrays.asList(
                "Assign     : Token name, Expr value",
                "Binary     : Expr left, Token op, Expr right",
                "Call       : Expr callee, Token paren, List<Expr> args",
                "Grouping   : Expr expression",
                "Literal    : Object value",
                "Logical    : Expr left, Token op, Expr right",
                "Unary      : Token op, Expr right",
                "Variable   : Token name"
                ));

        defineAst(outputDir, "Stmt", Arrays.asList(
                "Block      : List<Stmt> statements",
                "Expression : Expr expression",
                "Function   : Token name, List<Token> params," +
                            "List<Stmt> body",
                "If         : Expr condition, Stmt thenBranch," +
                            " Stmt elseBranch",
                "Print      : Expr expression",
                "Return     : Token keyword, Expr value",
                "Var        : Token name, Expr initializer",
                "While      : Expr condition, Stmt body",
                "Break      : Token self"
        ));
    }
    private static void defineAst(
            String outputDir, String baseName, List<String> types)
            throws IOException {
        String path = outputDir + File.separator + baseName + ".java";
        PrintWriter writer = new PrintWriter(path, "UTF-8");
        writer.println("package lox;");
        writer.println();
        writer.println("import java.util.List;");
        writer.println();
        writer.println("public abstract class " + baseName + " {");
        defineVisitor(writer, baseName, types);

        for (String type : types) {
            String[] typeDef = type.strip().split(":");
            String className = typeDef[0].strip();
            String fields = typeDef[1].trim();

            defineType(writer, baseName, className, fields);
        }

        writer.println();
        writer.println("    abstract <R> R accept(Visitor<R> visitor);");

        writer.println("}");
        writer.close();
    }
    private static void defineType
            (PrintWriter writer, String baseName, String className, String fields) {
            writer.println("    public static class " + className + " extends " + baseName + " {");
            writer.println("        public " + className +"(" + fields + ") {");

            String[] fieldArray = fields.split(",");
            for (String field : fieldArray) {
                field = field.strip().split(" ")[1].trim();
                writer.println("            " + "this." + field + " = " + field + ";");
            }
            writer.println("        }");

            writer.println();
            writer.println("        @Override");
            writer.println("        <R> R accept (Visitor<R> visitor) {");
            writer.println("            return visitor.visit" + className + baseName + "(this);");
            writer.println("        }");

            for (String field : fieldArray) {
                field = field.strip();
                writer.println("        final " + field + ";");
            }
            writer.println("    }");

    }

    private static void defineVisitor(PrintWriter writer, String baseName, List<String> types) {
        writer.println("");
        writer.println("    public interface Visitor<R> {");
        for (String type : types) {
            String typeName = type.split(":")[0].trim();
            writer.println("        R visit" + typeName + baseName + "("
            + typeName + " " + baseName.toLowerCase() + ");");
        }
        writer.println("    }");

    }
}
