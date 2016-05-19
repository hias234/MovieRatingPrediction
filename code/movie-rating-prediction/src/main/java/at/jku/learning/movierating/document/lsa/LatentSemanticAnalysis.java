package at.jku.learning.movierating.document.lsa;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;


public class LatentSemanticAnalysis {
  private final int kFeatures; //Features
  private final Map<Long, Map<String, Double>> weightedDocs;
  private final Set<String> allTerms = new HashSet<String>(); 
  
  public LatentSemanticAnalysis(int k, Map<Long, Map<String, Double>> weightedDocs) {
    this.kFeatures = k;
    this.weightedDocs = weightedDocs;
  }
  
  public Set<String> getAllTerms(){
    return allTerms;
  }

  //What about the performance?
  public RealMatrix getModelCorpus(){
    
    //Get all Terms 
    for(Entry<Long, Map<String, Double>> entry: weightedDocs.entrySet()) {
      entry.getValue().forEach((k,v) -> allTerms.add(k));
    }
    
    double[][] data = new double[weightedDocs.size()][allTerms.size()];
    int i=0;
    
    for(Entry<Long, Map<String, Double>> entry: weightedDocs.entrySet()) {
      
      int j = 0;
      for(String term : allTerms) {
        Map<String,Double> termMap = entry.getValue();
        data[i][j] = termMap.containsKey(term) ? termMap.get(term) : 0.0;
        j++;
      }
      i++;
    }
        
    return MatrixUtils.createRealMatrix(data);
  }
  
  public RealMatrix performSVD(){
    //Split model corpus matrix into: A = T * Sigma * D(T)
    //T: term-concept matrix - Eigenvectors of X*X(T)
    //Sigma: diagonal matrix with singular values - Eigenvalues of T and D
    //D: document matrix transpose - Eigenvectors of X(T)*X
    //k: ranks (between. 100-1000)
    
    RealMatrix xMatrix = getModelCorpus();
    SingularValueDecomposition svd = new SingularValueDecomposition(xMatrix);
    
    
    RealMatrix documentMatrix = svd.getU(); //Eigenvectors D
    RealMatrix termMatrix = svd.getV(); //Eigenvectors T
    RealMatrix sigmaMatrix = svd.getS(); //Eigenvalues
    
    //Get shortened SubMatrix with k-Ranks.
    //TODO automatically sorted?
    sigmaMatrix = sigmaMatrix.getSubMatrix(1, kFeatures, 1, kFeatures);
    documentMatrix = documentMatrix.getSubMatrix(1, documentMatrix.getRowDimension(), 1, kFeatures);
    termMatrix = termMatrix.getSubMatrix(1, documentMatrix.getRowDimension(), 1, kFeatures);
    
    //Multiply T * S * D(T) = Original Matrix with Dimensionality reduction through k
    return termMatrix.multiply(documentMatrix.transpose().preMultiply(sigmaMatrix));
  }
  
  
}
