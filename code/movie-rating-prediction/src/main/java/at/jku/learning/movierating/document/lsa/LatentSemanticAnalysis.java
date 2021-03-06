package at.jku.learning.movierating.document.lsa;

import org.apache.commons.math3.linear.RealMatrix;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.apache.commons.math3.linear.MatrixUtils;
import org.apache.commons.math3.linear.SingularValueDecomposition;




public class LatentSemanticAnalysis implements LSA{
  private int kFeatures; //Features
  private final Map<Long, Map<String, Double>> weightedDocs;
  private final Set<String> allTerms = new HashSet<String>(); 
  private RealMatrix model;
  private RealMatrix documentMatrix;
  private RealMatrix termMatrix;
  private RealMatrix sigmaMatrix;
  
  
  public LatentSemanticAnalysis(Map<Long, Map<String, Double>> weightedDocs) {

    this.weightedDocs = weightedDocs;
    this.computeModelCorpus();
    this.performSVD();
  }
  
  public RealMatrix getModel() {
    return model;
  }

  public void setModel(RealMatrix model) {
    this.model = model;
  }

  
  public Set<String> getAllTerms(){
    return allTerms;
  }
  
  public void computeModelCorpus(){
    this.setModel(getModelCorpus());
  }

  //What about the performance?
  protected RealMatrix getModelCorpus(){
    
    //Get all Terms to create a matrix
    for(Entry<Long, Map<String, Double>> entry: weightedDocs.entrySet()) {
      entry.getValue().forEach((k,v) -> allTerms.add(k));
    }
    
    double[][] data = new double[weightedDocs.size()][allTerms.size()];
    int i=0;
    
    for(Entry<Long, Map<String, Double>> entry: weightedDocs.entrySet()) {
      
      int j = 0;
      Map<String,Double> termMap = entry.getValue();
      for(String term : allTerms) { 
        data[i][j] = termMap.containsKey(term) ? termMap.get(term) : 0.0;
        j++;
      }
      i++;
    }
        
    return MatrixUtils.createRealMatrix(data);
  }
  
  public void performSVD(){
    //Split model corpus matrix into: A = T * Sigma * D(T)
    //T: term-concept matrix - Eigenvectors of X*X(T)
    //Sigma: diagonal matrix with singular values - Eigenvalues of T and D
    //D: document matrix transpose - Eigenvectors of X(T)*X
    //k: ranks (between. 100-1000)
    
    RealMatrix xMatrix = this.getModel();
    SingularValueDecomposition svd = new SingularValueDecomposition(xMatrix);
    
    
    documentMatrix = svd.getU(); //Eigenvectors D
    termMatrix = svd.getV(); //Eigenvectors T
    sigmaMatrix = svd.getS(); //Eigenvalues
    
    /*
    //Get shortened SubMatrix with k-Ranks.
    sigmaMatrix = sigmaMatrix.getSubMatrix(1, kFeatures, 1, kFeatures);
    documentMatrix = documentMatrix.getSubMatrix(1, documentMatrix.getRowDimension(), 1, kFeatures);
    termMatrix = termMatrix.getSubMatrix(1, documentMatrix.getRowDimension(), 1, kFeatures);
    
    //Multiply T * S * D(T) = Original Matrix with Dimensionality reduction through k
    this.setModel(termMatrix.multiply(documentMatrix.transpose().preMultiply(sigmaMatrix)));
    */
  }
  
  public Map<Long, Map<String, Double>>  getWeightedDocs(int k){
    
    this.kFeatures = k;
    
    //Get shortened SubMatrix with k-Ranks.
    sigmaMatrix = sigmaMatrix.getSubMatrix(0, kFeatures-1, 0, kFeatures-1);
    documentMatrix = documentMatrix.getSubMatrix(0, documentMatrix.getRowDimension()-1, 0, kFeatures-1);
    termMatrix = termMatrix.getSubMatrix(0, documentMatrix.getRowDimension()-1, 0, kFeatures-1);
    
    //Multiply T * S * D(T) = Original Matrix with Dimensionality reduction through k
    RealMatrix svdMatrix = termMatrix.multiply(documentMatrix.transpose().preMultiply(sigmaMatrix));
    
    double[][] data = svdMatrix.getData();
    Map<Long, Map<String, Double>> reducedWeightedDocs = new HashMap<Long, Map<String, Double>>();
    int i=0;
    
    for(Entry<Long, Map<String, Double>> entry: weightedDocs.entrySet()) {
      
      int j = 0;
      Map<String,Double> termMap = entry.getValue();
      
      for(String term : allTerms) {
          data[i][j] = termMap.containsKey(term) ? termMap.put(term, data[i][j]) : null;
          j++;
      }
      i++;
      reducedWeightedDocs.put(entry.getKey(), termMap);
    }
    
    return reducedWeightedDocs;
  }
  
  
}
