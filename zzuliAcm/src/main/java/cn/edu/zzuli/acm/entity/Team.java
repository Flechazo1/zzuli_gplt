package cn.edu.zzuli.acm.entity;

import com.baomidou.mybatisplus.annotation.*;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.Accessors;

import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * <p>
 * 
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@Data
@EqualsAndHashCode(callSuper = false)
@Accessors(chain = true)
@TableName("rank_team")
@ApiModel(value="Team对象", description="")
public class Team implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @TableField(exist = false)
    private Integer rank;

    @ApiModelProperty(value = "队伍名")
    @TableField("teamName")
    private String teamname;

    @ApiModelProperty(value = "基础分数")
    @TableField("primaryScore")
    private Double primaryscore;

    @ApiModelProperty(value = "进阶分数")
    @TableField("advanceScore")
    private Double advancescore;

    @ApiModelProperty(value = "高级分数")
    @TableField("seniorScore")
    private Double seniorscore;

    @ApiModelProperty(value = "总分数")
    @TableField("totleScore")
    private Double totlescore;

    @ApiModelProperty(value = "添加时间")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    private Integer deleted;

    @ApiModelProperty(value = "最新提交时间")
    @TableField(exist = false)
    private String lastSubmitAt;

    @TableField(exist = false)
    private String the_class;


}
