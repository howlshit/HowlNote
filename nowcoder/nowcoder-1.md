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

