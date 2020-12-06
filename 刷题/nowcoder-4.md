## 1-------------

序列化二叉树

```java
public class Solution {
    int index = -1;
    
    // #表示空、！表示结束、，间隔值
    String Serialize(TreeNode root) {
        StringBuilder sb = new StringBuilder();
        if(root == null){
            sb.append("#,");
            return sb.toString();
        }
        
        sb.append(root.val + ",");
        sb.append(Serialize(root.left));
        sb.append(Serialize(root.right));
        
        return sb.toString();
  }
    TreeNode Deserialize(String str) {
        index++;
        String[] arr = str.split(",");
        TreeNode node = null;
        if(!arr[index].equals("#")){
            node = new TreeNode(Integer.valueOf(arr[index]));
            node.left = Deserialize(str);
            node.right = Deserialize(str);
        }
        return node;
  }
}
```





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





## 4

每个滑动窗口的最大值

```java
// 不推荐大顶堆方法，没效率，但容易理解

import java.util.ArrayList;
import java.util.PriorityQueue;
public class Solution {
    public ArrayList<Integer> maxInWindows(int [] num, int size){
        ArrayList<Integer> rs = new ArrayList();  // 窗口可为0,且可能大于数组长度
        if(num == null || size < 1 || size > num.length) return rs;
        
        PriorityQueue<Integer> heap = new PriorityQueue<>((x, y) -> y - x);// 大顶堆顶部最大
        for(int i = 0; i < size;i++){
            heap.add(num[i]);  // 开始先滑动填满窗口，记录第一个最大值
        }
        rs.add(heap.peek());
        
        for(int i = 1; i < num.length-size+1; i++){  // 模拟窗口滑动，一次滑一格
            heap.remove(num[i-1]);
            heap.add(num[i+size-1]);
            rs.add(heap.peek());
        }
        return rs;
    }
}
```

```java
import java.util.ArrayList;
import java.util.LinkedList;
public class Solution {
    public ArrayList<Integer> maxInWindows(int [] num, int size){
        
        ArrayList<Integer> rs = new ArrayList();
        if(num == null || size <= 0 || num.length < size) return rs;
        
        LinkedList<Integer> qmax = new LinkedList<>();  // 双端队列，记录下标
        for(int i = 0; i < num.length; i++){
            
            while(!qmax.isEmpty() && num[qmax.peekLast()] < num[i]){  // 不空对比才不报错
                qmax.pollLast();  // 新进元素从队尾往对首比，保持对首最大
            }
            qmax.addLast(i);  // 队尾添加
            if(qmax.peekFirst() == i - size){  // 队首超出窗口
                qmax.pollFirst();
            }
            if(i >= size - 1){  // 开始时插入了窗口大小才，添加结果集，此时的队首是最大的
                rs.add(num[qmax.peekFirst()]);
            }
        }
        return rs;
    }
}
```





## 5

矩阵中的路径

```java
// 思路：DFS的回溯法
public class Solution {
    public boolean hasPath(char[] matrix, int rows, int cols, char[] str){
        
        if(matrix == null || rows < 1 || cols < 1 || str == null) return false;  // 条件判断
        
        boolean[] flag = new boolean[matrix.length];  // 标志位，初始化为false、回溯法常用标志辅助
        
        for(int i = 0; i < rows; i++)  // DFS开始
            for(int j = 0 ; j < cols; j++)
                 if( judge(matrix,i,j,rows,cols,flag,str,0) ) return true;
                     
        return false;
    }
    
    private boolean judge(char[] matrix,int i,int j,int rows,int cols,boolean[] flag,char[] str,int k){
        
        int index = i * cols + j;  // 计算矩阵的下标
        
        // 注意下标越界，
        if(i < 0 || j < 0 || i > rows-1 || j > cols-1 || flag[index] == true) return false;
        if(matrix[index] != str[k]) return false;
        if(k == str.length-1) return true;
        
        flag[index] = true;  // 标志走过
        
        if( judge(matrix,i-1,j,rows,cols,flag,str,k+1) ||
            judge(matrix,i+1,j,rows,cols,flag,str,k+1) ||
            judge(matrix,i,j-1,rows,cols,flag,str,k+1) ||
            judge(matrix,i,j+1,rows,cols,flag,str,k+1)  ){
            return true;
        }
        
        flag[index] = false;  // 回溯
        return false;
    }
}
```





## 6

机器人运动范围

```java
public class Solution {
    int count = 0;
    public int movingCount(int threshold, int rows, int cols){
        boolean[] pass = new boolean[rows * cols];
        movingCount(threshold,0,0,rows,cols,pass);
        return count;
    }
    
    public void movingCount(int threshold, int i, int j,int rows, int cols,boolean[] pass){
        int index = i * cols + j;
        if(i < 0 || j < 0 || i >= rows || j >= cols || pass[index] == true) return ;
        if(helper(i,j) <= threshold){
            count++;
            pass[index]=true;
        }else{
            pass[index]=false;
            return;
        }
        movingCount(threshold, i-1, j,rows,cols,pass);
        movingCount(threshold, i+1, j,rows,cols,pass);
        movingCount(threshold, i, j-1,rows,cols,pass);
        movingCount(threshold, i, j+1,rows,cols,pass);
    }

    // 计算是否坐标数值大于K
    public int helper(int i,int j){
        int sum = 0;
        do{
            sum += i % 10;
        }while((i = i / 10) > 0);
         
        do{
            sum += j % 10;
        }while((j = j / 10) > 0);
        return sum;
    }
}
```





## 7

割绳子

```java
// 贪心
// 数学运算分成2，3乘积最大，其中3要最多
public class Solution {
    public int cutRope(int target) {
        
        if(target == 2){
            return 1;
        }else if(target == 3){
            return 2;
        }
        
        // 分成多少个3
        int timesOf3 = target / 3;
        
        // 不是3倍数时，分为余1和余2
        // 若余1：则取一个3及余数1凑成2 * 2
        // 若余2：直接参数乘法运算
        if(target - timesOf3 * 3 == 1){
            timesOf3--;
        }
        if(target - timesOf3 * 3 == 2){
            // donothing;
        }
        
        // 算出2有多少个
        int timesOf2 = (target - timesOf3 * 3) / 2;
        
        return (int)(Math.pow(3, timesOf3))*(int)(Math.pow(2, timesOf2));
    }
}
```

```java
// 动态规划
```

