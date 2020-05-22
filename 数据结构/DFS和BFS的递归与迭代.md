> 在看数据结构时发现图的这章没怎么看，赶紧瞅瞅



## 1. 树结构

为了方便读者查看简洁的DFS和BFS逻辑，这里把树的基本结构统一抽取出来且不讨论树的实现

```java
// 树的基本结构
public class Tree {

    // 树根
    private Node root;

    // 树的节点
    private class Node{
        int value;
        Node left;
        Node right;
        public Node (int value,Node left,Node right){
            this.value = value;
            this.left = left;
            this.right = right;
        }
    }
    
    // 省略各种树内部操作，添加查找删除
}
```









## 2. DFS

深度优先搜索，从某个初始点出发，首先访问初始点，然后选择一个与该点相邻且没有访问过的点，接着以该相邻点为初始点，重复上述操作，直到所有点都被访问过了，即考虑访问到最深度，然后再回溯



**递归实现**

```java
// 树的DFS日常经常使用，前序遍历即可
// dfs遍历，前序遍历即这个思想，到了叶子节点才回溯
public void dfs(){
    dfs(root);
}
private void dfs(Node node){
    if(node != null){
        System.out.println(node.value);
        dfs(node.left);
        dfs(node.right);
    }
}
```

递归虽然容易实现，但其递归过深容易发生StackOverflowError或OOM



**迭代实现**

```java
// 迭代借用栈来实现，也是前序遍历思想，先访问根打印，然后访问左子树再右子树
// 具体访问顺序使用栈，这里得先右子树入栈，再左子树入栈
// 从栈弹出节点时也是先一样，重复上面步骤

public void dfsWithStack(){

    // LinkedList继承了Deque-->Queue有栈功能
    // 首先入栈根节点
    LinkedList<Node> list = new LinkedList();
    list.addFirst(root);  // 头插法，速度快

    // 栈不空则还有节点没遍历
    while(!list.isEmpty()){
        Node temp = list.removeFirst();  // 头删法
        System.out.println(temp.value);

        if (temp.right != null){
            list.addFirst(temp.right);
        }
        if(temp.left != null){
            list.addFirst(temp.left);
        }
    }
}
```









## 3. BFS

广度优先搜索，从某个节点出发，访问初始节点，接着访问初始节点的所有为未访问过的领接节点，再按照前一步的访问顺序访问每一个未访问过的领接节点，直至所有节点被访问过了



**迭代实现**

```java
// 深度使用栈，而广度使用队列
// 转换到树中就是层级遍历

public void bfsWithStack(){
    Node node = root;
    if(node == null) return ;

    LinkedList<Node> list = new LinkedList();  // 队列
    list.addLast(root);  // 进队

    while(!list.isEmpty()){
        Node temp = list.removeFirst();  // 出队
        System.out.println(temp.value);

        if(temp.left != null){
            list.addLast(temp.left);  // 进队
        }
        if(temp.right != null){
            list.addLast(temp.right);  // 进队
        }
    }
}
```









## 4. 应用

BFS：最短链

DFS：走迷宫