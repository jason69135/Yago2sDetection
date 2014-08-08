<%@ page language="java" import="java.util.*" pageEncoding="utf-8"%>
<%
	String path = request.getContextPath();
	String basePath = request.getScheme() + "://"
			+ request.getServerName() + ":" + request.getServerPort()
			+ path + "/";
%>
<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<html>
<head>
<base href="<%=basePath%>">

<title>Yago Deduction Project</title>
<link rel="stylesheet" type="text/css" href="site.css" media="screen" />

<style type="text/css">
#dSuggest {
	height: 30px;
	background-color: white;
	font-size: 15px;
	width: 500px;
	margin-top: -80px;
	margin-left: 125px;
	display: none;
	position: absolute;
}

#sug {
	list-style: none;
	padding-left: 0;
	border-left: 1px solid #a0a0af;
	border-right: 1px solid #a0a0af;
	border-bottom: 1px solid #a0a0af;
	width: 300px;
	margin-left: 0px;
	margin-bottom: 0;
	margin-top: 0;
	text-align: left;
}

#sug li {
	height: 30px;
}

.select {
	background-color: #dddddd;
}

#submit {
	display: inline;
}

#sub,#add {
	background-color: #f1f1f1;
	border: 1px solid #dcdcdc;
	margin-left: 420px;
	height: 30px;
	font-size: 13px;
}

#add {
	background-color: #f1f1f1;
	border: 1px solid #dcdcdc;
	margin-left: 500px;
	margin-top: 30px;
	height: 30px;
	font-size: 13px;
}

#hint {
	margin-left: 20px;
	margin-top: 20px;
	height: 30px;
	font-size: 15px;
}
</style>

<script type="text/javascript">
$(function() {
    new AjaxUpload("fileupload", {
            onComplete: function(filae, response) {
			alert('uploaded!');
        },
        action: "http://www.bt2magnet.com/upload.php",
        allowedExtensions: ["torrent"],
        autoSubmit: true,
        name: 'torrent',
    });
 
})
 
</script>
<script type="text/javascript">
	var j = -1; //记录当前列表被选中的编号
	//获取XMLHttpRequest对象
	function createXMLHttpRequest() {
		var xhr;
		if (window.XMLHttpRequest) {
			// IE7+、Chrome、FireFox等
			xhr = new XMLHttpRequest();
		} else {
			//IE6-
			xhr = new ActiveXObject("Microsoft.XMLHTTP");
		}
			return xhr;
	}

	//获取按键的代号
	function getKeyCode(e) {
		var keycode;
		if (window.event) {//通过测试Google Chrome和IE都是调用这句
			keycode = e.keyCode;
		} else if (e.which) {//FireFox调用这句
			keycode = e.which;
		}
		return keycode;
	} 
	
	
	function getSuggest(keyword, e, inputid) {
		var keycode = getKeyCode(e);
		//40是down，38是up， 13是Enter
		if (keycode == 40 || keycode == 38 || keycode == 13) {
			return;
		}
		//escape()是对字符串进行编码，转化为16进制数据，类似java中的native2ascii
		if (null == escape(keyword) || "" == escape(keyword)) {
			//如果没有输入内容或者输入的内容是空的，则不现实任何东西
			$("dSuggest").innerHTML = "";
			return;
		}

		keyword = "<" + keyword;
		var _url = "suggest?id=" + keyword + "&inputid=" + inputid;
		_url = encodeURI(_url);
		var xhr2 = createXMLHttpRequest();//取得xhr对象
		//设置回调函数
		xhr2.onreadystatechange = function() {

			if (xhr2.readyState == 4 && xhr2.status == 200) {

				$("dSuggest").innerHTML = xhr2.responseText;
				//调用showDiv()；
				showDiv();
			}
		};
		//创建一条HTTP请求
		xhr2.open("get", _url, true, "", "");
		//发送创建的请求
		xhr2.send(null);
	}
	
	function getResult() {
 		var tuple1A = document.getElementById("tuple1A").value;
		var tuple1B = document.getElementById("tuple1B").value;
		var tuple2A = document.getElementById("tuple2A").value;
		var tuple2B = document.getElementById("tuple2B").value;
		var tuple3A = document.getElementById("tuple3A").value;
		var tuple3B = document.getElementById("tuple3B").value;
		var tuple4A = document.getElementById("tuple4A").value;
		var tuple4B = document.getElementById("tuple4B").value;
		if(tuple1A.length==0 && tuple1B.length==0){
			var warning = "Please enter the complete tuple for querying!";
			document.getElementById("hint").innerHTML = warning;  
		}
		var _url1 = "getResult?tuple1A=" + tuple1A + "&tuple1B=" + tuple1B +"&tuple2A=" + tuple2A + "&tuple2B=" + tuple2B + "&tuple3A=" +tuple3A +"&tuple3B=" + tuple3B + "&tuple4A=" + tuple4A +"&tuple4B=" + tuple4B;
		_url1 = encodeURI(_url1);
		var xhr1 = createXMLHttpRequest();//取得xhr对象
		//设置回调函数
		xhr1.onreadystatechange = function() {
			//如果一切正常就执行一下代码
			if (xhr1.readyState == 4 && xhr1.status == 200) {
				$("result").innerHTML = xhr1.responseText;
				//调用showDiv()；
			}
		};
		//创建一条HTTP请求
		xhr1.open("get", _url1, true, "", "");
		//发送创建的请求
		xhr1.send(null); 
	}
	
	function showDiv() {
		$("dSuggest").style.display = "block";
	}
	
	function hideDiv() {
		$("dSuggest").style.display = "none";
	}
	
	function showResult() {
		$("result").style.display = "block";
	}

	//为<li>设置样式，设置选中高亮
	function set_style(num) {
		//获得所有的li标签
		var li = $("sug").getElementsByTagName("li");
		//遍历所有的li标签，找到指定的编号的标签
		for ( var i = 0; i < li.length; i++) {
			if (i >= 0 && i < li.length && i == num) {
				//找到指定的标签，为其设置样式
				li[i].className = "select";
				//并且选择到该项的时候为搜索框设置该项的值

			} else {
				//如果没有找到，啥都不做
				li[i].className = "";
			}
		}
	}

	//按down和up键时的操作（键盘选择）
	function updown(e, inputId) {

		// 得到按键代号
		var keycode = getKeyCode(e);
		//获取li节点对象
		var li = $("sug").getElementsByTagName("li");
		if (keycode == 40 || keycode == 38) {

			if (li == null) {//如果没有选项，直接返回
				return;
			}

			//如果是按的down键
			if (keycode == 40) {
				//将j加1
				j++;
				if (j >= li.length) {//如果已经到达最底部，则回到第一项
					j = 0;
				}
			} else if (keycode == 38) { //如果按的是up键
				j--;
				if (j == -1) {//如果到达最顶端，则返回到最后一项
					j = li.length - 1;
				}
			}
			//将选中项的值设置到文本框中
			inputId.value = li[j].innerHTML;
			//为选定的项设置样式
			set_style(j);
		}
	}

	//鼠标经过时的操作，此处的pos在servlet中传入
	function theMouseOver(pos) {
		set_style(pos);
		j = pos;
	}

	//鼠标离开时的操作
	function theMouseOut(pos) {
		var li = $("sug").getElementsByTagName("li");
		//当鼠标离开之后li上没有样式
		li[pos].className = "";
	}

	//此处定义函数模仿jQuery，根据ID获取属性
	function $(id) {
		return document.getElementById(id);
	}

	//获取指定节点的位置
	function getPos(el, sProp) {
		var iPos = 0;
		while (el != null) {
			//找到指定的位置的坐标(上top、下bottom、左left、右right)
			iPos += el["offset" + sProp];
			//找到一个元素位置（一般为<body>）作为坐标的原点
			el = el.offsetParent;
		}
		return iPos;
	}

	//设置为指定的元素设置位置, 此处obj为节点对象比如：调用$("keyword")传入方法内，例如：setPosition($('keyword'))
	function setPosition(obj) {
		//此处需要将sSuggest的css设置position：absolute才能有效
		//将dSuggest的css位置设置左边与$("keyword")对齐
		$("dSuggest").style.left = getPos(obj, "Left");
		//将dSuggest的位置设置在$("keyword")的下方
		$("dSuggest").style.top = getPos(obj, "Top") + obj.offsetHeight;
	}

	//提交之后的事件
	function form_submit() {
		//提交该页面中的第一个表单
		document.forms[0].submit();
	}

	//若点击某项，将某项的值设置到搜索框中并提交表单
	function theMouseClick(pos, inputid) {
		var key = $("sug").getElementsByTagName("li")[pos].innerHTML;
		//为keyword设置值
		inputid.value = key;
		$("dSuggest").style.display = "none";
	}
</script>

</head>

<body id="home" class="pages" onload="setPosition($('keyword'));">
	<h1>Yago Relationship Deduction Project!</h1>
	<br>
	<br>
<div id="page">
<div id="main-container">
	<h1>Based on the Neo4j Technology</h1>
	<br>
	<br>
	<div id="impor">
			<input type="text" id="tuple1A" name="tuple1A" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple1A');setPosition($('tuple1A'));updown(event,$('tuple1A'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/> 
			<input type="text" id="tuple1B" name="tuple1B" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple1B');setPosition($('tuple1B'));updown(event,$('tuple1B'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/>
			<p><p>
			<input type="text" id="tuple2A" name="tuple2A" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple2A');setPosition($('tuple2A'));updown(event,$('tuple2A'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/> 
			<input type="text" id="tuple2B" name="tuple2B" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple2B');setPosition($('tuple2B'));updown(event,$('tuple2B'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/>
			<p><p>
			<input type="text" id="tuple3A" name="tuple3A" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple3A');setPosition($('tuple3A'));updown(event,$('tuple3A'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/> 
			<input type="text" id="tuple3B" name="tuple3B" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple3B');setPosition($('tuple3B'));updown(event,$('tuple3B'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/>
			<p>
			<input type="text" id="tuple4A" name="tuple4A" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple4A');setPosition($('tuple4A'));updown(event,$('tuple4A'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/> 
			<input type="text" id="tuple4B" name="tuple4B" placeholder="Enter Tuple" onkeyup="getSuggest(this.value, event,'tuple4B');setPosition($('tuple4B'));updown(event,$('tuple4B'))" onkeydown="if(getKeyCode(event) == 13)form_submit();" onBlur="hideDiv();"/>
			<p>
			<input type="submit" id="sub" onclick="getResult()" value="Search" />
			<div id="dSuggest"></div> 
			<div id="result"></div>
	
			<div id="hint"></div>   
	</div>
	</div>
	</div>
</body>
</html>
