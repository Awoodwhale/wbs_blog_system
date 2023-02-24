<template>
    <div class="j-comment" @click="showVEmojiPicker = false">
        <div id="v-emoji-picker" @click.stop="() => {}">
            <VEmojiPicker v-show="showVEmojiPicker" @select="selectEmoji"/>
        </div>
        <textarea
            v-if="withCommentContent"
            v-model="commentText"
            :rows="3"
            id="j-comment-content"
            placeholder="请输入评论"
            class="j-comment-content"
            @input="commentInput"
        ></textarea>
        <div v-if="withCommentContent" class="j-comment-content-btns">
            <img
                @click.stop="showEmoji"
                id="comment-emoji"
                style="width:20px;height:20px;cursor:pointer;"
                src="/emoji.png"
            />
            <el-popconfirm style="margin-left: auto;"
                           @confirm="submitComment()"
                           title="您确定要发表此评论吗？"
            >
                <button slot="reference">发表</button>
            </el-popconfirm>
        </div>
        <div class="j-comment-title" v-if="title !== ''">{{ title }}</div>
        <div
            class="j-comment-item"
            v-for="(item, index) in showComentDatas"
            :key="'comment' + index"
        >
            <div class="j-comment-header">
                <div class="j-comment-header-pre">
                    <img class="j-comment-header-img" :src="item.avatar"/>
                    <span class="j-comment-header-nickname"
                    >{{
                            item.nickname
                        }}<span
                            v-if="item[keyMap.isAdmin] === 1"
                            style="color:skyblue;"
                        >(博主)</span
                        ></span
                    >
                </div>
                <div class="j-comment-header-time">{{ item.create_time }}</div>
            </div>
            <div class="j-comment-body">
                {{ entitiestoUtf16(item.content) }}
            </div>
            <div class="j-comment-footer">
                <img
                    class="j-comment-icon"
                    src="/comment.png"
                    @click="replyClick(item, index)"
                />
                <span @click="replyClick(item, index)">{{
                        item.showReply
                            ? "取消回复"
                            : (item.children && item.children.length) || ""
                    }}</span>
            </div>
            <textarea
                v-if="item.showReply"
                v-model="replyText"
                :id="'reply-' + index"
                :placeholder="replyText === '' ? '回复' + item.nickname : ''"
                class="j-comment-reply-content focused"
                @input="replyInput"
            ></textarea>
            <div v-if="item.showReply" class="j-comment-reply-btns">
                <img
                    class="emoji-btn"
                    @click.stop="showEmoji"
                    :id="'emoji-reply-' + index"
                    style="width:20px;height:20px;cursor:pointer;"
                    src="/emoji.png"
                />
                <el-popconfirm style="margin-left: auto;padding-right: 1.1rem;"
                               @confirm="submitReply(item.id)"
                               title="您确定要发表此评论吗？"
                >
                    <button slot="reference">发表</button>
                </el-popconfirm>

            </div>
            <div
                class="j-comment-childer"
                v-if="item.children && item.children.length > 0"
            >
                <div
                    class="j-comment-item"
                    v-for="(children, childrenIndex) in item.showChildren"
                    :key="'children' + childrenIndex"
                >
                    <div class="j-comment-header">
                        <div class="j-comment-header-pre">
                            <img
                                class="j-comment-header-img"
                                :src="children.avatar"
                            />
                            <span class="j-comment-header-nickname">
                                {{ children.nickname }}
                                <span
                                    v-if="children[keyMap.isAdmin] === 1"
                                    style="color:skyblue;"
                                >(博主)</span>
                            </span>
                        </div>
                        <div
                            class="j-comment-header-reply-nickname"
                            v-if="children.reply"
                        >
                            <span>回复</span>{{ children.reply }}
                        </div>
                        <div class="j-comment-header-time">
                            {{ children.create_time }}
                        </div>
                    </div>
                    <div class="j-comment-body">
                        {{ entitiestoUtf16(children.content) }}
                    </div>
                    <div class="j-comment-footer">
                        <img
                            class="j-comment-icon"
                            src="/comment.png"
                            @click="
                                replyClick(
                                    item,
                                    index,
                                    children,
                                    childrenIndex,
                                    true
                                )
                            "
                        />
                        <span
                            @click="
                                replyClick(
                                    item,
                                    index,
                                    children,
                                    childrenIndex,
                                    true
                                )
                            "
                        >{{
                                children.showReply
                                    ? "取消回复"
                                    : (children.children &&
                                        children.children.length) ||
                                    ""
                            }}</span
                        >
                    </div>
                    <textarea
                        v-if="children.showReply"
                        v-model="replyText"
                        :id="'reply-' + index + '-' + childrenIndex"
                        :placeholder="
                            replyText === '' ? '回复' + children.nickname : ''
                        "
                        class="j-comment-reply-content focused"
                        @input="replyInput"
                    ></textarea>
                    <div v-if="children.showReply" class="j-comment-reply-btns">
                        <img
                            class="emoji-btn"
                            @click.stop="showEmoji"
                            :id="'emoji-reply-' + index + '-' + childrenIndex"
                            style="width:20px;height:20px;cursor:pointer;"
                            src="/emoji.png"
                        />
                        <el-popconfirm style="margin-left: auto;padding-right: 1.1rem;" title="您确定要发表此评论吗？"
                                       @confirm="submitReply(children[keyMap.id || 'id'])">
                            <button slot="reference" class="replay_btn">发表</button>
                        </el-popconfirm>

                    </div>
                    <div class="j-comment-childer" v-if="children.pContent">
                        <div
                            class="j-coment-children-content"
                            :title="entitiestoUtf16(children.pContent)"
                        >
                            {{ entitiestoUtf16(children.pContent) }}
                        </div>
                    </div>
                </div>
                <div
                    v-if="item.showChildren.length < item.children.length"
                    :key="'more' + index"
                    class="j-comment-childer-more"
                    @click="showMore(item, index)"
                >
                    查看更多回复∨
                </div>
                <div
                    v-else-if="item.showChildren.length > showNumber"
                    :key="'more' + index"
                    class="j-comment-childer-more"
                    @click="showMore(item, index)"
                >
                    收起回复∧
                </div>
            </div>
        </div>
    </div>
</template>

<script>
import {VEmojiPicker} from "v-emoji-picker";

export default {
    inject: ["scrollRef"],
    name: "JComment",
    props: {
        title: {
            type: String,
            default: "热门评论"
        },
        orderBy: {
            type: String,
            default: "time"
        },
        rank: {
            type: String,
            default: "desc"
        },
        showNumber: {
            type: Number,
            default: 2
        },
        withCommentContent: {
            type: Boolean,
            default: true
        },
        keyMap: {
            type: Object,
            default: () => {
                return {
                    pid: "pid",
                    id: "id",
                    isAdmin: "isAdmin"
                };
            }
        },
        commentDatas: {
            type: Array,
            default: () => {
                return [];
            }
        }
    },
    components: {
        VEmojiPicker
    },
    data() {
        return {
            showComentDatas: [],
            replyText: "",
            commentText: "",
            lastLength: 0,
            showItem: [],
            showVEmojiPicker: false,
            emojiTextId: ""
        };
    },
    created() {
        this.initData();
    },
    watch: {
        commentDatas() {
            this.initData();
        }
    },
    computed: {},
    methods: {
        // 表情转码
        utf16toEntities(str) {
            const patt = /[\ud800-\udbff][\udc00-\udfff]/g; // 检测utf16字符正则
            str = str.replace(patt, char => {
                let H;
                let L;
                let code;
                let s;
                if (char.length === 2) {
                    H = char.charCodeAt(0); // 取出高位
                    L = char.charCodeAt(1); // 取出低位
                    code = (H - 0xd800) * 0x400 + 0x10000 + L - 0xdc00; // 转换算法
                    s = `&#${code};`;
                } else {
                    s = char;
                }
                return s;
            });
            return str;
        },
        // 表情解码
        entitiestoUtf16(strObj) {
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
        },
        showEmoji(el) {
            let v = document.getElementById("v-emoji-picker");
            let sc = this.scrollRef().$refs.wrap.scrollTop
            let bgh = document.getElementById("header-background").offsetHeight
            v.style.left = el.layerX + 5 + "px"
            v.style.top = sc - bgh + el.pageY + 10 + "px";
            this.showVEmojiPicker = !this.showVEmojiPicker;
            this.emojiTextId = el.target.id;
        },
        selectEmoji(emoji) {
            // 选择emoji后调用的函数
            let input = null;
            if (this.emojiTextId === "comment-emoji") {
                input = document.getElementById("j-comment-content");
            } else {
                let id = this.emojiTextId.split("emoji-");
                input = document.getElementById(id[1]);
            }
            let startPos = input.selectionStart;
            let endPos = input.selectionEnd;
            let resultText =
                input.value.substring(0, startPos) +
                emoji.data +
                input.value.substring(endPos);
            input.value = resultText;
            input.focus();
            input.selectionStart = startPos + emoji.data.length;
            input.selectionEnd = startPos + emoji.data.length;
            if (this.emojiTextId === "comment-emoji")
                this.commentText = resultText;
            else {
                this.replyText = resultText;
            }
        },
        submitReply(id) {
            const params = {
                pid: id,
                content: this.utf16toEntities(this.replyText)
            };
            this.replyText = "";
            this.showVEmojiPicker = false;
            this.$emit("submitComment", params);
        },
        submitComment(id = null) {
            const params = {
                pid: id,
                content: this.utf16toEntities(this.commentText)
            };
            this.commentText = "";
            this.showVEmojiPicker = false;
            this.$emit("submitComment", params);
        },
        showMore(item, index) {
            this.showItem[index] = !this.showItem[index];
            if (item.showChildren.length < item.children.length) {
                item.showChildren = item.children;
            } else {
                item.showChildren = item.children.slice(0, this.showNumber);
            }
            this.$set(this.showComentDatas, index, item);
        },
        replyClick(
            item,
            index,
            children = {},
            childrenIndex = 0,
            isChildren = false
        ) {
            const flag = isChildren ? !children.showReply : !item.showReply;
            this.showVEmojiPicker = false;
            this.hideAll();
            let ref = "reply-" + index;
            if (isChildren) {
                children.showReply = flag;
                if (children.showReply)
                    ref = "reply-" + index + "-" + childrenIndex;
            } else {
                item.showReply = flag;
            }
            this.$set(this.showComentDatas, index, item);
            if (children.showReply || item.showReply) {
                this.$nextTick(() => {
                    document.getElementById(ref).focus();
                });
            } else {
                this.replyText = "";
            }
        },
        hideAll() {
            this.replyText = "";
            this.showComentDatas.map(item => {
                item.showReply = false;
                item.showChildren &&
                item.showChildren.map(item1 => {
                    item1.showReply = false;
                });
            });
            this.showComentDatas = [...this.showComentDatas];
        },
        commentInput(item) {
            const currentLength = item.target.value.length;
            // 判断字数如果比之前少了，说明内容正在减少，需要清除高度样式，重新获取
            if (currentLength < this.lastLength) {
                item.target.style.height = "";
            }
            const currentHeight = item.target.scrollHeight;
            // 如果内容高度发生了变化，再去设置高度值
            if (currentHeight > item.target.offsetHeight) {
                item.target.style.height = currentHeight + "px";
            }
            this.lastLength = currentLength;
        },
        replyInput(item) {
            const currentLength = item.target.value.length;
            // 判断字数如果比之前少了，说明内容正在减少，需要清除高度样式，重新获取
            if (currentLength < this.lastLength) {
                item.target.style.height = "";
            }
            const currentHeight = item.target.scrollHeight;
            // 如果内容高度发生了变化，再去设置高度值
            if (currentHeight > item.target.offsetHeight) {
                item.target.style.height = currentHeight + "px";
            }
            this.lastLength = currentLength;
        },
        rankDatas(datas) {
            if (this.rank === "desc") {
                return datas.sort((b, a) => {
                    return (
                        new Date(a[this.orderBy]).getTime() -
                        new Date(b[this.orderBy]).getTime()
                    );
                });
            }
            return datas.sort((a, b) => {
                return (
                    new Date(a[this.orderBy]).getTime() -
                    new Date(b[this.orderBy]).getTime()
                );
            });
        },
        initData() {
            this.showComentDatas = this.getTreeData(
                this.commentDatas,
                this.keyMap.id,
                this.keyMap.pid
            );
            this.showComentDatas = this.rankDatas(this.showComentDatas);
            for (let i = 0; i < this.showComentDatas.length; i++) {
                if (this.showComentDatas[i].children) {
                    this.showComentDatas[i].children = this.treeToArr(
                        this.showComentDatas[i].children
                    );
                    this.showComentDatas[i].children = this.rankDatas(
                        this.showComentDatas[i].children
                    );
                    this.showComentDatas[i].showChildren = this.showComentDatas[
                        i
                        ].children.slice(
                        0,
                        this.showItem[i]
                            ? this.showComentDatas[i].children.length
                            : this.showNumber
                    );
                }
            }
        },
        treeToArr(data, pName = null, pContent = "") {
            let cloneData = JSON.parse(JSON.stringify(data));
            let res = [];
            cloneData.map(item => {
                if (item.children) {
                    res.unshift(
                        ...this.treeToArr(
                            item.children,
                            item.nickname,
                            item.content
                        )
                    );
                }
                item["reply"] = pName;
                item["pContent"] = pContent;
                res.unshift(item);
            });
            if (this.showItem.length === 0) {
                this.showItem = new Array(res.length).fill(false);
            }
            return res;
        },
        getTreeData(data, id = "id", pid = "pid") {
            let cloneData = JSON.parse(JSON.stringify(data));
            return cloneData.filter(parent => {
                let branchArr = cloneData.filter(
                    child => parent[id] === child[pid]
                );
                for (let i = 0; i < branchArr.length; i++) {
                    branchArr.parent_nickname = parent.nickname;
                }
                branchArr.length > 0 ? (parent["children"] = branchArr) : "";
                return parent[pid] === null;
            });
        }
    }
};
</script>

<style scoped>
.j-comment {
    text-align: left;
    width: 100%;
    padding: 1rem;
}

.j-comment #v-emoji-picker {
    position: absolute;
    z-index: 999;
}

.j-comment .j-comment-content {
    color: #737478;
    width: 100%;
    resize: vertical;
    min-height: 3rem;
    padding: 0.5rem;
    border-radius: 5px;
    border: 1px solid #dcdfe6;
    font-family: var(--base-font-family);
    font-size: var(--base-font-size);
}

.j-comment .j-comment-content:focus-visible {
    outline: 1px solid #409eff;
}

.j-comment .j-comment-content-btns {
    display: flex;
}

.j-comment .j-comment-content-btns button {
    margin-left: auto;
    background-color: #1e80ff;
    border: none;
    color: white;
    padding: 0.5rem 1.5rem;
    border-radius: 8px;
    cursor: pointer;
}

.j-comment .j-comment-title {
    font-weight: bolder;
    font-size: large;
    margin-bottom: 1rem;
}

.j-comment .j-comment-item .j-comment-header {
    display: flex;
    line-height: 40px;
    flex-wrap: wrap;
}

.j-comment .j-comment-item .j-comment-header .j-comment-header-pre {
    display: flex;
    line-height: 40px;
}

.j-comment .j-comment-item .j-comment-header .j-comment-header-pre .j-comment-header-img {
    width: 40px;
    height: 40px;
    border-radius: 50%;
}

.j-comment .j-comment-item .j-comment-header .j-comment-header-pre .j-comment-header-nickname {
    padding-left: 1em;
    font-size: 16px;
}

.j-comment .j-comment-item .j-comment-header .j-comment-header-reply-nickname span {
    margin-left: 0.5rem;
    margin-right: 0.5rem;
}

.j-comment .j-comment-item .j-comment-header .j-comment-header-time {
    margin-left: auto;
}

.j-comment .j-comment-item .j-comment-body {
    padding: 0.5rem 0.5rem 0.5rem calc(40px + 1em);
}

.j-comment .j-comment-item .j-comment-footer {
    padding: 0.5rem 0rem 0.5rem calc(40px + 1em);
    display: flex;
    align-items: center;
}

.j-comment .j-comment-item .j-comment-footer .j-comment-icon {
    width: 20px;
    height: 20px;
    cursor: pointer;
}

.j-comment .j-comment-item .j-comment-footer span {
    cursor: pointer;
}

.j-comment .j-comment-item .j-comment-reply-content {
    width: calc(100% - 40px - 1em - 1rem);
    min-height: 1rem;
    padding: 0.5rem;
    margin: 0.5rem 0 0.2rem calc(40px + 1em);
    resize: vertical;
    overflow: hidden;
    border-radius: 5px;
    border: 1px solid #dcdfe6;
    font-family: var(--base-font-family);
    font-size: var(--base-font-size);
}

.j-comment .j-comment-item .j-comment-reply-content:focus-visible {
    outline: 1px solid #409eff;
}

.j-comment .j-comment-item .j-comment-reply-content::before {
    content: attr(placeholder);
    position: absolute;
    pointer-events: none;
    -webkit-user-select: none;
    -moz-user-select: none;
    -ms-user-select: none;
    user-select: none;
    color: #8a919f;
}

.j-comment .j-comment-item .j-comment-reply-btns {
    display: flex;
}

.j-comment .j-comment-item .j-comment-reply-btns .emoji-btn {
    margin-left: calc(40px + 1em);
    margin-top: auto;
    margin-bottom: auto;
}

.j-comment .j-comment-item .j-comment-reply-btns button {
    margin-left: auto;
    background-color: #1e80ff;
    border: none;
    color: white;
    padding: 0.5rem 1.5rem;
    border-radius: 8px;
    cursor: pointer;
}

.j-comment .j-comment-item .j-comment-childer {
    margin-left: calc(40px + 1em);
    padding: 0.5rem 0.5rem 0.5rem 1em;
    background-color: #f9fafb;
    border-radius: 10px;
}

.j-comment .j-comment-item .j-comment-childer .j-coment-children-content {
    background-color: #f2f3f5;
    padding: 0.5rem;
    overflow: hidden;
    text-overflow: ellipsis;
    white-space: nowrap;
}

.j-comment .j-comment-childer-more {
    cursor: pointer;
}

</style>
