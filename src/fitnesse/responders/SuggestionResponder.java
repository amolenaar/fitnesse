package fitnesse.responders;

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
import fitnesse.wiki.PageCrawler;
import fitnesse.wiki.PageCrawlerImpl;
import fitnesse.wiki.PageData;
import fitnesse.wiki.PathParser;
import fitnesse.wiki.WikiPage;
import fitnesse.wiki.WikiPagePath;
import fitnesse.wikitext.parser.Parser;
import fitnesse.wikitext.parser.Symbol;

public class SuggestionResponder implements SecureResponder {
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
    String html = page.getData().translateToHtml(syntaxTree);

    response.setContent(html);
    return response;
  }

  protected Symbol combineAllPageData() throws Exception {
    List<WikiPage> test2run = new SuiteContentsFinder(page, null, context.root).makePageListForSingleTest();

    PageData pageData = page.getData();
    SetupTeardownAndLibraryIncluder.includeSetupsTeardownsAndLibrariesBelowTheSuite(pageData, page);
    
    Symbol syntaxTree = pageData.getSyntaxTree();
    Symbol preparsedScenarioLibrary = getPreparsedScenarioLibrary();
    syntaxTree.addToFront(preparsedScenarioLibrary);

    return syntaxTree;
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
