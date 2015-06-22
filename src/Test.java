import java.util.HashMap;
import java.util.Map;
import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;


public class Test
{
    public static void main(String[] args)
    {
	  Map<String,Integer> commitStat= new HashMap<String,Integer>();
	  String auth="wang";
	  commitStat.compute(auth, (k,v)->k==null?1:v+1);
    }
}
