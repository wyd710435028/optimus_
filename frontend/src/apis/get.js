// 导入axios配置
import service from '../utils/request'

// 获取病历列表
export function getRecordList(hospitalName,admissionId,pageSize,currentPage) {
    return service.get('/medicalrecord/getMedicalRecordList', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            hospitalId: hospitalName,
            admissionId: admissionId,
            pageSize: pageSize,
            pageNum: currentPage
        }
    })
}
export function getHospitalDropDown(){
    return service.get('/medicalrecord/getHospitalDropDown', {
        headers: { 'Content-Type': 'application/json' }
    })
}
export function getUnderstandResult(hospitalId,admissionId,stage){
    return service.get('/medicalrecord/getUnderstandResult', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            hospitalId: hospitalId,
            admissionId: admissionId,
            stage:stage
        }
    })
}

export function docQueryList(hospitalId,admissionId,stage,fileName,emrNo,tags,pageSize,currentPage){
    return service.get('/medicalrecord/docQueryList', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            hospitalId: hospitalId,
            admissionId: admissionId,
            stage:stage,
            docName:fileName,
            emrNo:emrNo,
            tags:tags,
            pageSize:pageSize,
            pageNum:currentPage
        }
    })
}

export function eventQueryList(hospitalId,admissionId,stage,eventName,pageSize,currentPage){
    return service.get('/medicalrecord/eventQueryList', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            hospitalId: hospitalId,
            admissionId: admissionId,
            stage:stage,
            eventName:eventName,
            pageSize:pageSize,
            pageNum:currentPage
        }
    })
}

export function docContentDetail(hospitalId,admissionId,stage,fileId){
    return service.get('/medicalrecord/docContentDetail', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            hospitalId: hospitalId,
            admissionId: admissionId,
            stage:stage,
            fileId:fileId
        }
    })
}

/**
 * 查询评论列表
 * @returns {Promise<axios.AxiosResponse<any>>}
 */
export function getCommentLst() {
    return service.get('/comment/getCommentList', {
        headers: { 'Content-Type': 'application/json' },
    })
}

export function getCommentHistoryList(keyWords, fileId, nodeName, labelName) {
    return service.get('/comment/getCommentHistoryList', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            keyWords: keyWords,
            fileId: fileId,
            nodeName:nodeName,
            labelName:labelName
        }
    })
}

export function getOrderCommentHistoryList(fileId,content,executeTime,executorSign) {
    return service.get('/comment/getOrderCommentHistoryList', {
        headers: { 'Content-Type': 'application/json' },
        params: {
            fileId: fileId,
            content: content,
            executeTime:executeTime,
            executorSign:executorSign
        }
    })
}



