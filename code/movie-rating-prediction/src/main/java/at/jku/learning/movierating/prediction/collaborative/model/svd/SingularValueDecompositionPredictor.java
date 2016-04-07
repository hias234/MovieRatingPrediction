package at.jku.learning.movierating.prediction.collaborative.model.svd;

import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import at.jku.learning.movierating.model.FeatureSVD;
import at.jku.learning.movierating.model.Rating;
import at.jku.learning.movierating.prediction.Predictor;

/**
 * Singular Value Decomposition implementation based on Simon Funks Netflix-approach 
 * and the FunkSVD implementation of the LensKit project. 
 * 
 */

public class SingularValueDecompositionPredictor implements Predictor {
  
  private List<Rating> trainingSet;
  private Set<FeatureSVD> featureSet;
  private Map<Long, Double> movieMeanMap;
  private Map<Long, Double> userOffsetMap;
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
    prediction = getBaseline(movieId, userId) + sum;
    
    //System.out.println(prediction);
    
    return prediction;
  }

  @Override
  public void setTrainingSet(List<Rating> trainingSet) {
    this.trainingSet = trainingSet;
    train();
  }
  
  private void train() {
    FeatureSVD feature;
    featureSet = new HashSet<FeatureSVD>();
    computePreTrain();
    
    for (int featureRank=1; featureRank <= this.featureLimit; ++featureRank) {
      feature = new FeatureSVD(featureRank); //Create new Feature
      featureSet.add(feature);
      
      //Eventually check if RMSE changes just a little between two epochs and then skip instead of fixed cycles
      //Alternatively check if the sum of changes of feature values are rather small
      for (int epoch=1; epoch <= this.cycles; ++epoch) {
        for(Rating rating : this.trainingSet) {
          // set feature
          
          //Calculate the error
          Double error = rating.getRating()- predictRating(rating.getUserId(),rating.getMovieId());
          
          //Update the features
          Double userFeature = feature.getUserFeature(rating.getUserId());
          userFeature += this.learningRate*(error*feature.getItemFeature(rating.getMovieId()) 
              - (regularizationTerm*feature.getUserFeature(rating.getUserId())));
          
          Double itemFeature = feature.getItemFeature(rating.getMovieId());
          itemFeature += this.learningRate*(error*feature.getUserFeature(rating.getUserId()) 
              - regularizationTerm*feature.getItemFeature(rating.getMovieId()));
          
          feature.setUserFeature(rating.getUserId(), userFeature);
          feature.setItemFeature(rating.getMovieId(), itemFeature);
          
        }
      }
      
      //System.out.println(feature.toString());
    }
  }
  
  private Double getBaseline(Long movieId, Long userId) {
    return movieMeanMap.get(movieId) + userOffsetMap.get(userId);
  }
  
  private void computePreTrain() {
    //Precompute the average movie rating and user offset for a good starting point
    
    Map<Long, List<Rating>> trainingSetByUserId;
    Map<Long, List<Rating>> trainingSetByMovieId;
    movieMeanMap = new HashMap<Long,Double>();
    userOffsetMap = new HashMap<Long,Double>();
    
    Long movieId=0L;
    Long userId=0L;
    Long sumMovie = 0L;
    Long sumUser = 0L;
    Long countMovie = 0L;
    Long countUser = 0L;
    Double globalMovieRatingMean = 0.0;
    Double flattenedMean = 0.0;
    Double userOffset = 0.0;
    int k = ParameterSVD.INSTANCE.meanMultiplier;
    
    //Flattened mean of movies
    trainingSetByMovieId = this.trainingSet.stream().collect(Collectors.groupingBy(i -> i.getMovieId()));
    globalMovieRatingMean = this.trainingSet.stream().mapToInt(r -> r.getRating()).average().getAsDouble();
    
    for (Map.Entry<Long, List<Rating>> movieRatings : trainingSetByMovieId.entrySet()) {
      movieId = movieRatings.getKey();
      sumMovie = trainingSetByMovieId.get(movieId).stream().mapToLong(r -> r.getRating()).sum();
      countMovie = trainingSetByMovieId.get(movieId).stream().mapToLong(r -> r.getRating()).count();
      
      flattenedMean = (globalMovieRatingMean*k + sumMovie)/(k + countMovie);
      movieMeanMap.put(movieId, flattenedMean);
    }
    
    //Flattened offset of users
    trainingSetByUserId = this.trainingSet.stream().collect(Collectors.groupingBy(i -> i.getUserId()));
    for (Map.Entry<Long, List<Rating>> userRatings : trainingSetByUserId.entrySet()) {
      userId = userRatings.getKey();
      sumUser = trainingSetByUserId.get(userId).stream().mapToLong(r -> r.getRating()).sum();
      countUser = trainingSetByUserId.get(userId).stream().mapToLong(r -> r.getRating()).count();
      
      flattenedMean = (globalMovieRatingMean*k + sumUser)/(k + countUser);
      userOffset = globalMovieRatingMean - flattenedMean;
      userOffsetMap.put(userId, userOffset);
    }

    
    
    System.out.println("Pre-training done." + movieMeanMap.size() + " " + userOffsetMap.size());
    
  }
  
  @Override
	public String toString() {
		return "SVD cycles=" + cycles;
	}
  
  private enum ParameterSVD {
    INSTANCE;
    private final int cycles = 50; //Set Default to 100
    private final Double learningRate = 0.001;
    private final int featureLimit = 20; //Set Default to 30
    private final Double regularizationTerm = 0.02;
    private final int meanMultiplier = 15; //Eventually set to 25
  }

}
