import Vue from 'vue'
import Router from 'vue-router'
Vue.use(Router)

export default new Router({
  routes: [
    // {
    //   path: '/IcpcRank',
    //   component:IcpcRank
    // },
    {
      path: '/',
      component:()=>import('../views/layout/index.vue'),
      redirect:'/TeamRank',
      children:[
        // {
        //   path:'*',
        //   redirect:'/TeamRank'
        // },
        {
          path: 'SingleRank',
          component:()=>import('../views/singleRank/index.vue')
        },
        {
          path: 'TeamRank',
          component:()=>import('../views/teamRank/index.vue')
        },
      ]
    },
    {
      path: '/Manage',
      component:()=>import('../views/manage/index.vue')
    },
  ]
})
