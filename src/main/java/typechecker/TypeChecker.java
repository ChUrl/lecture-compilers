package typechecker;

import parser.ast.AST;

public final class TypeChecker {

    private TypeChecker() {}

    // Wirft exception bei typeerror, returned nix
    public static void validate(AST tree) {
        SymbolTable table = SymbolTable.fromAST(tree);
    }
}
