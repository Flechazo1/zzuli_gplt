<template>
  <div>
    <el-tooltip class="item" effect="dark" content="回到顶部" placement="top">
      <div @click="backTop" class="backtop">
        <i class="el-icon-caret-top"></i>
      </div>
    </el-tooltip>
    <el-container>
      <el-row
        style="
          display: flex;
          justify-content: center;
          padding: 20px;
          align-items: center;
          margin-left: 80px;
          color: #ffd700;
          font-size: 30px;
        "
      >
        <span>郑州轻工业大学程序设计团体赛</span>
      </el-row>
      <el-header
        id="title"
        style="width: 100%; position: fixed; z-index: 2; top: 80px"
      >
        <ul class="nav">
          <li>
            <router-link exact to="/TeamRank" replace append>团队排名</router-link>
          </li>
          <li>
            <router-link to="/SingleRank" replace append>个人排名</router-link>
          </li>
          <li @click="openNewWorld">拓展功能</li>
        </ul>
      </el-header>
      <el-main style="padding: 0; margin-top: 80px">
        <router-view />
      </el-main>
      <el-footer height="120px" style="margin: 0; padding: 0">
        <PageDescription />
      </el-footer>
    </el-container>
  </div>
</template>
<script>
import SingleRank from "../singleRank/index.vue";
import TeamRank from "../teamRank/index";
import PageDescription from "./PageDescription";
import $ from "jquery";

export default {
  name: "App",
  components: {
    SingleRank,
    TeamRank,
    PageDescription,
  },
  data() {
    return {
      activeIndex: this.$route.path,
      tipsShow: true,
      allScore: [],
      scorllTop: "",
    };
  },
  created() {},
  mounted() {
    $("body").scroll(function () {
      if ($("body").scrollTop() > 45) {
        $("#title").css("top", "0px");
        $("#title").css("transition", "top .5s");
        $(".backtop").css("display", "flex");
      } else {
        $("#title").css("top", "80px");
        $("#title").css("transition", "top .5s");
        $(".backtop").css("display", "none");
      }
    });
  },
  methods: {
    handleSelect(key, keyPath) {
      console.log(key, keyPath);
    },
    openNewWorld() {
      const h = this.$createElement;
      this.$notify({
        title: "敬请期待",
        message: h("i", { style: "color: teal" }, "该功能暂未开发,敬请期待"),
      });
    },
    handleScroll() {
      let read = document.body.scrollTop;
      console.log(read);
    },
    //回到顶部
    backTop() {
      $("body").scrollTop(0);
    },
  },
};
</script>

<style>

.nav {
  text-decoration: none;
  color: white;
  display: flex;
  flex-direction: row;
  justify-content: center;
  align-items: center;
  padding: 15px;
  list-style: none;

  margin-left: -100px;
}

.nav > li {
  margin-left: 150px;
  font-size: 20px;
}

.nav > li:nth-child(3):hover {
  border-radius: 50%;
  box-shadow: 0px 0px 20px 25px white;
  color: #0088ae;
  background-color: white;
}

.router-link-active {
  border-radius: 50%;
  box-shadow: 0px 0px 20px 25px white;
  color: #0088ae;
  background-color: white;
}

a {
  text-decoration: none;
  color: white;
}

.backtop {
  display: none;
  position: absolute;
  bottom: 40px;
  right: 40px;
  font-size: 50px;
  color: #ffd700;
  line-height: 50px;
}

/*@keyframes move{*/
/*  0%{*/
/*    clip-path: circle(30px at 0% 50%);*/
/*  }*/
/*  100%{*/
/*    clip-path: circle(30px at 100% 50%);*/
/*  }*/
/*}*/
</style>