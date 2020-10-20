package top.shauna.dfs.kingmanager.bean;

import lombok.*;
import top.shauna.dfs.type.TransactionType;

import java.io.Serializable;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/19 20:31
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
public class Transaction<T> implements Serializable {
    private Integer id;
    private TransactionType type;
    private T msg;
}
