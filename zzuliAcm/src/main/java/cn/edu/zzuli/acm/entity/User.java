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
@TableName("rank_user")
@ApiModel(value="User对象", description="")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "用户的学号")
    private String stuId;

    @ApiModelProperty(value = "用户对应pta平台账号id")
    private String sn;

    @ApiModelProperty(value = "用户的真实姓名")
    private String username;

    @ApiModelProperty(value = "用户的昵称")
    private String nickname;

    @TableField("gender")
    private String gender;

    @ApiModelProperty(value = "团队的Id")
    @TableField("teamId")
    private Integer teamid;

    @TableField("the_class")
    @ApiModelProperty(value = "所在班级")
    private String theClass;

    @ApiModelProperty(value = "加入日期")
    @TableField(fill = FieldFill.INSERT)
    private LocalDateTime addTime;

    @ApiModelProperty(value = "逻辑删除")
    @TableLogic
    private Integer deleted;


}
