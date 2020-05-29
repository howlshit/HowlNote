## 1

判定入栈，出栈序列是否匹配

```java
// 思路：用辅助栈模拟出入栈
import java.util.Stack;
public class Solution {
    public boolean IsPopOrder(int [] pushA,int [] popA) {
        
        if(pushA == null || popA == null) return false;
        
        int cnt = 0;
        Stack<Integer> stack = new Stack();  // 辅助栈
        
        for(int i = 0; i < pushA.length; i++){  // 模拟入栈
            stack.push(pushA[i]);
            while(!stack.isEmpty() && stack.peek() == popA[cnt]){  // while循环模拟出栈
                stack.pop();
                cnt++;
            }
        }
        return stack.isEmpty();  // 判断辅助栈是否为空
    }
}
```





## 2

从上往下层级遍历二叉树

```java
// 思路：用一个队列模拟层次
import java.util.ArrayList;
import java.util.LinkedList;
public class Solution {
    public ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {
        ArrayList<Integer> list = new ArrayList();  // 存取层次遍历序列
        LinkedList<TreeNode> queue = new LinkedList();  // 存储节点模拟层次的
        
        if(root == null) return list;
        queue.addLast(root);
        
        while(!queue.isEmpty()){
            TreeNode temp = queue.removeFirst();  // 出队
            list.add(temp.val);
            
            if(temp.left != null){
                queue.addLast(temp.left);
            }
            if(temp.right != null){
                queue.addLast(temp.right);
            }
        }
        return list;
    }
}

public class Solution {
    public ArrayList<Integer> PrintFromTopToBottom(TreeNode root) {
       ArrayList<Integer> list = new ArrayList();
       ArrayList<TreeNode> queue = new ArrayList();
        
       if(root == null) return list;
       queue.add(root);
        
       while(queue.size() != 0){
           TreeNode temp = queue.remove(0);
           list.add(temp.val);
           
           if(temp.left != null){
               queue.add(temp.left);
           }
           if(temp.right != null){
               queue.add(temp.right);
           }
       }
        return list;
    }
}
```





## 3

判断是否后序遍历

```java
// 后序定义：最后一个是根，去除最后一个可以分成两段。前段小于根，后段大于根，以此类推递归
public class Solution {
    public boolean VerifySquenceOfBST(int [] sequence) {
        if(sequence == null || sequence.length == 0) return false;
        return search(sequence,0,sequence.length-1);
    }
    private boolean search(int[] arr,int left,int right){
        
        if(left >= right) return true;  // 递归出口
        
        int mid = left;  // 从左遍历找分界（对比根），小心越界
        while(arr[mid] < arr[right] && mid < right){
            mid++;
        }
        for(int i = mid; i < right; i++){  // 判断右端是否符合
            if(arr[i] < arr[right]){
                return false;
            }
        }
        return search(arr,left,mid-1) && search(arr,mid,right-1);
    }
}
```





## 4

二叉树和和为某值的路径

```java
import java.util.ArrayList;
public class Solution {
    
    // 一个保存当前遍历的路径，一个保存符合的全部路径
    ArrayList<ArrayList<Integer>> list = new ArrayList<ArrayList<Integer>>();
    ArrayList<Integer> path = new ArrayList<Integer>();
    
    public ArrayList<ArrayList<Integer>> FindPath(TreeNode root,int target) {
        
        if(root == null || target < 0) return list; // 到叶子节点值后值还不够，或已经多了
        
        path.add(root.val); // 添加当前节点进路径
        target -= root.val; // 计算和
        
        if(target == 0 && root.left == null && root.right == null){
            list.add(new ArrayList<Integer>(path));  // 访问到了叶子节点，且和为target，添加路径
        }
        
        FindPath(root.left, target);  // 深度搜索
        FindPath(root.right, target);
        
        path.remove(path.size()-1); // 回溯
        return list;
    }
}
```





## 5

复杂链表的复制

```java
/*
public class RandomListNode {
    int label;
    RandomListNode next = null;
    RandomListNode random = null;

    RandomListNode(int label) {
        this.label = label;
    }
}
*/
// 思路：
// 1. 复制每个节点（暂不处理随机指向），将新节点插入原节点后面：A->A1
// 2. 处理随机指向
// 3. 复制链表和原链表分离
public class Solution {
    public RandomListNode Clone(RandomListNode pHead){
        
        if(pHead == null) return null;
        
        // 1. 复制链表，复制节点插入到原节点后面
        RandomListNode node = pHead;
        while(node != null){
            RandomListNode next = node.next;
            RandomListNode cloneNode = new RandomListNode(node.label);
            node.next = cloneNode;  // 链表插入过程
            cloneNode.next = next;
            node = next;  // 节点插入后，当前节点记得跳转到next
        }
        
        // 2. 遍历处理随机指向
        node = pHead;
        while(node != null){
            if(node.random != null){
                // 重点：指向随机的下一个（因复制时插入到后一个去了）
                node.next.random = node.random.next;
            }
            node = node.next.next;  // 复制插入要跳多一个
        }
        
        // 3. 分离节点，奇偶分离
        RandomListNode oldNode = pHead;
        RandomListNode newHead = pHead.next;  // 新表头
        while(oldNode != null){
            RandomListNode newNode = oldNode.next;
            oldNode.next = newNode.next;
            if(newNode.next != null){
                newNode.next = newNode.next.next;
            }
            oldNode = oldNode.next; // 上面已经更新了旧节点指向，已经跳过一个节点了
        }
        return newHead;
    }
}

// 3. 分离节点，奇偶分离
// RandomListNode oldNode = pHead;
// RandomListNode newNode = pHead.next;  // 因为有复制，所以后一个节点一定不为空
// RandomListNode newHead = newNode;  // 新表头
// while(newNode.next != null){
//     oldNode.next = newNode.next;
//     oldNode = oldNode.next;
//     newNode.next = oldNode.next;
//     newNode = newNode.next;
// }
```





## 6

二叉搜索树转变双向链表

```java
/**
public class TreeNode {
    int val = 0;
    TreeNode left = null;
    TreeNode right = null;

    public TreeNode(int val) {
        this.val = val;

    }

}
*/
/**
 * 递归中序遍历：左 根 右
 * 下面if、else中的意思
 *    4
     / \
 *  3   5
 * 第一步if：head与temp赋值3节点；
 * 第二步else：改动temp节点互相指向，最后head赋值4节点：3 <--> 4
 * 第三步else：改动temp节点互相指向，最后head赋值5节点：4 <--> 5
 * 综上：3 <--> 4 <--> 5，链表完成
 */
public class Solution {
    TreeNode temp = null;          // 临时节点，帮助形成双向链表
    TreeNode head = null;          // 表头，用于返回
    public TreeNode Convert(TreeNode pRootOfTree) {
        
        if(pRootOfTree == null) return null;  // 递归出口
        
        Convert(pRootOfTree.left); // 左子树遍历
        
        if (head == null) {        // 首次要处理根节点
            head = pRootOfTree;    // 第一次访问，记录头节点，用于访问返回
            temp = pRootOfTree;
        } else {
            temp.right = pRootOfTree;  // 按中序遍历顺序连成链表，详情看上面图
            pRootOfTree.left = temp;   // 中序就是有序，只需将当前temp指向下一个即可
            temp = temp.right;        // 然后移动当前节点到下一个
        }
        
        Convert(pRootOfTree.right); // 右子树递归
        return head;
    }
}
```





## 7

字符串的排列（DFS +  交换 / 回溯）

```java
import java.util.ArrayList;
import java.util.Collections;
public class Solution {
    
    // 思路：
    // 排列中：每个元素都有排在首位的机会（首位与后面的遍历交换swap）
    // 选择一个元素固定在第一位，后面的元素全排列（递归实现foo）
    // 而后面的元素也采取上面的做法，直到最后一个元素
    
    // 保存排列
    ArrayList<String> list = new ArrayList();
    
    public ArrayList<String> Permutation(String str) {
       
        // 排列过程
        char[] arr = str.toCharArray();
        foo(arr,0);
        
        // 排序后返回
        Collections.sort(list);
        return (ArrayList) list;
    }
    
    private void foo(char[] arr,int start){
        
        if(start == arr.length-1){  // 到了叶子节点才认为一个排列，递归出口
            String s = new String(arr);
            if(!list.contains(s)){  // 排除重复排列
                list.add(new String(s));
            }
        }else{
            for(int i = start;i < arr.length;i++){  // 遍历交换：使得每个元素都在首位
                swap(arr,start,i);
                foo(arr,start+1);  // 对当前排列来说，固定首位，后面的全排列
                swap(arr,start,i); // 交换回来，不影响后面的每个元素都排在首位
            }
        }
    }
    
    // 交换
    private void swap(char[] arr,int i,int j){
        char temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
    }
}
```







## 8

找出数组中的一个出现的次数超过数组长度的一半的数

```java
// 思路：摩尔投票法，查找出现1/n的数
public class Solution {
    public int MoreThanHalfNum_Solution(int [] array) {
        
        int rs = 0;
        int count = 0;
        
        for(int i = 0; i < array.length; i++){  // 投票法
            if(count == 0){      // 若count为零，则重选一个元素投票
                rs = array[i];
            }
            if(rs == array[i]){  // 遇到相同的票数count++，不同则--
                count++;
            }else{
                count--;
            }
        }
        
        int countRs = 0;
        for(int i = 0; i < array.length; i++){  // 计算rs保存的数是否超过一半
            if( rs == array[i]){
                countRs++;
            }
        }
        
        if(countRs > array.length/2) return rs;
        return 0;
    }
}
```





## 9

找出其中最小的K个数（快排，堆排，）

```java
// 快排，我叫为哨兵排
import java.util.ArrayList;
public class Solution {
    ArrayList<Integer> list = new ArrayList();
    public ArrayList<Integer> GetLeastNumbers_Solution(int [] input, int k) {
        
        if(input == null || k > input.length) return list;
        
        quickSort(input,0,input.length-1);  // 核心在于快排
        
        for(int i = 0; i < k; i++)
            list.add(input[i]);
        
        return list;
    }
    
    private void quickSort(int[] arr, int left,int right){
        
        if(left > right) return ;
        int base = arr[left];
        int i = left,j = right;
        
        while(i < j){  // 选基准交换
            while(i < j && base <= arr[j]){
                j--;
            }
            while(i < j && base >= arr[i]){
                i++;
            }
            if(i < j){
                int temp = arr[i];
				arr[i] = arr[j];
				arr[j] = temp;
            }
        }
        
        arr[left] = arr[i];  // 基准归位
        arr[i] = base;
        
        quickSort(arr,left,i-1);  // 二分治
        quickSort(arr,i+1,right);
    }
}
```





## 10

计算连续子向量的最大和，有负数

```java
public class Solution {
    public int FindGreatestSumOfSubArray(int[] array) {
        
        if(array == null) return 0;

        // 注意：题目指的连续，不一定从下标0开始，可以窗口滑动的
        // maxSum不初始化为0，存在全负数情况，所以初始值array[0]
        // maxSum存储最大和
        int curSum,maxSum;
        curSum = maxSum = array[0];
        
        for(int i = 1; i < array.length; i++){
            
            // 一旦遇到和为负数，证明前面的正数效果作废了
            // 当前和小于0，抛弃前面的和，重新从现在加起
            if(curSum < 0){
                curSum = array[i];
            }else if(curSum > 0){
                curSum += array[i];
            }
            
            // 更新最大和
            if(curSum > maxSum){
                maxSum = curSum;
            }
        }
        return maxSum;
    }
}
```





## 11

计算整数中1出现的次数

```java
// 暴力转成字符串判断
public class Solution {
    public int NumberOf1Between1AndN_Solution(int n) {
        StringBuffer str = new StringBuffer();
        for(int i = 1;i <= n; i++){
            str.append(i);
        }
        
        int count = 0;
        String s = str.toString();
        
        for(int i = 0;i < s.length(); i++){
            if(s.charAt(i) == '1'){
                count++;
            }
        }
        return count;
    }
}
```

```java
// 计算方法：
public class Solution {
    public int NumberOf1Between1AndN_Solution(int n) {
        int count = 0;
        for(int i = 1; i <= n; i *=10){
            
            int high = n / i;
            int low  = n % i;
            
            if(high % 10 == 1){
                count += low + 1;
            }
            
            count += (high + 8) / 10 * i;
        }
        return count;
    }
}
```





## 12

把数组排成最小

```java
public class Solution {
    public String PrintMinNumber(int [] numbers) {
        
        for(int i = 0; i < numbers.length-1; i++)  // 冒泡排序
            for(int j = 0; j < numbers.length-i-1; j++){
                String str1 = numbers[j] + "" + numbers[j+1];
                String str2 = numbers[j+1] + "" + numbers[j];
                if(str1.compareTo(str2) > 0){  // 排到最后的是最大
                    int temp = numbers[j];
                    numbers[j] = numbers[j+1];
                    numbers[j+1] = temp;
                }
            }
        
        String str = "";
        for(int i = 0; i < numbers.length; i++){
            str += numbers[i];
        }
        return str;
    }
}
```

```java
// 思路二
// 数字m、n拼接成 mn 和 nm
// 若mn>nm，则m大于n
// 若mn<nm，则m小于n
// 若mn=nm，则m等于n
```

