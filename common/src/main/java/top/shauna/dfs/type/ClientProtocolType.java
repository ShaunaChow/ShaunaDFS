package top.shauna.dfs.type;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 16:23
 * @E-Mail z1023778132@icloud.com
 */
@Getter
@AllArgsConstructor
public enum  ClientProtocolType {
    SUCCESS(200,"OK"),
    NO_SUCH_DIR(401,"目录路径不存在"),
    NO_SUCH_File(403,"文件不存在"),
    ALLREADY_EXITS(402,"路径已经存在"),
    IT_IS_NOT_AN_EMPTY_DIR(404,"路径为文件"),
    UNKNOWN(499,"未知错误");

    private Integer code;
    private String msg;
}
