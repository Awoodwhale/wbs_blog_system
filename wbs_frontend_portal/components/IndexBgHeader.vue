<template>
    <header class="header-box">
        <section id="main-container">
            <div class="headertop">
                <ImgBox></ImgBox>
            </div>
        </section>
    </header>
</template>

<script>
import ImgBox from './ImgBox.vue'
export default {
    components: {ImgBox},
}
</script>

<style scoped>
.headertop,
.pattern-center {
    -webkit-animation: header 1s;
    animation: header 1s;
}

.header-box,
#main-container,
.headertop {
    height: 100%
}

.headertop.filter-undertint:before {
    background-color: rgba(255, 255, 255, .3)
}

.headertop.filter-dim:before {
    background-color: rgba(0, 0, 0, .3)
}

.headertop-bar:after {
    content: '';
    width: 150%;
    height: 4.375rem;
    background: #fff;
    left: -25%;
    bottom: -2.875rem;
    border-radius: 100%;
    position: absolute;
    z-index: 4
}


@-webkit-keyframes wave1 /*Safari and Chrome*/
{
    0% {
        left: -1500px;
        bottom: -5px
    }
    50% {
        left: 0px;
        bottom: 0px
    }
    100% {
        left: -1500px;
        bottom: -5px
    }
}

@-webkit-keyframes wave2 {
    0% {
        left: -1450px
    }
    50% {
        left: 0px
    }
    100% {
        left: -1450px
    }

}

@keyframes wave1 /*Safari and Chrome*/
{
    0% {
        left: -1500px;
        bottom: -5px
    }
    50% {
        left: 0px;
        bottom: 0px
    }
    100% {
        left: -1500px;
        bottom: -5px
    }
}

@keyframes wave2 {
    0% {
        left: -1450px
    }
    50% {
        left: 0px
    }
    100% {
        left: -1450px
    }

}

#banner_wave_1 {
    width: auto;
    height: 65px;
    background: url("https://cdn.jsdelivr.net/gh/yremp/resource@2.0/img/other/wave1.png") repeat-x;
    _filter: alpha(opacity=80);
    position: absolute;
    bottom: 0;
    width: 400%;
    left: -236px;
    z-index: 5;
    opacity: 1;
    transiton-property: opacity, bottom;
    animation: wave1 90s infinite;
    -webkit-animation: wave1 90s infinite;
}

#banner_wave_2 {
    width: auto;
    height: 80px;
    background: url("https://cdn.jsdelivr.net/gh/yremp/resource@2.0/img/other/wave2.png") repeat-x;
    _filter: alpha(opacity=80);
    position: absolute;
    bottom: 0;
    width: 400%;
    left: 0;
    z-index: 4;
    opacity: 1;
    transiton-property: opacity, bottom;
    animation: wave2 90s infinite;
    -webkit-animation: wave2 90s infinite;
}

.banner_wave_hide, .banner_wave_hide_fit_skin {
    opacity: 0 !important;
    bottom: -999px !important
}

@media (max-width: 860px) {
    #banner_wave_1, #banner_wave_2 {
        display: inline-block;

    }

    @-webkit-keyframes wave1 /*Safari and Chrome*/
    {
        0% {
            left: -750px;
            bottom: -2px
        }
        50% {
            left: 0px;
            bottom: 0px
        }
        100% {
            left: -750px;
            bottom: -2px
        }
    }

    @-webkit-keyframes wave2 {
        0% {
            left: -750px
        }
        50% {
            left: 0px
        }
        100% {
            left: -750px
        }

    }
    @keyframes wave1 /*Safari and Chrome*/
    {
        0% {
            left: -750px;
            bottom: -2px
        }
        50% {
            left: 0px;
            bottom: 0px
        }
        100% {
            left: -750px;
            bottom: -2px
        }
    }

    @keyframes wave2 {
        0% {
            left: -750px
        }
        50% {
            left: 0px
        }
        100% {
            left: -750px
        }

    }
    #banner_wave_1 {
        width: auto;
        height: 65px;
        background: url("https://cdn.jsdelivr.net/gh/yremp/resource@2.0/img/other/wave1.png") repeat-x;
        _filter: alpha(opacity=80);
        position: absolute;
        bottom: 0;
        width: 400%;
        left: 750px;
        z-index: 5;
        opacity: 1;
        transiton-property: opacity, bottom;
        animation: wave1 90s infinite;
        -webkit-animation: wave1 60s infinite;
    }

    #banner_wave_2 {
        width: auto;
        height: 80px;
        background: url("https://cdn.jsdelivr.net/gh/yremp/resource@2.0/img/other/wave2.png") repeat-x;
        _filter: alpha(opacity=80);
        position: absolute;
        bottom: 0;
        width: 400%;
        left: 0;
        z-index: 4;
        opacity: 1;
        transiton-property: opacity, bottom;
        animation: wave2 90s infinite;
        -webkit-animation: wave2 60s infinite;
    }
}

</style>
