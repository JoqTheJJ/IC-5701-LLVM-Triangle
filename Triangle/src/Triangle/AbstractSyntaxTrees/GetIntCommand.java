package Triangle.AbstractSyntaxTrees;

import Triangle.SyntacticAnalyzer.SourcePosition;

public class GetIntCommand extends Command {

  public GetIntCommand(Vname vAST, SourcePosition thePosition) {
    super(thePosition);
    V = vAST;
  }

  public Object visit(Visitor v, Object o) {
    return v.visitGetIntCommand(this, o);
  }

  public Vname V;
}
