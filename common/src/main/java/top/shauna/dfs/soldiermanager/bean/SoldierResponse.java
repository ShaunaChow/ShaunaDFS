package top.shauna.dfs.soldiermanager.bean;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import top.shauna.dfs.type.SoldierResponseType;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/12 15:45
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class SoldierResponse implements Serializable {
    private SoldierResponseType res;
    private String msg;
    private String uuid;
}
