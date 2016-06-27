package test;

public class Sort {
	/**
	 * 快速排序方法 int[] 原理:从个位开始 先排序 从前往后 再10位 从前往后直到没有则排序结束 顺序放入新数组,直到全部拿完
	 * 
	 * @param list为
	 *            list的相关 extend类型
	 * @param left
	 *            选取左侧标记
	 * @param right
	 *            选取右侧标记
	 * @param flag如果为true则表示
	 *            desc false为 asc
	 * @return
	 * @throws ClassNotFoundException
	 */
	public static void quickSort(float[] arr, int left, int right, boolean flag) {
		if (flag) {
			// 拆成多组
			int i = left, j = right;
			float middle, iTemp;
			middle = arr[(left + right) / 2];
			do {
				while ((arr[i] > middle) && (i < right))
					// 从左扫描小于中值的树
					i++;
				while ((arr[j] < middle) && (j > left))
					// 从右扫描大于中值的数
					j--;
				if (i <= j) {
					iTemp = arr[i];
					arr[i] = arr[j];
					arr[j] = iTemp;
					i++;
					j--;
				}
			} while (i <= j);
			// 当左边部分有值(left<j)，递归左半边
			if (left < j)
				quickSort(arr, left, j, flag);
			// 当右边部分有值(right>i)，递归右半边
			if (right > i)
				quickSort(arr, i, right, flag);
		} else {

			// 拆成多组
			int i = left, j = right;
			float middle, iTemp;
			middle = arr[(left + right) / 2];
			do {
				while ((arr[i] < middle) && (i < right))
					// 从左扫描大于中值的数
					i++;
				while ((arr[j] > middle) && (j > left))
					// 从右扫描小于中值的数
					j--;
				if (i <= j) {
					iTemp = arr[i];
					arr[i] = arr[j];
					arr[j] = iTemp;
					i++;
					j--;
				}
			} while (i <= j);
			// 当左边部分有值(left<j)，递归左半边
			if (left < j)
				quickSort(arr, left, j, flag);
			// 当右边部分有值(right>i)，递归右半边
			if (right > i)
				quickSort(arr, i, right, flag);
		}
	}
}
