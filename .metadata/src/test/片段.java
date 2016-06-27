package test;

public class 片段 {
//	@Override
//	  public float estimatePreference(long userID, long itemID) throws TasteException {
//	    DataModel model = getDataModel();
//	    Float actualPref = model.getPreferenceValue(userID, itemID);
//	    if (actualPref != null) {
//	      return actualPref;
//	    }
//	    long[] theNeighborhood = neighborhood.getUserNeighborhood(userID);
//	    return doEstimatePreference(userID, theNeighborhood, itemID);
//	  }
//	
//	 protected float doEstimatePreference(long theUserID, long[] theNeighborhood, long itemID) throws TasteException {
//	    if (theNeighborhood.length == 0) {
//	      return Float.NaN;
//	    }
//	    DataModel dataModel = getDataModel();
//	    double preference = 0.0;
//	    double totalSimilarity = 0.0;
//	    int count = 0;
//	    for (long userID : theNeighborhood) {
//	      if (userID != theUserID) {
//	        // See GenericItemBasedRecommender.doEstimatePreference() too
//	        Float pref = dataModel.getPreferenceValue(userID, itemID);
//	        if (pref != null) {
//	          double theSimilarity = similarity.userSimilarity(theUserID, userID);
//	          if (!Double.isNaN(theSimilarity)) {
//	            preference += theSimilarity * pref;
//	            totalSimilarity += theSimilarity;
//	            count++;
//	          }
//	        }
//	      }
//	    }
//	    // Throw out the estimate if it was based on no data points, of course, but also if based on
//	    // just one. This is a bit of a band-aid on the 'stock' item-based algorithm for the moment.
//	    // The reason is that in this case the estimate is, simply, the user's rating for one item
//	    // that happened to have a defined similarity. The similarity score doesn't matter, and that
//	    // seems like a bad situation.
//	    if (count <= 1) {
//	      return Float.NaN;
//	    }
//	    float estimate = (float) (preference / totalSimilarity);
//	    if (capper != null) {
//	      estimate = capper.capEstimate(estimate);
//	    }
//	    return estimate;
//	  }
//
//	public static void svd(DataModel dataModel) throws TasteException {
//	        RecommenderBuilder recommenderBuilder = RecommendFactory.svdRecommender(new ALSWRFactorizer(dataModel, 10, 0.05, 10));
//	
//	        RecommendFactory.evaluate(RecommendFactory.EVALUATOR.AVERAGE_ABSOLUTE_DIFFERENCE, recommenderBuilder, null, dataModel, 0.7);
//	        RecommendFactory.statsEvaluator(recommenderBuilder, null, dataModel, 2);
//	
//	        LongPrimitiveIterator iter = dataModel.getUserIDs();
//	        while (iter.hasNext()) {
//	            long uid = iter.nextLong();
//	            List list = recommenderBuilder.buildRecommender(dataModel).recommend(uid, RECOMMENDER_NUM);
//	            RecommendFactory.showItems(uid, list, true);
//	        }
//	    }
	
}

