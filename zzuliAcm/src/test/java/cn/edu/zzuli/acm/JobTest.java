package cn.edu.zzuli.acm;

import cn.edu.zzuli.acm.util.ProblemSubmitJobUtils;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JobTest {

    @Autowired
    ProblemSubmitJobUtils problemSubmitJobUtils;


    @Test
    void testJob() {
        problemSubmitJobUtils.openOrStopJob();
    }

}
