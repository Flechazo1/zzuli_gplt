import Vue from 'vue'
import Vuex from 'vuex'
import axios from 'axios'
import createPersistedState from 'vuex-persistedstate'

import {getRank,} from '../api/index'

Vue.use(Vuex)
let store=new Vuex.Store({
  plugins:[createPersistedState({
    storage:window.sessionStorage,
    reducer(val){
      return{
        allScore:val.allScore
      }
    }
  })],
  state:{
    allScore:[],
    rankList:[]
  },
  mutations:{
    getAllScore(state,res){
      state.allScore=res
    },
    setRankMutation(state,data){
      state.rankList = data
    },
  },
  actions:{
    //学弟写的
    getAllScore(context){
      const url='info/problems'
      axios.get(url)
        .then(response=>{
          let problems_set=response.data.data.problems_set
          let primaryScore=0
          let advanceScore=0
          let sensiorScore=0
          for(let i=0;i<problems_set.length;i++){
            if(i<4){
              primaryScore=primaryScore+problems_set[i].score
            }else if(i<8){
              advanceScore=advanceScore+problems_set[i].score
            } else if(i<12){
              sensiorScore=sensiorScore+problems_set[i].score
            }
          }
          let allScore=[primaryScore,advanceScore,sensiorScore]
          context.commit('getAllScore',allScore)
        })
        .catch(error=>{
          console.log(error)
        })
      console.log('怎么回事')
    },
    //获取rankList,被icpcRank组件使用
    getRankAction(context){
      getRank()
          .then(val => {
              let {ranking} = val.data.data
              context.commit("setRankMutation",ranking)
          })
          .catch(err => {
              // this.$message({
              //     message: '获取详情失败，请重试',
              //     type: 'warning'
              // });
              console.log("获取rankList失败了")
          })
    }
  },
})
export default store
