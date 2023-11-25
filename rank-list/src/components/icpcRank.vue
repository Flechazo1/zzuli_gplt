<template>
    <div class="icpcRank">
                <div class="icpcRank-header">
                    <div class="icpcRank-header-rank icpcRank-header-item">
                        #
                    </div>
                    <div class="icpcRank-header-name icpcRank-header-item">
                        Name
                    </div>
                    <div class="icpcRank-header-ac icpcRank-header-item">
                        Ac
                    </div>
                    <div class="icpcRank-header-time icpcRank-header-item">
                        Time
                    </div>
                    <div class="icpcRank-header-subject-container icpcRank-header-item">
                        <div  v-for="(item,index) in problemsList" :data="index"  :key="index" class="icpcRank-header-subject-item">
                            {{
                                item.label
                            }}
                        </div>  
                    </div>
                </div>
                <div v-for="(item,index) in rankListSlice" :key="index" class="icpcRank-item">
                    <div class="icpcRank-rank-rank icpcRank-rank-item">
                        {{item.rank}}
                    </div>
                    <div class="icpcRank-rank-name icpcRank-rank-item">
                        {{item.user.name.slice(item.user.name.indexOf("_")+1)}} 
                          <el-popover
                            v-for="(item2,index2) in firstAc"
                            v-show="item2.name == item.user.name"
                            :key="index2"
                            popper-class="medal-popover"
                            placement="top"
                            trigger="hover"
                            :content="index2 + '  Fitst Ac'">
                            <!-- <el-button slot="reference">hover 激活</el-button> -->
                            <img slot="reference" class="medalIcon" src="../assets/images/medal.png" alt="">
                        </el-popover>

                    </div>
                    <div class="icpcRank-rank-ac icpcRank-rank-item">
                        {{item.acCount}}
                    </div>
                    <div class="icpcRank-rank-time icpcRank-rank-item">
                        {{item.solvingTime}}
                    </div>
                    <div class="icpcRank-rank-subject-container icpcRank-rank-item">
                        <div  v-for="(item2,index2) in getRankSubject(index)"  :key="index2" class="icpcRank-rank-subject-item">
                            <!-- 该题未作答 -->
                            <span class="status" v-if="Object.keys(item2).length == 0">
                                /
                            </span>
                            <!-- 该题作答并通过 -->
                            <!-- <div  v-if="item2.score > 0">
                                <span class="acStatus" >{{item2.submitCountSnapshot}}</span>{{"/"+item2.acceptTime}}
                            </div> -->
                            <span class="acStatus" v-if="item2.score > 0">
                                {{item2.submitCountSnapshot+"/"+item2.acceptTime}}
                            </span>
                            <!-- 该题作答未通过 -->
                            <span class="noAcStatus" v-if="(item2.score == 0) && (Object.keys(item2).length != 0)">
                                {{item2.submitCountSnapshot}}
                            </span>
                        </div>  
                    </div>
                </div>
                <el-pagination
                    class="icpcRankPage"
                    background
                    :page-size="pageSize"
                    @current-change="currentChange"
                    layout="prev, pager, next"
                    :total="rankList.total">
                </el-pagination>
    </div>
</template>

<script>
import store from '../store/store'
import {mapMutations,mapState,mapActions} from 'vuex'
import WebsocketClass from '../util/websocket'
import {
    getRankHeader,
    getRank,
    getFirstAc,
} from '../api/index'
export default {
    name:"icpcRank",
    store,
    data(){
        return{
            problemsList:[
            ],
            rankSubject:[],
            rankListSlice:[],
            firstAc:{},
            pageSize:20,
            visible: false,//element-ui的弹出框组件
            current:1,
            init:false
        }
    },
    watch:{
        rankList(){
            //vuex中的rankList有值之后，且标志state的init为false表示仍未第一页渲染，赋予rankListSlice初值(就是第一页)
            //若不进行init判断，后续websocket接受的数据更新后也会触发该函数
            if(!this.init){
                this.init = true
                this.rankListSlice = this.rankList.icpcRanks.slice(0,this.pageSize)
            }
        }
    },
    methods:{
        getRankSubject(index){
            let currentIndex = index + (this.current-1) * this.pageSize
            let myIndex = 1
            let subjectItem = this.rankList.icpcRanks[currentIndex].problemScores
            let subjectItemKeys = Object.keys(subjectItem)
            let problemsKeys = []
            let subjectItemArr = []
            let problemsNameFormat = this.problemsList[0].label
            let cutIndex = this.problemsList[0].label.indexOf("-")
            let problemsNamePrefix = problemsNameFormat.slice(0,cutIndex)
            this.problemsList.map((item,index) => {
                problemsKeys.push(item.label)
            })
            //遍历题目数量次数
            this.problemsList.map((item,index) => {
                if(!subjectItem[problemsKeys[index]]){
                    subjectItem[problemsKeys[index]] = {}
                }
            })
            //将subjectItem转为排序好的数组
            for(let item in subjectItem){
                subjectItemArr.push(subjectItem[problemsNamePrefix+"-"+myIndex++])
            }
                return subjectItemArr
        },
        currentChange(current){
            this.current = current
            let left = (current-1) * this.pageSize
            let right = left + 20
            this.rankListSlice = this.rankList.icpcRanks.slice(left,right)
            document.body.scrollTop = 0;
            document.documentElement.scrollTop = 0;
        }
    },
    computed:{
        ...mapState(['rankList']),
        ...mapActions(['getRankAction',"setRankMutation"])
    },
    mounted(){
        //获取rank-header接口，更新problemsList
        getRankHeader()
            .then(val => {
                if(val.data.code == 200 || val.data.code == 304){
                    this.problemsList = val.data.data.problems_set
                }
            })
            .catch(err => {
                this.$message({
                    message: '获取题目详情失败，请重试',
                    type: 'warning'
                });
            })
        //获取FirstAc接口
        getFirstAc()
            .then(val => {
                if(val.data.code == 200 || val.data.code == 304){
                    this.firstAc = val.data.data.firstAc
                }
            })
            .catch(err => {
                this.$message({
                    message: '获取勋章失败，请重试',
                    type: 'warning'
                });
            })
        //获取rank接口，更新rankList
        //前端分页，在vuex的actions中定义getRank做全局状态存储，在当前页面进行渲染
        this.getRankAction //为啥这就执行函数了???有点懵逼
        // getRank()
        //     .then(val => {
        //         let {ranking} = val.data.data
        //         this.rankList = ranking
        //     })
        //     .catch(err => {
        //         this.$message({
        //             message: '获取详情失败，请重试',
        //             type: 'warning'
        //         });
        //     })

        let myFun = (data) => {
            if(JSON.parse(data).code === 212){
                            console.log("----------------------")
            console.log(JSON.parse(data).data)
                //更新vuex中state
                this.$store.commit("setRankMutation",JSON.parse(data).data.ranking)
                // this.setRankMutation(JSON.parse(data).data.ranking)
            }
        }
        myFun.bind(this)
        let socket = new WebsocketClass("ws://nothing.cn1.utools.club/api/link",myFun,"helloSocket")
        socket.connect("socket connecting....")
        socket.heartCheck()
    }
}
</script>

<style>

    .icpcRank{
        width: 90vw;
        padding: 20px;
        margin: 0 auto;
        overflow: hidden;
        border-radius: 20px;
        position: relative;
        z-index: 1; 
        color: #f0f0f0;
    }
    .icpcRank::before{
        z-index: -1;
        content: "";
        display: block;
        position: absolute;
        filter: blur(20px);
        top: 0;
        left: 0;
        bottom: 0;
        right: 0;
        /* background-size: 100% 100%; */
        background-image: url("../assets/images/313353.jpg");
        background-color: white;
    }
    .icpcRank-header{
        height: 60px;
        display: flex;
        /* border-radius: 5px; */
        /* border-top: 1px solid #1a1a2e; */
    }
    .icpcRank-header-rank,
    .icpcRank-header-ac,
    .icpcRank-header-time,
    .icpcRank-rank-rank,
    .icpcRank-rank-ac,
    .icpcRank-rank-time{
        flex-basis: 5%;
        line-height: 60px;
        text-align: center;
    }
    .icpcRank-header-rank,
    .icpcRank-rank-rank{
         color: #FFD700;
         font-weight: bold;
     }
    .icpcRank-header-name,
    .icpcRank-rank-name{
        flex-basis: 40%;
        /* flex-grow: 1; */
        line-height: 60px;
        text-align: center;
    }
    .icpcRank-rank-name{
        overflow: hidden;
        text-overflow: ellipsis;
        white-space: nowrap;
        vertical-align: middle;
    }
    .icpcRank-header-subject-container,
    .icpcRank-rank-subject-container{
        flex-grow: 2;
        display: flex;
        justify-content: space-between;
    }
    .icpcRank-header-subject-item{
        line-height: 60px;
    }
    .icpcRank-header-subject-item,
    .icpcRank-rank-subject-item{
        height: 100%;
        text-align: center;
        /* font-size: 22px; */
        line-height: 60px;
        width: 50px;
        position: relative;
    }
    .medalIcon{
        vertical-align: middle;
        width: 20px;
    }
    .status{
        line-height: 60px;
    }
    .acStatusDiv{
        width: 100%;
        position: absolute;
        left: 50%;
        top: 50%;
        transform: translate3d(-50%,-50%,0)
    }
    .acStatus{
        /* 
        width: 15px;
        height: 15px;
        position: absolute;
        text-align: center;
        line-height: 15px;
        top: 50%; */
        /* left: 50%; */
        /* transform: translate3d(-50%,-50%,0); */
        /* transform: translateY(-50%); */
        /* width: 22px;
        height: 22px; */
        background-image: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
        display: block;
        width: 45px;
        height: 45px;
        position: absolute;
        top: 50%; 
        left: 50%;
        transform: translate3d(-50%,-50%,0);
        text-align: center;
        box-sizing: border-box;
        border-radius: 50%;
        line-height: 45px;
    }
    .acStatusRight{
        /* margin-left: 10px; */
    }
    .noAcStatus{
        display: inline-block;
        width: 22px;
        height: 22px;
        position: absolute;
        text-align: center;
        line-height: 22px;
        top: 50%;
        left: 50%;
        transform: translate3d(-50%,-50%,0);
        border-radius: 50%;
        background-color: #ec524b;
    }
    .icpcRank-header-item{
        height: 100%;
    }
    .icpcRank-item{
        height: 60px;
        display: flex;
        border-radius: 5px;
        border-top: 1px solid #1a1a2e;
    }
    .icpcRankPage{
        margin:10px auto;
        text-align: center;
    }
    /* 覆盖element-ui */
    .medal-popover{
        padding: 3px !important;
        min-width: 30px !important;
        text-align: center;
        color:#f0f0f0;
        border-color: gray;
        background-color: gray;
    }
    .medal-popover .popper__arrow::after{
         border-color: transparent !important;
         border-top-color: gray !important;
    }
</style>