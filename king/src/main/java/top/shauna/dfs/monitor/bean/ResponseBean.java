package top.shauna.dfs.monitor.bean;

import lombok.Getter;
import lombok.Setter;

/**
 * @Author Shauna.Chou
 * @Date 2020/11/1 15:36
 * @E-Mail z1023778132@icloud.com
 */
@Setter
@Getter
public class ResponseBean<T> {
    private int code;
    private T msg;
    public ResponseBean(int code, T msg){
        this.code = code;
        this.msg = msg;
    }
}
