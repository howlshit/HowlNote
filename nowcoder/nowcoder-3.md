### 1 

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





## 2

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





## 3

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





## 4

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





## 7

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
            }else if(sum != 0){  // 这里可能返回 + - 0
                return 0;
            }
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





## 10

输出数组第一个重复元素

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





## 11

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
        B[0] = 1;
        for(int i = 1; i < B.length; i++){
            B[i] = B[i-1] * A[i-1];  // 下三角
        }
        int temp = 1;
        for(int j = B.length-2; j >= 0; j--){
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





## 12

正则表达式匹配

```java
public class Solution {
    public boolean match(char[] str, char[] pattern){
        if(str == null || pattern == null) return false;
        return match(str,pattern,0,0);
    }
    private boolean match(char[] str, char[] pattern,int sindex,int pindex){
        // 完美匹配
        if(sindex == str.length && pindex == pattern.length) return true;
        
        // pattern先用完了
        if(pindex >= pattern.length) return false;
        
        // 下一个字符是 *
        if(pindex + 1 < pattern.length && pattern[pindex + 1] == '*'){
            // 当前字符相等或遇到'.'
            if( sindex < str.length && (str[sindex] == pattern[pindex] || pattern[pindex] == '.') ){
                return match(str,pattern,sindex,pindex+2)    // 模式后移2，视为x*匹配0个字符
                    || match(str,pattern,sindex+1,pindex+2)  // 视为模式匹配1个字符
                    || match(str,pattern,sindex+1,pindex);   // *匹配1个，再匹配str中的下一个
            }else{
                return match(str,pattern,sindex,pindex+2);
            }
            
        // 下一个字符不是 *
        }else{
            if( sindex < str.length && (str[sindex] == pattern[pindex] || pattern[pindex] == '.') ){
                return match(str,pattern,sindex+1,pindex+1);
            }
            else{
                return false;
            }
        }
    }
}
```

