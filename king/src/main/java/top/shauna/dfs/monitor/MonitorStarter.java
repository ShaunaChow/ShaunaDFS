package top.shauna.dfs.monitor;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import top.shauna.dfs.starter.Starter;

/**
 * @Author Shauna.Chou
 * @Date 2020/11/1 14:58
 * @E-Mail z1023778132@icloud.com
 */
@SpringBootApplication
public class MonitorStarter implements Starter {
    @Override
    public void onStart() throws Exception {
        SpringApplication.run(MonitorStarter.class,new String[0]);
    }

    public static void main(String[] args) {
        SpringApplication.run(MonitorStarter.class,new String[0]);
    }
}
