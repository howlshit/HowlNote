## 1

从每行每列都是递增的二维数组中找是否存在某数

```java
public class Solution {
    public boolean Find(int target, int [][] array) {
        int rows = array.length;
        int cols = array[0].length;
        
        int i = rows - 1;
        int j = 0;
        
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
// 效率也没差
public class Solution {
    public String replaceSpace(StringBuffer str) {
        char[] arr = str.toString().toCharArray();
        StringBuffer sb = new StringBuffer(arr.length); //避免多次扩容  System.arraycopy
        for(int i = 0; i < arr.length; i++){
            if(arr[i] == ' '){
                sb.append("%20");
            }else{
                sb.append(arr[i]);
            }
        }
        return sb.toString();
    }
}
```

```java
// 考察的是优化，从后往前替换，移动次数少
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
// 模拟栈，ArrayList(index,val)也可以模拟栈，remove(index)
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
        return reConBTree(pre,0,pre.length-1,in,0,in.length-1);
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
// 用了copy
import java.util.*;
public class Solution {
    public TreeNode reConstructBinaryTree(int [] pre,int [] in) {
        
       if(pre.length == 0 || in.length == 0) return null;  // 条件判断
        
        TreeNode node = new TreeNode(pre[0]);  // 创建根节点
        
        for(int i = 0; i < in.length; i++){
            if(pre[0] == in[i]){
                
                node.left = reConstructBinaryTree
                    (Arrays.copyOfRange(pre, 1, i+1), 
                     Arrays.copyOfRange(in, 0, i));
                
                node.right = reConstructBinaryTree
                    (Arrays.copyOfRange(pre, i+1, pre.length), 
                     Arrays.copyOfRange(in, i+1,in.length));
            }
        }
        return node;
    }
}
```





## 5

两个栈模拟队列

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
// 用二分法：最后第一个指针指向分界点的前一个，第二个指针指向分界点的后一个，即二者相邻，那么
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
            }else {  // 重复元素无法判断是分界前后，只能顺序找
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

        if(n == 0) return 0;
        if(n == 1) return 1;
        
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
        if(n == 0) return 0;
        if(n == 1) return 1;
        return Fibonacci(n-1) + Fibonacci(n-2);
    }
}
```





## 8

跳台阶：注重思想

迭代没什么好说的

递归：只剩最后一步时，要么是跳1阶，要么是跳2阶

```java
// 设n个台阶有f(n)种走法
// 只剩最后一步时，要么是跳1阶，要么是跳2阶
// 最后一步跳1阶，即之前有n-1个台阶，据前面的假设，即n-1个台阶有f(n-1)种走法
// 最后一步跳2阶，即之前有n-2个台阶，据前面的假设，即n-2个台阶有f(n-2)种走法
// 总结规律：n个台阶的走法等于前两种情况的走法之和即 f(n) = f(n-1)+f(n-2)
// 变相斐波那契数列
```





## 9

跳台阶II，式子推导出来后看怎么实现迭代思想

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

public class Solution {
    public int RectCover(int target) {
        if(target == 0) return 0;
        if(target == 1) return 1;
        int a = 1,b = 1,c = 0;
        while(target > 1){
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

