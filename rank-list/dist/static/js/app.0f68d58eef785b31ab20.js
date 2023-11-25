webpackJsonp([6],{0:function(e,t,n){n("j1ja"),e.exports=n("NHnr")},"75l9":function(e,t){e.exports={name:"axios",version:"0.21.4",description:"Promise based HTTP client for the browser and node.js",main:"index.js",scripts:{test:"grunt test",start:"node ./sandbox/server.js",build:"NODE_ENV=production grunt build",preversion:"npm test",version:"npm run build && grunt version && git add -A dist && git add CHANGELOG.md bower.json package.json",postversion:"git push && git push --tags",examples:"node ./examples/server.js",coveralls:"cat coverage/lcov.info | ./node_modules/coveralls/bin/coveralls.js",fix:"eslint --fix lib/**/*.js"},repository:{type:"git",url:"https://github.com/axios/axios.git"},keywords:["xhr","http","ajax","promise","node"],author:"Matt Zabriskie",license:"MIT",bugs:{url:"https://github.com/axios/axios/issues"},homepage:"https://axios-http.com",devDependencies:{coveralls:"^3.0.0","es6-promise":"^4.2.4",grunt:"^1.3.0","grunt-banner":"^0.6.0","grunt-cli":"^1.2.0","grunt-contrib-clean":"^1.1.0","grunt-contrib-watch":"^1.0.0","grunt-eslint":"^23.0.0","grunt-karma":"^4.0.0","grunt-mocha-test":"^0.13.3","grunt-ts":"^6.0.0-beta.19","grunt-webpack":"^4.0.2","istanbul-instrumenter-loader":"^1.0.0","jasmine-core":"^2.4.1",karma:"^6.3.2","karma-chrome-launcher":"^3.1.0","karma-firefox-launcher":"^2.1.0","karma-jasmine":"^1.1.1","karma-jasmine-ajax":"^0.1.13","karma-safari-launcher":"^1.0.0","karma-sauce-launcher":"^4.3.6","karma-sinon":"^1.0.5","karma-sourcemap-loader":"^0.3.8","karma-webpack":"^4.0.2","load-grunt-tasks":"^3.5.2",minimist:"^1.2.0",mocha:"^8.2.1",sinon:"^4.5.0","terser-webpack-plugin":"^4.2.3",typescript:"^4.0.5","url-search-params":"^0.10.0",webpack:"^4.44.2","webpack-dev-server":"^3.11.0"},browser:{"./lib/adapters/http.js":"./lib/adapters/xhr.js"},jsdelivr:"dist/axios.min.js",unpkg:"dist/axios.min.js",typings:"./index.d.ts",dependencies:{"follow-redirects":"^1.14.0"},bundlesize:[{path:"./dist/axios.min.js",threshold:"5kB"}]}},"7VFK":function(e,t,n){"use strict";var a={render:function(){var e=this.$createElement,t=this._self._c||e;return t("div",{attrs:{id:"app"}},[t("router-view")],1)},staticRenderFns:[]};t.a=a},M93x:function(e,t,n){"use strict";var a=n("xJD8"),r=n.n(a),o=n("7VFK");var s=function(e){n("je0D")},i=n("VU/8")(r.a,o.a,!1,s,null,null);t.default=i.exports},NHnr:function(e,t,n){"use strict";Object.defineProperty(t,"__esModule",{value:!0});var a=n("7+uW"),r=n("M93x"),o=n("/ocq");a.default.use(o.a);var s=new o.a({routes:[{path:"/",component:function(){return Promise.all([n.e(0),n.e(1)]).then(n.bind(null,"6f/g"))},redirect:"/TeamRank",children:[{path:"SingleRank",component:function(){return Promise.all([n.e(0),n.e(2)]).then(n.bind(null,"s65b"))}},{path:"TeamRank",component:function(){return Promise.all([n.e(0),n.e(3)]).then(n.bind(null,"v2dx"))}}]},{path:"/Manage",component:function(){return n.e(4).then(n.bind(null,"/6cs"))}}]}),i=n("zL8q"),c=n.n(i),u=(n("tvR6"),n("NYxO")),l=n("mtWM"),p=n.n(l),d=n("424j"),m=n("fZjL"),f=n.n(m),g=function(e){var t=arguments.length>1&&void 0!==arguments[1]?arguments[1]:"GET",n=arguments.length>2&&void 0!==arguments[2]?arguments[2]:{},a="http://101.132.72.74:8080/api";if("GET"==t){var r="?";return f()(n).map(function(e){r+=e+"="+n[e]+"&"}),e+=r=r.slice(0,r.length-1),p.a.get(a+e)}if("POST"==t)return p.a.post(a+e,n)};a.default.use(u.a);var h=new u.a.Store({plugins:[Object(d.a)({storage:window.sessionStorage,reducer:function(e){return{allScore:e.allScore}}})],state:{allScore:[],rankList:[]},mutations:{getAllScore:function(e,t){e.allScore=t},setRankMutation:function(e,t){e.rankList=t}},actions:{getAllScore:function(e){p.a.get("info/problems").then(function(t){for(var n=t.data.data.problems_set,a=0,r=0,o=0,s=0;s<n.length;s++)s<4?a+=n[s].score:s<8?r+=n[s].score:s<12&&(o+=n[s].score);var i=[a,r,o];e.commit("getAllScore",i)}).catch(function(e){console.log(e)}),console.log("怎么回事")},getRankAction:function(e){g("/icpc/rank").then(function(t){var n=t.data.data.ranking;e.commit("setRankMutation",n)}).catch(function(e){console.log("获取rankList失败了")})}}});n("j1ja");a.default.prototype.$axios=p.a,p.a.defaults.baseURL="http://101.132.72.74:8080/api/",p.a.defaults.headers.post["Content-Type"]="application/json",a.default.config.productionTip=!1,a.default.use(c.a),new a.default({el:"#app",router:s,store:h,components:{App:r.default},template:"<App/>",render:function(e){return e(r.default)}})},VDCw:function(e,t,n){var a=n("kxFB");(e.exports=n("FZ+f")(!1)).push([e.i,"#app{background:url("+a(n("plV2"))+") 0/cover fixed;min-height:100vh}",""])},je0D:function(e,t,n){var a=n("VDCw");"string"==typeof a&&(a=[[e.i,a,""]]),a.locals&&(e.exports=a.locals);n("rjj0")("0ba041ce",a,!0,{})},plV2:function(e,t,n){e.exports=n.p+"static/img/313353.6a457fd.jpg"},tvR6:function(e,t){},xJD8:function(e,t){}},[0]);
//# sourceMappingURL=app.0f68d58eef785b31ab20.js.map