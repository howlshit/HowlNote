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

