package top.shauna.dfs.interact.client.impl;

import lombok.extern.slf4j.Slf4j;
import top.shauna.dfs.kingmanager.ShaunaFSManager;
import top.shauna.dfs.kingmanager.bean.ClientFileInfo;
import top.shauna.dfs.protocol.ClientProtocol;
import top.shauna.dfs.type.ClientProtocolType;

/**
 * @Author Shauna.Chou
 * @Date 2020/10/10 15:26
 * @E-Mail z1023778132@icloud.com
 */
@Slf4j
public class ClientProtocolImpl implements ClientProtocol {
    @Override
    public ClientFileInfo uploadFile(ClientFileInfo fileInfo) {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();
        try{
            shaunaFSManager.uploadFile(fileInfo);
        }catch (Exception e){
            log.error("上传时出错："+e.getMessage());
            fileInfo.setRes(ClientProtocolType.UNKNOWN);
        }
        return fileInfo;
    }

    @Override
    public void uploadFileOk(ClientFileInfo fileInfo) {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();
        try{
            shaunaFSManager.uploadFileOk(fileInfo);
        }catch (Exception e){
            log.error("上传OK时出错："+e.getMessage());
            fileInfo.setRes(ClientProtocolType.UNKNOWN);
        }
    }

    @Override
    public ClientFileInfo downloadFile(ClientFileInfo fileInfo) {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();
        try{
            shaunaFSManager.downloadFile(fileInfo);
        }catch (Exception e){
            log.error("下载出错："+e.getMessage());
            fileInfo.setRes(ClientProtocolType.UNKNOWN);
        }
        return fileInfo;
    }

    @Override
    public ClientFileInfo mkdir(ClientFileInfo fileInfo) {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();
        try{
            shaunaFSManager.mkdir(fileInfo);
        }catch (Exception e){
            log.error("创建文件夹出错："+e.getMessage());
            fileInfo.setRes(ClientProtocolType.UNKNOWN);
        }
        return fileInfo;
    }

    @Override
    public ClientFileInfo rmFile(ClientFileInfo fileInfo) {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();
        try{
            shaunaFSManager.rmr(fileInfo,false);
        }catch (Exception e){
            log.error("删除出错："+e.getMessage());
            fileInfo.setRes(ClientProtocolType.UNKNOWN);
        }
        return fileInfo;
    }

    @Override
    public ClientFileInfo rmDir(ClientFileInfo fileInfo) {
        ShaunaFSManager shaunaFSManager = ShaunaFSManager.getInstance();
        try{
            shaunaFSManager.rmr(fileInfo,true);
        }catch (Exception e){
            log.error("删除出错："+e.getMessage());
            fileInfo.setRes(ClientProtocolType.UNKNOWN);
        }
        return fileInfo;
    }
}
