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









## 3.2

