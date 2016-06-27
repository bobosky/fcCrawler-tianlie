package com.analysis;


import java.util.ArrayList;
import java.util.Random;
import java.util.Vector;

/**
 * kmeans
 * @author Administrator
 *
 */
public class Kmeans{
	public double[][] matrix;//数据源
	public int clusterCount=1;//聚类数量
	/**
	 *每个样本对应的所属类别
	 */
	public int[] cluster;//
	public int[] oldcluster;//
	
	public boolean[] clusterCenterFlag;
	/**
	 * 文件长度
	 */
	public int fileLength=0;
	/**
	 * 维度
	 */
	public int dimensionalityCount=0;
	
	/**
	 * 存储当前类在聚类过程中的最大半径
	 */
	public double[] clusterTempDouble;
	/**
	 * conopy算法中运用到的t1>t2
	 */
	public double T1=0.3;
	public double T2=0.1;
	/**
	 * 迭代次数
	 */
	public int computeCount=0;
	/**
	 * 存储误差
	 */
	public double errorOld=0.0;
	/**
	 * cannopy簇，-1为没计算,1为<t2，强,2为<t1 弱
	 */
	public int[] canopyCluster;
	/**
	 * 存储维度的最大最小值
	 */
	public ArrayList<double[]> maxMinValue=new ArrayList<double[]>();
	
	/**
	 * 存储聚类数级对应p的类中心
	 */
	public ArrayList<double[]> clusterCenter=new ArrayList<double[]>();
	public ArrayList<double[]> oldclusterCenter=new ArrayList<double[]>();
	
	/**
	 * 存储在随机cannopy中聚类数量
	 */
	public ArrayList<Integer> canopyCount=new ArrayList<Integer>();
	
	/**
	 * 存储在随机cannopy聚类数对应的聚类中心
	 */
	public ArrayList<ArrayList<double[]>> canopyCountCenter=new ArrayList<ArrayList<double[]>>();
	
	/**
	 * 存储加权半径
	 */
	public double oldoldRadius=0.0;
	public double oldRadius=0.0;
	public double newRadius=0.0;
	
	/**
	 * 存储计算的半径
	 */
	public double oldoldComputeRadius=0.0;
	public double oldComputeRadius=0.0;
	public double newComputeRadius=0.0;
	/**
	 * 用于存储 第几次聚类中每个类中心的最长半径
	 */
	public ArrayList<ArrayList<Double>> cannopyRadius=new ArrayList<ArrayList<Double>>();
	/**
	 * 
	 * @param matrix
	 * @param T2
	 * @param T1
	 */
	public Kmeans(double[][] matrix,double T2,double T1)
	{
		this.T2=T2;
		this.T1=T1;
		this.matrix = matrix.clone();
		this.matrix=new double[matrix.length][matrix[0].length];
		for(int i=0;i<matrix.length;i++)
		{
			this.matrix[i]=matrix[i].clone();
		}
		fileLength=matrix.length;
		canopyCluster=new int[fileLength];
		cluster=new int[fileLength];
		try{
			dimensionalityCount=matrix[0].length;
		}
		catch(Exception e)
		{
			System.out.println("输入数据错误");
			System.exit(1);
		}
		for(int i=0;i<fileLength;i++)
		{
			canopyCluster[i]=-1;
			cluster[i]=-1;
		}
	}
	
	public Kmeans(double[][] matrix)
	{
		try{
				dimensionalityCount=matrix[0].length;
			}
			catch(Exception e)
			{
				System.out.println("输入数据错误");
				System.exit(1);
			}
			
			
			this.matrix = matrix.clone();
			this.matrix=new double[matrix.length][matrix[0].length];
			for(int i=0;i<matrix.length;i++)
			{
				this.matrix[i]=matrix[i].clone();
			}
			
		fileLength=matrix.length;
		canopyCluster=new int[fileLength];
		cluster=new int[fileLength];
		
		for(int i=0;i<fileLength;i++)
		{
			canopyCluster[i]=-1;
			cluster[i]=-1;
		}
	}
	/**
	 * 标准化到0-1
	 */
	public void stander()
	{
		System.out.println("标准化值");
		for(int i=0;i<this.dimensionalityCount;i++)
		{
			double[] mami={Double.MIN_VALUE,Double.MAX_VALUE};
			for(int j=0;j<this.fileLength;j++)
			{
				if(matrix[j][i]>mami[0])
				{
					mami[0]=matrix[j][i];
				}
				if(matrix[j][i]<mami[1])
				{
					mami[1]=matrix[j][i];
				}
			}
			this.maxMinValue.add(mami);
			//标准化
			for(int j=0;j<this.fileLength;j++)
			{
				matrix[j][i]=(matrix[j][i]-mami[1])/(mami[0]-mami[1]);
				//System.out.print(matrix[j][i]+"\t");
			}
			//System.out.println();
		}
	}
	
	
	/**
	 * 获取聚类个数及初始类中心
	 */
	public void Canopy()
	{
		//判断是否在中心
		while(true)
		{
			
		}
	}
	
	
	
	/**
	 * 执行
	 */
	public void clusterCompute()
	{
	
		System.out.println("初始");
		for(int i=0;i<fileLength;i++)
		{
			System.out.print(this.cluster[i]+"\t");
		}
		System.out.println();
		for(int i=0;i<this.clusterCount;i++)
		{
			for(int j=0;j<this.dimensionalityCount;j++)
			{
				//System.out.print(this.clusterCenter.get(i)[j]+"\t");
			}
			//System.out.println();
		}
		double errorNew=0.0;
		while(true)
		{
			int flag=0;
			if(Math.abs(oldRadius-newRadius)<1E-10 && this.clusterCount==2)
			{
				//如果满足条件则跳出
				System.out.println("跳出:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
				oldoldRadius=oldRadius;
				oldRadius=newRadius;
				newRadius=0.0;
				break;
			}
			if(Math.abs(oldRadius-newRadius)<(Math.abs(oldRadius-this.oldoldRadius)/3)&& newRadius-oldRadius<0 && this.clusterCount>2)
			{
				//如果满足条件则跳出
				System.out.println("跳出:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
				oldoldRadius=oldRadius;
				oldRadius=newRadius;
				newRadius=0.0;
				break;
			}
			if(clusterCount==3)
			{
				System.out.println("444:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
			}
			if(clusterCount==4)
			{
				System.out.println("444:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
			}
			if(clusterCount==5)
			{
				System.out.println("444:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
			}
			if(clusterCount==6)
			{
			
				System.out.println("444:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
				break;
			}
			oldoldRadius=oldRadius;
			oldRadius=newRadius;
			newRadius=0.0;
			boolean computeFlag=false;
			clusterTempDouble=new double[clusterCount];
		
			while(true)
			{
				for(int i=0;i<clusterCount;i++)
				{
					clusterTempDouble[i]=Double.MIN_VALUE;
				}
				//执行计算
				flag++;
				errorOld=errorNew;
				 errorNew=0.0;
				for(int i=0;i<fileLength;i++)
				{
					double valueUp=Double.MAX_VALUE;
					//this.cluster[i]=-1;
					for(int k=0;k<this.clusterCount;k++)
					{
						//执行
						if(clusterCenterFlag[k]==false)
						{//如果中心点为无效点则跳过
							continue;
						}
						double value=0.0;
						for(int j=0;j<this.dimensionalityCount;j++)
						{
							value+=Math.pow(matrix[i][j]-clusterCenter.get(k)[j],2.0);
						}
						if(valueUp>value)
						{
							valueUp=value;
							this.cluster[i]=k;
						}
					}
					errorNew+=Math.sqrt(valueUp);
					if(errorNew>this.clusterTempDouble[cluster[i]])
					{
						clusterTempDouble[cluster[i]]=errorNew;
					}
					
				}
				//System.out.println("第"+flag+"次");
				//for(int i=0;i<fileLength;i++)
				//{
				//	System.out.print(this.cluster[i]+"\t");
				//}
				//System.out.println();
				
				//重新计算聚类中心
				for(int i=0;i<this.clusterCount;i++)
				{
					int count=0;
					double[] ll=new double[fileLength];
					for(int j=0;j<fileLength;j++)
					{
						if(cluster[j]==i)
						{
							count++;
							for(int m=0;m<this.dimensionalityCount;m++)
							{
								ll[m]+=matrix[j][m];
							}
						}
					}
					if(count<=1)
					{
						//剔除掉cluster类
						//并将分类标记为-1
						clusterCenterFlag[i]=false;
					//	System.out.println("标记为无效："+i+"\t----------------------------"+count);
						continue;
						
					}
					if(count>1)
					{
						for(int j=0;j<this.dimensionalityCount;j++)
						{
							clusterCenter.get(i)[j]=ll[j]/count;
						}
					}
				}
				for(int i=0;i<this.clusterCount;i++)
				{
					for(int j=0;j<this.dimensionalityCount;j++)
					{
					//	System.out.print(this.clusterCenter.get(i)[j]+"\t");
					}
					//System.out.println();
				}
				if(flag==1)
				{
					continue;
				}
				else
				{
					if(Math.abs(errorNew-errorOld)<1E-7)
					{
						//System.out.println("收敛:聚类算法执行次数:"+flag+"\t方差和为:"+errorNew);
						//收敛的时候需要判断
						newComputeRadius=0.0;
						for(int zn=0;zn<this.clusterCount;zn++)
						{
							if(clusterCenterFlag[zn]==true)
							{
								newComputeRadius+=this.clusterTempDouble[zn];
							}
						}
						if(Math.abs(oldComputeRadius-newComputeRadius)<1E-10 && this.clusterCount==2)
						{
							//如果满足条件则跳出
							System.out.println("跳出:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
							oldoldComputeRadius=oldComputeRadius;
							oldComputeRadius=newComputeRadius;
							newComputeRadius=0.0;
							computeFlag=true;//标记为最终
						}
						if(Math.abs(oldComputeRadius-newComputeRadius)<(Math.abs(oldComputeRadius-this.oldoldComputeRadius)/3)&& newRadius-oldRadius<0 && this.clusterCount>2)
						{
							//如果满足条件则跳出
							System.out.println("跳出:"+"\t"+oldoldRadius+"\t"+oldRadius+"\t"+newRadius+"\t"+clusterCount);
							oldoldComputeRadius=oldComputeRadius;
							oldComputeRadius=newComputeRadius;
							newComputeRadius=0.0;
							computeFlag=true;//标记为最终
						}
						else
						{
							oldoldComputeRadius=oldComputeRadius;
							oldComputeRadius=newComputeRadius;
							newComputeRadius=0.0;
						}
						
						break;
					}
					errorOld=errorNew;
				}
			}
			if(computeFlag==true)
			{
				//如果正确则选择 clusterCount--个
				clusterCount--;
				cluster=new int[fileLength];
				for(int i=0;i<this.fileLength;i++)
				{
					this.cluster[i]=this.oldcluster[i];
				}
				this.clusterCenter=new ArrayList<double[]>();
				for(int i=0;i<this.clusterCount;i++)
				{
					this.clusterCenter.add(this.oldclusterCenter.get(i).clone());
				}
				System.out.println("计算后挑出");
				break;
			}
			oldcluster=new int[fileLength];
			for(int i=0;i<this.fileLength;i++)
			{
				this.oldcluster[i]=this.cluster[i];
			}
			this.oldclusterCenter=new ArrayList<double[]>();
			for(int i=0;i<this.clusterCount;i++)
			{
				this.oldclusterCenter.add(this.clusterCenter.get(i).clone());
			}
			clusterCount++;
			Canopy();
		}
	}
	

	/**
	 * 打印方法
	 */
	public void print()
	{
		for(int i=0;i<fileLength;i++)
		{
			System.out.print(this.cluster[i]+"\t");
		}
		System.out.println();
	}
	
	
	
	/**
	 *执行方法
	 * @param args
	 */
	public void run()
	{
		stander();
		Canopy();
		this.clusterCompute();
	}
	public static void main(String[] args) {
		double[][] data=null;
		Kmeans kmeans=new Kmeans(data,0.1,0.2);
		kmeans.run();
		kmeans.print();
	}
}
