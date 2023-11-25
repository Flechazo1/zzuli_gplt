package cn.edu.zzuli.acm.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import io.swagger.annotations.ApiModel;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
@TableName("rank_problemSubmitStatu")
@ApiModel(value="Problemsubmitstatu对象", description="")
public class ProblemsubmitStatu implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Integer id;

    private String sn;

    @TableField("userId")
    private Integer userid;

    @TableField("problemType")
    private String problemtype;

    @TableField("ProblemInfoLabel")
    private String probleminfolabel;

    @TableField("submitAt")
    private LocalDateTime submitat;

    private Double score;

    private String compiler;

    private Double time;

    private Double memory;

    @TableField("showDetail")
    private Integer showdetail;

    @TableField("previewSubmission")
    private Integer previewsubmission;

    @TableField("problem_id")
    private String problemId;


}
