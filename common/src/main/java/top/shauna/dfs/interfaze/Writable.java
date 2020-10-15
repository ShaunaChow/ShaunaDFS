package top.shauna.dfs.interfaze;

import java.io.DataOutputStream;
import java.io.IOException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 14:56
 * @E-Mail z1023778132@icloud.com
 */
public interface Writable {
    boolean write(DataOutputStream fileOutputStream) throws IOException;
}
