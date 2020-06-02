## 1

两数之和

```java
// 暴力法：O(n^2)
class Solution {
    public int[] twoSum(int[] nums, int target) {
        int[] arr = new int[2];
        for (int i = 0; i < nums.length; i++) {
            for (int j = i + 1; j < nums.length; j++) {
                if (nums[j] + nums[i] == target) {
                    arr[0] = i;
                    arr[1] = j;
                }
            }
        }
        return arr;
    }
}
```

```java
// 一遍哈希表HashMap
class Solution {
    public int[] twoSum(int[] nums, int target) {
        
        int[] arr = new int[2];
        HashMap map = new HashMap();  // 哈希表

        for(int i = 0; i < nums.length; i++){
            
            int minus = target - nums[i];
            if (map.containsKey(minus)) {
                arr[0] = (Integer) map.get(minus);
                arr[1] = i;
            }
            map.put(nums[i], i);  // put数进去
        }
        return arr;
    }
}
```





## 2

反转数字

```java
class Solution {
    public int reverse(int x) {
        long n = 0;                    // 用长整型来判断溢出
        while(x != 0){
            n *= 10;
            n += x % 10;
            x /= 10;
        }
        return (int)n == n ? (int)n : 0;
    }
}
```

