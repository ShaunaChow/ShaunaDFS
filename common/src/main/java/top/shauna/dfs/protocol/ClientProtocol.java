package top.shauna.dfs.protocol;

import top.shauna.dfs.kingmanager.bean.ClientFileInfo;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 15:09
 * @E-Mail z1023778132@icloud.com
 */
public interface ClientProtocol {

    ClientFileInfo uploadFile(ClientFileInfo fileInfo);

    void uploadFileOk(ClientFileInfo fileInfo);

    ClientFileInfo downloadFile(ClientFileInfo fileInfo);

    ClientFileInfo mkdir(ClientFileInfo fileInfo);
}
