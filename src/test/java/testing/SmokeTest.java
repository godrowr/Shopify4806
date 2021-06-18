package testing;

import static org.assertj.core.api.Assertions.assertThat;

import myapp.FrontController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes={myapp.BootApplication.class})
public class SmokeTest {

    @Autowired
    private FrontController frontController;

    @Test
    public void contextLoads(){
        assertThat(frontController).isNotNull();
    }
}
