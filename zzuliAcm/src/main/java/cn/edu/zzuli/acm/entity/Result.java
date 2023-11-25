package cn.edu.zzuli.acm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author geji
 * @since 2020-12-21
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rank_result")
@ApiModel(value="Result对象", description="")
public class Result implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "当前题目集的数据")
    private String rankProblemsInfo;

    @ApiModelProperty(value = "比赛结果最后的个人json数据")
    private String rankUserInfo;

    @ApiModelProperty(value = "比赛结果最后的个人json数据")
    private String rankTeamInfo;

    @ApiModelProperty(value = "比赛的题目集id")
    private String rankProblemId;

    @ApiModelProperty(value = "当前比赛的名字")
    private String rankName;

    @ApiModelProperty(value = "是不是icpc赛制，0为天梯赛，1为icpc")
    private Integer isIcpc;

    @ApiModelProperty(value = "当前天梯赛的标准分")
    private String stands;

    @TableField(exist = false)
    private String sessionId;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    private Integer deleted;


}
