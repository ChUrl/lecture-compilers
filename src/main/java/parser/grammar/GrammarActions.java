package parser.grammar;

public enum GrammarActions {
    COMPACT, // Ersetzt Node mit Child, wenn es nur ein Child gibt
    NULLABLE, // Entfernt Node, wenn dieser keinen Inhalt hat
    MOVE, // TODO: Setzt den Child-Namen als Parent-Value und löscht das Child
    RENAME // TODO: Führt eine Umbenennung durch
}
