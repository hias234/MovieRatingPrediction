package at.jku.learning.movierating.prediction.collaborative.model.svd;

import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import at.jku.learning.movierating.model.FeatureSVD;
import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

/**
 * Singular Value Decomposition implementation based on Simon Funks Netflix-approach 
 * and the FunkSVD implementation of the LensKit. 
 * 
 */

public class SingularValueDecompositionPredictor implements Predictor {
  
  private List<Rating> trainingSet;
  private Set<FeatureSVD> featureSet = new HashSet<FeatureSVD>();
  private final int cycles;
  private final int featureLimit;
  private final Double learningRate;
  private final Double regularizationTerm;

  
  public SingularValueDecompositionPredictor(){
    this.cycles = ParameterSVD.INSTANCE.cycles;
    this.learningRate = ParameterSVD.INSTANCE.learningRate;
    this.featureLimit = ParameterSVD.INSTANCE.featureLimit;
    this.regularizationTerm = ParameterSVD.INSTANCE.regularizationTerm;
  }
  
  public SingularValueDecompositionPredictor(int cycles) {
    this.cycles = cycles;
    this.learningRate = ParameterSVD.INSTANCE.learningRate;
    this.featureLimit = ParameterSVD.INSTANCE.featureLimit;
    this.regularizationTerm = ParameterSVD.INSTANCE.regularizationTerm;
  } 
  
  public SingularValueDecompositionPredictor(int cycles, Double learningRate, int featureLimit) {
    this.cycles = cycles;
    this.learningRate = learningRate;
    this.featureLimit = featureLimit;
    this.regularizationTerm = ParameterSVD.INSTANCE.regularizationTerm;
  }

  @Override
  public Double predictRating(Long userId, Long movieId) {
    // TODO P = mean + userOffset + itemOffset + user_features * item_features
    Double prediction;
    Double sum = 0.0;
    //Calculate the dot product of user and item features
    for (FeatureSVD feature : featureSet) {
      sum += feature.getUserFeature(userId)*feature.getItemFeature(movieId);
    }
    //Add mean + userOffset + itemOffset
    prediction = getBaseline() + sum;
    
    return prediction;
  }

  @Override
  public void setTrainingSet(List<Rating> trainingSet) {
    this.trainingSet = trainingSet;
    train();
  }
  
  private void train() {
    
    Collections.shuffle(trainingSet);
    
    for (int featureRank=1; featureRank <= this.featureLimit; ++featureRank) {
      FeatureSVD feature = new FeatureSVD(featureRank); //Create new Feature
      featureSet.add(feature);
      
      //Eventually check if RMSE changes just a little between two epochs and then skip instead of fixed cycles
      //Alternatively check if the sum of changes of feature values are rather small
      for (int epoch=1; epoch <= this.cycles; ++epoch) {
        for(Rating rating : this.trainingSet) {
          // set feature
          
          //Calculate the error
          Double error = predictRating(rating.getUserId(),rating.getMovieId()) - rating.getRating();
          
          //Update the features
          Double userFeature = feature.getUserFeature(rating.getUserId());
          userFeature += this.learningRate*(error*feature.getItemFeature(rating.getMovieId()) 
              - regularizationTerm*feature.getUserFeature(rating.getUserId()));
          
          Double itemFeature = feature.getItemFeature(rating.getMovieId());
          itemFeature += this.learningRate*(error*feature.getUserFeature(rating.getUserId()) 
              - regularizationTerm*feature.getItemFeature(rating.getMovieId()));
          
          //System.out.println(userFeature + " | " + itemFeature);
          
          feature.setUserFeature(rating.getUserId(), userFeature);
          feature.setItemFeature(rating.getMovieId(), itemFeature);
          
        }
      }
    }
  }
  
  private Double getBaseline() {
    //TODO Create Meaningful-Baseline
    return 0.0;
  }
  
  private enum ParameterSVD {
    INSTANCE;
    private final int cycles = 10; //Set Default to 100
    private final Double learningRate = 0.001;
    private final int featureLimit = 3; //Set Default to 30
    private final Double regularizationTerm = 0.015;
  }

}
