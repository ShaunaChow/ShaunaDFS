package top.shauna.dfs.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/11 15:26
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@AllArgsConstructor
public enum HeartBeatResponseType {
    SUCCESS(200,"OK"),
    NO_SUCH_BLOCK(401,"块不存在"),
    OUT_OF_DATE(403,"文件过期"),
    REPORT_BLOCKS_AGAIN(402,"请重新汇报blocks"),
//    IT_IS_NOT_AN_EMPTY_DIR(404,"路径为文件"),
    UNKNOWN(499,"未知错误");

    private Integer code;
    private String msg;
}
