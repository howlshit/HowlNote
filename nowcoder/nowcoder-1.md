## 1

从每行每列都是递增的二维数组中找是否存在某数

```java
public class Solution {
    public boolean Find(int target, int[][] array) {
        
        int rows = array.length;
        int cols = array[0].length;
        
        int i = rows - 1;
        int j = 0;
        
        // 从左下角，或右上角扫描
        while(i >= 0 && j < cols){
            if(target < array[i][j]){
                i--;
            }else if(target > array[i][j]){
                j++;
            }else{    
                return true;
            }
        }
        return false;
    }
}
```





## 2

字符串替换

```java
// 内部函数
public class Solution {
    public String replaceSpace(StringBuffer str) {
        
        return str.toString().replaceAll(" ", "%20");
    }
}
```

```java
// 效率也没差
public class Solution {
    public String replaceSpace(StringBuffer str) {
        
        StringBuffer sb = new StringBuffer(str.length());	// 避免多次扩容  System.arraycopy
        
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == ' '){
                sb.append("%20");
            }else{
                sb.append(str.charAt(i));
            }
        }
        return sb.toString();
    }
}
```

```java
// 考察优化
// 从前向后计算空格数
// 从后向前替换，这样移动的次数相对少了
public class Solution {
    public String replaceSpace(StringBuffer str) {
        
        int spaceNum = 0;  // 算出空格总数
        for(int i = 0; i < str.length(); i++){
            if(str.charAt(i) == ' '){
                spaceNum++;
            }
        }
        
        if(spaceNum == 0) return str.toString();  // 没有空格直接返回
        
        int oldLength = str.length();
        int newLength = str.length() + spaceNum * 2;  // 设置新长度
        str.setLength(newLength);
        
        newLength--;  // Java中没有 "\0"结尾字符，所以实际大小减一
        for(int i = oldLength-1; i >= 0; i--){
            if(str.charAt(i) == ' '){
                str.setCharAt(newLength--, '0');
                str.setCharAt(newLength--, '2');
                str.setCharAt(newLength--, '%');
            }else{
                str.setCharAt(newLength--, str.charAt(i));
            }
        }
        
        return str.toString();
    }
}
```





## 3

从尾到头遍历链表

```java
// 递归
public class Solution {
    ArrayList list = new ArrayList();
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        if(listNode != null){
            printListFromTailToHead(listNode.next);
            list.add(listNode.val);
        }
        return list;
    }
}
```

```java
// 非递归
// 模拟栈，ArrayList(index,val)也可以模拟栈，remove(index)，LinkedList？？
// ArrayList的头插法
public class Solution {
    public ArrayList<Integer> printListFromTailToHead(ListNode listNode) {
        ArrayList list = new ArrayList();
        while(listNode != null){
            list.add(0,listNode.val);
            listNode = listNode.next;
        }
        return list;
    }
}
```





## 4

重建二叉树（前序，中序）

```java
// 一般树都是递归操作，用索引
public class Solution {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        return reConBTree(pre,0,pre.length-1,in,0,in.length-1);	// 传入实际长度
    }
    
    public TreeNode reConBTree(int[] pre,int pleft,int pright,int[] in,int inleft,int inright){
        
        if(pleft > pright || inleft > inright) return null;  // 递归出口
        
        TreeNode root = new TreeNode(pre[pleft]);  // 父节点可以确定的
        
        for(int i = inleft; i <= inright; i++){  // 遍历寻找根节点
            if(pre[pleft] == in[i]){  // in的要排除根节点+-1、
                root.left = reConBTree(pre,pleft+1,pleft+(i-inleft),in,inleft,i-1);
                root.right = reConBTree(pre,pleft+1+(i-inleft),pright,in,i+1,inright);
                break;
            }
        }
        return root;  // 返回父节点
    }
}
```

```java
// Arrays.copyOfRange(arr,from,to)，注意包括上标，不包括下标
// Arrays.copyOf(arr,newLength)，新长度大于就长度就填充默认值
import java.util.*;
public class Solution {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        
       if(pre.length == 0 || in.length == 0) return null;  // 条件判断
        
        TreeNode root = new TreeNode(pre[0]);  // 创建根节点
        
        for(int i = 0; i < in.length; i++){
            if(pre[0] == in[i]){
                
                root.left = reConstructBinaryTree
                    (Arrays.copyOfRange(pre, 1, i+1),
                     Arrays.copyOfRange(in, 0, i));
                
                root.right = reConstructBinaryTree
                    (Arrays.copyOfRange(pre, i+1, pre.length), 
                     Arrays.copyOfRange(in, i+1,in.length));
            }
        }
        return root;
    }
}
```





## 5

两个栈模拟队列，先清空栈2才进入元素，先清空栈1才弹出元素

```java
import java.util.Stack;

public class Solution {
    Stack<Integer> stack1 = new Stack<Integer>();
    Stack<Integer> stack2 = new Stack<Integer>();
    
    public void push(int node) {
        while(!stack2.isEmpty()){
            stack1.push(stack2.pop());
        }
        stack1.push(node);
    }
    
    public int pop() {
        while(!stack1.isEmpty()){
            stack2.push(stack1.pop());
        }
        return stack2.pop();
    }
}
```





## 6

旋转数组的最小数字

```java
// 非递减，即递增有重复
// 用二分法：最后low指向分界点前一个，high指向分界点后一个，二者相邻，返回low++
// 第二个指针将指向最小元素
import java.util.ArrayList;
public class Solution {
    public int minNumberInRotateArray(int [] array) {
    
        if(array.length == 0) return 0;
        
        int low = 0;
        int high = array.length - 1;
        int mid = 0;
        
        while(low < high){
            mid = low + (high-low)/2;
            if(array[low] < array[mid]){
                low = mid;
            }else if(array[mid] < array[high]){
                high = mid;
            }else {  	// 重复元素无法判断是分界前后，只能顺序找
                low++;  // 且最后也使指针指向分界的后一个
            }
        }
        return array[low];
    }
}
```





## 7

斐波那契数列

```java
// 迭代法
public class Solution {
    public int Fibonacci(int n) {

        if(n == 0 || n == 1) return n;
        
        int a = 0;
        int b = 1;
        int c = 0;
        
        while(n > 1){  // 第一项1，上面已经给出了
            c = a + b;
            a = b;
            b = c;
            n--;
        }
        return c;
    }
}
```

```java
// 递归
public class Solution {
    public int Fibonacci(int n) {
        if(n == 0 || n == 1) return n;
        return Fibonacci(n-1) + Fibonacci(n-2);
    }
}
```





## 8

跳台阶：注重思想，就是斐波那契数列

迭代没什么好说的

递归：只剩最后一步时，要么是跳1阶，要么是跳2阶

```java
// 设n个台阶有f(n)种走法
// 只剩最后一步时，要么是跳1阶，要么是跳2阶
// 最后一步跳1阶，即之前有n-1个台阶，据前面的假设，即n-1个台阶有f(n-1)种走法
// 最后一步跳2阶，即之前有n-2个台阶，据前面的假设，即n-2个台阶有f(n-2)种走法
// 总结规律：n个台阶的走法等于前两种情况的走法之和即 f(n) = f(n-1)+f(n-2)
// 变相斐波那契数列

// 注意和上面那题起点不一样
public class Solution {
    public int JumpFloor(int target) {
        if(target == 1 || target == 2) return target;
        return JumpFloor(target - 1) + JumpFloor(target - 2);
    }
}
```





## 9

跳台阶II

```java
// n级台阶，第一步有n种跳法：跳1级、跳2级、……、到跳n级
// 跳1级，剩下n-1级，则剩下跳法是f(n-1)
// 跳2级，剩下n-2级，则剩下跳法是f(n-2)
// 所以f(n) = f(n-1)+f(n-2)+...+f(1)
// 因为f(n-1) = f(n-2)+f(n-3)+...+f(1)
// 所以f(n) = 2*f(n-1)

public class Solution {
    public int JumpFloorII(int target) {
        int temp = 1;
        while(target > 1){  // 第一个已给出{
            temp *= 2;
            target--;
        }
        return temp;
    }
}
```





## 10

矩形覆盖

```java
// 逆序思想：最后一步有两种情况
// OO		OO
// XX		OO
// XX		XX
// ..		..
// XX		XX
// 第一种情况:阴影部分的n-1块矩形有多少种覆盖方法，为f(n-1);
// 第二种情况:阴影部分的n-2块矩形有多少种覆盖方法，为f(n-2);
// 故f(n) = f(n-1) + f(n-2)，还是一个斐波那契数列

// 斐波那契数列就是注意起点问题
public class Solution {
    public int RectCover(int target) {
        if(target == 0) return 0;
        if(target == 1) return 1;
        int a = 1,b = 1,c = 0;
        while(target > 1){ // 第一项1，上面已经给出了
            c = a + b;
            a = b;
            b = c;
            target--;
        }
        return c;
    }
}
```





## 11

求整数的二进制中1的个数

```java
// 逐位比较
public class Solution {
    public int NumberOf1(int n) {
        int cnt = 0;
        while(n != 0){  // 不是大于0，因为有负数
            cnt += n & 1;
            n >>>= 1;
        }
        return cnt;
    }
}
```

```java
// 最优解
// 一个整数减去1，再和原整数做与运算，会把该整数最右边一个1变成0
// 那么一个整数的二进制有多少个1，就可以进行多少次这样的操作
public class Solution {
    public int NumberOf1(int n) {
        int cnt = 0;
        while(n != 0){
            n = n & (n-1);
            cnt++;
        }
        return cnt;
    }
}
```

```java
// 作弊解
// 计算该数的二进制，然后返回二进制中`1`的个数 
public class Solution {
    public int NumberOf1(int n) {
        return Integer.bitCount(n);
    }
}
```





## 12

数值的整数次方

```java
// 连乘思路：O(n)
public class Solution {
    public double Power(double base, int exponent) {
        double rs = 1;
        for(int i = 0; i < Math.abs(exponent); i++){
            rs *= base;  // 连乘
        }
        if(exponent < 0){
            rs = 1 / rs;  // 负数次幂，直接倒数
        }
        return rs;
  }
}
```

```java
// 快速幂：log(n)
// 11可转化为二进制次幂：11 = 1011 = 2³×1 + 2²×0 + 2¹×1 + 2º×1 = 2³×1 + 2¹×1 + 2º×1
// base *= base 保持累乘的作用：base-->base2-->base4-->base8-->base16
// 那么化简后：a¹¹ = a^(2º+2¹+2³) = a^(1+2+8)
// 那么 a¹¹ = a¹ * a² * a^8 = base * base^2 * base^8

public class Solution {
    public double Power(double base, int exponent) {
        
        double rs = 1;  // 保存结果
        int power = Math.abs(exponent);  // 幂的绝对值
        
        // 快速幂核心
        while(power != 0){
            if( (power & 1) == 1 ){  // 幂的二进制当前位为1即有效，结果相乘
                rs *= base;
            }
            power >>>= 1;      // 幂右移
            base *= base;  // 保持累乘，后面利用
        }
        
        if(exponent < 0){  // 负次幂，结果取倒数
            rs = 1 / rs;
        }
        return rs;
  }
}
```





## 13

调整数组顺序，奇数位于偶数前面，且相对顺序不变（稳定性）

```java
// 分治，思路明了
import java.util.ArrayList;
public class Solution {
    public void reOrderArray(int [] array) {
        ArrayList<Integer> list = new ArrayList();
        
        for(int i = 0; i < array.length; i++){
            if(array[i] % 2 != 0){
                list.add(array[i]);
            }
        }
        
        for(int i = 0; i < array.length; i++){
            if(array[i] % 2 == 0){
                list.add(array[i]);
            }
        }
        
        for(int i = 0; i < array.length; i++){
            array[i] = (Integer)list.get(i);
        }
    }
}
```

```java
// 相对位置不变：保持稳定性即可，冒泡、直接插入等
// 类似冒泡算法，前偶后奇数就交换：从后往前
public class Solution {
    public void reOrderArray(int [] array) {
        for(int i = 0; i < array.length; i++){
            for(int j = array.length-1; j > i; j--){
               if(array[j] % 2 == 1 && array[j-1] % 2 == 0){  // 整体奇数前移
                   int temp = array[j];
                   array[j] = array[j-1];
                   array[j-1] = temp;
               }
            }
        }
    }
}
```

```java
// 就地算法，不借助辅助，原地修改数据结构
// i 前面的奇数都排好了
public class Solution {
    public void reOrderArray(int [] array) {
        int temp;
        int i = 0;
        for(int j = 0; j < array.length; j++){
            if(array[j] % 2 != 0){        			// j遇到奇数，非奇后移
                temp = array[j];         		    // 保存移动覆盖的奇数
                for(int k = j - 1; k >= i; k--){ 	// i到j的偶数后移一位
                    array[k+1] = array[k];
                }
                array[i] = temp;           			// 奇数往前跳动
                i++;                       			// i指向奇数排好的下一个
            }
        }
    }
}
```





## 14

返回链表倒数第K个节点

```java
// 设置快慢指针
public class Solution {
    public ListNode FindKthToTail(ListNode head,int k) {
        
        ListNode node,pre;
        node = pre = head;
        
        for(int i = 1; i <= k; i++){  // 先驱走k步
            if(pre == null) return null;  // 链表长没有k步长
            pre = pre.next;
        }
        
        while(pre != null){  // 同步走
            pre = pre.next;
            node = node.next;
        }
        return node;
    }
}
```





## 15

反转链表

```java
// 需要三个指针
public class Solution {
    public ListNode ReverseList(ListNode head) {
        
        ListNode pre,next;  // 如果head为空，那么next.next就空指针异常
        pre = next = null;  // 所以只能在head不为空的循环内next了
        
        while(head != null){  // head表示当前节点
            next = head.next;  // 上面说的next，这个要先写
            head.next = pre;
            pre = head;
            head = next;
        }
        return pre;  // 这里注意：上面的循环结束条件为head为空，那么前驱节点才是真正的头节点
    }
}
```





## 16

合并两个链表，并且按大小排序

```java
public class Solution {
    public ListNode Merge(ListNode list1,ListNode list2) {
        ListNode head,node;  // 要有一个表头head来返回，node保存节点，防止断链
        head = node = new ListNode(0);	// 这里居然这样
        
        while(list1 != null && list2 != null){
            if(list1.val < list2.val){
                node.next = list1;
                node = node.next;  // 指针移动
                list1 = list1.next;
            }else{
                node.next = list2;
                node = node.next;
                list2 = list2.next;
            }
        }
        if(list1 == null) node.next = list2;
        if(list2 == null) node.next = list1;
        return head.next;
    }
}
```





## 17------

判断是否某树的子结构（两个递归，一个遍历树1，一个用来对比）

```java
public class Solution {
    public boolean HasSubtree(TreeNode root1,TreeNode root2) {
       
        boolean flag = false;
        if(root1 == null || root2 == null) return false;
        
        if(root1.val == root2.val){  // 递归遍历，遇到根相同的
            flag = recur(root1,root2);  // 以当前根为起点，再遍历对比
        }
        
        if(!flag){  // 这里记住是!flag，即没找到相同的，找到就不用进来遍历了
            flag = HasSubtree(root1.left,root2);
        }
        if(!flag){
            flag = HasSubtree(root1.right,root2);
        }
        return flag;
    }
    private boolean recur(TreeNode root1,TreeNode root2){
        if(root2 == null) return true;  // 一定要先判断tree2
        if(root1 == null) return false;
        if(root1.val != root2.val) return false;
        return recur(root1.left,root2.left) && recur(root1.right,root2.right);
    }
}
```





## 18

转变成二叉树的镜像

```java
public class Solution {
    public void Mirror(TreeNode root) {
        
        if(root == null) return ;  // 递归出口
        swap(root);  // 交换根节点的左右孩子
        Mirror(root.left);  // 孩子的孩子也交换
        Mirror(root.right);  // 孩子的孩子也交换
    }
    private void swap(TreeNode root){  
        TreeNode temp = root.left;
        root.left = root.right;
        root.right = temp;
    }
}
```





## 19-------------

螺旋打印矩阵

```java
// 上下左右，四个边界分别为l,r,t,b
// 思路1：按题意打印
import java.util.ArrayList;
public class Solution {
    public ArrayList<Integer> printMatrix(int [][] matrix) {
        
       ArrayList<Integer> list = new ArrayList();
        
       int left = 0, right = matrix[0].length - 1;  // 四个边界
       int top = 0, bottom = matrix.length - 1;
        
       while(true){  // 无限循环遍历，出口在内部的边界判断
           
           for(int i = left; i <= right; i++) list.add(matrix[top][i]);
           if(++top > bottom) break;
           
           for(int i = top; i <= bottom; i++) list.add(matrix[i][right]);
           if(left > --right) break;
           
           for(int i = right; i >= left; i--) list.add(matrix[bottom][i]);
           if(top > --bottom) break;
           
           for(int i = bottom; i >= top; i--) list.add(matrix[i][left]);
           if(++left > right) break;
        }
        return list;
    }
}
```

```java
// 思路2：打印顺序第一行，然后删除第一行，最后旋转矩阵
```





## 20

用两个栈求最小元素----O(1)

```java
import java.util.Stack;

public class Solution {
    
    Stack<Integer> data = new Stack();
    Stack<Integer> min = new Stack();

    public void push(int node) {
        data.push(node);
        if(min.isEmpty() || node < min.peek()){
            min.push(node);
        }
    }
    
    public void pop() {
        if(data.peek() == min.peek()){
            min.pop();
        }
        data.pop();
    }
    
    public int top() {
        return data.peek();
    }
    
    public int min() {
        return min.peek();
    }
}
```

