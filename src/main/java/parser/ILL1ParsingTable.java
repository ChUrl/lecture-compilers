package parser;

import java.util.List;

public interface ILL1ParsingTable {

    List<String> get(String nonterminal, String terminal);

    String getStartSymbol();

    List<String> getNonterminals();

    List<String> getTerminals();

    String getEpsilon();
}
