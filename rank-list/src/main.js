// The Vue build version to load with the `import` command
// (runtime-only or standalone) has been set in webpack.base.conf with an alias.
import Vue from 'vue'
import App from './App'
import router from './router'
import ElementUI from 'element-ui'
import 'element-ui/lib/theme-chalk/index.css'
import store from './store/store'
import axios from 'axios'
Vue.prototype.$axios=axios
// axios.defaults.baseURL='http://202.196.1.132:8080/api/'
axios.defaults.baseURL='http://101.132.72.74:8080/api/'
// axios.defaults.baseURL='http://nothing.utools.club/api/'
axios.defaults.headers.post['Content-Type']='application/json'
// axios.defaults.timeout=6000//axios超过6秒即为超时
Vue.config.productionTip = false
Vue.use(ElementUI)
import 'babel-polyfill'

/* eslint-disable no-new */
new Vue({
  el: '#app',
  router,
  store,
  components: { App },
  template: '<App/>',
  render: h => h(App)
})
