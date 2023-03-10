<template>
    <div class="tag-wall">
        <div class="tag-cloud" ref="wrapper">
            <p
                v-for="(item, index) in data"
                :key="index"
                ref="tag"
                @click="clickTag(item)"
                @dblclick="dbclickTag(item)"
            >
                {{ item.name }}
            </p>
        </div>
    </div>
</template>

<script>

export default {
    data() {
        return {
            data: this.$store.state.categories,
            option: {
                radius: 140, // 滚动半径，单位px
                maxFont: 30, // 最大字体大小
                color: null, // 字体颜色。为空时随机
                rotateAngleXbase: 600, // 默认旋转速度基数，数越小速度越快
                rotateAngleYbase: 600,
            },
            tagList: [],
        };
    },
    beforeDestroy() {
        if (this.timer) {
            clearInterval(this.timer);
            this.timer = null;
        }
    },
    watch: {
        data() {
            this.$nextTick(() => {
                this._initTags();
            });
        },
    },
    mounted() {
        this._initTags();
    },
    created() {
        if (this.config != null) {
            this.option = Object.assign({}, this.option, this.config);
        }
    },
    methods: {
        _initTags() {
            this.rotateAngleX = Math.PI / this.option.rotateAngleXbase;
            this.rotateAngleY = Math.PI / this.option.rotateAngleYbase;

            let i = 0, length = this.data.length;
            for (; i < length; i++) {
                // 获取球面上均匀的点的经纬度 θ = arccos( ((2*num)-1)/all - 1); Φ = θ*sqrt(all * π);
                let angleX = Math.acos((2 * (i + 1) - 1) / length - 1);
                let angleY = angleX * Math.sqrt(length * Math.PI);
                // 根据经纬度获取点的坐标，球中心的点坐标是 (0,0,0) x=r*sinθ*cosΦ   y=r*sinθ*sinΦ   z=r*cosθ;
                const x = this.option.radius * Math.sin(angleX) * Math.cos(angleY);
                const y = this.option.radius * Math.sin(angleX) * Math.sin(angleY);
                const z = this.option.radius * Math.cos(angleX);
                if (this.option.color) {
                    this.$refs.tag[i].style.color = this.option.color;
                } else {
                    // 随机颜色
                    this.$refs.tag[i].style.color =
                        "rgb(" +
                        Math.round(255 * Math.random()) +
                        "," +
                        Math.round(255 * Math.random()) +
                        "," +
                        Math.round(255 * Math.random()) +
                        ")";
                }
                // 每个标签对象都有四对值
                const tag = {
                    x: x,
                    y: y,
                    z: z,
                    ele: this.$refs.tag[i],
                };
                this.tagList.push(tag);
            }
            const _self = this;
            const datas = _self.tagList;
            this.timer = setInterval(function () {
                for (let i = 0; i < datas.length; i++) {
                    _self.rotateX(datas[i]);
                    _self.rotateY(datas[i]);
                    _self.setPosition(
                        datas[i],
                        _self.option.radius,
                        _self.option.maxFont
                    );
                }
            }, 20);
        },
        setPosition(tag, r, maxFont) {
            // 设置每个标签的坐标位置和字体大小以及透明度
            if (this.$refs.wrapper) {
                tag.ele.style.transform =
                    "translate(" +
                    (tag.x +
                        this.$refs.wrapper.offsetWidth / 2 -
                        tag.ele.offsetWidth / 2) +
                    "px," +
                    (tag.y +
                        this.$refs.wrapper.offsetHeight / 2 -
                        tag.ele.offsetHeight / 2) +
                    "px)";
                tag.ele.style.opacity = (tag.z / r / 2 + 0.7) + '';
                tag.ele.style.fontSize = (tag.z / r / 2 + 0.5) * maxFont + "px";
            }
        },
        rotateX(tag) {
            let cos = Math.cos(this.rotateAngleX);
            let sin = Math.sin(this.rotateAngleX);
            let y1 = tag.y * cos - tag.z * sin;
            let z1 = tag.y * sin + tag.z * cos;
            tag.y = y1;
            tag.z = z1;
        },
        rotateY(tag) {
            let cos = Math.cos(this.rotateAngleY);
            let sin = Math.sin(this.rotateAngleY);
            let x1 = tag.z * sin + tag.x * cos;
            let z1 = tag.z * cos - tag.x * sin;
            tag.x = x1;
            tag.z = z1;
        },
        dbclickTag() {
            if (this.timer) {
                clearInterval(this.timer);
                this.timer = null;
            } else {
                const _self = this;
                this.timer = setInterval(function () {
                    for (let i = 0; i < _self.tagList.length; i++) {
                        _self.rotateX(_self.tagList[i]);
                        _self.rotateY(_self.tagList[i]);
                        _self.setPosition(
                            _self.tagList[i],
                            _self.option.radius,
                            _self.option.maxFont
                        );
                    }
                }, 20);
            }
        },
        clickTag(item) {
            this.$emit("clickTag", item);
        },
    },
};
</script>

<style scoped>
.tag-cloud {
    width: 300px;
    height: 300px;
    position: relative;
    color: #333;
    margin: 0 auto;
    text-align: center;
}

.tag-cloud p {
    position: absolute;
    top: 0;
    left: 0;
    color: #333;
    text-decoration: none;
    margin: 0 10px 15px 0;
    line-height: 18px;
    text-align: center;
    font-size: 16px;
    padding: 4px 9px;
    display: inline-block;
    border-radius: 3px;
}

.tag-cloud p:hover {
    cursor: pointer;
}
</style>

