package parser;

import java.util.Set;

public interface ILL1ParsingTable {

    String get(String nonterminal, String terminal);

    String getStartSymbol();

    Set<String> getNonterminals();

    Set<String> getTerminals();

    String getEpsilon();
}
