## 1 

假设连续正整数之和为S（至少包括两个数），列出该条件下的所有序列

```java
/**
 * 左神的双指针滑动（类似TCP的窗口滑动）
 * 窗口内和小于sum，end指针后移。大于则start前移。相等保存序列，start前移
 */

import java.util.ArrayList;
public class Solution {
    public ArrayList<ArrayList<Integer> > FindContinuousSequence(int sum) {
        
        int start = 1,end = 2;  // 至少两个数
        ArrayList rs = new ArrayList();  // 保存序列
        while(end > start){                                    // 这里注意 下面有个+1
            int temp = (start + end) * (end - start + 1) / 2;  // 当前序列求和公式，窗口内之和
            if(temp == sum){  // 相等即保存该序列
                ArrayList list = new ArrayList();
                for(int i = start; i <= end; i++){
                    list.add(i);
                }
                rs.add(list);
                start++;
            }else if(temp < sum){  // 右指针移动是增加数目
                end++;
            }else if(temp > sum){  // 左指针移动是减少数目
                start++;
            }
        }
        return rs;
    }
}
```





## 2

在递增数组中查找和为S的两个数，输出其中两个数乘积最小的

```java
/**
 * a + b = s;
 * (a - m) + (b + m) = s，模拟从外到内的其他组合
 * 而(a - m)(b + m) = ab - (b-a)m - m * m < ab，说明外层的乘积更小
 * 总结：两数和一定，差越大，积越小
 */

import java.util.ArrayList;
public class Solution {
    public ArrayList<Integer> FindNumbersWithSum(int [] array,int sum) {
        
        ArrayList<Integer> list = new ArrayList();
        if(array == null || array.length < 2) return list;
        
        int start = 0;
        int end = array.length-1;
        while(start < end){
            if(array[start] + array[end] == sum){
                list.add(array[start]);
                list.add(array[end]);
                return list;  // 外层符合就是最小直接返回
            }else if(array[start] + array[end] > sum){
                end--;  // 和大了则从右减小即可，
            }else{
                start++;
            }
        }
        return list;
    }
}
```





## 3

字符串循环左移

```java
// 剑指offer的三次反转
public class Solution {
    public String LeftRotateString(String str,int n) {
        
        if(str == null || str.length() == 0 || n == 0) return str;
        
        n %= str.length();  // 实际左移几位
        char[] arr = str.toCharArray();
        
        reverse(arr,0,n-1);
        reverse(arr,n,str.length()-1);
        reverse(arr,0,str.length()-1);
        // abcXYZdef  左移三位
        // cbaXYZdef
        // cbafedZYX
        // XYZdefabc
        
        return new String(arr);
    }
    
    // 字符串反转
    private void reverse(char[] arr, int start, int end){
        while(start < end){
            char temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
}
```

```java
// 暴力拼接截取
public class Solution {
    public String LeftRotateString(String str,int n) {
        if(str == null || str.length() == 0 || n == 0) return str;
        int realength = str.length();
        n %= realength;
        str += str;
        return str.substring(n,realength+n);
    }
}
```





## 4

句子反转：student a am I   ---->  I ma a tneduts   ----> I am a student

```java
public class Solution {
    public String ReverseSentence(String str) {
        if (str == null || str.trim().length() == 0) return str;
        
        char[] arr = str.toCharArray();
        reverse(arr,0,arr.length-1);  // 整体反转
        
        int left,right;
        left = right = 0;
        while(left < arr.length){  // 遍历
            if(arr[right] == ' '){  // 遇到空格就
                reverse(arr,left,right-1);
                right++;
                left = right;
            }
            if(right == arr.length-1){  // 单独处理最后一个，不用++了（最后一个没有空格）
                reverse(arr,left,right);
                break;
            }
            right++;  // 没遇到空格就向后移动
        }
        return new String(arr);
    }
    
    // 反转
    private void reverse(char[] arr,int start,int end){
        while(start < end){
            char temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
}
```





## 5

一副牌再加入2个王，大王能当作任意数字。随机抽牌，判断能否组成5张顺子，传入的是数组，返回T/F

```java
/**
 * 参考第一的回答写了注释，利用了桶排序的思想吧
 * 条件：1.数组长度为5
 *      2.最值差 < 5
 *      3.除0外没有重复的数
 * 前两个条件组合理解为：固定大小为5的窗口在数轴上滑动
 * 第三个条件理解为：大王填充窗口内的空缺数字，形成顺子窗口
 */
public class Solution {
    public boolean isContinuous(int [] numbers) {
        if(numbers.length < 5) return false;          // 牌数不少于5
        int min = 14,max = 0;                        // 记录最值
        int[] bucket = new int[14];                   // 桶子
        for(int i = 0; i < numbers.length; i++){      // 循环5个牌
            bucket[numbers[i]]++;                     // 将牌放入桶子
            if(numbers[i] == 0) continue;             // 跳过王，不进行下面的判断
            if(bucket[numbers[i]] > 1) return false;  // 判断重复
            if(numbers[i] > max) max = numbers[i];    // 最大值
            if(numbers[i] < min) min = numbers[i];    // 最小值
        }
        return (max - min) < 5;        // 判断最值差
    }
}
```





## 6

约瑟夫环问题，n为总数，m为间隔数

```java
import java.util.LinkedList;
public class Solution {
    public int LastRemaining_Solution(int n, int m) {
        LinkedList<Integer> list = new LinkedList();  // 链表模拟约瑟夫环
        for(int i = 0; i < n; i++) list.add(i);
        int cur = 0;                                  // 记录上次去除的位置
        while(list.size() > 1){                       // 剩最后一人时结束
            cur = (cur + m - 1) % list.size();        // （上次位置+间隔） % 长度 = 当前该删位置
            list.remove(cur);                         // 元素被去除
        }
        return list.size() == 1 ? list.get(0) : -1;
    }
}
```

```java
// 思路二：用数组来模拟，加个标志位表示被访问过了
```





## 7

求1+2+...n，不用乘除法、for、while、if、else、switch、case

```java
// 因为java判断有些不同，所以要用到无意义的变量
// 递归实现，出口的的if用 && 字符代替，短路的作用
public class Solution {
    public int Sum_Solution(int n) {
        int rs = 0;
        int noMean1 = 0;
        boolean noMean2 = (n > 0) && noMean == (rs = Sum_Solution(n - 1));
        rs += n;
        return rs;
    }
}
```





## 8 

求整数和，不能用 +  -  *  /  

```java
/**
 * 各位与：查看两个数都为1（进位），然后左移表示进位操作
 * 各位异或：查看只有一位为1的（即无进位），可以直接计算。
 */

public class Solution {
    public int Add(int num1,int num2) {
        
        while(num2 != 0){
            int sum = num1 ^ num2;  // 计算单1位（0和0位不用计算了，1和1的给下面进位操作了）
            int carry = (num1 & num2) << 1;  // 进位操作
            num1 = sum;
            num2 = carry;
        }
        return num1;
    }
}
```





## 9

字符串转换整数

```java
public class Solution {
    public int StrToInt(String str) {
        // 1.空串判断
        if(str == null || str.trim().equals("")) return 0;
        
        // 2.转换整数
        long sum = 0;
        char[] arr = str.toCharArray();
        for(int i = 0; i < arr.length; i++){
            if(arr[i] >= '0' && arr[i] <= '9'){
                sum = sum * 10 + arr[i] - '0';
            }else if(sum != 0){  // 拿证明，除了第一的符号位，有不符合数字的
                return 0;
            }
            // 符号位自动跳过，sum==0时，表明是第一位
        }
        
        // 3.符号判断
        sum = arr[0] == '-' ? -sum : sum;
        
        // 4.溢出判断
        if(sum > Integer.MAX_VALUE || sum < Integer.MIN_VALUE) return 0;
        
        // 5.返回值强转
        return (int)sum;
    }
}
```





## 10--------------------

输出数组第一个重复元素（或者使用bitmap？？？）

```java
public class Solution {
    public boolean duplicate(int numbers[],int length,int [] duplication) {
        // 用了桶排序思想，出现的桶位置上标1
        int[] arr = new int[length];
        for(int i = 0; i < length; i++){
            if(arr[numbers[i]] == 1){
                duplication[0] = numbers[i];
                return true;
            }
            arr[numbers[i]]++;
        }
        return false;
    }
}
```

```java
// 剑指offer思路：把元素值当成数组下标，时间O(n),空间O(1)
// 遍历将元素放入与元素值的相等的下标处，与原元素对换咯，下面模拟交换过程
//      — — — —     — — — —     — — — —     — — — —
//      3 2 1 2 --> 2 2 1 3 --> 1 2 2 3 --> 2 1 2 3   再换就重复了
//      _ _ _ _     _ _ _ _     _ _ _ _     _ _ _ _
//下标： 0 1 2 3     0 1 2 3     0 1 2 3     0 1 2 3
public class Solution {
    public boolean duplicate(int numbers[],int length,int [] duplication) {
        if(numbers == null || length <= 0) return false;
        
        for(int i = 0; i < length; i++){
            while(numbers[i] != i){  // 元素值与其下标不等
                if( numbers[i] == numbers[numbers[i]] ){  // 对应下标有了，且非位也有
                    duplication[0] = numbers[i];
                    return true;
                }
                
                int temp = numbers[i];  // 这个交换过程有点难理解
                numbers[i] = numbers[temp];
                numbers[temp] = temp;
            }
            // 这里是第一次相等，即出现一次
        }
        return false;
    }
}
```





## 11-------------------------

乘积数组：

给定数组A[0,1,...,n-1]，构建数组B[0,1,...,n-1]，数组B的元素值为排除自身下标的数组A各项积，且不能使用除法

![1587430819186](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1587430819186.png)

```java
// 剑指offer思路：利用上下三角
import java.util.ArrayList;
public class Solution {
    public int[] multiply(int[] A) {
        
        if (A == null || A.length == 0) return new int[1];
        
        int[] B = new int[A.length];
        B[0] = 1;                    // 首位复制，类似于迭代？？？？
        for(int i = 1; i < B.length; i++){
            B[i] = B[i-1] * A[i-1];  // 下边的三角
        }
        int temp = 1;
        for(int j = B.length-2; j >= 0; j--){  // 正常是减一
            temp *= A[j+1];  // 上三角，这里A应该横着看
            B[j] *= temp;
        }
        return B;
    }
}
```

```java
// 暴力法：嵌套for循环，外层遍历A，将int temp = A[i]，且赋值A[i] = 1;
// 内层循环直接暴力计算A[1] * A[n]
// 内层循环结束，走到外层循环最后时，还原A[i] = temp;
```





## 12-----------------------

模拟正则表达式匹配字符串（递归实现迭代的思路）

```java
// 1. 模式串的下一个字符是 * ：
//    1.1 当前字符匹配：
//			1.1.1：三种情况
//    1.2 当前字符不匹配，那模式串也是后移两位
//
// 2. 模式串下一个不是 * ：
//    2.1 当前字符匹配，两个同时后移一位
//    2.2 当前字符不匹配，GG
public class Solution {
    public boolean match(char[] str, char[] pattern){
        if(str == null || pattern == null) return false;
        return match(str,pattern,0,0);
    }
    
    private boolean match(char[] str,char[] pattern,int s,int p){
        if(s == str.length && p == pattern.length) return true;  // 两个串匹同时匹配完
        if(p >= pattern.length) return false;  					 // 模式串先完，那就GG
        
        if(p < pattern.length-1 && pattern[p+1] == '*'){  // 下一个字符是*
            // 当前字符匹配,有三种情况
            if(s < str.length && (str[s] == pattern[p] || pattern[p] == '.')){
                return
                    match(str,pattern,s,p+2) ||   // 匹配0个
                    match(str,pattern,s+1,p+2) || // 匹配1个
                    match(str,pattern,s+1,p);     // 匹配多个
            }else{ // 当前字符不匹配，那模式串也是后移两位
                return match(str,pattern,s,p+2);
            }
        }else{ 											 // 下一个字符不是 *
            if(s < str.length && (str[s] == pattern[p] || pattern[p] == '.')){
                return match(str,pattern,s+1,p+1);       // 那也得匹配当前字符，相等继续
            }else{										 // 当前字符不匹配GG
                return false;
            }
        }
    }
}
```





## 13

判断是否数值，"12e"，"1a3.14"，"1.2.3"，"+-5"，"12e+4.3"都不是

```java
// 不能同时存在两个e，后面一定要接数字
public class Solution {
    public boolean isNumeric(char[] str) {
        // 标记符号、小数点、e是否出现过
        boolean sign = false, decimal = false, hasE = false;
        for (int i = 0; i < str.length; i++) {  // 遍历每个字符
            if (str[i] == 'e' || str[i] == 'E') {
                if (i == str.length-1) return false; // e后面一定要接数字
                if (hasE) return false;  // 不能同时存在两个e
                hasE = true;  // e出现一次
            }else if (str[i] == '+' || str[i] == '-') {
                // 第二次出现+-符号，则必须紧接在e之后
                if (sign && str[i-1] != 'e' && str[i-1] != 'E') return false;
                // 第一次出现+-符号，且不是在字符串开头，则也必须紧接在e之后
                if (!sign && i > 0 && str[i-1] != 'e' && str[i-1] != 'E') return false;
                sign = true; // +-出现一次
            } else if (str[i] == '.') {
                // e后面不能接小数点，小数点不能出现两次
                if (hasE || decimal) return false;
                decimal = true;
            } else if (str[i] < '0' || str[i] > '9') // 不合法字符
                return false;
        }
        return true;
    }
}
```





## 14

从字符流中判断第一个出现一次的字符：google  ---> ggg#ll

```java
import java.util.ArrayList;
import java.util.HashMap;
public class Solution {

    HashMap<Character, Integer> map = new HashMap();
    ArrayList<Character> list = new ArrayList<Character>();
    //Insert one char from stringstream
    public void Insert(char ch)
    {
        if(map.containsKey(ch)){
            map.put(ch,map.get(ch) + 1);
        }else{
            map.put(ch,1);
        }
        list.add(ch);
    }
  //return the first appearence once char in current stringstream
    public char FirstAppearingOnce()
    {
        for(char value : list){
            if(map.get(value) == 1){
                return value;
            }
        }
        return '#';
    }
}

// 思路有三个，一个Map，一个数组，还有一个LinkedHashMap内有插入顺序和访问顺序
```





## 15

找链表环入口

```java
// 设置快慢指针，若有环一定环中相遇，因在环中追赶
// 从头节点和相遇节点同步走，一定环入口相遇,因相遇点就是环长度
public class Solution {

    public ListNode EntryNodeOfLoop(ListNode pHead){
        ListNode fast = pHead;
        ListNode slow = pHead;
        
        while(fast != null && fast.next != null){  // 环中相遇
            fast = fast.next.next;
            slow = slow.next;
            if(fast == slow){
                break;
            }
        }
        
        if(fast == null || fast.next == null) return null;  // 判断是否有环
        
        slow = pHead;
        while(slow != fast){
            slow = slow.next;
            fast = fast.next;
        }
        return slow;
    }
}
```





## 16

删除链表中重复的节点，eg：1->2->3->3->4->4->5  处理后为  1->2->5

```java
// 思路使用三个指针
public class Solution {
    public ListNode deleteDuplication(ListNode pHead){
        if(pHead == null || pHead.next == null) return pHead; // 只有0或1个直接返回空节点
        
        ListNode Head = new ListNode(0);
        Head.next = pHead;  // 保存首节点，防止第一二个就重复
        ListNode pre = Head;
        ListNode last = Head.next;
        
        while(last != null){  // 循环条件
            
            // 这里是有重复
            if(last.next != null && last.val == last.next.val){
                while(last.next != null && last.val == last.next.val){
                    last = last.next;
                }
                pre.next = last.next;
                last = last.next;
                
            // 这里没有重复
            }else{
                pre = pre.next;
                last = last.next;
            }
        }
        return Head.next;
    }
}
```





## 17

找出中序遍历二叉树的下一个节点

```java
// 本题的next指向父节点。。。没看题的尴尬
public class Solution {
    public TreeLinkNode GetNext(TreeLinkNode pNode){
        if(pNode == null) return null;
        if(pNode.right != null){  // 当前节点有右子树，找后继节点
            pNode = pNode.right;
            while(pNode.left != null){
                pNode = pNode.left;
            }
            return pNode;
        }
        while(pNode.next != null){  // 没右子树,则访问父节点
            if(pNode.next.left == pNode){  // 当前节点是其父亲的左子树
                return pNode.next;  // 按照中序去访问右子树了
            }
            pNode = pNode.next;  // 如果是其父亲的右子树，那么其父亲已经遍历完了，再向上找没被遍历左子树的
        }
        return null;  // 访问到根都没有，已经被遍历完了
    }
}
```





## 18

判断一颗二叉树是否对称

```java
public class Solution {
    boolean isSymmetrical(TreeNode pRoot){
        if(pRoot == null) return true;
        
        return same(pRoot.left,pRoot.right);
    }
    private boolean same(TreeNode left,TreeNode right){
        if(left == null && right == null) return true;  // 对称二叉树
        if(left == null || right == null) return false;
        if(left.val != right.val) return false;
        return same(left.left,right.right) && same(left.right,right.left);
    }
}
```





## 19 

之子型打印二叉树

```java
import java.util.ArrayList;
import java.util.LinkedList;

public class Solution {
    public ArrayList<ArrayList<Integer> > Print(TreeNode pRoot) {
        ArrayList<ArrayList<Integer>> rs = new ArrayList();
        LinkedList<TreeNode> queue = new LinkedList<>();
        
        if(pRoot == null) return rs;
        queue.add(pRoot);
        int count = 0;  // 记录层数奇偶
        
        while( !queue.isEmpty() ){
            int size = queue.size();  // 此层的节点个数
            ArrayList<Integer> temp = new ArrayList<>();  // 题目规定每层分开存储。。。
            for(int i = 0; i < size; i++){  // size是这层的节点个数，通过for循环poll出此层全部节点
                TreeNode node = queue.poll();
                if(count%2 == 0){
                    temp.add(node.val);
                }else{
                    temp.add(0,node.val);
                }
                if(node.left != null){
                    queue.add(node.left);
                }
                if(node.right != null){
                    queue.add(node.right);
                }
            }
            count++;
            rs.add(temp);
        }
        return rs;
    }
}
```





## 20

层次遍历二叉树（区别于每层独立存储，而且注解使用List还是Linked可以优化）

```java
import java.util.ArrayList;

public class Solution {
    ArrayList<ArrayList<Integer> > Print(TreeNode pRoot) {
        ArrayList<ArrayList<Integer>> rs = new ArrayList();
        ArrayList<TreeNode> queue = new ArrayList();
        
        if(pRoot == null) return rs;
        queue.add(pRoot);
        
        while( !queue.isEmpty() ){
            int size = queue.size();
            ArrayList temp = new ArrayList();
            for(int i = 0; i < size; i++){
                TreeNode node = queue.remove(0);
                temp.add(node.val);
                if(node.left != null){
                    queue.add(node.left);
                }
                if(node.right != null){
                    queue.add(node.right);
                }
            }
            rs.add(temp);
        }
        return rs;
    }
}
```

