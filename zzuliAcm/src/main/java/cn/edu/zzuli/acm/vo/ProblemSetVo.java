package cn.edu.zzuli.acm.vo;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class ProblemSetVo {

    @JsonIgnore
    private Integer id;
    private String label;
    private Object score;

}
