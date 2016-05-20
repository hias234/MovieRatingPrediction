package at.jku.learning.movierating.document.lsa;

import org.apache.commons.math3.linear.RealMatrix;

public interface LSA {
  
  void computeModelCorpus();
  void performSVD();
  
}
