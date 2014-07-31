// JavaScript Document
function change02(obj){
  for(var i=0;i<2;i++){
   document.getElementById("hot_new"+i).className="navv02";
   document.getElementById("hot_content_"+i).style.display="none";
  }
  document.getElementById("hot_new"+obj).className="navv01";
  document.getElementById("hot_content_"+obj).style.display="";
 }

function doChangeNumber(form,tableID){
	var redBallCount = form.elements["redBallCount"];
	var blueBallCount = form.elements["blueBallCount"];
	var table = document.getElementById(tableID);

	var rows = table.rows;

	var castMoney = document.getElementById("castMoney");



	castMoney.innerText = comp(6,redBallCount.value)*blueBallCount.value*2+"元";



	var rowLen = rows.length;

	for(var i = 1;i<rowLen;i++){

		var tr = rows[i];



		var td0 = tr.cells[0];

		var td1 = tr.cells[1];

		var td2 = tr.cells[2];

		var td3 = tr.cells[3];

		var td4 = tr.cells[4];

		var td5 = tr.cells[5];

		var td6 = tr.cells[6];

		var td7 = tr.cells[7];

		var td8 = tr.cells[8];

		var td9 = tr.cells[9];

		var td10 = tr.cells[10];

		var td11 = tr.cells[11];

		

		td0.innerText=redBallCount.value;

		td1.innerText=blueBallCount.value;

		td2.innerText = comp(6,redBallCount.value)*(blueBallCount.value*2);



		var a = td0.innerText;

		var b = td1.innerText;

		var c = td3.innerText;

		var d = td4.innerText;

		

		if(blueBallCount.value==16&&d==0){

			tr.style.display="none";

		}

		else{

			tr.style.display="";

		}

		

		td5.innerText = calFirst(a,b,c,d);

		td6.innerText = calSecond(a,b,c,d);

		td7.innerText = calThird(a,b,c,d);

		td8.innerText = calFourth(a,b,c,d);

		td9.innerText = calFiveth(a,b,c,d);

		td10.innerText = calSixth(a,b,c,d);

		td11.innerText =getMoney(td5.innerText,"A")+getMoney(td6.innerText,"B")+ (cal(td7.innerText,3000)+cal(td8.innerText,200)+cal(td9.innerText,10)+cal(td10.innerText,5));



	}

}

function getMoney(num,text){

	var count = new Number(num);

	if(count>0){

		return count+text+"+";

	}

	else{

		return "";

	}

}





function cal(a,b){

	var A = new Number(a);

	var B = new Number(b);	

	return A*B;

}

function comp(head,foot){

	var A = new Number(head);

	var B = new Number(foot);

	var C = 1;

	for(var i = B-A+1;i<=B;i++){

		C = C*i;

	}

	for(var i = 2;i<=A;i++){

		C=C/i;

	}

	return C;

}



function calFirst(a,b,c,d){

	var A = new Number(a);

	var B = new Number(b);

	var C = new Number(c);

	var D = new Number(d);

	var value = D*comp(6,C)*comp(0,A-C);

	return value;

}

function calSecond(a,b,c,d){

	var A = new Number(a);

	var B = new Number(b);

	var C = new Number(c);

	var D = new Number(d);

	var value = (B-D)*comp(6,C)*comp(0,A-C);

	return value;

}

function calThird(a,b,c,d){

	var A = new Number(a);

	var B = new Number(b);

	var C = new Number(c);

	var D = new Number(d);

	var value = D*comp(5,C)*comp(1,A-C);

	return value;

}



function calFourth(a,b,c,d){

	var A = new Number(a);

	var B = new Number(b);

	var C = new Number(c);

	var D = new Number(d);

	var value = D*comp(4,C)*comp(2,A-C)+(B-D)*comp(5,C)*comp(1,A-C);

	return value;

}

function calFiveth(a,b,c,d){

	var A = new Number(a);

	var B = new Number(b);

	var C = new Number(c);

	var D = new Number(d);

	var value = D*comp(3,C)*comp(3,A-C)+(B-D)*comp(4,C)*comp(2,A-C);

	return value;

}

function calSixth(a,b,c,d){

	var A = new Number(a);

	var B = new Number(b);

	var C = new Number(c);

	var D = new Number(d);

	var value = D*comp(2,C)*comp(4,A-C)+D*comp(1,C)*comp(5,A-C)+D*comp(0,C)*comp(6,A-C);

	return value;

}


	var num;

	var i;

	var tempNum;

	tempNum=0;

	function show(num)

	{

		if(fm1.rednum.value<20&&eval(fm1.RedGlob1[num-1]).style.display=="none")

		{//选择的红球

			eval(fm1.RedGlob1[num-1]).style.display="";

			eval(fm1.RedGlob2[num-1]).disabled=false;

			fm1.rednum.value++;

			myrednum.innerHTML=fm1.rednum.value;

			num=getnum(fm1.rednum.value,fm1.bluenum.value);

			myallnum.innerHTML=num;

			mymoney.innerHTML=2*num;

		}

	}

	function show2(num)

	{//取消红球

		eval(fm1.RedGlob1[num-1]).style.display="none";

		eval(fm1.RedGlob2[num-1]).disabled=true;

		fm1.rednum.value--;

		myrednum.innerHTML=fm1.rednum.value;

		num=getnum(fm1.rednum.value,fm1.bluenum.value);

		myallnum.innerHTML=num;

		mymoney.innerHTML=2*num;

	}

	function show3(num)

	{

		if(fm1.bluenum.value<17&&eval(fm1.BlueGlob1[num-1]).style.display=="none")

		{//选择篮球

			eval(fm1.BlueGlob1[num-1]).style.display="";

			eval(fm1.BlueGlob2[num-1]).disabled=false;

			fm1.bluenum.value++;

			mybluenum.innerHTML=fm1.bluenum.value;

			num=getnum(fm1.rednum.value,fm1.bluenum.value);

			myallnum.innerHTML=num;

			mymoney.innerHTML=2*num;

		}

	}

	function show4(num)

	{//取消篮球

		eval(fm1.BlueGlob1[num-1]).style.display="none";

		eval(fm1.BlueGlob2[num-1]).disabled=true;

		fm1.bluenum.value--;

		mybluenum.innerHTML=fm1.bluenum.value;

		num=getnum(fm1.rednum.value,fm1.bluenum.value);

		myallnum.innerHTML=num;

		mymoney.innerHTML=2*num;

	}

	

	function getnum(rn,bn)

	{		

		if(rn<6)

			return 0;

		if(bn==0)

			return 0;

		if(rn==6)

		{

			fm1.tempnum.value = bn;

			return bn;

		}

		

		tempNum=1;

		for(i=7;i<=rn;i++)

		{

			tempNum=tempNum*i;

		}

		for(i=2;i<=rn-6;i++)

		{

			tempNum=tempNum/i;

		}

		tempNum=tempNum*bn;

		fm1.tempnum.value = tempNum;



		if(tempNum>620160)

		{

			alert("方案注数最大不得超过620160注");

			return false;

		}	

		

		return tempNum;

	}

	

	function mysubmit()

	{

		var length = fm1.RedGlob2.length;

		var chooseRed = '';

		for (i=0;i<length;i++)

		{

			if (eval(fm1.RedGlob1[i]).style.display == "")//选择的红球

				{

					chooseRed += fm1.RedGlob2[i].value+',';

				}

		}

		fm1.choosered.value = chooseRed;



		var length = fm1.BlueGlob2.length;

		var chooseBlue = '';

		for (i=0;i<length;i++)

		{

			if (eval(fm1.BlueGlob1[i]).style.display == "")//选择的红球

				{

					chooseBlue += fm1.BlueGlob2[i].value+',';

				}

		}

		fm1.chooseblue.value = chooseBlue;

		var tempNum = fm1.tempnum.value;

		if(tempNum <= 3)

		{

			alert("合买最少1注,即6个红球,1个蓝球");

			return false;

		}

		if(tempNum>620160)

		{

			alert("方案注数最大不得超过620160注");

			return false;

		}	

				

		if(fm1.title.value=="")

		{

			alert("请输入方案标题");

			return false;

		}

		if(fm1.content.value=="")

		{

			alert("请输入方案内容");

			return false;

		}	

	}





//dantuo
//显示红球胆码
function showhqdm(obj){
	//alert(obj.value);
	
	var aa=Number(obj.value);
	var bb="hqdmtz"+aa;
	var dd="hqtmxz"+aa;
	var ff="hqdmxz"+aa;
	
	obj.disabled="false";
	//显示计算结果
	var hqdm_count=0;
	for(i=1;i<=33;i++){
		var a="hqdmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			hqdm_count++;
		}
	}
	if(hqdm_count>=5){
		alert("胆码不能超过5个!");
		var   gg=document.getElementById(ff);
		gg.disabled="";	
	}
	else{
	var   cc=document.getElementById(bb);
	cc.style.display="";		
	//alert(dd);
	var ee=document.getElementById(dd);
	//alert(ee.value);
	ee.disabled="false";
	}
	
	
	var   hqdmtj=document.getElementById("hqdmtj");
	hqdmtj.innerText=hqdm_count+1;
	
}

//显示红球拖码
function showhqtm(obj){
	//alert(obj.value);
	obj.disabled="false";
	var aa=Number(obj.value);
	var bb="hqtmtz"+aa;
	//alert(b);
	var   cc=document.getElementById(bb);
	cc.style.display="";
	
	var objzhi=Number(obj.value);
	var dd="hqdmxz"+objzhi;
	var ee=document.getElementById(dd);
	//alert(ee.value);
	ee.disabled="false";
	
	//显示计算结果
	var hqtm_count=0;
		for(i=1;i<=33;i++){
		var a="hqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			hqtm_count++;
		}
	}
	var   hqtmtj=document.getElementById("hqtmtj");
	hqtmtj.innerText=hqtm_count;
	
	
	
}
//显示篮球拖码
function showlqtm(obj){
	//alert(obj.value);
	obj.disabled="false";
	var aa=Number(obj.value);
	var bb="lqtmtz"+aa;
	//alert(b);
	var   cc=document.getElementById(bb);
	cc.style.display="";
	
	//显示计算结果
	var lqtm_count=0;
		
	for(i=1;i<=16;i++){
		var a="lqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			lqtm_count++;
		}
	}
	var   lqtmtj=document.getElementById("lqtmtj");
	lqtmtj.innerText=lqtm_count;
	
}
//隐藏选中号码
function yincanghao_hqdm(obj){
	obj.style.display="none";
	var objzhi=Number(obj.value);
	var dd1="hqtmxz"+objzhi;
	var dd2="hqdmxz"+objzhi;
	var jhhqtm1=document.getElementById(dd1);
	var jhhqtm2=document.getElementById(dd2);
	jhhqtm1.disabled="";
	jhhqtm2.disabled="";
	
	//显示计算结果
	var hqdm_count=0;
	for(i=1;i<=33;i++){
		var a="hqdmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			hqdm_count++;
		}
	}
	
	var   hqdmtj=document.getElementById("hqdmtj");
	hqdmtj.innerText=hqdm_count;
}


//隐藏选中号码
function yincanghao_hqtm(obj){
	obj.style.display="none";
	var objzhi=Number(obj.value);
	var dd1="hqdmxz"+objzhi;
	var dd2="hqtmxz"+objzhi;
	var jhhqdm1=document.getElementById(dd1);
	var jhhqdm2=document.getElementById(dd2);
	jhhqdm1.disabled="";
	jhhqdm2.disabled="";
	
	//显示计算结果
	var hqtm_count=0;
	
	
	var hqtm_count=0;
		for(i=1;i<=33;i++){
		var a="hqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			hqtm_count++;
		}
	}
	
	var   hqtmtj=document.getElementById("hqtmtj");
	hqtmtj.innerText=hqtm_count;
	
}

//隐藏选中号码
function yincanghao_lqtm(obj){
	obj.style.display="none";
	var objzhi=Number(obj.value);
	var dd1="lqtmxz"+objzhi;
	var jhlqtm1=document.getElementById(dd1);
	jhlqtm1.disabled="";
	
	//显示计算结果
	var lqtm_count=0;
	for(i=1;i<=16;i++){
		var a="lqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			lqtm_count++;
		}
	}
	var   lqtmtj=document.getElementById("lqtmtj");
	lqtmtj.innerText=lqtm_count;
}


//重新选择
function resert(){
	for(i=1;i<=33;i++){
		var a="hqdmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			b.style.display="none"
		}
	}
	
	for(i=1;i<=33;i++){
		var a="hqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			b.style.display="none"
		}
	}
	
	for(i=1;i<=16;i++){
		var a="lqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			b.style.display="none"
		}
	}
	
	for(i=1;i<=33;i++){
		var a="hqdmxz"+i;
		var b=document.getElementById(a);
			b.disabled="";
	}
	
	for(i=1;i<=33;i++){
		var a="hqtmxz"+i;
		var b=document.getElementById(a);
			b.disabled="";
	}
	
	for(i=1;i<=16;i++){
		var a="lqtmxz"+i;
		var b=document.getElementById(a);
			b.disabled="";
	}
	
	
	
	var   hqdmtj=document.getElementById("hqdmtj");
	var   hqtmtj=document.getElementById("hqtmtj");
	var   lqtmtj=document.getElementById("lqtmtj");   
	var   fazs=document.getElementById("fazs");
	var   tzje=document.getElementById("tzje");
	hqdmtj.innerText=0;
	hqtmtj.innerText=0;
	lqtmtj.innerText=0;
	fazs.innerText=0;
	tzje.innerText=0;
}


//统计投注
function count_betting(){
	//显示计算结果
	var hqdm_count=0;
	var hqtm_count=0;
	var lqtm_count=0;
	
	var fazs=0;
	var tzje=0;
	for(i=1;i<=33;i++){
		var a="hqdmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			hqdm_count++;
		}
	}
	
	var hqtm_count=0;
		for(i=1;i<=33;i++){
		var a="hqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			hqtm_count++;
		}
	}
	
	for(i=1;i<=16;i++){
		var a="lqtmtz"+i;
		var b=document.getElementById(a);
		if(b.style.display==""){
			lqtm_count++;
		}
	}
	if(hqdm_count>5){
		alert("红球胆码数不可超过5个!");
	}else if(hqdm_count<1){
		alert("未选中红球胆码!");
	}else if(hqtm_count<1){
		alert("未选中红球拖码!");
	}else if(lqtm_count<1){
		alert("未选中蓝球拖码!");
	}else if((hqdm_count+hqtm_count)<6){
		alert("选中的红球不够6个!");
	}else {
		var r=6-hqdm_count;
		var n=hqtm_count;
		fazs=(jiecheng(n)/(jiecheng(r)*jiecheng(n-r)))*lqtm_count;
		//alert(jiecheng(3));
	
	
	}
	
	var fazs1=document.getElementById("fazs");
	var tzje1=document.getElementById("tzje");
	fazs1.innerText=fazs;
	tzje1.innerText=fazs*2;
	
}

//求M的阶乘
function jiecheng(m){ //求阶乘
   if(m==1 || m==0)return 1;
    else return m*(jiecheng(m-1)); //递归算:法n!=n*(n-1)!
} 
