





## 2 

二叉树查找树的非递归中序遍历，即排序

```java
// 思路：二叉搜索树中序遍历是排序的
public class Solution {
    int count = 0;  // 记录第几个
    TreeNode KthNode(TreeNode pRoot, int k){
        if(pRoot == null || k < 0) return null;  // 递归条件
        
        TreeNode left = KthNode(pRoot.left,k);
        if(left != null) return left;
        
        count++;
        if(count == k) return pRoot;
        
        TreeNode right = KthNode(pRoot.right,k);
        if(right != null) return right;
        
        return null;
    }
}
```

```java

```





## 3

流数据中的中位数

```java
// 中位数：排序后，偶数是中间两数的平均数，奇数则中间那数
// 思路：数据排序后从中位数分成两半，两半的接口处可求中位数，用堆来实现
// 大顶堆存较小的部分（左边），小顶堆存较大的部分（右边）
// 存入方向：个数为奇数时存入小根堆，偶数存入大根堆
// 存入方式：偶数存入左边时，有可能存入的数比右边的大。为了保证左<右，先存入右边排序后，弹出右边最小值放入左边
// 从而动态平衡，总体过程还是偶数存入了左边

import java.util.Comparator;
import java.util.PriorityQueue;
public class Solution {
    
    int count = 0;  // 判断奇偶
    private PriorityQueue<Integer> rightHeap = new PriorityQueue<>();  // 小顶堆
    private PriorityQueue<Integer> leftHeap = new PriorityQueue<>((x, y) -> y - x);  // 大根堆

    public void Insert(Integer num) {
        if( (count&1) == 0 ){  // 个数为偶数进左边（进了就变奇数）
            rightHeap.offer(num);
            int temp = rightHeap.poll();
            leftHeap.offer(temp);
        }else{  // 奇数
            leftHeap.offer(num);
            int temp = leftHeap.poll();
            rightHeap.offer(temp);
        }
        count++;
    }
    public Double GetMedian() {
        if( (count&1) == 0 )
            return ( leftHeap.peek() + rightHeap.peek() ) / 2.0;
        else
            return leftHeap.peek() / 1.0;
    }
}
```

