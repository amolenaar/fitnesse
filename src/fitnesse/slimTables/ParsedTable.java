package fitnesse.slimTables;

import java.util.List;

import fitnesse.wikitext.parser.Symbol;

public class ParsedTable implements Table {

  private Symbol table;
  
  public ParsedTable(Symbol table) {
    this.table = table;
  }

  private Symbol getCell(int col, int row) {
    return table.childAt(row).childAt(col);
  }
  
  @Override
  public String getCellContents(int col, int row) {
    return getCell(col, row).childAt(0).getContent();
  }

  @Override
  public void appendToCell(int col, int row, String message) {
    getCell(col, row).add(message);
  }

  @Override
  public int getRowCount() {
    return table.getChildren().size();
  }

  @Override
  public int getColumnCountInRow(int row) {
    return table.childAt(row).getChildren().size();
  }

  @Override
  public String toHtml() {
    return "TO BE IMPLEMENTED";
  }

  @Override
  public void setCell(int col, int row, String contents) {
    // For now, do appendToCell
    appendToCell(col, row, contents);
  }

  @Override
  public int addRow(List<String> list) throws Exception {
    // TODO Auto-generated method stub
    return -1;
  }

  @Override
  public void appendCellToRow(int row, String contents) throws Exception {
    // TODO Auto-generated method stub

  }

  @Override
  public String error(String s) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String pass(String s) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String fail(String s) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String ignore(String s) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getUnescapedCellContents(int col, int row) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public String getCellResult(int col, int row) {
    // TODO Auto-generated method stub
    return null;
  }

  @Override
  public void appendCellToRow(int row, Table table) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setTestStatusOnRow(int row, boolean testStatus) {
    // TODO Auto-generated method stub

  }

  @Override
  public void setName(String tableName) {
    // TODO Auto-generated method stub

  }

}
