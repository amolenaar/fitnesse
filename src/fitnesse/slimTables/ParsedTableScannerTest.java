package fitnesse.slimTables;

import static org.junit.Assert.assertEquals;

import java.io.File;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import util.Clock;
import util.DateAlteringClock;
import util.DateTimeUtil;

import fitnesse.FitNesseContext;
import fitnesse.http.MockRequest;
import fitnesse.responders.run.TestResponder;
import fitnesse.testutil.FitNesseUtil;
import fitnesse.testutil.FitSocketReceiver;
import fitnesse.wiki.InMemoryPage;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Symbol;

public class ParsedTableScannerTest {

  private final String WIKI_TEXT = 
      "!|scenario|given page|page|\n" +
      "|provide page|@page|!-with content-!|nothing|\n" +
      "\n" +
      "!|scenario|given test page|page|\n" +
      "|given page|@page|\n" +
      "|make|@page|a test page|\n";
  
  private WikiPage root;

  private PageCrawler crawler;

  private FitNesseContext context;
  
  @Before
  public void setUp() throws Exception {
    File testDir = new File("TestDir");
    testDir.mkdir();
    root = InMemoryPage.makeRoot("RooT");
    //root.getData().setContent(WIKI_TEXT);
    crawler = root.getPageCrawler();
    context = FitNesseUtil.makeTestContext(root);
  }

  @After
  public void tearDown() throws Exception {
    FitNesseUtil.destroyTestContext();
  }

  @Test
  public void testScanner() {
     Symbol syntaxTree = Parser.make(root, WIKI_TEXT).parse();
     
     ParsedTableScanner scanner = new ParsedTableScanner(syntaxTree);
     
     assertEquals(2, scanner.getTableCount());
     
     Table table = scanner.getTable(0);
     
     assertEquals(2, table.getRowCount());
     assertEquals("scenario", table.getCellContents(0, 0));
     assertEquals("given page", table.getCellContents(1, 0));
     assertEquals("provide page", table.getCellContents(0, 1));
  }

}
