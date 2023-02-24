import {checkUserLoginByToken, showUserInfo, SUCCESS} from "@/assets/api/api";

export default async (ctx) => {
    // 检测用户是否登录
    if (ctx.store.state.userInfo.id === undefined) {
        let token = ctx.app.$cookies.get("blog_system_token")
        const res = await checkUserLoginByToken(token)
        if (res.code === SUCCESS) {
            // 从token获取的用户信息不一定是最新的，所以请求一次最新的用户信息
            const ress = await showUserInfo(res.data.id)
            if (ress.code === SUCCESS) {
                ctx.store.commit("setUserInfo", ress.data)
            } else {
                // 请求失败就去使用token中的信息保底
                ctx.store.commit("setUserInfo", res.data)
            }
        }
    }
};
