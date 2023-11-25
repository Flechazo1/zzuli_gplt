package cn.edu.zzuli.acm.to;

import lombok.Data;

@Data
public class ProblemSubmit {

    private String id;
    private UserV user;
    private String problemType;
    private ProblemSet problemSetProblem;
    private String submitAt;
    private String status;
    private Double score;
    private String compiler;
    private Double time;
    private Double memory;
    private Boolean showDetail;
    private Boolean previewSubmission;
}
