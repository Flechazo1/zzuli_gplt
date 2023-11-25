package cn.edu.zzuli.acm.controller;

import cn.edu.zzuli.acm.entity.R;
import cn.edu.zzuli.acm.service.ICPCRankService;
import io.swagger.annotations.Api;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/icpc")
@Api("icpc赛制接口")
public class ICPCRankController {

    @Autowired
    ICPCRankService icpcRankService;

    @GetMapping("/rank")
    public R getICPCRank() {
        return R.success().add("ranking", icpcRankService.getICPCTeamRank());
    }

}
