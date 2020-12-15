package parser.grammar;

public enum GrammarAction {
    PROMOTE, // Ersetzt Node mit Child, wenn es nur ein Child gibt
    DELIFEMPTY, // Entfernt Node, wenn dieser keinen Inhalt hat
    DELCHILD, // Entfernt bestimmte Child-Nodes
    VALTOVAL, // Setzt die Child-Value als Parent-Value und löscht das Child
    NAMETOVAL, // Setzt den Child-Namen als Parent-Value und löscht das Child
    RENAMETO // Führt eine Umbenennung durch
}
