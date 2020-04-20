### 3.1 

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
        while(end > start){
            int temp = (start + end) * (end - start + 1) / 2;  // 当前窗口内之和
            if(temp == sum){  // 相等即保存该序列
                ArrayList list = new ArrayList();
                for(int i = start; i <= end; i++){
                    list.add(i);
                }
                rs.add(list);
                start++;
            }else if(temp < sum){
                end++;
            }else{
                start++;
            }
        }
        return rs;
    }
}
```





## 3.2

在递增数组中查找和为S的两个数，输出其中两个数乘积最小的

```java
/**
 * a + b = s;
 * (a - m) + (b + m) = s，模拟从外到内的其他组合
 * 而(a - m)(b + m) = ab - (b-a)m - m * m < ab，说明外层的乘积更小
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





## 3.3

字符串循环左移

```java
// 剑指offer的三次反转
public class Solution {
    public String LeftRotateString(String str,int n) {
        
        if(str == null || str.length() == 0 || n == 0){
            return str;
        }
        
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
            char temp;
            temp = arr[start];
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





## 3.4

句子反转：student a am I   ---->  I am a student

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
            if(right == arr.length-1){  // 单独处理最后一个，不用++了
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
            char temp;
            temp = arr[start];
            arr[start] = arr[end];
            arr[end] = temp;
            start++;
            end--;
        }
    }
}
```





## 3.5

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





## 3.6

约瑟夫环问题，n为总数，m为间隔数

```java
import java.util.LinkedList;
public class Solution {
    public int LastRemaining_Solution(int n, int m) {
        LinkedList<Integer> list = new LinkedList();  // 链表模拟约瑟夫环
        for(int i = 0; i < n; i++) list.add(i);
        int pre = 0;                                  // 记录上次去除的位置
        while(list.size() > 1){                       // 剩最后一人时结束
            int cur = (pre + m - 1) % list.size();    // （上次位置+间隙） % 长度 = 当前该删位置
            list.remove(cur);                         // 元素被去除
            pre = cur;                                // 上次的位置，变成这次的了
        }
        return list.size() == 1 ? list.get(0) : -1;
    }
}

// 循环或者一句搞定
// int cur = 0;
// cur = (cur + m - 1) % list.size();
```





## 3.7

求1+2+...n，不用乘除法、for、while、if、else、switch、case

```java
// 因为java判断有些不同，所以要用到无意义的变量
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





## 3.8 

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

