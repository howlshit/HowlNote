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

