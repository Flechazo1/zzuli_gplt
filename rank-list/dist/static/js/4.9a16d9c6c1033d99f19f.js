webpackJsonp([4],{"/6cs":function(t,e,a){"use strict";Object.defineProperty(e,"__esModule",{value:!0});var o=a("mtWM"),r=a.n(o);var s={name:"manage",data:function(){return{active:0,step1_form:{sessionid:"bb41e5b8-951e-42c8-94cc-129a42fb1a92",problemid:"1465919762492227584",isIcpc:"1",rankName:""},step2_form:{primaryscore:"",advancescore:""}}},methods:{toCreate:function(){var t=this,e={session:this.step1_form.sessionid,problemid:this.step1_form.problemid},a=0;r.a.post("config/info/update",e).then(function(t){200==t.status&&a++}).catch(function(e){t.$message({message:"有错误，不能创建",type:"error"})}),r.a.post("result/completion",this.step1_form).then(function(t){200==t.status&&a++}).catch(function(e){t.$message({message:"有错误，不能创建",type:"error"})}),2==a&&this.active++},toSetScore:function(){var t=this;r.a.post("config/stands/update",this.step2_form).then(function(e){200==e.status&&t.active++})},startList:function(){this.active++},startRankDetail:function(){var t=this;r.a.get("config/job/icpc/rank").then(function(e){200==e.status&&t.active++})},createExcel:function(){!function(t){var e=this;r()({method:t.method,url:t.url+(t.params?"?"+t.params:""),responseType:"blob"}).then(function(e){var a=document.createElement("a"),o=new Blob([e.data],{type:"application/vnd.ms-excel"});a.style.display="none",a.href=URL.createObjectURL(o),a.download=t.fileName,document.body.appendChild(a),a.click(),document.body.removeChild(a)}).catch(function(t){e.$Notice.error({title:"错误",desc:"网络连接错误"}),console.log(t)})}({method:"get",url:"excel/team/export",fileName:"比赛数据"})}}},i={render:function(){var t=this,e=t.$createElement,a=t._self._c||e;return a("div",{staticClass:"container"},[a("el-card",{staticClass:"bg"},[a("h1",{staticStyle:{color:"white","text-align":"center"}},[t._v("管理比赛页面")]),t._v(" "),a("el-steps",{attrs:{direction:"vertical",active:0}},[a("el-step",{attrs:{title:"初始化阶段"}},[a("template",{slot:"description"},[a("el-form",{ref:"step1_form",staticStyle:{"margin-top":"20px"},attrs:{model:t.step1_form,"label-width":"100px"}},[a("el-form-item",{attrs:{label:"比赛名称"}},[a("el-input",{model:{value:t.step1_form.rankName,callback:function(e){t.$set(t.step1_form,"rankName",e)},expression:"step1_form.rankName"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"session"}},[a("el-input",{model:{value:t.step1_form.sessionid,callback:function(e){t.$set(t.step1_form,"sessionid",e)},expression:"step1_form.sessionid"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"题目集id"}},[a("el-input",{model:{value:t.step1_form.problemid,callback:function(e){t.$set(t.step1_form,"problemid",e)},expression:"step1_form.problemid"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"赛制选择"}},[a("el-radio",{attrs:{label:"0"},model:{value:t.step1_form.isIcpc,callback:function(e){t.$set(t.step1_form,"isIcpc",e)},expression:"step1_form.isIcpc"}},[t._v("天梯赛")]),t._v(" "),a("el-radio",{attrs:{label:"1"},model:{value:t.step1_form.isIcpc,callback:function(e){t.$set(t.step1_form,"isIcpc",e)},expression:"step1_form.isIcpc"}},[t._v("Icpc")])],1),t._v(" "),a("el-form-item",{attrs:{label:""}},[a("el-button",{attrs:{type:"primary"},on:{click:t.toCreate}},[t._v("立即创建")])],1)],1)],1)],2),t._v(" "),a("el-step",{attrs:{title:"爬取阶段"}},[a("template",{slot:"description"},[a("el-form",{ref:"step2_form",staticStyle:{"margin-top":"20px"},attrs:{model:t.step2_form,"label-width":"100px"}},[a("el-form-item",{attrs:{label:"设置基础分"}},[a("el-input",{model:{value:t.step2_form.primaryscore,callback:function(e){t.$set(t.step2_form,"primaryscore",e)},expression:"step2_form.primaryscore"}})],1),t._v(" "),a("el-form-item",{attrs:{label:"设置进阶分"}},[a("el-input",{model:{value:t.step2_form.advancescore,callback:function(e){t.$set(t.step2_form,"advancescore",e)},expression:"step2_form.advancescore"}})],1),t._v(" "),a("el-form-item",{attrs:{label:""}},[a("el-button",{attrs:{type:"primary"},on:{click:t.toSetScore}},[t._v("立即设置")])],1)],1)],1)],2),t._v(" "),a("el-step",{attrs:{title:"开启模块"}},[a("template",{slot:"description"},[a("el-form",{staticStyle:{"margin-top":"20px"},attrs:{"label-width":"100px"}},[a("el-form-item",{attrs:{label:""}},[a("el-button",{attrs:{type:"primary"},on:{click:t.startList}},[t._v("开启爬取题目集信息")])],1),t._v(" "),a("el-form-item",{attrs:{label:""}},[a("el-button",{attrs:{type:"primary"},on:{click:t.startRankDetail}},[t._v("开启爬取排行耪信息")])],1)],1)],1)],2),t._v(" "),a("el-step",{attrs:{title:"导出模块"}},[a("template",{slot:"description"},[a("el-form",{staticStyle:{"margin-top":"20px"},attrs:{"label-width":"100px"}},[a("el-form-item",{attrs:{label:""}},[a("el-button",{attrs:{type:"primary"},on:{click:t.createExcel}},[t._v("导出比赛成绩至数据库")])],1)],1)],1)],2)],1)],1),t._v(" "),a("div",{staticClass:"bg1"})],1)},staticRenderFns:[]};var l=a("VU/8")(s,i,!1,function(t){a("52oE")},"data-v-d0ed7754",null);e.default=l.exports},"52oE":function(t,e,a){var o=a("gd2B");"string"==typeof o&&(o=[[t.i,o,""]]),o.locals&&(t.exports=o.locals);a("rjj0")("6019fd52",o,!0,{})},gd2B:function(t,e,a){var o=a("kxFB");(t.exports=a("FZ+f")(!1)).push([t.i,".container[data-v-d0ed7754]{background:url("+o(a("plV2"))+') 0/cover fixed;min-height:100vh;position:absolute;top:0;left:0;right:0;bottom:0;overflow-y:scroll}.bg[data-v-d0ed7754]{margin-top:50px}.bg1[data-v-d0ed7754],.bg[data-v-d0ed7754]{width:800px;word-break:break-all;z-index:10;position:absolute;left:50%;margin-left:-400px;margin-bottom:100px}.bg1[data-v-d0ed7754]{height:10px;margin-top:950px}.bg[data-v-d0ed7754]:before{content:"";background:url('+o(a("plV2"))+") 50%/cover no-repeat fixed;-webkit-filter:blur(20px);filter:blur(20px);position:absolute;top:0;bottom:0;left:0;right:0;z-index:-1;margin:-30px}.el-form-item__label[data-v-d0ed7754]{color:#000}",""])}});
//# sourceMappingURL=4.9a16d9c6c1033d99f19f.js.map