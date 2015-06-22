 package smellminer.engine.dataprepare.codemetrics.astparser;
 
 import com.google.common.collect.Lists;
 import com.google.common.collect.Maps;
 import java.io.File;
 import java.io.IOException;
 import java.util.Arrays;
 import java.util.Collection;
 import java.util.List;
 import java.util.SortedMap;
 import java.util.logging.Logger;
 import org.apache.commons.io.FileUtils;
 import org.apache.commons.io.filefilter.AbstractFileFilter;
 import org.apache.commons.io.filefilter.RegexFileFilter;
 import org.eclipse.jdt.core.compiler.InvalidInputException;
 import org.eclipse.jdt.internal.core.util.PublicScanner;
 
 public class JavaTokenizer implements ITokenizer
 {
   private static final long serialVersionUID = 505587999946057082L;
   private static final Logger LOGGER = Logger.getLogger(JavaTokenizer.class
     .getName());
 
   public static final RegexFileFilter javaCodeFileFilter = new RegexFileFilter(
     ".*\\.java$");
   private final boolean tokenizeComments;
   public static final String IDENTIFIER_ID = Integer.toString(5);
   public static final String[] KEYWORD_TYPE_IDs = { 
     Integer.toString(18), 
     Integer.toString(19), 
     Integer.toString(20), 
     Integer.toString(21), 
     Integer.toString(22), 
     Integer.toString(23), 
     Integer.toString(24), 
     Integer.toString(25), 
     Integer.toString(26) };
 
   public static final String[] STRING_LITERAL_IDs = { 
     Integer.toString(45), 
     Integer.toString(44) };
 
   public static final String[] NUMBER_LITERAL_IDs = { 
     Integer.toString(43), 
     Integer.toString(42), 
     Integer.toString(40), 
     Integer.toString(41) };
 
   public static final String[] COMMENT_IDs = { 
     Integer.toString(1002), 
     Integer.toString(1003), 
     Integer.toString(1001) };
 
   public static final String[] OPERATOR_IDs = { 
     Integer.toString(62), 
     Integer.toString(79), 
     Integer.toString(172), 
     Integer.toString(154), 
     Integer.toString(90), 
     Integer.toString(10), 
     Integer.toString(171), 
     Integer.toString(6), 
     Integer.toString(402), 
     Integer.toString(167), 
     Integer.toString(35), 
     Integer.toString(68), 
     Integer.toString(67), 
     Integer.toString(15), 
     Integer.toString(14), 
     Integer.toString(176), 
     Integer.toString(69), 
     Integer.toString(66), 
     Integer.toString(7), 
     Integer.toString(4), 
     Integer.toString(169), 
     Integer.toString(2), 
     Integer.toString(8), 
     Integer.toString(170), 
     Integer.toString(71), 
     Integer.toString(36), 
     Integer.toString(70), 
     Integer.toString(173), 
     Integer.toString(80), 
     Integer.toString(3), 
     Integer.toString(168), 
     Integer.toString(1), 
     Integer.toString(81), 
     Integer.toString(166), 
     Integer.toString(9), 
     Integer.toString(175), 
     Integer.toString(11), 
     Integer.toString(177), 
     Integer.toString(86), 
     Integer.toString(64), 
     Integer.toString(72), 
     Integer.toString(12), 
     Integer.toString(178), 
     Integer.toString(63), 
     Integer.toString(174) };
 
   public static final String[] BRACE_IDs = { 
     Integer.toString(110), 
     Integer.toString(95) };
 
   public static final String[] SYNTAX_IDs = { 
     Integer.toString(90), 
     Integer.toString(6), 
     Integer.toString(402), 
     Integer.toString(64), 
     Integer.toString(110), 
     Integer.toString(95), 
     Integer.toString(7), 
     Integer.toString(86), 
     Integer.toString(15), 
     Integer.toString(166) };
 
   public JavaTokenizer()
   {
     this.tokenizeComments = false;
   }
 
   public JavaTokenizer(boolean tokenizeComments) {
     this.tokenizeComments = tokenizeComments;
   }
 
   public SortedMap<Integer, ITokenizer.FullToken> fullTokenListWithPos(char[] code)
   {
     PublicScanner scanner = prepareScanner();
     SortedMap<Integer,FullToken> tokens = Maps.newTreeMap();
     tokens.put(Integer.valueOf(-1), new ITokenizer.FullToken("<SENTENCE_START>", "<SENTENCE_START>"));
     tokens.put(Integer.valueOf(2147483647), new ITokenizer.FullToken("<SENTENCE_END/>", "<SENTENCE_END/>"));
     scanner.setSource(code);
     while (!scanner.atEnd()) {
       do
         try {
           int token = scanner.getNextToken();
           if (token == 158) {
             break;
           }
           String nxtToken = transformToken(token, 
             scanner.getCurrentTokenString());
           int position = scanner.getCurrentTokenStartPosition();
           tokens.put(Integer.valueOf(position), 
             new ITokenizer.FullToken(nxtToken, Integer.toString(token)));
         } catch (InvalidInputException e) {
           e.printStackTrace();
         }
       while (!scanner.atEnd());
     }
 
     return tokens;
   }
 
   public AbstractFileFilter getFileFilter()
   {
     return javaCodeFileFilter;
   }
 
   public String getIdentifierType()
   {
     return IDENTIFIER_ID;
   }
 
   public Collection<String> getKeywordTypes()
   {
     return Arrays.asList(KEYWORD_TYPE_IDs);
   }
 
   public Collection<String> getLiteralTypes()
   {
     List<String> allLiterals = Lists.newArrayList(
       Arrays.asList(NUMBER_LITERAL_IDs));
     allLiterals.addAll(Arrays.asList(STRING_LITERAL_IDs));
     return allLiterals;
   }
 
   public ITokenizer.FullToken getTokenFromString(String token)
   {
     if (token.equals("<SENTENCE_START>")) {
       return new ITokenizer.FullToken("<SENTENCE_START>", 
         "<SENTENCE_START>");
     }
 
     if (token.equals("<SENTENCE_END/>")) {
       return new ITokenizer.FullToken("<SENTENCE_END/>", 
         "<SENTENCE_END/>");
     }
     return (ITokenizer.FullToken)getTokenListFromCode(token.toCharArray()).get(1);
   }
 
   public List<ITokenizer.FullToken> getTokenListFromCode(char[] code)
   {
     List<ITokenizer.FullToken> tokens = Lists.newArrayList();
     tokens.add(new ITokenizer.FullToken("<SENTENCE_START>", "<SENTENCE_START>"));
     PublicScanner scanner = prepareScanner();
     scanner.setSource(code);
     do
       try {
         int token = scanner.getNextToken();
         if (token == 158) {
           break;
         }
         String nxtToken = transformToken(token, 
           scanner.getCurrentTokenString());
 
         tokens.add(
           new ITokenizer.FullToken(stripTokenIfNeeded(nxtToken), 
           Integer.toString(token)));
       } catch (InvalidInputException e) {
         e.printStackTrace();
       } catch (StringIndexOutOfBoundsException e) {
         e.printStackTrace();
       }
     while (!scanner.atEnd());
     tokens.add(new ITokenizer.FullToken("<SENTENCE_END/>", "<SENTENCE_END/>"));
     return tokens;
   }
 
   public List<ITokenizer.FullToken> getTokenListFromCode(File codeFile)
     throws IOException
   {
     return getTokenListFromCode(FileUtils.readFileToString(codeFile)
       .toCharArray());
   }
 
   protected PublicScanner prepareScanner()
   {
     PublicScanner scanner = new PublicScanner();
     scanner.tokenizeComments = this.tokenizeComments;
     return scanner;
   }
 
   protected String stripTokenIfNeeded(String token)
   {
     return token.replace('\n', ' ').replace('\t', ' ').replace('\r', ' ')
       .replace("\n", " ").replace("\t", " ").replace("\r", " ")
       .replace("'\\\\'", "'|'").replace("\\", "|");
   }
 
   public List<String> tokenListFromCode(char[] code)
   {
     PublicScanner scanner = prepareScanner();
     List<String> tokens = Lists.newArrayList();
     tokens.add("<SENTENCE_START>");
     scanner.setSource(code);
     do
       try {
         int token = scanner.getNextToken();
         if (token == 158) {
           break;
         }
         String nxtToken = transformToken(token, 
           scanner.getCurrentTokenString());
 
         tokens.add(stripTokenIfNeeded(nxtToken));
       } catch (InvalidInputException e) {
         e.printStackTrace();
       } catch (StringIndexOutOfBoundsException e) {
         e.printStackTrace();
       }
     while (!scanner.atEnd());
     tokens.add("<SENTENCE_END/>");
     return tokens;
   }
 
   public List<String> tokenListFromCode(File codeFile)
     throws IOException
   {
     return tokenListFromCode(FileUtils.readFileToString(codeFile)
       .toCharArray());
   }
 
   public SortedMap<Integer, String> tokenListWithPos(char[] code)
   {
     PublicScanner scanner = prepareScanner();
     SortedMap<Integer,String> tokens = Maps.newTreeMap();
     tokens.put(Integer.valueOf(-1), "<SENTENCE_START>");
     tokens.put(Integer.valueOf(2147483647), "<SENTENCE_END/>");
     scanner.setSource(code);
     while (!scanner.atEnd()) {
       do
         try {
           int token = scanner.getNextToken();
           if (token == 158) {
             break;
           }
           String nxtToken = transformToken(token, 
             scanner.getCurrentTokenString());
           int position = scanner.getCurrentTokenStartPosition();
           tokens.put(Integer.valueOf(position), stripTokenIfNeeded(nxtToken));
         } catch (InvalidInputException e) {
           e.printStackTrace();
         }
       while (!scanner.atEnd());
     }
 
     return tokens;
   }
 
   public SortedMap<Integer, ITokenizer.FullToken> tokenListWithPos(File file)
     throws IOException
   {
     return fullTokenListWithPos(FileUtils.readFileToString(file)
       .toCharArray());
   }
 
   protected String transformToken(int tokenType, String token)
   {
     return token;
   }
 }

/* Location:           D:\research\topics\Software Engineering\Bug Gene\Tools\tsg.jar
 * Qualified Name:     codemining.java.tokenizers.JavaTokenizer
 * JD-Core Version:    0.6.0
 */