// 导入axios配置
import service from '../utils/request'

export function getNodeByFileId(fileId,nodeMap,allEntityLabelList){
    return service.post('/medicalrecord/getNodeByFileId', {
        headers: { 'Content-Type': 'application/json' },
        fileId: fileId,
        nodeList: nodeMap,
        allEntityLabelList:allEntityLabelList
    })
}

export function transLabelList(allEntityLabelList){
    return service.post('/medicalrecord/transLabelList', {
        headers: { 'Content-Type': 'application/json' },
        allEntityLabelList:allEntityLabelList
    })
}

export function hightTextByOneTag(text,labelContent,labelColor,entityOrSpanListStr){
    return service.post('/medicalrecord/hightTextByOneTag', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            text:text,
            labelContent:labelContent,
            labelColor:labelColor,
            entityOrSpanListStr:entityOrSpanListStr
        }
    })
}


