package smellminer.engine.smellanalyze;


public class CliffDeltaTest
{
   public double test(double[] data1,double[] data2)
   {
	 int lx=data1.length;
	 int ly=data2.length;
	 double[][] mat=new double[lx][ly];
	 for(int i=0;i<lx;i++)
	    for(int j=0;j<ly;j++)mat[i][j]=0.0;
	 for (int i = 0;i<lx;i++)
		for(int j = 0;j<ly;j++)
		{
			if( data1[i] > data2[j]) mat[i][j] = 1;
			else if (data2[j] > data1[i])mat[i][j] = -1;
		}
	 double sum=0.0;
	 for(int i=0;i<lx;i++)
	    for(int j=0;j<ly;j++)sum+=mat[i][j];
		 
	  double d = sum / (lx * ly);
	  
	  return d;
   }   
}

/**
 * http://markread.info/2014/04/free-statistics-code/
function d = CliffDelta(X,Y)
% Calculates Cliff's Delta function, a non-parametric effect magnitude
% test. See: http://revistas.javeriana.edu.co/index.php/revPsycho/article/viewFile/643/1092
% for implementation details. 

% calculate length of vetors. 
lx = length(X);
ly = length(Y);

% comparison matrix. First dimension represnt elements in X, the second elements in Y
% Values calculated as follows:
% mat(i,j) = 1 if X(i) > Y(j), zero if they are equal, and -1 if X(i) < Y(j)
mat = zeros(lx, ly);	

% perform all the comparisons. 
for i = 1:lx
	for j = 1:ly
		if X(i) > Y(j)
			mat(i,j) = 1;
		elseif Y(j) > X(i)
			mat(i,j) = -1;
		end
	end
end

% calculate delta. 
d = sum(mat(:)) / (lx * ly)
*/
