package cn.edu.zzuli.acm.to;

import cn.edu.zzuli.acm.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.Accessors;

import java.util.List;

@Data
@Accessors(chain = true)
@AllArgsConstructor
@NoArgsConstructor
public class TeamExcelTo {

    private Integer rank;

    private String theClass;

    private String teamName;

    private List<User> users;
}
