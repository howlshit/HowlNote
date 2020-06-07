## 1

两数之和

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

反转数字（要判断溢出）

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





## 3

删除排序数组中的重复项，要求 S(1）

```java
// 思路：双指针
// 1. nums[i] = nums[j]，我们就增加 j 以跳过重复项（for里面自动j++）
// 2. nums[j] != nums[i]，则将nums[j] 复制到nums[i+1] 
class Solution {
    public int removeDuplicates(int[] nums) {
        if(nums == null) return 0;
        
        int i = 0;								// 指针 i,j
        for(int j = 1; j < nums.length; j++){   // 
            if(nums[i] != nums[j]){
                nums[i+1] = nums[j];
                i++;
            }
        }
        return i+1;
    }
}
```

