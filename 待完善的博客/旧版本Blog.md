## 1. 选项

![1605970144217](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1605970144217.png)





## 2. 设置



### 2.1 标题内容

![1605970182004](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1605970182004.png)





### 2.2 页面定制CSS代码

```css
/* 背景格子图 */
body {
    background-image: url("https://blog-static.cnblogs.com/files/Howlet/bg.gif");
}






/* 公告换头像 ------   [开始]*/
#profile_block {
    display: block;
}
#blog-news{
    display:none;
}
#sidebar_news h3{
    display:none;
}
#headImg {
    width: 200px;
    height: 200px;
}
#codeImg{
    display: none;
    width: 140px;
    height: 140px;
    position: absolute;
    top: 50%;
    left: 50%;
    transform: translate(-50%, -50%);
}
#sidebar_news{
    width: 200px;
    height: 200px;
    position: relative;
    margin-bottom: 0px;
}
#wxAcount{
    display: none;
    position: absolute;
    top: 90%;
    left: 50%;
    transform: translate(-50%, -50%);
    color: rgb(233, 195, 157);
    font-size: 14px;
    font-family: "Noto Serif SC","PT Serif",Georgia,Palatino,Songti SC,serif";
}
#sidebar_news:hover > #headImg{
    display: none;
}
#sidebar_news:hover > #codeImg{
    display: block;
}
#sidebar_news:hover > #wxAcount{
    display: block;
}
/* 公告换头像 ------   [结束]*/






/* 去除广告 */
.under-comment-nav {
    display: none;
}
#ad_text_under_commentbox {
    display: none;
}
#ad_t2 {
    display: none;
}
#under_post_news {
    display: none;
}
#cnblogs_c2 {
    display: none;
}
#under_post_kb {
    display: none;
}
#HistoryToday {
    display: none;
}
#cnblogs_c1 {
    display: none;
}




/* 页脚优化 */
#poweredby{
    display: none;
}
#footer{
    background-color: #6f6e6e;
}
```





### 2.3 侧边栏公告

```js
<script>

// 背景蜘蛛丝    [开始]
! function() {
    //封装方法，压缩之后减少文件大小
    function get_attribute(node, attr, default_value) {
        return node.getAttribute(attr) || default_value;
    }
    //封装方法，压缩之后减少文件大小
    function get_by_tagname(name) {
        return document.getElementsByTagName(name);
    }
    //获取配置参数
    function get_config_option() {
        var scripts = get_by_tagname("script"),
            script_len = scripts.length,
            script = scripts[script_len - 1]; //当前加载的script
        return {
            l: script_len, //长度，用于生成id用
            z: get_attribute(script, "zIndex", -1), //z-index
            o: get_attribute(script, "opacity", 0.5), //opacity
            c: get_attribute(script, "color", "0,0,0"), //color
            n: get_attribute(script, "count", 99) //count
        };
    }
    //设置canvas的高宽
    function set_canvas_size() {
        canvas_width = the_canvas.width = window.innerWidth || document.documentElement.clientWidth || document.body.clientWidth,
        canvas_height = the_canvas.height = window.innerHeight || document.documentElement.clientHeight || document.body.clientHeight;
    }
 
    //绘制过程
    function draw_canvas() {
        context.clearRect(0, 0, canvas_width, canvas_height);
        //随机的线条和当前位置联合数组
        var e, i, d, x_dist, y_dist, dist; //临时节点
        //遍历处理每一个点
        random_lines.forEach(function(r, idx) {
            r.x += r.xa,
            r.y += r.ya, //移动
            r.xa *= r.x > canvas_width || r.x < 0 ? -1 : 1,
            r.ya *= r.y > canvas_height || r.y < 0 ? -1 : 1, //碰到边界，反向反弹
            context.fillRect(r.x - 0.5, r.y - 0.5, 1, 1); //绘制一个宽高为1的点
            //从下一个点开始
            for (i = idx + 1; i < all_array.length; i++) {
                e = all_array[i];
                //不是当前点
                if (null !== e.x && null !== e.y) {
                        x_dist = r.x - e.x, //x轴距离 l
                        y_dist = r.y - e.y, //y轴距离 n
                        dist = x_dist * x_dist + y_dist * y_dist; //总距离, m
                    dist < e.max && (e === current_point && dist >= e.max / 2 && (r.x -= 0.03 * x_dist, r.y -= 0.03 * y_dist), //靠近的时候加速
                        d = (e.max - dist) / e.max,
                        context.beginPath(),
                        context.lineWidth = d / 2,
                        context.strokeStyle = "rgba(" + config.c + "," + (d + 0.2) + ")",
                        context.moveTo(r.x, r.y),
                        context.lineTo(e.x, e.y),
                        context.stroke());
                }
            }
        }), frame_func(draw_canvas);
    }
    //创建画布，并添加到body中
    var the_canvas = document.createElement("canvas"), //画布
        config = get_config_option(), //配置
        canvas_id = "c_n" + config.l, //canvas id
        context = the_canvas.getContext("2d"), canvas_width, canvas_height,
        frame_func = window.requestAnimationFrame || window.webkitRequestAnimationFrame || window.mozRequestAnimationFrame || window.oRequestAnimationFrame || window.msRequestAnimationFrame || function(func) {
            window.setTimeout(func, 1000 / 45);
        }, random = Math.random,
        current_point = {
            x: null, //当前鼠标x
            y: null, //当前鼠标y
            max: 20000
        },
        all_array;
    the_canvas.id = canvas_id;
    the_canvas.style.cssText = "position:fixed;top:0;left:0;z-index:" + config.z + ";opacity:" + config.o;
    get_by_tagname("body")[0].appendChild(the_canvas);
    //初始化画布大小
 
    set_canvas_size(), window.onresize = set_canvas_size;
    //当时鼠标位置存储，离开的时候，释放当前位置信息
    window.onmousemove = function(e) {
        e = e || window.event, current_point.x = e.clientX, current_point.y = e.clientY;
    }, window.onmouseout = function() {
        current_point.x = null, current_point.y = null;
    };
    //随机生成config.n条线位置信息
    for (var random_lines = [], i = 0; config.n > i; i++) {
        var x = random() * canvas_width, //随机位置
            y = random() * canvas_height,
            xa = 2 * random() - 1, //随机运动方向
            ya = 2 * random() - 1;
        random_lines.push({
            x: x,
            y: y,
            xa: xa,
            ya: ya,
            max: 12000 //沾附距离
        });
    }
    all_array = random_lines.concat([current_point]);
    //0.1秒后绘制
    setTimeout(function() {
        draw_canvas();
    }, 100);
}();
// 背景蜘蛛丝    [结束]


// 创建头像元素、二维码、微信
var head = document.createElement("img");
head.setAttribute("id","headImg");
head.setAttribute("src","https://images.cnblogs.com/cnblogs_com/Howlet/1579506/o_200308035421logo.png");

var code = document.createElement("img");
code.setAttribute("id","codeImg");
code.setAttribute("src","https://images.cnblogs.com/cnblogs_com/Howlet/1579506/o_201101023253QRcode.jpg");

// var wxAcount = document.createElement("p");
// wxAcount.setAttribute("id","wxAcount");
// wxAcount.innerHTML= "loadingKiller";

var sidebar_news = document.getElementById("sidebar_news");
sidebar_news.appendChild(head);
sidebar_news.appendChild(code);
// sidebar_news.appendChild(wxAcount);


// SEO搜索优化
document.title = 'loadingKiller的小小博客';


</script>
```







## 3. 效果

![1605970334838](C:\Users\Howl\AppData\Roaming\Typora\typora-user-images\1605970334838.png)