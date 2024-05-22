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

export function getNodeByFileIdWithHighLight(fileId,labelContent,labelColor,nodeMap){
    return service.post('/medicalrecord/getNodeByFileIdWithHighLight', {
        headers: { 'Content-Type': 'application/json' },
        fileId: fileId,
        labelContent: labelContent,
        labelColor: labelColor,
        nodeList: nodeMap
    })
}

export function transLabelList(allEntityLabelList){
    return service.post('/medicalrecord/transLabelList', {
        headers: { 'Content-Type': 'application/json' },
        allEntityLabelList:allEntityLabelList
    })
}

export function hightTextByOneTag(text,labelContent,labelColor,entityOrSpanListStr,fileId,nodeName){
    return service.post('/medicalrecord/hightTextByOneTag', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            text:text,
            labelContent:labelContent,
            labelColor:labelColor,
            entityOrSpanListStr:entityOrSpanListStr,
            fileId:fileId,
            nodeName:nodeName
        }
    })
}

export function saveComment(parentId,userId,rootParentId,content){
    return service.post('/comment/saveComment', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            parentId:parentId,
            userId:userId,
            rootParentId:rootParentId,
            content:content
        }
    })
}

export function deleteCommentById(id){
    return service.post('/comment/deleteCommentById', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            id:id
        }
    })
}

export function createNewRootComment(rootCommentContent,userId){
    return service.post('/comment/createNewRootComment', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            rootCommentContent:rootCommentContent,
            userId:userId
        }
    })
}

export function createNewResultComment(rootCommentContent,userId,keyWords,fileId,nodeName,labelName,docName){
    return service.post('/comment/createNewResultComment', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            rootCommentContent:rootCommentContent,
            userId:userId,
            keyWords:keyWords,
            fileId:fileId,
            nodeName:nodeName,
            labelName:labelName,
            docName:docName
        }
    })
}


export function createNewOrderComment(rootCommentContent,userId,fileId,orderContent,executeTime,executorSign,unisoundId,admissionNo,hospitalNo){
    return service.post('/comment/createNewOrderComment', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            rootCommentContent:rootCommentContent,
            userId:userId,
            fileId:fileId,
            orderContent:orderContent,
            executeTime:executeTime,
            executorSign:executorSign,
            unisoundId:unisoundId,
            admissionNo:admissionNo,
            hospitalNo:hospitalNo
        }
    })
}

export function updateOrderCommentStatus(id,orderCommentStatus){
    return service.post('/comment/updateOrderCommentStatus', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            id:id,
            orderCommentStatus:orderCommentStatus
        }
    })
}
export function markDoc(hospitalId,admissionId,stage,fileId){
    return service.post('/medicalrecord/markDoc', {
        headers: { 'Content-Type': 'application/json' },
        params:{
            hospitalId:hospitalId,
            admissionId:admissionId,
            stage:stage,
            fileId: fileId
        }
    })
}

export function addMarkedRemark(hospitalId,admissionId,stage,fileId,remarkContent){
    return service.post('/medicalrecord/addMarkedRemark', {
        headers: { 'Content-Type': 'application/json' },
        params:{
            hospitalId:hospitalId,
            admissionId:admissionId,
            stage:stage,
            fileId: fileId,
            remarkContent: remarkContent
        }
    })
}

export function markSpanError(hospitalId,admissionId,emrNo,docName,nodeName,spanTextContent,spanLabel){
    return service.post('/medicalrecord/markSpanError', {
        headers: { 'Content-Type': 'application/json' },
        params:{
            hospitalId: hospitalId,
            admissionId: admissionId,
            emrNo:emrNo,
            docName:docName,
            nodeName:nodeName,
            spanTextContent:spanTextContent,
            spanLabel:spanLabel
        }
    })
}

export function cancelSpanMark(hospitalId,admissionId,emrNo,docName,nodeName,spanTextContent,spanLabel){
    return service.post('/medicalrecord/cancelSpanMark', {
        headers: { 'Content-Type': 'application/json' },
        params:{
            hospitalId: hospitalId,
            admissionId: admissionId,
            emrNo:emrNo,
            docName:docName,
            nodeName:nodeName,
            spanTextContent:spanTextContent,
            spanLabel:spanLabel
        }
    })
}

export function addHospital(hospitalNo,hospitalName){
    return service.post('/medicalrecord/addHospital', {
        headers: { 'Content-Type': 'application/json' },
        params:{
            hospitalNo: hospitalNo,
            hospitalName: hospitalName
        }
    })
}


