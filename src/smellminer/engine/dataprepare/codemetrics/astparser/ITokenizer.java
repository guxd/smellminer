package smellminer.engine.dataprepare.codemetrics.astparser;

import com.google.common.base.Objects;
import com.google.common.base.Function;
import java.io.IOException;
import java.io.File;
import java.util.List;
import java.util.Collection;
import org.apache.commons.io.filefilter.AbstractFileFilter;
import java.util.SortedMap;
import java.io.Serializable;

public interface ITokenizer extends Serializable
{
   public static final String SENTENCE_END = "<SENTENCE_END/>";
   public static final String SENTENCE_START = "<SENTENCE_START>";

   SortedMap<Integer, FullToken> fullTokenListWithPos(char[] p0);

   AbstractFileFilter getFileFilter();

   String getIdentifierType();

   Collection<String> getKeywordTypes();

   Collection<String> getLiteralTypes();

   FullToken getTokenFromString(String p0);

   List<FullToken> getTokenListFromCode(char[] p0);

   List<FullToken> getTokenListFromCode(File p0) throws IOException;

   List<String> tokenListFromCode(char[] p0);

   List<String> tokenListFromCode(File p0) throws IOException;

   SortedMap<Integer, String> tokenListWithPos(char[] p0);

   SortedMap<Integer, FullToken> tokenListWithPos(File p0) throws IOException;

   public static class FullToken implements Serializable
   {
	 private static final long serialVersionUID = -49456240173307314L;
	 public static final Function<FullToken, String> TOKEN_NAME_CONVERTER;
	 public final String token;
	 public final String tokenType;
	 static
	 {
	    TOKEN_NAME_CONVERTER = new Function<FullToken, String>()
	    {
		  @Override
		  public String apply(FullToken input)
		  {
			return input.token;
		  }
	    };
	 }

	 public FullToken(FullToken other) {
	    super();
	    this.token = other.token;
	    this.tokenType = other.tokenType;
	 }

	 public FullToken(String tokName, String tokType) {
	    super();
	    this.token = tokName;
	    this.tokenType = tokType;
	 }

	 @Override
	 public boolean equals(Object obj)
	 {
	    FullToken other;
	    if (!(obj instanceof FullToken))
	    {
		  return false;
	    }
	    other = (FullToken) obj;
	    return other.token.equals(this.token) && other.tokenType.equals(this.tokenType);
	 }

	 @Override
	 public int hashCode()
	 {
	    return Objects.hashCode(this.token, this.tokenType);
	 }

	 @Override
	 public String toString()
	 {
	    return String.valueOf(this.token) + " (" + this.tokenType + ")";
	 }
   }
}