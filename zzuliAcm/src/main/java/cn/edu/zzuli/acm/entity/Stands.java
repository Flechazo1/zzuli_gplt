package cn.edu.zzuli.acm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * <p>
 * 
 * </p>
 *
 * @author geji
 * @since 2020-11-29
 */
@Data
@AllArgsConstructor
@NoArgsConstructor
@EqualsAndHashCode(callSuper = false)
@TableName("rank_stands")
@ApiModel(value="Stands对象", description="")
public class Stands implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    @ApiModelProperty(value = "普通")
    @TableField("primaryScore")
    private Double primaryscore;

    @ApiModelProperty(value = "进阶")
    @TableField("advanceScore")
    private Double advancescore;

//    @ApiModelProperty(value = "高级")
//    @TableField("seniorScore")
//    private Double seniorscore;
}
