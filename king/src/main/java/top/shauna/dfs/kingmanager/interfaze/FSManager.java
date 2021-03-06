package top.shauna.dfs.kingmanager.interfaze;

import top.shauna.dfs.kingmanager.bean.ClientFileInfo;

import java.io.FileNotFoundException;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/14 20:09
 * @E-Mail z1023778132@icloud.com
 */
public interface FSManager {
    void uploadFile(ClientFileInfo fileInfo) throws Exception;

    void uploadFileOk(ClientFileInfo fileInfo);

    void downloadFile(ClientFileInfo fileInfo);

    void mkdir(ClientFileInfo fileInfo);

    void rmr(ClientFileInfo fileInfo, boolean rmAll);

    void initFS() throws Exception;
}
