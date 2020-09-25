package top.shauna.dfs.monitor.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.shauna.dfs.bean.Block;

/**
 * @Author Shauna.Chou
 * @Date 2020/9/25 10:58
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class StaticBean {
    private Long startTime;
    private Long endTime;
    private Block block;
}
