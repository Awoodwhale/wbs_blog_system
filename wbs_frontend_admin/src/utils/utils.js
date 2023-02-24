import {
    ElMessage,
    ElNotification,
    ElMessageBox
} from "element-plus";

// 弹出message
export const showMessage = (
    content,
    state = "success",
    isCenter = true,
    isClose = true
) => {
    ElMessage({
        showClose: isClose,
        message: content,
        center: isCenter,
        type: state,
    });
};

// 弹出notify
export const showNotify = (head, content, state = "success", time = 5000) => {
    ElNotification({
        title: head,
        message: content,
        type: state,
        duration: time
    });
};

// 弹出messagebox
export const showMessageBox = (content, head, state = 'warning') => {
    return ElMessageBox.confirm(
        content,
        head, {
        confirmButtonText: '确认',
        cancelButtonText: '取消',
        type: state,
        draggable: true,
    }
    )
}

// 表情解码
export const entitiestoUtf16 = (strObj) => {
    const patt = /&#\d+;/g;
    const arr = strObj.match(patt) || [];
    let H;
    let L;
    let code;
    for (let i = 0; i < arr.length; i += 1) {
        code = arr[i];
        code = code.replace("&#", "").replace(";", "");
        // 高位
        H = Math.floor((code - 0x10000) / 0x400) + 0xd800;
        // 低位
        L = ((code - 0x10000) % 0x400) + 0xdc00;
        code = `&#${code};`;
        const s = String.fromCharCode(H, L);
        strObj = strObj.replace(code, s);
    }
    return strObj;
}