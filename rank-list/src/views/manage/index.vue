<template>
  <div class="container">
    <el-card class="bg">
      <h1 style="color: white; text-align: center">管理比赛页面</h1>
      <el-steps direction="vertical" :active="0">
        <el-step title="初始化阶段">
          <template slot="description">
            <el-form
              ref="step1_form"
              :model="step1_form"
              label-width="100px"
              style="margin-top: 20px"
            >
              <el-form-item label="比赛名称">
                <el-input v-model="step1_form.rankName"></el-input>
              </el-form-item>
              <el-form-item label="session">
                <el-input v-model="step1_form.sessionid"></el-input>
              </el-form-item>
              <el-form-item label="题目集id">
                <el-input v-model="step1_form.problemid"></el-input>
              </el-form-item>
              <el-form-item label="赛制选择">
                <el-radio v-model="step1_form.isIcpc" label="0"
                  >天梯赛</el-radio
                >
                <el-radio v-model="step1_form.isIcpc" label="1">Icpc</el-radio>
              </el-form-item>
              <el-form-item label="">
                <el-button type="primary" @click="toCreate">立即创建</el-button>
              </el-form-item>
            </el-form>
          </template>
        </el-step>
        <el-step title="爬取阶段">
          <template slot="description">
            <el-form
              ref="step2_form"
              :model="step2_form"
              label-width="100px"
              style="margin-top: 20px"
            >
              <el-form-item label="设置基础分">
                <el-input v-model="step2_form.primaryscore"></el-input>
              </el-form-item>
              <el-form-item label="设置进阶分">
                <el-input v-model="step2_form.advancescore"></el-input>
              </el-form-item>
              <el-form-item label="">
                <el-button type="primary" @click="toSetScore"
                  >立即设置</el-button
                >
              </el-form-item>
            </el-form>
          </template>
        </el-step>
        <el-step title="开启模块">
          <template slot="description">
            <el-form label-width="100px" style="margin-top: 20px">
              <el-form-item label="">
                <el-button type="primary" @click="startList"
                  >开启爬取题目集信息</el-button
                >
              </el-form-item>
              <el-form-item label="">
                <el-button type="primary" @click="startRankDetail"
                  >开启爬取排行耪信息</el-button
                >
              </el-form-item>
            </el-form>
          </template>
        </el-step>
        <el-step title="导出模块">
          <template slot="description">
            <el-form label-width="100px" style="margin-top: 20px">
              <el-form-item label="">
                <el-button type="primary" @click="createExcel"
                  >导出比赛成绩至数据库</el-button
                >
              </el-form-item>
            </el-form>
          </template>
        </el-step>
      </el-steps>
    </el-card>
    <div class="bg1"></div>
  </div>
</template>
<script>
import axios from "axios";
import { exportMethod } from "../../util/exportData";
export default {
  name: "manage",
  data() {
    return {
      active: 0,
      step1_form: {
        sessionid: "bb41e5b8-951e-42c8-94cc-129a42fb1a92",
        problemid: "1465919762492227584",
        isIcpc: "1",
        rankName: "",
      },
      step2_form: {
        primaryscore: "",
        advancescore: "",
      },
    };
  },
  methods: {
    toCreate() {
      let mainData = {
        session: this.step1_form.sessionid,
        problemid: this.step1_form.problemid,
      };
      let count = 0;
      axios
        .post("config/info/update", mainData)
        .then((res) => {
          if (res.status == 200) {
            count++;
          }
        })
        .catch((err) => {
          this.$message({
            message: "有错误，不能创建",
            type: "error",
          });
        });
      axios
        .post("result/completion", this.step1_form)
        .then((res) => {
          if (res.status == 200) {
            count++;
          }
        })
        .catch((err) => {
          this.$message({
            message: "有错误，不能创建",
            type: "error",
          });
        });
      if (count == 2) {
        this.active++;
      }
    },
    toSetScore() {
      axios.post("config/stands/update", this.step2_form).then((res) => {
        if (res.status == 200) {
          this.active++;
        }
      });
    },
    startList() {
      this.active++;
      // axios.get("config/problems/init/1464243200260685824").then((res) => {
      //   if(res.status==200){

      //   }
      // });
    },
    startRankDetail() {
      axios.get("config/job/icpc/rank").then((res) => {
        if (res.status == 200) {
          this.active++;
        }
      });
    },
    createExcel() {
      let myObj = {
        method: "get",
        url: 'excel/team/export',
        fileName: "比赛数据",
      };
      exportMethod(myObj)
    },
  },
};
</script>
<style scoped>
.container {
  background: url("../../assets/images/313353.jpg") 0 / cover fixed;
  min-height: 100vh;
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  overflow-y: scroll;
}

.bg {
  /* height: 500px; */
  width: 800px;
  word-break: break-all;
  z-index: 10;
  position: absolute;
  /* top: 50%; */
  left: 50%;
  margin-top: 50px;
  margin-left: -400px;
  margin-bottom: 100px;
  /* overflow: auto; */
  /* display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center; */
}
.bg1 {
  height: 10px;
  width: 800px;
  word-break: break-all;
  z-index: 10;
  position: absolute;
  /* top: 50%; */
  left: 50%;
  margin-top: 950px;
  margin-left: -400px;
  margin-bottom: 100px;
  /* overflow: auto; */
  /* display: flex;
  flex-direction: column;
  justify-content: center;
  align-items: center; */
}
.bg:before {
  content: "";
  background: url("../../assets/images/313353.jpg") center center / cover
    no-repeat fixed;
  filter: blur(20px);
  position: absolute;
  top: 0;
  bottom: 0;
  left: 0;
  right: 0;
  z-index: -1;
  margin: -30px;
}
.el-form-item__label {
  color: black;
}
</style>
