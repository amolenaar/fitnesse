package fitnesse.responders.editing;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import fitnesse.FitNesseContext;
import fitnesse.authentication.SecureOperation;
import fitnesse.authentication.SecureReadOperation;
import fitnesse.authentication.SecureResponder;
import fitnesse.html.SetupTeardownAndLibraryIncluder;
import fitnesse.http.Request;
import fitnesse.http.Response;
import fitnesse.http.Response.Format;
import fitnesse.http.ResponseSender;
import fitnesse.http.SimpleResponse;
import fitnesse.responders.run.MultipleTestsRunner;
import fitnesse.responders.run.SuiteContentsFinder;
import fitnesse.wikitext.parser.Table;
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageCrawlerImpl;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Symbol;
import fitnesse.wikitext.parser.SymbolTreeWalker;
import fitnesse.wikitext.parser.SymbolType;

public class SuggestionsResponder implements SecureResponder {
  public WikiPage page;
  protected WikiPagePath path;
  protected FitNesseContext context;

  @Override
  public Response makeResponse(FitNesseContext context, Request request) throws Exception {
    this.context = context;
    page = getRequestedPage(request);

    // TODO -AJM- Return suggestions in JSON format
    SimpleResponse response = new SimpleResponse();
    response.setContentType(Format.TEXT);
    Symbol syntaxTree = combineAllPageData();
    
    // Debug:
//    String html = page.getData().translateToHtml(syntaxTree);
//    response.setContent(html);

    // Find scenario tables
    List<Symbol> scenarioTables = findScenarioTables(syntaxTree);
    System.out.println("Found " + scenarioTables.size() + " tables");
    StringBuilder b = new StringBuilder(2048);
    for (Symbol t: scenarioTables) {
      b.append("\n\nNew Table:\n\n");
      b.append(t.dump());
    }
    response.setContent(b.toString());

    // Filter scenario signatures
    // Create JSON array and return that as response

    return response;
  }

  protected Symbol combineAllPageData() throws Exception {
    PageData pageData = page.getData();

    SetupTeardownAndLibraryIncluder.includeSetupsTeardownsAndLibrariesBelowTheSuite(pageData, page);
    
    Symbol syntaxTree = pageData.getSyntaxTree();
    Symbol preparsedScenarioLibrary = getPreparsedScenarioLibrary();
    syntaxTree.addToFront(preparsedScenarioLibrary);

    return syntaxTree;
  }

  private List<Symbol> findScenarioTables(final Symbol syntaxTree) {
    final List<Symbol> tables = new ArrayList<Symbol>();
    SymbolTreeWalker treeWalker = new SymbolTreeWalker() {
      
      @Override
      public boolean visitChildren(Symbol node) {
        System.out.println("Symbol type = " + node.getType().getClass() + " " + (node.getType() instanceof Table));
        if (node.getType() instanceof Table) {
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

  private WikiPage getRequestedPage(Request request) throws Exception {
    path = PathParser.parse(request.getResource());
    return getPageCrawler().getPage(context.root, path);
  }

  protected PageCrawler getPageCrawler() {
    return context.root.getPageCrawler();
  }
  
  public Symbol getPreparsedScenarioLibrary() throws Exception {
    return Parser.make(page, getScenarioLibraryContent()).parse();
  }

  private String getScenarioLibraryContent() throws Exception {
    String content = "";
    List<WikiPage> uncles = PageCrawlerImpl.getAllUncles("ScenarioLibrary", page);
    Collections.reverse(uncles);
    for (WikiPage uncle : uncles)
      content += include(page.getPageCrawler().getFullPath(uncle));
    return content;
  }

  private String include(WikiPagePath path) {
    return "!include -c ." + path + "\n";
  }

  @Override
  public SecureOperation getSecureOperation() {
    return new SecureReadOperation();
  }

}
