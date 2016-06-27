package test.similary;

import java.util.ArrayList;
import java.util.HashMap;

import test.bean.ItemNode;
import test.bean.UserNode;
import test.weight.WeightUtil;

/**
 * 对数似然比
 * 举例：用户1和用户2都浏览的商品k11=2个，用户1还浏览其他k12=5，用户2还浏览其他k21=8,都没有被这2个用户浏览的商品985，那-2logλ=10.55
 * 如果存在用户1和用户3，它们k11,k12,k21,k22分别是2,5,18,975，那么-2logλ=7.65
 * 如果存在用户1和用户4，它们k11,k12,k21,k22分别是1,6,1,993，那么-2logλ=10.07
 * @author Administrator
 *
 */
public class LogLikelihoodRatioSimilary implements SimilaryUtil{

	/**
	 * 物品总数
	 */
	private int itemSum=-1;
	
	private int itemSumInit=-1;
	
	public double logLikelihoodRatio(int k11, int k12, int k21, int k22) {
        double rowEntropy = entropy(k11, k12) + entropy(k21, k22);
       // System.out.println(entropy(k11, k12) +"\t"+entropy(k21, k22));
        double columnEntropy = entropy(k11, k21) + entropy(k12, k22);
       // System.out.println(entropy(k11, k21) +"\t"+entropy(k12, k22));
        double matrixEntropy = entropy(k11, k12, k21, k22);
        return 2 * (matrixEntropy - rowEntropy - columnEntropy);
    }
     
    public double entropy(int... elements) {
        double sum = 0;
        for (int element : elements) {
            sum += element;
        }
        double result = 0.0;
        for (int x : elements) {
            if (x < 0) {
                throw new IllegalArgumentException(
                    "Should not have negative count for entropy computation: (" + x + ')');
            }
            int zeroFlag = (x == 0 ? 1 : 0);
            result += x * Math.log((x + zeroFlag) / sum);
        }
        return -result;
    }
    public LogLikelihoodRatioSimilary()
    {
    }
    public LogLikelihoodRatioSimilary(int itemSum)
    {
    	this.itemSumInit=itemSum;
    	this.itemSum=itemSum;
    }
    public void setItemSum(int itemSum)
    {
    	this.itemSumInit=itemSum;
    	this.itemSum=itemSum;
    }
    
	@Override
	public SimilaryUtil getSimilaryFunc() {
		// TODO Auto-generated method stub
		return this;
	}
	@Override
	public float getSimilary(UserNode userNode1,UserNode userNode2,WeightUtil weight)
	{
		//加用户损失没有用
		if(itemSumInit>=0)
		{
			//如果物品数被设置 则 不使用设置物品总数，
		}else{
			//否则使用 该类下所有用户的全部物品计算
		this.itemSum=weight.size();
		}
		return getSimilary(userNode1,userNode2);
	}

	public float getSimilary(UserNode userNode1, UserNode userNode2) {
		// TODO Auto-generated method stub
		ArrayList<ItemNode> item1=userNode1.getItems();
		ArrayList<ItemNode> item2=userNode2.getItems();
		int item2Size=userNode1.size();
		if(item1.size()==0 || item2Size==0)
		{
			return 0f;
		}
		int k11=0,k12=0,k21=0,k22=0;
		
		for(ItemNode item:item1)
		{
			ItemNode it=userNode2.getItem(item.getItemId());
			if(it==null)
			{
				k12++;
				continue;
			}
			k11++;
		}
		for(ItemNode item:item2)
		{
			ItemNode it=userNode1.getItem(item.getItemId());
			if(it==null)
			{
				k21++;
				continue;
			}
		}
		k22=this.itemSum-k11-k12-k21;
		//float re=(float)logLikelihoodRatio(k11,k12,k21,k22);
		return (float) logLikelihoodRatio(k11,k12,k21,k22);
	}

	@Override
	public float getSimilary(UserNode userNode1, UserNode userNode2,
			HashMap<Long, long[]> map,WeightUtil weight,float[] weightPower) {
		// TODO Auto-generated method stub
		return 0;
	}
}
