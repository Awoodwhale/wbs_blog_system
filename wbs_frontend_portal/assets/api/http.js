import axios from "axios";

// TODO: frp或项目上线时需要修改
// axios.defaults.baseURL = "http://localhost:2022"
axios.defaults.baseURL = "http://120.48.126.17:11453"

// 携带证书
axios.defaults.withCredentials = true;

// 设置超时时间
axios.defaults.timeout = 100000;  // 100s

// 设置Content-Type
axios.defaults.headers.post['Content-Type'] = 'application/x-www=form-urlencoded'

// 导出方法，供其他调用使用
export default {
// get请求
    requestGet(url, params = {}) {
        return new Promise((resolve, reject) => {
            axios.get(url, params).then(res => {
                resolve(res.data)
            }).catch(error => {
                reject(error)
            })
        })
    },

    // post请求
    requestPost(url, params = {}) {
        return new Promise((resolve, reject) => {
            axios.post(url, params).then(res => {
                resolve(res.data)
            }).catch(error => {
                reject(error)
            })
        })
    },
    // delete请求
    requestDelete(url, params = {}) {
        return new Promise((resolve, reject) => {
            axios.delete(url, params).then(res => {
                resolve(res.data)
            }).catch(error => {
                reject(error)
            })
        })
    },
    // put请求
    requestPut(url, params = {}) {
        return new Promise((resolve, reject) => {
            axios.put(url, params).then(res => {
                resolve(res.data)
            }).catch(error => {
                reject(error)
            })
        })
    }
}
