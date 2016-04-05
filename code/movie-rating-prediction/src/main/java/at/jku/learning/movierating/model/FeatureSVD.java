package at.jku.learning.movierating.model;

import java.util.HashMap;
import java.util.Map;

public class FeatureSVD {
  private int id;
  private Map<Long, Double> userFeature;
  private Map<Long, Double> itemFeature;
  
  private static final Double FEATURE_INIT = 0.1;
  
  public FeatureSVD(int id) {
    this.id = id;   
    userFeature = new HashMap<Long,Double>();
    itemFeature = new HashMap<Long,Double>();
  }
  
  public int getId() {
    return id;
  }
  
  public void setId(int id) {
    this.id = id;
  }
  
  public Double getUserFeature(Long uid) {
    
    if (!userFeature.containsKey(uid)) {
      setUserFeature(uid, getInitialFeatureValue());
    }
    return userFeature.get(uid);
  }
  
  public void setUserFeature(Long uid, Double featureValue) {
    userFeature.put(uid, featureValue);
  }
  
  public Double getItemFeature(Long iid) {
    
    if (!itemFeature.containsKey(iid)) {
      setItemFeature(iid, getInitialFeatureValue());
    }
    return itemFeature.get(iid);
  }
  
  public void setItemFeature(Long iid, Double featureValue) {
    itemFeature.put(iid, featureValue);
  }
  
  //Eventually change it to small random numbers
  private Double getInitialFeatureValue() {
    return FEATURE_INIT;
  }
  
  @Override
  public String toString(){
    StringBuilder builder = new StringBuilder();
    builder.append("Feature ID: " + id + "\n");
    
    for (Map.Entry<Long, Double> singleUserFeature : userFeature.entrySet()) {
      builder.append("User: " + singleUserFeature.getKey() + " | Value: " +singleUserFeature.getValue() + "\n");
    }
    
    for (Map.Entry<Long, Double> singleItemFeature : itemFeature.entrySet()) {
      builder.append("Item: " + singleItemFeature.getKey() + " | Value: " +getItemFeature(singleItemFeature.getKey()) + "\n");
    }
    
    return builder.toString();
  }

}
