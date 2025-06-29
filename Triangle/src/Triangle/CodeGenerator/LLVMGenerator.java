package Triangle.CodeGenerator;

import Triangle.AbstractSyntaxTrees.*;
import Triangle.SyntacticAnalyzer.SourcePosition;
import java.util.HashMap;
import java.util.Map;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Map;

import Triangle.ErrorReporter;
import Triangle.StdEnvironment;
import Triangle.AbstractSyntaxTrees.AST;
import Triangle.AbstractSyntaxTrees.AnyTypeDenoter;
import Triangle.AbstractSyntaxTrees.ArrayExpression;
import Triangle.AbstractSyntaxTrees.ArrayTypeDenoter;
import Triangle.AbstractSyntaxTrees.AssignCommand;
import Triangle.AbstractSyntaxTrees.BinaryExpression;
import Triangle.AbstractSyntaxTrees.BinaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.BoolTypeDenoter;
import Triangle.AbstractSyntaxTrees.CallCommand;
import Triangle.AbstractSyntaxTrees.CallExpression;
import Triangle.AbstractSyntaxTrees.CharTypeDenoter;
import Triangle.AbstractSyntaxTrees.CharacterExpression;
import Triangle.AbstractSyntaxTrees.CharacterLiteral;
import Triangle.AbstractSyntaxTrees.ConstActualParameter;
import Triangle.AbstractSyntaxTrees.ConstDeclaration;
import Triangle.AbstractSyntaxTrees.ConstFormalParameter;
import Triangle.AbstractSyntaxTrees.Declaration;
import Triangle.AbstractSyntaxTrees.DotVname;
import Triangle.AbstractSyntaxTrees.EmptyActualParameterSequence;
import Triangle.AbstractSyntaxTrees.EmptyCommand;
import Triangle.AbstractSyntaxTrees.EmptyExpression;
import Triangle.AbstractSyntaxTrees.EmptyFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.ErrorTypeDenoter;
import Triangle.AbstractSyntaxTrees.FuncActualParameter;
import Triangle.AbstractSyntaxTrees.FuncDeclaration;
import Triangle.AbstractSyntaxTrees.FuncFormalParameter;
import Triangle.AbstractSyntaxTrees.Identifier;
import Triangle.AbstractSyntaxTrees.IfCommand;
import Triangle.AbstractSyntaxTrees.IfExpression;
import Triangle.AbstractSyntaxTrees.IntTypeDenoter;
import Triangle.AbstractSyntaxTrees.IntegerExpression;
import Triangle.AbstractSyntaxTrees.IntegerLiteral;
import Triangle.AbstractSyntaxTrees.LetCommand;
import Triangle.AbstractSyntaxTrees.LetExpression;
import Triangle.AbstractSyntaxTrees.MultipleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleArrayAggregate;
import Triangle.AbstractSyntaxTrees.MultipleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.MultipleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.MultipleRecordAggregate;
import Triangle.AbstractSyntaxTrees.Operator;
import Triangle.AbstractSyntaxTrees.ProcActualParameter;
import Triangle.AbstractSyntaxTrees.ProcDeclaration;
import Triangle.AbstractSyntaxTrees.ProcFormalParameter;
import Triangle.AbstractSyntaxTrees.Program;
import Triangle.AbstractSyntaxTrees.RecordExpression;
import Triangle.AbstractSyntaxTrees.RecordTypeDenoter;
import Triangle.AbstractSyntaxTrees.SequentialCommand;
import Triangle.AbstractSyntaxTrees.SequentialDeclaration;
import Triangle.AbstractSyntaxTrees.SimpleTypeDenoter;
import Triangle.AbstractSyntaxTrees.SimpleVname;
import Triangle.AbstractSyntaxTrees.SingleActualParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleArrayAggregate;
import Triangle.AbstractSyntaxTrees.SingleFieldTypeDenoter;
import Triangle.AbstractSyntaxTrees.SingleFormalParameterSequence;
import Triangle.AbstractSyntaxTrees.SingleRecordAggregate;
import Triangle.AbstractSyntaxTrees.SubscriptVname;
import Triangle.AbstractSyntaxTrees.TypeDeclaration;
import Triangle.AbstractSyntaxTrees.UnaryExpression;
import Triangle.AbstractSyntaxTrees.UnaryOperatorDeclaration;
import Triangle.AbstractSyntaxTrees.VarActualParameter;
import Triangle.AbstractSyntaxTrees.VarDeclaration;
import Triangle.AbstractSyntaxTrees.VarFormalParameter;
import Triangle.AbstractSyntaxTrees.Visitor;
import Triangle.AbstractSyntaxTrees.Vname;
import Triangle.AbstractSyntaxTrees.VnameExpression;
import Triangle.AbstractSyntaxTrees.WhileCommand;
public class LLVMGenerator implements Visitor {

    private StringBuilder code;
    private int tempCount;
    private final Map<Declaration, String> llvmNames = new HashMap<>();


    public LLVMGenerator() {
        code = new StringBuilder();
        tempCount = 0;
    }

    public String generate(Program program) {
        code.insert(0, "@true = global i32 1\n@false = global i32 0\n");
        code.insert(0, "declare i32 @readInt()\n");
        code.insert(0, "declare void @printInt(i32)\n");
        code.insert(0, "declare i8 @readChar()\n");
        code.insert(0, "declare void @printChar(i8)\n");


        code.append("define i32 @main() {\n");
        program.visit(this, null);
        code.append("  ret i32 0\n");
        code.append("}\n");
        return code.toString();
    }

    private String newTemp() {
        return "%t" + (tempCount++);
    }

    // Implementa los métodos visit... para cada nodo del AST que te interesa

    public Object visitProgram(Program prog, Object o) {
        prog.C.visit(this, o);
        return null;
    }

    public Object visitIntegerLiteral(IntegerLiteral il, Object o) {
        String temp = newTemp();
        code.append("  ").append(temp).append(" = add i32 ").append(il.spelling).append(", 0\n");
        return temp;
    }

    public Object visitVname(Vname vname, Object o) {
        if (vname instanceof SimpleVname) {
            return visitSimpleVname((SimpleVname) vname, o);
        }
        return null;
    }


    public Object visitAssignCommand(AssignCommand ac, Object o) {
        String val = (String) ac.E.visit(this, o);

        if (ac.V instanceof SimpleVname) {
            SimpleVname simple = (SimpleVname) ac.V;

            if (simple.D instanceof VarDeclaration) {
                String varName = llvmNames.get(simple.D);
                if (varName == null) varName = "@" + simple.I.spelling;

                code.append("  store i32 ").append(val)
                    .append(", ptr ").append(varName).append("\n");
            }
        }

        return null;
    }

    public Object visitCallCommand(CallCommand cmd, Object o) {
    String value = (String) cmd.APS.visit(this, o);

    if (cmd.I.spelling.equals("print") || cmd.I.spelling.equals("putint")) {
        code.append("  call void @printInt(i32 ").append(value).append(")\n");
    } else if (cmd.I.spelling.equals("put")) {
        String temp = newTemp();
        code.append("  ").append(temp).append(" = trunc i32 ").append(value).append(" to i8\n");
        code.append("  call void @printChar(i8 ").append(temp).append(")\n");
    }

    return null;
}



    public Object visitEmptyCommand(EmptyCommand ast, Object o) {
        return null;
    }

    public Object visitIfCommand(IfCommand ast, Object o) {
        String thenLabel = "then" + tempCount++;
        String elseLabel = "else" + tempCount++;
        String endLabel  = "endif" + tempCount++;

        // Evaluar la condición
        String condValue = (String) ast.E.visit(this, o);
        String condBool = newTemp();
        code.append("  ").append(condBool)
            .append(" = icmp ne i32 ").append(condValue).append(", 0\n");
        code.append("  br i1 ").append(condBool)
            .append(", label %").append(thenLabel)
            .append(", label %").append(elseLabel).append("\n");

        // Bloque THEN
        code.append(thenLabel).append(":\n");
        ast.C1.visit(this, o);
        code.append("  br label %").append(endLabel).append("\n");

        // Bloque ELSE
        code.append(elseLabel).append(":\n");
        ast.C2.visit(this, o);
        code.append("  br label %").append(endLabel).append("\n");

        // Fin del IF
        code.append(endLabel).append(":\n");

        return null;
    }


    @Override
    public Object visitLetCommand(LetCommand lc, Object o) {
        // Declaraciones: asumimos variables globales por ahora
        lc.D.visit(this, o); // visita la declaFración

        // Comando dentro del 'in'
        lc.C.visit(this, o); // visita el cuerpo del let
        return null;
    }

    public Object visitSequentialCommand(SequentialCommand ast, Object o) {
        ast.C1.visit(this, o);
        ast.C2.visit(this, o);
        return null;
    }

    
    public Object visitWhileCommand(WhileCommand ast, Object o) {
        String loopLabel = "loop" + tempCount++;
        String bodyLabel = "body" + tempCount++;
        String endLabel = "end" + tempCount++;

        // Ir al bloque de condición
        code.append("  br label %").append(loopLabel).append("\n");

        // Condición del while
        code.append(loopLabel).append(":\n");
        String condValue = (String) ast.E.visit(this, o);  // debe retornar i1
        code.append("  br i1 ").append(condValue)
            .append(", label %").append(bodyLabel)
            .append(", label %").append(endLabel).append("\n");

        // Cuerpo del while
        code.append(bodyLabel).append(":\n");
        ast.C.visit(this, o);
        code.append("  br label %").append(loopLabel).append("\n");

        // Fin
        code.append(endLabel).append(":\n");

        return null;
    }



    @Override
    public Object visitArrayExpression(ArrayExpression ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitBinaryExpression(BinaryExpression ast, Object o) {
        String left = (String) ast.E1.visit(this, o);
        String right = (String) ast.E2.visit(this, o);
        String temp = newTemp();
        String op = ast.O.spelling;

        switch (op) {
            case "+":
                code.append("  ").append(temp).append(" = add i32 ").append(left).append(", ").append(right).append("\n");
                break;
            case "-":
                code.append("  ").append(temp).append(" = sub i32 ").append(left).append(", ").append(right).append("\n");
                break;
            case "*":
                code.append("  ").append(temp).append(" = mul i32 ").append(left).append(", ").append(right).append("\n");
                break;
            case "/":
                code.append("  ").append(temp).append(" = sdiv i32 ").append(left).append(", ").append(right).append("\n");
                break;
            case "<":
                code.append("  ").append(temp).append(" = icmp slt i32 ").append(left).append(", ").append(right).append("\n");
                break;

            case "<=":
                code.append("  ").append(temp).append(" = icmp sle i32 ").append(left).append(", ").append(right).append("\n");
                return castBoolToInt(temp);
            case "==":
                code.append("  ").append(temp).append(" = icmp eq i32 ").append(left).append(", ").append(right).append("\n");
                return castBoolToInt(temp);
            case "!=":
                code.append("  ").append(temp).append(" = icmp ne i32 ").append(left).append(", ").append(right).append("\n");
                return castBoolToInt(temp);
            case ">=":
                code.append("  ").append(temp).append(" = icmp sge i32 ").append(left).append(", ").append(right).append("\n");
                return castBoolToInt(temp);
            case ">":
                code.append("  ").append(temp).append(" = icmp sgt i32 ").append(left).append(", ").append(right).append("\n");
                return castBoolToInt(temp);
            default:
                throw new UnsupportedOperationException("Operador no soportado: " + op);
        }

        return temp;
    }

        
    private String castBoolToInt(String boolVar) {
        String intVar = newTemp();
        code.append("  ").append(intVar).append(" = zext i1 ").append(boolVar).append(" to i32\n");
        return intVar;
    }



    @Override
    public Object visitCallExpression(CallExpression expr, Object o) {
        if (expr.I.spelling.equals("read")) {
            String temp = newTemp();
            code.append("  ").append(temp).append(" = call i32 @readInt()\n");
            return temp;
        }
        return null;
    }





    @Override
    public Object visitCharacterExpression(CharacterExpression ast, Object o) {
        // Obtener el literal de carácter, por ejemplo: 'a'
    char c = ast.CL.spelling.charAt(1);  // char literal como 'a'
    int ascii = (int) c;

    String temp = newTemp();
    code.append("  ").append(temp).append(" = add i32 ").append(ascii).append(", 0\n");
    return temp;
    }

    @Override
    public Object visitEmptyExpression(EmptyExpression ast, Object o) {
        return "0";
    }

    @Override
    public Object visitIfExpression(IfExpression ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitIntegerExpression(IntegerExpression ast, Object o) {
        return ast.IL.visit(this, o);  // IL es el IntegerLiteral
    }


    public Object visitLetExpression(LetExpression ast, Object o) {     
        ast.D.visit(this, "let");
        return ast.E.visit(this, o);
    }

    @Override
    public Object visitRecordExpression(RecordExpression ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitUnaryExpression(UnaryExpression ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitVnameExpression(VnameExpression ast, Object o) {
        return ast.V.visit(this, o);  // esto permite que n dentro de print(n) funcione
    }


    @Override
    public Object visitBinaryOperatorDeclaration(BinaryOperatorDeclaration ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    @Override
    public Object visitConstDeclaration(ConstDeclaration ast, Object o) {
        String constName = "@" + ast.I.spelling;
        String value = (String) ast.E.visit(this, o);

        // Asumimos que el valor retornado puede ser un temporario como %t3
        // Queremos declarar la constante directamente con un literal

        // Si el valor es un literal directo como "1", úsalo. Si es temp (%t3), ignóralo.
        String literal;
        if (value.matches("\\d+")) {
            literal = value;
        } else {
            // Fallback a 0 si no podemos extraer valor literal
            literal = "0";
        }

    code.insert(0, constName + " = global i32 " + literal + "\n");
    llvmNames.put(ast, constName);
    return null;
}


    private String extractIntValue(String temp) {
        // Si es un literal inmediato como '1' o '0', lo dejamos.
        if (temp.matches("\\d+")) {
            return temp;
        }
        // Si es un temporario, esto es más complejo.
        // Asumimos que solo usamos esta función cuando el valor es literal.
        return "0"; // fallback para evitar errores
    }


    @Override
    public Object visitFuncDeclaration(FuncDeclaration ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitProcDeclaration(ProcDeclaration ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSequentialDeclaration(SequentialDeclaration ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitTypeDeclaration(TypeDeclaration ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitUnaryOperatorDeclaration(UnaryOperatorDeclaration ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }
    @Override
    public Object visitVarDeclaration(VarDeclaration decl, Object o) {
        String suffix = (o instanceof String) ? "." + o : "";
        String varName = "@" + decl.I.spelling;
        llvmNames.put(decl, varName);
        code.insert(0, varName + " = global i32 0\n");
        return null;
    }

    @Override
    public Object visitMultipleArrayAggregate(MultipleArrayAggregate ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSingleArrayAggregate(SingleArrayAggregate ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitMultipleRecordAggregate(MultipleRecordAggregate ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSingleRecordAggregate(SingleRecordAggregate ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitConstFormalParameter(ConstFormalParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitFuncFormalParameter(FuncFormalParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitProcFormalParameter(ProcFormalParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitVarFormalParameter(VarFormalParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitEmptyFormalParameterSequence(EmptyFormalParameterSequence ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitMultipleFormalParameterSequence(MultipleFormalParameterSequence ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSingleFormalParameterSequence(SingleFormalParameterSequence ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitConstActualParameter(ConstActualParameter ast, Object o) {
        return ast.E.visit(this, o);
    }

    @Override
    public Object visitFuncActualParameter(FuncActualParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitProcActualParameter(ProcActualParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitVarActualParameter(VarActualParameter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitEmptyActualParameterSequence(EmptyActualParameterSequence ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitMultipleActualParameterSequence(MultipleActualParameterSequence ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSingleActualParameterSequence(SingleActualParameterSequence ast, Object o) {
        return ast.AP.visit(this, o);
    }

    @Override
    public Object visitAnyTypeDenoter(AnyTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitArrayTypeDenoter(ArrayTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

   
    public Object visitBoolTypeDenoter(BoolTypeDenoter ast, Object o) {
       return "i1";
    }

    public Object visitCharTypeDenoter(CharTypeDenoter ast, Object o) {
        return "i8";
    }

    @Override
    public Object visitErrorTypeDenoter(ErrorTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSimpleTypeDenoter(SimpleTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitIntTypeDenoter(IntTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitRecordTypeDenoter(RecordTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitMultipleFieldTypeDenoter(MultipleFieldTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitSingleFieldTypeDenoter(SingleFieldTypeDenoter ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitCharacterLiteral(CharacterLiteral ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitIdentifier(Identifier ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    @Override
    public Object visitOperator(Operator ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

    
    public Object visitDotVname(DotVname ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }

 
@Override
public Object visitSimpleVname(SimpleVname ast, Object o) {
    Declaration decl = (Declaration) ast.I.decl;  // <--- esta línea es la clave
    System.out.println(">> visitSimpleVname: " + ast.I.spelling + ", decl=" + decl);

    if (decl instanceof VarDeclaration || decl instanceof ConstDeclaration) {
        String varName = llvmNames.get(decl);
        if (varName == null) {
            varName = "@" + ast.I.spelling;
        }
        String temp = newTemp();
        code.append("  ").append(temp).append(" = load i32, ptr ").append(varName).append("\n");
        return temp;
    }


    return null;
}

    @Override
    public Object visitSubscriptVname(SubscriptVname ast, Object o) {
        throw new UnsupportedOperationException("Not supported yet."); // Generated from nbfs://nbhost/SystemFileSystem/Templates/Classes/Code/GeneratedMethodBody
    }


    public Object visitGetCharCommand(GetCharCommand ast, Object o) {
        // Obtener nombre LLVM de la variable de destino
        if (ast.V instanceof SimpleVname) {
            SimpleVname simple = (SimpleVname) ast.V;

            if (simple.D instanceof VarDeclaration) {
                String varName = llvmNames.get(simple.D);
                if (varName == null) varName = "@" + simple.I.spelling;

                // Llamar a readChar()
                String temp = newTemp();
                code.append("  ").append(temp).append(" = call i8 @readChar()\n");

                // Extender a i32 si es necesario (para almacenarlo en int)
                String extended = newTemp();
                code.append("  ").append(extended).append(" = zext i8 ").append(temp).append(" to i32\n");

                // Guardar en variable destino
                code.append("  store i32 ").append(extended).append(", ptr ").append(varName).append("\n");
            }
        }

        return null;
    }

        public Object visitGetIntCommand(GetIntCommand ast, Object o) {
        if (ast.V instanceof SimpleVname) {
            SimpleVname simple = (SimpleVname) ast.V;

            if (simple.D instanceof VarDeclaration) {
                String varName = llvmNames.get(simple.D);
                if (varName == null) varName = "@" + simple.I.spelling;

                // Llamar a readInt()
                String temp = newTemp();
                code.append("  ").append(temp).append(" = call i32 @readInt()\n");

                // Guardar en variable destino
                code.append("  store i32 ").append(temp).append(", ptr ").append(varName).append("\n");
            }
        }

        return null;
    }


    
    
}
