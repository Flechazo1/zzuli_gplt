<template>
  <div>
    <div class="shelter" @click="closeShlter" v-show="shelter"></div>
    <div class="table">
      <el-row>
        <span class="title">个人排名</span>
        <span style="color:wheat;position: absolute;right: 0;top: 20px">该榜单每30秒刷新一次,请稍等。</span>
      </el-row>
      <el-row class="pagination">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          size="5"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="total"
          style="color: white!important;">
        </el-pagination>
      </el-row>
      <div v-loading="loading1" :style="{height:(height1)}"
           element-loading-text="拼命加载中"
           element-loading-spinner="el-icon-loading"
           element-loading-background="rgba(57, 72, 145, 0)">
        <el-alert
          v-show="isShowAlert"
          title="暂时没有个人得分信息，请稍后刷新"
          type="warning"
          center
          show-icon
          :closable="false">
        </el-alert>
        <el-row class="single" @click.native="showDetail(item)" v-for="(item,index) in items" :key="index">
          <div class="badge">
            <span><i>{{item.rank}}</i></span>
          </div>
          <div class="progress">
            <div class="head">
              <img src="../../assets/images/zzuli.png">
            </div>
            <div class="progress1">
                <span style="color:white;display: inline-block;white-space: nowrap">
                  {{item.user.name?`${item.user.name}`:'---'}}——{{item.user.theClass?`${item.user.theClass}`:'---'}}&nbsp;
                <el-tooltip class="item" effect="dark" content="第一阶段等级" placement="top">
                  <svg v-if="item.primaryScore>0" class="icon" aria-hidden="true" color="#6FE0FF">
                    <use v-if="item.primaryScore===allScore[0]" xlink:href="#icon-dengji-A"></use>
                    <use v-else-if="item.primaryScore>=standard[0]" xlink:href="#icon-dengji-B"></use>
                    <use v-else-if="item.primaryScore<standard[0]" xlink:href="#icon-dengji-C"></use>
                  </svg>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="第二阶段等级" placement="top">
                  <svg v-if="item.advanceScore>0" class="icon" aria-hidden="true" color="#DCB672">
                    <use v-if="item.advanceScore===allScore[1]" xlink:href="#icon-dengji-A"></use>
                    <use v-else-if="item.advanceScore>=standard[1]" xlink:href="#icon-dengji-B"></use>
                    <use v-else-if="item.advanceScore<standard[1]" xlink:href="#icon-dengji-C"></use>
                  </svg>
                </el-tooltip>
                <el-tooltip class="item" effect="dark" content="第三阶段等级" placement="top">
                  <svg v-if="item.seniorScore>0" class="icon" aria-hidden="true" color="#DC7272">
                    <use v-if="item.seniorScore===allScore[2]" xlink:href="#icon-dengji-A"></use>
                    <use v-else-if="item.seniorScore>=allScore[2]/2" xlink:href="#icon-dengji-B"></use>
                    <use v-else-if="item.seniorScore<allScore[2]/2" xlink:href="#icon-dengji-C"></use>
                  </svg>
                </el-tooltip>
                </span>
              <el-tooltip class="item" effect="dark" :content="item.primaryScore+'/'+allScore[0]" placement="bottom">
                <img src="../../assets/images/progress_blue.png" :style="{width:(item.primaryScore/allScore[0])*100+'%'}"
                     height="20px">
              </el-tooltip>
              <img v-show="item.primaryScore>=0 && (item.primaryScore<standard[0]||item.advanceScore===0)"
                   src="../../assets/images/circle_blue.png" class="circleStyle"
                   :style="{left:(item.primaryScore/allScore[0])*100+'%'}">
            </div>
            <div class="point_sm">
              <!--            <img width="45px" height="50px" src="../assets/images/two.png">-->
            </div>

            <div class="progress1">
              <el-tooltip class="item" effect="dark" :content="item.advanceScore+'/'+allScore[1]" placement="bottom">
                <img v-if="item.primaryScore>=standard[0]" src="../../assets/images/progress_orange.png"
                     :style="{width:(item.advanceScore/allScore[1])*100+'%'}" height="20px">
                <img v-else src="../../assets/images/progress_gray.png"
                     :style="{width:(item.advanceScore/allScore[1])*100+'%'}" height="20px">
              </el-tooltip>
              <img
                v-show="(item.advanceScore>0||item.primaryScore===allScore[0]) && item.primaryScore>=standard[0]&&(item.seniorScore===0||item.advanceScore<standard[1])"
                src="../../assets/images/circle_yellow.png" class="circleStyle"
                :style="{left:(item.advanceScore/allScore[1])*100+'%'}">
            </div>

            <div class="point_sm"></div>

            <div class="progress2">
              <el-tooltip class="item" effect="dark" :content="item.seniorScore+'/'+allScore[2]" placement="bottom">
                <img v-if="item.advanceScore>=standard[1] && item.primaryScore>=standard[0]"
                     src="../../assets/images/progress_red.png" :style="{width:(item.seniorScore/allScore[2])*100+'%'}"
                     height="20px">
                <img v-else src="../../assets/images/progress_gray.png"
                     :style="{width:(item.seniorScore/allScore[2])*100+'%'}" height="20px">
              </el-tooltip>
              <img v-show="item.primaryScore>=standard[0] && item.advanceScore>=standard[1] && item.seniorScore>0"
                   src="../../assets/images/circle_red.png" class="circleStyle"
                   :style="{left:(item.seniorScore/allScore[2])*100+'%'}">
            </div>

            <div class="point_big"></div>
          </div>
          <div class="score">
            <span><i>{{item.totalScore}}</i></span>
          </div>
        </el-row>
      </div>
      <el-row class="pagination">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total, prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </el-row>
      <div class="detail" v-show="isShow">
        <el-card class="box-card">
          <el-row style="display: flex;justify-content: flex-end"><i class="fa fa-times" aria-hidden="true"
                                                                     @click="closeShlter"></i></el-row>
          <el-row class="title_detail">
            <span><i>{{scoreDetails.userName}}</i></span>
            <span><i>&nbsp;——{{scoreDetails.theClass}}</i></span>
          </el-row>
          <el-row>
            <table>
              <tr class="row1">
                <td></td>
                <td></td>
                <td :colspan="labelCount[0].length" style="text-align: center;font-weight: bold;color: #7ACCEC">基础级</td>
                <td :colspan="labelCount[1].length" style="text-align: center;font-weight: bold;color:#FFD700;">进阶级</td>
                <td :colspan="labelCount[2].length" style="text-align: center;font-weight: bold;color:#F04F4F;">登顶级</td>
              </tr>
              <tr class="row2">
                <td></td>
                <td></td>
                <td v-for="(item1) in scoreDetails.primaryScoreItemsLabel" style="color: #7ACCEC">{{item1}}</td>
                <td v-for="(item2) in scoreDetails.advanceScoreItemsLabel" style="color: #FFD700">{{item2}}</td>
                <td v-for="(item3) in scoreDetails.seniorScoreItemsLabel" style="color: #F04F4F">{{item3}}</td>
                <td style="color:#FFD700;"><b>总分</b></td>
              </tr>
              <tr class="row3">
                <td>
                  <el-avatar size="small"
                             src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                </td>
                <td><span style="color:white;">{{scoreDetails.userName?`${scoreDetails.userName}`:'---'}}</span></td>
                <td v-for="(item4) in scoreDetails.primaryScoreItems" style="color: #7ACCEC">{{item4.score}}</td>
                <td v-for="(item5) in scoreDetails.advanceScoreItems"
                    :class="[scoreDetails.primaryScore<standard[0]?xiahuaxianClass:'']" style="color:#FFD700;">
                  {{item5.score}}
                </td>
                <td v-for="(item6) in scoreDetails.seniorScoreItems"
                    :class="[scoreDetails.advanceScore<standard[1]?xiahuaxianClass:'']" style="color:#F04F4F;">
                  {{item6.score}}
                </td>
                <td style="color:#FFD700;">{{scoreDetails.sumScore}}</td>
              </tr>
            </table>
          </el-row>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
    import '../../assets/iconfont'
    import $ from 'jquery'
    import axios from 'axios'
    import userRanking from '../../assets/userRanking'

    export default {
        name: "SingleRank",
        data() {
            return {
                xiahuaxianClass: 'xiahuaxian',
                isShowAlert: false,
                height1: '328px',
                loading1: false,
                pageSize: 20,
                total: 100,
                currentPage: 1,
                shelter: false,
                isShow: false,
                items: [],
                scoreDetails: {},
                allScore: [],
                standard: [20, 20],
                loading: false,
                labelCount: [[], [], []]
            }
        },
        created() {
            const height = document.body.clientHeight - 260
            this.height = height + 'px'
            this.getAllScore()
            this.getStandard()
            this.getList()
            const timer = setInterval(() => {
                setTimeout(this.getList(), 0)
            }, 180000)
            this.$once('hook:beforeDestroy', () => {
                clearInterval(timer)
            })
        },
        mounted() {

        },
        methods: {
            handleCurrentChange(val) {
                this.getList()
            },
            //显示个人得分具体情况
            showDetail(item) {
                let problems_set = item.problemScores
                let primaryScoreItems = [], primaryScoreItemsLabel = []
                let advanceScoreItems = [], advanceScoreItemsLabel = []
                let seniorScoreItems = [], seniorScoreItemsLabel = []
                var reg1 = new RegExp("1-\\w*")
                var reg2 = new RegExp("2-\\w*")
                var reg3 = new RegExp("3-\\w*")
                for (let key in problems_set) {
                    if (reg1.test(key)) {
                        primaryScoreItemsLabel.push(key)
                        primaryScoreItems.push(problems_set[key])
                    } else if (reg2.test(key)) {
                        advanceScoreItemsLabel.push(key)
                        advanceScoreItems.push(problems_set[key])
                    } else if (reg3.test(key)) {
                        seniorScoreItemsLabel.push(key)
                        seniorScoreItems.push(problems_set[key])
                    }
                }
                let labelCount = [primaryScoreItemsLabel, advanceScoreItemsLabel, seniorScoreItemsLabel]
                this.labelCount = labelCount
                let scoreItems = {
                    'primaryScoreItemsLabel': primaryScoreItemsLabel,
                    'advanceScoreItemsLabel': advanceScoreItemsLabel,
                    'seniorScoreItemsLabel': seniorScoreItemsLabel,
                    'primaryScoreItems': primaryScoreItems,
                    'advanceScoreItems': advanceScoreItems,
                    'seniorScoreItems': seniorScoreItems,
                    'sumScore': item.totalScore,
                    'userName': item.user.name,
                    'theClass': item.user.theClass
                }
                this.scoreDetails = scoreItems
                this.isShow = !this.isShow
                this.shelter = !this.shelter
                document.body.parentNode.style.overflowY = "hidden";
                $("body").parent().css("overflow-y", "hidden");
            },
            //关闭遮罩层
            closeShlter() {
                this.isShow = !this.isShow
                this.shelter = !this.shelter
                document.body.parentNode.style.overflowY = "auto";
                $("body").parent().css("overflow-y", "auto");
            },
            // 获取列表
            getList() {
                this.loading1 = !this.loading1
                const url = `user/getUserScore/${this.currentPage}`
                axios.get(url)
                    .then(response => {
                        let items = response.data.data.ranking
                        if (items === null) {
                            this.isShowAlert = true
                            this.loading1 = !this.loading1
                            return
                        }
                        this.isShowAlert = false
                        this.items = response.data.data.ranking.ranks
                        this.height1 = `${this.items.length * 120}px`
                        this.total = response.data.data.ranking.total
                        this.loading1 = !this.loading1
                    })
                    .catch(error => {
                        this.$message({
                            showClose: true,
                            message: '个人信息获取失败',
                            type: 'error'
                        });
                        console.log(error)
                        this.loading1 = !this.loading1
                    })
            },
            //获取比较标准分数
            getStandard() {
                const url = 'info/stands'
                axios.get(url)
                    .then(response => {
                        let standard = [response.data.data.stands.primaryscore, response.data.data.stands.advancescore]
                        this.standard = standard
                    })
                    .catch(error => {
                        this.$message({
                            showClose: true,
                            message: '比较标准获取失败',
                            type: 'error'
                        });
                        console.log(error)
                    })
            },
            getAllScore() {
                const url = 'info/problems'
                axios.get(url)
                    .then(response => {
                        let problems_set = response.data.data.problems_set
                        let primaryScore = 0, flag1 = []
                        let advanceScore = 0, flag2 = []
                        let sensiorScore = 0, flag3 = []
                        var reg1 = new RegExp("1-\\w*")
                        var reg2 = new RegExp("2-\\w*")
                        var reg3 = new RegExp("3-\\w*")
                        for (let i = 0; i < problems_set.length; i++) {
                            if (reg1.test(problems_set[i].label)) {
                                primaryScore = primaryScore + problems_set[i].score
                                // flag1.push(problems_set[i].label)
                            } else if (reg2.test(problems_set[i].label)) {
                                advanceScore = advanceScore + problems_set[i].score
                                // flag2.push(problems_set[i].label)
                            } else if (reg3.test(problems_set[i].label)) {
                                sensiorScore = sensiorScore + problems_set[i].score
                                // flag3.push(problems_set[i].label)
                            }
                        }
                        let allScore = [primaryScore, advanceScore, sensiorScore]
                        this.allScore = allScore
                        // let labelCount = [flag1, flag2, flag3]
                        // this.labelCount = labelCount
                    })
                    .catch(error => {
                        this.$message({
                            showClose: true,
                            message: '阶段总分获取失败',
                            type: 'error'
                        });
                        console.log(error)
                    })
            },
        },
    }
</script>

<style scoped>
  @import "../../assets/rank.css";

  .el-pagination__total {
    color: white !important;
  }

</style>
