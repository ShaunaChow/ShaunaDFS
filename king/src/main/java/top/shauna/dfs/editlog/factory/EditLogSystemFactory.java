package top.shauna.dfs.editlog.factory;

import top.shauna.dfs.editlog.ShaunaEditLogSystem;
import top.shauna.dfs.editlog.interfaze.EditLogSystem;



/**
 * @Author Shauna.Chou
 * @Date 2020/10/15 16:51
 * @E-Mail z1023778132@icloud.com
 */
public class EditLogSystemFactory {

    public static EditLogSystem getEditLogSystem(){
        ShaunaEditLogSystem shaunaEditLogSystem = new ShaunaEditLogSystem();
        return shaunaEditLogSystem;
    }
}
