package fitnesse.slimTables;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.htmlparser.Node;
import org.htmlparser.tags.TableTag;
import org.htmlparser.util.NodeList;

import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolTreeWalker;

public class ParsedTableScanner implements TableScanner {

  private List<Symbol> tables;

  public ParsedTableScanner(Symbol syntaxTree) {
    this.tables = scanForTables(syntaxTree);
  }
  
  private List<Symbol> scanForTables(final Symbol syntaxTree) {
    final List<Symbol> tables = new ArrayList<Symbol>();
    SymbolTreeWalker treeWalker = new SymbolTreeWalker() {
      
      @Override
      public boolean visitChildren(Symbol node) {
        if (node.getType() instanceof fitnesse.wikitext.parser.Table) {
          tables.add(node);
          return false;
        }
        return true;
      }
      
      @Override
      public boolean visit(Symbol node) {
        return true;
      }
    };
    
    syntaxTree.walkPostOrder(treeWalker);
    return tables;
  }

  @Override
  public int getTableCount() {
    return tables.size();
  }

  @Override
  public Table getTable(int i) {
    return new ParsedTable(tables.get(i));
  }

  @Override
  public Iterator<Table> iterator() {
    
    return new Iterator<Table>() {
      final List<Symbol> tables = ParsedTableScanner.this.tables;
      int index;
      
      @Override
      public boolean hasNext() {
        return index < tables.size();
      }

      @Override
      public Table next() {
        return new ParsedTable(tables.get(index++));
      }

      @Override
      public void remove() {
        // Do nothing
      }
    };
  }

  @Override
  public String toWikiText() {
    return null;
  }

  @Override
  public String toHtml() {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String toHtml(Table startAfterTable, Table endWithTable) {
    // TODO Auto-generated method stub
    return null;
  }

}
