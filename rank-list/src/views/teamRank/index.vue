<template>
  <div>
    <div class="shelter" @click="closeShlter" v-show="shelter"></div>
    <div class="table">
      <el-row>
        <span class="title">团队排名</span>
        <span style="color:wheat;position: absolute;right: 0;top: 20px">该榜单每30秒刷新一次,请稍等。</span>
      </el-row>
      <el-row class="pagination">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total,  prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </el-row>
      <div v-loading="loading1" :style="{height:(height1)}"
           element-loading-text="拼命加载中"
           element-loading-spinner="el-icon-loading"
           element-loading-background="rgba(255, 255, 255, 0)">
        <el-alert
          v-show="isShowAlert"
          title="暂时没有团队得分信息，请稍后刷新"
          type="warning"
          center :closable="false"
          show-icon>
        </el-alert>
        <el-row class="single" @click.native="showDetail(item.id,item.teamname,item.the_class)" v-for="(item,index) in items.slice((currentPage-1)*pageSize,currentPage*pageSize)" :key="index">
          <div class="badge">
            <span><i>{{item.rank}}</i></span>
          </div>
          <div class="progress">
            <div class="head">
              <img src="../../assets/images/zzuli.png">
            </div>
            <div class="progress1">
                <span style="color:white;display: inline-block;white-space: nowrap">
                  {{item.teamname?`${item.teamname}`:'---'}}——{{item.the_class?`${item.the_class}`:'---'}}&nbsp;
                   <el-tooltip class="item" effect="dark" content="第一阶段等级" placement="top">
                    <svg v-if="item.primaryscore>0" class="icon" aria-hidden="true" color="#6FE0FF">
                      <use v-if="item.primaryscore===allScore[0]" xlink:href="#icon-dengji-A"></use>
                      <use v-else-if="item.primaryscore>=standard[0]" xlink:href="#icon-dengji-B"></use>
                      <use v-else-if="item.primaryscore<standard[0]" xlink:href="#icon-dengji-C"></use>
                    </svg>
                   </el-tooltip>
                   <el-tooltip class="item" effect="dark" content="第二阶段等级" placement="top">
                    <svg v-if="item.advancescore>0" class="icon" aria-hidden="true" color="#DCB672">
                      <use v-if="item.advancescore===allScore[1]" xlink:href="#icon-dengji-A"></use>
                      <use v-else-if="item.advancescore>=standard[1]" xlink:href="#icon-dengji-B"></use>
                      <use v-else-if="item.advancescore<standard[1]" xlink:href="#icon-dengji-C"></use>
                    </svg>
                  </el-tooltip>
                  <el-tooltip class="item" effect="dark" content="第三阶段等级" placement="top">
                    <svg v-if="item.seniorscore>0" class="icon" aria-hidden="true" color="#DC7272">
                      <use v-if="item.seniorscore===allScore[2]" xlink:href="#icon-dengji-A"></use>
                      <use v-else-if="item.seniorscore>=allScore[2]/2" xlink:href="#icon-dengji-B"></use>
                      <use v-else-if="item.seniorscore<allScore[2]/2" xlink:href="#icon-dengji-C"></use>
                    </svg>
                  </el-tooltip>
                </span>
              <el-tooltip class="item" effect="dark" :content="item.primaryscore+'/'+allScore[0]" placement="bottom">
                <img src="../../assets/images/progress_blue.png" :style="{width:(item.primaryscore/allScore[0])*100+'%'}" height="20px">
              </el-tooltip>
              <img v-show="item.primaryscore>=0 && (item.primaryscore<standard[0]||item.advancescore===0)"
                   src="../../assets/images/circle_blue.png" class="circleStyle"
                   :style="{left:(item.primaryscore/allScore[0])*100+'%'}">
            </div>
            <div class="point_sm">
              <!--            <img width="45px" height="50px" src="../assets/images/two.png">-->
            </div>

              <div class="progress1">
                <el-tooltip class="item" effect="dark" :content="item.advancescore+'/'+allScore[1]" placement="bottom">
                <img v-if="item.primaryscore>=standard[0]" src="../../assets/images/progress_orange.png"
                     :style="{width:(item.advancescore/allScore[1])*100+'%'}" height="20px">

                <img v-else src="../../assets/images/progress_gray.png"
                     :style="{width:(item.advancescore/allScore[1])*100+'%'}" height="20px">
                </el-tooltip>
                <img
                  v-show="(item.advancescore>0||item.primaryscore===allScore[0]) && item.primaryscore>=standard[0]&&(item.seniorscore===0||item.advancescore<standard[1])"
                  src="../../assets/images/circle_yellow.png" class="circleStyle"
                  :style="{left:(item.advancescore/allScore[1])*100+'%'}">
              </div>
            <div class="point_sm"></div>

              <div class="progress2">
                <el-tooltip class="item" effect="dark" :content="item.seniorscore+'/'+allScore[2]" placement="bottom">
                <img v-if="item.advancescore>=standard[1] && item.primaryscore>=standard[0]"
                     src="../../assets/images/progress_red.png" :style="{width:(item.seniorscore/allScore[2])*100+'%'}"
                     height="20px">
                <img v-else src="../../assets/images/progress_gray.png"
                     :style="{width:(item.seniorscore/allScore[2])*100+'%'}" height="20px">
                </el-tooltip>
                <img v-show="item.primaryscore>=standard[0] && item.advancescore>=standard[1] && item.seniorscore>0"
                     src="../../assets/images/circle_red.png" class="circleStyle"
                     :style="{left:(item.seniorscore/allScore[2])*100+'%'}">
              </div>

            <div class="point_big"></div>
          </div>
          <div class="score">
            <span><i>{{item.totlescore}}</i></span>
          </div>
        </el-row>
      </div>
      <el-row class="pagination">
        <el-pagination
          background
          @current-change="handleCurrentChange"
          :current-page.sync="currentPage"
          :page-size="pageSize"
          layout="total,  prev, pager, next, jumper"
          :total="total">
        </el-pagination>
      </el-row>
      <div class="detail" v-show="isShow">
        <el-card class="box-card" v-loading="loading2"
                 element-loading-text="拼命加载中"
                 element-loading-spinner="el-icon-loading"
                 element-loading-background="rgba(57, 72, 145, .9)">
          <el-row style="display: flex;justify-content: flex-end"><i class="fa fa-times" aria-hidden="true"
                                                                     @click="closeShlter"></i></el-row>
          <el-row class="title_detail">
            <span><i>{{scoreDetails.teamname}}</i></span>
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
                <td v-for="(item1) in labelCount[0]" style="color: #7ACCEC">{{item1}}
                </td>
                <td v-for="(item2) in labelCount[1]" style="color:#FFD700;">{{item2}}
                </td>
                <td v-for="(item3) in labelCount[2]" style="color:#F04F4F;">{{item3}}
                </td>
                <td style="color:#FFD700;"><b>总分</b></td>
              </tr>
              <tr class="row3" v-for="(item4) in scoreDetails.detail">
                <td>
                  <el-avatar size="small"
                             src="https://cube.elemecdn.com/0/88/03b0d39583f48206768a7534e55bcpng.png"></el-avatar>
                </td>
                <td><span style="color:white;">
                  {{item4[0]?`${item4[0]}`:'---'}}
                </span></td>
                <td v-for="(item5) in item4[1]" style="color: #7ACCEC">{{item5.score}}</td>
                <td v-for="(item6) in item4[3]" :class="[item4[2]<standard[0]/10?xiahuaxian1Class:'']"
                    style="color:#FFD700;">{{item6.score}}
                </td>
                <td v-for="(item7) in item4[5]"
                    :class="[item4[2]<standard[0]/10&&item4[4]<standard[1]/10?xiahuaxian2Class:'']"
                    style="color:#F04F4F;">{{item7.score}}
                </td>
                <td style="color:#FFD700;">
                  {{item4[6]}}
                </td>
              </tr>
            </table>
          </el-row>
        </el-card>
      </div>
    </div>
  </div>
</template>

<script>
    import axios from 'axios'
    import '../../assets/iconfont'
    export default {
        name: "TeamRank",
        data() {
            return {
                xiahuaxian1Class: 'xiahuaxian1',
                xiahuaxian2Class: 'xiahuaxian2',
                isShowAlert: false,
                height1: '328px',
                loading1: false,
                loading2: false,
                currentPage: 1,
                pageSize: 10,
                total: 10,
                shelter: false,
                isShow: false,
                items: [],
                allScore: [],
                standard: [200, 100],
                scoreDetails: {
                    'teamname': 'loading',
                    'detail': [
                        {'user': {'username': 'loading'}},
                    ],
                },
                labelCount: [[], [], []],
                websock: null,
                heartCheck: {},
                lockReconnect: false,//是否真正建立连接
                timeout: 28 * 1000,//30秒一次心跳
                timeoutObj: null,//心跳心跳倒计时
                serverTimeoutObj: null,//心跳倒计时
                timeoutnum: null,//断开 重连倒计时
            }
        },
        created() {
            // this.initWebSocket()
            const height = document.body.clientHeight - 260
            this.height = height + 'px'
            this.getStandard()
            this.getTeamScore()
            this.getAllScore()
            const timer = setInterval(() => {
                setTimeout(this.getTeamScore(), 0)
            }, 180000)
            this.$once('hook:beforeDestroy', () => {
                clearInterval(timer)
            })
        },
        mounted() {
        },
        methods: {
            //切换页面时计算页面高度
            handleCurrentChange(val) {
                let totalPages = Math.ceil(this.total / this.pageSize)
                if (val === totalPages) {
                    let items = this.total % this.pageSize
                    if (items === 0) {
                        this.height1 = `${this.pageSize * 120}px`
                        return
                    }
                    this.height1 = `${items * 120}px`
                }
            },
            //获取团队列表
            getTeamScore() {
                this.loading1 = !this.loading1
                const url = 'team/getTeamScore'
                axios.get(url)
                    .then(response => {
                        let items = response.data.data.teamScores
                        console.log(items)
                        if (items === null) {
                            this.isShowAlert = true
                            this.loading1 = !this.loading1
                            return
                        }
                        this.isShowAlert = false
                        this.total = items.length
                        this.items = items
                        this.height1 = `${this.pageSize * 120}px`
                        this.loading1 = !this.loading1
                    })
                    .catch(error => {
                        this.$message({
                            showClose: true,
                            message: '团队信息获取失败',
                            type: 'error'
                        });
                        console.log(error)
                        this.loading1 = !this.loading1
                    })
            },
            //获取标准 websockt版本
            // getStandard(standard) {
            //     let standards = [standard.primaryscore * 10, standard.advancescore * 10]
            //     this.standard = standards
            // },
            //获取总分 websockt版本
            // getAllScore(data) {
            //     let primaryScore = 0, flag1 = []
            //     let advanceScore = 0, flag2 = []
            //     let sensiorScore = 0, flag3 = []
            //     var reg1 = new RegExp("1-\\w*")
            //     var reg2 = new RegExp("2-\\w*")
            //     var reg3 = new RegExp("3-\\w*")
            //     for (let key in data) {
            //         if (reg1.test(key)) {
            //             primaryScore = primaryScore + data[key]
            //         } else if (reg2.test(key)) {
            //             advanceScore = advanceScore + data[key]
            //         } else if (reg3.test(key)) {
            //             sensiorScore = sensiorScore + data[key]
            //         }
            //     }
            //     let allScore = [primaryScore * 10, advanceScore * 10, sensiorScore * 10]
            //     let labelCount = [flag1, flag2, flag3]
            //     this.allScore = allScore
            //     this.labelCount = labelCount
            // },
            //显示个人信息
            showDetail(index, teamname, theClass) {
                this.loading2 = true
                const url = `team/getTeamScoreDetil/${index}`
                axios.get(url)
                    .then(response => {
                        let problems_set = response.data.data.scoresDetail[0].problemScores
                        let primaryScoreItemsLabel = []
                        let advanceScoreItemsLabel = []
                        let seniorScoreItemsLabel = []
                        var reg1 = new RegExp("1-\\w*")
                        var reg2 = new RegExp("2-\\w*")
                        var reg3 = new RegExp("3-\\w*")
                        for (let key in problems_set) {
                            if (reg1.test(key)) {
                                primaryScoreItemsLabel.push(key)
                            } else if (reg2.test(key)) {
                                advanceScoreItemsLabel.push(key)
                            } else if (reg3.test(key)) {
                                seniorScoreItemsLabel.push(key)
                            }
                        }
                        let scoresDetail = []
                        console.log(response.data.data.scoresDetail)
                        for (let i = 0; i < response.data.data.scoresDetail.length; i++) {
                            let problems_set = response.data.data.scoresDetail[i].problemScores
                            let teamOne = []
                            let primaryScoreItems = []
                            let advanceScoreItems = []
                            let seniorScoreItems = []
                            for (let key in problems_set) {
                                if (reg1.test(key)) {
                                    primaryScoreItems.push(problems_set[key])
                                } else if (reg2.test(key)) {
                                    advanceScoreItems.push(problems_set[key])
                                } else if (reg3.test(key)) {
                                    seniorScoreItems.push(problems_set[key])
                                }
                            }
                            teamOne = [
                                response.data.data.scoresDetail[i].user.name,
                                primaryScoreItems,
                                response.data.data.scoresDetail[i].primaryScore,
                                advanceScoreItems,
                                response.data.data.scoresDetail[i].advanceScore,
                                seniorScoreItems,
                                response.data.data.scoresDetail[i].totalScore]
                            scoresDetail.push(teamOne)
                        }
                        let labelCount = [primaryScoreItemsLabel, advanceScoreItemsLabel, seniorScoreItemsLabel]
                        let scoreDetails = {
                            'teamname': teamname,
                            'theClass': theClass,
                            'detail': scoresDetail
                        }
                        this.labelCount = labelCount
                        this.scoreDetails = scoreDetails
                        this.loading2 = !this.loading2
                    })
                    .catch(error => {
                        this.loading2 = !this.loading2
                        this.$message({
                            showClose: true,
                            message: '团队得分详情获取失败',
                            type: 'error'
                        });
                        console.log(error)
                    })
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

            // 获取标准
            getStandard() {
                const url = 'info/stands'
                axios.get(url)
                    .then(response => {
                        let standard = [response.data.data.stands.primaryscore * 10, response.data.data.stands.advancescore * 10]
                        this.standard = standard
                    })
                    .catch(error => {
                        this.$message({
                            showClose: true,
                            message: '标准信息获取失败',
                            type: 'error'
                        });
                        console.log(error)
                    })
            },
            // 获取题目集信息,
            getAllScore() {
                const url = 'info/problems'
                axios.get(url)
                    .then(response => {
                        let problems_set = response.data.data.problems_set
                        console.log(problems_set)
                        let primaryScore = 0, flag1 = []
                        let advanceScore = 0, flag2 = []
                        let sensiorScore = 0, flag3 = []
                        var reg1 = new RegExp("1-\\w*")
                        var reg2 = new RegExp("2-\\w*")
                        var reg3 = new RegExp("3-\\w*")
                        for (let i = 0; i < problems_set.length; i++) {
                            if (reg1.test(problems_set[i].label)) {
                                primaryScore = primaryScore + problems_set[i].score
                                flag1.push(problems_set[i].label)
                            } else if (reg2.test(problems_set[i].label)) {
                                advanceScore = advanceScore + problems_set[i].score
                                flag2.push(problems_set[i].label)
                            } else if (reg3.test(problems_set[i].label)) {
                                sensiorScore = sensiorScore + problems_set[i].score
                                flag3.push(problems_set[i].label)
                            }
                        }
                        let allScore = [primaryScore * 10, advanceScore * 10, sensiorScore * 10]
                        let labelCount = [flag1, flag2, flag3]
                        this.allScore = allScore
                        this.labelCount = labelCount
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
  .detail {
    top: 120px;
  }

  td {
    height: 40px;
  }
</style>
