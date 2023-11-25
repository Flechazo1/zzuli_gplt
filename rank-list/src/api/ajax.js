import axios from 'axios'

export default (url,method="GET",data={}) => {
    let baseUrl = "http://101.132.72.74:8080/api"
    if(method == "GET"){
        let str = "?"
        Object.keys(data).map(item => {
            str += item + "=" + data[item] + '&'
        })
        str = str.slice(0,str.length-1)
        url += str
        return axios.get(baseUrl+url)
    }else if(method == "POST"){
        return axios.post(baseUrl+url,data)
    }
}