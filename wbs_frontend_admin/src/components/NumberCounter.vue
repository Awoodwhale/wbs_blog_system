<template>
    <div class="number-grow-warp">
        <span ref="numberGrow" :data-time="time" class="number-grow">0</span>
    </div>
</template>

<script>
export default {
    props: {
        time: {
            type: Number,
            default: 1
        },
        value: {
            type: Number,
            default: 0
        }
    },
    data() {
        return {

        }
    },
    mounted() {
        this.numberGrow(this.$refs.numberGrow)
    },
    methods: {
        numberGrow(ele) {
            let step = parseInt((this.value) / (this.time * 100))
            if (step < 0) { step = 0 }
            let current = 0
            let start = 0
            let t = setInterval(() => {
                start += step + Math.floor((Math.random() * 2) + 1)
                if (start >= parseInt(this.value / 3)) {
                    clearInterval(t)
                    start = parseInt(this.value / 3)
                    t = null
                }
                if (current === start) {
                    return
                }
                current = start
                ele.innerHTML = current.toString().replace(/(\d)(?=(?:\d{3}[+]?)+$)/g, '$1,')
            }, 40)
            let t2 = setInterval(() => {
                start += step + Math.floor((Math.random() * 2) + 1)
                if (start >= this.value - 6) {
                    clearInterval(t2)
                    if (this.value > 6) {
                        start = this.value - 6
                    } else {
                        start = 0
                    }
                    t2 = null
                }
                if (current === start) {
                    return
                }
                current = start
                ele.innerHTML = current.toString().replace(/(\d)(?=(?:\d{3}[+]?)+$)/g, '$1,')
            }, 50)
            let t3 = setInterval(() => {
                start += 1
                if (start >= this.value) {
                    clearInterval(t3)
                    start = this.value
                    t3 = null
                }
                if (current === start) {
                    return
                }
                current = start
                ele.innerHTML = current.toString().replace(/(\d)(?=(?:\d{3}[+]?)+$)/g, '$1,')
            }, 175)
        }
    },
}
</script>

